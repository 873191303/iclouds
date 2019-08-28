package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.Azone;

/**
 * Created by yKF7317 on 2016/11/19.
 */
public interface AzoneBiz extends BaseBiz<Azone> {
	
	String[] getAzoneIds(String projectId);
}
