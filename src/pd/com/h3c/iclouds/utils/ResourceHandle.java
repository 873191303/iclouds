package com.h3c.iclouds.utils;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.common.ConfigProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/12/8.
 */
public class ResourceHandle {

    private static final Map<String, String> firewallMap = new HashMap<>();
    private static final Map<String, String> routeMap = new HashMap<>();
    private static final Map<String, String> externalInfoMap = new HashMap<>();
    private static final Map<String, String> fixedIpMap = new HashMap<>();
    private static final Map<String, String> networkMap = new HashMap<>();
    private static final Map<String, String> subnetMap = new HashMap<>();
    private static final Map<String, String> vlbPoolMap = new HashMap<>();
    private static final Map<String, String> healthMonitorMap = new HashMap<>();
    private static final Map<String, String> vlbvipMap = new HashMap<>();
    private static final Map<String, String> vlbMemberMap = new HashMap<>();
    private static final Map<String, String> policieMap = new HashMap<>();
    private static final Map<String, String> policieRuleMap = new HashMap<>();
    private static final Map<String, String> vlbMap = new HashMap<>();
    private static final Map<String, String> portMap = new HashMap<>();
    private static final Map<String, String> volumeMap = new HashMap<>();
    private static final Map<String, String> floatingipMap = new HashMap<>();

    static {
        //防火墙对接数据格式映射
        firewallMap.put("name", "name");
        firewallMap.put("description", "description");
        firewallMap.put("adminStateUp", "admin_state_up");
        firewallMap.put("tenantId", "tenant_id");
        //路由器对接相关数据格式映射
        fixedIpMap.put("ip", "ip");
        externalInfoMap.put("enableSnat", "enable_snat");
        routeMap.put("name", "name");
        routeMap.put("adminStateUp", "admin_state_up");
        routeMap.put("tenantId", "tenant_id");
        //网络对接相关数据格式映射
        networkMap.put("adminStateUp", "admin_state_up");
        networkMap.put("name", "name");
        networkMap.put("shared", "shared");
        networkMap.put("externalNetworks", "router:external");
        networkMap.put("tenantId", "tenant_id");
        //子网对接相关数据格式映射
        subnetMap.put("name", "name");
        subnetMap.put("ipVersion", "ip_version");
        subnetMap.put("gatewayIp", "gateway_ip");
        subnetMap.put("cidr", "cidr");
        subnetMap.put("enableDhcp", "enable_dhcp");
        subnetMap.put("tenantId", "tenant_id");
        //负载均衡对接相关数据格式映射
        vlbPoolMap.put("name", "name");
        vlbPoolMap.put("description", "description");
        vlbPoolMap.put("adminStateUp", "admin_state_up");
        vlbPoolMap.put("lbMethod", "lb_method");
        vlbPoolMap.put("protocol", "protocol");
        vlbPoolMap.put("tenantId", "tenant_id");
        //监视器对接相关数据格式映射
        healthMonitorMap.put("delay", "delay");
        healthMonitorMap.put("type", "type");
        healthMonitorMap.put("timeout", "timeout");
        healthMonitorMap.put("maxRetries", "max_retries");
        healthMonitorMap.put("httpMethod", "http_method");
        healthMonitorMap.put("expectedCodes", "expected_codes");
        healthMonitorMap.put("urlPath", "url_path");
        healthMonitorMap.put("adminStateUp", "admin_state_up");
        healthMonitorMap.put("tenantId", "tenant_id");
        //vip池对接相关数据格式映射
        vlbvipMap.put("name", "name");
        vlbvipMap.put("adminStateUp", "admin_state_up");
        vlbvipMap.put("protocol", "protocol");
        vlbvipMap.put("protocolPort", "protocol_port");
        vlbvipMap.put("vipAddress", "address");
        vlbvipMap.put("tenantId", "tenant_id");
        //负载均衡成员对接相关数据格式映射
        vlbMemberMap.put("address", "address");
        vlbMemberMap.put("adminStateUp", "admin_state_up");
        vlbMemberMap.put("protocolPort", "protocol_port");
        vlbMemberMap.put("poolCloudosId", "pool_id");
        vlbMemberMap.put("weight", "weight");
        vlbMemberMap.put("tenantId", "tenant_id");
        //防火墙策略对接相关数据格式映射
        policieMap.put("name", "name");
        policieMap.put("description", "description");
        policieMap.put("audited", "audited");
        policieMap.put("shared", "shared");
        //policieMap.put("tenantId", "tenant_id");
        //防火墙策略规则对接相关数据格式映射
        policieRuleMap.put("name", "name");
        policieRuleMap.put("enabled", "enabled");
        policieRuleMap.put("protocol", "protocol");
        policieRuleMap.put("action", "action");
        policieRuleMap.put("sourcePort", "source_port");
        policieRuleMap.put("destinationPort", "destination_port");
        policieRuleMap.put("destinationIp", "destination_ip_address");
        policieRuleMap.put("sourceIp", "source_ip_address");
        policieRuleMap.put("shared", "shared");
        policieRuleMap.put("description", "description");
        policieRuleMap.put("tenantId", "tenant_id");
        //负载均衡组对接相关数据格式映射
        vlbMap.put("name", "name");
        vlbMap.put("throughPut", "throughPut");
        vlbMap.put("description", "description");
        vlbMap.put("extra", "extra");
        vlbMap.put("projectId", "tenantId");
        //虚拟网卡对接相关数据格式映射
        portMap.put("name", "name");
        portMap.put("adminStateUp", "admin_state_up");
        portMap.put("networkCloudosId", "network_id");
        portMap.put("securityGroupIds", "security_groups");
        portMap.put("bindHost", "binding:host_id");
        portMap.put("deviceOwner", "device_owner");
        //云硬盘对接相关数据格式映射
        volumeMap.put("zone", "availability_zone");
        volumeMap.put("size", "size");
        volumeMap.put("imageRef", "imageRef");
        volumeMap.put("projectId", "project_id");
        volumeMap.put("description", "description");
        //IP对接相关数据格式映射
        floatingipMap.put("name", "name");
        floatingipMap.put("norm", "h3c_norm");
    }

    /**
     * 将数据封装成cloudos需要的参数map
     * @param dataMap
     * @param type
     * @return
     */
    public static Map<String, Object> getParamMap(Map<String, Object> dataMap, String type){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(type, dataMap);
        return paramMap;
    }

    /**
     * 遍历实体对象的map属性与目的map的属性,将需要的属性转换成传递的名称并与值一起放入新的map中
     * @param objectMap
     * @param referMap
     * @return
     */
    public static Map<String, Object> tranMap(Map<String, Object> objectMap, Map<String, String> referMap){
        Map<String, Object> resultMap = new HashMap<>();
        for (String s : objectMap.keySet()) {
            for (String s1 : referMap.keySet()) {
                if (s.equals(s1)){
                    resultMap.put(referMap.get(s), objectMap.get(s));
                }
            }
        }
        return resultMap;
    }

    /**
     * 将实体类转换成与cloudos对接的数据格式
     * @param object
     * @param type
     * @return
     */
    public static Map<String, Object> tranToMap(Object object, String type){
        Map<String, Object> map = InvokeSetForm.tranClassToMap(object);
        Map<String, Object> resultMap = new HashMap<>();
        switch (type){
            case ConfigProperty.RESOURCE_TYPE_FIREWALL:
                resultMap = tranMap(map, firewallMap);
                resultMap.put("router_ids", new ArrayList<>());
                break;
            case ConfigProperty.RESOURCE_TYPE_ROUTE:
                resultMap = tranMap(map, routeMap);
                break;
            case ConfigProperty.RESOURCE_TYPE_NETWORK:
                resultMap = tranMap(map, networkMap);
                break;
            case ConfigProperty.RESOURCE_TYPE_SUBNET:
                resultMap = tranMap(map, subnetMap);
                break;
            case ConfigProperty.RESOURCE_TYPE_VLBPOOL:
                resultMap = tranMap(map, vlbPoolMap);
                break;
            case ConfigProperty.RESOURCE_TYPE_HEALTHMONITOR:
                resultMap = tranMap(map, healthMonitorMap);
                break;
            case ConfigProperty.RESOURCE_TYPE_VLBVIP:
                resultMap = tranMap(map, vlbvipMap);
                break;
            case ConfigProperty.RESOURCE_TYPE_FIREWALL_POLICY:
                resultMap = tranMap(map, policieMap);
                break;
            case ConfigProperty.RESOURCE_TYPE_FIREWALL_POLICY_RULE:
                resultMap = tranMap(map, policieRuleMap);
                break;
            case ConfigProperty.RESOURCE_TYPE_VLB:
                resultMap = tranMap(map, vlbMap);
                break;
            case ConfigProperty.RESOURCE_TYPE_VLBMEMBER:
                resultMap = tranMap(map, vlbMemberMap);
                break;
            case ConfigProperty.RESOURCE_TYPE_PORT:
                resultMap = tranMap(map, portMap);
                break;
            case ConfigProperty.RESOURCE_TYPE_VOLUME:
            	resultMap = tranMap(map, volumeMap);
            	break;
            case ConfigProperty.RESOURCE_TYPE_FLOATINGIP:
            	resultMap = tranMap(map, floatingipMap);
            	break;
            default:break;
        }
        return resultMap;
    }

    /**
     * 判断响应结果是否成功
     * @param response
     * @return
     */
    public static boolean judgeResponse(JSONObject response){
        if (StrUtils.checkParam(response)){
            String result = response.getString("result");
            if ("201".equals(result) || "200".equals(result) || "204".equals(result) || "202".equals(result) || "203".equals(result)) {
                return true;
            }
            if ("500".equals(result)){
                response.put("record", "cloudos_500_error");
            }
        }
        return false;
    }

    /**
     * 获取返回数据的id
     * @param response
     * @param type
     * @return
     */
    public static String getId(JSONObject response, String type){
        JSONObject record = response.getJSONObject("record");
        String id;
        if (StrUtils.checkParam(type)){
            JSONObject object = record.getJSONObject(type);
            id = object.getString("id");
        }else {
            id = record.getString("id");
        }
        return id;
    }

    /**
     * 获取返回数据中的某个参数值
     * @param response
     * @param type
     * @param key
     * @return
     */
    public static String getParam(JSONObject response, String type, String key){
        JSONObject record = response.getJSONObject("record");
        String value;
        if (StrUtils.checkParam(type)){
            JSONObject object = record.getJSONObject(type);
            value = object.getString(key);
        }else {
            value = record.getString(key);
        }
        return value;
    }

}
