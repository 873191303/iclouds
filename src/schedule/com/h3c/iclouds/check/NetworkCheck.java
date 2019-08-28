package com.h3c.iclouds.check;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.po.HealthyInstance;
import com.h3c.iclouds.po.HealthyType2Item;

/**
 * Created by zkf5485 on 2017/9/12.
 */
public class NetworkCheck extends BaseCheck {

    public static final String name = "7";

    public NetworkCheck(JSONObject params, HealthyInstance entity) {
        super(params, entity);
    }

    @Override
    public Double check() {
        String result = this.excuteScript();
        if(result == null) {    // 未能获取到结果
            return 0.;
        }

        Double ping;
        Double loss;
        try {
            String[] array = result.split("\\n");
            ping = this.ping(array[0], .6);
            try {
                loss = this.loss(array[1]);
            } catch (Exception e) {
                e.printStackTrace();
                loss = null;
            }
            if(ping == null || loss == null) {    //  ping 或 telnet为nul则强制为0
                return 0.;
            }
        } catch (Exception e) {
            this.exception(e, "计算健康度失败, 实例id: " + this.entity.getId());
            return 0.;
        }
        return ping + loss;
    }

    Double loss(String value) {
        HealthyType2Item type2Item = this.type2ItemMap.get("loss");
        double weight = type2Item == null ? .4 : type2Item.getWeight();
        String itemId = type2Item == null ? null : type2Item.getId();

        if(!value.contains("avg")) {   // 正常的标志
            this.saveOrigin(0., weight, itemId);
            return null;    // 不正常则返回空
        }
        value = value.substring(value.indexOf("avg,") + 4, value.length());
        value = value.replace("loss)", "").replace("%", "").trim();
        if("100".equals(value)) {
            this.saveOrigin(0., weight, itemId);
            return null;
        }
        int intVal = Integer.valueOf(value);
        double result = (100 - intVal) / 100 * weight;
        this.saveOrigin(result, weight, itemId);
        return result;
    }

    @Override
    public String getCommand() {
        // 不存在则创建
        StringBuilder builder = new StringBuilder();
        // fping $ip
        builder.append("fping ").append(this.ip).append("\r\n");
        // fping -c 1 $ip
        builder.append("fping -c 1 ").append(this.ip);
        return builder.toString();
    }

    @Override
    public String getName() {
        return name;
    }

}