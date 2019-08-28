package com.h3c.iclouds.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Firewall;
import com.h3c.iclouds.po.Policie;

import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/23.
 */
public interface FirewallBiz extends BaseBiz<Firewall> {
    
    Map<String, Object> update(String id, Firewall entity, CloudosClient client);
    
    Map<String, Object> delete(String id, CloudosClient client);

    void vdcSave(Firewall entity, String vdcId, String projectId);
    
    void localSave(Firewall entity, Policie policie, String vdcId, String status);
    
    String cloudSave(Firewall entity, Policie policie, CloudosClient client, String type);
    
    ResultType verify(Firewall entity);

    void updateStatus(String id, String status);

    List<String> getRouteIds(String fwId);
    
    String cloudDelete(String cloudFwId, String cloudPyId, CloudosClient client);

    void localDelete(Firewall firewall);

    String cloudUpdate(Firewall entity, String id, CloudosClient client);

    void localUpdate(String id, Firewall entity);

    String [] getPolicyIds(String firewallId);
    
    JSONArray getFirewallArray(CloudosClient client);
    
    JSONObject getFirewallJson(String cloudosId, CloudosClient client);
    
    JSONObject getNormJson(String cloudosId, CloudosClient client);
    
    Firewall getFirewallByJson (JSONObject fwJson);
    
    void backWrite(Firewall entity, Policie policie);
}
