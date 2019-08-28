package com.h3c.iclouds.dao;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.po.Notice;

/**
* @author  zKF7420
* @date 2017年1月11日 上午11:08:06
*/
public interface NoticeDao extends BaseDAO<Notice> {

	PageModel<Notice> findForPageByTenant(PageEntity entity);

}
