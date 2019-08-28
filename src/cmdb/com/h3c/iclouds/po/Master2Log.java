package com.h3c.iclouds.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * Created by yKF7317 on 2016/11/4.
 */
@Api(value = "资产管理设备变更日志", description = "资产管理设备变更日志")
public class Master2Log extends BaseEntity{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @Length(max = 36)
    @ApiModelProperty(value = "资产ID")
    private String assetId;

    @Length(max = 100)
    @NotNull
    @ApiModelProperty(value = "变更原因 (NotNull)")
    private String chgCause;

    @Length(max = 100)
    @NotNull
    @ApiModelProperty(value = "更改前属性值 (NotNull)")
    private String fpropValue;

    @Length(max = 100)
    @NotNull
    @ApiModelProperty(value = "更改后属性值 (NotNull)")
    private String apropValue;

    @Length(max = 50)
    @NotNull
    @ApiModelProperty(value = "更改责任人 (NotNull)")
    private String chOwner;

    @ApiModelProperty(value = "更改时间")
    private Date chDate;

    @Length(max = 50)
    @ApiModelProperty(value = "变更号")
    private String chgId;

    @NotNull
    @CheckPattern(type = PatternType.CONTAINS, values = {"0", "1"})
    @ApiModelProperty(value = "确认状态", required = true, notes = "0:已确认 1:未确认")
    private String flag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getFpropValue() {
        return fpropValue;
    }

    public void setFpropValue(String fpropValue) {
        this.fpropValue = fpropValue;
    }

    public String getApropValue() {
        return apropValue;
    }

    public void setApropValue(String apropValue) {
        this.apropValue = apropValue;
    }

    public String getChgCause() {
        return chgCause;
    }

    public void setChgCause(String chgCause) {
        this.chgCause = chgCause;
    }

    public String getChOwner() {
        return chOwner;
    }

    public void setChOwner(String chOwner) {
        this.chOwner = chOwner;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    public Date getChDate() {
        return chDate;
    }

    public void setChDate(Date chDate) {
        this.chDate = chDate;
    }

    public String getChgId() {
        return chgId;
    }

    public void setChgId(String chgId) {
        this.chgId = chgId;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

}
