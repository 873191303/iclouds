package com.h3c.iclouds.operate;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.po.bean.model.AzoneBean;
import com.h3c.iclouds.po.Project2Azone;
import com.h3c.iclouds.utils.HttpUtils;

public class CloudosAzone extends CloudosBase {
	public CloudosAzone(CloudosClient client) {
		this.client = client;
	}

	public JSONObject delete(Project2Azone project2Azone, String projectId) {
		client.setHeaderLocal("Content-Type", "text/plain");
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_KEYSTONE);
		// String uri="/keystone/projects/{projectId}/azones/{azoneId}";
		uri = HttpUtils.tranUrl(uri, project2Azone.getId(), project2Azone.getIyuUuid());
		JSONObject result = client.delete(uri);
		return result;
	}

	public JSONObject save(AzoneBean azone, String projectId) {
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_KEYSTONE);
		// String uri="/keystone/projects/{projectId}/azones/{azoneId}";
		client.setHeaderLocal("Content-Type", "text/plain");
		uri = HttpUtils.tranUrl(uri, projectId, azone.getUuid());
		JSONObject result = client.put(uri);
		return result;
	}

	public JSONObject delete(AzoneBean azoneBean, String projectId) {
		client.setHeaderLocal("Content-Type", "text/plain");
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_KEYSTONE);
		// String uri="/keystone/projects/{projectId}/azones/{azoneId}";
		uri = HttpUtils.tranUrl(uri, projectId, azoneBean.getUuid());
		JSONObject result = client.delete(uri);
		return result;
	}
}
