package com.h3c.iclouds.task.novavm;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.QuotaUsedBiz;
import com.h3c.iclouds.biz.TaskBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.TaskTypeProperty;
import com.h3c.iclouds.po.NovaFlavor;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.task.BaseTask;
import com.h3c.iclouds.utils.StrUtils;

public abstract class NovaVmState extends BaseTask {

	protected final static Integer queryNum = Integer.parseInt(CacheSingleton.getInstance().getConfigValue("queryNum"));
	
	protected final static Integer millisecond = Integer.parseInt(CacheSingleton.getInstance().getConfigValue("millisecond"));
	// 云主机的配置
	final static int second = 1;
	final static Integer OK = 200;
	final static Integer accepted = 202;
	final static Integer noContent = 204;
	final static Integer notfound = 404;

	protected String rootId = CacheSingleton.getInstance().getConfigValue("rootid");
	
	protected QuotaUsedBiz quotaUsedBiz = SpringContextHolder.getBean("quotaUsedBiz");
	
	protected BaseDAO<NovaFlavor> novaFlavorDao = SpringContextHolder.getBean("baseDAO");
	
//	protected CometEngine engine;
	
	protected  String novaChannel;
	
	protected String busId;
	
	protected NovaVm novaVm;
	
	protected TaskBiz taskBiz = SpringContextHolder.getBean("taskBiz");
	
	//操作租户
	protected String projectId;
	
	public NovaVmState(Task task) {
		super(task);
//		CometContext context=CometContext.getInstance();
//		engine=context.getEngine();
	}
	

	@Override
	protected abstract String handle();

	public void updateQuota(Task task, NovaVm novaVm, String projectId, Integer ramdisk_gb, boolean flag) {
		// 修改租户配额使用量
		quotaUsedBiz.change("instances", task.getProjectId(), flag, 1);
		if (StrUtils.checkParam(novaVm.getFlavorId())) {
			NovaFlavor novaFlavor = novaFlavorDao.findById(NovaFlavor.class, novaVm.getFlavorId());
			if (StrUtils.checkParam(novaFlavor)) {
				quotaUsedBiz.change("cores", projectId, flag, novaFlavor.getVcpus());
				quotaUsedBiz.change("ram", projectId, flag, novaFlavor.getRam() / 1024);
				// 创建要保存云硬盘的记录
				quotaUsedBiz.change("gigabytes", projectId, flag, ramdisk_gb);
			}

		}
		quotaUsedBiz.change("ips", projectId, flag, 1);
	}
	public String getNotification(ResultType type) {
		Map<String, Object> data=BaseRestControl.tranReturnValue(type);
		return new JSONObject(data).toJSONString();
	}
	public String getNotification(ResultType cloudosApiError, String netUrl) {
		Map<String, Object> data=BaseRestControl.tranReturnValue(cloudosApiError,netUrl);
		return new JSONObject(data).toJSONString();
	}
	
	public boolean check(NovaVm novaVm) {
		if (StrUtils.checkParam(novaVm)) {
			String vmState=novaVm.getVmState();
			if (StrUtils.checkParam(vmState)) {
				if (vmState.endsWith("ing")) {
					return false;
				}
			}
			if ("state_exception".equals(vmState)||"state_create_failure".equals(vmState)) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	public Integer caculate(String taskId,Integer queryNum) {
		Task task = taskBiz.findById(Task.class, taskId);
		Integer num=0;
		if (StrUtils.checkParam(task)) {
			switch (task.getBusType()) {
			case TaskTypeProperty.VM_STARTUP: // 开机
				num=getNum("start_query_num", "start_milliseconds");
				queryNum +=num;
				//queryNum += 30;
				break;
			case TaskTypeProperty.VM_SHUTDOWN: // 关机
				num=getNum("shutdown_query_num", "shutdown_milliseconds");
				queryNum +=num;
				//queryNum += 100;
				break;
			case TaskTypeProperty.VM_CREATE: // 创建
				num=getNum("create_query_num", "create_milliseconds");
				queryNum +=num;
				//queryNum += 60;
				break;
			case TaskTypeProperty.VM_REBOOT: // 重启
				num=getNum("reboot_query_num", "reboot_milliseconds");
				queryNum +=num;
				//queryNum += 50;
				break;
			case TaskTypeProperty.VM_DELETE: // 删除
				//queryNum += 60;
				num=getNum("delete_query_num", "delete_milliseconds");
				queryNum +=num;
				break;
			}
		}
		return queryNum;
	}
	
	private Integer getNum(String query_num,String milliseconds) {
		CacheSingleton instance=CacheSingleton.getInstance();
		Integer query_num_int=Integer.parseInt(instance.getConfigValue(query_num));
		Integer milliseconds_int=Integer.parseInt(instance.getConfigValue(milliseconds));
		Integer num=query_num_int*milliseconds_int/5000;
		return num;
	}
	
	
}
