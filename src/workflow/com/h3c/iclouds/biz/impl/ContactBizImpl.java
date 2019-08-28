package com.h3c.iclouds.biz.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.ContactBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.ContactDao;
import com.h3c.iclouds.po.business.Contact;

@Service("contactBiz")
public class ContactBizImpl extends BaseBizImpl<Contact> implements ContactBiz {

	@Resource
	private ContactDao contactDao;

	@Override
	public PageModel<Contact> findForPage(PageEntity entity) {
		return contactDao.findForPage(entity);
	}
	
}
