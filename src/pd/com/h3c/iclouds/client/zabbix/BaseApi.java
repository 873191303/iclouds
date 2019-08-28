package com.h3c.iclouds.client.zabbix;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;

public abstract class BaseApi {

    private URI uri;

    private String auth;

    private String user;

    private String password;

    private String loginMsg = null;

    public BaseApi(String url) {
        try {
            uri = new URI(url.trim());
        } catch (URISyntaxException e) {
            throw new RuntimeException("url invalid", e);
        }
    }

    public BaseApi(URI uri) {
        this.uri = uri;
    }

    /**
     * 换取token
     * @param user
     * @param password
     * @return
     */
    public boolean login(String user, String password) {
        Request request = RequestBuilder.newBuilder()
                .paramEntry("user", user)
                .paramEntry("password", password)
                .method(ZabbixDefine.USER + ZabbixDefine.LOGIN).build();
        try {
            JSONObject response = post(request);
            String auth = response.getString("result");
            if (auth != null && !"".equals(auth)) {
                this.auth = auth;
                return true;
            } else {
                JSONObject errorObj = response.getJSONObject("error");
                loginMsg = StrUtils.tranString(errorObj.get("data"));
                return false;
            }
        } catch (Exception e) {
            loginMsg = "监控系统异常";
            return false;
        }
    }

    /**
     * 注销
     * @return
     */
    public void logout() {
        Request request = RequestBuilder.newBuilder()
                .paramEntry("user", user)
                .auth(auth)
                .paramEntry("password", password)
                .method(ZabbixDefine.USER + ZabbixDefine.LOGOUT).build();
        this.post(request);
    }

    /**
     * ��������
     * @param request
     * @return
     */
    private JSONObject call(Request request) {
        if (request.getAuth() == null) {
            boolean result = this.login(user, password);
            if(result)
                request.setAuth(auth);
            else
                return null;
        }
        return post(request);
    }

    private void closeResponse(CloseableHttpResponse response) {
        if(response != null) {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public JSONObject post(Request request) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = null;
        try {
        	httpClient = this.init();
            RequestConfig requestConfig = HttpUtils.createRequestConfig();
            HttpUriRequest httpRequest = org.apache.http.client.methods.RequestBuilder
                    .post()
                    .setConfig(requestConfig)
                    .setUri(uri)
                    .addHeader("Accept-Language", "zh_CN")
                    .addHeader("Content-Language", "zh")
                    .addHeader("Content-type", "application/json;charset:UTF-8")
                    .setEntity(new StringEntity(JSON.toJSONString(request), "UTF-8"))
                    .build();
            response = httpClient.execute(httpRequest);

            HttpEntity entity = response.getEntity();
            byte[] data = EntityUtils.toByteArray(entity);
            this.closeResponse(response);
            // get请求不做日志打印，防止日志内容过多
            if(!request.getMethod().contains("get")) {
                LogUtils.info(this.getClass(), "request method: " + request.getMethod() + "\trequest result:" + new String(data));
            }
            String strData = new String(data);
            // session已中断,则更新登录（且请求是第一次）,防止死循环
            if(strData.contains("Session terminated") && request.isSR()) {
                request.setSR(true);    // 设置查询状态标识
                LogUtils.warn(this.getClass(), "Session异常, 重新登录后再做查询");
                this.login(this.user, this.password);
                return this.post(request);
            }
            JSONObject json = JSONObject.parseObject(strData);
            return json;
        } catch (IOException e) {
            LogUtils.exception(this.getClass(), e);
            if(request.isSR()) {
                JSONObject jsonObj = new JSONObject();
                JSONObject data = new JSONObject();
                data.put("error", "CONNECT_TIME_OUT");
                jsonObj.put("data", data);
                return jsonObj;
            } else {
                LogUtils.info(this.getClass(), "请求失败，发起第二次请求");
                request.setSR(true);
                return post(request);
            }
        } finally {
            this.closeResponse(response);
            this.destory(httpClient);
        }
    }

    /**
     * ��ʼ������
     */
    public CloseableHttpClient init() {
        return HttpClients.custom().build();
    }

    /**
     * 摧毁http链接
     */
    public void destory(CloseableHttpClient httpClient) {
        if (httpClient != null) {
            try {
                httpClient.close();
                httpClient = null;
            } catch (Exception e) {
            	LogUtils.exception(this.getClass(), e, "close httpclient error!");
            }
        }
    }

    public BaseApi setUser(String user) {
        this.user = user;
        return this;
    }

    public BaseApi setPassword(String password) {
        this.password = password;
        return this;
    }

    public JSONObject oper(String fun, List<String> list, Integer id) {
        if(fun.contains("..")) {
            fun = fun.replace("..", ".");
        }

        Request request = RequestBuilder.newBuilder()
                .paramList(list)
                .auth(auth)
                .method(fun)
                .build();
        if(id != null)
            request.setId(id);

        return call(request);
    }

    public JSONObject oper(String fun, JSONArray list, Integer id) {
        if(fun.contains("..")) {
            fun = fun.replace("..", ".");
        }

        Request request = RequestBuilder.newBuilder()
                .paramList(list)
                .auth(auth)
                .method(fun)
                .build();
        if(id != null)
            request.setId(id);

        return call(request);
    }

    public JSONObject oper(String fun, Map<String, Object> map) {
        return this.oper(fun, map, null);
    }

    public JSONObject oper(String fun, Map<String, Object> map, Integer id) {
        if(fun.contains("..")) {
            fun = fun.replace("..", ".");
        }
        this.special(fun, map);
        Request request = RequestBuilder.newBuilder()
                .paramEntryMap(map)
                .auth(auth)
                .method(fun)
                .build();
        if(id != null) {
            request.setId(id);
        }
        JSONObject jsonObj = call(request);
        return jsonObj;
    }

    /**
     * zabbix3.0特殊处理
     * @param fun
     * @param map
     */
    public void special(String fun, Map<String, Object> map) {
        if(fun.equals("host.update")) {
            map.put("tls_connect", "1");
            map.put("tls_accept", "1");
        }
    }

    public JSONObject oper(String operation, String fun, List<String> list) {
        return this.oper(operation + fun, list, null);
    }

    public JSONObject oper(String operation, String fun, List<String> list,  Integer id) {
        return this.oper(operation + fun, list, id);
    }

    public JSONObject oper(String operation, String fun, Map<String, Object> map) {
        return this.oper(operation + fun, map, null);
    }

    public JSONObject oper(String operation, String fun, Map<String, Object> map, Integer id) {
        return this.oper(operation + fun, map, id);
    }

    public JSONObject get(String operation, Map<String, Object> map, Integer id) {
        return this.oper(operation + ZabbixDefine.GET, map, id);
    }

    public JSONObject get(String operation, Map<String, Object> map) {
        return this.oper(operation + ZabbixDefine.GET, map);
    }

    public JSONObject get(String operation) {
        return this.oper(operation + ZabbixDefine.GET, new HashMap<>());
    }

    public JSONObject create(String operation, Map<String, Object> map, Integer id) {
        return this.oper(operation + ZabbixDefine.CREATE, map, id);
    }

    public JSONObject create(String operation, Map<String, Object> map) {
        return this.oper(operation + ZabbixDefine.CREATE, map);
    }

    public JSONObject delete(String operation, List<String> list, Integer id) {
        // TODO: 2017/6/29 删除结果可能需要记录到数据库，做后续审计查问题
        return this.oper(operation + ZabbixDefine.DELETE, list, id);
    }

    public JSONObject delete(String operation, String deleteId, Integer id) {
        List<String> list = new ArrayList<>();
        list.add(deleteId);
        return this.delete(operation, list, id);
    }

    public JSONObject delete(String operation, String deleteId) {
        List<String> list = new ArrayList<>();
        list.add(deleteId);
        return this.delete(operation, list, null);
    }

    public JSONObject delete(String operation, List<String> list) {
        return this.delete(operation, list, null);
    }

    public JSONObject update(String operation, Map<String, Object> map, Integer id) {
        return this.oper(operation + ZabbixDefine.UPDATE, map, id);
    }

    public JSONObject update(String operation, Map<String, Object> map) {
        return this.oper(operation + ZabbixDefine.UPDATE, map);
    }

    public JSONObject create(String operation, List<Map<String, Object>> list) {
        JSONArray array = JSONArray.parseArray(JSONArray.toJSONString(list));
        return this.oper(operation + ZabbixDefine.CREATE, array, null);
    }

    public JSONObject update(String operation, List<Map<String, Object>> list) {
        JSONArray array = JSONArray.parseArray(JSONArray.toJSONString(list));
        return this.oper(operation + ZabbixDefine.UPDATE, array, null);
    }

    public String getLoginMsg() {
        return loginMsg;
    }

}