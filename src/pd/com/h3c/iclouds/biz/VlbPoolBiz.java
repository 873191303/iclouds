package com.h3c.iclouds.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.HealthMonitor;
import com.h3c.iclouds.po.VlbPool;
import com.h3c.iclouds.po.VlbVip;

import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/23.
 */
public interface VlbPoolBiz extends BaseBiz<VlbPool> {
   
   Map<String, Object> update(String id, VlbPool vlbPool, CloudosClient client);
   
   Map<String, Object> delete(String id, CloudosClient client);

   String cloudSave(VlbPool vlbPool, HealthMonitor healMonitor, VlbVip vlbVip, CloudosClient client, String type);

   void localSave(VlbPool vlbPool, HealthMonitor healMonitor, VlbVip vlbVip, String vdcId, String status);

   String cloudDelete(String lbId, String poolCdId, String healCdId, String vipCdId, CloudosClient client);

   void localDelete(VlbPool vlbPool);

   String cloudUpdate(VlbPool poolEntity, HealthMonitor healEntity, VlbVip vipEntity, CloudosClient client);

   void localUpdate(VlbPool poolEntity, HealthMonitor healEntity, VlbVip vipEntity);

   void vdcSave(VlbPool vlbPool, String vdcId, String projectId);
   
   ResultType verify(VlbPool vlbPool);
   
   ResultType verifyParam(VlbPool vlbPool);

   void updateStatus(String id, String status);
   
   JSONArray getVipArray(CloudosClient client);
   
   JSONObject getPoolJson(String cloudosId, CloudosClient client);
   
   JSONObject getVipJson(String cloudosId, CloudosClient client);
   
   JSONObject getHealthJson(String cloudosId, CloudosClient client);
   
   VlbPool getPoolByJson(JSONObject poolJson);
   
   VlbVip getVipByJson(JSONObject vipJson);
   
   HealthMonitor gethealthByJson(JSONObject healthJson);
   
   void backWrite(VlbPool pool, HealthMonitor heal, VlbVip vip, CloudosClient client);
}
