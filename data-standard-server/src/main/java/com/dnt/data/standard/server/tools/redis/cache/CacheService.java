package com.dnt.data.standard.server.tools.redis.cache;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CacheService extends Serializable {
    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    boolean expire(String pk, String key, long time);

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    long getExpire(String pk, String key);

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    boolean hasKey(String pk, String key);

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    void del(String pk, String... key);

    //============================String=============================
    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    Object get(String pk, String key);

    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    boolean set(String pk, String key, Object value);

    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    boolean set(String pk, String key, Object value, long time);

    /**
     * 递增
     * @param key 键
     * @param delta 要增加几(大于0)
     * @return
     */
    long incr(String pk, String key, long delta);

    /**
     * 递减
     * @param key 键
     * @param delta 要减少几(小于0)
     * @return
     */
    long decr(String pk, String key, long delta);

    //================================Map=================================
    /**
     * HashGet
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    Object hget(String pk, String key, String item);

    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    Map<Object,Object> hmget(String pk, String key);

    /**
     * HashSet
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    boolean hmset(String pk, String key, Map<String, Object> map);

    /**
     * HashSet 并设置时间
     * @param key 键
     * @param map 对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    boolean hmset(String pk, String key, Map<String, Object> map, long time);

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true 成功 false失败
     */
    boolean hset(String pk, String key, String item, Object value) ;

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @param time 时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    boolean hset(String pk, String key, String item, Object value, long time);

    /**
     * 删除hash表中的值
     * @param key 键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    void hdel(String pk, String key, Object... item);

    /**
     * 判断hash表中是否有该项的值
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    boolean hHasKey(String pk, String key, String item);

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     * @param key 键
     * @param item 项
     * @param by 要增加几(大于0)
     * @return
     */
    double hincr(String pk, String key, String item, double by);

    /**
     * hash递减
     * @param key 键
     * @param item 项
     * @param by 要减少记(小于0)
     * @return
     */
    double hdecr(String pk, String key, String item, double by);
    //============================set=============================
    /**
     * 根据key获取Set中的所有值
     * @param key 键
     * @return
     */
    Set<Object> sGet(String pk, String key);

    /**
     * 根据value从一个set中查询,是否存在
     * @param key 键
     * @param value 值
     * @return true 存在 false不存在
     */
    boolean sHasKey(String pk, String key, Object value);

    /**
     * 将数据放入set缓存
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    long sSet(String pk, String key, Object... values);

    /**
     * 将set数据放入缓存
     * @param key 键
     * @param time 时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    long sSetAndTime(String pk, String key, long time, Object... values);

    /**
     * 获取set缓存的长度
     * @param key 键
     * @return
     */
    long sGetSetSize(String pk, String key);
    /**
     * 移除值为value的
     * @param key 键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    long setRemove(String pk, String key, Object... values);
    //===============================list=================================

    /**
     * 获取list缓存的内容
     * @param key 键
     * @param start 开始
     * @param end 结束  0 到 -1代表所有值
     * @return
     */
    List<Object> lGet(String pk, String key, long start, long end);
    /**
     * 获取list缓存的长度
     * @param key 键
     * @return
     */
    long lGetListSize(String pk, String key);

    /**
     * 通过索引 获取list中的值
     * @param key 键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    Object lGetIndex(String pk, String key, long index);

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @return
     */
    boolean lSet(String pk, String key, Object value);

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    boolean lSet(String pk, String key, Object value, long time);

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @return
     */
    boolean lSet(String pk, String key, List<Object> value);

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    boolean lSet(String pk, String key, List<Object> value, long time);

    /**
     * 根据索引修改list中的某条数据
     * @param key 键
     * @param index 索引
     * @param value 值
     * @return
     */
    boolean lUpdateIndex(String pk, String key, long index, Object value);

    /**
     * 移除N个值为value
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    long lRemove(String pk, String key, long count, Object value);

    //===============================zset=================================

    /**
     * 添加元素,有序集合是按照元素的score值由小到大排列
     *
     * @param key
     * @param value
     * @param score
     * @return
     */
    Boolean zAdd(String pk,String key, String value, double score);

    /**
     *
     * @param key
     * @param values
     * @return
     */
    Long zAdd(String pk,String key, Set<ZSetOperations.TypedTuple<Object>> values);
    /**
     *
     * @param key
     * @param values
     * @return
     */
    Long zRemove(String pk,String key, Object... values);

    /**
     * 增加元素的score值，并返回增加后的值
     *
     * @param key
     * @param value
     * @param delta
     * @return
     */
    Double zIncrementScore(String pk,String key, String value, double delta);
    /**
     * 返回元素在集合的排名,有序集合是按照元素的score值由小到大排列
     *
     * @param key
     * @param value
     * @return 0表示第一位
     */
    Long zRank(String pk,String key, Object value);

    /**
     * 返回元素在集合的排名,按元素的score值由大到小排列
     *
     * @param key
     * @param value
     * @return
     */
    Long zReverseRank(String pk,String key, Object value);

    /**
     * 获取集合的元素, 从小到大排序
     *
     * @param key
     * @param start
     *            开始位置
     * @param end
     *            结束位置, -1查询所有
     * @return
     */
    Set<Object> zRange(String pk,String key, long start, long end);

    /**
     * 获取集合元素, 并且把score值也获取
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    Set<ZSetOperations.TypedTuple<Object>> zRangeWithScores(String pk,String key, long start,
                                                            long end);

    /**
     * 根据Score值查询集合元素
     *
     * @param key
     * @param min
     *            最小值
     * @param max
     *            最大值
     * @return
     */
    Set<Object> zRangeByScore(String pk,String key, double min, double max);

    /**
     * 根据Score值查询集合元素, 从小到大排序
     *
     * @param key
     * @param min
     *            最小值
     * @param max
     *            最大值
     * @return
     */
    Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(String pk, String key,
                                                                   double min, double max);

    /**
     *
     * @param key
     * @param min
     * @param max
     * @param start
     * @param end
     * @return
     */
    Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(String pk,String key,
                                                                   double min, double max, long start, long end) ;

    /**
     * 获取集合的元素, 从大到小排序
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    Set<Object> zReverseRange(String pk,String key, long start, long end) ;

    /**
     * 获取集合的元素, 从大到小排序, 并返回score值
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    Set<ZSetOperations.TypedTuple<Object>> zReverseRangeWithScores(String pk,String key,
                                                                   long start, long end) ;

    /**
     * 根据Score值查询集合元素, 从大到小排序
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    Set<Object> zReverseRangeByScore(String pk,String key, double min,
                                     double max) ;

    /**
     * 根据Score值查询集合元素, 从大到小排序
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    Set<ZSetOperations.TypedTuple<Object>> zReverseRangeByScoreWithScores(
            String pk,String key, double min, double max) ;

    /**
     *
     * @param key
     * @param min
     * @param max
     * @param start
     * @param end
     * @return
     */
    Set<Object> zReverseRangeByScore(String pk,String key, double min,
                                     double max, long start, long end) ;

    /**
     * 根据score值获取集合元素数量
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    Long zCount(String pk,String key, double min, double max) ;

    /**
     * 获取集合大小
     *
     * @param key
     * @return
     */
    Long zSize(String pk,String key) ;

    /**
     * 获取集合大小
     *
     * @param key
     * @return
     */
    Long zZCard(String pk,String key) ;

    /**
     * 获取集合中value元素的score值
     *
     * @param key
     * @param value
     * @return
     */
    Double zScore(String pk,String key, Object value);

    /**
     * 移除指定索引位置的成员
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    Long zRemoveRange(String pk,String key, long start, long end);

    /**
     * 根据指定的score值的范围来移除成员
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    Long zRemoveRangeByScore(String pk,String key, double min, double max);

    /**
     * 获取key和otherKey的并集并存储在destKey中
     *
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    Long zUnionAndStore(String pk,String key, String otherKey, String destKey) ;

    /**
     *
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    Long zUnionAndStore(String pk,String key, Collection<String> otherKeys,
                        String destKey) ;

    /**
     * 交集
     *
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    Long zIntersectAndStore(String pk,String key, String otherKey,
                            String destKey) ;

    /**
     * 交集
     *
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    Long zIntersectAndStore(String pk,String key, Collection<String> otherKeys,
                            String destKey) ;

    /**
     *
     * @param key
     * @param options
     * @return
     */
    Cursor<ZSetOperations.TypedTuple<Object>> zScan(String pk, String key, ScanOptions options);
    //===============================lock=================================
    boolean lock(String pk,String key,Object value);
    boolean lock(String pk,String key,Object value,long time);
    //====================================scan================================================
    Set<String> scan(String pk, String key);


}
