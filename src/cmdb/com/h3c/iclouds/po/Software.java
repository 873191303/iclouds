package com.h3c.iclouds.po;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiModelProperty;

@Api(value = "软件", description = "软件")
public class Software extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private String id;

	@Length(max = 36)
	@NotNull
	@ApiModelProperty(value = "软件编号 (NotNull)")
	private String scode;

	@Length(max = 50)
	@ApiModelProperty(value = "软件简称")
	private String shortName;

	@Length(max = 200)
	@NotNull
	@ApiModelProperty(value = "软件名称 （NotNull）")
	private String sname;

	@Length(max = 50)
	@ApiModelProperty(value = "软件版本")
	private String softwareVersion;

	@CheckPattern(type = PatternType.CONTAINS, values = {"1", "2", "3"})
	@NotNull
	@ApiModelProperty(value = "状态 (NotNull) (Contains 1：草稿，2：使用中，3：停用)")
	private String status;

	@Length(max = 50)
	@ApiModelProperty(value = "资产管理员")
	private String sowner;

	@Length(max = 50)
	@ApiModelProperty(value = "介质物理位置")
	private String position;

	@NotNull
	@ApiModelProperty(value = "总采购License个数 (NotNull)")
	private Integer totalAuth;

	@ApiModelProperty(value = "总使用License个数")
	private Integer totalUser;

	@ApiModelProperty(value = "发布时间")
	private Date releaseDate;

	@Length(max = 50)
//	@NotNull
	@ApiModelProperty(value = "软件类型 (NotNull)")
	private String stype;

	@Length(max = 500)
	@ApiModelProperty(value = "软件供应商信息")
	private String supId;

	@Length(max = 500)
	@ApiModelProperty(value = "补丁信息")
	private String patch;

	@Length(max = 500)
	@ApiModelProperty(value = "备注")
	private String remark;
	
	@ApiModelProperty(value = "软件类型名称")
	private String stypeName;
	
	public Software() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getScode() {
		return scode;
	}

	public void setScode(String scode) {
		this.scode = scode;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}

	public String getSoftwareVersion() {
		return softwareVersion;
	}

	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSowner() {
		return sowner;
	}

	public void setSowner(String sowner) {
		this.sowner = sowner;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Integer getTotalAuth() {
		return totalAuth;
	}

	public void setTotalAuth(Integer totalAuth) {
		this.totalAuth = totalAuth;
	}

	public Integer getTotalUser() {
		return totalUser;
	}

	public void setTotalUser(Integer totalUser) {
		this.totalUser = totalUser;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getStype() {
		return stype;
	}

	public void setStype(String stype) {
		this.stype = stype;
	}

	public String getSupId() {
		return supId;
	}

	public void setSupId(String supId) {
		this.supId = supId;
	}

	public String getPatch() {
		return patch;
	}

	public void setPatch(String patch) {
		this.patch = patch;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public String getStypeName () {
		return stypeName;
	}
	
	public void setStypeName (String stypeName) {
		this.stypeName = stypeName;
	}
}
