package com.h3c.iclouds.biz;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.VlbMember;

/**
 * Created by yKF7317 on 2016/11/23.
 */
public interface VlbMemberBiz extends BaseBiz<VlbMember> {
	
	JSONObject getMemberJson(String cloudosId, CloudosClient client);
	
	VlbMember getMember(JSONObject memberJson);
}
