package com.h3c.iclouds.operate;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.po.bean.cloudos.InterfaceAttachment;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;

public class CloudosPorts extends CloudosBase{

	public CloudosPorts(CloudosClient client) {
		this.client = client;
	}

	public JSONObject get(InterfaceAttachment interfaceAttachment) {
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_PORTS_ACTION);
		//String uri="/v2.0/ports/{port_id}";
		//cloudos.api.ports.action
		//String uri="/v2.0/ports/{port_id}";
		uri=HttpUtils.tranUrl(uri, interfaceAttachment.getPort_id());
		JSONObject result=client.get(uri);
		if ("200".equals(result.getString("result"))) {
			
		}else {
			LogUtils.warn(Project.class, result.getString("record"));
			throw new MessageException(ResultType.cloudos_api_error);
		}
		return result;
	}
	public JSONObject delete(String portId) {
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_PORTS_ACTION);
		//String uri="/v2.0/ports/{port_id}";
		uri=HttpUtils.tranUrl(uri, portId);
		JSONObject result=client.delete(uri);
		if ("200".equals(result.getString("result"))) {
			
		}else {
			LogUtils.warn(Project.class, result.getString("record"));
			throw new MessageException(ResultType.cloudos_api_error);
		}
		return result;
	}
}
