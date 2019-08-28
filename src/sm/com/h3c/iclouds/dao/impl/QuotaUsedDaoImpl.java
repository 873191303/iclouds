package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.dao.QuotaUsedDao;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Project2Quota;
import com.h3c.iclouds.po.QuotaClass;
import com.h3c.iclouds.po.QuotaUsed;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("quotaUsedDao")
public class QuotaUsedDaoImpl extends BaseDAOImpl<QuotaUsed> implements QuotaUsedDao {

	@Override
	public void save(Project project) {
		List<QuotaClass> quotaClasses = CacheSingleton.getInstance().getQuotaClasses();
		for (QuotaClass quotaClass : quotaClasses) {
			QuotaUsed quotaUsed = new QuotaUsed();
			quotaUsed.createDate();
			quotaUsed.setTenantId(project.getId());
			quotaUsed.setClassId(quotaClass.getId());
			quotaUsed.setClassCode(quotaClass.getClassCode());
			quotaUsed.setQuotaUsed(0);
			add(quotaUsed);
		}
	}

	@Override
	public QuotaUsed get(Project project,Project2Quota project2Quota) {
		Map<String, Object> param = new HashMap<>();
		param.put("tenantId", project.getId());
		param.put("classCode", project2Quota.getClassCode());
		param.put("deleted", 0);
		List<QuotaUsed> quotaUseds = listByClass(QuotaUsed.class, param);
		return StrUtils.checkCollection(quotaUseds)?quotaUseds.get(0):null;
	}
	
}
