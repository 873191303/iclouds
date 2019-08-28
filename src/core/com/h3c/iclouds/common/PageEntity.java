package com.h3c.iclouds.common;

import java.util.Map;
import java.util.Set;

public class PageEntity {
	
	private String searchValue;
	
	private String asSorting;
	
	private String columnName;
	
	private String sEcho;
	
	private int pageNo;
	
	private int pageSize;
	
	private Set<String> groupSet;
	
	private Map<String, Object> queryMap;
	
	private String[] specialParams;
	
	private String specialParam;
	
	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public String getAsSorting() {
		return asSorting;
	}

	public void setAsSorting(String asSorting) {
		this.asSorting = asSorting;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getsEcho() {
		return sEcho;
	}

	public void setsEcho(String sEcho) {
		this.sEcho = sEcho;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Map<String, Object> getQueryMap() {
		return queryMap;
	}

	public void setQueryMap(Map<String, Object> queryMap) {
		this.queryMap = queryMap;
	}

	public Set<String> getGroupSet() {
		return groupSet;
	}

	public void setGroupSet(Set<String> groupSet) {
		this.groupSet = groupSet;
	}

	public String[] getSpecialParams() {
		return specialParams;
	}

	public void setSpecialParams(String[] specialParams) {
		this.specialParams = specialParams;
	}

	public String getSpecialParam() {
		return specialParam;
	}

	public void setSpecialParam(String specialParam) {
		this.specialParam = specialParam;
	}

}
