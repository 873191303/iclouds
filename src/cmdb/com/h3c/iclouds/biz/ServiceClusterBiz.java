package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.bean.ApplicationBean;
import com.h3c.iclouds.po.ServiceCluster;

public interface ServiceClusterBiz extends BaseBiz<ServiceCluster>{

	ServiceCluster save(ApplicationBean bean);

}
