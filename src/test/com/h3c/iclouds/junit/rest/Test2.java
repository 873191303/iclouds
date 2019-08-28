package com.h3c.iclouds.junit.rest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zkf5485 on 2017/11/2.
 */
public class Test2 {

    public static void main(String[] args) {
        try {
            List<String> list = Files.readAllLines(Paths.get("D:\\gitspace\\szbd\\szyt\\src\\main\\webapp\\block_company.json"));
            StringBuilder builder = new StringBuilder();
            list.forEach(str -> builder.append(str));
            JSONArray array = JSONArray.parseArray(builder.toString());
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                double totalSale = obj.getString("2017_totalSale").equals("") ? 0 : obj.getDouble("2017_totalSale");
                double totalTax = obj.getString("2017_realityTotalTax").equals("") ? 0 : obj.getDouble("2017_realityTotalTax");
                double totalArea = obj.getString("totalArea").equals("") ? 0 : obj.getDouble("totalArea");

                if(totalArea == 0) {
                    obj.put("2017_avgTax", 0.);
                    obj.put("2017_muSale", 0.);
                } else {
                    if(totalSale == 0.) {
                        obj.put("2017_muSale", 0.);
                    } else {
                        obj.put("2017_muSale", totalSale / totalArea);
                    }

                    if(totalTax == 0.) {
                        obj.put("2017_avgTax", 0.);
                    } else {
                        obj.put("2017_avgTax", totalTax / totalArea);
                    }
                }
            }
            System.out.println(array);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        Path $path = Paths.get("D:\\project\\szyt\\cun.txt");
//        try {
//            List<String> list = Files.readAllLines($path, Charset.forName("UTF-8"));
//            StringBuilder string = new StringBuilder();
//            for (String s : list) {
//                string.append(s);
//            }
//            JSONObject jsonObj = JSONObject.parseObject(string.toString());
//            JSONArray array = jsonObj.getJSONObject("Data").getJSONArray("tdc");
//            System.out.println(array.size());
//
//            List<Map<String, String>> l = new ArrayList<>();
//
//            Set<String> set = new HashSet<>();
//            for (int i = 0; i < array.size(); i++) {
//                JSONArray tdcArray = array.getJSONArray(i);
//
//                Map<String, String> map = new HashMap<>();
//                for (int i1 = 0; i1 < tdcArray.size(); i1++) {
//                    JSONObject obj1 = tdcArray.getJSONObject(i1);
//                    if(!"Shape".equals(obj1.getString("did"))) {
//                        map.put(obj1.getString("did"), obj1.getString("dv"));
//                    }
//                    if("地块编码".equals(obj1.getString("did"))) {
//                        if(!set.add(obj1.getString("dv"))) {
//                            System.out.println(obj1.getString("dv"));
//                        }
//                    }
//                }
//                l.add(map);
//            }
//            System.out.println(JSONObject.toJSONString(l));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static Map<String, String> m11 = new HashMap<String, String>() {{
        put("未知", "33");
        put("漕湖街道", "34");
        put("未知", "39");
        put("清水", "40");
        put("沺泾", "41");
        put("新泾", "42");
        put("洋沟溇", "43");
        put("未知", "22");
        put("北渔", "23");
        put("漕湖", "24");
        put("鹅东", "25");
        put("丰泾", "26");
        put("莲花庄", "27");
        put("灵峰", "28");
        put("芮埭", "29");
        put("石桥", "30");
        put("新北", "31");
        put("庄基", "32");
    }};

    public static Map<String, String> m8 = new HashMap<String, String>() {{
        put("A", "94");
        put("康桥", "95");
        put("蠡口", "96");
        put("娄北", "97");
        put("元和", "98");
        put("朱巷", "99");
    }};

    public static Map<String, String> m7 = new HashMap<String, String>() {{
        put("未知", "44");
        put("大庄", "45");
        put("方浜", "46");
        put("工业区", "47");
        put("胡湾", "48");
        put("黄桥", "49");
        put("黄桥", "49");
        put("金之桥工业园", "50");
        put("木巷", "51");
        put("生田", "52");
        put("占上", "53");
        put("张庄", "54");
    }};

    public static Map<String, String> m6 = new HashMap<String, String>() {{
        put("未知", "35");
        put("泰元", "36");
        put("西子", "37");
        put("徐庄", "38");
        put("徐庄", "38");
    }};

    public static Map<String, String> m5 = new HashMap<String, String>() {{
        put("未知", "55");
        put("花倪", "56");
        put("聚金", "57");
        put("乐安", "58");
        put("黎明", "59");
        put("莲港", "60");
        put("沈桥", "61");
        put("盛泽", "62");
        put("旺巷", "63");
    }};

    public static Map<String, String> m4 = new HashMap<String, String>() {{
        put("未知", "64");
        put("何家角", "65");
        put("华阳", "66");
        put("华阳", "66");
        put("四旺", "67");
        put("四旺", "67");
        put("望亭镇迎湖", "68");
        put("项路", "69");
        put("项路", "69");
        put("新埂", "70");
        put("新埂", "70");
        put("迎湖", "71");
        put("宅基", "72");
        put("宅基", "72");
    }};

    public static Map<String, String> m3 = new HashMap<String, String>() {{
        put("A", "73");
        put("未知", "74");
        put("凤凰泾", "75");
        put("凤凰泾", "75");
        put("凤阳", "76");
        put("骑河", "77");
        put("盛泽荡", "78");
        put("渭北", "79");
        put("渭南", "80");
        put("渭塘镇", "81");
        put("渭西", "82");
    }};

    public static Map<String, String> m2 = new HashMap<String, String>() {{
        put("岸山", "84");
        put("未知", "83");
        put("北前", "85");
        put("北前", "85");
        put("车渡", "86");
        put("戴溇", "87");
        put("陆巷", "88");
        put("枪堂", "89");
        put("沈周", "90");
        put("圣堂", "91");
        put("十图", "92");
        put("消泾", "93");
    }};

    public static Map<String, String> m1 = new HashMap<String, String>() {{
        put("春申", "1");
        put("春申", "1");
        put("埭川", "2");
        put("东新", "3");
        put("方埝", "4");
        put("方埝", "4");
        put("冯梦龙", "5");
        put("古宫", "6");
        put("鹤泾", "7");
        put("鹤泾", "7");
        put("胡桥", "8");
        put("胡桥", "8");
        put("黄桥", "9");
        put("黄桥", "9");
        put("金龙", "10");
        put("金龙", "10");
        put("丽岛", "11");
        put("潘阳", "12");
        put("潘阳", "12");
        put("潘阳", "12");
        put("裴圩", "13");
        put("青龙", "14");
        put("青龙", "14");
        put("三埂", "15");
        put("三埂", "15");
        put("旺庄", "16");
        put("旺庄", "16");
        put("望亭", "17");
        put("西桥", "18");
        put("西桥", "18");
        put("斜桥", "19");
        put("斜桥", "19");
        put("长泾", "20");
        put("长泾", "20");
        put("长康", "21");
    }};
}
