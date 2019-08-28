package com.h3c.iclouds.client.zabbix;

import com.h3c.iclouds.auth.CacheSingleton;

import java.util.Date;

public class ZabbixBean {

	private ZabbixApi zabbixApi;
	
	private Date zabbixDate = null;
	
	public void setZabbixDate(Date zabbixDate) {
		this.zabbixDate = zabbixDate;
	}

	public long getZabbixDateTime() {
		return zabbixDate.getTime();
	}

	public ZabbixApi getZabbixApi() {
		// 判断是否登录且是否超过12小时
		if(zabbixDate != null && (zabbixDate.getTime() + 1000 * 60 * 60 * 12) > System.currentTimeMillis()) {
			return zabbixApi;
		}
		if(zabbixApi != null) {
			try {
				zabbixApi.logout();	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		zabbixApi = null;	// 释放原本对象
		zabbixDate = null;	// 清除登录时间
		zabbixApi = new ZabbixApi(CacheSingleton.getInstance().getZabbixAppUrl());// 初始化对象
		try {
			zabbixApi.init();	// 初始化参数
			boolean login = zabbixApi.login(
					CacheSingleton.getInstance().getConfigValue("cocenter.zabbix.app.userName"),
					CacheSingleton.getInstance().getConfigValue("cocenter.zabbix.app.password")
			);	// 登录
			if(login) {	// 登录成功
				return zabbixApi;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		zabbixDate = new Date();
		return null;
	}
}
