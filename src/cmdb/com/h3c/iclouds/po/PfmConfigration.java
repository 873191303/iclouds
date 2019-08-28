package com.h3c.iclouds.po;

import java.io.Serializable;

import com.wordnik.swagger.annotations.ApiModel;

/**
 * Created by yKF7317 on 2017/3/1.
 */
@ApiModel(value = "云资源性能数据参数表")
public class PfmConfigration implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

    private String param;

    private Float paramValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Float getParamValue() {
        return paramValue;
    }

    public void setParamValue(Float paramValue) {
        this.paramValue = paramValue;
    }
}
