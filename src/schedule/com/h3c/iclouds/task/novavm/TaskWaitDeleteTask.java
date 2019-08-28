package com.h3c.iclouds.task.novavm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.TaskBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.TaskTypeProperty;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;

public class TaskWaitDeleteTask extends NovaVmState {

	private NovaVmBiz novaVmBiz = SpringContextHolder.getBean("novaVmBiz");

	private TaskBiz taskBiz = SpringContextHolder.getBean("taskBiz");

	public TaskWaitDeleteTask(Task task) {
		super(task);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String handle() {
		busId = task.getBusId();
		// 执行操作的租户
		projectId = task.getProjectId();
		JSONObject re = JSONObject.parseObject(task.getInput());
		// String channel = re.getString(ConfigProperty.NOVAVM_PREFIX);
		Integer taskNum = re.getInteger("taskNum");
		List<String> taskIds = new ArrayList<>();
		Integer queryNum1 = 3;
		for (int i = 0; i < taskNum; i++) {
			String taskId = re.getString("taskId" + i);
			if (StrUtils.checkParam(taskId)) {
				taskIds.add(taskId);
				queryNum1 = caculate(taskId, queryNum1);
			}
		}
		NovaVm novaVm = novaVmBiz.findById(NovaVm.class, busId);
		if (!StrUtils.checkParam(novaVm)) {
			// engine.sendToAll(channel,
			// getNotification(ResultType.novam_not_exist));
			LogUtils.warn(NovaVm.class, "等待任务执行时云主机[" + busId + "]");
			this.saveTask2Exec("未查询到云主机[" + busId + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, 0);
			return ConfigProperty.TASK_STATUS4_END_FAILURE;
		}
		for (int j = 0; j < queryNum1; j++) {
			try {
				if (StrUtils.checkCollection(taskIds)) {
					Iterator<String> iterator = taskIds.iterator();
					while (iterator.hasNext()) {
						String taskId = (String) iterator.next();
						Task waitTask = taskBiz.findById(Task.class, taskId);
						if (!StrUtils.checkParam(waitTask)) {
							iterator.remove();
						}
					}
					if (!StrUtils.checkCollection(taskIds)) {
						break;
					}
				} else {
					// 没有传入等待的任务
					break;
				}
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				LogUtils.exception(InterruptedException.class, e, task.getInput());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// 正常结束时删除云主机
		if (StrUtils.checkParam(taskIds)) {
			for (String taskId : taskIds) {
				taskBiz.deleteById(Task.class, taskId);
			}
		} else {
			// 频繁点击删除，第一个删除任务已经删除
			novaVm = novaVmBiz.findById(NovaVm.class, busId);
			if (StrUtils.checkParam(novaVm)) {
				taskBiz.save(busId, TaskTypeProperty.VM_DELETE, task.getInput(), task.getCreatedBy(), projectId);
			}
		}
		return ConfigProperty.TASK_STATUS3_END_SUCCESS;
	}

}
