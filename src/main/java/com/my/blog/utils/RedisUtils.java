package com.my.blog.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置缓存
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取缓存
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     * @param key 键
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 判断key是否存在
     * @param key 键
     * @return 是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 原子性操作：设置键不存在时写入值，并设置过期时间
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否设置成功
     */
    public Boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
    }


    /**
     * 向有序集合中添加元素，使用指定的分数
     * @param key 键
     * @param value 值
     * @param score 分数
     * @return 添加成功的数量
     */
    public Boolean addToZSet(String key, String value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 向集合中添加元素
     * @param key 键
     * @param value 值
     * @return 添加成功的数量
     */
    public Long addToSet(String key, String value) {
        return redisTemplate.opsForSet().add(key, value);
    }



    /**
     * 获取有序集合中的所有成员
     * @param key 键
     * @return 有序集合中的所有成员
     */
    public Set<String> getZSetMembers(String key) {
        Set<Object> members = redisTemplate.opsForZSet().range(key, 0, -1);
        Set<String> result = new HashSet<>();
        if (members != null) {
            for (Object member : members) {
                result.add(member.toString());
            }
        }
        return result;
    }

    /**
     * 获取集合中的所有成员
     * @param key 键
     * @return 集合中的所有成员
     */
    public Set<String> getMembers(String key) {
        Set<Object> members = redisTemplate.opsForSet().members(key);
        Set<String> result = new HashSet<>();
        if (members != null) {
            for (Object member : members) {
                result.add(member.toString());
            }
        }
        return result;
    }


    /**
     * 从有序集合中移除元素
     * @param key 键
     * @param value 值
     * @return 移除成功的数量
     */
    public Long removeFromZSet(String key, String value) {
        return redisTemplate.opsForZSet().remove(key, value);
    }

    /**
     * 从集合中移除元素
     * @param key 键
     * @param value 值
     * @return 移除成功的数量
     */
    public Long removeFromSet(String key, String value) {
        return redisTemplate.opsForSet().remove(key, value);
    }

    /**
     * 设置key的过期时间
     * @param key 键
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否成功
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 根据分数范围移除有序集合中的元素
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 移除成功的数量
     */
    public Long removeRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }
}