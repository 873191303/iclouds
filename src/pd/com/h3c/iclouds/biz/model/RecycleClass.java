package com.h3c.iclouds.biz.model;

public class RecycleClass {
	private int level=0;
	public RecycleClass(String classId) {
		this.level=Integer.parseInt(classId);
	}
	public boolean above(RecycleClass recycleClass) {
		if (this.level>=recycleClass.level) {
			return true;
		}
		return false;
	}
	
}
