package com.h3c.iclouds.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Firewall;
import com.h3c.iclouds.po.Route;

import java.util.Map;

/**
 * 路由器biz接口类
 * Created by yKF7317 on 2016/11/23.
 */
public interface RouteBiz extends BaseBiz<Route> {
   
   Map<String, Object> delete(String id, CloudosClient client);
   
   Map<String, Object> linkFirewall(String id, String firewallId, CloudosClient client);
   
   Map<String, Object> unlinkFirewall(String id, CloudosClient client);

   String cloudSave(Route entity, CloudosClient client, String type);
   
   void localSave(Route entity, String vdcId, String status);

   String cloudDelete(String rtCloudId, CloudosClient client);

   void localDelete(Route route);

   String cloudLink(Firewall firewall, String rtCloudId, String type, CloudosClient client);

   void localLink(Route route, String firewallId);
   
   ResultType verify (Route entity, CloudosClient client);
   
   void vdcSave(Route entity, String vdcId, String projectId);
   
   Map<String, Object> setGateway(String id, String networkId, CloudosClient client);

   void updateStatus(String id, String status);
   
   Map<String, Object> unSetGateway(String id, CloudosClient client);
   
   void localSetGateway(Route route, String networkId);
   
   String cloudSetGateway(String rtCdId, String ntCdId, CloudosClient client);
   
   JSONArray getRouteArray(CloudosClient client);
   
   JSONObject getRouteJson(String cloudosId, CloudosClient client);
   
   Route getRouteByJson(JSONObject routeJson);
   
   void backWrite(Route route, String fwStatus);

}
