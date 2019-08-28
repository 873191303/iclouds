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
import com.h3c.iclouds.operate.CloudosNovaVm;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;

public class NovaVmWaitShutdownTask extends NovaVmState {

	private NovaVmBiz novaVmBiz = SpringContextHolder.getBean("novaVmBiz");

	private TaskBiz taskBiz = SpringContextHolder.getBean("taskBiz");

	public NovaVmWaitShutdownTask(Task task) {
		super(task);
	}

	private Integer time = 5;

	@Override
	protected String handle() {
		busId = task.getBusId();
		// 执行操作的租户
		projectId = task.getProjectId();
		String input = task.getInput();
		JSONObject re = JSONObject.parseObject(task.getInput());
		String uri = re.getString("uri");
		// String channel = re.getString(ConfigProperty.NOVAVM_PREFIX);
		Integer taskNum = re.getInteger("taskNum");
		Boolean flag = re.getBoolean("flag");
		List<String> taskIds = new ArrayList<>();
		Integer queryNum1 = 3;
		for (int i = 0; i < taskNum; i++) {
			String taskId = re.getString("taskId" + i);
			taskIds.add(taskId);
			queryNum1 = caculate(taskId, queryNum1);
		}
		NovaVm novaVm = novaVmBiz.findById(NovaVm.class, busId);
		if (!StrUtils.checkParam(novaVm)) {
			// engine.sendToAll(channel,
			// getNotification(ResultType.novam_not_exist));
			LogUtils.warn(NovaVm.class, "等待任务执行时云主机[" + busId + "]云主机名" + novaVm.getHostName() + "被删除");
			this.saveTask2Exec("未查询到云主机[" + busId + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, 0);
			return ConfigProperty.TASK_STATUS4_END_FAILURE;
		}
		for (int j = 0; j < queryNum1; j++) {
			try {
				if (flag) {
					novaVm = novaVmBiz.findById(NovaVm.class, busId);
					String uuid = novaVm.getUuid();
					if (StrUtils.checkParam(uuid)) {
						uri = HttpUtils.tranUrl(uri, uuid);
						re.put("uri", uri);
						task.setInput(re.toJSONString());
					}
				}
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
				TimeUnit.SECONDS.sleep(time);
			} catch (InterruptedException e) {
				LogUtils.exception(InterruptedException.class, e, task.getInput());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (StrUtils.checkParam(taskIds)) {
			for (String taskId : taskIds) {
				taskBiz.deleteById(Task.class, taskId);
			}
		} else {
			novaVm = novaVmBiz.findById(NovaVm.class, busId);
			// 此任务不会云主机状态操作
			CloudosNovaVm cloudosNovaVm = new CloudosNovaVm(client);
			if (StrUtils.checkParam(cloudosNovaVm)) {
				if (cloudosNovaVm.check(novaVm)) {
					if (novaVm.getVmState().equals("state_normal")) {
						taskBiz.save(busId, TaskTypeProperty.VM_SHUTDOWN, input, task.getCreatedBy(), projectId);
					}
				}
			}
		}
		return ConfigProperty.TASK_STATUS3_END_SUCCESS;
	}

//	private boolean check(String busType) {
//		if (TaskTypeProperty.VM_CREATE.equals(busType) || TaskTypeProperty.VM_REBOOT.equals(busType)
//				|| TaskTypeProperty.VM_STARTUP.equals(busType)) {
//			return true;
//		} else {
//			return false;
//		}
//	}
}
