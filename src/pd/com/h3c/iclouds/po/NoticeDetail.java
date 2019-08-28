package com.h3c.iclouds.po;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
* @author  zKF7420
* @date 2017年1月12日 上午9:20:12
*/
public class NoticeDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "通知公告详情ID")
	 @Length(max = 36)
	 private String id;
	 
	 @ApiModelProperty(value = "通知公告id")
	 @Length(max = 36)
	 private String noticeId;
	 
	 @ApiModelProperty(value = "详情")
	 @Length(max = 1024)
	 private String detail;
	 
	 public NoticeDetail() {
		 
	 }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
	 
}
