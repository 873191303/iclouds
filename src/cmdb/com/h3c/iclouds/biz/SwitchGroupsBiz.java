package com.h3c.iclouds.biz;

import java.util.List;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.SwitchGroups;
import com.h3c.iclouds.po.SwitchGroups2Items;

public interface SwitchGroupsBiz extends BaseBiz<SwitchGroups> {
	
	public void addLimit(SwitchGroups group, AsmMaster entity, String remark);
	
	public void removeLimit(String stackId, List<SwitchGroups2Items> list);
	
}
