package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

/**
 * Created by zkf5485 on 2017/9/8.
 */
public class HealthyType2Item extends BaseEntity implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;

    private String itemName;

    private String type;

    private Double weight;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
