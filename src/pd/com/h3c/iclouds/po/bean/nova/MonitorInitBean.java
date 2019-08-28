package com.h3c.iclouds.po.bean.nova;

import com.h3c.iclouds.validate.NotNull;

import java.io.Serializable;

/**
 * Created by zkf5485 on 2017/6/1.
 */
public class MonitorInitBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Integer port;

    @NotNull
    private String portId;

    public String getPortId() {
        return portId;
    }

    public void setPortId(String portId) {
        this.portId = portId;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
