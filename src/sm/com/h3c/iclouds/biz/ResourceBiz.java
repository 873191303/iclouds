package com.h3c.iclouds.biz;

import java.util.List;

import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.Resource;

public interface ResourceBiz extends BaseBiz<Resource> {
	
	List<Resource> getResourceBySessionBean(SessionBean sessionBean);
	
}
