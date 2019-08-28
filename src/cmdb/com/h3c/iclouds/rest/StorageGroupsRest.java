package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.StorageClustersBiz;
import com.h3c.iclouds.biz.StorageGroupsBiz;
import com.h3c.iclouds.biz.StorageManageBiz;
import com.h3c.iclouds.biz.StorageVolumsBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.StorageClusters;
import com.h3c.iclouds.po.StorageGroups;
import com.h3c.iclouds.po.StorageManage;
import com.h3c.iclouds.po.StorageVolums;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * Created by yKF7317 on 2016/11/14.
 */
@Api(value = "资源配置存储管理组", description = "资源配置存储管理组")
@RestController
@RequestMapping("/storage/groups")
public class StorageGroupsRest extends BaseRest<StorageGroups> {

    @Resource
    private StorageGroupsBiz storageGroupsBiz;

    @Resource
    private StorageClustersBiz storageClustersBiz;

    @Resource
    private StorageVolumsBiz storageVolumsBiz;

    @Resource
    private StorageManageBiz storageManageBiz;

    @Override
    @ApiOperation(value = "获取存储管理组信息列表", response = StorageGroups.class)
    @RequestMapping(method = RequestMethod.GET)
    public Object list() {
        PageEntity pageEntity = this.beforeList();
        PageModel<StorageGroups> pageModel = storageGroupsBiz.findForPage(pageEntity);
        PageList<StorageGroups> page = new PageList<>(pageModel, pageEntity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }


    @ApiOperation(value = "获取存储管理组详细信息", response = StorageGroups.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String id) {
        StorageGroups storageGroups = storageGroupsBiz.findById(StorageGroups.class, id);
        if (storageGroups != null){
            return BaseRestControl.tranReturnValue(storageGroups);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    @ApiOperation(value = "删除存储管理组", response = StorageGroups.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Object delete(@PathVariable String id) {
        StorageGroups storageGroups = storageGroupsBiz.findById(StorageGroups.class, id);
        if (null == storageGroups){
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        List<StorageClusters> clusterses = storageClustersBiz.findByPropertyName(StorageClusters.class, "gid", id);
        if (null != clusterses && clusterses.size() > 0){
            return BaseRestControl.tranReturnValue(ResultType.pools_used);
        }
        storageGroupsBiz.delete(storageGroups);
        return BaseRestControl.tranReturnValue(ResultType.success);
    }

    @Override
    @ApiOperation(value = "新增存储管理组", response = StorageGroups.class)
    @RequestMapping(method = RequestMethod.POST)
    public Object save(@RequestBody StorageGroups entity) {
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);
        if(validatorMap.isEmpty()) {
            entity.createdUser(this.getLoginUser());
            String id = UUID.randomUUID().toString();
            entity.setId(id);
            try {
                storageGroupsBiz.add(entity);
                return BaseRestControl.tranReturnValue(ResultType.success, entity);
            } catch (Exception e) {
                this.exception(getClass(), e);
                return BaseRestControl.tranReturnValue(ResultType.failure);
            }
        }
        return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
    }

    @Override
    @ApiOperation(value = "修改存储管理组", response = StorageGroups.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Object update(@PathVariable String id, @RequestBody StorageGroups entity) throws IOException {
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);
        if(validatorMap.isEmpty()) {
            StorageGroups storageGroups = storageGroupsBiz.findById(StorageGroups.class, id);
            if (storageGroups != null){
                InvokeSetForm.copyFormProperties(entity, storageGroups);
                storageGroups.updatedUser(this.getLoginUser());
                storageGroupsBiz.update(storageGroups);
                return BaseRestControl.tranReturnValue(ResultType.success, entity);
            }
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
    }

    @ApiOperation(value = "获取当前存储管理组下的集群列表", response = StorageClusters.class)
    @RequestMapping(value = "/{id}/clusters", method = RequestMethod.GET)
    public Object clustersList(@PathVariable String id){
        PageEntity entity = this.beforeList();
        entity.setSpecialParam(id);//设置查询条件
        PageModel<StorageClusters> pageModel = storageClustersBiz.findForPage(entity);
        PageList<StorageClusters> page = new PageList<StorageClusters>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

    @ApiOperation(value = "获取当前存储管理组下的管理器列表", response = StorageManage.class)
    @RequestMapping(value = "/{id}/manage", method = RequestMethod.GET)
    public Object managerList(@PathVariable String id){
        PageEntity entity = this.beforeList();
        entity.setSpecialParam(id);//设置查询条件
        PageModel<StorageManage> pageModel = storageManageBiz.findForPage(entity);
        PageList<StorageManage> page = new PageList<StorageManage>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

    @ApiOperation(value = "获取当前存储管理组下的挂载列表", response = StorageVolums.class)
    @RequestMapping(value = "/{id}/volums", method = RequestMethod.GET)
    public Object volumsList(@PathVariable String id){
        List<StorageClusters> clusterses = storageClustersBiz.findByPropertyName(StorageClusters.class, "gid", id);
        PageEntity entity = this.beforeList();
        if (null != clusterses && clusterses.size() > 0){
            String [] ids = new String[clusterses.size()];
            for (int i = 0; i < clusterses.size(); i++) {//遍历当前管理组下的集群列表
                StorageClusters clusters = clusterses.get(i);
                String cid = clusters.getId();
                ids[i] = cid;//将所有归属该管理组的集群id放入数组中
            }
            entity.setSpecialParams(ids);//添加查询条件
        }
        PageModel<StorageVolums> pageModel = storageVolumsBiz.findForPage(entity);
        PageList<StorageVolums> page = new PageList<StorageVolums>(pageModel, entity.getsEcho());
        page = storageVolumsBiz.transPage(page);
        return BaseRestControl.tranReturnValue(page);
    }

    @ApiOperation(value = "在当前存储管理组下删除管理器", response = StorageManage.class)
    @RequestMapping(value = "/{id}/manage/{manaId}", method = RequestMethod.DELETE)
    public Object deleteManage(@PathVariable String manaId) {
        StorageManage manage = storageManageBiz.findById(StorageManage.class, manaId);
        if (null != manage){
            storageManageBiz.delete(manage);
            return BaseRestControl.tranReturnValue(ResultType.success);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @ApiOperation(value = "在当前存储管理组下增加管理器", response = StorageManage.class)
    @RequestMapping(value = "/{id}/manage", method = RequestMethod.POST)
    public Object addManage(@PathVariable String id, @RequestBody StorageManage entity) {
        entity.setGid(id);
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);
        if(validatorMap.isEmpty()) {
            entity.createdUser(this.getLoginUser());
            String mid = UUID.randomUUID().toString();
            entity.setId(mid);
            try {
                storageManageBiz.add(entity);
                return BaseRestControl.tranReturnValue(ResultType.success, entity);
            } catch (Exception e) {
                this.exception(getClass(), e);
                return BaseRestControl.tranReturnValue(ResultType.failure);
            }
        }
        return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
    }

    @ApiOperation(value = "在当前管理组下添加集群（绑定集群）", response = StorageClusters.class)
    @RequestMapping(value = "/{id}/linkClusters", method = RequestMethod.POST)
    public Object linkClusters(@PathVariable String id, @RequestBody List<String> ids){
        if (null != ids && ids.size() > 0){
            for (int i = 0; i < ids.size(); i++) {
                String cid = ids.get(i);
                StorageClusters cluster = storageClustersBiz.findById(StorageClusters.class, cid);
                cluster.setGid(id);
                storageClustersBiz.update(cluster);
            }
            return BaseRestControl.tranReturnValue(ResultType.success);
        }
        return BaseRestControl.tranReturnValue(ResultType.failure);
    }

    @ApiOperation(value = "在当前管理组下删除集群（解除绑定）", response = StorageClusters.class)
    @RequestMapping(value = "/{id}/unlinkClusters", method = RequestMethod.POST)
    public Object unlinkClusters(@PathVariable String id, @RequestBody List<String> ids){
        if (null != ids && ids.size() > 0){
            for (int i = 0; i < ids.size(); i++) {
                String cid = ids.get(i);
                StorageClusters cluster = storageClustersBiz.findById(StorageClusters.class, cid);
                cluster.setGid(null);
                storageClustersBiz.update(cluster);
            }
            return BaseRestControl.tranReturnValue(ResultType.success);
        }
        return BaseRestControl.tranReturnValue(ResultType.failure);
    }
}
