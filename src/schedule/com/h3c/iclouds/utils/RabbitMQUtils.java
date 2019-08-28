package com.h3c.iclouds.utils;

import com.h3c.iclouds.listen.StartTaskQueenListen;

public class RabbitMQUtils {
	
	/**
	 * 开始任务执行队列
	 */
	public static final String TASK_QUEEN = "iyun_task_queen";
	
	/**
	 * 镜像任务执行队列
	 */
	public static final String IMAGE_TASK_QUEEN = "iyun_task_image_queen";
	
	/**
	 * 开始执行任务
	 * @param queenName		队列名称
	 * @param taskId		任务id
	 */
	public static void startTaskQueen(String queenName, String taskId) {
		// TODO 目前先直接启动，后续修改采用队列的方式执行任务
		StartTaskQueenListen.startTask(taskId);
	}

}
