package com.h3c.iclouds.po;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseEntity;

import java.util.Date;

/**
 * iyun_netflow_originals
 * Created by zkf5485 on 2017/9/4.
 */
public class Originals extends BaseEntity implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;

    private Date collectTime;

    private String vassetId;

    private Long value;

    private String type;

    private String uuid;

    private String tenantId;
    
    private Long currentTotal;

    public Originals() {

    }

    public Originals(Long value, String type, Date collectTime) {
        this.value = value;
        this.type = type;
        this.collectTime = collectTime;
    }

    public static final Originals create(String tenantId, String type, JSONObject jsonObject) {
        Originals entity = new Originals();

        entity.setType(type);

        entity.setUuid(tenantId);
        entity.setVassetId(tenantId);
        entity.setTenantId(tenantId);

        String clock = jsonObject.getString("clock") + "000";
        Date collectTime = new Date(Long.valueOf(clock));
        entity.setCollectTime(collectTime);
        entity.setCreatedDate(collectTime);
        entity.setUpdatedDate(collectTime);

        long value = jsonObject.getLongValue("value");
        entity.setValue(value);
        return entity;
    }
    
    public static Originals create(Vdevice vdevice, long value, long lastValue, String type) {
        Originals entity = new Originals();
        entity.setType(type);
        entity.setUuid(vdevice.getId());
        entity.setVassetId(vdevice.getId());
        entity.setTenantId(vdevice.getTenant());
        entity.setCollectTime(new Date());
        entity.setCreatedDate(new Date());
        entity.setUpdatedDate(new Date());
        entity.setValue(value > lastValue ? (value - lastValue) : value); //value为差值,如果当前值不大于上次值则重新计算
        entity.setCurrentTotal(value);
        return entity;
    }
    
    public Date getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Date collectTime) {
        this.collectTime = collectTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getVassetId() {
        return vassetId;
    }

    public void setVassetId(String vassetId) {
        this.vassetId = vassetId;
    }
    
    public Long getCurrentTotal () {
        return currentTotal;
    }
    
    public void setCurrentTotal (Long currentTotal) {
        this.currentTotal = currentTotal;
    }
}
