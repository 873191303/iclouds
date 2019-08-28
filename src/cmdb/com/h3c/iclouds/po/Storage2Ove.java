package com.h3c.iclouds.po;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by yKF7317 on 2017/2/23.
 */
@ApiModel(value = "资源配置存储容量表", description = "资源配置存储容量表")
public class Storage2Ove implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "存储或集群id")
    private String id;

    @ApiModelProperty(value = "存储或集群名称")
    @Length(max = 100)
    @NotNull
    private String name;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "总容量")
    private Float totalCapa;

    @ApiModelProperty(value = "已分配容量")
    private Float allocationCapa;

    @ApiModelProperty(value = "已使用容量")
    private Float usedCapa;

    @ApiModelProperty(value = "剩余容量")
    private Float freeCapa;

    @ApiModelProperty(value = "容量使用率")
    private Float capaUsage;

    @ApiModelProperty(value = "容量超配率")
    private Float capaOverflow;

    @ApiModelProperty(value = "年")
    @Length(max = 4)
    private String year;

    @ApiModelProperty(value = "月")
    @Length(max = 2)
    private String month;

    @ApiModelProperty(value = "日")
    @Length(max = 2)
    private String day;

    @ApiModelProperty(value = "日期")
    private Date date;

    @ApiModelProperty(value = "hpId")
    @Length(max = 36)
    private String hpId;

    @ApiModelProperty(value = "belongId")
    private String belongId;

    public Storage2Ove() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Float getTotalCapa() {
        return totalCapa;
    }

    public void setTotalCapa(Float totalCapa) {
        this.totalCapa = totalCapa;
    }

    public Float getAllocationCapa() {
        return allocationCapa;
    }

    public void setAllocationCapa(Float allocationCapa) {
        this.allocationCapa = allocationCapa;
    }

    public Float getUsedCapa() {
        return usedCapa;
    }

    public void setUsedCapa(Float usedCapa) {
        this.usedCapa = usedCapa;
    }

    public Float getFreeCapa() {
        return freeCapa;
    }

    public void setFreeCapa(Float freeCapa) {
        this.freeCapa = freeCapa;
    }

    public Float getCapaUsage() {
        return capaUsage;
    }

    public void setCapaUsage(Float capaUsage) {
        this.capaUsage = capaUsage;
    }

    public Float getCapaOverflow() {
        return capaOverflow;
    }

    public void setCapaOverflow(Float capaOverflow) {
        this.capaOverflow = capaOverflow;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getHpId() {
        return hpId;
    }

    public void setHpId(String hpId) {
        this.hpId = hpId;
    }

    public String getBelongId() {
        return belongId;
    }

    public void setBelongId(String belongId) {
        this.belongId = belongId;
    }
}
