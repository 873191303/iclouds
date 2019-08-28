package com.h3c.iclouds.po;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/12/19.
 */
public class Sub2Dns implements Serializable {

    private static final long serialVersionUID = 1L;

    private String address;

    private String subnetId;

    public Sub2Dns() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSubnetId() {
        return subnetId;
    }

    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }
}
