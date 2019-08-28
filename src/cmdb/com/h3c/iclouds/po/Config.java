package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseEntity;

@SuppressWarnings({"rawtypes", "unused"})
public class Config {

	private BaseDAO dao;
	
	private Class clazz;
	
	public Config(BaseDAO dao, Class clazz) {
		this.dao = dao;
		this.clazz = clazz;
	}
	
	public Class getClazz() {
		return clazz;
	}

	public BaseDAO getDao() {
		return dao;
	}

}
