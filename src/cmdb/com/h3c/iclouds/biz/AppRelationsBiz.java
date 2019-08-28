package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.bean.ApplicationBean;
import com.h3c.iclouds.po.AppRelations;

public interface AppRelationsBiz extends BaseBiz<AppRelations>{

	void save(ApplicationBean bean, String viewId);

}
