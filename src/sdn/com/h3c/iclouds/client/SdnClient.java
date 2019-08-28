package com.h3c.iclouds.client;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseClient;
import com.h3c.iclouds.opt.MonitorClient;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by yKF7317 on 2017/5/2.
 */
public class SdnClient extends BaseClient implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static CacheSingleton singleton = CacheSingleton.getInstance();
	
	private static String sdnBaseUrl = "http://" + singleton.getSdnApi(SdnParams.SDN_IP) + ":" + singleton.getSdnApi(SdnParams.SDN_PORT);
	
	private String token;
	
	public static SdnClient createClient () {
		SdnClient sdnClient = new SdnClient();
		if (sdnClient.token == null) {
			sdnClient = null;
		}
		return sdnClient;
	}
	
	private SdnClient () {
		this.token = this.getToken();
	}
	
	public static String tranUrl(String uri, String ... params) {
		if (null != params && params.length > 0) {
			uri = HttpUtils.tranUrl(uri, params);
		}
		return sdnBaseUrl + uri;
	}
	
	@Override
	public JSONObject queryHttp (String url, Map<String, Object> param, String type, Map<String, Object> headers) {
		try {
			HttpRequestBase request = this.getRequest(url, param, type);
			if(StrUtils.checkParam(request)) {
				request.setConfig(HttpUtils.createRequestConfig());
				request.addHeader("X-Auth-Token", token);
				request.addHeader("Content-Type", "application/json");
				CloseableHttpClient sdnClient = BaseClient.getClient();
				return this.executeRequest(request, sdnClient);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.exception(MonitorClient.class, e);
		}
		return null;
	}
	
	private String getToken() {
		String url = tranUrl(SdnParams.SDN_API_GET_TOKEN);
		Map<String, Object> param = StrUtils.createMap("user", singleton.getSdnApi(SdnParams.SDN_USERNAME));
		param.put("password", singleton.getSdnApi(SdnParams.SDN_PASSWORD));
		param.put("domain", singleton.getSdnApi(SdnParams.SDN_DOMAIN));
		JSONObject jsonObject = this.queryHttp(url, StrUtils.createMap("login", param), SdnParams.POST, null);
		return this.getToken(jsonObject);
	}
	
	public static boolean checkResult(JSONObject jsonObject) {
		if (null == jsonObject) {
			return false;
		}
		String result = jsonObject.getString("result");
		if (result.startsWith("2")) {
			return true;
		}
		return false;
	}
	
	public static JSONObject getRecord(JSONObject jsonObject) {
		if (jsonObject.containsKey("record")) {
			JSONObject record = jsonObject.getJSONObject("record");
			return record;
		}
		return null;
	}
	
	private String getToken(JSONObject jsonObject) {
		JSONObject record = jsonObject.getJSONObject("record");
		if (SdnClient.checkResult(jsonObject)) {
			return record.getJSONObject("record").getString("token");
		} else {
			LogUtils.warn(this.getClass(), "GET SDN TOKEN FAILURE! error:" + JSONObject.toJSONString(record));
		}
		return null;
	}
}
