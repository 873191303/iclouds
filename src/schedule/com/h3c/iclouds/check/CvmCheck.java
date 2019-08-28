package com.h3c.iclouds.check;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.po.HealthyInstance;
import com.h3c.iclouds.po.HealthyType2Item;

/**
 * cvm可用性检测
 * Created by zkf5485 on 2017/8/29.
 */
public class CvmCheck extends BaseCheck {

    public static final String name = "4";

    public CvmCheck(JSONObject params, HealthyInstance entity) {
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
        Double memory;
        Double storage;
        try {
            String[] array = result.split("\\n");
            ping = this.ping(array[0], .3);
            telnet = this.telnet(array[1], .3);
            try {
                cpu = this.cpu(Double.valueOf(array[2]));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                cpu = 0.;
            }
            try {
                memory = this.memory(Double.valueOf(array[3]));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                memory = null;
            }
            try {
                storage = this.storage(Double.valueOf(array[4]));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                storage = null;
            }
            if(ping == null || telnet == null || memory == null || storage == null) {    //  为nul则强制为0
                return 0.;
            }
        } catch (Exception e) {
            this.exception(e, "计算健康度失败, 实例id: " + this.entity.getId());
            return 0.;
        }
        return ping + cpu + telnet + memory + storage;
    }

    /**
     * cpu使用率得分
     * 81-100   ~   0.6
     * 61-80    ~   0.8
     * 0-60     ~   1
     * @param value
     * @return
     */
    Double storage(Double value) {
        HealthyType2Item type2Item = this.type2ItemMap.get("storage");
        double weight = type2Item == null ? .2 : type2Item.getWeight();
        String itemId = type2Item == null ? null : type2Item.getId();

        Double calc;
        if(value == 100.) {
            this.saveOrigin(0., weight, itemId);
            return null;    // 不正常则返回空
        } else if(value >= 80.) {
            calc = 0.4;
        } else {
            calc = 1.;
        }

        this.saveOrigin(calc, weight, itemId);
        return weight * calc;
    }

    /**
     * cpu使用率得分
     * 81-100   ~   0.6
     * 61-80    ~   0.8
     * 0-60     ~   1
     * @param value
     * @return
     */
    Double memory(Double value) {
        HealthyType2Item type2Item = this.type2ItemMap.get("memory");
        double weight = type2Item == null ? .1 : type2Item.getWeight();
        String itemId = type2Item == null ? null : type2Item.getId();

        value = 100 - value;
        Double calc;
        if(value == 100.) {
            this.saveOrigin(0., weight, itemId);
            return null;    // 不正常则返回空
        } else if(value >= 81.) {
            calc = 0.6;
        } else if(value >= 61.) {
            calc = 0.8;
        } else {
            calc = 1.;
        }

        this.saveOrigin(calc, weight, itemId);
        return weight * calc;
    }

    @Override
    public String getCommand() {
        // 不存在则创建
        StringBuilder builder = new StringBuilder();
        // fping $ip
        builder.append("fping ").append(this.ip).append("\r\n");
        // zabbix_get -s 127.0.0.1 -k net.tcp.port[$ip,$port]
        builder.append("zabbix_get -s 127.0.0.1 -k net.tcp.port[").append(this.ip).append(",").append(this.port).append("]").append(";");
        // zabbix_get -s $ip -k system.cpu.util[]
        builder.append("zabbix_get -s ").append(this.ip).append(" -k system.cpu.util[]").append(";");
        // zabbix_get -s $ip -k custom.memory.pused 内存使用率
        builder.append("zabbix_get -s ").append(this.ip).append(" -k vm.memory.size[pused]").append(";");
        // zabbix_get -s $ip -k vfs.fs.size[/var/log,pused]  磁盘已使用量
        builder.append("zabbix_get -s ").append(this.ip).append(" -k vfs.fs.size[/var/log,pused]");
        return builder.toString();
    }

    @Override
    public String getName() {
        return name;
    }
}
