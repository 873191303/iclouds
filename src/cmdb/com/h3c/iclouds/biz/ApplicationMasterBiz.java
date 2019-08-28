package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.ApplicationMaster;
import com.h3c.iclouds.po.bean.AppInfo;

public interface ApplicationMasterBiz extends BaseBiz<ApplicationMaster>{

	PageModel<ApplicationMaster> findForPage(PageEntity entity);
	
	ResultType save(ApplicationMaster applicationMaster);

	ResultType delete(String appId);

	void update(AppInfo appInfo);
	
	void updateApp(ApplicationMaster applicationMaster);

}
