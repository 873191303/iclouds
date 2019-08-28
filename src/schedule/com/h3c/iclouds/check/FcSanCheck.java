package com.h3c.iclouds.check;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.po.HealthyInstance;
import com.h3c.iclouds.po.HealthyType2Item;
import com.h3c.iclouds.utils.Ssh2Utils;

import java.util.List;

/**
 * Created by zkf5485 on 2017/9/13.
 */
public class FcSanCheck extends BaseCheck {

    public static final String name = "6";

    public FcSanCheck(JSONObject params, HealthyInstance entity) {
        super(params, entity);
    }

    @Override
    public Double check() {
        Ssh2Utils ssh = null;
        Double resultValue = 0.;
        try {
            ssh = Ssh2Utils.create(this.ip, "root", singleton.getConfigValue("iyun.cas.host.password"));
            if(!ssh.isAuthenticated()) {
                this.warn("SSH 无法连接到" + this.ip + ", 计算健康度失败");
                return 0.;
            }
            List<String> list = ssh.execCmd("iostat -x");
            int index = -1;
            Double value = null;
            for (String s : list) {
                if(s.trim().length() == 0) {
                    continue;
                }
                // 多空格转为单空格
                s = s.replaceAll("\\s+", " ");
                if(index != -1) {
                    String[] array = s.split(" ");
                    if(value == null) {
                        value = 0.;
                    }
                    value += Double.valueOf(array[index]);
                } else if(s.contains("Device")) {
                    String[] array = s.split(" ");
                    for (int i = 0; i < array.length; i++) {
                        if("await".equals(array[i])) {
                            index = i;
                            break;
                        }
                    }
                }
            }
            HealthyType2Item type2Item = this.type2ItemMap.get("delay");
            double weight = type2Item == null ? 1. : type2Item.getWeight();
            String itemId = type2Item == null ? null : type2Item.getId();
            if(value == null || value > 100.) {
                resultValue = 0.;
            } else if(value >= 31) {
                resultValue = 0.6;
            } else {
                resultValue = 1.;
            }
            this.saveOrigin(resultValue, weight, itemId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(ssh != null) {
                ssh.close();
            }
        }
        return resultValue;
    }

    @Override
    public String getName() {
        return name;
    }
}
