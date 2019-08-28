package com.h3c.iclouds.dao;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.FloatingIp;

import java.util.List;

/**
* @author  zKF7420
* @date 2017年1月7日 上午9:47:16
*/
public interface FloatingIpDao extends BaseDAO<FloatingIp> {

	List<String> checkNetwork(FloatingIp floatingIp);
	
	int allotionCount();
}
