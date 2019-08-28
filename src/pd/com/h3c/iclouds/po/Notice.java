package com.h3c.iclouds.po;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
* @author  zKF7420
* @date 2017年1月11日 上午10:34:02
*/
@ApiModel(value = "通知公告", description = "通知公告")
public class Notice extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	 @ApiModelProperty(value = "通知公告ID")
	 @Length(max = 36)
	 private String id;
	 
	 @NotNull
	 @ApiModelProperty(value = "标题(not null)")
	 @Length(max = 255)
	 private String title;
	 
	 @ApiModelProperty(value = "简要信息")
	 @Length(max = 255)
	 private String brief;
	 
	 @NotNull
	 @ApiModelProperty(value = "状态（1-草稿，2-发布）")
	 @Length(max = 16)
	 private String status;
	 
	 @NotNull
	 @ApiModelProperty(value = "消息等级(not null，1-紧急，2-一般)")
	 @Length(max = 16)
	 private String grade;
	 
	 @ApiModelProperty(value = "租户id")
	 @Length(max = 64)
	 private String tenantId;
	 
	 @NotNull
	 @ApiModelProperty(value = "公告详情")
	 @Length(max = 1024)
	 private String detail;
	 
	 @ApiModelProperty(value = "删除标志位")
	 @Length(max = 32)
	 private String deleted;
	 
	 @NotNull
	 @ApiModelProperty(value = "消息提醒标志位")
	 private Boolean hint;
	 
	 @ApiModelProperty(value = "是否已读(不需要传值)")
	 private Boolean isRead;
	 
	 public Notice() {
		 
	 }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getDeleted() {
		return deleted;
	}

	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}

	public Boolean getHint() {
		return hint;
	}

	public void setHint(Boolean hint) {
		this.hint = hint;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}
	
}
