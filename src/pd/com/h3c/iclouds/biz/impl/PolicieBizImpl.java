package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.PolicieBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.PolicieDao;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.Policie;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Service("policieBiz")
public class PolicieBizImpl extends BaseBizImpl<Policie> implements PolicieBiz {

    @Resource
    private PolicieDao policieDao;

    @Override
    public PageModel<Policie> findForPage(PageEntity entity) {
        return policieDao.findForPage(entity);
    }
    
    @Override
    public JSONObject getPolicyJson (String cloudosId, CloudosClient client) {
        if (!StrUtils.checkParam(cloudosId)) {
            return null;
        }
        String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_POLICIE_ACTION), cloudosId);
        JSONObject json = HttpUtils.getJson(uri, "firewall_policy", client);
        return json;
    }
    
    @Override
    public Policie getPolicyByJson (JSONObject policyJson) {
        Policie policy = new Policie();
        String pyCdId = policyJson.getString("id");
        String pyName = policyJson.getString("name");
        String pyDescription = policyJson.getString("description");
        String tenantId = policyJson.getString("tenant_id");
        Boolean audited = policyJson.getBoolean("audited");
        Boolean shared = policyJson.getBoolean("shared");
        policy.setCloudosId(pyCdId);
        policy.setTenantId(tenantId);
        policy.setDescription(pyDescription);
        policy.setAudited(audited);
        policy.setName(pyName);
        policy.setShared(shared);
        return policy;
    }
}
