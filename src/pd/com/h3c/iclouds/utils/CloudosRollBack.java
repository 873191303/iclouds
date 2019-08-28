package com.h3c.iclouds.utils;

import java.util.HashMap;
import java.util.Map;

import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;

/**
 * Created by yKF7317 on 2017/1/8.
 */
public class CloudosRollBack {

    @SuppressWarnings("unchecked")
	public static void rollBack(String projectId, String type, CloudosClient client){
        Map<String, Map<String, Object>> map = getMap(type);
        Map<String, Object> actionMap = map.get(projectId);
        if (StrUtils.checkParam(actionMap)){
            for (int i = 0; i < actionMap.size(); i++) {
                Map<String, Object> objectMap = (Map<String, Object>)actionMap.get(i+"");
                String uri = StrUtils.tranString(objectMap.get("uri"));
                Map<String, Object> param = new HashMap<>();
                if (StrUtils.checkParam(objectMap.get("param"))){
                    param = (Map<String, Object>)objectMap.get("param");
                }
                String action = StrUtils.tranString(objectMap.get("action"));
                try {
                    cloudosAction(client, uri, action, param);
                } catch (Exception e) {
                    LogUtils.exception(CloudosClient.class, e);
                }
            }
            setMap(projectId, type, map);
        }
    }

    public static Map<String, Map<String, Object>> getMap(String type){
        Map<String, Map<String, Object>> map = new HashMap<>();
        switch (type){
            case ConfigProperty.RESOURCE_TYPE_FIREWALL:
                map = MapRecord.getFwMap();
                break;
            case ConfigProperty.RESOURCE_TYPE_ROUTE:
                map = MapRecord.getRtMap();
                break;
            case ConfigProperty.RESOURCE_TYPE_NETWORK:
                map = MapRecord.getNtMap();
                break;
            case ConfigProperty.RESOURCE_TYPE_VLBPOOL:
                map = MapRecord.getVpMap();
                break;
            default:break;
        }
        return map;
    }

    public static void setMap(String projectId, String type, Map<String, Map<String, Object>> map){
        if (!StrUtils.checkParam(map)){
            map = getMap(type);
        }
        map.remove(projectId);
        switch (type){
            case ConfigProperty.RESOURCE_TYPE_FIREWALL:
                MapRecord.setFwMap(map);
                break;
            case ConfigProperty.RESOURCE_TYPE_ROUTE:
                MapRecord.setRtMap(map);
                break;
            case ConfigProperty.RESOURCE_TYPE_NETWORK:
                MapRecord.setNtMap(map);
                break;
            case ConfigProperty.RESOURCE_TYPE_VLBPOOL:
                MapRecord.setVpMap(map);
                break;
            default:break;
        }
    }

    public static void cloudosAction(CloudosClient client, String uri, String action, Map<String, Object> param){
        switch (action){
            case CloudosParams.DELETE:
                client.delete(uri);
                break;
            case CloudosParams.POST:
                if (StrUtils.checkParam(param)){
                    client.post(uri, param);
                }else {
                    client.post(uri);
                }
                break;
            case CloudosParams.PUT:
                if (StrUtils.checkParam(param)){
                    client.put(uri, param);
                }else {
                    client.put(uri);
                }
                break;
            default:break;
        }
    }

}
