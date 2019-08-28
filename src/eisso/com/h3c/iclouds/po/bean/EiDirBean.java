package com.h3c.iclouds.po.bean;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.client.EisooParams;

import java.io.Serializable;

/**
 * 目录协议
 * Created by yKF7317 on 2017/5/23.
 */
public class EiDirBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;//目录名称
	
	private String docid;//父级目录gns路径
	
	private String path;//多级目录名称
	
	private String ondup = CacheSingleton.getInstance().getEisooApi(EisooParams.EISOO_ONDUP);//检查重名
	
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
	
	public String getPath () {
		return path;
	}
	
	public void setPath (String path) {
		this.path = path;
	}
	
	public String getOndup () {
		return ondup;
	}
	
	public void setOndup (String ondup) {
		this.ondup = ondup;
	}
}
