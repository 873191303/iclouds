package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.VdcBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Vdc;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Api(value = "云管理虚拟数据中心")
@RestController
@RequestMapping("/vdc")
public class VdcRest extends BaseRest<Vdc> {

    @Resource
    private VdcBiz vdcBiz;

    @Resource
    private ProjectBiz projectBiz;

    @ApiOperation(value = "获取某个租户下的云管理虚拟数据中心数据", response = Vdc.class)
    @RequestMapping(method = RequestMethod.GET)
    public Object tenantList(){
        String projectId = this.getProjectId();
        List<Vdc> vdcs = vdcBiz.findByPropertyName(Vdc.class, "projectId", projectId);
        if (StrUtils.checkParam(vdcs)){
            return BaseRestControl.tranReturnValue(vdcs.get(0));
        }else {
            Vdc vdc = new Vdc();
            Project project = projectBiz.findById(Project.class, projectId);
            if (StrUtils.checkParam(project)){
                vdcBiz.save(vdc, project.getName()+"-vdc");
                return BaseRestControl.tranReturnValue(vdc);
            }
            return BaseRestControl.tranReturnValue(ResultType.tenant_not_exist);
        }
    }

    @Override
    @ApiOperation(value = "获取云管理虚拟数据中心详细信息", response = Vdc.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String id) {
        Vdc vdc = vdcBiz.findById(Vdc.class, id);
        if (null != vdc){
            return BaseRestControl.tranReturnValue(vdc);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
//    @ApiOperation(value = "保存云管理虚拟数据中心", response = Vdc.class)
//    @RequestMapping(method = RequestMethod.POST)
//    @Perms(value = "vdc.ope.vdc")
    public Object save(@RequestBody Vdc entity) {
        try {
            return vdcBiz.save(entity, null);
        } catch (Exception e) {
            this.exception(Vdc.class, e);
            if(e instanceof MessageException) {
                return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
            }
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @Override
    public Object update(@PathVariable String id, @RequestBody Vdc entity) throws IOException {
        return null;
    }

    public Object list() {
        return null;
    }

    public Object delete(@PathVariable String id) {
        return null;
    }


}
