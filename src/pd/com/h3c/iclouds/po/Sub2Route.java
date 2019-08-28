package com.h3c.iclouds.po;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/12/19.
 */
public class Sub2Route implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nextHop;

    private String destination;

    private String subnetId;

    public String getNextHop() {
        return nextHop;
    }

    public void setNextHop(String nextHop) {
        this.nextHop = nextHop;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSubnetId() {
        return subnetId;
    }

    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }

    public Sub2Route() {
    }
}
