package com.h3c.iclouds.quartz;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseLogs;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.utils.ThreadContext;

public abstract class BaseQuartz extends BaseLogs {

	protected CacheSingleton singleton = CacheSingleton.getInstance();
	
	public void task() {
		ThreadContext.clear();
		// 设置为同步任务模式，日志记录内容打印显示为Quartz
		ThreadContext.set(ConfigProperty.LOGS_TOP_FLAG, ConfigProperty.LOGS_TOP_FLAG);
		this.info("Start quartz: " + this.getClass().getSimpleName());
		this.startQuartz();
		this.info("End quartz: " + this.getClass().getSimpleName());
		ThreadContext.clear();
	}

	public abstract void startQuartz();
}
