package com.h3c.iclouds.biz.impl;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.listen.StartTaskQueenListen;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;

public class TaskReceiver implements MessageListener {

	@Resource(name = "baseDAO")
	private BaseDAO<Task> taskDao;

	@Override
	public void onMessage(Message message) {
		String taskId = new String(message.getBody());
		LogUtils.info(getClass(), "Get rabbitMQ queen[iyun_task_queen] message:" + taskId);
		if (StrUtils.checkParam(taskId)) {
			for (int i = 0; i < 2000; i++) {
				if (StartTaskQueenListen.isBusy()) {
					try {
						LogUtils.info(TaskReceiver.class, "Thread busy. Start task[" + taskId + "] failure, wait for 5 sec.");
						TimeUnit.MILLISECONDS.sleep(5000);
					} catch (InterruptedException e) {
						LogUtils.exception(TaskReceiver.class, e);
					}
				} else {
					StartTaskQueenListen.startTask(taskId);
					break;
				}
			}
		}
	}

}
