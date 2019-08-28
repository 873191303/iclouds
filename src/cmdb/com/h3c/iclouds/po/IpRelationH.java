package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

@ApiModel(value = "数据中心IP地址使用历史", description = "数据中心IP地址使用历史")
public class IpRelationH extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "日志id")
	private String id;

	@CheckPattern(type = PatternType.IP)
	@NotNull
	@ApiModelProperty(value = "IP地址 (NotNull)")
	private String ip;

	@Length(max = 50)
	@ApiModelProperty(value = "寄宿资源id")
	private String assetId;

	@Length(max = 50)
	@ApiModelProperty(value = "资源类型")
	private String classId;

	@Length(max = 36)
	@ApiModelProperty(value = "网口id")
	private String ncid;

	@Length(max = 32)
	@NotNull
	@ApiModelProperty(value = "操作类型 (NotNull)")
	private String operation;

	public IpRelationH() {

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

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

}
