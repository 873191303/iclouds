package com.h3c.iclouds.rest;

import java.io.IOException;

import javax.annotation.Resource;

import com.h3c.iclouds.utils.StrUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.ClustersBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.Clusters;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;


/**
 * Created by yKF7317 on 2016/11/8.
 */
@Api(value = "资源配置服务器集群配置", description = "资源配置服务器集群配置")
@RestController
@RequestMapping("/clusters")
public class ClustersRest extends BaseRest<Clusters> {

    @Resource
    private ClustersBiz clustersBiz;

    @Override
    @ApiOperation(value = "获取服务器集群配置信息列表", response = Clusters.class)
    @RequestMapping(method = RequestMethod.GET)
    public Object list() {
        PageEntity entity = this.beforeList();
        PageModel<Clusters> pageModel = clustersBiz.findForPage(entity);
        PageList<Clusters> page = new PageList<Clusters>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

    @ApiOperation(value = "获取主机池服务器集群配置信息列表", response = Clusters.class)
    @RequestMapping(value = "/{poolId}/clusters", method = RequestMethod.GET)
    public Object list(@PathVariable String poolId) {
        PageEntity entity = this.beforeList();
        entity.setSpecialParam(poolId);
        PageModel<Clusters> pageModel = clustersBiz.findForPage(entity);
        PageList<Clusters> page = new PageList<Clusters>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

    @Override
    @ApiOperation(value = "获取服务器集群配置详细信息", response = Clusters.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String id) {
        Clusters clusters = clustersBiz.findById(Clusters.class, id);
        if (StrUtils.checkParam(clusters)){
            return BaseRestControl.tranReturnValue(clusters);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    @ApiOperation(value = "保存服务器集群配置", response = Clusters.class)
    @RequestMapping(method = RequestMethod.POST)
    public Object save(@RequestBody Clusters entity) {
        try {
            return clustersBiz.save(entity);
        } catch (Exception e) {
            this.exception(getClass(), e);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @Override
    @ApiOperation(value = "删除服务器集群配置", response = Clusters.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Object delete(@PathVariable String id) {
        try {
            return clustersBiz.delete(id);
        } catch (Exception e) {
            this.exception(getClass(), e);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @Override
    @ApiOperation(value = "修改服务器集群配置", response = Clusters.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Object update(@PathVariable String id, @RequestBody Clusters entity) throws IOException {
        try {
            return clustersBiz.update(id, entity);
        } catch (Exception e) {
            this.exception(getClass(), e);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }
}
