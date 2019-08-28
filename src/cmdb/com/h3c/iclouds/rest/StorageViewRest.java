package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.biz.StorageVolumsBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.po.StorageVolums;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.StorageViewBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.StorageRelation;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * Created by yKF7317 on 2017/2/18.
 */
@Api(value = "资源配存储池视图", description = "资源配存储池视图")
@RestController
@RequestMapping("/storageView")
public class StorageViewRest extends BaseRest<StorageRelation> {

    @Resource
    private StorageViewBiz storageViewBiz;

    @Resource(name = "baseDAO")
    private BaseDAO<StorageRelation> storageRelationDao;

    @Resource
    private StorageVolumsBiz storageVolumsBiz;

    @Override
    @ApiOperation(value = "获取存储池视图信息")
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public Object list() {
        List<StorageRelation> storageRelation = storageViewBiz.getView();
        return BaseRestControl.tranReturnValue(storageRelation);
    }

    @Override
    @ApiOperation(value = "获取管理组下存储池视图信息")
    @RequestMapping(value = "/view/{groupId}", method = RequestMethod.GET)
    public Object get(@PathVariable String groupId) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("netId", groupId);
        StorageRelation groupRelation = storageRelationDao.singleByClass(StorageRelation.class, queryMap);
        if (StrUtils.checkParam(groupRelation)){
            List<StorageRelation> clusterRelations = storageRelationDao.findByPropertyName(StorageRelation.class,
                    "previous", groupRelation.getNetId());
            if (StrUtils.checkParam(clusterRelations)){
                List<StorageRelation> groupChildren = new ArrayList<>();
                for (StorageRelation clusterRelation : clusterRelations) {
                    groupChildren.add(clusterRelation);
                    List<StorageRelation> itemRelations = storageRelationDao.findByPropertyName(StorageRelation.class,
                            "previous", clusterRelation.getNetId());
                    if (StrUtils.checkParam(clusterRelations)){
                        List<StorageRelation> clusterChildren = new ArrayList<>();
                        for (StorageRelation itemRelation : itemRelations) {
                            clusterChildren.add(itemRelation);
                        }
                        clusterRelation.setChildren(itemRelations);
                        clusterRelation.setChildCount(itemRelations.size());
                        if (ConfigProperty.STORAGE_ALONE.equals(clusterRelation.getPoolType()) || ConfigProperty.STORAGE_CLUSTER.equals
                                (clusterRelation.getPoolType())){
                            int count = storageVolumsBiz.findCountByPropertyName(StorageVolums.class, "sid",
                                    clusterRelation.getNetId());
                            clusterRelation.setVolumnCount(count);
                        }
                    }
                }
                groupRelation.setChildren(groupChildren);
                groupRelation.setChildCount(groupChildren.size());
            }
            return BaseRestControl.tranReturnValue(groupRelation);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    public Object delete(@PathVariable String id) {
        return null;
    }

    @Override
    public Object save(@RequestBody StorageRelation entity) {
        return null;
    }

    @Override
    public Object update(@PathVariable String id, @RequestBody StorageRelation entity) throws IOException {
        return null;
    }
}
