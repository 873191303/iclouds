package com.h3c.iclouds.po;

import java.util.ArrayList;
import java.util.List;

public class BackupTaskGroup {
	
	private String TaskCount;
	
	private List<BackupNode2Tasks> Tasks = new ArrayList<BackupNode2Tasks>();
	
	public BackupTaskGroup() {
		
	}

	public String getTaskCount() {
		return TaskCount;
	}

	public void setTaskCount(String taskCount) {
		TaskCount = taskCount;
	}

	public List<BackupNode2Tasks> getTasks() {
		return Tasks;
	}

	public void setTasks(List<BackupNode2Tasks> tasks) {
		Tasks = tasks;
	}
	
}
