package com.h3c.iclouds.opt;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseClient;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.client.Hp3parClient;
import com.h3c.iclouds.dao.UserDao;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.PwdUtils;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.StrUtils;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yKF7317 on 2017/5/2.
 */
public class IssoClient extends BaseClient implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private CacheSingleton singleton = CacheSingleton.getInstance();
	
	private String token;
	
	private String issoBaseUrl = "http://" + singleton.getIssoApi(IssoParams.ISSO_IP) + ":" + singleton.getIssoApi
			(IssoParams.ISSO_PORT) + "/icloudsopen/";
	
	public static IssoClient createClient(String loginName, String password) {
		IssoClient client = new IssoClient(loginName, password);
		if (null == client.token) {
			LogUtils.warn(IssoClient.class, "ISSO AUTH FAILURE");
			return null;
		}
		return client;
	}
	
	public static IssoClient createAdmin() {
		CacheSingleton single = CacheSingleton.getInstance();
		String loginName = single.getIssoApi(IssoParams.ISSO_ADMIN_LOGINNAME);

		UserDao userDao = SpringContextHolder.getBean("userDao");
		User user = userDao.singleByClass(User.class, StrUtils.createMap("loginName", loginName));
		String password = null;
		try {
			password = PwdUtils.decrypt(user.getPassword(), user.getLoginName() + user.getId());
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(!StrUtils.checkParam(password)) {
			password = single.getIssoApi(user.getPassword());
		}

		IssoClient adminClient = new IssoClient(loginName, password);
		if (null == adminClient.token) {
			LogUtils.warn(IssoClient.class, "ISSO AUTH FAILURE");
			return null;
		}
		return adminClient;
	}
	
	private IssoClient(String loginName, String password){
		this.token = getAuthToken(loginName, password);
	}
	
	public String tranUrl(String url, String ... params){
		if (null != params && params.length > 0) {
			url = HttpUtils.tranUrl(url, params);
		}
		return this.issoBaseUrl + url;
	}
	
	private String getAuthToken(String loginName, String password){
		String url = this.tranUrl(singleton.getIssoApi(IssoParams.ISSO_API_AUTH));
		Map<String, Object> map = new HashMap<>();
		map.put("loginName", loginName);
		map.put("password", password);
		map.put("sysFlag", singleton.getIssoApi(IssoParams.ISSO_SYSFLAG));
		try {
			JSONObject jsonObject = queryHttp(url, map, IssoParams.POST, null);
			if (checkResult(jsonObject)) {
				String token = getValue(jsonObject, "token");
				return token;
			}
			return null;
		} catch (Exception e) {
			LogUtils.exception(Hp3parClient.class, e);
		}
		return null;
	}
	
	public JSONObject queryHttp (String url, Map<String, Object> params, String type, Map<String, Object> headers){
		try {
			HttpRequestBase request = getRequest(url, params, type);
			if(StrUtils.checkParam(request)) {
				request.setConfig(HttpUtils.createRequestConfig());
				request.addHeader("I_CLOUDS_OPEN_TOKEN", this.token);
				request.addHeader("Content-Type", "application/json");
				CloseableHttpClient issoClient = BaseClient.getClient();
				return executeRequest(request, issoClient);
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
