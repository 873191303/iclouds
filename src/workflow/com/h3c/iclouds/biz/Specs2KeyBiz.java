package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.business.Specs2Key;

public interface Specs2KeyBiz extends BaseBiz<Specs2Key> {

    boolean checkUnit(String name, String unit);

    void delete(String id);
	
	Specs2Key getKey(String keyName, String key, String classId, String createdUserId, String unit, String dataType, String validate);
}
