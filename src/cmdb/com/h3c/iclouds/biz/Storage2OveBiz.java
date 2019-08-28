package com.h3c.iclouds.biz;

import java.util.List;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.Storage2Ove;

public interface Storage2OveBiz extends BaseBiz<Storage2Ove> {
	List<Storage2Ove> findTop5();
}
