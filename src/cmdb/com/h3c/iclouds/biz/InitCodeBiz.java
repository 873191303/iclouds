package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.InitCode;

public interface InitCodeBiz extends BaseBiz<InitCode> {
	
	InitCode getByTypeCode(String code, String type);
	
}
