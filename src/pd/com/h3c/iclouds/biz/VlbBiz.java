package com.h3c.iclouds.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Vlb;

/**
 * Created by yKF7317 on 2016/11/23.
 */
public interface VlbBiz extends BaseBiz<Vlb> {
	
	JSONArray getVlbArray(CloudosClient client);
	
	JSONObject getVlbJson(String cloudosId, CloudosClient client);

	Vlb getVlbByJson(JSONObject vlbJson);
}
