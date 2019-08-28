package com.h3c.iclouds.biz.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.TaskBiz;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.utils.RabbitMQUtils;
import com.h3c.iclouds.utils.StrUtils;

@Service("taskBiz")
public class TaskBizImpl extends BaseBizImpl<Task> implements TaskBiz {
	
	@Resource
	private AmqpTemplate amqpTemplate;

	@Override
	public Task save(String busId, String busType, String input) {
		Task task = new Task();
		task.setBusId(busId);
		task.setBusType(busType);
		task.setInput(input);
		task.createdUser(this.getLoginUser());
		task.setProjectId(this.getProjectId());
		task.setPushTime(new Date());
		this.info("Save task value " + StrUtils.toJSONString(task));
		this.add(task);

//		amqpTemplate.convertAndSend(RabbitMQUtils.QUEEN_NAME, task.getId());

		if(singleton.isRabbitMqMethod()) {
			amqpTemplate.convertAndSend(RabbitMQUtils.TASK_QUEEN, task.getId());
		} else {
			RabbitMQUtils.startTaskQueen(RabbitMQUtils.TASK_QUEEN, task.getId());
		}
		return task;
	}
	
	@Override
	public Task save(String busId, String busType, String input,String userId,String projectId) {
		Task task = new Task();
		task.setBusId(busId);
		task.setBusType(busType);
		task.setInput(input);
		task.createdUser(userId);
		task.setProjectId(projectId);
		task.setPushTime(new Date());
		this.info("Save task value " + StrUtils.toJSONString(task));
		this.add(task);
		// 程序开发阶段暂时由多线程的方式启动
//		amqpTemplate.convertAndSend(RabbitMQUtils.QUEEN_NAME, task.getId());
		if(singleton.isRabbitMqMethod()) {
			amqpTemplate.convertAndSend(RabbitMQUtils.TASK_QUEEN, task.getId());
		} else {
			RabbitMQUtils.startTaskQueen(RabbitMQUtils.TASK_QUEEN, task.getId());
		}
		return task;
	}
	
	@Override
	public boolean check(String busType,NovaVm novaVm) {
		List<Task> tasks=findByPropertyName(Task.class, "busType", novaVm.getId());
		for (Task task : tasks) {
			if (task.getBusType().equals(busType)) {
				return true;
			}
		}
		return false;
	}


	@Override
	public void save(Task task) {
		this.info("Save task value " + StrUtils.toJSONString(task));
//		this.add(task);
//		amqpTemplate.convertAndSend(RabbitMQUtils.QUEEN_NAME, task.getId());
		if(singleton.isRabbitMqMethod()) {
			amqpTemplate.convertAndSend(RabbitMQUtils.TASK_QUEEN, task.getId());
		} else {
			RabbitMQUtils.startTaskQueen(RabbitMQUtils.TASK_QUEEN, task.getId());
		}
	}

	@Override
	public Task saveImageTask(String busId, String busType, String input) {
		Task task = new Task();
		task.setBusId(busId);
		task.setBusType(busType);
		task.setInput(input);
		task.createdUser(this.getLoginUser());
		task.setProjectId(this.getProjectId());
		task.setPushTime(new Date());
		this.info("Save task value " + StrUtils.toJSONString(task));
		this.add(task);
		amqpTemplate.convertAndSend(RabbitMQUtils.IMAGE_TASK_QUEEN, task.getId());
		return task;
	}

}
