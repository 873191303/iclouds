package com.h3c.iclouds.biz;

import java.util.List;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.RouterGroups;
import com.h3c.iclouds.po.RouterGroups2Items;

public interface RouterGroupsBiz extends BaseBiz<RouterGroups> {
	
	public void addLimit(RouterGroups group, AsmMaster entity, String remark);
	
	public void removeLimit(String stackId, List<RouterGroups2Items> list);
	
}
