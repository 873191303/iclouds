package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.MeasureDetailBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.business.Instance;
import com.h3c.iclouds.po.business.MeasureDetail;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * Created by yKF7317 on 2017/1/19.
 */
@RestController
@RequestMapping("/measureDetail")
@Api(value = "云管理云资源对象计量账单明细", description = "云管理云资源对象计量账单明细")
public class MeasureDetailsRest extends BaseRest<MeasureDetail>{

    @Resource
    private MeasureDetailBiz measureDetailBiz;

    @Resource(name = "baseDAO")
    private BaseDAO<Instance> instanceDao;

    @Override
    @ApiOperation(value = "获取云管理云资源对象计量账单明细列表")
    @RequestMapping(method = RequestMethod.GET)
    public Object list() {
        PageEntity entity = this.beforeList();
        PageModel<MeasureDetail> pageModel = measureDetailBiz.findForPage(entity);
        PageList<MeasureDetail> page = new PageList<MeasureDetail>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

    @ApiOperation(value = "获取某个资源的云管理云资源对象计量账单明细列表")
    @RequestMapping(value = "/{resourceId}/list", method = RequestMethod.GET)
    public Object list(@PathVariable String resourceId) {
        PageEntity entity = this.beforeList();
        List<Instance> instances = instanceDao.findByPropertyName(Instance.class, "instance", resourceId);
        if (StrUtils.checkParam(instances)){
            Instance instance = instances.get(0);
            entity.setSpecialParam(instance.getId());
        }else {
            entity.setSpecialParam("null");
        }
        PageModel<MeasureDetail> pageModel = measureDetailBiz.findForPage(entity);
        PageList<MeasureDetail> page = new PageList<MeasureDetail>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

    @Override
    @ApiOperation(value = "获取云管理云资源对象计量账单明细详细信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String id) {
        try {
            MeasureDetail measureDetail = measureDetailBiz.findById(MeasureDetail.class, id);
            if (!StrUtils.checkParam(measureDetail)){
                return BaseRestControl.tranReturnValue(ResultType.deleted);
            }
            return BaseRestControl.tranReturnValue(measureDetail);
        } catch (Exception e) {
            this.exception(MeasureDetail.class, e, id);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @Override
    @ApiOperation(value = "删除云管理云资源对象计量账单明细")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Object delete(@PathVariable String id) {
        return null;
    }

    @Override
    @ApiOperation(value = "保存云管理云资源对象计量账单明细")
    @RequestMapping(method = RequestMethod.POST)
    public Object save(@RequestBody MeasureDetail entity) {
        return null;
    }

    @Override
    @ApiOperation(value = "修改云管理云资源对象计量账单明细")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Object update(@PathVariable String id, @RequestBody MeasureDetail entity) throws IOException {
        return null;
    }
}
