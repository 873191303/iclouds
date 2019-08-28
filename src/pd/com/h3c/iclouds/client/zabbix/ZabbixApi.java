package com.h3c.iclouds.client.zabbix;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;

import java.util.HashMap;
import java.util.Map;

public class ZabbixApi extends BaseApi {

    public final static Map<String, ZabbixApi> ZABBIX_API_MAP = new HashMap<>();

	public ZabbixApi(String url) {
		super(url);
	}

	/**
	 * 群组数据
	 */
	public static String GROUP_ARRAY_STRING = "[{\"groupid\": \"$VALUE0$\"}]";

	/**
	 * 模板数据
	 */
	public static String TEMPLATE_ARRAY_STRING = "[{\"templateid\": \"$VALUE0$\"}]";

	/**
	 * 接口数据
	 */
	public static String INTERFACE_ARRAY_STRING = "[{\"type\": 1, \"main\": 1, \"useip\": 1, \"ip\": \"$VALUE0$\", \"dns\": \"\", \"port\": \"$VALUE1$\"}]";

	/**
	 * 替换数据
	 * @param source
	 * @param array
     * @return
     */
	public static String replace(String source, String... array) {
		if(array != null && array.length > 0) {
			for (int i = 0; i < array.length; i++) {
				source = source.replace("$VALUE" + i + "$", array[i]);
			}
		}
		return source;
	}

	public static ZabbixApi createAdmin() {
		return create(
				CacheSingleton.getInstance().getZabbixConfig("zabbix.userName"),
				CacheSingleton.getInstance().getZabbixConfig("zabbix.password")
		);
	}

	public static ZabbixApi create(String user, String password) {
		ZabbixApi zabbixApi = new ZabbixApi(CacheSingleton.getInstance().getZabbixAppUrl());
		if(zabbixApi.login(user, password)) {
			return zabbixApi;
		}
		LogUtils.warn(ZabbixApi.class, "获取Zabbix Api auth失败, 原因: " + zabbixApi.getLoginMsg());
		return null;
	}

	public static String getCreateId(JSONObject jsonObject, String key) {
		try {
			if(jsonObject.containsKey("result")) {
                JSONObject result = jsonObject.getJSONObject("result");
                if(result.containsKey(key)) {
					JSONArray array = result.getJSONArray(key);
					if(!array.isEmpty()) {
						return array.getString(0);
					}
				}
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getErrorMsg(JSONObject jsonObject) {
		JSONObject errorObject = jsonObject.getJSONObject("error");
		if(errorObject == null || errorObject.getString("data") == null) {
			return "未知错误";
		}
		return errorObject.getString("data");
	}

    /**
     * 是否包含result对象，如果包含则表示zabbix请求是成功的
     * @param response
     * @return
     */
    public static boolean requestSuccess(JSONObject response) {
        return response != null && response.containsKey("result");
    }
	
	
	public int checkCount(String type, Map<String, Object> map) {
		if(map == null || map.isEmpty()) {
			throw MessageException.create("必须指定查询对象id");
		}
		map.put("output", "count");
		JSONObject jsonObject = this.get(type, map);
		if(!requestSuccess(jsonObject)) {
			throw MessageException.create("查询监控对象异常，原因：" + ZabbixApi.getErrorMsg(jsonObject));
		}
		return jsonObject.getJSONArray("result").size();
	}
	
	public int checkCount(String type, Object id) {
		return checkCount(type, StrUtils.createMap(type.replace(".", "ids"), id));
	}

	public static String getScriptId(String name, ZabbixApi zabbixApi) {
		String scriptId = null;
		// 是否已经存在
		Map<String, Object> filter = StrUtils.createMap("name", name);
		Map<String, Object> queryMap = StrUtils.createMap("filter", filter);
		queryMap.put("output", new String[]{"scriptid"});   // 只查询单个字段

		JSONObject jsonObject = zabbixApi.get(ZabbixDefine.SCRIPT, queryMap);
		if(ZabbixApi.requestSuccess(jsonObject)) {
			JSONArray array = jsonObject.getJSONArray("result");
			if(StrUtils.checkCollection(array)) {
				scriptId = array.getJSONObject(0).getString("scriptid");
				return scriptId;
			}
		}
		return scriptId;
	}
}
