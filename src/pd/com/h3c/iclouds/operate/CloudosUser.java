package com.h3c.iclouds.operate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.StrUtils;

import java.util.HashMap;
import java.util.Map;

public class CloudosUser extends CloudosBase {

	public CloudosUser(CloudosClient client) {
		this.client = client;
	}

	public JSONObject save(User user, String projectId, String domainId) {
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_USERS);
		Map<String, Object> userParams = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		params.put("default_project_id", projectId);// 无效
		//params.put("name", user.getLoginName()+UUID.randomUUID().toString());
		params.put("name", user.getLoginName());
		params.put("fullname", user.getUserName());
		params.put("certificate", "123456");
		params.put("password", user.getPassword());
		domainId = CacheSingleton.getInstance().getDomainId(domainId);
		params.put("domain_id", domainId);
		params.put("email", user.getEmail());
		params.put("phone", user.getTelephone());
		params.put("address", "这是地址");
		params.put("enabled", true);
		userParams.put("user", params);
		JSONObject result = client.post(uri, userParams);// 创建用户
		return result;
	}

	public JSONObject delete(String userId) {
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_USERS_ACTION);
		uri = HttpUtils.tranUrl(uri, userId);
		JSONObject result = client.delete(uri);// 删除
		return result;
	}

	public JSONObject get(String userId) {
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_USERS_ACTION);
		uri = HttpUtils.tranUrl(uri, userId);
		JSONObject result = client.get(uri);// 查询
		return result;
	}

	public boolean checkName(String loginName) {
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_USERS);
		uri = uri + "?name=" + loginName;
		JSONObject result = client.get(uri);
		JSONArray users = HttpUtils.getJSONArray(result, "users");
		if (StrUtils.checkCollection(users)) {
			// 重复
			return true;
		} else {
			return false;
		}

	}

}
