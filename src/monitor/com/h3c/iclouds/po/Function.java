package com.h3c.iclouds.po;

import java.io.Serializable;
import com.h3c.iclouds.utils.InvokeSetForm;
import java.util.Map;

/**
 * Created by zkf5485 on 2017/6/28.
 */
public class Function implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Integer functionId;

    private Integer itemId;

    private Integer triggerId;

    private String function;

    private String parameter;

    public static Function create(Map<String, Object> map) {
        Function entity = new Function();
        InvokeSetForm.settingForm(map, entity);
        return entity;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public Integer getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Integer functionId) {
        this.functionId = functionId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public Integer getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(Integer triggerId) {
        this.triggerId = triggerId;
    }
}
