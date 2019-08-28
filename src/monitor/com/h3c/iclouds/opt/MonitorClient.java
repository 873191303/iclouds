package com.h3c.iclouds.opt;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.base.BaseClient;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.SessionRedisUtils;
import com.h3c.iclouds.utils.StrUtils;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by yKF7317 on 2017/5/2.
 */
public class MonitorClient extends BaseClient implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private CacheSingleton singleton = CacheSingleton.getInstance();
	
	private String token;
	
	private String monitorBaseUrl = "http://" + singleton.getMonitorApi(MonitorParams.MONITOR_IP) + ":" + singleton.getMonitorApi(MonitorParams.MONITOR_PORT);
	
	public static MonitorClient createClient(String icloudsToken) {
		MonitorClient client = new MonitorClient(icloudsToken);
		if (null == client.token) {
			LogUtils.warn(MonitorClient.class, "ISSO AUTH FAILURE");
			return null;
		}
		return client;
	}
	
	public static MonitorClient createAdmin(User user) {
		// 生成token
		String tokenKey = StrUtils.getUUID(ConfigProperty.PROJECT_TOKEN_IYUN_PROFIX);
		SessionBean sessionBean = new SessionBean(user, tokenKey);
		SessionRedisUtils.setValue2Redis(tokenKey, sessionBean);
		MonitorClient client = new MonitorClient(tokenKey);
		if (null == client.token) {
			LogUtils.warn(MonitorClient.class, "ISSO AUTH FAILURE");
			return null;
		}
		return client;
	}
	
	private MonitorClient (String icloudsToken){
		this.token = icloudsToken;
	}
	
	public String tranUrl(String url, String ... params){
		if (null != params && params.length > 0) {
			url = HttpUtils.tranUrl(url, params);
		}
		return this.monitorBaseUrl + url;
	}
	
	public JSONObject queryHttp (String url, Map<String, Object> params, String type, Map<String, Object> headers){
		try {
			HttpRequestBase request = getRequest(url, params, type);
			if(StrUtils.checkParam(request)) {
				request.setConfig(HttpUtils.createRequestConfig());
				request.addHeader("I_CLOUDS_TOKEN", this.token);
				request.addHeader("Content-Type", "application/json");
				CloseableHttpClient monitorClient = BaseClient.getClient();
				return executeRequest(request, monitorClient);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.exception(IssoClient.class, e);
		}
		return null;
	}
	
	/**
	 * 检查请求结果
	 * @param jsonObject
	 * @return
	 */
	public boolean checkResult(JSONObject jsonObject) {
		if (!ResourceHandle.judgeResponse(jsonObject)) {
			return false;
		}
		jsonObject = jsonObject.getJSONObject("record");
		if (null != jsonObject && "success".equals(jsonObject.getString("result"))) {
			return true;
		}
		return false;
	}
	
	public String getValue(JSONObject jsonObject, String key) {
		if (jsonObject.containsKey("record")) {
			jsonObject = jsonObject.getJSONObject("record");
			if (jsonObject.containsKey("record")) {
				jsonObject = jsonObject.getJSONObject("record");
				if (jsonObject.containsKey(key)) {
					return jsonObject.getString(key);
				}
			}
		}
		return null;
	}
	
	/**
	 * 获取错误信息
	 * @param jsonObject
	 * @return
	 */
	public String getError(JSONObject jsonObject) {
		if (!ResourceHandle.judgeResponse(jsonObject)) {
			return HttpUtils.getError(jsonObject);
		}
		jsonObject = jsonObject.getJSONObject("record");
		String result = jsonObject.getString("result");
		if (jsonObject.containsKey("record")) {
			result = JSONObject.toJSONString(jsonObject.get("record"));
		}
		return result;
	}
}
