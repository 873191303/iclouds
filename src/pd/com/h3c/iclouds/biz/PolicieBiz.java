package com.h3c.iclouds.biz;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Policie;

/**
 * Created by yKF7317 on 2016/11/23.
 */
public interface PolicieBiz extends BaseBiz<Policie> {
	
	JSONObject getPolicyJson(String cloudosId, CloudosClient client);
	
	Policie getPolicyByJson (JSONObject pyJson);
	
}
