package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.VlbBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.VlbDao;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.Vlb;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Service("vlbBiz")
public class VlbBizImpl extends BaseBizImpl<Vlb> implements VlbBiz {

    @Resource
    private VlbDao vlbDao;

    @Override
    public PageModel<Vlb> findForPage(PageEntity entity) {
        return vlbDao.findForPage(entity);
    }
    
    @Override
    public JSONArray getVlbArray (CloudosClient client) {
        String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VLB);
        JSONArray jsonArray = HttpUtils.getArray(uri, "vlb", null, client);
        return jsonArray;
    }
    
    @Override
    public JSONObject getVlbJson (String cloudosId, CloudosClient client) {
        if (!StrUtils.checkParam(cloudosId)) {
            return null;
        }
        String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VLB_ACTION), cloudosId);
        JSONObject json = HttpUtils.getJson(uri, "vlb", client);
        return json;
    }
    
    @Override
    public Vlb getVlbByJson (JSONObject vbJson) {
        Vlb vlb = new Vlb();
        String vbCdId = vbJson.getString("id");
        String vbName = vbJson.getString("name");
        Integer status = vbJson.getInteger("status");
        String tenantId = vbJson.getString("tenantId");
        String description = vbJson.getString("description");
        String creator = vbJson.getString("creator");
        Integer throughPut = vbJson.getInteger("throughPut");
        String extra = vbJson.getString("extra");
        vlb.setCloudosId(vbCdId);
        vlb.setExtra(extra);
        vlb.setDescription(description);
        vlb.setName(vbName);
        vlb.setOwner(creator);
        vlb.setProjectId(tenantId);
        vlb.setStatus(status);
        vlb.setThroughPut(throughPut);
        return vlb;
    }
}
