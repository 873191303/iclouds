package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.ClustersBiz;
import com.h3c.iclouds.biz.PoolViewBiz;
import com.h3c.iclouds.biz.Server2OveBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.ClustersDao;
import com.h3c.iclouds.dao.Pools2HostDao;
import com.h3c.iclouds.po.Clusters;
import com.h3c.iclouds.po.Pools2Host;
import com.h3c.iclouds.po.Server2Ove;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("clustersBiz")
public class ClustersBizImpl extends BaseBizImpl<Clusters> implements ClustersBiz {

	@Resource
	private Pools2HostDao pools2HostDao;

	@Resource
	private ClustersDao clustersDao;

	@Resource
	private PoolViewBiz poolViewBiz;

	@Resource
	private Server2OveBiz server2OveBiz;

	@Override
	public PageModel<Clusters> findForPage(PageEntity entity) {
		return clustersDao.findForPage(entity);
	}

	public Object save(Clusters entity){
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(!validatorMap.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.success, entity);
		}
		if (StrUtils.checkParam(entity.getPhostId())){
			Pools2Host pools2Host = pools2HostDao.findById(Pools2Host.class, entity.getPhostId());
			if (!StrUtils.checkParam(pools2Host)){
				return BaseRestControl.tranReturnValue(ResultType.poolhost_not_exist);
			}
		}
		entity.createdUser(this.getLoginUser());
		String id = UUID.randomUUID().toString();
		entity.setId(id);
		String clusterId = clustersDao.add(entity);
		poolViewBiz.saveItemAndRelation(clusterId, entity.getCname(), ConfigProperty
						.POOLVIEW_CLUSTERS_TYPE, null, entity.getPhostId());
		return BaseRestControl.tranReturnValue(ResultType.success, entity);
	}

	public Object update(String id, Clusters entity){
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(!validatorMap.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
		}
		Clusters clusters = clustersDao.findById(Clusters.class, id);
		if (!StrUtils.checkParam(clusters)){
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		String name = null;
		String previousId = entity.getPhostId();
		if (!clusters.getCname().equals(entity.getCname())){
			name = entity.getCname();
		}
		if (StrUtils.checkParam(clusters.getPhostId()) && !clusters.getPhostId().equals(entity.getPhostId())){
			if (StrUtils.checkParam(entity.getPhostId())){
				Pools2Host pools2Host = pools2HostDao.findById(Pools2Host.class, entity.getPhostId());
				if (!StrUtils.checkParam(pools2Host)){
					return BaseRestControl.tranReturnValue(ResultType.poolhost_not_exist);
				}
			}
		}
		InvokeSetForm.copyFormProperties(entity, clusters);
		clusters.updatedUser(this.getLoginUser());
		clustersDao.update(clusters);
		poolViewBiz.updateItemAndRelation(id, name, previousId);
		return BaseRestControl.tranReturnValue(ResultType.success, entity);
	}

	public Object delete(String id){
		Clusters clusters = clustersDao.findById(Clusters.class, id);
		if (!StrUtils.checkParam(clusters)){
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		List<Server2Ove> server2Oves = server2OveBiz.findByPropertyName(Server2Ove.class, "custerId", id);
		if (StrUtils.checkParam(server2Oves)){//当有服务器归属当前集群时，不能删除
			return BaseRestControl.tranReturnValue(ResultType.clusters_used);
		}
		clustersDao.delete(clusters);
		poolViewBiz.deleteItemAndRelation(id);
		return BaseRestControl.tranReturnValue(ResultType.success);
	}

	@Override
	public void synAdd(Clusters entity) {
		entity.createdUser(this.getLoginUser());
		String id = UUID.randomUUID().toString();
		entity.setId(id);
		clustersDao.add(entity);
		poolViewBiz.saveItemAndRelation(id, entity.getCname(), ConfigProperty
				.POOLVIEW_CLUSTERS_TYPE, null, entity.getPhostId());
	}

	@Override
	public void synDelete(Clusters entity) {
		List<Server2Ove> server2Oves = server2OveBiz.findByPropertyName(Server2Ove.class, "custerId", entity.getId());
		if (StrUtils.checkCollection(server2Oves)) {
			for (Server2Ove server2Ove : server2Oves) {
				server2OveBiz.delete(server2Ove);
			}
		}
		poolViewBiz.deleteItemAndRelation(entity.getId());
		clustersDao.delete(entity);
	}
}
