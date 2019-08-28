package com.h3c.iclouds.dao;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.Renewal;
import java.util.List;

public interface RenewalDao extends BaseDAO<Renewal> {
	
	//查
	List<Renewal> selRenewalByAdmin(String userName);
	//增
	void insertRenewal(Renewal dto);
	
	//改
	void updateRenewal(Renewal dto);

}
