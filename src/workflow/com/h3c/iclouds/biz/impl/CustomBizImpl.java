package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.po.bean.model.TenantBean;
import com.h3c.iclouds.biz.CustomBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.CustomDao;
import com.h3c.iclouds.po.business.Custom;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("customBiz")
public class CustomBizImpl extends BaseBizImpl<Custom> implements CustomBiz {

	@Resource
	private CustomDao customDao;
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;

	@Override
	public PageModel<Custom> findForPage(PageEntity entity) {
		return customDao.findForPage(entity);
	}

	@Override
	public Object listCustom(String text) {
		if (!StrUtils.checkParam(text)) {
			text = "";
		}
		List<Map<String, Object>> list = sqlQueryBiz.queryBySqlLike(SqlQueryProperty.QUERY_NOT_TENANT_CUSTOM, text);
		List<Custom> data = new ArrayList<>();
		Map<String, Object> result=new HashMap<>();
		for (Map<String, Object> map2 : list) {
			Custom custom = new Custom();
			InvokeSetForm.settingForm(map2, custom);
			data.add(custom);
		}
		result.put("result", "success");
		result.put("record", data);
		return result;
	}
	@Override
	public List<Custom> get(TenantBean project) {
		String hql="from Custom c where c.id=:id And status=:status";
		Map<String, Object> map=new  HashMap<String, Object>();
		map.put("id", project.getCusId());
		map.put("status", 0);
		List<Custom> customs = customDao.findByHql(hql, map);
		return customs;
	}
}
