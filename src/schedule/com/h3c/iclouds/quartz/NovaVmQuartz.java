package com.h3c.iclouds.quartz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.bean.nova.ServerDetail;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.TaskBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.TaskTypeProperty;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.po.Task2Exec;
import com.h3c.iclouds.po.TaskHis;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.JacksonUtil;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.ResourceNovaHandle;
import com.h3c.iclouds.utils.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NovaVmQuartz {

	public Logger log = LoggerFactory.getLogger(NovaVmQuartz.class);

	@Resource
	private NovaVmBiz novaVmBiz;

	@Resource
	private TaskBiz taskBiz;

	@Resource(name = "baseDAO")
	private BaseDAO<Task2Exec> task2ExecDao;

	@Resource(name = "baseDAO")
	private BaseDAO<TaskHis> taskHisDao;

	public void updateIyun() {
		List<NovaVm> novaVms = novaVmBiz.getAll(NovaVm.class);
		Map<String, ServerDetail> details = getServer();
		if (details.isEmpty()) {
			//获取云主机信息可能会失败
			return;
		}
		for (NovaVm novaVm : novaVms) {
			String uuid = novaVm.getUuid();
			if (!StrUtils.checkParam(uuid)) {
				List<Task> tasks = taskBiz.findByPropertyName(Task.class, "busId", novaVm.getId());
				if (!StrUtils.checkCollection(tasks)) {
					// 创建失败
					novaVm.setVmState(ConfigProperty.novaVmState.get(6));
					novaVmBiz.update(novaVm);
				}
				LogUtils.warn(Task.class, tasks);
				LogUtils.warn(NovaVmQuartz.class, "cloudos云主机uuid未写入");
				continue;
			}
			// 得到云主机记录，若无则不比较，若有比较
			List<Task> tasks = null;
			ServerDetail detail = details.get(uuid);
			if(detail == null) {
				novaVm.setVmState(ConfigProperty.novaVmState.get(4));
				novaVmBiz.update(novaVm);
				LogUtils.warn(NovaVmQuartz.class, JacksonUtil.toJSon(novaVm));
				continue;
			}
			String vmState = CloudosParams.getVmState(detail.getStatus());
			if (!vmState.equals(novaVm.getVmState())) {
				tasks = taskBiz.findByPropertyName(Task.class, "busId", novaVm.getId());
				if (!StrUtils.checkCollection(tasks)) {
					// 任务执行完，
					novaVm.setVmState(vmState);
					novaVmBiz.update(novaVm);
					continue;
				}
				// 有任务时的处理
				boolean flag = false;
				int count = 0;
				for (Task task : tasks) {
					List<Task2Exec> task2Execs = task2ExecDao.findByPropertyName(Task2Exec.class, "taskId",
							task.getId());
					if (StrUtils.checkCollection(task2Execs)) {
						flag = true;
					}
					List<TaskHis> taskHis = taskHisDao.findByPropertyName(TaskHis.class, "busId",
							novaVm.getId());
					if (StrUtils.checkCollection(taskHis) || flag) {
						if (novaVm.getVmState().endsWith("ing")
								&& !novaVm.getVmState().equals(ResultType.state_clone_operating.toString())
								&& !novaVm.getVmState().equals(ResultType.state_image_translating.toString())) {
							// 克隆状态且有克隆的任务则不做修改
							if(novaVm.getVmState().equals(ResultType.state_clone_operating.toString())
									|| novaVm.getVmState().equals(ResultType.state_cloning.toString())) {
								if(count == 0) {
									count = taskBiz.count(Task.class, StrUtils.createMap("busType", TaskTypeProperty.VM_CLONE));
								}
								if(count > 0) {
									continue;
								}
							}

							novaVm.setVmState(vmState);
							novaVmBiz.update(novaVm);
							if (novaVm.getVmState().equals("state_deleting")) {
								ResourceNovaHandle resourceNovaHandle = new ResourceNovaHandle();
								resourceNovaHandle.updateFlavorQuota(novaVm.getFlavorId(), novaVm
										.getProjectId(), true, 1);
								novaVmBiz.update(novaVm);
							}
						}
					}
				}
			}
		}
	}

	public Map<String, ServerDetail> getServer() {
		Map<String, ServerDetail> map = new HashMap<>();
		CloudosClient client = CloudosClient.createAdmin();
		if(client != null) {
			String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_SERVER_DETAIL);
			String rootId = CacheSingleton.getInstance().getConfigValue("rootid");
			uri = HttpUtils.tranUrl(uri, rootId);
			JSONObject result = client.get(uri);
			if (ResourceHandle.judgeResponse(result)) {
				JSONArray servers = HttpUtils.getJSONArray(result, "servers");
				for (int i = 0; i < servers.size(); i++) {
					JSONObject server = servers.getJSONObject(i);
					ServerDetail serverDetail = new ServerDetail();
					InvokeSetForm.settingForm(server, serverDetail);
					map.put(serverDetail.getId(), serverDetail);
				}
			}
		}
		return map;
	}

	public void syn() {
		try {
			updateIyun();
		} catch (Exception e) {
		}

	}
}
