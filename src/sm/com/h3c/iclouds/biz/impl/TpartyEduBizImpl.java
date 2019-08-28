package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.TpartyEduBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.TpartyEduDao;
import com.h3c.iclouds.po.TpartyEdu;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/19.
 */
@Service("tpartyEduBiz")
public class TpartyEduBizImpl extends BaseBizImpl<TpartyEdu> implements TpartyEduBiz {

    @Resource
    private TpartyEduDao tpartyEduDao;
    
    @Resource
    private SqlQueryBiz sqlQueryBiz;
    
    @Override
    public PageModel<TpartyEdu> findForPage(PageEntity entity) {
        return tpartyEduDao.findForPage(entity);
    }
    
    @Override
    public List<String> getProjectIds () {
        List<String> projectIds = new ArrayList<>();
        String sql = "SELECT p.id FROM iyun_keystone_project p WHERE p.id != '"+singleton.getRootProject()+"'AND NOT EXISTS" +
                "(SELECT edu.projectid FROM iyun_tparty_edu edu WHERE edu.projectid = p.id)";
        List<Map<String, Object>> result = sqlQueryBiz.queryBySql(sql);
        if (StrUtils.checkCollection(result)) {
            for (Map<String, Object> map : result) {
                String projectId = StrUtils.tranString(map.get("id"));
                projectIds.add(projectId);
            }
        }
        return projectIds;
    }
}
