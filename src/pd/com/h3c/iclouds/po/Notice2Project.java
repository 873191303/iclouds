package com.h3c.iclouds.po;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
* @author  zKF7420
* @date 2017年1月19日 下午9:20:02
*/
public class Notice2Project extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@Length(max = 36)
	private String id;
	
	@ApiModelProperty(value = "租户id")
	@Length(max = 36)
	private String tenantId;
	
	@ApiModelProperty(value = "公告id")
	@Length(max = 36)
	private String noticeId;
	
	@ApiModelProperty(value = "消息是否已读")
	private Boolean isRead;
	
	public Notice2Project() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	public String getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}
	
	
}
