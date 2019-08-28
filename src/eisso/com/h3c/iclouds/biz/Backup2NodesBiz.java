package com.h3c.iclouds.biz;

import java.util.List;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.Backup2Nodes;

public interface Backup2NodesBiz extends BaseBiz<Backup2Nodes> {
	void upate(List<Backup2Nodes> nodes);
}
