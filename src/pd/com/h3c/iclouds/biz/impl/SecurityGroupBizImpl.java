package com.h3c.iclouds.biz.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.SecurityGroupBiz;
import com.h3c.iclouds.biz.TaskBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.SecurityGroupDao;
import com.h3c.iclouds.po.SecurityGroup;

/**
 * Created by yKF7317 on 2016/11/28.
 */
@Service("securityGroupBiz")
public class SecurityGroupBizImpl extends BaseBizImpl<SecurityGroup> implements SecurityGroupBiz {

    @Resource
    private SecurityGroupDao securityGroupDao;
    
    @Resource
    private TaskBiz taskBiz;

    @Override
    public PageModel<SecurityGroup> findForPage(PageEntity entity) {
        return securityGroupDao.findForPage(entity);
    }
}
