package com.h3c.iclouds.utils;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.po.bean.inside.SaveNovaVmBean;
import com.h3c.iclouds.biz.QuotaUsedBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.NovaFlavor;
import com.h3c.iclouds.po.NovaVm;

public class ResourceNovaHandle {

	private QuotaUsedBiz quotaUsedBiz = SpringContextHolder.getBean("quotaUsedBiz");

	private BaseDAO<NovaFlavor> novaFlavorDao = SpringContextHolder.getBean("baseDAO");

	public ResultType updateQuota(String flavorId, String projectId, boolean flag, int count) {
		// 修改租户配额使用量
		quotaUsedBiz.change("ips", projectId, flag, count);
		return updateFlavorQuota(flavorId, projectId, flag, count);
	}
	
	public ResultType updateQuota(NovaVm novaVm, boolean flag, int ipsCount) {
		// 修改租户配额使用量
		quotaUsedBiz.change("ips", novaVm.getProjectId(), flag, ipsCount);
		return updateFlavorQuota(novaVm.getFlavorId(), novaVm.getProjectId(), flag, 1);
	}
	
	public ResultType updateQuota(SaveNovaVmBean bean, boolean flag, String projectId) {
		Integer count = bean.getCount();
		// 修改租户配额使用量
		quotaUsedBiz.change("ips", projectId, flag, count);
		return updateFlavorQuota(bean.getFlavorId(), projectId, flag, count);
	}

	public ResultType updateFlavorQuota(String flavorId, String projectId, boolean flag, int count) {
		quotaUsedBiz.change("instances", projectId, flag, count);
		if (StrUtils.checkParam(flavorId)) {
			NovaFlavor novaFlavor = novaFlavorDao.findById(NovaFlavor.class, flavorId);
			if (StrUtils.checkParam(novaFlavor)) {
				quotaUsedBiz.change("cores", projectId, flag, novaFlavor.getVcpus()*count);
				quotaUsedBiz.change("ram", projectId, flag, count*novaFlavor.getRam() / 1024);
			} else {
				return ResultType.flavor_not_exist;
			}
		} else {
			return ResultType.novavm_flavor_not_exist;
		}
		return ResultType.success;
	}
}
