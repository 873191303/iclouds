package com.h3c.iclouds.biz;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBiz;

@SuppressWarnings("rawtypes")
public interface SyncVdcDataBiz extends BaseBiz {

	void startSyncVdc();
	
	void syncRule(JSONObject ruleJson, String pyId);
	
	String syncPort(JSONObject ptJson, String deviceId, String userId);
	
	void syncVlb();
	
	void syncFloatingIp(JSONObject floatingIpJson);
}
