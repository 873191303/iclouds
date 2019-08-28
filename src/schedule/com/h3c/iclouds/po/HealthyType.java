package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

/**
 * Created by zkf5485 on 2017/9/8.
 */
public class HealthyType extends BaseEntity implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String healthName;
    private String config;
    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getHealthName() {
        return healthName;
    }

    public void setHealthName(String healthName) {
        this.healthName = healthName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
