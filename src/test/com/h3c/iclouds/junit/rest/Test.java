package com.h3c.iclouds.junit.rest;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zkf5485 on 2017/10/28.
 */
public class Test {

    public static Map<String, String> getMap(String id) {
        switch (id) {
            case "320507102":
                return m1;
            case "320507109":
                return m2;
            case "320507105":
                return m3;
            case "320507100":
                return m4;
            case "320507002":
                return m5;
            case "320507007":
                return m6;
            case "320507003":
                return m7;
            case "320507001":
                return m8;
            case "320507004":
                return m9;
            case "320507108":
                return m10;
            case "320507006":
                return m11;
            default:
                return null;
        }
    }

    public static int _id = 0;

    public static void main(String[] args) throws IOException {
        File dir = new File("D:\\project\\szyt\\txt_1");
        File[] files = dir.listFiles();
        List<Map<String, Object>> re = new ArrayList<>();
        for (File file : files) {
            Path $path = Paths.get(file.getAbsolutePath());
            List<String> list = Files.readAllLines($path, Charset.forName("UTF-8"));
            List<String> first = null;
            String streetName = file.getName().replace(".txt", "");
            String streetId = streetMap.get(streetName);
            if (streetId == null) {
                throw new RuntimeException();
            }
            Map<String, String> communityMap = getMap(streetId);
            if (communityMap == null) {
                throw new RuntimeException();
            }

            for (int i = 0; i < list.size(); i++) {
                String[] data = list.get(i).split("\t");
                if (i == 0) {
                    first = Arrays.asList(data);
                } else {
                    Map<String, Object> map = new HashMap<>();
                    for (int i1 = 0; i1 < 96; i1++) {
                        String key = first.get(i1);
                        if(null == key || "".equals(key)) {
                            continue;
                        }

                        if (key.contains("单位性质")) {
                            key = "单位性质";
                        }

                        String value = data[i1];

                        if (key.contains("单位名称")) {
                            if("苏州罗普斯金铝业股份有限公司".equals(value)) {
                                map.put("url", "https://dev.xiyiqq.com/cciai/trialEval/report?evalNo=2773");
                            } else if("苏州科斯伍德油墨股份有限公司".equals(value)) {
                                map.put("url", "https://dev.xiyiqq.com/cciai/trialEval/report?evalNo=2772");
                            }
                        }

                        boolean isNumber = false;
                        if(key.contains("持证面积") || key.contains("亩") || key.contains("平方米") || key.contains("溢地占比") || key.contains("2015")
                                || key.contains("2016") || key.contains("2017")) {
                            isNumber = true;
                            if(null == value || "".equals(value)) {
                                value = "0";
                            }
                        }

                        String engKey = wordToEng.get(key);
                        if (engKey == null) {
                            System.out.println(streetName);
                            System.out.println(key);
                            throw new RuntimeException();
                        }
                        if (null != key && !"".equals(key)) {
                            map.put(engKey, isNumber ? Double.valueOf(value) : value);
                        }
                    }
                    
                    String communityName = data[94];
                    if (null == communityName || "".equals(communityName)) {
                        communityName = "未知村";
                    } else {
                        if ("taiping".equals(streetName)) {
                            if ("0".equals(communityName)) {
                                communityName = "未知村";
                            }
                        }
                    }
                    String communityId = communityMap.get(communityName);
                    if (communityId == null) {
                        String word = null;
                        if (communityName.contains("村")) {
                            word = communityName.replace("村", "");
                        } else if (communityName.contains("社区")) {
                            word = communityName.replace("社区", "");
                        }
                        if (communityMap.containsKey(word)) {
                            communityId = communityMap.get(word);
                        } else if (communityMap.containsKey(word + "社区")) {
                            communityId = communityMap.get(word + "社区");
                        } else if (communityMap.containsKey(word + "村")) {
                            communityId = communityMap.get(word + "村");
                        }
                    }

                    if (communityId == null) {
                        System.out.println(streetName);
                        System.out.println(communityName);
                        System.out.println(JSONObject.toJSONString(data));
                        //throw new RuntimeException();
                    }
                    double d1 = (double)map.get("2017_realityCountryTax") + (double)map.get("2017_realityLandTax");
                    map.put("2017_realityTotalTax", d1);
    
                    double d2 = (double)map.get("2016_realityCountryTax") + (double)map.get("2016_realityLandTax");
                    map.put("2016_realityTotalTax", d2);
    
                    double d3 = (double)map.get("2015_realityCountryTax") + (double)map.get("2015_realityLandTax");
                    map.put("2015_realityTotalTax", d3);
                    map.put("communityId", communityId);
                    map.put("streetId", streetId);
                    map.put("id", ++_id);
                    re.add(map);
                }
            }


//            File file_ = new File("D:\\project\\szyt\\result\\" + file.getName());
//            if (file_.exists()) {
//                file_.delete();
//            }
//            File parent = file_.getParentFile();
//            if (!parent.exists()) {
//                parent.mkdirs();
//            }
//            file_.createNewFile();
//            Files.write(Paths.get(file_.getAbsolutePath()), str.getBytes(Charset.forName("UTF-8")));
        }
        List<Map<String, Object>> res= new ArrayList<>();
        Set<String> names = new HashSet<>();
        for (int i = 0; i < re.size(); i++) {
            String companyName = re.get(i).get("companyName").toString();
            if(names.add(companyName)) {
                res.add(re.get(i));
            }
        }
        String str = JSONObject.toJSONString(res);
        Files.write(Paths.get("D:\\project\\szyt\\result\\total.txt"), str.getBytes(Charset.forName("UTF-8")));
    }

    public static Map<String, String> m11 = new HashMap<String, String>() {{
        put("未知村", "33");
        put("漕湖街道", "34");
    }};

    public static Map<String, String> m10 = new HashMap<String, String>() {{
        put("未知村", "39");
        put("清水村", "320507108002");
        put("沺泾社区", "41");
        put("新泾村", "42");
        put("洋沟溇村", "320507108004");
    }};

    public static Map<String, String> m9 = new HashMap<String, String>() {{
        put("未知村", "22");
        put("北渔社区", "320507004001");
        put("漕湖村", "320507004002");
        put("鹅东村", "320507004003");
        put("丰泾村", "320507004004");
        put("莲花庄村", "320507004005");
        put("灵峰村", "320507004006");
        put("芮埭村", "320507004007");
        put("石桥村", "320507004010");
        put("新北村", "320507004012");
        put("庄基村", "320507004013");
    }};

    public static Map<String, String> m8 = new HashMap<String, String>() {{
        put("A村", "94");
        put("康桥社区", "320507001010");
        put("蠡口", "320507001012");
        put("娄北", "320507001014");
        put("元和", "320507001027");
        put("朱巷", "320507001029");
    }};

    public static Map<String, String> m7 = new HashMap<String, String>() {{
        put("未知村", "44");
        put("大庄村", "320507003002");
        put("方浜村", "320507003003");
        put("工业区", "47");
        put("胡湾村", "320507003004");
        put("黄桥", "320507003005");
        put("黄桥村", "320507003005");
        put("金之桥工业园", "50");
        put("木巷村", "320507003007");
        put("生田村", "320507003008");
        put("占上村", "320507003009");
        put("张庄村", "320507003010");
    }};

    public static Map<String, String> m6 = new HashMap<String, String>() {{
        put("未知村", "35");
        put("泰元社区", "320507007004");
        put("西子社区", "37");
        put("徐庄社区", "320507007005");
        put("徐庄", "320507007005");
    }};

    public static Map<String, String> m5 = new HashMap<String, String>() {{
        put("未知村", "55");
        put("花倪村", "320507002003");
        put("聚金村", "320507002005");
        put("乐安村", "320507002006");
        put("黎明村", "320507002007");
        put("莲港村", "320507002008");
        put("沈桥村", "320507002010");
        put("盛泽村", "320507002011");
        put("旺巷村", "320507002012");
    }};

    public static Map<String, String> m4 = new HashMap<String, String>() {{
        put("未知村", "64");
        put("何家角", "320507100001");
        put("华阳", "320507100003");
        put("华阳村", "320507100003");
        put("四旺村", "320507100005");
        put("四旺", "320507100005");
        put("望亭镇迎湖村", "68");
        put("项路", "320507100006");
        put("项路村", "320507100006");
        put("新埂村", "320507100007");
        put("新埂", "320507100007");
        put("迎湖", "320507100008");
        put("宅基村", "320507100009");
        put("宅基", "320507100009");
    }};

    public static Map<String, String> m3 = new HashMap<String, String>() {{
        put("A村", "73");
        put("未知村", "74");
        put("凤凰泾", "320507105001");
        put("凤凰泾村", "320507105001");
        put("凤阳村", "320507105002");
        put("骑河村", "320507105003");
        put("盛泽荡村", "320507105004");
        put("渭北村", "320507105005");
        put("渭南村", "320507105006");
        put("渭塘镇", "81");
        put("渭西", "320507105007");
    }};

    public static Map<String, String> m2 = new HashMap<String, String>() {{
        put("岸山", "320507108006");
        put("未知村", "83");
        put("北前村", "320507108007");
        put("北前", "320507108007");
        put("车渡村", "320507108008");
        put("戴溇", "320507108009");
        put("陆巷", "320507108010");
        put("枪堂", "320507109007");
        put("沈周", "320507108003");
        put("圣堂", "320507109008\n");
        put("十图", "320507109009");
        put("消泾村", "320507109010");
    }};

    public static Map<String, String> m1 = new HashMap<String, String>() {{
        put("春申", "320507102001");
        put("春申社区", "320507102001");
        put("埭川", "320507102002");
        put("东新", "320507102003");
        put("方埝", "320507102004");
        put("方埝村", "320507102004");
        put("冯梦龙村", "320507102005");
        put("古宫", "320507102006");
        put("鹤泾村", "320507102007");
        put("鹤泾", "320507102007");
        put("胡桥", "320507102008");
        put("胡桥村", "320507102008");
        put("金龙村", "320507102009");
        put("金龙", "320507102009");
        put("丽岛", "320507102010");
        put("潘阳社区", "320507102011");
        put("潘阳", "320507102011");
        put("潘阳村", "320507102011");
        put("裴圩村", "320507102012");
        put("青龙", "320507102013");
        put("青龙社区", "320507102013");
        put("三埂", "320507102014");
        put("三埂村", "320507102014");
        put("旺庄", "320507102015");
        put("旺庄村", "320507102015");
        put("西桥村", "320507102016");
        put("西桥", "320507102016");
        put("斜桥", "320507102017");
        put("斜桥社区", "320507102017");
        put("长泾", "320507102019");
        put("长泾社区", "320507102019");
        put("长康社区", "320507102020");
        put("黄桥", "320507102020");
        put("黄桥村", "320507102020");
        put("望亭", "320507102020");
    }};

    public static Map<String, String> streetMap = new HashMap<String, String>() {{
        put("huangdai", "320507102");
        put("yangchenghu", "320507109");
        put("weitang", "320507105");
        put("wangting", "320507100");
        put("taiping", "320507002");
        put("chengyang", "320507007");
        put("huangqiao", "320507003");
        put("yuanhe", "320507001");
        put("beiqiao", "320507004");
        put("dujia", "320507108");
        put("chaohu", "320507006");
    }};

    public static Map<String, String> wordToEng = new HashMap<String, String>() {{
        put("单位性质", "unitNature");
        put("单位名称", "companyName");
        put("\"证照情况\"", "isLicense");
        put("企业性质", "companyNature");
        put("经营地", "managePlace");
        put("注册地", "regeditPlace");
        put("统一社会信用代码", "creditNo");
        put("是否工业企业", "isIndustry");
        put("经营范围", "manageRange");
        put("行业代码", "companyNo");
        put("263整治", "bade");
        put("注册时间", "regeditTime");
        put("地块编号", "placeNo");
        put("经营性质", "manageNature");
        put("持证面积", "licenseArea");
        put("\"占地面积总数（亩）\"", "testTOtal");
        put("\"占地面积明细（亩）\"", "detailArea");
        put("\"建筑面积总数（平方米）\"", "buildTotalArea");
        put("\"建筑面积明细（平方米）\"", "buildDetailArea");
        put("\"占地面积（亩）\"", "totalArea");
        put("\"建筑面积（平方米）\"", "buildTotalArea");
        put("\"建设主体\"", "mainBuild");
        put("建设年份", "buildYear");
        put("是否持有土地证", "haveLandCertificate");
        put("土地性质", "landNature");
        put("溢地占比", "landOverflowPercent");
        put("是否租赁", "isLease");
        put("2015_实缴国税", "2015_realityCountryTax");
        put("2015_实缴地税", "2015_realityLandTax");
        put("2015_亩均税收", "2015_avgTax");
        put("2015_销售收入", "2015_totalSale");
        put("2015_亩均销售", "2015_muSale");
        put("2015_增加值", "2015_increaseValue");
        put("2015_平均职工人数", "2015_avgStaff");
        put("2015_全员劳动生产率", "2015_allProductPercent");
        put("2015_研发经费支出", "2015_rdExpenditure");
        put("2015_研发经费占销售的比重", "2015_developFundScale");
        put("2015_用电量", "2015_power");
        put("2015_用电户号", "2015_powerNo");
        put("2015_燃气", "2015_gas");
        put("2015_原煤", "2015_rawCoal");
        put("2015_柴油", "2015_dieselOil");
        put("2015_汽油", "2015_gasoline");
        put("2015_热力", "2015_vapour");
        put("2015_使用其它能源", "2015_otherEnergy");
        put("2015_总能耗", "2015_totalEnergy");
        put("2015_单位能耗增加值", "2015_increaseEnergyValue");
        put("2015_主要污染物排放总量", "2015_totalPollute");
        put("2015_单位主要污染物增加值", "2015_increasePolluteValue");
        put("2016_实缴国税", "2016_realityCountryTax");
        put("2016_实缴地税", "2016_realityLandTax");
        put("2016_亩均税收", "2016_avgTax");
        put("2016_销售收入", "2016_totalSale");
        put("2016_亩均销售", "2016_muSale");
        put("2016_增加值", "2016_increaseValue");
        put("2016_平均职工人数", "2016_avgStaff");
        put("2016_全员劳动生产率", "2016_allProductPercent");
        put("2016_研发经费支出", "2016_rdExpenditure");
        put("2016_研发经费占销售的比重", "2016_developFundScale");
        put("2016_用电量", "2016_power");
        put("2016_用电户号", "2016_powerNo");
        put("2016_燃气", "2016_gas");
        put("2016_原煤", "2016_rawCoal");
        put("2016_柴油", "2016_dieselOil");
        put("2016_汽油", "2016_gasoline");
        put("2016_热力", "2016_vapour");
        put("2016_使用其它能源", "2016_otherEnergy");
        put("2016_总能耗", "2016_totalEnergy");
        put("2016_单位能耗增加值", "2016_increaseEnergyValue");
        put("2016_主要污染物排放总量", "2016_totalPollute");
        put("2016_单位主要污染物增加值", "2016_increasePolluteValue");
        put("2017_实缴国税", "2017_realityCountryTax");
        put("2017_实缴地税", "2017_realityLandTax");
        put("2017_亩均税收", "2017_avgTax");
        put("2017_销售收入", "2017_totalSale");
        put("2017_亩均销售", "2017_muSale");
        put("2017_增加值", "2017_increaseValue");
        put("2017_平均职工人数", "2017_avgStaff");
        put("2017_全员劳动生产率", "2017_allProductPercent");
        put("2017_研发经费支出", "2017_rdExpenditure");
        put("2017_研发经费占销售的比重", "2017_developFundScale");
        put("2017_用电量", "2017_power");
        put("2017_用电户号", "2017_powerNo");
        put("2017_燃气", "2017_gas");
        put("2017_原煤", "2017_rawCoal");
        put("2017_柴油", "2017_dieselOil");
        put("2017_汽油", "2017_gasoline");
        put("2017_热力", "2017_vapour");
        put("2017_使用其它能源", "2017_otherEnergy");
        put("2017_总能耗", "2017_totalEnergy");
        put("2017_单位能耗增加值", "2017_increaseEnergyValue");
        put("2017_主要污染物排放总量", "2017_totalPollute");
        put("2017_单位主要污染物增加值", "2017_increasePolluteValue");
        put("社区", "community");
        put("区域", "street");
        put("", "");
    }};

    public static Map<String, String> communityMap = new HashMap<String, String>() {{

    }};
}
