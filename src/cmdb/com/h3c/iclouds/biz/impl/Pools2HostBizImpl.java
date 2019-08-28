package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.ClustersBiz;
import com.h3c.iclouds.biz.PoolViewBiz;
import com.h3c.iclouds.biz.Pools2HostBiz;
import com.h3c.iclouds.biz.Server2OveBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.Pools2HostDao;
import com.h3c.iclouds.po.Clusters;
import com.h3c.iclouds.po.Pools2Host;
import com.h3c.iclouds.po.Server2Ove;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by yKF7317 on 2016/11/9.
 */
@Service("pools2HostBiz")
public class Pools2HostBizImpl extends BaseBizImpl<Pools2Host> implements Pools2HostBiz {

    @Resource
    private Pools2HostDao pools2HostDao;

    @Resource
    private ClustersBiz clustersBiz;

    @Resource
    private PoolViewBiz poolViewBiz;

    @Resource
    private Server2OveBiz server2OveBiz;
    
    @Resource
    private SqlQueryBiz queryBiz;

    @Override
    public PageModel<Pools2Host> findForPage(PageEntity entity) {
        return pools2HostDao.findForPage(entity);
    }

    public Object save(Pools2Host entity){
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);
        if(!validatorMap.isEmpty()) {
            return BaseRestControl.tranReturnValue(ResultType.success, entity);
        }
        entity.createdUser(this.getLoginUser());
        String id = UUID.randomUUID().toString();
        entity.setId(id);
        String poolId = pools2HostDao.add(entity);
        poolViewBiz.saveItemAndRelation(poolId, entity.getPoolName(), ConfigProperty.POOLVIEW_POOLHOST_TYPE, null,
                null);
        return BaseRestControl.tranReturnValue(ResultType.success, entity);
    }

    public Object update(String id, Pools2Host entity){
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);
        if(!validatorMap.isEmpty()) {
            return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
        }
        Pools2Host pools2host = pools2HostDao.findById(Pools2Host.class, id);
        if (pools2host == null){
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        String name = null;
        if (!pools2host.getPoolName().equals(entity.getPoolName())){
            name = entity.getPoolName();
        }
        InvokeSetForm.copyFormProperties(entity, pools2host);
        pools2host.updatedUser(this.getLoginUser());
        pools2HostDao.update(pools2host);
        if (StrUtils.checkParam(name)){
            poolViewBiz.updateItemAndRelation(id, entity.getPoolName(), null);
        }
        return BaseRestControl.tranReturnValue(ResultType.success, entity);
    }

    public Object delete(String id){
        Pools2Host pools2host = pools2HostDao.findById(Pools2Host.class, id);
        if (!StrUtils.checkParam(pools2host)){
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        List<Clusters> clusterses = clustersBiz.findByPropertyName(Clusters.class, "phostId", id);
        List<Server2Ove> server2Oves = server2OveBiz.findByPropertyName(Server2Ove.class, "poolId", id);
        if (StrUtils.checkParam(server2Oves) || StrUtils.checkParam(clusterses)){//当主机池底下挂了集群和服务器时，不能删除
            return BaseRestControl.tranReturnValue(ResultType.pools_used);
        }
        pools2HostDao.delete(pools2host);
        poolViewBiz.deleteItemAndRelation(id);
        return BaseRestControl.tranReturnValue(ResultType.success);
    }

    @Override
    public void synAdd(Pools2Host entity) {
        entity.createdUser(this.getLoginUser());
        String id = UUID.randomUUID().toString();
        entity.setId(id);
        pools2HostDao.add(entity);
        poolViewBiz.saveItemAndRelation(id, entity.getPoolName(), ConfigProperty.POOLVIEW_POOLHOST_TYPE, null,
                null);
    }

    @Override
    public void synDelete(Pools2Host entity) {
        List<Server2Ove> server2Oves = server2OveBiz.findByPropertyName(Server2Ove.class, "poolId", entity.getId());
        if (StrUtils.checkCollection(server2Oves)) {
            for (Server2Ove server2Ove : server2Oves) {
                server2OveBiz.synDelete(server2Ove);
            }
        }
        List<Clusters> clusterses = clustersBiz.findByPropertyName(Clusters.class, "phostId", entity.getId());
        if (StrUtils.checkCollection(clusterses)) {
            for (Clusters clusterse : clusterses) {
                clustersBiz.synDelete(clusterse);
            }
        }
        poolViewBiz.deleteItemAndRelation(entity.getId());
        pools2HostDao.delete(entity);
    }
    
    @Override
    public void addParam (Pools2Host pools2Host) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("poolId", pools2Host.getId());
        List<Map<String, Object>> projectList = queryBiz.queryByName(SqlQueryProperty.QUERY_HOSTPOOL_PROJECT,
                queryMap);
        if (StrUtils.checkCollection(projectList)) {
            pools2Host.setProjectList(projectList);
            pools2Host.setProjectCount(projectList.size());
        } else {
            pools2Host.setProjectCount(0);
        }
    }
}
