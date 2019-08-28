package com.h3c.iclouds.junit.rest;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.utils.HttpUtils;

public class cloudClientTest {

	public static void main(String[] args) {
		CloudosClient client = CloudosClient.createAdmin();
		client.setHeaderLocal("H3CloudOS-Core-Target", "block_storage");
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_FIREWALL_ACTION);
		uri = HttpUtils.tranUrl(uri, "4f34b573-890c-45c5-913f-34c73fb8492a");
		System.out.println(client.get(uri));
		/*
		JSONObject result=client.get(uri);
		if ("200".equals(result.getString("result"))) {
			result = result.getJSONObject("record");
			JSONObject quota_set = new JSONObject();
			for (String string : ConfigProperty.class_storage_resource) {
				quota_set.put(string, 1);
			}
			result.put("quota_set", quota_set);
			
			client.put(uri, result);
			
		}else {
			throw new MessageException(ResultType.cloudos_api_error);
		}
		client.setHeaderLocal("H3CloudOS-Core-Target", "compute");
		result=client.get(uri);
		if ("200".equals(result.getString("result"))) {
			result = result.getJSONObject("record");
			JSONObject quota_set1 = new JSONObject();
			for (String string : ConfigProperty.class_computer_resource) {
				if ("ram".equals(string)) {
					quota_set1.put(string, 1 * 1024);
				} else {
					quota_set1.put(string, 2);
				}
			}
			result.put("quota_set", quota_set1);
			client.put(uri, result);
			
		}else {
			throw new MessageException(ResultType.cloudos_api_error);
		}*/
	}
}
