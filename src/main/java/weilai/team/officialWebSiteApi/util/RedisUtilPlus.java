package weilai.team.officialWebSiteApi.util;

import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.connection.RedisListCommands;
//import org.springframework.data.redis.connection.zset.Aggregate;
//import org.springframework.data.redis.connection.zset.Weights;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * ClassName:RedisUtil
 * Description:
 *
 * @Author:独酌
 * @Create:2025/8/5 16:26
 */
@Component
public class RedisUtilPlus {
    public ValueOperations<String, String> STRING;
    public ListOps LIST;
    public HashOpes HASH;
    public SetOpes SET;
    public ZSetOpes Z_SET;
    public StringRedisTemplate REDIS;

    @Autowired
    public RedisUtilPlus(StringRedisTemplate redis) {
        STRING = redis.opsForValue();
        LIST = new ListOps(redis.opsForList());
        HASH = new HashOpes(redis.opsForHash());
        SET = new SetOpes(redis.opsForSet());
        Z_SET = new ZSetOpes(redis.opsForZSet());
        REDIS = redis;
    }

    /*
     * 对 String 类型的操作，不需要重写，只需要用自带的就行
     */


    /**
     * 对 Redis 中的 List 类型的操作重写
     */
    public static class ListOps {
        private final ListOperations<String, String> listOps;
        public ListOps(ListOperations<String, String> ops) {
            this.listOps = ops;
        }

        public <T> List<T> range(String key, long start, long end,Class<T> clazz) {
            return Objects.requireNonNull(listOps.range(key, start, end))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toList());
        }

        public void trim(String key, long start, long end) {
            listOps.trim(key, start, end);
        }

        public Long size(String key) {
            return listOps.size(key);
        }

        public Long leftPush(String key, Object value) {
            return listOps.leftPush(key,JSON.toJSONString(value));
        }

        public Long leftPushAll(String key, Object... values) {
            List<String> collect = Arrays.stream(values).map(JSON::toJSONString).collect(Collectors.toList());
            return listOps.leftPushAll(key, collect);
        }

        public Long leftPushAll(String key, Collection<Object> values) {
            List<String> collect = values.stream().map(JSON::toJSONString).collect(Collectors.toList());
            return listOps.leftPushAll(key, collect);
        }

        public Long leftPushIfPresent(String key, Object value) {
            return listOps.leftPushIfPresent(key, JSON.toJSONString(value));
        }

        public Long leftPush(String key, Object pivot, Object value) {
            return listOps.leftPush(key, JSON.toJSONString(pivot), JSON.toJSONString(value));
        }

        public Long rightPush(String key, Object value) {
            return listOps.rightPush(key, JSON.toJSONString(value));
        }

        public Long rightPushAll(String key, Object... values) {
            List<String> collect = Arrays.stream(values).map(JSON::toJSONString).collect(Collectors.toList());
            return listOps.rightPushAll(key, collect);
        }

        public Long rightPushAll(String key, Collection<Object> values) {
            List<String> collect = values.stream().map(JSON::toJSONString).collect(Collectors.toList());
            return listOps.rightPushAll(key, collect);
        }

        public Long rightPushIfPresent(String key, Object value) {
            return listOps.rightPushIfPresent(key, JSON.toJSONString(value));
        }

        public Long rightPush(String key, Object pivot, Object value) {
            return listOps.rightPush(key, JSON.toJSONString(pivot), JSON.toJSONString(value));
        }

//        public <T> T move(String sourceKey, RedisListCommands.Direction from, String destinationKey, RedisListCommands.Direction to,Class<T> clazz) {
//            String move = listOps.move(sourceKey, from, destinationKey, to);
//            return JSON.parseObject(move, clazz);
//        }
//
//        public <T> T move(String sourceKey, RedisListCommands.Direction from, String destinationKey, RedisListCommands.Direction to, long timeout, TimeUnit unit,Class<T> clazz) {
//            String move = listOps.move(sourceKey, from, destinationKey, to, timeout, unit);
//            return JSON.parseObject(move, clazz);
//        }

        public void set(String key, long index, Object value) {
            listOps.set(key, index, JSON.toJSONString(value));
        }

        public Long remove(String key, long count, Object value) {
            return listOps.remove(key, count, JSON.toJSONString(value));
        }

        public <T> T index(String key, long index,Class<T> clazz) {
            return JSON.parseObject(listOps.index(key, index), clazz);
        }

        public Long indexOf(String key, Object value) {
            return listOps.indexOf(key, JSON.toJSONString(value));
        }

        public Long lastIndexOf(String key, Object value) {
            return listOps.lastIndexOf(key, JSON.toJSONString(value));
        }

        public <T> T leftPop(String key,Class<T> clazz) {
            return JSON.parseObject(listOps.leftPop(key), clazz);
        }

//        public <T> List<T> leftPop(String key, long count, Class<T> clazz) {
//            return Objects.requireNonNull(listOps.leftPop(key, count))
//                    .stream()
//                    .map(item -> JSON.parseObject(item, clazz))
//                    .toList();
//        }

        public <T> T leftPop(String key, long timeout, TimeUnit unit,Class<T> clazz) {
            return JSON.parseObject(listOps.leftPop(key, timeout, unit), clazz);
        }

        public <T> T rightPop(String key, Class<T> clazz) {
            return JSON.parseObject(listOps.rightPop(key), clazz);
        }

//        public <T> List<T> rightPop(String key, long count, Class<T> clazz) {
//            return Objects.requireNonNull(listOps.rightPop(key, count)).stream()
//                    .map(e -> JSON.parseObject(e, clazz))
//                    .toList();
//        }

        public <T> T rightPop(String key, long timeout, TimeUnit unit, Class<T> clazz) {
            return JSON.parseObject(listOps.rightPop(key, timeout, unit), clazz);
        }

        public <T> T rightPopAndLeftPush(String sourceKey, String destinationKey,Class<T> clazz) {
            return JSON.parseObject(listOps.rightPopAndLeftPush(sourceKey, destinationKey), clazz);
        }

        public <T> T rightPopAndLeftPush(String sourceKey, String destinationKey, long timeout, TimeUnit unit,Class<T> clazz) {
            return JSON.parseObject(listOps.rightPopAndLeftPush(sourceKey, destinationKey, timeout, unit), clazz);
        }
    }

    /**
     * 对 Redis 中的 Hash 类型的操作重写
     */
    public static class HashOpes {
        private final HashOperations<String, String, String> hashOps;
        public HashOpes(HashOperations<String, String, String> ops) {
            this.hashOps = ops;
        }

        public Long delete(String key, Object... hashKeys) {
            Object[] array = Arrays.stream(hashKeys).map(JSON::toJSONString).toArray();
            return hashOps.delete(key, array);
        }

        public Boolean hasKey(String key, Object hashKey) {
            return hashOps.hasKey(key, JSON.toJSONString(hashKey));
        }

        public <T> T get(String key, Object hashKey,Class<T> clazz) {
            return JSON.parseObject(hashOps.get(key, JSON.toJSONString(hashKey)),clazz);
        }

        public <T> List<T> multiGet(String key, Collection<Object> hashKeys,Class<T> clazz) {
            List<String> collect = hashKeys.stream().map(JSON::toJSONString).collect(Collectors.toList());
            List<String> strings = hashOps.multiGet(key, collect);
            return strings.stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toList());
        }

        public Long increment(String key, Object hashKey, long delta) {
            return hashOps.increment(key, JSON.toJSONString(hashKey), delta);
        }

        public Double increment(String key, Object hashKey, double delta) {
            return hashOps.increment(key, JSON.toJSONString(hashKey), delta);
        }

//        public <T> T randomKey(String key,Class<T> clazz) {
//            return JSON.parseObject(hashOps.randomKey(key),clazz);
//        }
//
//        public <U,V> Map.Entry<U, V> randomEntry(String key,Class<U> hashKey,Class<V> value) {
//            Map.Entry<String, String> entry = hashOps.randomEntry(key);
//            if(entry != null) {
//                return new AbstractMap.SimpleEntry<>(JSON.parseObject(entry.getKey(),hashKey),JSON.parseObject(entry.getValue(),value));
//            }
//            return null;
//        }
//
//        public <T> List<T> randomKeys(String key, long count,Class<T> clazz) {
//            return Objects.requireNonNull(hashOps.randomKeys(key, count))
//                    .stream()
//                    .map(s -> JSON.parseObject(s, clazz))
//                    .collect(Collectors.toList());
//        }
//
//        public <U,V> Map<U,V> randomEntries(String key, long count,Class<U> hashKey,Class<V> value) {
//            return Objects.requireNonNull(hashOps.randomEntries(key, count))
//                    .entrySet()
//                    .stream()
//                    .map(entry -> new AbstractMap.SimpleEntry<>(JSON.parseObject(entry.getKey(),hashKey),JSON.parseObject(entry.getValue(),value)))
//                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//        }

        public <T> Set<T> keys(String key,Class<T> clazz) {
            return Objects.requireNonNull(hashOps.keys(key))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toSet());
        }

        public Long lengthOfValue(String key, Object hashKey) {
            return hashOps.lengthOfValue(key, JSON.toJSONString(hashKey));
        }

        public Long size(String key) {
            return hashOps.size(key);
        }

        public <U,V> void putAll(String key, Map<U,V> m) {
            hashOps.putAll(key, m.entrySet()
                    .stream()
                    .map(entry -> new AbstractMap.SimpleEntry<>(JSON.toJSONString(entry.getKey()),JSON.toJSONString(entry.getValue())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }

        public void put(String key, Object hashKey, Object value) {
            hashOps.put(key, JSON.toJSONString(hashKey), JSON.toJSONString(value));
        }

        public Boolean putIfAbsent(String key, Object hashKey, Object value) {
            return hashOps.putIfAbsent(key, JSON.toJSONString(hashKey), JSON.toJSONString(value));
        }

        public <T> List<T> values(String key, Class<T> clazz) {
            return hashOps.values(key)
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toList());
        }

        public <U,V> Map<U, V> entries(String key, Class<U> hashKey, Class<V> value) {
            return hashOps.entries(key)
                    .entrySet()
                    .stream()
                    .map(entry -> new AbstractMap.SimpleEntry<>(JSON.parseObject(entry.getKey(),hashKey),JSON.parseObject(entry.getValue(),value)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }

    /**
     * 对 Redis 中的 Set 类型的操作重写
     */
    public static class SetOpes {
        private final SetOperations<String, String> setOps;
        public SetOpes(SetOperations<String, String> ops) {
            this.setOps = ops;
        }
        public Long add(String key, Object... values) {
            String[] array = Arrays.stream(values).map(JSON::toJSONString).toArray(String[]::new);
            return setOps.add(key,array);
        }

        public Long remove(String key, Object... values) {
            Object[] array = Arrays.stream(values).map(JSON::toJSONString).toArray();
            return setOps.remove(key, array);
        }

        public <T> T pop(String key, Class<T> clazz) {
            return JSON.parseObject(setOps.pop(key),clazz);
        }

        public <T> List<T> pop(String key, long count,Class<T> clazz) {
            return Objects.requireNonNull(setOps.pop(key, count))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toList());
        }

        public Boolean move(String key, Object value, Object destKey) {
            return setOps.move(key, JSON.toJSONString(value), JSON.toJSONString(destKey));
        }

        public Long size(String key) {
            return setOps.size(key);
        }

        public Boolean isMember(String key, Object o) {
            return setOps.isMember(key, JSON.toJSONString(o));
        }

        public Map<Object, Boolean> isMember(String key, Object... objects) {
            Map<Object, Boolean> map = new HashMap<>();
            Arrays.stream(objects).forEach((v)-> {
                Boolean member = setOps.isMember(key, JSON.toJSONString(v));
                map.put(v,member);
            });
            return map;
        }

        public <T> Set<T> intersect(String key, String otherKey, Class<T> clazz) {
            return Objects.requireNonNull(setOps.intersect(key, otherKey))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toSet());
        }

        public <T> Set<T> intersect(String key, Collection<String> otherKeys, Class<T> clazz) {
            return Objects.requireNonNull(setOps.intersect(key, otherKeys))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toSet());
        }

        public <T> Set<T> intersect(Collection<String> keys, Class<T> clazz) {
            return Objects.requireNonNull(setOps.intersect(keys))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toSet());
        }

        public Long intersectAndStore(String key, String otherKey, String destKey) {
            return setOps.intersectAndStore(key, otherKey, destKey);
        }

        public Long intersectAndStore(String key, Collection<String> otherKeys, String destKey) {
            return setOps.intersectAndStore(key, otherKeys, destKey);
        }

        public Long intersectAndStore(Collection<String> keys, String destKey) {
            return setOps.intersectAndStore(keys, destKey);
        }

        public <T> Set<T> union(String key, String otherKey, Class<T> clazz) {
            return Objects.requireNonNull(setOps.union(key, otherKey))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toSet());
        }

        public <T> Set<T> union(String key, Collection<String> otherKeys, Class<T> clazz) {
            return Objects.requireNonNull(setOps.union(key, otherKeys))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toSet());
        }

        public <T> Set<T> union(Collection<String> keys, Class<T> clazz) {
            return Objects.requireNonNull(setOps.union(keys))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toSet());
        }

        public Long unionAndStore(String key, String otherKey, String destKey) {
            return setOps.unionAndStore(key, otherKey, destKey);
        }

        public Long unionAndStore(String key, Collection<String> otherKeys, String destKey) {
            return setOps.unionAndStore(key, otherKeys, destKey);
        }

        public Long unionAndStore(Collection<String> keys, String destKey) {
            return setOps.unionAndStore(keys, destKey);
        }

        public <T> Set<T> difference(String key, String otherKey, Class<T> clazz) {
            return Objects.requireNonNull(setOps.difference(key, otherKey))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toSet());
        }

        public <T> Set<T> difference(String key, Collection<String> otherKeys, Class<T> clazz) {
            return Objects.requireNonNull(setOps.difference(key, otherKeys))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toSet());
        }

        public <T> Set<T> difference(Collection<String> keys, Class<T> clazz) {
            return Objects.requireNonNull(setOps.difference(keys))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toSet());
        }

        public Long differenceAndStore(String key, String otherKey, String destKey) {
            return setOps.differenceAndStore(key, otherKey, destKey);
        }

        public Long differenceAndStore(String key, Collection<String> otherKeys, String destKey) {
            return setOps.differenceAndStore(key, otherKeys, destKey);
        }

        public Long differenceAndStore(Collection<String> keys, String destKey) {
            return setOps.differenceAndStore(keys, destKey);
        }

        public <T> Set<T> members(String key, Class<T> clazz) {
            return Objects.requireNonNull(setOps.members(key))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toSet());
        }

        public <T>  T randomMember(String key, Class<T> clazz) {
            return JSON.parseObject(setOps.randomMember(key), clazz);
        }

        public <T> Set<T> distinctRandomMembers(String key, long count, Class<T> clazz) {
            return Objects.requireNonNull(setOps.distinctRandomMembers(key, count))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toSet());
        }

        public <T> List<T> randomMembers(String key, long count, Class<T> clazz) {
            return Objects.requireNonNull(setOps.randomMembers(key, count))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toList());
        }
    }

    /**
     * 对 Redis 中的 ZSet 类型的操作重写
     */
    public static class ZSetOpes {
        private final ZSetOperations<String, String> zSetOps;
        public ZSetOpes(ZSetOperations<String, String> ops) {
            this.zSetOps = ops;
        }

        public Boolean add(String key, Object value, double score) {
            return zSetOps.add(key, JSON.toJSONString(value), score);
        }

        public Boolean addIfAbsent(String key, Object value, double score) {
            return zSetOps.add(key, JSON.toJSONString(value), score);
        }

        public Long remove(String key, Object... values) {
            Object[] array = Arrays.stream(values).map(JSON::toJSONString).toArray();
            return zSetOps.remove(key, array);
        }

        public Double incrementScore(String key, Object value, double delta) {
            return zSetOps.incrementScore(key, JSON.toJSONString(value), delta);
        }

//        public <T> T randomMember(String key, Class<T> clazz) {
//            return JSON.parseObject(zSetOps.randomMember(key), clazz);
//        }
//
//        public <T> Set<T> distinctRandomMembers(String key, long count, Class<T> clazz) {
//            return Objects.requireNonNull(zSetOps.distinctRandomMembers(key, count))
//                    .stream()
//                    .map(s -> JSON.parseObject(s, clazz))
//                    .collect(Collectors.toSet());
//        }
//
//        public <T> List<T> randomMembers(String key, long count, Class<T> clazz) {
//            return Objects.requireNonNull(zSetOps.randomMembers(key, count))
//                    .stream()
//                    .map(s -> JSON.parseObject(s, clazz))
//                    .collect(Collectors.toList());
//        }

        public Long rank(String key, Object o) {
            return zSetOps.rank(key, JSON.toJSONString(o));
        }

        public Long reverseRank(String key, Object o) {
            return zSetOps.reverseRank(key, JSON.toJSONString(o));
        }

        public <T> Set<T> range(String key, long start, long end, Class<T> clazz) {
            return Objects.requireNonNull(zSetOps.range(key, start, end))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toSet());
        }

        public <T> Set<T> rangeByScore(String key, double min, double max, Class<T> clazz) {
            return Objects.requireNonNull(zSetOps.rangeByScore(key, min, max))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toSet());
        }

        public <T> Set<T> rangeByScore(String key, double min, double max, long offset, long count, Class<T> clazz) {
            return Objects.requireNonNull(zSetOps.rangeByScore(key, min, max, offset, count))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toSet());
        }

        public <T> Set<T> reverseRange(String key, long start, long end, Class<T> clazz) {
            return Objects.requireNonNull(zSetOps.reverseRange(key, start, end))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toSet());
        }

        public <T> Set<T> reverseRangeByScore(String key, double min, double max, Class<T> clazz) {
            return Objects.requireNonNull(zSetOps.reverseRangeByScore(key, min, max))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toSet());
        }

        public <T> Set<T> reverseRangeByScore(String key, double min, double max, long offset, long count, Class<T> clazz) {
            return Objects.requireNonNull(zSetOps.reverseRangeByScore(key, min, max, offset, count))
                    .stream()
                    .map(s -> JSON.parseObject(s, clazz))
                    .collect(Collectors.toSet());
        }

        public Long count(String key, double min, double max) {
            return zSetOps.count(key, min, max);
        }


        public Long size(String key) {
            return zSetOps.size(key);
        }

        public Long zCard(String key) {
            return zSetOps.zCard(key);
        }

        public Double score(String key, Object o) {
            return zSetOps.score(key, JSON.toJSONString(o));
        }

//        public List<Double> score(String key, Object... o) {
//            Object[] array = Arrays.stream(o).map(JSON::toJSONString).toArray();
//            return zSetOps.score(key, array);
//        }

        public Long removeRange(String key, long start, long end) {
            return zSetOps.removeRange(key, start, end);
        }

        public Long removeRangeByScore(String key, double min, double max) {
            return zSetOps.removeRangeByScore(key, min, max);
        }

//        public <T> Set<T> difference(String key, Collection<String> otherKeys, Class<T> clazz) {
//            return Objects.requireNonNull(zSetOps.difference(key, otherKeys))
//                    .stream()
//                    .map(s -> JSON.parseObject(s, clazz))
//                    .collect(Collectors.toSet());
//        }


//        public Long differenceAndStore(String key, Collection<String> otherKeys, String destKey) {
//            return zSetOps.differenceAndStore(key, otherKeys, destKey);
//        }

//        public <T> Set<T> intersect(String key, Collection<String> otherKeys, Class<T> clazz) {
//            return Objects.requireNonNull(zSetOps.intersect(key, otherKeys))
//                    .stream()
//                    .map(s -> JSON.parseObject(s, clazz))
//                    .collect(Collectors.toSet());
//        }

        public Long intersectAndStore(String key, String otherKey, String destKey) {
            return zSetOps.intersectAndStore(key, otherKey, destKey);
        }

        public Long intersectAndStore(String key, Collection<String> otherKeys, String destKey) {
            return zSetOps.intersectAndStore(key, otherKeys, destKey);
        }

//        public Long intersectAndStore(String key, Collection<String> otherKeys, String destKey, Aggregate aggregate, Weights weights) {
//            return zSetOps.intersectAndStore(key, otherKeys, destKey, aggregate, weights);
//        }

//        public <T> Set<T> union(String key, Collection<String> otherKeys, Class<T> clazz) {
//            Set<String> union = zSetOps.union(key, otherKeys);
//            if(union != null) {
//                return union.stream()
//                        .map(s -> JSON.parseObject(s,clazz))
//                        .collect(Collectors.toSet());
//            }
//            return null;
//        }

        public Long unionAndStore(String key, String otherKey, String destKey) {
            return zSetOps.unionAndStore(key, otherKey, destKey);
        }

        public Long unionAndStore(String key, Collection<String> otherKeys, String destKey) {
            return zSetOps.unionAndStore(key, otherKeys, destKey);
        }

//        public Long unionAndStore(String key, Collection<String> otherKeys, String destKey, Aggregate aggregate, Weights weights) {
//            return zSetOps.unionAndStore(key, otherKeys, destKey, aggregate, weights);
//        }
    }
}
