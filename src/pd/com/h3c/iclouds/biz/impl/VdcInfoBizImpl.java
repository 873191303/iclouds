package com.h3c.iclouds.biz.impl;

import com.google.gson.Gson;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.TaskBiz;
import com.h3c.iclouds.biz.VdcInfoBiz;
import com.h3c.iclouds.common.TaskTypeProperty;
import com.h3c.iclouds.dao.VdcDao;
import com.h3c.iclouds.po.Vdc;
import com.h3c.iclouds.po.VdcInfo;
import com.h3c.iclouds.utils.VdcHandle;
import com.h3c.iclouds.utils.VdcValidator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2017/1/10.
 */
@Service("vdcInfoBiz")
public class VdcInfoBizImpl extends BaseBizImpl<VdcInfo> implements VdcInfoBiz {

    @Resource
    private VdcDao vdcDao;

    @Resource
    private TaskBiz taskBiz;

    @Override
    public void save(List<VdcInfo> list, Vdc vdc) {
        Map<Integer, String> sortMap = VdcValidator.getSortMap();//获取操作顺序
        for (int i = 0; i < sortMap.size(); i++) {
            String sequence = sortMap.get(i);
            for (VdcInfo vdcInfo : list) {
                if (sequence.equals(vdcInfo.getSequence())){//根据操作顺序操作数据
                    new VdcHandle().handleVdcInfo(vdcInfo, vdc.getId(), vdc.getProjectId());//本地操作
                }
            }
        }
        vdc.setLock(true);//锁定vdc
        vdcDao.update(vdc);
        taskBiz.save(vdc.getId(), TaskTypeProperty.VDC_VIEW_CREATE, new Gson().toJson(list));//保存到任务表
    }
}
