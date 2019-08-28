package com.h3c.iclouds.biz.model;

public class Request {

	private RecycleClass recycleClass;
	
	public Request(RecycleClass recycleClass) {
		// TODO Auto-generated constructor stub
		this.setRecycleClass(recycleClass);
	}

	public RecycleClass getRecycleClass() {
		return recycleClass;
	}

	public void setRecycleClass(RecycleClass recycleClass) {
		this.recycleClass = recycleClass;
	}
}
