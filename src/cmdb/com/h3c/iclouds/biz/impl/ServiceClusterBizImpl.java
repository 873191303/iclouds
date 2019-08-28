package com.h3c.iclouds.biz.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.po.bean.ApplicationBean;
import com.h3c.iclouds.biz.ServiceClusterBiz;
import com.h3c.iclouds.po.ServiceCluster;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;

@Service("serviceClusterBiz")
public class ServiceClusterBizImpl extends BaseBizImpl<ServiceCluster> implements ServiceClusterBiz{

	@Override
	public ServiceCluster save(ApplicationBean bean) {
		Map<String, Object> data=bean.getData();
		String itemId = bean.getId();
		ServiceCluster serviceCluster = new ServiceCluster();
		serviceCluster.setId(itemId);
		InvokeSetForm.settingForm(data, serviceCluster);
		
		if (!StrUtils.checkParam(serviceCluster.getCname())) {
			serviceCluster.setCname("集群名称");
			serviceCluster.setRelation("服务关系");
		}else {
			serviceCluster.setCname((String) data.get("cname"));
			serviceCluster.setRelation(String.valueOf(data.get("relation")));
		}
		serviceCluster.createdUser(getLoginUser());
		serviceCluster.setProjectId(getProjectId());
		this.add(serviceCluster);
		return serviceCluster;
	}

}
