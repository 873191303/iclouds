package com.h3c.iclouds.biz.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.InitCodeBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.InitCodeDao;
import com.h3c.iclouds.po.InitCode;
import com.h3c.iclouds.utils.StrUtils;

@Service("initCodeBiz")
public class InitCodeBizImpl extends BaseBizImpl<InitCode> implements InitCodeBiz {
	
	@Resource
	private InitCodeDao initCodeDao;

	@Override
	public PageModel<InitCode> findForPage(PageEntity entity) {
		return initCodeDao.findForPage(entity);
	}

	@Override
	public InitCode getByTypeCode(String code, String type) {
		Map<String, Object> map = StrUtils.createMap("codeId", code);
		map.put("codeTypeId", type);
		List<InitCode> list = initCodeDao.listByClass(InitCode.class, map);
		return StrUtils.checkCollection(list) ? list.get(0) : null;
	}
	
}
