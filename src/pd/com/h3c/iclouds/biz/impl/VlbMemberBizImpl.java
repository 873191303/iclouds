package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.VlbMemberBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.VlbMemberDao;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.VlbMember;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Service("vlbMemberBiz")
public class VlbMemberBizImpl extends BaseBizImpl<VlbMember> implements VlbMemberBiz {

    @Resource
    private VlbMemberDao vlbMemberDao;

    @Override
    public PageModel<VlbMember> findForPage(PageEntity entity) {
        return vlbMemberDao.findForPage(entity);
    }
    
    @Override
    public JSONObject getMemberJson (String cloudosId, CloudosClient client) {
        if (!StrUtils.checkParam(cloudosId)) {
            return null;
        }
        String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VLBMEMBER_ACTION), cloudosId);
        JSONObject json = HttpUtils.getJson(uri, "member", client);
        return json;
    }
    
    @Override
    public VlbMember getMember (JSONObject memJson) {
        VlbMember member = new VlbMember();
        String memCdId = memJson.getString("id");
        Integer protocolPort = memJson.getInteger("protocol_port");
        String tenantId = memJson.getString("tenant_id");
        String address = memJson.getString("address");
        Boolean stateUp = memJson.getBoolean("admin_state_up");
        Integer weight = memJson.getInteger("weight");
        member.setTenantId(tenantId);
        member.setAddress(address);
        member.setAdminStateUp(stateUp);
        member.setProtocolPort(protocolPort);
        member.setWeight(weight);
        member.setCloudosId(memCdId);
        return member;
    }
}
