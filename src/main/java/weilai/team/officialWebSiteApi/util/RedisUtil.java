package weilai.team.officialWebSiteApi.util;

import com.alibaba.excel.util.BooleanUtils;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import weilai.team.officialWebSiteApi.entity.admin.DO.User;
import weilai.team.officialWebSiteApi.mapper.message.MessageMapper;
import weilai.team.officialWebSiteApi.mapper.message.MessageUserNoticeMapper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static weilai.team.officialWebSiteApi.util.Values.*;

@Slf4j
@Component  //将 RedisUtil 类交给 IOC 容器管理，这样再使用是，就可以不用 new 而是注入的方式使用
public class RedisUtil {

    @Resource  // 注入 StringRedisTemplate 类
    private StringRedisTemplate redis;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    UserUtil userUtil;

    @Resource
    MessageMapper messageMapper;

    @Resource
    MessageUserNoticeMapper messageUserNoticeMapper;

    /**
     * 储存 String 类型的数据
     *
     * @param key   键
     * @param value String 类型的值
     * @return true 储存成功
     */
    public boolean setRedisString(String key, String value, Long outTime, TimeUnit timeUnit) {
        ValueOperations<String, String> str = redis.opsForValue();
        str.set(key, value, outTime, timeUnit);
        return true;
    }

    /**
     * 储存 String 类型的值，并设置有效时间
     *
     * @param key     键
     * @param value   String 类型的值
     * @param outTime 有效时间（毫秒）
     */
    public boolean setRedisStringWithOutTime(String key, String value, long outTime) {
        ValueOperations<String, String> str = redis.opsForValue();
        str.set(key, value, Duration.ofMillis(outTime));
        return true;
    }

    /**
     * 通过键获取 String 类型的值
     *
     * @param key 键
     * @return 该 String 类型的值
     */
    public String getRedisString(String key) {
        ValueOperations<String, String> str = redis.opsForValue();
        return str.get(key);
    }

    /**
     * 储存 Object 类型的值。
     * 注意：如果是自定义类，则该类的所有属性的值
     * 不能为 null否则不能被序列化！
     *
     * @param key 键
     * @param obj 值
     */
    public boolean setRedisObject(String key, Object obj) {
        ValueOperations<String, String> str = redis.opsForValue();
        String jsonString = JSON.toJSONString(obj);
        str.set(key, jsonString);
        return true;
    }

    /**
     * 储存 Object 类型的值。
     * 注意：如果是自定义类，则该类的所有属性的值
     * 不能为 null否则不能被序列化！
     *
     * @param key     键
     * @param obj     值
     * @param outTime 有效时间（毫秒）
     * @return true 储存成功
     */
    public boolean setRedisObjectWithOutTime(String key, Object obj, long outTime) {
        ValueOperations<String, String> str = redis.opsForValue();
        String jsonString = JSON.toJSONString(obj);
        str.set(key, jsonString, Duration.ofMillis(outTime));
        return true;
    }

    /**
     * 通过键获取指定对象类型的值
     *
     * @param key   键
     * @param clazz 指定对象的 <class>
     * @param <K>   表示泛型，用于指定任意类型的对象
     * @return 返回指定 <class> 的对象
     */
    public <K> K getRedisObject(String key, Class<K> clazz) {
        ValueOperations<String, String> str = redis.opsForValue();
        String jsonString = str.get(key);
        return JSON.parseObject(jsonString, clazz);
    }

    /**
     * 根据键判断是否存在该值
     *
     * @param key 键
     * @return true 存在
     * false 不存在
     */
    public boolean isExist(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 删除指定键值对
     *
     * @param key 键
     * @return true 删除成功
     * false 删除失败
     */
    public boolean deleteRedis(String key) {
        return Boolean.TRUE.equals(redis.delete(key));
    }

    /**
     * 在指定set集合中添加元素
     *
     * @param ZSetName 表名
     * @param obj      值
     * @return true 储存成功
     */
    public boolean setRedisZSetWithOutTime(String ZSetName, Long score, Object obj) {
        ZSetOperations<String, String> zSetOps = redis.opsForZSet();

        // 将对象转换为 JSON 字符串
        String jsonString = JSON.toJSONString(obj);

        // 判断 ZSet 是否为空，如果为空则设置过期时间
        boolean exists = redis.hasKey(ZSetName);

        // 将对象添加到 Redis Sorted Set 中，使用 时间戳 作为分数，jsonString 作为值
        boolean added = zSetOps.add(ZSetName, jsonString, score);

        // 如果 ZSet 不存在（即 Redis 中没有此键），我们设置过期时间
        if (!exists) {
            // 设置集合的过期时间
            redis.expire(ZSetName, Duration.ofMillis(NOTICE_OUT_TIME));
        }

        return added;
    }


    /**
     * 判断是否已读
     *
     * @param setName 名
     * @param id      用户ID
     * @return
     */
    public boolean isReadNotice(String setName, Long id) {
        SetOperations<String, String> setOps = redis.opsForSet();
        return setOps.isMember(setName, String.valueOf(id));
    }

    /**
     * 存储关于某个公告的已读人员
     *
     * @param setName
     * @param id      用户ID
     */
    public void setRedisSet(String setName, Long id) {
        SetOperations<String, String> setOps = redis.opsForSet();
        setOps.add(setName, String.valueOf(id));
    }

    public void deleteUserNoticeSet(String setName) {
        redis.delete(setName);
    }


    /**
     * 删除ZSet
     *
     * @param ZSetName ZSet名字
     */
    public void deleteAllMessageFromRedis(String ZSetName) {
        redis.delete(ZSetName);
    }

    /**
     * 从redis中删除一条信息
     *
     * @param ZSetName 名字
     * @param score    ID充当分数
     */
    public void deleteOneMessage(String ZSetName, Long score) {
        // 获取 Redis 的 Sorted Set 操作接口
        ZSetOperations<String, String> zSetOps = redis.opsForZSet();
        // 从 Sorted Set 中移除该成员
        zSetOps.removeRangeByScore(ZSetName, score, score);
    }

    /**
     * 信息数量增加
     *
     * @param stringName 名字
     */
    public void incrementMessageCount(String stringName, int messageType, Long receiverId) {
        ValueOperations<String, String> str = redis.opsForValue();
        String countStr = str.get(stringName);
        if (countStr == null || countStr.isEmpty() || !StringUtils.isNumeric(countStr)) {
            int count = 0;
            switch (messageType) {
                case 1:
                case 3:
                case 5:
                    count = messageMapper.getMessageCountByMessageType(receiverId, messageType);
                    break;
                case 2:
                case 6:
                case 10:
                    count = messageMapper.getNotReadMessageCountByMessageType(receiverId, messageType / 2);
                    break;
                case 11:
                    count = messageUserNoticeMapper.getAllNoticeCount();
                    break;
            }
            str.set(stringName, count + "", 60L, TimeUnit.MINUTES);
        } else {
            str.increment(stringName, 1);
        }

    }

    /**
     * 信息数量减少
     *
     * @param stringName 名字
     */
    public void decreaseMessageCount(String stringName, int counts) {
        //如果键不存在就不做操作了
        if (redis.hasKey(stringName)) {
            Long currentValue = Long.valueOf(redis.opsForValue().get(stringName));
            long newValue = Math.max(0, currentValue - counts);
            if (currentValue != newValue) {
                redis.opsForValue().set(stringName, String.valueOf(newValue));
            }
        }
    }


    /**
     * 设置信息数量
     *
     * @param stringName 名字
     * @param count      数量
     */
    public void setMessageCount(String stringName, int count) {
        redis.opsForValue().set(stringName, String.valueOf(count));
    }

    /**
     * 将未读数量变为0
     *
     * @param stringName 名字
     */
    public void markMessageAsRead(String stringName) {
        // 使用 getAndSet 方法，原子性地获取当前值并设置为 0
        redis.opsForValue().getAndSet(stringName, "0");
    }

    /**
     * 获取信息数量
     *
     * @param stringName 名字
     * @return
     */
    public int getMessageCount(String stringName, int messageType, Long receiverId) {
        ValueOperations<String, String> str = redis.opsForValue();

        // 从Redis中获取消息数量
        String countStr = str.get(stringName);
        if (countStr == null || countStr.isEmpty() || !StringUtils.isNumeric(countStr)) {
            int count = 0;
            switch (messageType) {
                case 1:
                case 3:
                case 5:
                    count = messageMapper.getMessageCountByMessageType(receiverId, messageType);
                    break;
                case 2:
                case 6:
                case 10:
                    count = messageMapper.getNotReadMessageCountByMessageType(receiverId, messageType / 2);
                    break;
                case 11:
                    count = messageUserNoticeMapper.getAllNoticeCount();
                    break;
            }
            //60分钟后过期
            str.set(stringName, count + "", 60L, TimeUnit.MINUTES);
            return count;
        } else {
            return Integer.parseInt(countStr);
        }
    }

    /**
     * 获取最后一条信息
     *
     * @param ZSetName
     * @return
     */
    public <T> T getLastMessageFromRedis(String ZSetName, Class<T> clazz) {
        // 获取 Redis 的 Sorted Set 操作接口
        ZSetOperations<String, String> zSetOps = redis.opsForZSet();

        // 获取 ZSet 的最大索引位置，获取最后一条数据
        Set<String> dataSet = zSetOps.reverseRange(ZSetName, 0, 0);  // 获取最后一条数据

        if (dataSet != null && !dataSet.isEmpty()) {
            // 反序列化最后一条 JSON 字符串为指定类型的对象
            String jsonString = dataSet.iterator().next();
            T object = JSON.parseObject(jsonString, clazz);
            return object;
        }

        return null;  // 如果数据为空，返回 null
    }

    /**
     * 判断是否点赞
     *
     * @param id
     * @param request
     * @return
     */
    public Boolean isLike(Long id, HttpServletRequest request) {
        //获取用户信息
        User userInfo = userUtil.getUserInfo(request);
        //判断用户是否点过赞
        Boolean isLike = redis.opsForSet().isMember(Values.KEY_LIKED + id, userInfo.getId().toString());
        return BooleanUtils.isTrue(isLike);
    }

    /**
     * 判断是否收藏
     *
     * @param id
     * @param request
     * @return
     */
    public Boolean isCollect(Long id, HttpServletRequest request) {
        //获取用户信息
        User userInfo = userUtil.getUserInfo(request);
        //判断用户是否点过赞
        Boolean isCollect = redis.opsForSet().isMember(Values.IS_COLLECT__KEY + id, userInfo.getId().toString());
        return BooleanUtils.isTrue(isCollect);
    }

    /**
     * 添加点赞
     */

    public void addLike(String key, String userIdStr) {
        SetOperations<String, String> set = redis.opsForSet();
        //判断用户是否点过赞
        set.add(key, userIdStr);
    }

    /**
     * 添加收藏
     */

    public void addCollect(String key, String userIdStr) {
        SetOperations<String, String> set = redis.opsForSet();
        //判断用户是否点过赞
        set.add(key, userIdStr);
    }

    /**
     * 删除点赞
     *
     * @param userIdStr
     * @param key
     */
    public void removeLike(String key, String userIdStr) {
        SetOperations<String, String> set = redis.opsForSet();
        set.remove(key, userIdStr);
    }

    /**
     * 删除收藏
     *
     * @param userIdStr
     * @param key
     */
    public void removeCollect(String key, String userIdStr) {
        SetOperations<String, String> set = redis.opsForSet();
        set.remove(key, userIdStr);
    }

    /**
     * 查询收藏数量
     *
     * @param key
     * @return
     */
    public int getCollectCount(String key) {
        SetOperations<String, String> set = redis.opsForSet();
        Long size = set.size(key);
        if (size == null) {
            size = 0L;
        }
        return size.intValue();
    }

    /**
     * 删除所有收藏
     *
     * @param key
     */
    public void removeAllCollect(String key) {
        redisTemplate.delete(key);
    }


    /**
     * 重置过期时间
     */
    public void reSetOutTime(String key, long time, TimeUnit timeUnit) {
        redis.expire(key, time, timeUnit);
    }

    /**
     * 利用Redis的性质加锁
     *
     * @param key
     * @param time
     * @param timeUnit
     * @return
     */
    public boolean tryLock(String key, int time, TimeUnit timeUnit) {
        //setIfAbsent() ：String的一个方法，如果key不存在，则设置value，返回true；
        //               如果key已经存在，则不做任何操作，返回false。
        Boolean b = redis.opsForValue().setIfAbsent(key, "post_lock", time, timeUnit);
        return b != null && b;
    }

    /**
     * 删除 key
     *
     * @param key 锁的id
     */
    public void unlock(String key) {
        redis.delete(key);

    }

    /**
     * 原子性的增加浏览量
     *
     * @param key
     * @return
     */
    public void incrementViewCount(Long key) {
        redis.opsForValue().increment(key + Values.POST_VIEW_COUNT_KEY);
    }

    public void incrementViewCount(Long key, Integer count) {
        redis.opsForValue().increment(key + Values.POST_VIEW_COUNT_KEY, count);
    }

    /**
     * 获取浏览量
     *
     * @param key
     * @return
     */
    public Integer getViewCount(Long key) {
        String viewCountStr = redis.opsForValue().get(key + Values.POST_VIEW_COUNT_KEY);
        if (viewCountStr == null || viewCountStr.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(viewCountStr);
    }


    // ---------------------------- 文件----------------------------------

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateUserAuth(User user) {
        String s = Values.REDIS_TOKEN_PREFIX + user.getUsername();
        Long expire = redisTemplate.getExpire(s);
        if (expire != null && expire != -2) {
            setRedisObjectWithOutTime(s, user, expire * 1000);
        }
    }

    /**
     * 执行 Lua 脚本
     *
     * @param luaScript Lua 脚本内容
     * @param keys      Redis 键列表
     * @param args      参数列表
     * @return Lua 脚本的返回值
     */
    public Object executeLua(String luaScript, List<String> keys, List<String> args) {
        if (keys == null || args == null) {
            throw new IllegalArgumentException("Lua 脚本的 keys 和 args 参数不能为空");
        }
        try {
            // 使用 DefaultRedisScript 封装 Lua 脚本
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(luaScript);
            redisScript.setResultType(Long.class); // 设置返回值类型
            // 执行脚本
            return redis.execute(redisScript, keys, args.toArray());
        } catch (Exception e) {
            log.error("执行 Lua 脚本失败", e);
            throw new RuntimeException("Redis Lua 脚本执行失败", e);
        }
    }
}
