package com.h3c.iclouds.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.h3c.iclouds.auth.CacheSingleton;

public class StrUtils {
	
	/**
	 * 取数组第一个元素，为null则返回""
	 * 
	 * @param str
	 * @return
	 */
	public static String tranArrayToStr(String[] str) {
		return str != null && str.length > 0 ? str[0] : "";
	}
	
	public static String tranString(Object obj) {
		return obj != null ? obj.toString() : "";
	}
	
	public static Integer tranInteger(Object obj) {
		Integer intObj = null;
		try {
			intObj = (obj != null && obj.toString().length() > 0) ? Integer.valueOf(obj.toString()) : 0;
		} catch (Exception e) {
			e.printStackTrace();
			intObj = 0;
		}
		return intObj;
	}
	
	public static Double tranDouble(Object obj) {
		Double doubleObj = null;
		try {
			doubleObj = (obj != null && obj.toString().length() > 0) ? Double.valueOf(obj.toString()) : 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doubleObj;
	}
	
	public static Float tranFloat(Object obj) {
		Float floatObj = null;
		try {
			floatObj = (obj != null && obj.toString().length() > 0) ? Float.valueOf(obj.toString()) : 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return floatObj;
	}
	
	public static Long tranLong(Object obj) {
		return (obj != null && obj.toString().length() > 0) ? Long.valueOf(obj.toString()) : 0l;
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean checkParam(Object...objects) {
		if (null == objects){
			return false;
		}
		for(Object obj : objects) {
			if(null == obj) {
				return false;
			}
			if(obj instanceof String) {
				if(obj.toString().trim().equals("")) {
					return false;
				}
			} else if(obj instanceof Collection) {
				if(((Collection) obj).isEmpty()) {
					return false;
				}
			} else if(obj instanceof Map) {
				if(((Map) obj).isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	public static boolean checkCollection(Collection collection) {
		return collection != null && !collection.isEmpty() ? true : false;
	}
	
	/**
	 * int型字符串排序
	 * @param strs
	 * @return
	 */
	public static String[] sortByStr(String[] strs){
		String temp = "";
		for (int i = 0; i < strs.length; i++) {
			for (int j = i + 1; j <= strs.length - 1; j++) {
				if (Integer.valueOf(strs[i]) > Integer.valueOf(strs[j])) {
					temp = strs[i];
					strs[i] = strs[j];
					strs[j] = temp;
				}
			}
		}
		return strs;
	}
	
	/**
	 * 判断字符串是否为数字
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str){
		boolean result = true;
		for (int i = str.length();--i>=0;){   
		   if (!Character.isDigit(str.charAt(i))){
			   result = false;
		   }
		}
		return result;
	}
	
	public static boolean isNotEmpty(String str) {
		if(str == null || "".equals(str)) {
			return false;
		}
		return true;
	}
	
	public static List<Map<String, Object>> createList(String id, String[] array) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if(array != null && array.length > 0) {
			for (int i = 0; i < array.length; i++) {
				if(array[i].length() > 0){
					Map<String, Object> linkMap = new HashMap<String, Object>();
					linkMap.put(id, array[i]);
					list.add(linkMap);
				}
			}
		}
		return list;
	}
	
	public static List<Map<String, Object>> createList(String id, List<String> array) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if(array != null && array.size() > 0) {
			for (int i = 0; i < array.size(); i++) {
				if(array.get(i).length() > 0){
					Map<String, Object> linkMap = new HashMap<String, Object>();
					linkMap.put(id, array.get(i));
					list.add(linkMap);
				}
			}
		}
		return list;
	}
	
	public static String[] listToArray(List<String> list) {
		String[] array = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}
	
	public static Map<String, Object> createMap(String key, Object value) {
		Map<String, Object> map = createMap();
		map.put(key, value);
		return map;
	}
	
	public static Map<String, Object> createMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		return map;
	}
	
	public static String additionalNum(int num, Integer length) {
		StringBuffer numStr = new StringBuffer(String.valueOf(num));
		if(numStr.length() < length) {
			for (int i = numStr.length(); i < length; i++) {
				numStr.insert(0, "0");
			}
		}
		return numStr.toString();
	}
	
	public static final ObjectMapper objectMapper = new ObjectMapper();
	
	public static String toJSONString(Object obj) {
		try {
			if(obj != null) {
				return objectMapper.writeValueAsString(obj);	
			}
		} catch (JsonProcessingException e) {
			LogUtils.exception(StrUtils.class, e);
		}
		return "";
	}
	
	/**
	 * 获取时间，单位：毫秒。
	 * @param date
	 * @param isBig		为空是否取最大值
	 * @return
	 */
	public static long getTime(Date date, boolean isBig) {
		return date == null  ? (isBig ? Long.MAX_VALUE : -1) : date.getTime();
	}
	
	public static boolean contains(String source, String...strings) {
		if(strings != null && strings.length > 0) {
			for (String str : strings) {
				if(source.contains(str)) {
					return true;
				}
			}
			return false;
		} 
		return true;
	}
	
	public static boolean equals(String source, String...strings) {
		if(strings != null && strings.length > 0) {
			if(source == null) {
				return false;
			}
			for (String str : strings) {
				if(source.equals(str)) {
					return true;
				}
			}
			return false;
		} 
		return true;
	}
	
	public static String getUUID() {
		String uuid = UUID.randomUUID().toString();
		return uuid.replaceAll("-", "");
	}
	
	public static String getUUID(String code) {
		String uuid = getUUID();
		if(code != null) {
			String start = CacheSingleton.getInstance().getConfigValue(code);
			if(StrUtils.checkParam(start))
				uuid = start + uuid;
			else
				uuid = code + uuid;
		}
		return uuid;
	}

    /**
     * 允许不做过滤的URI
     * @param uri
     * @return
     */
	public static boolean checkIgonreURI(String uri) {
        Set<String> ignoreSuffix = CacheSingleton.getInstance().getIgnoreSuffixSet();
        for (String ignore : ignoreSuffix) {
            if(uri.endsWith(ignore)) {
                return true;
            }
        }

        Set<String> ignoreURI = CacheSingleton.getInstance().getIgnoreURISet();
        for (String ignore : ignoreURI) {
            if(uri.contains(ignore)) {
                return true;
            }
        }
		return false;
	}
	
	public static String tranMap2Xml(Map<String, Object> map) {
		StringBuffer result = new StringBuffer();
		if (StrUtils.checkParam(map)) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				result.append(tran2Xml(key, value));
			}
		}
		return result.toString();
	}
	
	public static String tran2Xml(String key, Object value) {
		StringBuffer result = new StringBuffer();
		result.append("<" + key + ">");
		if (value instanceof Map) {
			Map<String, Object> valueMap = (Map<String, Object>)value;
			if (StrUtils.checkParam(valueMap)) {
				for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
					result.append(StrUtils.tran2Xml(entry.getKey(), entry.getValue()));
				}
			}
		} else {
			result.append(StrUtils.tranString(value));
		}
		result.append("</" + key + ">");
		return result.toString();
	}

	public static boolean isFreeBSDImage(String osType) {
        return osType != null &&
                osType.contains(CacheSingleton.getInstance().getTonghuashunConfigKey("iyun.image.freebsd"));
	}
}
