package com.h3c.iclouds.dao;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.business.ListPrice;

/**
 * Created by yKF7317 on 2017/1/12.
 */
public interface ListPriceDao extends BaseDAO<ListPrice> {

    /**
     * 获取最后更新对象
     * @param classId
     * @return
     */
    ListPrice findLastModifyPrice(String classId);
}
