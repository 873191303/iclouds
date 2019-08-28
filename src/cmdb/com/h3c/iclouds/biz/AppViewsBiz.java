package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.AppViews;

import java.util.Map;

public interface AppViewsBiz extends BaseBiz<AppViews>{
	
	Map<String, Object> getAppViewByAppId(String appId);

	Object get(String type,String id);
	
	String getViews(String id);

	void clearLock(AppViews appViews);

}
