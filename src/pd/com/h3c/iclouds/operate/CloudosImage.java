package com.h3c.iclouds.operate;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.po.Rules;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.HttpUtils;

public class CloudosImage extends CloudosBase{

	public CloudosImage(CloudosClient client) {
		this.client = client;
	}
	public List<Rules> get(List<Rules> rules) {
		List<Rules> rules2 = new ArrayList<>();
		for (Rules rule : rules) {
			String uri1 = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_IMAGE);
			uri1 = HttpUtils.tranUrl(uri1, rule.getId());
			JSONObject result = client.get(uri1);
			if (ResourceHandle.judgeResponse(result)) {
				rules2.add(rule);
			}
		}
		return rules2;
	}
	
}
