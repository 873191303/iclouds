package com.h3c.iclouds.dao;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.HealthyInstance;
import com.h3c.iclouds.po.HealthyValue;

import java.util.Date;
import java.util.List;

/**
 * Created by zkf5485 on 2017/9/13.
 */
public interface HealthyInstanceDao extends BaseDAO<HealthyInstance> {

    List<HealthyValue> data(String id, Date start, Date end);

}
