package com.dnt.data.standard.server.tools.redis.cache.impl;


import com.dnt.data.standard.server.tools.redis.RedisKeyUtil;
import com.dnt.data.standard.server.tools.redis.cache.CacheService;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @description: redis工具的实现 <br>
 * @date: 2021/6/23 下午3:43 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@Service
@Slf4j
public class RedisCacheServiceImpl  implements CacheService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    //=============================common============================
    /**
     * 指定缓存失效时间
     * @param key 键
     * @param time 时间(秒)
     * @return
     */
    @Override
    public boolean expire(String pk,String key,long time){
        try {
            if(time>0){
                key = buildCacheName(pk,key);
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */

    @Override
    public long getExpire(String pk,String key){
        key = buildCacheName(pk,key);
        return redisTemplate.getExpire(key,TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false不存在
     */
    @Override
    public boolean hasKey(String pk,String key){
        try {
            key = buildCacheName(pk,key);
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("redisCacheServiceImpl-->hasKey error",e);
            return false;
        }
    }

    /**
     * 删除缓存
     * @param key 可以传一个值 或多个
     */
    @Override
    public void del(String pk,String ... key){
        if(key!=null&&key.length>0){
            String kName = null;
            if(key.length==1){
                kName = buildCacheName(pk,key[0]);
                redisTemplate.delete(kName);
            }else{
                List<String> keys = new ArrayList<>();
                //重新组装key
                List<String> afBuildKeys = CollectionUtils.arrayToList(key);
                afBuildKeys.forEach(v->{
                    keys.add(buildCacheName(pk,v));
                });
                redisTemplate.delete(keys);
            }
        }
    }

    //============================String=============================
    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    @Override
    public Object get(String pk,String key){
        return key==null?null:redisTemplate.opsForValue().get(buildCacheName(pk,key));
    }

    /**
     * 普通缓存放入
     * @param pk 项目标识
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    @Override
    public boolean set(String pk,String key,Object value) {
        try {
            //数据为空时不做缓存
            if(ObjectUtils.isEmpty(value)){
                return false;
            }
            key = buildCacheName(pk,key);
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    @Override
    public boolean set(String pk,String key,Object value,long time){
        try {
            //数据为空时不做缓存
            if(ObjectUtils.isEmpty(value)){
                return false;
            }

            if(time>0){
                key = buildCacheName(pk,key);
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            }else{
                set(pk,key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("redisCacheServiceImpl set error",e);
            return false;
        }
    }

    /**
     * 递增
     * @param key 键
     * @return
     */
    @Override
    public long incr(String pk,String key, long delta){
        if(delta<0){
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(buildCacheName(pk,key), delta);
    }

    /**
     * 递减
     * @param key 键
     * @return
     */
    @Override
    public long decr(String pk,String key, long delta){
        if(delta<0){
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(buildCacheName(pk,key), -delta);
    }

    //================================Map=================================
    /**
     * HashGet
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    @Override
    public Object hget(String pk,String key,String item){
        return redisTemplate.opsForHash().get(buildCacheName(pk,key), item);
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    @Override
    public Map<Object,Object> hmget(String pk,String key){
        return redisTemplate.opsForHash().entries(buildCacheName(pk,key));
    }

    /**
     * HashSet
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    @Override
    public boolean hmset(String pk,String key, Map<String,Object> map){
        try {
            redisTemplate.opsForHash().putAll(buildCacheName(pk,key), map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     * @param key 键
     * @param map 对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    @Override
    public boolean hmset(String pk,String key, Map<String,Object> map, long time){
        try {
            redisTemplate.opsForHash().putAll(buildCacheName(pk,key), map);
            if(time>0){
                expire(pk,key,time);
            }
            return true;
        } catch (Exception e) {
            log.error("RedisCacheServiceImpl-->hmset error",e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true 成功 false失败
     */

    @Override
    public boolean hset(String pk,String key,String item,Object value) {
        try {
            redisTemplate.opsForHash().put(buildCacheName(pk,key), item, value);
            return true;
        } catch (Exception e) {
            log.error("RedisCacheServiceImpl-->hset error",e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @param time 时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    @Override
    public boolean hset(String pk,String key,String item,Object value,long time) {
        try {
            redisTemplate.opsForHash().put(buildCacheName(pk,key), item, value);
            if(time>0){
                expire(pk,key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("RedisCacheServiceImpl-->hset time error",e);
            return false;
        }
    }

    /**
     * 删除hash表中的值
     * @param key 键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    @Override
    public void hdel(String pk,String key, Object... item){
        redisTemplate.opsForHash().delete(buildCacheName(pk, key),item);
    }

    /**
     * 判断hash表中是否有该项的值
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    @Override
    public boolean hHasKey(String pk,String key, String item){
        return redisTemplate.opsForHash().hasKey(buildCacheName(pk, key), item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     * @param key 键
     * @param item 项
     * @param by 要增加几(大于0)
     * @return
     */
    @Override
    public double hincr(String pk,String key, String item,double by){
        return redisTemplate.opsForHash().increment(buildCacheName(pk,key), item, by);
    }

    /**
     * hash递减
     * @param key 键
     * @param item 项
     * @param by 要减少记(小于0)
     * @return
     */
    @Override
    public double hdecr(String pk,String key, String item,double by){
        return redisTemplate.opsForHash().increment(buildCacheName(pk,key), item,-by);
    }

    //============================set=============================
    /**
     * 根据key获取Set中的所有值
     * @param key 键
     * @return
     */
    @Override
    public Set<Object> sGet(String pk,String key){
        try {
            return redisTemplate.opsForSet().members(buildCacheName(pk,key));
        } catch (Exception e) {
            log.error("RedisCacheServiceImpl-->sGet error",e);
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     * @param key 键
     * @param value 值
     * @return true 存在 false不存在
     */
    @Override
    public boolean sHasKey(String pk,String key,Object value){
        try {
            return redisTemplate.opsForSet().isMember(buildCacheName(pk,key), value);
        } catch (Exception e) {
            log.error("RedisCacheServiceImpl-->sHasKey error",e);
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    @Override
    public long sSet(String pk,String key, Object...values) {
        try {
            return redisTemplate.opsForSet().add(buildCacheName(pk,key), values);
        } catch (Exception e) {
            log.error("RedisCacheServiceImpl-->sSet error",e);
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     * @param key 键
     * @param time 时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    @Override
    public long sSetAndTime(String pk,String key,long time,Object...values) {
        try {
            Long count = redisTemplate.opsForSet().add(buildCacheName(pk,key), values);
            if(time>0) expire(pk,key, time);
            return count;
        } catch (Exception e) {
            log.error("RedisCacheServiceImpl-->sSetAndTime error",e);
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     * @param key 键
     * @return
     */
    @Override
    public long sGetSetSize(String pk,String key){
        try {
            return redisTemplate.opsForSet().size(buildCacheName(pk,key));
        } catch (Exception e) {
            log.error("RedisCacheServiceImpl-->sGetSetSize error",e);
            return 0;
        }
    }

    /**
     * 移除值为value的
     * @param key 键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    @Override
    public long setRemove(String pk,String key, Object ...values) {
        try {
            String nowKey = StringUtils.isEmpty(pk)?key:buildCacheName(pk,key);

            Long count = redisTemplate.opsForSet().remove(nowKey, values);
            return count;
        } catch (Exception e) {
            log.error("RedisCacheServiceImpl-->setRemove error",e);
            return 0;
        }
    }
    //===============================list=================================

    /**
     * 获取list缓存的内容
     * @param key 键
     * @param start 开始
     * @param end 结束  0 到 -1代表所有值
     * @return
     */
    @Override
    public List<Object> lGet(String pk,String key, long start, long end){
        try {
            return redisTemplate.opsForList().range(buildCacheName(pk,key), start, end);
        } catch (Exception e) {
            log.error("RedisCacheServiceImpl-->lGet error",e);
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     * @param key 键
     * @return
     */
    @Override
    public long lGetListSize(String pk,String key){
        try {
            return redisTemplate.opsForList().size(buildCacheName(pk,key));
        } catch (Exception e) {
            log.error("RedisCacheServiceImpl-->lGetListSize error",e);
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     * @param key 键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    @Override
    public Object lGetIndex(String pk,String key,long index){
        try {
            return redisTemplate.opsForList().index(buildCacheName(pk,key), index);
        } catch (Exception e) {
            log.error("RedisCacheServiceImpl-->lGetIndex error",e);
            return null;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @return
     */
    @Override
    public boolean lSet(String pk,String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(buildCacheName(pk,key), value);
            return true;
        } catch (Exception e) {
            log.error("RedisCacheServiceImpl-->lSet error",e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    @Override
    public boolean lSet(String pk,String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(buildCacheName(pk,key), value);
            if (time > 0) expire(pk,key, time);
            return true;
        } catch (Exception e) {
            log.error("RedisCacheServiceImpl-->lSet Time error",e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @return
     */
    @Override
    public boolean lSet(String pk,String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(buildCacheName(pk,key), value);
            return true;
        } catch (Exception e) {
            log.error("RedisCacheServiceImpl-->lSet list error",e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    @Override
    public boolean lSet(String pk,String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(buildCacheName(pk,key), value);
            if (time > 0) expire(pk,key, time);
            return true;
        } catch (Exception e) {
            log.error("RedisCacheServiceImpl-->lset list time error",e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     * @param key 键
     * @param index 索引
     * @param value 值
     * @return
     */
    @Override
    public boolean lUpdateIndex(String pk,String key, long index,Object value) {
        try {
            redisTemplate.opsForList().set(buildCacheName(pk,key), index, value);
            return true;
        } catch (Exception e) {
            log.error("RedisCacheServiceImpl-->lUpdateIndex error",e);
            return false;
        }
    }

    /**
     * 移除N个值为value
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */

    @Override
    public long lRemove(String pk,String key,long count,Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(buildCacheName(pk,key), count, value);
            return remove;
        } catch (Exception e) {
            log.error("RedisCacheServiceImpl-->lRemove error",e);
            return 0;
        }
    }
    //===============================zset=================================
    /**
     * 添加元素,有序集合是按照元素的score值由小到大排列
     *
     * @param key
     * @param value
     * @param score
     * @return
     */
    public Boolean zAdd(String pk,String key, String value, double score) {
        return redisTemplate.opsForZSet().add(buildCacheName(pk,key), value, score);
    }

    /**
     *
     * @param key
     * @param values
     * @return
     */
    public Long zAdd(String pk,String key, Set<TypedTuple<Object>> values) {
        return redisTemplate.opsForZSet().add(buildCacheName(pk,key), values);
    }

    /**
     *
     * @param key
     * @param values
     * @return
     */
    public Long zRemove(String pk,String key, Object... values) {
        return redisTemplate.opsForZSet().remove(buildCacheName(pk,key), values);
    }

    /**
     * 增加元素的score值，并返回增加后的值
     *
     * @param key
     * @param value
     * @param delta
     * @return
     */
    public Double zIncrementScore(String pk,String key, String value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(buildCacheName(pk,key), value, delta);
    }

    /**
     * 返回元素在集合的排名,有序集合是按照元素的score值由小到大排列
     *
     * @param key
     * @param value
     * @return 0表示第一位
     */
    public Long zRank(String pk,String key, Object value) {
        return redisTemplate.opsForZSet().rank(buildCacheName(pk,key), value);
    }

    /**
     * 返回元素在集合的排名,按元素的score值由大到小排列
     *
     * @param key
     * @param value
     * @return
     */
    public Long zReverseRank(String pk,String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(buildCacheName(pk,key), value);
    }

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
    public Set<Object> zRange(String pk,String key, long start, long end) {
        return redisTemplate.opsForZSet().range(buildCacheName(pk,key), start, end);
    }

    /**
     * 获取集合元素, 并且把score值也获取
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<TypedTuple<Object>> zRangeWithScores(String pk,String key, long start,
                                                    long end) {
        return redisTemplate.opsForZSet().rangeWithScores(buildCacheName(pk,key), start, end);
    }

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
    public Set<Object> zRangeByScore(String pk,String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(buildCacheName(pk,key), min, max);
    }

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
    public Set<TypedTuple<Object>> zRangeByScoreWithScores(String pk,String key,
                                                           double min, double max) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(buildCacheName(pk,key), min, max);
    }

    /**
     *
     * @param key
     * @param min
     * @param max
     * @param start
     * @param end
     * @return
     */
    public Set<TypedTuple<Object>> zRangeByScoreWithScores(String pk,String key,
                                                           double min, double max, long start, long end) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(buildCacheName(pk,key), min, max,
                start, end);
    }

    /**
     * 获取集合的元素, 从大到小排序
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<Object> zReverseRange(String pk,String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(buildCacheName(pk,key), start, end);
    }

    /**
     * 获取集合的元素, 从大到小排序, 并返回score值
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<TypedTuple<Object>> zReverseRangeWithScores(String pk,String key,
                                                           long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(buildCacheName(pk,key), start,
                end);
    }

    /**
     * 根据Score值查询集合元素, 从大到小排序
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<Object> zReverseRangeByScore(String pk,String key, double min,
                                            double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(buildCacheName(pk,key), min, max);
    }

    /**
     * 根据Score值查询集合元素, 从大到小排序
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<TypedTuple<Object>> zReverseRangeByScoreWithScores(
            String pk,String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(buildCacheName(pk,key),
                min, max);
    }

    /**
     *
     * @param key
     * @param min
     * @param max
     * @param start
     * @param end
     * @return
     */
    public Set<Object> zReverseRangeByScore(String pk,String key, double min,
                                            double max, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeByScore(buildCacheName(pk,key), min, max,
                start, end);
    }

    /**
     * 根据score值获取集合元素数量
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zCount(String pk,String key, double min, double max) {
        return redisTemplate.opsForZSet().count(buildCacheName(pk,key), min, max);
    }

    /**
     * 获取集合大小
     *
     * @param key
     * @return
     */
    public Long zSize(String pk,String key) {
        return redisTemplate.opsForZSet().size(buildCacheName(pk,key));
    }

    /**
     * 获取集合大小
     *
     * @param key
     * @return
     */
    public Long zZCard(String pk,String key) {
        return redisTemplate.opsForZSet().zCard(buildCacheName(pk,key));
    }

    /**
     * 获取集合中value元素的score值
     *
     * @param key
     * @param value
     * @return
     */
    public Double zScore(String pk,String key, Object value) {
        return redisTemplate.opsForZSet().score(buildCacheName(pk,key), value);
    }

    /**
     * 移除指定索引位置的成员
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Long zRemoveRange(String pk,String key, long start, long end) {
        return redisTemplate.opsForZSet().removeRange(buildCacheName(pk,key), start, end);
    }

    /**
     * 根据指定的score值的范围来移除成员
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zRemoveRangeByScore(String pk,String key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(buildCacheName(pk,key), min, max);
    }

    /**
     * 获取key和otherKey的并集并存储在destKey中
     *
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long zUnionAndStore(String pk,String key, String otherKey, String destKey) {
        return redisTemplate.opsForZSet().unionAndStore(buildCacheName(pk,key), otherKey, destKey);
    }

    /**
     *
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long zUnionAndStore(String pk,String key, Collection<String> otherKeys,
                               String destKey) {
        return redisTemplate.opsForZSet()
                .unionAndStore(buildCacheName(pk,key), otherKeys, destKey);
    }

    /**
     * 交集
     *
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long zIntersectAndStore(String pk,String key, String otherKey,
                                   String destKey) {
        return redisTemplate.opsForZSet().intersectAndStore(buildCacheName(pk,key), otherKey,
                destKey);
    }

    /**
     * 交集
     *
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long zIntersectAndStore(String pk,String key, Collection<String> otherKeys,
                                   String destKey) {
        return redisTemplate.opsForZSet().intersectAndStore(buildCacheName(pk,key), otherKeys,
                destKey);
    }

    /**
     *
     * @param key
     * @param options
     * @return
     */
    public Cursor<TypedTuple<Object>> zScan(String pk,String key, ScanOptions options) {
        return redisTemplate.opsForZSet().scan(buildCacheName(pk,key), options);
    }

    //===============================lock=================================
    @Override
    public boolean lock(String pk, String key, Object value) {
        String lock = buildCacheName(pk,key);
        return (boolean) redisTemplate.execute((RedisCallback) connection->{
            long expireAt = System.currentTimeMillis() + 300 + 1;
            Boolean acquire = connection.setNX(lock.getBytes(), String.valueOf(expireAt).getBytes());
            if (acquire) {
                return true;
            } else {
                byte[] bv = connection.get(lock.getBytes());
                if (ArrayUtils.isEmpty(bv) && bv.length > 0) {
                    long expireTime = Long.parseLong(new String(bv));
                    if (expireTime < System.currentTimeMillis()) {
                        // 如果锁已经过期
                        byte[] oldValue = connection.getSet(lock.getBytes(), String.valueOf(System.currentTimeMillis() + 300 + 1).getBytes());
                        // 防止死锁
                        return Long.parseLong(new String(oldValue)) < System.currentTimeMillis();
                    }
                }
            }
            return false;
        });
    }

    @Override
    public boolean lock(String pk, String key, Object value, long time) {
        String lockKey = buildCacheName(pk,key);
        boolean b = (boolean) redisTemplate.execute((RedisCallback) connection->{
            Boolean acquire = connection.setNX(lockKey.getBytes(), String.valueOf(value).getBytes());
            if (acquire) {
                return true;
            } else {
                byte[] bv = connection.get(lockKey.getBytes());
                if (ArrayUtils.isEmpty(bv) && bv.length > 0) {
                    long expireTime = Long.parseLong(new String(bv));
                    if (expireTime < System.currentTimeMillis()) {
                        // 如果锁已经过期
                        byte[] oldValue = connection.getSet(lockKey.getBytes(), String.valueOf(value).getBytes());
                        // 防止死锁
                        return Long.parseLong(new String(oldValue)) < System.currentTimeMillis();
                    }
                }
            }
            return false;
        });
        if(b) {
            expire(pk, key, time);
        }
        return b;
    }


    //====================================scan================================================

    @Override
    public Set<String> scan(String pk, String key) {
        String pattern = buildCacheName(pk,key);
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keys = Sets.newHashSet();
            ScanOptions scanOptions = ScanOptions.scanOptions().match(pattern).count(10000).build();


            Cursor<String> cursor = (Cursor<String>) redisTemplate.executeWithStickyConnection(
                    redisConnection -> new ConvertingCursor<>(redisConnection.scan(scanOptions),
                            redisTemplate.getKeySerializer()::deserialize));
            try {
                cursor.forEachRemaining(k -> {
                   keys.add(StringUtils.substring(k,4));
                });
            }finally {
                try {
                    cursor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            return keys;
        });
    }





    private String buildCacheName(String pk, String key) {
        return RedisKeyUtil.PREFIX + ":" + pk + ":" + key;
    }
}
