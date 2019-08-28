package com.h3c.iclouds.po;

import com.wordnik.swagger.annotations.ApiModel;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/3/1.
 */
@ApiModel(value = "云资源性能指标")
public class CasItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String item;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
