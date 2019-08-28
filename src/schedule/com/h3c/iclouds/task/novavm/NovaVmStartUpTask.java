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

public class NovaVmStartUpTask extends NovaVmState {

	private NovaVmBiz novaVmBiz = SpringContextHolder.getBean("novaVmBiz");
	
	private String uri = null;

	private JSONObject param = null;
	
	private Integer start_query_num=300;
	
	private Integer start_milliseconds=1000;

	public NovaVmStartUpTask(Task task) {
		super(task);
		CacheSingleton instance=CacheSingleton.getInstance();
		start_query_num=Integer.parseInt(instance.getConfigValue("start_query_num"));
		start_milliseconds=Integer.parseInt(instance.getConfigValue("start_milliseconds"));
	}

	@Override
	protected String handle() {
		busId = task.getBusId();
		novaVm = novaVmBiz.findById(NovaVm.class, busId);
		if (!StrUtils.checkParam(novaVm)) {
			LogUtils.warn(NovaVm.class, "操作用户id["+task.getCreatedBy()+"]"+"未查询到云主机[" + busId + "]");
			this.saveTask2Exec("操作用户id["+task.getCreatedBy()+"]"+"未查询到云主机[" + busId + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, 0);
			return ConfigProperty.TASK_STATUS4_END_FAILURE;
		}
		for (int i = 0; i < retryNum; i++) {
			try {
				JSONObject re = JSONObject.parseObject(task.getInput());
				uri = re.getString("uri");
				final String stateUri = uri.substring(0, uri.indexOf("/action"));
				boolean flag = false;
				param = re.getJSONObject("params");
				JSONObject result = client.post(uri, param);
				if (!result.getInteger("result").equals(202)) {
					if (result.getInteger("result").equals(409)) {
						result = client.get(stateUri);
						JSONObject server = HttpUtils.getJSONObject(result, "server");
						if (StrUtils.checkParam(server)) {
							String state = server.getString("status");
							if ("ACTIVE".equals(state)) {
								flag = true;
							}
						}
					}
					LogUtils.warn(NovaVm.class, "输入参数:" + param.toJSONString() + "cloudos返回:" + result.toJSONString());
					this.saveTask2Exec("输入参数:" + param.toJSONString() + "cloudos返回:" + result.toJSONString(), ConfigProperty.TASK_STATUS4_END_FAILURE, i);
					if (!flag) {
						continue;
					}
				} else {
					LogUtils.warn(NovaVmStartUpTask.class, "轮询次数" + start_query_num);
					for (int j = 0; j < start_query_num; j++) {
						JSONObject vmstate = client.get(stateUri);
						JSONObject server = HttpUtils.getJSONObject(vmstate, "server");
						if (StrUtils.checkParam(server)) {
							String state = server.getString("status");
							if ("ACTIVE".equals(state)) {
								flag = true;
								break;
							}
						} else {
							break;
						}
						LogUtils.warn(NovaVmStartUpTask.class, "轮询时间间隔" + start_milliseconds);
						Thread.sleep(start_milliseconds);
						
					}
				}
				if (flag) {
					novaVm.setVmState(ConfigProperty.novaVmState.get(1));// 正常
					novaVm.setPowerState(TaskTypeProperty.VM_POWER_STATE_START);
					novaVmBiz.update(novaVm);
					this.saveTask2Exec("success", ConfigProperty.TASK_STATUS3_END_SUCCESS, i);
					return ConfigProperty.TASK_STATUS3_END_SUCCESS;
				} else {
					LogUtils.warn(NovaVm.class, "操作用户id["+task.getCreatedBy()+"]"+"cloudos关机过程失败");
					break;
				}

			} catch (Exception e) {
				// engine.sendToAll(channel,BaseRestControl.tranReturnValue(ResultType.novam_not_exist));
				novaVm.setVmState(ConfigProperty.novaVmState.get(4));// 异常
				novaVmBiz.update(novaVm);
				LogUtils.exception(Task.class, e,"操作用户id["+task.getCreatedBy()+"]", task.getInput(), novaVm);
				saveTask2Exec(e.toString(), ConfigProperty.TASK_STATUS4_END_FAILURE, i);
				return ConfigProperty.TASK_STATUS4_END_FAILURE;
			}

		}
		//engine.sendToAll(channel, getNotification(ResultType.cloudos_api_error, task.getInput()));
		novaVm.setVmState(ConfigProperty.novaVmState.get(2));// 停止
		novaVm.setPowerState(TaskTypeProperty.VM_POWER_STATE_SHUTDOWN);
		novaVmBiz.update(novaVm);
		LogUtils.warn(NovaVm.class,"操作用户id["+task.getCreatedBy()+"]"+"三次cloudos请求开机失败");
		saveTask2Exec(param.toJSONString(), ConfigProperty.TASK_STATUS4_END_FAILURE, retryNum);
		return ConfigProperty.TASK_STATUS4_END_FAILURE;
	}

}
