package com.h3c.iclouds.biz.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.VdcBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.VdcDao;
import com.h3c.iclouds.po.Vdc;
import com.h3c.iclouds.utils.VdcHandle;
import com.h3c.iclouds.validate.ValidatorUtils;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Service("vdcBiz")
public class VdcBizImpl extends BaseBizImpl<Vdc> implements VdcBiz {



    @Resource
	private VdcDao vdcDao;

	@Resource
    private ProjectBiz projectBiz;

    @Override
    public PageModel<Vdc> findForPage(PageEntity entity) {
        return vdcDao.findForPage(entity);
    }

    @Override
    public Map<String, Object> save(Vdc entity, String name) {
        if (!StrUtils.checkParam(entity)){
            entity = new Vdc();
        }
        String projectId = this.getProjectId();
        List<Vdc> vdcs = vdcDao.findByPropertyName(Vdc.class, "projectId", projectId);
        if (null != vdcs && vdcs.size() > 0){//检查租户是否已经创建vdc（租户只有一个vdc）
            return BaseRestControl.tranReturnValue(ResultType.vdc_already_created);
        }
        if (StrUtils.checkParam(name)){
            entity.setName(name);
        }
        entity.setUuid(UUID.randomUUID().toString());
        entity.setProjectId(projectId);
        entity.createdUser(this.getLoginUser());
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);
        if (!validatorMap.isEmpty()){
            return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
        }
        String vdcId = vdcDao.add(entity);
        new VdcHandle().saveViewAndItem(vdcId, entity.getUuid(), entity.getName(), vdcId, null, "0", 0, ConfigProperty.TASK_STATUS3_END_SUCCESS);//创建vdc在vdc视图和视图对象里面的数据
        SessionBean sessionBean = this.getSessionBean();
        sessionBean.setVdcId(vdcId);
        return BaseRestControl.tranReturnValue(ResultType.success, entity);

    }

    @Override
    public void updateItem(Vdc entity, Vdc vdc) {
        if (!entity.getName().equals(vdc.getName())){//如果名称改变同步更新到视图对象表
            new VdcHandle().updateItemName(entity.getId(), vdc.getName());
        }
    }

    public Vdc getVdc(String projectId){
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("projectId", projectId);
        Vdc vdc = this.singleByClass(Vdc.class, queryMap);
        if (!StrUtils.checkParam(vdc)){
            Project project = projectBiz.findById(Project.class, projectId);
            if(project == null) {
            	return null;
            }
            vdc = new Vdc();
            vdc.setName(project.getName() + "-vdc");
            vdc.setUuid(UUID.randomUUID().toString());
            vdc.setProjectId(projectId);
            vdc.setVdcType("1");
            vdc.createdUser(this.getLoginUser());
            vdc.setLock(false);
            String vdcId = vdcDao.add(vdc);
            new VdcHandle().saveViewAndItem(vdcId, vdc.getUuid(), vdc.getName(), vdcId, "1", "0", 0,
                    ConfigProperty.TASK_STATUS3_END_SUCCESS);//创建vdc在vdc视图和视图对象里面的数据
//            new VdcHandle().saveViewAndItem(vdcId, vdc.getUuid(), vdc.getName(), vdcId, null, "0", 0,
//                    ConfigProperty.TASK_STATUS3_END_SUCCESS);//创建vdc在vdc视图和视图对象里面的数据
            SessionBean sessionBean = this.getSessionBean();
            sessionBean.setVdcId(vdcId);
        }
        return vdc;
    }

    public boolean checkLock(String projectId){
        Vdc vdc = getVdc(projectId);
        if (StrUtils.checkParam(vdc.getUserId())){
            return false;
        }
        return true;
    }

    @Override
    public void clearLock(Vdc vdc) {
        vdc.setLock(false);
        vdc.setUserId(null);
        vdc.setVersion(null);
        vdc.setSessionId(null);
        vdcDao.update(vdc);
    }
}
