package com.h3c.iclouds.dao;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.Originals;

import java.util.Date;
import java.util.List;

/**
 * Created by zkf5485 on 2017/9/4.
 */
public interface OriginalsDao extends BaseDAO<Originals> {

    List<Originals> findLastData(String vassetId, String type);

    List<Originals> findOneDateData(String vassetId, Date start, Date end);
}
