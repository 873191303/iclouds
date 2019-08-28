package com.h3c.iclouds.check;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.po.HealthyInstance;

/**
 * Created by zkf5485 on 2017/9/13.
 */
public class IpSanCheck extends BaseCheck {

    public static final String name = "5";

    public IpSanCheck(JSONObject params, HealthyInstance entity) {
        super(params, entity);
    }

    @Override
    public Double check() {
        String result = this.excuteScript();
        if (result == null) {    // 未能获取到结果
            return 0.;
        }
        Double ping;
        try {
            String[] array = result.split("\\n");
            ping = this.ping(array[0], 1);
            if (ping == null) {    //  ping 或 telnet为nul则强制为0
                return 0.;
            }
        } catch (Exception e) {
            this.exception(e, "计算健康度失败, 实例id: " + this.entity.getId());
            return 0.;
        }
        return ping;
    }

    @Override
    public String getCommand() {
        // 不存在则创建
        StringBuilder builder = new StringBuilder();
        // fping $ip
        builder.append("fping ").append(this.ip).append(";");
        return builder.toString();
    }

    @Override
    public String getName() {
        return name;
    }
}
