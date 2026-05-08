package weilai.team.officialWebSiteApi.util;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * ClassName:RedisCacheUtil
 * Description:
 * Redis 缓存，解决缓存穿透和缓存击穿的两种缓存方式
 * @Author:独酌
 * @Create:2024/11/24 20:50
 */
@Component //交给Spring管理
@Slf4j
public class RedisCacheUtil {
    private final StringRedisTemplate stringRedisTemplate;

    //缓存击穿时，空数据的过期时间，尽量短，取平衡
    private static final Long CACHE_NULL_TTL = 1L;

    //缓存击穿的过期时间的单位
    private static final TimeUnit CACHE_NULL_TTL_TIME_UNIT = TimeUnit.MINUTES;

    //互斥锁
    private static final String LOCK_SHOP_KEY = "lock: RedisCache: ";

    //设置10个线程，用于缓存击穿时，开启线程查询
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);


    public RedisCacheUtil(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 缓存有实际的过期时间
     * @param key 键
     * @param value 值
     * @param time 过时间
     * @param unit 时间单位
     */
    private void set(String key, Object value, Long time, TimeUnit unit){
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(value),time,unit);
    }

    /**
     * 逻辑 过期时间的添加
     * 注意
     *    此方法要与 queryWithLogicalExpire() 方法搭配使用，而且，是后台手动调用，设置 key 为热点key
     * @param key 键
     * @param value 值
     * @param time 逻辑 过期时间 要与缓存击穿的过期时间一致
     * @param unit 时间单位
     */
    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit){
        // 设置逻辑过期
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        //写入redis
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(redisData));
    }

    /**
     * 利用 redis 进行缓存，并且解决 Redis缓存穿透 问题，主要解决恶意请求数据库中没有的数据，造成数据库的压力剧增
     * 如果数据库中也不存在的数据，认为是缓存穿透，将空字符串传入redis中
     * 过期时间为 CACHE_NULL_TTL ,时间单位为 CACHE_NULL_TTL_TIME_UNIT
     *
     * 若要使该缓存做到实时更新，则需要在更新该缓存缓存的数据时，删除该缓存，即可做到实时更新
     * 删除缓存时，注意事务的添加，保证原子性，并且，要更新数据库，再删除缓存
     *
     * 本方法：用于缓存的类型是String类型，返回的结果是目标实体类
     *
     * @param keyPrefix 前缀
     * @param id 查询id
     * @param type 返回值类型的 Class 对象
     * @param dbFallback 数据库中根据 id 的查询逻辑
     * @param time 当缓存中不存在时，将存入 redis ，设置过期时间
     * @param unit 时间的单位
     * @return 泛型的返回值类型
     * @param <R> 返回值类型
     * @param <ID> 根据数据库查询的 id 的类型
     */
    public <R,ID> R queryWithPassThroughByString(String keyPrefix, ID id, Class<R> type, Supplier<R> dbFallback,Long time,TimeUnit unit){
        String key = keyPrefix + id;
        //1、从 redis 中查询数据缓存
        String json = stringRedisTemplate.opsForValue().get(key);

        //2、判断是否存在
        if(json != null && !json.isEmpty()){
            //3、存在，直接返回
            return JSON.parseObject(json,type);
        }

        //3、判断命中的是空值（可能是 null 或 ""[空字符串]，null不需要处理，空字符串返回null）
        //【防止缓存 <穿透> 】
        if(json != null){
            return null;
        }

        //4、如果redis中不存在，根据id从数据库中查询
        R r = dbFallback.get();

        //5、数据库中还不存在，向 redis 中存入空值【防止缓存 <穿透> 】
        if(r == null){
            stringRedisTemplate.opsForValue().set(key,"",CACHE_NULL_TTL,CACHE_NULL_TTL_TIME_UNIT);
            return null;
        }

        //6、如果存在，向 redis 中储存
        this.set(key,r,time,unit);

        return r;
    }

    /**
     * 本方法：用于缓存的类型是String类型，返回的结果是目标实体类的 List 集合
     * @param keyPrefix 前缀
     * @param id 查询id
     * @param type 返回值类型的 Class 对象
     * @param dbFallback 数据库中根据 id 的查询逻辑
     * @param time 当缓存中不存在时，将存入 redis ，设置过期时间
     * @param unit 时间的单位
     * @return 泛型的返回值类型
     * @param <R> 返回值类型
     * @param <ID> 根据数据库查询的 id 的类型
     */
    public <R,ID> List<R> queryWithPassThroughToList(String keyPrefix, ID id, Class<R> type, ListSupplier<R> dbFallback, Long time, TimeUnit unit){
        String key = keyPrefix + id;
        //1、从 redis 中查询数据缓存
        String json = stringRedisTemplate.opsForValue().get(key);

        //2、判断是否存在
        if(json != null && !json.isEmpty()){
            //3、存在，直接返回
            return JSON.parseArray(json,type);
        }

        //3、判断命中的是空值（可能是 null 或 ""[空字符串]，null不需要处理，空字符串返回null）
        //【防止缓存 <穿透> 】
        if(json != null){
            return null;
        }

        //4、如果redis中不存在，根据id从数据库中查询
        List<R> r = dbFallback.get();

        //5、数据库中还不存在，向 redis 中存入空值【防止缓存 <穿透> 】
        if(r == null){
            stringRedisTemplate.opsForValue().set(key,"",CACHE_NULL_TTL,CACHE_NULL_TTL_TIME_UNIT);
            return null;
        }

        //6、如果存在，向 redis 中储存
        this.set(key,r,time,unit);

        return r;
    }


    /**
     * 本方法：用于缓存的类型是Hash类型，返回的结果是目标实体类
     * @param keyPrefix key前缀
     * @param majorId   主id --> 整个 Hash 的键
     * @param minorId   次id --> Hash 中的键
     * @param type 返回值类型的 Class 对象
     * @param dbFallback 数据库中根据 majorId 的查询逻辑
     * @param time 当缓存中不存在时，将存入 redis ，设置过期时间
     * @param unit 时间的单位
     * @return 泛型的返回值类型
     * @param <R> 返回值类型
     * @param <MID> 根据数据库查询的 id 的类型 【主id】
     * @param <SID> 根据Redis中的 id 的类型 【次id】
     */
    public <R,MID,SID> R queryWithPassThroughByHash(String keyPrefix, MID majorId, SID minorId, Class<R> type, Supplier<R> dbFallback, Long time, TimeUnit unit){
        String key = keyPrefix + majorId;
        //1、从 redis 中查询数据缓存
        String json = (String) stringRedisTemplate.opsForHash().get(key,minorId);

        //2、判断是否存在
        if(json != null && !json.isEmpty()){
            //3、存在，直接返回
            return JSON.parseObject(json,type);
        }

        //3、判断命中的是空值（可能是 null 或 ""[空字符串]，null不需要处理，空字符串返回null）
        //【防止缓存 <穿透> 】
        if(json != null){
            return null;
        }

        //4、如果redis中不存在，根据majorId和minorId的查找代码片段
        R r = dbFallback.get();


        //5、数据库中还不存在，向 redis 中存入空值【防止缓存 <穿透> 】
        if(r == null){
            //为这个key设置一个空值
            stringRedisTemplate.opsForHash().put(key,minorId,"");
            //设置过期时间，并为整个hash重新设置一个较短的过期时间
            stringRedisTemplate.expire(key,CACHE_NULL_TTL,CACHE_NULL_TTL_TIME_UNIT);
            return null;
        }

        //6、如果存在，向 redis 中储存
        stringRedisTemplate.opsForHash().put(key,minorId,JSON.toJSONString(r));

        //7、设置过期时间
        stringRedisTemplate.expire(key,time,unit);
        return r;
    }

    /**
     * 利用redis缓存，解决缓存穿透问题，存储类型为Hash类型
     *  如果数据库中也不存在的数据，认为是缓存穿透，将空字符串传入redis中
     *  过期时间为 CACHE_NULL_TTL ,时间单位为 CACHE_NULL_TTL_TIME_UNIT
     * @param keyPrefix key前缀
     * @param majorId   主id --> 整个 Hash 的键
     * @param minorId   次id --> Hash 中的键
     * @param type 返回值类型的 Class 对象
     * @param dbFallback 数据库中根据 majorId 的查询逻辑
     * @param time 当缓存中不存在时，将存入 redis ，设置过期时间
     * @param unit 时间的单位
     * @return 泛型的返回值类型
     * @param <R> 返回值类型
     * @param <MID> 根据数据库查询的 id 的类型 【主id】
     * @param <SID> 根据Redis中的 id 的类型 【次id】
     */
    public <R,MID,SID> List<R> queryWithPassThroughByHashToList(String keyPrefix, MID majorId, SID minorId, Class<R> type, ListSupplier<R> dbFallback, Long time, TimeUnit unit){
        String key = keyPrefix + majorId;
        //1、从 redis 中查询数据缓存
        String json = (String) stringRedisTemplate.opsForHash().get(key,minorId);

        //2、判断是否存在
        if(json != null && !json.isEmpty()){
            //3、存在，直接返回
            return JSON.parseArray(json,type);
        }

        //3、判断命中的是空值（可能是 null 或 ""[空字符串]，null不需要处理，空字符串返回null）
        //【防止缓存 <穿透> 】
        if(json != null){
            return null;
        }

        //4、如果redis中不存在，根据majorId和minorId的查找代码片段
        List<R> r = dbFallback.get();


        //5、数据库中还不存在，向 redis 中存入空值【防止缓存 <穿透> 】
        if(r == null || r.isEmpty()){
            //为这个key设置一个空值
            stringRedisTemplate.opsForHash().put(key,minorId,"");
            //设置过期时间，并为整个hash重新设置一个较短的过期时间
            stringRedisTemplate.expire(key,CACHE_NULL_TTL,CACHE_NULL_TTL_TIME_UNIT);
            return null;
        }

        //6、如果存在，向 redis 中储存
        stringRedisTemplate.opsForHash().put(key,minorId,JSON.toJSONString(r));

        //7、设置过期时间
        stringRedisTemplate.expire(key,time,unit);
        return r;
    }

    /**
     * 利用 redis 进行缓存，并且解决 Redis缓存击穿 问题，主要解决热点 key 的数据，造成数据库的压力剧增
     * 注意:
     *    使用时，必须要事先逻辑存入热点key setWithLogicalExpire() 方法，且过期时间要与其逻辑过期时间一致
     * @param keyPrefix 前缀
     * @param id 查询id
     * @param type 返回值类型的 Class 对象
     * @param dbFallback 数据库中根据 id 的查询逻辑
     * @param time 当缓存中不存在时，将存入 redis ，设置 逻辑 过期时间
     * @param unit 时间的单位
     * @return 泛型的返回值类型
     * @param <R> 返回值类型
     * @param <ID> 根据数据库查询的 id 的类型
     */
    public <R,ID> R queryWithLogicalExpire(String keyPrefix,ID id,Class<R> type,Supplier<R> dbFallback,Long time,TimeUnit unit){
        String key = keyPrefix + id;

        //1、从 redis 中查询数据缓存【理论上，redis层面不会失效】
        String json = stringRedisTemplate.opsForValue().get(key);

        //2、判断是否存在，理论上永远存在，如果不存在，就说明不是热点key
        if(json == null || json.isEmpty()){
            //3、存在返回 null
            return null;
        }

        //4、命中，需要先把json反序列化为对象
        RedisData redisData = JSON.parseObject(json, RedisData.class);
        R r = JSON.to(type,redisData.getData());

        LocalDateTime e = redisData.getExpireTime();

        //5、判断是否过期
        if(e.isAfter(LocalDateTime.now())){
            // 5.1、未过期，直接返回旧的结果
            return r;
        }

        String lockKey = LOCK_SHOP_KEY + id;
        boolean isLock = tryLock(lockKey);
        if(isLock){
            //6 加锁成功，开启线程，实现缓存重建
            CACHE_REBUILD_EXECUTOR.submit(() ->{
               try {
                   //查询数据库
                   R r1 = dbFallback.get();
                   //写入 redis
                   this.setWithLogicalExpire(key,r1,time,unit);
               } catch (Exception e1){
                   log.error("redis 缓存出错",e1);
               } finally {
                   //释放锁
                   unlock(lockKey);
               }
            });
        }

        //返回数据
        return r;
    }

    /**
     * 利用Redis的性质加锁
     * @param key 锁的id
     * @return 是否加锁成功
     */
    private boolean tryLock(String key){
        //setIfAbsent() ：String的一个方法，如果key不存在，则设置value，返回true；
        //               如果key已经存在，则不做任何操作，返回false。
        Boolean b = stringRedisTemplate.opsForValue().setIfAbsent(key, "value", 20L, TimeUnit.SECONDS);
        return b != null && b;
    }

    /**
     * 解锁
     * @param key 锁的id
     */
    private void unlock(String key){
        stringRedisTemplate.delete(key);
    }

    /**
     * 删除 key
     * @param key 锁的id
     */
    public void deleteRedis(String key){
        stringRedisTemplate.delete(key);
    }

    @Data
    private static final class RedisData{
        private LocalDateTime expireTime;
        private Object data;
    }

    @FunctionalInterface
    public interface ListSupplier<R> {
        List<R> get();
    }
}
