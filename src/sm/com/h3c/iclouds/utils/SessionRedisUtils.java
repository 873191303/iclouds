package com.h3c.iclouds.utils;

import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.AppViewsBiz;
import com.h3c.iclouds.biz.VdcBiz;
import com.h3c.iclouds.client.zabbix.ZabbixApi;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.AppViews;
import com.h3c.iclouds.po.Vdc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionRedisUtils {
	
	public static boolean existKey(String key) {
		Object redisValue = RedisUtils.get(key);
		return redisValue == null ? false : true;
	}

	/**
	 * 设置键值
	 * @param key
	 * @param value
	 */
	public static void setValue2Redis(String key, Serializable value) {
		RedisUtils.set(key, value, ConfigProperty.IYUN_TOKEN_TIMEOUT);
	}
	
	/**
	 * 设置键值
	 * @param key
	 * @param value
	 */
	public static void setValue2Redis(String key, Serializable value, int seconds) {
		RedisUtils.set(key, value, seconds);
	}
	
	/**
	 * 获取
	 * @param key
	 * @return
	 */
	public static Object getValue(String key) {
		Object redisValue = RedisUtils.get(key);
		// 重置时间
		RedisUtils.expire(key, ConfigProperty.IYUN_TOKEN_TIMEOUT);
		return redisValue;
	}
	
	/**
	 * 获取
	 * @param key
	 * @return
	 */
	public static void delValue(String key) {
		RedisUtils.delete(key);
	}
	
	public static synchronized void removeSession(String token, String userId) {
		Object tokenObj = SessionRedisUtils.getValue(token);
		if(tokenObj == null) {
			LogUtils.info(SessionRedisUtils.class, ResultType.not_found_token);
		}
		
		if(tokenObj instanceof SessionBean) {
			ZabbixApi.ZABBIX_API_MAP.remove(token);

			AppViewsBiz appViewsBiz = SpringContextHolder.getBean("appViewsBiz");
			VdcBiz vdcBiz = SpringContextHolder.getBean("vdcBiz");
			try {
				String sessionId = token;
				Map<String, Object> queryMap = new HashMap<>();
				queryMap.put("userId", userId);
				queryMap.put("sessionId", sessionId);
				List<AppViews> views = appViewsBiz.listByClass(AppViews.class, queryMap);
				Vdc vdc = vdcBiz.singleByClass(Vdc.class, queryMap);
				if (StrUtils.checkParam(vdc)){
					if (!vdc.getLock()){//lock为false的时候表明没有后台任务正在进行,此时可以清除锁（若为true表明有视图任务还未完成，此时不能清锁）
						vdcBiz.clearLock(vdc);
					}
                }
				if (StrUtils.checkCollection(views)){
					for (AppViews appView : views) {
						if (appView.getLock()){
							appViewsBiz.clearLock(appView);
						}
					}
                }
				// 查询过时的任务导致未执行成功的
			} catch (Exception e) {
				LogUtils.exception(Vdc.class, e);
			}
		}
	}
}
