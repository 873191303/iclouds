package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.dao.Project2QuotaDao;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Project2Quota;
import com.h3c.iclouds.po.QuotaClass;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


@Repository("project2Quota")
public class Project2QuotaDaoImpl extends BaseDAOImpl<Project2Quota> implements Project2QuotaDao{

	private HashMap<String, String> init() {
		List<QuotaClass> list = CacheSingleton.getInstance().getQuotaClasses();
		HashMap<String, String> map = new HashMap<>();
		if (list != null) {
			for (QuotaClass quotaClass : list) {
				map.put(quotaClass.getClassCode(), quotaClass.getId());
			}
		}
		return map;
	}
	@Override
	public void save(Project project) {//初始化代码
		List<String> list = new ArrayList<>();
		list.addAll(ConfigProperty.class_computer_resource);
		list.addAll(ConfigProperty.class_storage_resource);
		list.addAll(ConfigProperty.class_network_resource);
		list.addAll(ConfigProperty.class_third_resource);
		HashMap<String, String> map = init();
		// 保存资源配额
		for (int i = 0; i < list.size(); i++) {
			Project2Quota project2Quota = new Project2Quota();
			project2Quota.setId(UUID.randomUUID().toString());
			project2Quota.setTenantId(project.getId());
			project2Quota.setClassCode(list.get(i));
			project2Quota.setClassId(map.get(list.get(i)));
			project2Quota.setHardLimit(0);
			project2Quota.createDate();
			add(project2Quota);
		}
	}
	
	@Override
	public List<Project2Quota> get(String projectId) {
		String hql="from Project2Quota p where p.tenantId=:tenantId And p.deleted=0";
		HashMap<String, Object> map=new HashMap<String, Object>();
		map.put("tenantId",projectId);
		return findByHql(hql, map);
	}
	@Override
	public List<Project2Quota> get(String id, Project2Quota project2Quota) {
		String hql="from Project2Quota p where p.tenantId=:tenantId And p.deleted=0";
		HashMap<String, Object> map=new HashMap<String, Object>();
		map.put("tenantId",id);
		map.put("classCode", project2Quota.getClassCode());
		return findByHql(hql, map);
		
	}
}
