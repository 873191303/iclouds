package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.AzoneBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.Azone;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/19.
 */
@Api(value = "云管理可用域", description = "云管理可用域")
@RestController
@RequestMapping("/azone")
public class AzoneRest extends BaseRest<Azone> {

    @Resource
    private AzoneBiz azoneBiz;

    @Override
    @ApiOperation(value = "获取创建租户的可用域信息列表", response = Azone.class)
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Object list() {
        if (!this.getSessionBean().getSuperUser()) {
            return BaseRestControl.tranReturnValue(Collections.EMPTY_LIST);
        }
        String type = this.request.getParameter("type");
        if(StrUtils.checkParam(type)) {
            return BaseRestControl.tranReturnValue(azoneBiz.findByPropertyName(Azone.class, "resourceType", type));
        }
        return BaseRestControl.tranReturnValue(azoneBiz.getAll(Azone.class));
    }
    
    @ApiOperation(value = "获取创建云主机和云硬盘的可用域信息列表", response = Azone.class)
    @RequestMapping(value = "/create/{type}", method = RequestMethod.GET)
    public Object list(@PathVariable String type) {
        String tenantId = request.getParameter("projectId");
        if (this.getSessionBean().getSuperUser()) {//管理员获取租户的列表，非管理员只能获取自己的列表
            if (!StrUtils.checkParam(tenantId)) {
                tenantId = this.getProjectId();
            }
        } else {
            tenantId = this.getProjectId();
        }
        Map<String, Object> queryMap = new HashMap<>();
        String[] azoneIds = azoneBiz.getAzoneIds(tenantId); // 筛选出租户对应的可用域id
        if(azoneIds == null) {
            return BaseRestControl.tranReturnValue(Collections.EMPTY_LIST);
        }
        queryMap.put("uuid", azoneIds);
        switch (type) {
            case "nova":
                queryMap.put("resourceType", "nova");
                break;
            case "cinder":
                queryMap.put("resourceType", "cinder");
                break;
            default:
                return BaseRestControl.tranReturnValue(Collections.EMPTY_LIST);
        }
        List<Azone> azones = azoneBiz.listByClass(Azone.class, queryMap);
        return BaseRestControl.tranReturnValue(azones);
    }
    
    @Override
    @ApiOperation(value = "获取云管理可用域详细信息", response = Azone.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String id) {
        Azone azone = azoneBiz.findById(Azone.class, id);
        if (null != azone){
            return BaseRestControl.tranReturnValue(ResultType.success, azone);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    @ApiOperation(value = "删除云管理可用域", response = Azone.class)
//    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Object delete(@PathVariable String id) {
        Azone azone = azoneBiz.findById(Azone.class, id);
        if (null != azone){
            try {
                azoneBiz.delete(azone);
                return BaseRestControl.tranReturnValue(ResultType.success, azone);
            }catch (Exception e){
                this.exception(Azone.class, e);
                return BaseRestControl.tranReturnValue(ResultType.deleted);
            }
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    @ApiOperation(value = "保存云管理可用域", response = Azone.class)
//    @RequestMapping(method = RequestMethod.POST)
    public Object save(@RequestBody Azone entity) {
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);
        if (validatorMap.isEmpty()){
            entity.setDeleted(ConfigProperty.YES);
            try {
                azoneBiz.add(entity);
                return BaseRestControl.tranReturnValue(ResultType.success);
            } catch (Exception e) {
                this.exception(Azone.class, e);
                return BaseRestControl.tranReturnValue(ResultType.failure);
            }
        }
        return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
    }

    @Override
    @ApiOperation(value = "修改云管理可用域", response = Azone.class)
//    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Object update(@PathVariable String id, @RequestBody Azone entity) throws IOException {
        Azone before = azoneBiz.findById(Azone.class, id);
        if (null != before){
            Map<String, String> validatorMap = ValidatorUtils.validator(entity);
            if (validatorMap.isEmpty()){
                InvokeSetForm.copyFormProperties(entity, before);
                before.updatedUser(this.getLoginUser());
                try {
                    azoneBiz.update(before);
                    return BaseRestControl.tranReturnValue(ResultType.success);
                } catch (Exception e) {
                    this.exception(Azone.class, e);
                    return BaseRestControl.tranReturnValue(ResultType.failure);
                }
            }
            return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

}
