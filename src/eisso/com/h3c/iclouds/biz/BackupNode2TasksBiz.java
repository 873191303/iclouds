package com.h3c.iclouds.biz;

import java.util.List;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.BackupNode2Tasks;


public interface BackupNode2TasksBiz extends BaseBiz<BackupNode2Tasks> {
	/**
	 * 更新从爱数请求的任务到本地
	 * @param tasks
	 */
	void update(List<BackupNode2Tasks> tasks);
	
}
