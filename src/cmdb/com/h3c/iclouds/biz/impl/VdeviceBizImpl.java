package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.VdeviceBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.VdeviceDao;
import com.h3c.iclouds.po.Vdevice;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by ykf7317 on 2017/9/5.
 */
@Service("vdeviceBiz")
public class VdeviceBizImpl extends BaseBizImpl<Vdevice> implements VdeviceBiz {
	
	@Resource
	private VdeviceDao vdeviceDao;
	
	@Override
	public PageModel<Vdevice> findForPage (PageEntity entity) {
		return vdeviceDao.findForPage(entity);
	}
}
