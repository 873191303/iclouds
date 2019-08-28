package com.h3c.iclouds.biz;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.PolicieRule;

/**
 * Created by yKF7317 on 2016/11/23.
 */
public interface PolicieRuleBiz extends BaseBiz<PolicieRule> {

    void save (PolicieRule policieRule);

    void delete (String id);
    
    /**
     * 与cloudos对接删除
     */
    String cloudDelete(String ruleCloudId, String pyCloudId, CloudosClient client);
    
    /**
     * 与cloudos对接新增
     */
    String cloudSave(PolicieRule policieRule, String pyCloudId, String beforeCloudId, String afterCloudId, CloudosClient client);
    
    String cloudSave(PolicieRule policieRule, String pyCloudId, CloudosClient client);
    
    JSONObject getRuleJson(String cloudosId, CloudosClient client);
    
    PolicieRule getRulebyJson(JSONObject ruleJson);
}
