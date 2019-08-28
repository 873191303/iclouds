package com.h3c.iclouds.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.exception.MessageException;

@SuppressWarnings("rawtypes")
public class LogUtils {

	private static final Map<Class, Logger> logMap = new HashMap<Class, Logger>();

	private static Logger getLog(Class clazz) {
		Logger log = logMap.get(clazz);
		if(log == null) {
			log = LoggerFactory.getLogger(clazz);
			logMap.put(clazz, log);
		}
		return log;
	}

	/**
	 * 需要记录的信息日志
	 * @param clazz
	 * @param obj
	 */
	public static final void info(Class clazz, Object obj) {
		Logger log = getLog(clazz);
		log.info(print(obj));
	}
	
	/**
	 * 异常内容日志打印
	 * @param clazz
	 * @param e
	 */
	public static final void exception(Class clazz, Exception e, Object...objects) {
		Logger log = getLog(clazz);
		if(!(e instanceof MessageException)) {	// message异常不做打印
			e.printStackTrace();
		}
		log.error(print(e.getMessage()));
		if(objects != null && objects.length > 0) {
			for (Object obj : objects) {
				log.error(print(obj));
			}
		}
	}
	
	/**
	 * 重要内容日志打印
	 * @param clazz
	 * @param e
	 */
	public static final void warn(Class clazz, Object obj) {
		Logger log = getLog(clazz);
		log.warn(print(obj));
	}
	
	/**
	 * 日志内容转换，增加公共内容区域
	 * @param obj
	 * @return
	 */
	public static String print(Object obj) {
		StringBuffer buffer = new StringBuffer();
		if(obj instanceof String) {
			buffer.append(obj.toString());
		} else if(obj instanceof JSONObject) {
			buffer.append(((JSONObject)obj).toJSONString());
		} else if(obj instanceof JSONArray) {
			buffer.append(((JSONArray)obj).toJSONString());
		} else {
			buffer.append(StrUtils.toJSONString(obj));
		}
		String token = (String)ThreadContext.get(ConfigProperty.PROJECT_TOKEN_KEY);
		StringBuffer flagStr = null;
		if(StrUtils.checkParam(token)) {
			Object tokenObj = ThreadContext.get(token);
			if(tokenObj != null) {
				String requestUUID = (String)ThreadContext.get(ConfigProperty.REQUEST_UUID);
				flagStr = new StringBuffer();
				if(tokenObj instanceof SessionBean) {
					SessionBean sessionBean = (SessionBean) tokenObj;
					flagStr.append("Current user[")
							.append(sessionBean.getUserId())
							.append("][")
							.append(sessionBean.getUserName())
							.append("][REQUEST_UUID:")
							.append(requestUUID)
							.append("]\t----\t")
							;
				} else {
					flagStr.append("Current user[ABC][")
					.append("REQUEST_UUID:")
					.append(requestUUID)
					.append("]\t----\t")
					;
				}
			}
		}
		if(flagStr == null) {
			flagStr = new StringBuffer();
			String logFlag = (String) ThreadContext.get(ConfigProperty.LOGS_TOP_FLAG);
			if(logFlag == null) {
				flagStr.append("System\t----\t");
			} else if(logFlag.contains("Task")) {
				flagStr.append(logFlag + "\t----\t");
			} else {
				flagStr.append("Quartz\t----\t");
			}
		}
		buffer.insert(0, flagStr.toString());
		return buffer.toString();
	}
}
