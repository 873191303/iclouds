package com.h3c.iclouds.quartz;

import javax.annotation.Resource;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.common.TaskTypeProperty;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.task.BaseTask;
import com.h3c.iclouds.task.novavm.NovaVmCloneTask;
import com.h3c.iclouds.task.novavm.NovaVmToImageTask;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;

public class ImageTaskReceiver implements MessageListener {

	@Resource(name = "baseDAO")
	private BaseDAO<Task> taskDao;

	@Override
	public void onMessage(Message message) {
		String taskId = new String(message.getBody());
		LogUtils.info(getClass(), "Get rabbitMQ queen[iyun_task_image_queen] message:" + taskId);
		if (StrUtils.checkParam(taskId)) {
			Task entity = null;
			for (int i = 0; i < 10; i++) {	// 增加循环查询数据
				entity = taskDao.findById(Task.class, taskId);
				if(entity != null) {
					break;
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace(); 
				}
			}
			if(entity != null) {
				BaseTask task = null;
				LogUtils.info(this.getClass(), "Start task, value:" + StrUtils.toJSONString(entity));
				if(TaskTypeProperty.VM_TO_IMAGE.toString().equals(entity.getBusType())) {
					task = new NovaVmToImageTask(entity);	
				} else {
					task = new NovaVmCloneTask(entity);
				}
				task.run();	// 不使用线程的方式启动
			}
		}
	}

}
