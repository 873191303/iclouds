package com.h3c.iclouds.operate;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.po.bean.inside.UpdateProject2QuotaBean;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;

import java.util.Map;

public class CloudosQuota extends CloudosBase {

	public CloudosQuota(CloudosClient client) {
		this.client = client;
	}

	public Object updateCloudQuota(Map<String, Integer> map, String rootProjectId, UpdateProject2QuotaBean bean) {
		
		///keystone/{parent_tenantId}/os-quota-sets/{tenantId}
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_QUOTA);
		uri = HttpUtils.tranUrl(uri, rootProjectId, bean.getProjectId());
//		client.setHeaderLocal("Content-type", "application/json");
		JSONObject result = client.get(uri);
		if (!"200".equals(result.getString("result"))) {
			LogUtils.warn(Project.class, result);
			throw new MessageException(ResultType.cloudos_api_error);
		}
		result = result.getJSONObject("record");
		if (!result.containsKey("network")) {
			LogUtils.warn(Project.class, "Get Cloudos Quota Empty");
			throw new MessageException(ResultType.cloudos_login_lose);
		}
		JSONObject networkObject = result.getJSONObject("network");
		JSONObject quota_set = new JSONObject();
		quota_set.put("backup_gigabytes", 1000);
		quota_set.put("backups", 10);
		for (String string : ConfigProperty.class_storage_resource) {
			quota_set.put(string, map.get(string));
		}
		result.getJSONObject("block_storage").put("quota_set", quota_set);
		JSONObject quota_set1 = new JSONObject();
		for (String string : ConfigProperty.class_computer_resource) {
			if ("ram".equals(string)) {
				quota_set1.put(string, map.get(string) * 1024);
			} else {
				quota_set1.put(string, map.get(string));
			}
		}
		result.getJSONObject("compute").put("quota_set", quota_set1);
		for (String string : ConfigProperty.class_network_resource) {
			if ("ips".equals(string)) {
				networkObject.put("vnic", map.get(string));
			} else {
				networkObject.put(string, map.get(string));
			}
		}
		result = client.put(uri, result);
		if (!ResourceHandle.judgeResponse(result)) {
			LogUtils.warn(Project.class, result);
			throw new MessageException(ResultType.cloudos_api_error);
		}
		return null;

	}
	
	public JSONObject init( String rootProjectId,Project project) {
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_QUOTA);
		uri = HttpUtils.tranUrl(uri, rootProjectId, project.getId());
		JSONObject result = client.get(uri);
		
		if (!"200".equals(result.getString("result"))) {
			LogUtils.warn(Project.class, result);
			throw new MessageException(ResultType.cloudos_api_error);
		}
		result = result.getJSONObject("record");
		JSONObject quota_set = new JSONObject();
		quota_set.put("backup_gigabytes", 1000);
		quota_set.put("backups", 10);
		for (String string : ConfigProperty.class_storage_resource) {
			quota_set.put(string, 0);
		}
		result.getJSONObject("block_storage").put("quota_set", quota_set);
		JSONObject quota_set1 = new JSONObject();
		for (String string : ConfigProperty.class_computer_resource) {
			if ("ram".equals(string)) {
				quota_set1.put(string, 0);
			} else {
				quota_set1.put(string, 0);
			}
		}
		result.getJSONObject("compute").put("quota_set", quota_set1);
		JSONObject network=result.getJSONObject("network");
		if (!StrUtils.checkParam(network)) {
			network = new JSONObject();
			result.put("network", network);
		}
		for (String string : ConfigProperty.class_network_resource) {
			if ("ips".equals(string)) {
				network.put("vnic", 0);
			} else {
				network.put(string, 0);
			}
		}
		result = client.put(uri, result);
		if (!ResourceHandle.judgeResponse(result)) {
			LogUtils.warn(Project.class, result);
			throw new MessageException(ResultType.cloudos_api_error);
		}
		return null;
	}
	
}
