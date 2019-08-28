package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.util.Date;

/**
 * iyun_netflow_originals
 * Created by zkf5485 on 2017/9/4.
 */
public class Originals2Day extends BaseEntity implements java.io.Serializable {

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

    public static final Originals2Day create(String tenantId, String type, Date date, long value) {
        Originals2Day entity = new Originals2Day();

        entity.setType(type);

        entity.setUuid(tenantId);
        entity.setVassetId(tenantId);
        entity.setTenantId(tenantId);

        entity.setCollectTime(date);
        entity.setCreatedDate(date);
        entity.setUpdatedDate(date);

        entity.setValue(value);
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
}
