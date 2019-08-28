package com.h3c.iclouds.validate;

public enum PatternType {
	IP,
	CONTAINS,
	UNCOPY,
	LENGTH,
	NOTEQUAL,
	/**
	 * 资源管理中类别
	 */
	SYSTYPE,
	
	/**
	 * null也拷贝，一般用于外键
	 */
	FK,
}
