package com.h3c.iclouds.utils;

public class DbContextHolder {
	private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();
	private static final ThreadLocal<String> dbHolder = new ThreadLocal<String>();

	public static void setDbType(String dbType, String dbname) {
		contextHolder.set(dbType);
		dbHolder.set(dbname);
	}

	public static void setDbType(String dbType) {
		if(!"cbmsSource".equals(dbType)) {
			dbType = "dataSource";
		}
		String currentDB = contextHolder.get();
		if(!dbType.equals(currentDB)) {
			System.out.println("change db source : " + dbType);
			contextHolder.set(dbType);
		}
	}
	
	public static String getDbType() {
		String str = (String) contextHolder.get();
		if (null == str || "".equals(str))
			str = "dataSource";
		return str;
	}

	public static String getDbName() {
		String str = (String) dbHolder.get();
		if (null == str || "".equals(str))
			str = "mysql";
		return str;
	}

	public static void clearDbType() {
		contextHolder.remove();
		dbHolder.remove();
	}
}
