package com.h3c.iclouds.po;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "数据中心IP使用信息表", description = "数据中心IP使用信息表")
public class IpRelation extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private String id;

	@CheckPattern(type = PatternType.IP)
	@NotNull
	@ApiModelProperty(value = "IP地址 (NotNull)")
	private String ip;

	@Length(max = 50)
	@ApiModelProperty(value = "寄宿资源id(即虚拟机id)")
	private String assetId;

	@Length(max = 36)
	@ApiModelProperty(value = "资源类型")
	private String classId;

	@Length(max = 36)
	@ApiModelProperty(value = "网口id")
	private String ncid;

	//@Max(16)
	@ApiModelProperty(value = "是否管理IP")
	private Integer isIlop;

	@ApiModelProperty(value = "网口序号")
	private Integer asmPortSeq;

	@ApiModelProperty(value = "网口备注")
	private Integer asmPortRemark;
	
	public IpRelation() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getNcid() {
		return ncid;
	}

	public void setNcid(String ncid) {
		this.ncid = ncid;
	}

	public Integer getIsIlop() {
		return isIlop;
	}

	public void setIsIlop(Integer isIlop) {
		this.isIlop = isIlop;
	}

	public Integer getAsmPortSeq() {
		return asmPortSeq;
	}

	public void setAsmPortSeq(Integer asmPortSeq) {
		this.asmPortSeq = asmPortSeq;
	}

	public Integer getAsmPortRemark() {
		return asmPortRemark;
	}

	public void setAsmPortRemark(Integer asmPortRemark) {
		this.asmPortRemark = asmPortRemark;
	}

}
