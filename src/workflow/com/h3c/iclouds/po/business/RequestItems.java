package com.h3c.iclouds.po.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@ApiModel(value = "业务办理条目对象")
public class RequestItems extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "条目id")
	private String id;

	@Length(max = 36)
	@ApiModelProperty(value = "申请单id")
	private String reqId;

	@Length(max = 36)
	@NotNull
	@ApiModelProperty(value = "类型id")
	private String classId;

	@ApiModelProperty(value = "条目类型 (NotNull)(Contain: 1-新增; 2-修改; 3-删除; 4-不变;)")
	@NotNull
	@CheckPattern(type = PatternType.CONTAINS, values = {"1", "2", "3", "4"})
	private String reqType;

	@Length(max = 1000)
	@NotNull
	@ApiModelProperty(value = "条目明细 (NotNull)")
	private String ajson;

	@Length(max = 36)
	@ApiModelProperty(value = "原条目id，变更时填写")
	private String oitemId;

	@CheckPattern(type = PatternType.CONTAINS, values = {"1", "2"})
	@ApiModelProperty(value = "状态，不需要传递", notes = "1:已开通, 2:已回收")
	private String status;
	
	@ApiModelProperty(value = "套数")
	@NotNull
	private Integer nums;
	
	@ApiModelProperty(value = "要求交付时间")
	@NotNull
	private Date reqTime;
	
	@ApiModelProperty(value = "原条目数量")
	private Integer onums;
	
	@ApiModelProperty(value = "原条目明细")
	private String oajson;
	
	private Integer seq;
	
	private RequestMaster master;

	public RequestItems() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getReqType() {
		return reqType;
	}

	public void setReqType(String reqType) {
		this.reqType = reqType;
	}

	public String getAjson() {
		return ajson;
	}

	public void setAjson(String ajson) {
		this.ajson = ajson;
	}

	public String getOitemId() {
		return oitemId;
	}

	public void setOitemId(String oitemId) {
		this.oitemId = oitemId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@JsonIgnore
	public RequestMaster getMaster() {
		return master;
	}

	public void setMaster(RequestMaster master) {
		this.master = master;
	}

	public Integer getNums() {
		return nums;
	}

	public void setNums(Integer nums) {
		this.nums = nums;
	}

	@JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
	public Date getReqTime() {
		return reqTime;
	}

	public void setReqTime(Date reqTime) {
		this.reqTime = reqTime;
	}

	public Integer getOnums() {
		return onums;
	}

	public void setOnums(Integer onums) {
		this.onums = onums;
	}

	public String getOajson() {
		return oajson;
	}

	public void setOajson(String oajson) {
		this.oajson = oajson;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
}
