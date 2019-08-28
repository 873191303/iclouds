package com.h3c.iclouds.biz;

import java.util.List;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.Cvm2Ove;

public interface Cvm2OveBiz extends BaseBiz<Cvm2Ove>{
	List<Cvm2Ove> findTop5();
}
