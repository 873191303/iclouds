package com.h3c.iclouds.biz;

import java.util.List;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.bean.model.TenantBean;
import com.h3c.iclouds.po.business.Custom;

public interface CustomBiz extends BaseBiz<Custom> {

	Object listCustom(String text);

	List<Custom> get(TenantBean project);

}
