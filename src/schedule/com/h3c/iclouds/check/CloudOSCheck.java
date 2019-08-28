package com.h3c.iclouds.check;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.po.HealthyInstance;

/**
 * Cloudos可用性检测
 * Created by zkf5485 on 2017/8/29.
 */
public class CloudOSCheck extends BaseCheck {

    public static final String name = "1";

    public CloudOSCheck(JSONObject params, HealthyInstance entity) {
        super(params, entity);
    }

    @Override
    public Double check() {
        String result = this.excuteScript();
        if(result == null) {    // 未能获取到结果
            return 0.;
        }

        Double ping;
        Double cpu;
        Double telnet;
        try {
            String[] array = result.split("\\n");
            ping = this.ping(array[0], .5);
            try {
                cpu = this.cpu(Double.valueOf(array[2]));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                cpu = 0.;
            }
            telnet = this.telnet(array[1], .4);
            if(ping == null || telnet == null) {    //  ping 或 telnet为nul则强制为0
                return 0.;
            }
        } catch (Exception e) {
            this.exception(e, "计算健康度失败, 实例id: " + this.entity.getId());
            return 0.;
        }
        return ping + cpu + telnet;
    }

    @Override
    public String getCommand() {
        // 不存在则创建
        StringBuilder builder = new StringBuilder();
        // fping $ip
        builder.append("fping ").append(this.ip).append(";");
        // zabbix_get -s 127.0.0.1 -k net.tcp.port[$ip,$port]
        builder.append("zabbix_get -s 127.0.0.1 -k net.tcp.port[").append(this.ip).append(",").append(this.port).append("]").append(";");
        // zabbix_get -s $ip -k system.cpu.util[]
        builder.append("zabbix_get -s ").append(this.ip).append(" -k system.cpu.util[]");
        return builder.toString();
    }

    @Override
    public String getName() {
        return name;
    }
}
