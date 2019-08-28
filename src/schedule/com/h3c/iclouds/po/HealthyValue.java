package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.util.Date;

/**
 * Created by zkf5485 on 2017/9/8.
 */
public class HealthyValue extends BaseEntity implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    private String id;

    private String instanceId;

    private Date collectTime;

    private Double healthValue;

    public Date getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Date collectTime) {
        this.collectTime = collectTime;
    }

    public Double getHealthValue() {
        return healthValue;
    }

    public void setHealthValue(Double healthValue) {
        this.healthValue = healthValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
