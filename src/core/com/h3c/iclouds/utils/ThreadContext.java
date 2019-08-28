package com.h3c.iclouds.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 线程安全控制安全对象
 * @author zkf5485
 *
 */
public class ThreadContext {
	
	private final static ThreadLocal<Map<String, Object>> threadContext = new ThreadLocal<Map<String, Object>>();
	
	/**
	 * 清空线程安全对象
	 */
	public static void clear() {
		Map<String, Object> map = get();
		if(map != null) {
			// 清空内容
			map.clear();
		}
	}
	
	/**
	 * 设置内容
	 * @param key
	 * @param obj
	 */
	public static void set(String key, Object obj) {
		Map<String, Object> map = threadContext.get();
		if(map == null) {
			map = new HashMap<String, Object>();
			threadContext.set(map);
		}
		map.put(key, obj);
	}

	/**
	 * 根据key获取内容
	 * @param key
	 * @return
	 */
	public static Object get(String key) {
		Map<String, Object> map = threadContext.get();
		if(map == null) {
			return null;
		}
		return map.get(key);
	}
	
	/**
	 * 获取完整内容
	 * @return
	 */
	public static Map<String, Object> get() {
		return threadContext.get();
	}

}
