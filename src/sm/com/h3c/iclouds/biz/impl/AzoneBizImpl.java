package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.AzoneBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.AzoneDao;
import com.h3c.iclouds.dao.Project2AzoneDao;
import com.h3c.iclouds.po.Azone;
import com.h3c.iclouds.po.Project2Azone;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by yKF7317 on 2016/11/19.
 */
@Service("azoneBiz")
public class AzoneBizImpl extends BaseBizImpl<Azone> implements AzoneBiz {

    @Resource
    private AzoneDao azoneDao;
    
    @Resource
    private Project2AzoneDao project2AzoneDao;
    
    @Override
    public PageModel<Azone> findForPage(PageEntity entity) {
        return azoneDao.findForPage(entity);
    }
    
    @Override
    public String[] getAzoneIds(String projectId) {
        List<Project2Azone> lists = project2AzoneDao.findByPropertyName(Project2Azone.class, "id", projectId);
        if (null != lists && lists.size() > 0) {
            String[] ids = new String[lists.size()];
            for (Project2Azone project2Azone : lists) {
                String azoneId = project2Azone.getIyuUuid();
                ids[lists.indexOf(project2Azone)] = azoneId;
            }
            return ids;
        }
        return null;
    }
}
