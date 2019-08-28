package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.NovaFlavorDao;
import com.h3c.iclouds.po.NovaFlavor;
import com.h3c.iclouds.po.Rules;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("novaFlavorDao")
public class NovaFlavorDaoImpl extends BaseDAOImpl<NovaFlavor> implements NovaFlavorDao {

	@Resource(name = "baseDAO")
	private BaseDAO<Rules> rulesDao;
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;
	
	@Override
	public PageModel<NovaFlavor> findForPage (PageEntity entity) {
		Criteria criteria = getSession().createCriteria(NovaFlavor.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 模糊查询
			criteria.add(Restrictions.or(
					Restrictions.like("name", "%" + entity.getSearchValue() + "%")
			));
		}
		if (StrUtils.checkParam(entity.getSpecialParams()) && entity.getSpecialParams().length > 0) {
			criteria.add(Restrictions.in("id", entity.getSpecialParams()));
		}
		Map<String, String> order = new HashMap<String, String>();
		addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
		return this.findForPage(NovaFlavor.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<NovaFlavor> get() {
		Criteria criteria = getSession().createCriteria(NovaFlavor.class);
		String imageRef=request.getParameter("imageRef");
		if (StrUtils.checkParam(imageRef)) {
			Rules rules=rulesDao.findById(Rules.class, imageRef);
			criteria.add(Restrictions.ge("disk", rules.getMinDisk()));
			criteria.add(Restrictions.ge("ram", rules.getMinRam()));
			criteria.add(Restrictions.ge("vcpus", rules.getVcpu()));
			List<NovaFlavor> list = criteria.list();
			return list;
		}
		
		criteria.add(Restrictions.eq("deleted", "0"));
		List<NovaFlavor> list = criteria.list();
		return list;
	}

	@Override
	public NovaFlavor getByImage(Rules entity) {
		String listSql = getSession().getNamedQuery(SqlQueryProperty.QUERY_PUBLIC_NOVAFLAVOR).getQueryString();
		List<Map<String, Object>> countList = sqlQueryBiz.queryBySql(listSql);
		NovaFlavor novaFlavor=new NovaFlavor();
		for (Map<String, Object> map : countList) {
			InvokeSetForm.settingForm(map, novaFlavor );
		}
		return novaFlavor;
		
	}
}
