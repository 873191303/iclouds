package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.VportBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.VportDao;
import com.h3c.iclouds.po.Vport;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by ykf7317 on 2017/9/5.
 */
@Service("vportBiz")
public class VportBizImpl extends BaseBizImpl<Vport> implements VportBiz {
	
	@Resource
	private VportDao vportDao;
	
	@Override
	public PageModel<Vport> findForPage (PageEntity entity) {
		return vportDao.findForPage(entity);
	}
}
