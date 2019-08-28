package com.h3c.iclouds.junit.rest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.utils.HttpUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zkf5485 on 2017/11/4.
 */
public class ClientTest {

    public static Map<String, String> streetMap = new HashMap<String, String>() {{
        put("yangchenghu", "320507109");
        put("weitang", "320507105");
        put("wangting", "320507100");
        put("taiping", "320507002");
        put("chengyang", "320507007");
        put("huangdai", "320507102");
        put("huangqiao", "320507003");
        put("yuanhe", "320507001");
        put("beiqiao", "320507004");
        put("dujia", "320507108");
        put("chaohu", "320507006");
    }};

    static String token = "token=3QzM2Mywq/XwoTGoR5tB0e5emXIS2Oc3/r8n6CHk1pZIIqrqiqzdzb0XN0pCOcLyrOyMoxYLBPtC6mKqQxrhyCY4n6ssd1gVI0yYB7nYVdGyNYQuml9xpQ==";

    public static void main(String[] args) {
        final String uri = "http://172.16.101.11/xcscms/sipsd/service/DBService/XCIE_XDDK?" + token;
        String json = get(uri);
        JSONArray array = getBlock(json);
        System.out.println(JSONObject.toJSONString(array));

        List<Map<String, Object>> list = new ArrayList<>();
        int total = 0;
        for (int i = 0; i < array.size(); i++) {
            System.out.println(i);
            Map<String, Object> map = (Map<String, Object>)array.get(i);
            String id = map.get("id").toString();

            int nreg = getNumber("nreg", "3", id);
            map.put("noLincenseCount", nreg);

            int bade = getNumber("bade", "3", id);
            map.put("badeCount", bade);

            int totalCount = getNumber("enum", "3", id);
            map.put("errorTotalCount", totalCount);
            total += totalCount;

            int allopatryCount = getNumber("fregion", "3", id);
            map.put("allopatryCount", allopatryCount);

            int businessCount = getNumber("pent", "3", id);
            map.put("businessCount", businessCount);

            int personalCount = getNumber("pself", "3", id);
            map.put("personalCount", personalCount);

//            http://172.16.101.11/xcscms/sipsd/service/DBService/XCIE_JDHCSQCXQY?token=3QzM2Mywq/XwoTGoR5tB0e5emXIS2Oc3/r8n6CHk1pZIIqrqiqzdzb0XN0pCOcLyrOyMoxYLBPtC6mKqQxrhyCY4n6ssd1gVI0yYB7nYVdGyNYQuml9xpQ==&jlgxlx=3&jlgxdxbs=1011510002&PageSize=9999&PageNo=1
            String totalUrl = "http://172.16.101.11/xcscms/sipsd/service/DBService/XCIE_JDHCSQCXQY?" + token + "&jlgxlx=3&PageSize=9999&PageNo=1&jlgxdxbs=" + id;
            String totalJSON = get(totalUrl);
            JSONObject obj = JSONObject.parseObject(totalJSON);
            int totalCount1 = obj.getJSONObject("Data").getInteger("rnum");
            map.put("totalCount", totalCount1);

            String uuu = "http://172.16.101.11/xcscms/sipsd/service/DBService/XCIE_DKTJ?" + token + "&dkwybs=" + id;
            String uuujson = get(uuu);
            JSONObject uuujsonobj = JSONObject.parseObject(uuujson);
            JSONObject uuuData = uuujsonobj.getJSONObject("Data");
            JSONArray tdcArray = uuuData.getJSONArray("tdc");
            JSONArray tdcObject = tdcArray.getJSONArray(0);
            for (int k = 0; k < tdcObject.size(); k++) {
                String key = "工业实占面积".equals(tdcObject.getJSONObject(k).getString("did")) ? "totalArea":
                        "工业企业总数".equals(tdcObject.getJSONObject(k).getString("did")) ? "industryCount":
                                "销售总额".equals(tdcObject.getJSONObject(k).getString("did")) ? "totalSell":
                                        "税收总额".equals(tdcObject.getJSONObject(k).getString("did")) ? "totalTax":
                        "";
                if(!key.equals("")) {
                    map.put(key, tdcObject.getJSONObject(k).getString("dv"));
                }
            }

            String r = "";
        }
        System.out.println(total);
        System.out.println(JSONObject.toJSONString(array));

        System.exit(-1);
        streetMap.forEach((k, v) -> {
            String url = uri + "&jlgxlx=1&cxcs=nreg&jlgxdxbs=" + v;
            System.out.println(v + "\t" + get(url));
        });
    }

    static int getNumber(String type, String level, String id) {
        String u = "http://172.16.101.11/xcscms/sipsd/service/DBService/XCIE_CXWZQYSL?" + token;
        u += "&cxcs=" + type + "&jlgxlx=" + level + "&jlgxdxbs=" + id;
        String j = get(u);
        JSONObject js = JSONObject.parseObject(j);
        JSONArray tdc = js.getJSONObject("Data").getJSONArray("tdc");
        return tdc.getJSONArray(0).getJSONObject(0).getInteger("dv");
    }

    static JSONArray getBlock(String json) {
        JSONObject jsonObj = JSONObject.parseObject(json.toString());
        JSONArray array = jsonObj.getJSONObject("Data").getJSONArray("tdc");
        System.out.println(array.size());

        JSONArray l = new JSONArray();

        for (int i = 0; i < array.size(); i++) {
            JSONArray tdcArray = array.getJSONArray(i);
            Map<String, Object> map = new HashMap<>();
            for (int i1 = 0; i1 < tdcArray.size(); i1++) {
                JSONObject obj1 = tdcArray.getJSONObject(i1);
                if("地块编码".equals(obj1.getString("did")) || "地块名称".equals(obj1.getString("did")) || "持证面积".equals(obj1.getString("did"))) {
                    String name = "地块编码".equals(obj1.getString("did")) ? "id" : "持证面积".equals(obj1.getString("did")) ? "totalArea" : "name";
                    map.put(name, obj1.getString("dv"));
                }
            }
            l.add(map);
        }
        return l;
    }

    static String get(String url) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url.trim());
        String result = "";
        try {
            CloseableHttpResponse response = client.execute(get);
            HttpEntity entity = null;
            if(response != null) {
                entity = response.getEntity();
            }

            result = EntityUtils.toString(entity, "UTF-8").trim();
//            System.out.println(v + "\t" + json);
            HttpUtils.close(response);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
