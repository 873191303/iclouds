package com.h3c.iclouds.po;

import java.util.ArrayList;
import java.util.List;

public class BackupNodeGroup {
	
	private String Message;
	
	private String Code;
	
	List<Backup2Nodes> Data = new ArrayList<Backup2Nodes>();
	
	public BackupNodeGroup() {
		
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public List<Backup2Nodes> getData() {
		return Data;
	}

	public void setData(List<Backup2Nodes> data) {
		Data = data;
	}
	
}
