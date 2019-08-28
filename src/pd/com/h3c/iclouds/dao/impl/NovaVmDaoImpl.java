package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.dao.NovaVmDao;
import com.h3c.iclouds.po.Metadata;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.VmExtra;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("novaVmDao")
public class NovaVmDaoImpl extends BaseDAOImpl<NovaVm> implements NovaVmDao {

	@Resource(name = "baseDAO")
	private BaseDAO<Metadata> metadatDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<VmExtra> vmExtraDao;
	
	@Override
	public void deleteNovavm(NovaVm novaVm) {
		String id=novaVm.getId();
		VmExtra vmExtra = vmExtraDao.findById(VmExtra.class, id);
		if (StrUtils.checkParam(vmExtra)) {
			vmExtraDao.deleteById(VmExtra.class, id);
		}
		Map<String, Object> deleteWhere = new HashMap<>();
		deleteWhere.put("instanceUuid", id);
		metadatDao.delete(deleteWhere, Metadata.class);
		delete(novaVm);
	}
	
	@Override
	public int monthCount () {
		Criteria criteria = getSession().createCriteria(NovaVm.class);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		criteria.add(Restrictions.ge("createdDate", calendar.getTime()));
		Long result = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
		return null == result ? 0 : result.intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NovaVm> findListByProfix(String projectId, String profix) {
		Criteria criteria = getSession().createCriteria(NovaVm.class);
		criteria.add(Restrictions.eq("projectId", projectId));
		criteria.add(Restrictions.like("hostName", profix + "%"));
		return criteria.list();
	}
}
