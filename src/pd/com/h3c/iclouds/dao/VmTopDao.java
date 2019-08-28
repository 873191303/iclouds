package com.h3c.iclouds.dao;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.VmTop;

import java.util.List;

/**
 * Created by ykf7317 on 2017/8/25.
 */
public interface VmTopDao extends BaseDAO<VmTop> {
	
	List<VmTop> findTop(String type, int size);
	
}
