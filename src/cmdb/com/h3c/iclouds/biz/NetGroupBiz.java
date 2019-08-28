package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.NetGroup;

import java.util.List;

/**
 * Created by ykf7317 on 2017/9/5.
 */
public interface NetGroupBiz extends BaseBiz<NetGroup> {
	
	Object linkMasters(String id, List<String> masterIds);
	
	void deleteGroup(NetGroup netGroup);
	
}
