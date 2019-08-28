package com.h3c.iclouds.po.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3636552845841816623L;
	
	@JsonIgnore
	private boolean isValid;
	
	@JsonIgnore
	public boolean isValid() {
		if (validatorMap == null) {
			return true;
		}
		return validatorMap.isEmpty();
	}
	
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	
	@JsonIgnore
	protected Map<String, String> validatorMap;
	
	public abstract Map<String,String> getValidatorMap();
		
	
	public void setValidatorMap(Map<String, String> validatorMap) {
		this.validatorMap = validatorMap;
	}

	public void addValidMap(Map<String, String> map) {
		if (validatorMap == null) {
			validatorMap = new HashMap<String, String>();
		}
		if (!map.isEmpty()) {
			for (String key : map.keySet()) {
				map.put(key, map.get(key));
			}
		}

	}
}
