package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.FirewallGroups;
import com.h3c.iclouds.po.FirewallGroups2Items;

import java.util.List;

public interface FirewallGroupsBiz extends BaseBiz<FirewallGroups> {
	
	void addLimit (FirewallGroups group, AsmMaster entity, String remark);
	
	void removeLimit (String stackId, List<FirewallGroups2Items> list);
	
}
