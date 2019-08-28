package com.h3c.iclouds.utils;

import java.lang.Thread.UncaughtExceptionHandler;

public class ErrHandler implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		LogUtils.warn(Thread.class,"This is:" + t.getName() + ",Message:" + e.getMessage());
		MailUtils.sendEmail("KF.yangzailang@h3c.com", "线程出现异常", "任务执行过程出现未能捕获的异常");

	}

}
