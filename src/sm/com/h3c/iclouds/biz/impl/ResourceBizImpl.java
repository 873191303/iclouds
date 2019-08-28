package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.ResourceBiz;
import com.h3c.iclouds.dao.ResourceDao;
import com.h3c.iclouds.po.Resource;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.TreePick;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("resourceBiz")
public class ResourceBizImpl extends BaseBizImpl<Resource> implements ResourceBiz {
	
	@javax.annotation.Resource
	private ResourceDao resourceDao;

	@Override
	public void delete(Resource entity) {
		List<Resource> list = this.findByPropertyName(Resource.class, "parentId", entity.getId());
		if(list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				this.delete(list.get(i));	
			}
		}
		super.delete(entity);
	}

	@Override
	public List<Resource> getResourceBySessionBean(SessionBean sessionBean) {
		List<Resource> resources = resourceDao.getResourceByUserId(sessionBean.getUserId());
		Set<String> keySet = new HashSet<>();
		if(StrUtils.checkCollection(resources)) {
			resources.forEach(res -> keySet.add(res.getResPath()));
		}
		sessionBean.setKeySet(keySet);
		resources = TreePick.getUserResource(resources, sessionBean.getSuperUser() ? -1 : sessionBean.getRoleAuthIndex());

		List<Resource> root = TreePick.pickResource(resources, "1");
		sessionBean.setResources(root);
		return root;
	}
	
}
