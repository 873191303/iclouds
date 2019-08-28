package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.NetPortsBiz;
import com.h3c.iclouds.biz.StorageClustersBiz;
import com.h3c.iclouds.biz.StorageGroups2ItemsBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.NetPorts;
import com.h3c.iclouds.po.StorageClusters;
import com.h3c.iclouds.po.StorageGroups2Items;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by yKF7317 on 2016/11/14.
 */
@Api(value = "资源配置存储集群配置", description = "资源配置存储集群配置")
@RestController
@RequestMapping("/storage/clusters")
public class StorageClustersRest extends BaseRest<StorageClusters> {

    @Resource
    private StorageClustersBiz storageClustersBiz;

    @Resource
    private StorageGroups2ItemsBiz storageGroups2ItemsBiz;

    @Resource
    private NetPortsBiz netPortsBiz;
    
    @Resource
    private StorageGroups2ItemsBiz groups2ItemsBiz;
    
    @Override
    @ApiOperation(value = "获取所有存储信息列表", response = StorageClusters.class)
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Object list() {
        PageEntity entity = this.beforeList();
        PageModel<StorageClusters> pageModel = storageClustersBiz.findForPage(entity);
        PageList<StorageClusters> page = new PageList<>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }
    
    @ApiOperation(value = "获取集群存储信息列表", response = StorageClusters.class)
    @RequestMapping(method = RequestMethod.GET)
    public Object clusterList() {
        PageEntity entity = this.beforeList();
        entity.setSpecialParam("cluster");
        PageModel<StorageClusters> pageModel = storageClustersBiz.findForPage(entity);
        PageList<StorageClusters> page = new PageList<>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }
    
    @ApiOperation(value = "获取独立存储集群信息列表", response = StorageClusters.class)
    @RequestMapping(value = "/alone", method = RequestMethod.GET)
    public Object aloneList() {
        PageEntity entity = this.beforeList();
        entity.setSpecialParam("alone");
        PageModel<StorageClusters> pageModel = storageClustersBiz.findForPage(entity);
        PageList<StorageClusters> page = new PageList<>(pageModel, entity.getsEcho());
        List<StorageClusters> clusters = page.getAaData();
        if (StrUtils.checkCollection(clusters)){
            for (StorageClusters cluster : clusters) {
                Map<String, Object> queryMap = new HashMap<>();
                queryMap.put("cid", cluster.getId());
                StorageGroups2Items groups2Items = groups2ItemsBiz.singleByClass(StorageGroups2Items.class, queryMap);
                if (StrUtils.checkParam(groups2Items)){
                    cluster.setStatus(groups2Items.getStatus());
                    cluster.setSerial(groups2Items.getSerial());
                }
            }
        }
        return BaseRestControl.tranReturnValue(page);
    }

    @Override
    @ApiOperation(value = "获取存储集群详细信息", response = StorageClusters.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String id) {
        StorageClusters storageClusters = storageClustersBiz.findById(StorageClusters.class, id);
        if (null != storageClusters){
            return BaseRestControl.tranReturnValue(storageClusters);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    @ApiOperation(value = "删除存储集群或独立存储", response = StorageClusters.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Object delete(@PathVariable String id) {
        StorageClusters storageClusters = storageClustersBiz.findById(StorageClusters.class, id);
        if (null != storageClusters){
            List<StorageGroups2Items> storageGroups2Itemses = storageGroups2ItemsBiz.findByPropertyName(StorageGroups2Items.class, "cid", id);
            if (ConfigProperty.NO.equals(storageClusters.getType()) && StrUtils.checkCollection(storageGroups2Itemses))
            {//当集群存储有子存储归属于当前集群下时，不能删除
                return BaseRestControl.tranReturnValue(ResultType.clusters_used);
            }
            storageClustersBiz.deleteCluster(storageClusters);
            return BaseRestControl.tranReturnValue(ResultType.success);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    @ApiOperation(value = "新增存储集群", response = StorageClusters.class)
    @RequestMapping(method = RequestMethod.POST)
    public Object save(@RequestBody StorageClusters entity) {
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);
        if(!validatorMap.isEmpty()) {
            return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
        }
        entity.createdUser(this.getLoginUser());
        String id = UUID.randomUUID().toString();
        entity.setId(id);
        try {
            storageClustersBiz.add(entity);
            return BaseRestControl.tranReturnValue(ResultType.success, entity);
        } catch (Exception e) {
            this.exception(StorageClusters.class, e);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @Override
    @ApiOperation(value = "修改存储集群", response = StorageClusters.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Object update(@PathVariable String id, @RequestBody StorageClusters entity) throws IOException {
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);
        if(!validatorMap.isEmpty()) {
            return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
        }
        StorageClusters storageClusters = storageClustersBiz.findById(StorageClusters.class, id);
        if (storageClusters == null){
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        InvokeSetForm.copyFormProperties(entity, storageClusters);
        storageClusters.updatedUser(this.getLoginUser());
        storageClustersBiz.update(storageClusters);
        return BaseRestControl.tranReturnValue(ResultType.success, storageClusters);
    }

    @ApiOperation(value = "获取当前存储集群下的存储列表", response = StorageGroups2Items.class)
    @RequestMapping(value = "/{id}/groups2Items", method = RequestMethod.GET)
    public Object itemList(@PathVariable String id){
        StorageClusters storageClusters = storageClustersBiz.findById(StorageClusters.class, id);
        if (null == storageClusters){
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        if (storageClusters.getType().equals(ConfigProperty.YES)) {//独立存储没有子存储
            return BaseRestControl.tranReturnValue(Collections.emptyList());
        }
        PageEntity entity = this.beforeList();
        entity.setSpecialParam(id);
        PageModel<StorageGroups2Items> pageModel = storageGroups2ItemsBiz.findForPage(entity);
        PageList<StorageGroups2Items> page = new PageList<>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

    @ApiOperation(value = "在当前存储集群下添加存储(绑定存储)", response = StorageGroups2Items.class)
    @RequestMapping(value = "/{id}/linkGroups2Items", method = RequestMethod.POST)
    public Object linkGroups2Items(@PathVariable String id, @RequestBody List<String> ids){
        return storageClustersBiz.optionGroups2Item(id, ids, true);
    }

    @ApiOperation(value = "在当前存储集群下删除存储(解除绑定)", response = StorageGroups2Items.class)
    @RequestMapping(value = "/{id}/unlinkGroups2Items", method = RequestMethod.POST)
    public Object unlinkGroups2Items(@PathVariable String id, @RequestBody List<String> ids){
        return storageClustersBiz.optionGroups2Item(id, ids, false);
    }
    
    @ApiOperation(value = "在当前存储集群下添加子存储资产(绑定存储资产)", response = StorageGroups2Items.class)
    @RequestMapping(value = "/{id}/linkMasters", method = RequestMethod.POST)
    public Object linkMasters(@PathVariable String id, @RequestBody List<String> ids){
        return storageClustersBiz.optionMaster(id, ids);
    }

    @ApiOperation(value = "获取当前存储集群下的网口列表", response = NetPorts.class)
    @RequestMapping(value = "/{id}/netPorts", method = RequestMethod.GET)
    public Object netPortList(@PathVariable String id){
        StorageClusters storageClusters = storageClustersBiz.findById(StorageClusters.class, id);
        if (StrUtils.checkParam(storageClusters)){
            PageEntity entity = this.beforeList();
            List<StorageGroups2Items> storageGroups2Itemses = storageGroups2ItemsBiz.findByPropertyName(StorageGroups2Items.class, "cid", id);
            if (StrUtils.checkParam(storageGroups2Itemses)){//当有存储归属于当前集群下时，不能删除
                String [] masterIds = new String[storageGroups2Itemses.size()];
                for (int i = 0; i < storageGroups2Itemses.size(); i++) {
                    masterIds[i] = storageGroups2Itemses.get(i).getMasterId();
                }
                entity.setSpecialParams(masterIds);
            }else {
                entity.setSpecialParams(new String[]{"null"});
            }
            PageModel<NetPorts> pageModel = netPortsBiz.findForPage(entity);
            PageList<NetPorts> page = new PageList<NetPorts>(pageModel, entity.getsEcho());
            return BaseRestControl.tranReturnValue(page);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }
    
}
