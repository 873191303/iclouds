package com.h3c.iclouds.dao;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.Port;

import java.util.List;

/**
 * Created by yKF7317 on 2016/11/26.
 */
public interface PortDao extends BaseDAO<Port> {

	List<Port> getPorts();
	
}
