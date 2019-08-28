package com.h3c.iclouds.client;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseClient;
import com.h3c.iclouds.common.EisooAPIConst;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yKF7317 on 2017/5/2.
 */
public class EisooClient extends BaseClient implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String tokenId;
	
	private String userId;
	
	private String expires;
	
	private boolean isKeepAlive = false;
	
	private CacheSingleton singleton = CacheSingleton.getInstance();
	
	private CloseableHttpClient eisooClient;
	
	private String eisooBaseUrl = "http://" + singleton.getEisooApi(EisooParams.EISOO_IP) + ":";
	
	public EisooClient (){
		this.eisooClient = getClient();
		setToken();
	}
	
	public EisooClient (boolean isKeepAlive){
		this.isKeepAlive = isKeepAlive;
		this.eisooClient = getClient();
		setToken();
	}
	
	public void setToken(){
		JSONObject tokenObject = getAuthToken();
		if (ResourceHandle.judgeResponse(tokenObject)) {
			JSONObject record = tokenObject.getJSONObject("record");
			this.userId = record.getString("userid");
			this.tokenId = record.getString("tokenid");
			this.expires = record.getString("expires");
		} else {
			LogUtils.warn(EisooClient.class, "Eisso auth exception");
		}
	}
	
	public String tranUrl(String url, String method, String ... params){
		String portParam = url.substring(2, url.indexOf("]"));
		String port = singleton.getEisooApi(portParam);
		url = url.replace(url.substring(0, url.indexOf("]") + 1), port);
		if (StrUtils.checkParam(method) && url.contains("{method}")) {
			url = url.replace("{method}", method);
		}
		if (url.contains("{appid}")) {
			url = url.replace("{appid}", singleton.getEisooApi(EisooParams.EISOO_APPID));
		}
		if (url.contains("{userid}")) {
			url = url.replace("{userid}", this.userId);
		}
		if (url.contains("{tokenid}")) {
			url = url.replace("{tokenid}", this.tokenId);
		}
		if (null != params && params.length > 0) {
			url = HttpUtils.tranUrl(url, params);
		}
		return this.eisooBaseUrl + url;
	}
	
	private JSONObject getAuthToken(){
		if(!isKeepAlive || null == this.eisooClient) {	// 长连接模式
			this.eisooClient = getClient();
		}
		String url = this.tranUrl(singleton.getEisooApi(EisooParams.EISOO_API_AUTH), EisooParams.EISOO_AUTH_METHOD);
		HttpPost post = new HttpPost(url);
		post.addHeader("Accept", "application/json");
		post.addHeader("Content-Type", "application/json");
		Map<String, String> map = new HashMap<>();
		String appId = singleton.getEisooApi(EisooParams.EISOO_APPID);
		String account = singleton.getEisooApi(EisooParams.EISOO_ACCOUNT);
		String appKey = singleton.getEisooApi(EisooParams.EISOO_APPKEY);
		map.put("account", account);
		map.put("appid", appId);
		String key = EisooAPIConst.getMD5(appId + appKey + account);
		map.put("key", key);
		try {
			HttpEntity entity = new StringEntity(JSONObject.toJSONString(map));
			post.setEntity(entity);
			CloseableHttpResponse response = eisooClient.execute(post);
			JSONObject loginResponse = HttpUtils.tranResponse(response, eisooClient, this.isKeepAlive);
			return loginResponse;
		} catch (Exception e) {
			LogUtils.exception(Hp3parClient.class, e);
		}
		return null;
	}
	
	public JSONObject queryHttp (String url, Map<String, Object> params, String type, Map<String, Object> headers){
		if(!isKeepAlive || null == this.eisooClient) {
			this.eisooClient = getClient();
		}
		try {
			CloseableHttpResponse response = null;
			HttpRequestBase request = null;
			HttpEntity entity = null;
			if(CloudosParams.POST.equals(type)) {
				HttpPost post = new HttpPost(url.trim());
				if (StrUtils.checkParam(params)) {
					entity = new StringEntity(JSONObject.toJSONString(params).toString(), "UTF-8");
					post.setEntity(entity);
				}
				request = post;
			} else if(CloudosParams.GET.equals(type)) {
				
			} else if(CloudosParams.DELETE.equals(type)) {
				
			} else if(CloudosParams.PUT.equals(type)) {
				HttpPut put = new HttpPut(url.trim());
				if (StrUtils.checkParam(params)) {
					String filePath = StrUtils.tranString(params.get(CloudosParams.INPUTSTREAM));
					if (StrUtils.checkParam(filePath)) {
						try {
							File file = new File(filePath);
							entity = new InputStreamEntity(new FileInputStream(filePath), file.length(), ContentType.APPLICATION_OCTET_STREAM);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
							LogUtils.warn(getClass(), "File " + filePath + " not Found!");
						}
					} else {
						entity = new StringEntity(JSONObject.toJSONString(params).toString(), "UTF-8");
					}
					put.setEntity(entity);
				}
				request = put;
			}
			if(StrUtils.checkParam(request)) {
				request.setConfig(HttpUtils.createRequestConfig());
				if (StrUtils.checkParam(headers)) {
					for (String s : headers.keySet()) {
						request.addHeader(s, StrUtils.tranString(headers.get(s)));
					}
				}
				response = eisooClient.execute(request);
				JSONObject jsonObject = HttpUtils.tranResponse(response, eisooClient, this.isKeepAlive);
				return jsonObject;
			}
		} catch (IOException e) {
			e.printStackTrace();
			LogUtils.exception(EisooClient.class, e);
		}
		return null;
	}
	
}
