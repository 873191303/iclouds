package com.h3c.iclouds.operate;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.StrUtils;

public class CloudosNetwork extends CloudosBase{

	
	public CloudosNetwork(CloudosClient client) {
		this.client = client;
	}

	public boolean isExist(String cloudosId) {
		// TODO Auto-generated method stub
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_NETWORK_ACTION);
		uri=HttpUtils.tranUrl(uri, cloudosId);
		if (StrUtils.checkParam(client)) {
			JSONObject result = client.get(uri);
			if (ResourceHandle.judgeResponse(result)) {
				return true;
			}else {
				return false;
			}
		}else {
			return true;
		}
		
	}
	
}
