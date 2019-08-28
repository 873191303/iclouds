package com.h3c.iclouds.po.bean.cloudos;

import java.io.Serializable;
import java.util.Map;

public class Storage implements Serializable{

	
	private static final long serialVersionUID = 1034283934968666525L;
	private Map<String, String> quota_set;
	public Map<String, String> getQuota_set() {
		return quota_set;
	}
	public void setQuota_set(Map<String, String> quota_set) {
		this.quota_set = quota_set;
	}
}
