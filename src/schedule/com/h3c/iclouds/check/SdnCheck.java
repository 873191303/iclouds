package com.h3c.iclouds.check;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.po.HealthyInstance;

/**
 * Created by zkf5485 on 2017/9/12.
 */
public class SdnCheck extends BaseCheck {

    public static final String name = "3";

    public SdnCheck(JSONObject params, HealthyInstance entity) {
        super(params, entity);
    }

    @Override
    public String getName() {
        return name;
    }

}