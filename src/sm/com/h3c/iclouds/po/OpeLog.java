package com.h3c.iclouds.po;

import java.io.Serializable;
import java.util.Date;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;

public class OpeLog extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String logTypeId;
	
	private String opeUserId;
	
	private String opeUserName;
	
	private String opeLoginName;
	
	private Date opeStartTime;
	
	private Date opeEndTime;
	
	private String result = ConfigProperty.SYSTEM_FLAG;
	
	private String opeUrl;
	
	private String opeParams;
	
	private String opeIp;
	
	private String remark;
	
	public OpeLog() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOpeUserId() {
		return opeUserId;
	}

	public void setOpeUserId(String opeUserId) {
		this.opeUserId = opeUserId == null ? ConfigProperty.SYSTEM_FLAG : opeUserId;
	}

	public String getOpeUserName() {
		return opeUserName;
	}

	public void setOpeUserName(String opeUserName) {
		this.opeUserName = opeUserName == null ? ConfigProperty.SYSTEM_FLAG : opeUserName;
	}

	public String getOpeLoginName() {
		return opeLoginName;
	}

	public void setOpeLoginName(String opeLoginName) {
		this.opeLoginName = opeLoginName == null ? ConfigProperty.SYSTEM_FLAG : opeLoginName;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getOpeIp() {
		return opeIp;
	}

	public void setOpeIp(String opeIp) {
		this.opeIp = opeIp;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getOpeStartTime() {
		return opeStartTime;
	}

	public void setOpeStartTime(Date opeStartTime) {
		this.opeStartTime = opeStartTime;
	}

	public Date getOpeEndTime() {
		return opeEndTime;
	}

	public void setOpeEndTime(Date opeEndTime) {
		this.opeEndTime = opeEndTime;
	}

	public String getLogTypeId() {
		return logTypeId;
	}

	public void setLogTypeId(String logTypeId) {
		this.logTypeId = logTypeId;
	}

	public String getOpeUrl() {
		return opeUrl;
	}

	public void setOpeUrl(String opeUrl) {
		this.opeUrl = opeUrl;
	}

	public String getOpeParams() {
		return opeParams;
	}

	public void setOpeParams(String opeParams) {
		this.opeParams = opeParams;
	}

}
