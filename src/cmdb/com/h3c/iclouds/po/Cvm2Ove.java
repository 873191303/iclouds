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
@ApiModel(value = "容量管理CVM使用记录", description = "容量管理CVM使用记录")
public class Cvm2Ove implements Serializable {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "cvm名称 (NotNull)")
    @Length(max = 100)
    @NotNull
    private String cvmName;

    @ApiModelProperty(value = "总CPU核")
    private Integer totalCpu;

    @ApiModelProperty(value = "已分配CPU核")
    private Integer assignCpu;

    @ApiModelProperty(value = "cpu使用率")
    private Float cpuUsage;

    @ApiModelProperty(value = "总内存")
    private Integer totalMem;

    @ApiModelProperty(value = "已分配内存")
    private Integer assignMem;

    @ApiModelProperty(value = "内存使用率")
    private Float memUsage;

    @ApiModelProperty(value = "年")
    @Length(max = 4)
    private String year;

    @ApiModelProperty(value = "月")
    @Length(max = 2)
    private String month;

    @ApiModelProperty(value = "日")
    @Length(max = 2)
    private String day;

    @ApiModelProperty(value = "用户")
    @Length(max = 36)
    private String userId;

    @ApiModelProperty(value = "日期")
    private Date date;

    public Cvm2Ove() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCvmName() {
        return cvmName;
    }

    public void setCvmName(String cvmName) {
        this.cvmName = cvmName;
    }

    public Integer getTotalCpu() {
        return totalCpu;
    }

    public void setTotalCpu(Integer totalCpu) {
        this.totalCpu = totalCpu;
    }

    public Integer getAssignCpu() {
        return assignCpu;
    }

    public void setAssignCpu(Integer assignCpu) {
        this.assignCpu = assignCpu;
    }

    public Integer getTotalMem() {
        return totalMem;
    }

    public void setTotalMem(Integer totalMem) {
        this.totalMem = totalMem;
    }

    public Integer getAssignMem() {
        return assignMem;
    }

    public void setAssignMem(Integer assignMem) {
        this.assignMem = assignMem;
    }

    public Float getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Float cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Float getMemUsage() {
        return memUsage;
    }

    public void setMemUsage(Float memUsage) {
        this.memUsage = memUsage;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
