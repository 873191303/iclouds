package com.h3c.iclouds.task.novavm;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.po.bean.cloudos.InterfaceAttachment;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.PortBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosNovaVm;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Port;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class NovaVmDeleteTask extends NovaVmState {
	
	private NovaVmBiz novaVmBiz = SpringContextHolder.getBean("novaVmBiz");
	
	private PortBiz portBiz = SpringContextHolder.getBean("portBiz");
	
	private UserBiz userBiz = SpringContextHolder.getBean("userBiz");
	
	private Integer delete_milliseconds = 5000;
	
	private Integer delete_query_num = 200;
	
	private String uri = null;

	public NovaVmDeleteTask (Task task) {
		super(task);
		busId = task.getBusId();
		projectId = task.getProjectId();
		novaVm = novaVmBiz.findById(NovaVm.class, busId);
		CacheSingleton instance = CacheSingleton.getInstance();
		delete_query_num = Integer.parseInt(instance.getConfigValue("delete_query_num"));
		delete_milliseconds = Integer.parseInt(instance.getConfigValue("delete_milliseconds"));
	}
	
	@SuppressWarnings("unused")
	@Override
	protected String handle () {
		JSONObject re = JSONObject.parseObject(task.getInput());
		uri = re.getString("uri");
		if (null == novaVm) {
			LogUtils.warn(NovaVm.class, "云主机已删除");
			return ConfigProperty.TASK_STATUS4_END_FAILURE;
		}
		uri = checkUri(uri, novaVm);
		novaVm.setVmState(ConfigProperty.novaVmState.get(10));// 删除中
		novaVmBiz.update(novaVm);
		User user = userBiz.findById(User.class, novaVm.getOwner());
		CloudosClient cloudosClient = CloudosClient.create(user.getCloudosId(), user.getLoginName());
		Port port = portBiz.get(novaVm);
		List<InterfaceAttachment> interfaceAttachments = null;
		if (null == port) {
			LogUtils.warn(NovaVm.class, "操作用户id[" + task.getCreatedBy() + "]" + "云主机默认的网卡记录不存在");
		} else {
			// 保存当前的虚拟网卡记录
			CloudosNovaVm cloudosNovaVm = new CloudosNovaVm(cloudosClient);
			cloudosNovaVm.getOsInterface(novaVm.getProjectId(), novaVm.getUuid(), port);
		}
		for (int i = 0; i < retryNum; i++) {
			try {
				boolean flag = false;
				JSONObject vmstate = cloudosClient.get(uri);
				if (vmstate.getInteger("result").equals(404)) {// 已经不存在了
					flag = true;
				} else {
					JSONObject result = cloudosClient.delete(uri);
					// 删除不同主机
					if (!ResourceHandle.judgeResponse(result)) {
						this.saveTask2Exec("输入参数:" + uri + "cloudos返回:" + result.toJSONString(), ConfigProperty.TASK_STATUS4_END_FAILURE, i);
						LogUtils.info(NovaVm.class, result.toJSONString());
						continue;
					}
					LogUtils.warn(NovaVmDeleteTask.class, "轮询次数" + delete_query_num);
					for (int j = 0; j < delete_query_num; j++) {
						vmstate = cloudosClient.get(uri);
						if (vmstate.getInteger("result").equals(404)) {// 已经不存在了
							flag = true;
							break;
						}
						LogUtils.warn(NovaVmDeleteTask.class, "轮询时间间隔" + delete_milliseconds);
						TimeUnit.MILLISECONDS.sleep(delete_milliseconds);
					}
				}
				// 删除成功，否则继续请求轮询
				if (flag) {
					novaVmBiz.deleteNovaVm(novaVm, busId, cloudosClient);
					this.saveTask2Exec("success", ConfigProperty.TASK_STATUS3_END_SUCCESS, i);
					return ConfigProperty.TASK_STATUS3_END_SUCCESS;
				}
			} catch (Exception e) {
				LogUtils.exception(NovaVm.class, e, task.getInput());
				novaVm.setVmState(ConfigProperty.novaVmState.get(4));// 异常
				novaVmBiz.update(novaVm);
				saveTask2Exec(e.toString(), ConfigProperty.TASK_STATUS4_END_FAILURE, i);
				return ConfigProperty.TASK_STATUS4_END_FAILURE;
			}
		}
		novaVmBiz.deleteNovaVm(novaVm, busId, cloudosClient);
		saveTask2Exec(uri, ConfigProperty.TASK_STATUS4_END_FAILURE, retryNum);
		return ConfigProperty.TASK_STATUS4_END_FAILURE;
		
	}
	
	public String checkUri (String uri, NovaVm novaVm) {
		if (uri.contains("{")) {
			uri = HttpUtils.tranUrl(uri, novaVm.getUuid());
			return uri;
		} else {
			return uri;
		}
	}
	
}
