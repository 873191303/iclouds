package com.h3c.iclouds.biz.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import com.h3c.iclouds.biz.Server2VmBiz;
import com.h3c.iclouds.po.Server2Vm;
import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.PoolViewBiz;
import com.h3c.iclouds.biz.Server2OveBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.Server2OveDao;
import com.h3c.iclouds.po.Server2Ove;
import com.h3c.iclouds.utils.StrUtils;

@Service("server2OveBiz")
public class Server2OveBizImpl extends BaseBizImpl<Server2Ove> implements Server2OveBiz {

	@Resource
	private PoolViewBiz poolViewBiz;

	@Resource
	private Server2OveDao server2OveDao;
	
	@Resource
	private SqlQueryBiz queryBiz;
	
	@Resource
	private Server2VmBiz server2VmBiz;
	
	@Override
	public PageModel<Server2Ove> findForPage(PageEntity entity) {
		return server2OveDao.findForPage(entity);
	}

	@Override
	public void synAdd(Server2Ove entity) {
		String previousId = entity.getCusterId();
		if (!StrUtils.checkParam(previousId)){
			previousId = entity.getPoolId();
		}
		String id = UUID.randomUUID().toString();
		entity.setId(id);
		entity.setHostId(id);
		this.add(entity);
		poolViewBiz.saveItemAndRelation(id, entity.getHostName(), ConfigProperty
				.POOLVIEW_SERVER2OVE_TYPE, null, previousId);
	}

	@Override
	public void synDelete(Server2Ove entity) {
		List<Server2Vm> server2Vms = server2VmBiz.findByPropertyName(Server2Vm.class, "hostId", entity.getId());
		if (StrUtils.checkCollection(server2Vms)) {
			for (Server2Vm server2Vm : server2Vms) {
				server2VmBiz.delete(server2Vm);
			}
		}
		poolViewBiz.deleteItemAndRelation(entity.getId());
		server2OveDao.delete(entity);
	}

	@Override
	public List<Server2Ove> findTop5(String previousId) {
		return server2OveDao.findTop5(previousId);
	}

	@Override
	public List<Server2Ove> findTop5() {
		return findTop5(null);
	}
	@Override
	public void addParam (Server2Ove server2Ove) {
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("hostId", server2Ove.getId());
		List<Map<String, Object>> projectList = queryBiz.queryByName(SqlQueryProperty.QUERY_SERVER2OVE_PROJECT,
				queryMap);
		if (StrUtils.checkCollection(projectList)) {
			server2Ove.setProjectList(projectList);
			server2Ove.setProjectCount(projectList.size());
		} else {
			server2Ove.setProjectCount(0);
		}
	}
	
	@Override
	public List<Server2Ove> cpuTopList () {
		return server2OveDao.cpuTopList();
	}
	
	@Override
	public List<Server2Ove> memoryTopList () {
		return server2OveDao.memoryTopList();
	}
}
