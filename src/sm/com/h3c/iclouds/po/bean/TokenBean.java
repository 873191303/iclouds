package com.h3c.iclouds.po.bean;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class TokenBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@NotNull
	@ApiModelProperty(value = "用户名")
	private String loginName;
	
	@NotNull
	@ApiModelProperty(value = "密码")
	private String password;
	
	@NotNull
	@Length(min = 4, max = 4)
	@ApiModelProperty(value = "验证码")
	private String code;
	
	@NotNull
	@ApiModelProperty(value = "验证码token")
	private String codeToken;
	
	@CheckPattern(type = PatternType.CONTAINS, values = {ConfigProperty.SYS_FLAG_YUNWEI, ConfigProperty.SYS_FLAG_YUNYING})
	@NotNull
	@ApiModelProperty(value = "登录系统类别")
	private String sysFlag;

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodeToken() {
		return codeToken;
	}

	public void setCodeToken(String codeToken) {
		this.codeToken = codeToken;
	}

	public String getSysFlag() {
		return sysFlag;
	}

	public void setSysFlag(String sysFlag) {
		this.sysFlag = sysFlag;
	}
	
}
