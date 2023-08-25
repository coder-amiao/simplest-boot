package cn.soboys.simplest.cache;

import org.springframework.data.redis.connection.DataType;

import java.util.*;
import java.util.concurrent.TimeUnit;

public interface ICache<K, V, HK> {

    //Value操作 对应redisTemplate.opsForValue().xxx()

    void valueSet(K key, V value);

    void valueSet(K key, V value, long offset);

    /**
     * 设置缓存及过期时间
     *
     * @param key     缓存Key
     * @param value   缓存值
     * @param timeout 过期时间
     * @param unit    时间对应的单元粒度
     */
    Boolean valueSetAndExpire(K key, V value, long timeout, TimeUnit unit);

    V valueGet(K key);

    //Set操作 对应redisTemplate.opsForSet().xxx()

    void setAdd(K key, V... values);

    void setRemove(K key, Object... values);

    Boolean setIsMember(K key, Object o);

    Set<V> setMembers(K key);

    Long setSize(K key);

    // zSet操作 对应redisTemplate.opsForZSet().xxx()

    Boolean zsetAdd(K key, V value, double score);

    Boolean zsetAddIfAbsent(K key, V value, double score);

    Set<V> zsetRangeByScore(K key, double min, double max);

    Long zsetRemove(K key, Object... values);

    Long zsetRemoveRangeByScore(K key, double min, double max);

    Double zsetScore(K key, Object o);

    List<Double> zsetScore(K key, Object... o);

    Long zsetCount(K key, double min, double max);

    //List操作 对应redisTemplate.opsForList().xxx()

    void listLeftPush(K key, V value);

    void listRightPush(K key, V value);

    V listLeftPop(K key);

    V listRightPop(K key);

    List<V> listRange(K key, long start, long end);

    void listRemove(K key, long count, Object value);

    Long listSize(K key);

    //Hash操作 对应redisTemplate.opsForHash().xxx()

    void hashPut(K key, HK hashKey, V value);

    void hashPutAll(K key, Map<HK, V> map);

    V hashGet(K key, Object hashKey);

    Boolean hashHasKey(K key, Object hashKey);

    Long hashDelete(K key, Object... hashKeys);

    Map<HK, V> hashEntries(K key);

    List<V> hashValues(K key);

    Set<HK> hashKeys(K key);

    Long hashSize(K key);


    //Key操作 对应redisTemplate.xxx()

    Boolean delete(K key);

    Long delete(Collection<K> keys);

    Boolean expire(K key, final long timeout, final TimeUnit unit);

    Boolean expireAt(K key, final Date date);

    /**
     * 获取剩余过期时间
     *
     * @param key
     * @return
     */
    Long getExpire(K key);

    /**
     * 根据时间格式获取剩余过期时间
     *
     * @param key
     * @param timeUnit
     * @return
     */
    Long getExpire(K key, TimeUnit timeUnit);

    Set<K> keys(K pattern);

    Boolean hasKey(K key);

    void cleanUp();

    DataType type(K key);

}
