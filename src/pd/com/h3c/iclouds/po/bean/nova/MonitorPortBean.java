package com.h3c.iclouds.po.bean.nova;

import java.io.Serializable;

/**
 * Created by zkf5485 on 2017/6/2.
 */
public class MonitorPortBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String portId;

    private String type;

    private String form;

    private String ipAddr;

    private String macAddr;

    private String saveFlag;

    private String joinFlag;

    public String getPortId() {
        return portId;
    }

    public void setPortId(String portId) {
        this.portId = portId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public String getSaveFlag() {
        return saveFlag;
    }

    public void setSaveFlag(String saveFlag) {
        this.saveFlag = saveFlag;
    }

    public String getJoinFlag() {
        return joinFlag;
    }

    public void setJoinFlag(String joinFlag) {
        this.joinFlag = joinFlag;
    }
}
