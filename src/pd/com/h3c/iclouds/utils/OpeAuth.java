package com.h3c.iclouds.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.TaskBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Task;

public class OpeAuth {

	private TaskBiz taskBiz = SpringContextHolder.getBean("taskBiz");

	private NovaVmBiz novaVmBiz = SpringContextHolder.getBean("novaVmBiz");

	/**
	 * 判断操作权限
	 * 
	 * @param entity
	 * @param isDeleted
	 * @return
	 */
	public ResultType checkOpeAuth(NovaVm entity, boolean isDeleted) {
		if (entity == null) {
			return ResultType.not_exist;
		}
		// 主机对应在cloudos的uuid未写入
		if (!StrUtils.checkParam(entity.getUuid())) {
			return ResultType.state_creating;
		}
		if (!isDeleted) {
			// 正在处理中
			if (entity.getVmState().contains("ing")) {
				return ResultType.failure;
			}
		}
		return ResultType.success;
	}

	public ResultType checkOpeAuth(NovaVm entity) {
		return checkOpeAuth(entity, true);
	}

	public ResultType checkOpeAuth(String id) {
		NovaVm entity = novaVmBiz.findById(NovaVm.class, id);
		return checkOpeAuth(entity, false);
	}

	public ResultType checkInTask(NovaVm entity) {
		Map<String, Object> params = new HashMap<>();
		params.put("busId", entity.getId());
		Task task = taskBiz.singleByClass(Task.class, params);
		if (StrUtils.checkParam(task)) {
			return ResultType.in_handle_task;
		}
		return checkOpeAuth(entity, false);
	}

	public ResultType checkStart(NovaVm novaVm) {
		if ("state_stop".equals(novaVm.getVmState())) {
			return ResultType.success;
		} else {
			return ResultType.novavm_start_only_stop;
		}
	}

	public ResultType checkStopOrReboot(NovaVm novaVm) {
		if ("state_normal".equals(novaVm.getVmState())) {
			return ResultType.success;
		} else {
			return ResultType.novavm_stoporreboot_only_normal;
		}
	}

	public ResultType checkStatus(NovaVm novaVm) {
		if (StrUtils.checkParam(novaVm)) {
			String vmState = novaVm.getVmState();
			ConcurrentHashMap<Integer, String> map = ConfigProperty.novaVmState;
			if (map.get(4).equals(vmState) || map.get(6).equals(vmState) || map.get(11).equals(vmState)) {
				return ResultType.novavm_state_illegal;
			} else {
				return ResultType.success;
			}
		}
		return ResultType.novam_not_exist;
	}

	public ResultType checkIng(NovaVm novaVm) {
		if (StrUtils.checkParam(novaVm)) {
			if (novaVm.getVmState().contains("ing")) {
				return ResultType.failure;
			} else {
				return ResultType.success;
			}
		}
		return ResultType.novam_not_exist;
	}

	/**
	 * 本身有权限
	 * @param sessionBean
	 * @param projectId
	 * @param owner
	 * @return
	 */
	public boolean checkAuth(SessionBean sessionBean, String projectId, String owner) {
		if (sessionBean.getSuperUser()) {
			if (!sessionBean.equals(projectId)) {
				return false;
			}
		}
		if (!sessionBean.getSuperRole()) {
			if (!sessionBean.getUserId().equals(owner)) {
				return false;
			}
		}
		return true;
	}

	public boolean checkAuth(SessionBean sessionBean, String owner) {
		if (sessionBean.getSuperUser()) {
			return true;
		}
		if (!sessionBean.getSuperRole()) {
			if (!sessionBean.getUserId().equals(owner)) {
				return false;
			}
		}
		return true;
	}

}
