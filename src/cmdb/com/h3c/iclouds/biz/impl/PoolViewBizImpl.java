package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.biz.PoolViewBiz;
import com.h3c.iclouds.biz.Server2OveBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.po.PoolItem;
import com.h3c.iclouds.po.PoolRelation;
import com.h3c.iclouds.po.PoolView;
import com.h3c.iclouds.po.Server2Ove;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by yKF7317 on 2017/2/20.
 */
@Service("poolViewBiz")
public class PoolViewBizImpl extends BaseBizImpl<PoolView> implements PoolViewBiz {

    @Resource(name = "baseDAO")
    private BaseDAO<PoolRelation> poolRelationDao;

    @Resource(name = "baseDAO")
    private BaseDAO<PoolItem> poolItemDao;

    @Resource
    private Server2OveBiz server2OveBiz;

    @Override
    public List<PoolRelation> getView() {
        List<PoolRelation> poolRelationList = poolRelationDao.getAll(PoolRelation.class);
        List<PoolRelation> relations = new ArrayList<>();
        if (StrUtils.checkParam(poolRelationList)){
            for (PoolRelation poolRelation : poolRelationList) {
                if (!StrUtils.checkParam(poolRelation.getPrevious())){
                    sort(poolRelation, poolRelationList);
                    relations.add(poolRelation);
                }
            }
        }
        return relations;
    }

    public void saveItemAndRelation(String objId, String objName, String type, String uuid, String
            previousId){
        if (!StrUtils.checkParam(uuid)){
            uuid = UUID.randomUUID().toString();
        }
        PoolRelation poolRelation = new PoolRelation();
        PoolItem poolItem = new PoolItem();
        poolItem.setId(objId);
        poolItem.setName(objName);
        poolItem.setItemType(type);
        poolItem.setUuid(uuid);
        poolItemDao.add(poolItem);
        poolRelation.setHpoolId(objId);
        poolRelation.setPrevious(previousId);
        poolRelationDao.add(poolRelation);
    }

    public void updateItemAndRelation(String objId, String objName, String
            previousId){
        if (StrUtils.checkParam(objName)){
            PoolItem poolItem = poolItemDao.findById(PoolItem.class, objId);
            poolItem.setName(objName);
            poolItemDao.update(poolItem);
        }
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("hpoolId", objId);
        PoolRelation poolRelation = poolRelationDao.singleByClass(PoolRelation.class, queryMap);
        String previous = poolRelation.getPrevious();
        if ((StrUtils.checkParam(previousId) && !previousId.equals(previous)) || (!StrUtils.checkParam(previousId) &&
                StrUtils.checkParam(previous))){
            poolRelation.setPrevious(previousId);
            poolRelationDao.update(poolRelation);
        }
    }

    public void deleteItemAndRelation(String objId){
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("hpoolId", objId);
        PoolRelation poolRelation = poolRelationDao.singleByClass(PoolRelation.class, queryMap);
        if (StrUtils.checkParam(poolRelation)) {
            poolRelationDao.delete(poolRelation);
        }
        PoolItem poolItem = poolItemDao.findById(PoolItem.class, objId);
        if (StrUtils.checkParam(poolItem)) {
            poolItemDao.delete(poolItem);
        }
    }

    public void sort(PoolRelation poolRelation, List<PoolRelation> poolRelationList){
        List<PoolRelation> relations = new ArrayList<>();
        for (PoolRelation relation : poolRelationList) {
            if (poolRelation.getHpoolId().equals(relation.getPrevious())){
                sort(relation, poolRelationList);
                relations.add(relation);
            }
        }
        poolRelation.setChildren(relations);
        poolRelation.setChildCount(relations.size());
        if (ConfigProperty.POOLVIEW_SERVER2OVE_TYPE.equals(poolRelation.getPoolType())){
            Server2Ove server2Ove = server2OveBiz.findById(Server2Ove.class, poolRelation.getHpoolId());
            if (StrUtils.checkParam(server2Ove)){
                server2OveBiz.addParam(server2Ove);
                poolRelation.setChildCount(server2Ove.getVms());
                poolRelation.setProjectCount(server2Ove.getProjectCount());
            }
        }
    }
}
