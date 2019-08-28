package com.h3c.iclouds.dao;

import java.util.List;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Volume;

public interface VolumeDao extends BaseDAO<Volume> {

	List<Volume> findVolume(NovaVm novaVm);
	
	int monthCount();
}
