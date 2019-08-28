package com.h3c.iclouds.quartz;

import com.h3c.iclouds.biz.HealthyInstanceBiz;
import com.h3c.iclouds.check.BaseCheck;
import com.h3c.iclouds.client.zabbix.ZabbixApi;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.po.HealthyInstance;
import com.h3c.iclouds.utils.StrUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zkf5485 on 2017/8/30.
 */
public class HealthyQuartz extends BaseQuartz {

    @Resource
    private HealthyInstanceBiz healthyInstanceBiz;

    @Override
    public void startQuartz() {
        List<HealthyInstance> list = healthyInstanceBiz.findByPropertyName(HealthyInstance.class, "status", ConfigProperty.YES);
        if(StrUtils.checkCollection(list)) {
            ZabbixApi zabbixApi = ZabbixApi.createAdmin();
            list.forEach(entity -> {
                try {
                    BaseCheck check = BaseCheck.createByEntity(entity);
                    if(check != null) {
                        check.setZabbixApi(zabbixApi);
                        Double value = check.check();
                        check.saveHistory(value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
