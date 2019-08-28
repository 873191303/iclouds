package com.h3c.iclouds.dao;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.po.Vlb;
import com.h3c.iclouds.po.VlbVip;

/**
 * Created by yKF7317 on 2016/11/23.
 */
public interface VlbDao extends BaseDAO<Vlb> {

	PageModel<VlbVip> vipPoolList(PageEntity entity);

}
