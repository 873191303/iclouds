package com.h3c.iclouds.biz.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.biz.PfmValueBiz;
import com.h3c.iclouds.dao.PfmValue2HistoryDao;
import com.h3c.iclouds.dao.PfmValueDao;
import com.h3c.iclouds.po.CasItem;
import com.h3c.iclouds.po.PfmValue;
import com.h3c.iclouds.po.PfmValue2History;
import com.h3c.iclouds.utils.StrUtils;

@Service("pfmValueBiz")
public class PfmValueBizImpl extends BaseBizImpl<PfmValue> implements PfmValueBiz{

    @Resource(name = "baseDAO")
    private BaseDAO<CasItem> casItemDao;

    @Resource
    private PfmValueDao pfmValueDao;

    @Resource
    private PfmValue2HistoryDao pfmValue2HistoryDao;

    @Override
    public void save(String uuid, String resType, Float keyValue, String item, Date collectTime) {
        this.save(uuid, resType, keyValue, item, collectTime, false);
    }

    @Override
    public void save(String uuid, String resType, Float keyValue, String item, Date collectTime, boolean needCheck) {
        if (!StrUtils.checkParam(keyValue)){
            keyValue = (float)0;
        }
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("item", item);
        CasItem casItem = casItemDao.singleByClass(CasItem.class, queryMap);
        if (StrUtils.checkParam(casItem)){
            String itemId = casItem.getId();
            if (!StrUtils.checkParam(collectTime)){
                collectTime = new Date();
            }

            // 需要验证是否新数据
            if(needCheck) {
                queryMap.clear();
                queryMap.put("uuid", uuid);
                queryMap.put("itemId", itemId);
                queryMap.put("collectTime", collectTime);
                int count = pfmValueDao.count(PfmValue.class, queryMap);
                if(count != 0) {
                    return;
                }
            }

            queryMap.clear();
            queryMap.put("uuid", uuid);
            queryMap.put("itemId", itemId);
            queryMap.put("resType", resType);
            PfmValue pfmValue = pfmValueDao.singleByClass(PfmValue.class, queryMap);
            if (StrUtils.checkParam(pfmValue)){
                pfmValue.setKeyValue(keyValue);
                pfmValue.setUpdatedDate(new Date());
                pfmValue.setCollectTime(collectTime);
                pfmValueDao.update(pfmValue);
            }else {
                pfmValue = new PfmValue();
                pfmValue.setUuid(uuid);
                pfmValue.setResType(resType);
                pfmValue.setKeyValue(keyValue);
                pfmValue.setItemId(itemId);
                pfmValue.setCreatedDate(new Date());
                pfmValue.setUpdatedDate(new Date());
                pfmValue.setCollectTime(collectTime);
                pfmValueDao.add(pfmValue);
            }
            PfmValue2History pfmValue2History = new PfmValue2History();
            pfmValue2History.setUuid(uuid);
            pfmValue2History.setResType(resType);
            pfmValue2History.setKeyValue(keyValue);
            pfmValue2History.setItemId(itemId);
            pfmValue2History.setCreatedDate(new Date());
            pfmValue2History.setUpdatedDate(new Date());
            pfmValue2History.setCollectTime(collectTime);
            pfmValue2HistoryDao.add(pfmValue2History);
        }
    }

}
