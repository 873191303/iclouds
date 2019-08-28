package com.h3c.iclouds.po.bean;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.client.EisooParams;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/5/2.
 */
public class EiFileBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;//文件名称
	
	private String docid;//文件gns路径
	
	private String ondup = CacheSingleton.getInstance().getEisooApi(EisooParams.EISOO_ONDUP);//重名规则
	
	public EiFileBean (String name, String docid) {
		this.name = name;
		this.docid = docid;
	}
	
	public EiFileBean (String docid) {
		this.docid = docid;
	}
	
	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public String getDocid () {
		return docid;
	}
	
	public void setDocid (String docid) {
		this.docid = docid;
	}
	
	public String getOndup () {
		return ondup;
	}
	
	public void setOndup (String ondup) {
		this.ondup = ondup;
	}
}
