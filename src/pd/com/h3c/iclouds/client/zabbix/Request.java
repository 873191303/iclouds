package com.h3c.iclouds.client.zabbix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

@SuppressWarnings("unchecked")
public class Request {
	String jsonrpc = "2.0";
	
	Object params = new Object();

	String method;

	String auth;

	Integer id;
	
	/**
	 * 是否为第二次请求
	 */
	boolean isSR = false;
	
	public boolean isSR() {
		return isSR;
	}

	public void setSR(boolean isSR) {
		this.isSR = isSR;
	}

	public void putList(List<String> array) {
		params = array;
	}
	
	public void putList(JSONArray list) {
		params = list;
	}
	
	public void putMap(Map<String, Object> map) {
		Map<String, Object> temp = null;
		try {
			temp = (Map<String, Object>) params;
		} catch (Exception e) {
		}
		if(temp == null) {
			temp = new HashMap<>();
		}
		temp.putAll(map);
		params = temp;
	}

	
	public void putParam(String key, Object value) {
		Map<String, Object> temp = null;
		try {
			temp = (Map<String, Object>) params;
		} catch (Exception e) {
		}
		if(temp == null) {
			temp = new HashMap<>();
		}
		temp.put(key, value);
		params = temp;
		
	}

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public Object getParams() {
		return params;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
