package com.h3c.iclouds.dao;

import java.util.Map;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.po.RecycleItems;

public interface RecycleItemsDao extends BaseDAO<RecycleItems> {

	PageModel<Map<String, Object>> findCompleteForPage(PageEntity entity);

	PageModel<Map<String, Object>> findVolumeCompleteForPage(PageEntity entity);

}
