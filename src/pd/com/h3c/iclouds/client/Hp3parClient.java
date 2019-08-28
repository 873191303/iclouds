package com.h3c.iclouds.client;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yKF7317 on 2017/2/27.
 */
public class Hp3parClient extends BaseClient implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String hostIp;
    
    private Integer hostPort;
    
    private String userName;
    
    private String password;
    
    private String sessionKey;

    public Hp3parClient(String hostIp, Integer hostPort, String userName, String password) {
        this.hostIp = hostIp;
        this.hostPort = hostPort;
        this.userName = userName;
        this.password = password;
        this.sessionKey = login();
    }
    
    public String login() {
        CloseableHttpClient hp3parClient = getClient(hostIp, hostPort, userName, password);
        String sessionKey = null;
        HttpPost post = new HttpPost("https://" + hostIp + ":" + hostPort + "/api/v1/credentials");
        post.addHeader("Accept", "application/json");
        post.addHeader("Accept-Language", "en");
        post.addHeader("Content-Type", "application/json");
        Map<String, String> map = new HashMap<String, String>();
        map.put("user", userName);
        map.put("password", password);
        try {
            HttpEntity entity = new StringEntity(JSONObject.toJSONString(map));
            post.setEntity(entity);
            CloseableHttpResponse response = hp3parClient.execute(post);
            JSONObject loginResponse = HttpUtils.tranResponse(response, hp3parClient);
            if (StrUtils.checkParam(loginResponse)){
                JSONObject record = loginResponse.getJSONObject("record");
                sessionKey = record.getString("key");
            }
        } catch (Exception e) {
            LogUtils.exception(Hp3parClient.class, e);
        }
        return sessionKey;
    }

    /**
     * 执行查询
     * @param uri
     * @param type
     * @param params
     * @return
     */
    public JSONObject queryHttp(String uri, Map<String, Object> params, String type, Map<String, Object> headers) {
        CloseableHttpClient hp3parClient = getClient(hostIp, hostPort, userName, password);
        uri = "https://" + hostIp + ":" + hostPort + uri;
        CloseableHttpResponse response = null;
        try {
            HttpRequestBase request = null;
            if(CloudosParams.GET.equals(type)) {
                HttpGet get = new HttpGet(uri.trim());
                get.addHeader("Accept", "application/json");
                get.addHeader("Accept-Language", "en");
                get.addHeader("Content-Type", "application/json");
                get.addHeader("X-Hp3Par-Wsapi-Sessionkey", this.sessionKey);
                request = get;
            }
            if(StrUtils.checkParam(request)) {
                request.setConfig(HttpUtils.createRequestConfig());
                response = hp3parClient.execute(request);
            }
        } catch (IOException e) {
            LogUtils.exception(Hp3parClient.class, e);
        }
        return HttpUtils.tranResponse(response, hp3parClient);
    }
    
}
