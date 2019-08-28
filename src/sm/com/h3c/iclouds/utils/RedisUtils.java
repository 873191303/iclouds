package com.h3c.iclouds.utils;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.h3c.iclouds.base.SpringContextHolder;

public class RedisUtils {
	
	private static RedisTemplate<Serializable, Serializable> redisBiz = SpringContextHolder.getBean("redisTemplate");
	
	private static ValueOperations<Serializable, Serializable> codeVO = redisBiz.opsForValue();
	
	/**
	 * 获取
	 * @param key
	 */
	public static void delete(String key) {
		redisBiz.delete(key);
	}
	
	/**
	 * 设置有效时长
	 * @param key
	 * @param mills
	 */
	public static void expire(String key, long mills) {
		redisBiz.expire(key, mills, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 设置
	 * @param key
	 * @param value
	 * @param mills
	 */
	public static void set(String key, byte[] value, long mills) {
		codeVO.set(key, value, mills, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 设置
	 * @param key
	 * @param value
	 * @param mills
	 */
	public static void set(String key, Serializable value, long mills) {
		codeVO.set(key, value, mills, TimeUnit.MILLISECONDS);
	}

	/**
	 * 设置
	 * @param key
	 * @param value
	 */
	public static void set(String key, Serializable value) {
		codeVO.set(key, value);
	}

	/**
	 * 获取
	 * @param key
	 */
	public static Object get(String key) {
		return codeVO.get(key);
	}
}
