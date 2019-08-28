package com.h3c.iclouds.operate;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.utils.HttpUtils;

public class CloudosGroup extends CloudosBase{
	public CloudosGroup(CloudosClient client) {
		this.client=client;
	}
	public JSONObject put(User user,Project project) {
		
		return null;
		
	}
//	cloudos.tenant.normal.user = 5dcd7b3a-ec8a-4d80-be8b-8f4d65d71f3a
//	cloudos.tenant.comptroller = e449e969-5705-4d05-9e4d-64cd322c3ffd
//	cloudos.tenant.normal.manager = 67bcfea5-0428-44be-9219-37d56a5454a1
//	cloudos.tenant.cloud.manager = baae5505-cc96-4cf1-ad5a-a87285af90ae
	public JSONObject put(String userId,String projectId) {
		uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_GRANT);
		uri = HttpUtils.tranUrl(uri, projectId, userId, CacheSingleton.getInstance().getConfigValue("cloudos.tenant.normal.manager"));// 授予普通用户角色
		JSONObject params=new JSONObject();
//		client.setHeaderLocal("Content-Type", "application/json");
		JSONObject result=client.put(uri,params);
		return result;
		
	}
	public JSONObject delete(String userId,String projectId) {
		 uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_GRANT);
		uri = HttpUtils.tranUrl(uri, projectId, userId, CacheSingleton.getInstance().getConfigValue("cloudos.tenant.normal.manager"));// 授予普通用户角色
//		client.setHeaderLocal("Content-Type", "application/json");
		JSONObject result=client.delete(uri);
		return result;
		
	}
	
	public JSONObject get(String userId,String projectId) {
		uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_GRANT);
		uri = HttpUtils.tranUrl(uri, projectId, userId, CacheSingleton.getInstance().getConfigValue("cloudos.tenant.normal.manager"));// 授予普通用户角色
		JSONObject result=client.get(uri);
		return result;
	}
	
}
