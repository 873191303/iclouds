package com.h3c.iclouds.check;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.po.HealthyInstance;

/**
 * Created by zkf5485 on 2017/9/12.
 */
public class OpenStackCheck extends BaseCheck {

    public static final String name = "2";

    public OpenStackCheck(JSONObject params, HealthyInstance entity) {
        super(params, entity);
    }

    @Override
    public String getName() {
        return name;
    }
}
