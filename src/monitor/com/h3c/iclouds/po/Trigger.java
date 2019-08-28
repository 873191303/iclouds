package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.utils.InvokeSetForm;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by zkf5485 on 2017/6/28.
 */
public class Trigger extends BaseEntity implements Serializable {

    private static final Long serialVersionUID = 1L;

    public static Trigger create(Map<String, Object> map) {
        Trigger entity = new Trigger();
        InvokeSetForm.settingForm(map, entity);
        return entity;
    }

    private Integer triggerId;

    private String expression;

    private String description;

    private String url;

    private Integer status;

    private Integer value;

    private Integer priority;

    private Integer lastChange;

    private Integer comments;

    private String error;

    private Integer templateId;

    private Integer type;

    private Integer state;

    private Integer flags;
    
    private String source;
    
    private String sourcetype;
    
    private String tenantId;

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Integer getFlags() {
        return flags;
    }

    public void setFlags(Integer flags) {
        this.flags = flags;
    }

    public Integer getLastChange() {
        return lastChange;
    }

    public void setLastChange(Integer lastChange) {
        this.lastChange = lastChange;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(Integer triggerId) {
        this.triggerId = triggerId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
    
    public String getSource () {
        return source;
    }
    
    public void setSource (String source) {
        this.source = source;
    }
    
    public String getSourcetype () {
        return sourcetype;
    }
    
    public void setSourcetype (String sourcetype) {
        this.sourcetype = sourcetype;
    }
}
