package com.h3c.iclouds.auth;

public enum ThirdPartEnum {

	EISOO("爱数"),	// 爱数使用

	;

	private String opeValue;

	private ThirdPartEnum(String opeValue) {
		this.opeValue = opeValue;
	}

	public String getOpeValue() {
		return opeValue;
	}

}
