package com.h3c.iclouds.task.novavm;

import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.TaskTypeProperty;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.JacksonUtil;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;

public class NovaVmShutDownTask extends NovaVmState {

//	private Integer shutdown;

	private NovaVmBiz novaVmBiz = SpringContextHolder.getBean("novaVmBiz");
	
	private Integer shutdown_milliseconds=5000;
	
	private Integer shutdown_query_num=200;
	
	

	public NovaVmShutDownTask(Task task) {
		super(task);
		CacheSingleton instance=CacheSingleton.getInstance();
		shutdown_milliseconds=Integer.parseInt(instance.getConfigValue("shutdown_milliseconds"));
		shutdown_query_num=Integer.parseInt(instance.getConfigValue("shutdown_query_num"));
	}

	private String uri = null;
	
	private JSONObject param = null;
	
	@Override
	protected String handle() {
		busId = task.getBusId();
		//等待关机
		NovaVm novaVm = novaVmBiz.findById(NovaVm.class, busId);
		novaVm.setVmState(ConfigProperty.novaVmState.get(9));// 关机中
		novaVmBiz.update(novaVm);
		JSONObject re = JSONObject.parseObject(task.getInput());
		uri = re.getString("uri");
		param = re.getJSONObject("params");
		if (!StrUtils.checkParam(novaVm)) {
			LogUtils.warn(NovaVm.class, "操作用户id["+task.getCreatedBy()+"]"+"未查询到云主机[" + busId + "]");
			this.saveTask2Exec("操作用户id["+task.getCreatedBy()+"]"+"未查询到云主机[" + busId + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, 0);
			return ConfigProperty.TASK_STATUS4_END_FAILURE;
		}
		uri = checkUri(uri, novaVm);
		for (int i = 0; i < retryNum; i++) {
			try {
				final String stateUri = uri.substring(0, uri.indexOf("/action"));
				boolean flag = false;
				JSONObject result = client.post(uri, param);
				if (!result.getInteger("result").equals(202)) {
					if (result.getInteger("result").equals(409)) {
						result = client.get(stateUri);
						JSONObject server = HttpUtils.getJSONObject(result, "server");
						if (StrUtils.checkParam(server)) {
							String state = server.getString("status");
							if ("SHUTOFF".equals(state)) {
								flag = true;
							}
						}
					}
					LogUtils.warn(NovaVm.class, "cloudos异常信息"+result.toJSONString());
					saveTask2Exec("输入参数:"+param.toJSONString()+"cloudos返回:"+result.toJSONString(), ConfigProperty.TASK_STATUS4_END_FAILURE, i);
					if (!flag) {
						continue;
					}
				} else {
					LogUtils.warn(NovaVmStartUpTask.class, "轮询次数" + shutdown_query_num);
					for (int j = 0; j < shutdown_query_num; j++) {
						JSONObject vmstate = client.get(stateUri);
						JSONObject server=HttpUtils.getJSONObject(vmstate, "server");
						//关机中被删除
						if (StrUtils.checkParam(server)) {
							String state = server.getString("status");
							if ("SHUTOFF".equals(state)) {
								flag = true;
								break;
							}
						}else {
							//云主机已经不存在
							break;
						}
						TimeUnit.MILLISECONDS.sleep(shutdown_milliseconds);
					}
				}
				if (flag) {
					novaVm.setPowerState(TaskTypeProperty.VM_POWER_STATE_SHUTDOWN);
					novaVm.setVmState(ConfigProperty.novaVmState.get(2));// 停止
					novaVmBiz.update(novaVm);
					this.saveTask2Exec("success", ConfigProperty.TASK_STATUS3_END_SUCCESS, i);
					return ConfigProperty.TASK_STATUS3_END_SUCCESS;
				} else {
					LogUtils.warn(NovaVm.class, "操作用户id["+task.getCreatedBy()+"]"+"关闭失败" + "云主机id:" + novaVm.getId()+ "云主机名称:" + novaVm.getHostName() +JacksonUtil.toJSon(novaVm));
					break;
				}

			} catch (Exception e) {
				novaVm.setVmState(ConfigProperty.novaVmState.get(4));// 异常
				novaVmBiz.update(novaVm);
				LogUtils.exception(NovaVm.class, e, "操作用户id["+task.getCreatedBy()+"]","关机过程出现异常" + JacksonUtil.toJSon(novaVm));
				saveTask2Exec(e.toString(), ConfigProperty.TASK_STATUS4_END_FAILURE, i);
				return ConfigProperty.TASK_STATUS4_END_FAILURE;
			}

		}
		// 三次请求失败，说明cloudos挂了或者出现繁忙状态
		novaVm.setVmState(ConfigProperty.novaVmState.get(1));// 正常
		novaVm.setPowerState(TaskTypeProperty.VM_POWER_STATE_START);
		saveTask2Exec(param.toJSONString(), ConfigProperty.TASK_STATUS4_END_FAILURE, retryNum);
		LogUtils.warn(NovaVm.class, "操作用户id["+task.getCreatedBy()+"]"+"关机失败，云主机保持正常状态");
		return ConfigProperty.TASK_STATUS4_END_FAILURE;
	}
	
	public String checkUri(String uri,NovaVm novaVm) {
		if (uri.contains("{")) {
			uri=HttpUtils.tranUrl(uri, novaVm.getUuid());
			return uri;
		}else {
			return uri;
		}
	}

}
