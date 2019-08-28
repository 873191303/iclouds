package com.h3c.iclouds.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseClient;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.InterfacesBiz;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.Interfaces;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by yKF7317 on 2017/2/23.
 */
public class CasClient extends BaseClient implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String hostIp;
    
    private Integer hostPort;
    
    private String userName;
    
    private String password;
    
    private CloseableHttpClient casClient;
    
    private boolean isKeepAlive = false;	// 默认为短连接
    
    /**
     * cas接口id
     */
    public final static String CAS_INTERFACE_ID = "1";
    
    public CasClient(String hostIp, Integer hostPort, String userName, String password) {
        this.hostIp = hostIp;
        this.hostPort = hostPort;
        this.userName = userName;
        this.password = password;
        this.casClient = getClient(hostIp, hostPort, userName, password);
    }
    
    public CasClient(String hostIp, Integer hostPort, String userName, String password, boolean isKeepAlive) {
        this.hostIp = hostIp;
        this.hostPort = hostPort;
        this.userName = userName;
        this.password = password;
        this.isKeepAlive = isKeepAlive;
        this.casClient = getClient(hostIp, hostPort, userName, password);
    }
    
    // 获取cas连接
    public static CasClient createByCasId(String belongCasId) {
        CasClient casClient = null;
        if(belongCasId != null) {
            InterfacesBiz interfacesBiz = SpringContextHolder.getBean("interfacesBiz");
            Interfaces interfaces = interfacesBiz.findById(Interfaces.class, belongCasId);
            try {
                String password = interfaces.getPasswd();
                casClient = new CasClient(interfaces.getIp(), Integer.parseInt(interfaces.getPort()), interfaces.getAdmin(), password);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return casClient;
    }

    /**
     * 执行查询
     * @param uri
     * @param type
     * @param params
     * @return
     */
    public JSONObject queryHttp(String uri, Map<String, Object> params, String type, Map<String, Object> headers) {
        if(!isKeepAlive || null == this.casClient) {	// 长连接模式
            this.casClient = getClient(hostIp, hostPort, userName, password);
        }
        uri = "http://" + hostIp + ":" + hostPort + uri;
        CloseableHttpResponse response = null;
        try {
            HttpRequestBase request = null;
            if(CloudosParams.GET.equals(type)) {
                HttpGet get = new HttpGet(uri.trim());
                get.setHeader("Accept", "application/json");
                request = get;
            } else if (CloudosParams.PUT.equals(type)) {
                HttpPut put = new HttpPut(uri.trim());
                put.setHeader("Content-Type", "application/xml");
                if (StrUtils.checkParam(params)) {
                    HttpEntity entity = new StringEntity(StrUtils.tranMap2Xml(params));
                    put.setEntity(entity);
                }
                put.setHeader("Accept", "application/json");
                request = put;
            }
            if(StrUtils.checkParam(request)) {
                request.setConfig(HttpUtils.createRequestConfig());
                response = this.casClient.execute(request);
            }
        } catch (IOException e) {
            LogUtils.exception(getClass(), e);
        }
        return HttpUtils.tranResponse(response, casClient, this.isKeepAlive);
    }
    
    /**
     * 获取在cas中云主机的存在信息及名称
     * @param casVmId
     * @return
     */
    public JSONObject getStorePath(String casVmId) {
        JSONObject casCloudHost = HttpUtils.getJSONObject(this.get("/cas/casrs/vm/" + casVmId));
        if(casCloudHost == null) {
            return null;
        }
        JSONObject storage = casCloudHost.getJSONObject("storage");
        if(storage == null) {
            return null;
        }

        // 不为连接克隆则直接返回错误
        String backingStore = storage.getString("backingStore");
        String storeFile = storage.getString("storeFile");
        if(!StrUtils.checkParam(backingStore, storeFile)) {
            return null;
        }

        Object networkObj = casCloudHost.get("network");
        JSONObject network = null;
        if(networkObj instanceof JSONArray) {
            for (int i = 0; i < ((JSONArray) networkObj).size(); i++) {
                String ipAddr = casCloudHost.getJSONArray("network").getJSONObject(i).getString("ipAddr");
                if(ipAddr.contains("183.131")) {
                    network = casCloudHost.getJSONArray("network").getJSONObject(i);
                    break;
                }
            }
        } else {
            network = casCloudHost.getJSONObject("network");
        }
        if(network == null) {
            return null;
        }

        // 不为连接克隆则直接返回错误
        String ipAddr = network.getString("ipAddr");
        String mac = network.getString("mac");
        if(!StrUtils.checkParam(ipAddr, mac)) {
            return null;
        }

        JSONObject obj = new JSONObject();
        obj.put("backingStore", backingStore);
        obj.put("storeFile", storeFile);
        obj.put("name", casCloudHost.getString("name"));

        obj.put("ipAddr", ipAddr);
        obj.put("mac", mac);
        return obj;
    }

    public JSONObject getHostIpById(String hostId) {
        String url = "/cas/casrs/host/id/" + hostId;
        JSONObject jsonObject = this.get(url);
        if (ResourceHandle.judgeResponse(jsonObject)) {
            jsonObject = jsonObject.getJSONObject("record");
            if (StrUtils.checkParam(jsonObject) && jsonObject.containsKey("ip")) {
                return jsonObject;
            }
        }
        return null;
    }
    
    public CloseableHttpClient getCasClient () {
        return casClient;
    }
}
