package com.h3c.iclouds.task.novavm;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.TaskTypeProperty;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;

public class NovaVmRebootTask extends NovaVmState {

	private NovaVmBiz novaVmBiz = SpringContextHolder.getBean("novaVmBiz");
	
	private Integer reboot_query_num=100;
	
	private Integer reboot_milliseconds=2000;

	public NovaVmRebootTask(Task task) {
		super(task);
		CacheSingleton instance=CacheSingleton.getInstance();
		reboot_query_num=Integer.parseInt(instance.getConfigValue("reboot_query_num"));
		reboot_milliseconds=Integer.parseInt(instance.getConfigValue("reboot_milliseconds"));
	}
	private String uri = null;
	
	private JSONObject param = null;

	@Override
	protected String handle() {
		JSONObject re = JSONObject.parseObject(task.getInput());
		uri = re.getString("uri");
		param = re.getJSONObject("params");
		//String channel=re.getString(ConfigProperty.NOVAVM_PREFIX);
		 busId = task.getBusId();
		 novaVm = novaVmBiz.findById(NovaVm.class, busId);
		if (!StrUtils.checkParam(novaVm)) {
			//engine.sendToAll(channel, getNotification(ResultType.novam_not_exist));
			LogUtils.warn(NovaVm.class, "操作用户id["+task.getCreatedBy()+"]"+"未查询到云主机[" + busId + "]");
			this.saveTask2Exec("操作用户id["+task.getCreatedBy()+"]"+"未查询到云主机[" + busId + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, 0);
			return ConfigProperty.TASK_STATUS4_END_FAILURE;
		}
		for (int i = 0; i < retryNum; i++) {
			try {
				JSONObject result = client.post(uri, param);
				if (!result.getInteger("result").equals(202)) {
					LogUtils.warn(NovaVm.class, "输入参数:"+param.toJSONString()+"cloudos返回:"+result.toJSONString());
					this.saveTask2Exec("输入参数:"+param.toJSONString()+"cloudos返回:"+result.toJSONString(), ConfigProperty.TASK_STATUS4_END_FAILURE, i);
					continue;
				}
				boolean flag = false;
				final String stateUri = uri.substring(0, uri.indexOf("/action"));
				LogUtils.warn(NovaVmRebootTask.class, "轮询次数" + reboot_query_num);
				for (int j = 0; j < reboot_query_num; j++) {
					JSONObject vmstate = client.get(stateUri);
					String state = HttpUtils.getJSONObject(vmstate, "server").getString("status");
					if ("ACTIVE".equals(state)) {
						flag = true;
						break;
					}
					LogUtils.warn(NovaVmRebootTask.class, "轮询时间间隔" + reboot_milliseconds);
					Thread.sleep(reboot_milliseconds);
				}
				if (flag) {
					novaVm.setVmState(ConfigProperty.novaVmState.get(1));// 正常
					novaVm.setPowerState(TaskTypeProperty.VM_POWER_STATE_START);
					novaVmBiz.update(novaVm);
					this.saveTask2Exec("success", ConfigProperty.TASK_STATUS3_END_SUCCESS, i);
					return ConfigProperty.TASK_STATUS3_END_SUCCESS;
				} else {
					break;
				}

			} catch (Exception e) {
				novaVm.setVmState(ConfigProperty.novaVmState.get(4));// 异常
				novaVmBiz.update(novaVm);
				LogUtils.exception(Task.class, e,"操作用户id["+task.getCreatedBy()+"]",task.getInput(),"重启任务异常");
				saveTask2Exec(e.toString(), ConfigProperty.TASK_STATUS4_END_FAILURE, i);
				return ConfigProperty.TASK_STATUS4_END_FAILURE;
			}

		}
		LogUtils.warn(NovaVm.class, "操作用户id["+task.getCreatedBy()+"]"+"重启失败的云主机[" + busId + "]");
		novaVm.setVmState(ConfigProperty.novaVmState.get(2));// 停止
		novaVm.setPowerState(TaskTypeProperty.VM_POWER_STATE_SHUTDOWN);
		novaVmBiz.update(novaVm);
		saveTask2Exec(param.toJSONString(), ConfigProperty.TASK_STATUS4_END_FAILURE, retryNum);
		return ConfigProperty.TASK_STATUS4_END_FAILURE;
	}

}
