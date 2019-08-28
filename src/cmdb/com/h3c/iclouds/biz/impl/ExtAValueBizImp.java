package com.h3c.iclouds.biz.impl;


import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.ExtAValueBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.ExtAValueDao;
import com.h3c.iclouds.po.ExtAValue;

@Service("extAValueBiz")
public class ExtAValueBizImp extends BaseBizImpl<ExtAValue> implements ExtAValueBiz {
    
	@Resource
	private ExtAValueDao extAValueDao;

	@Override
	public PageModel<ExtAValue> findForPage(PageEntity entity) {
		return extAValueDao.findForPage(entity);
	}

	

}
