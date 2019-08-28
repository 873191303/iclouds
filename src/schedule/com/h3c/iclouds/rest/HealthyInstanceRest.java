package com.h3c.iclouds.rest;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.HealthyInstanceBiz;
import com.h3c.iclouds.client.zabbix.ZabbixApi;
import com.h3c.iclouds.client.zabbix.ZabbixDefine;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.HealthyInstance;
import com.h3c.iclouds.po.HealthyType;
import com.h3c.iclouds.po.HealthyValue;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.PatternValidator;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * Created by zkf5485 on 2017/9/13.
 */
@RestController
@RequestMapping("/healthy")
@Api(value = "健康度实例", description = "健康度实例")
public class HealthyInstanceRest extends BaseRest<HealthyInstance> {

    @Resource
    private HealthyInstanceBiz healthyInstanceBiz;

    @Resource(name = "baseDAO")
    private BaseDAO<HealthyType> healthyTypeDao;

    @RequestMapping(method = RequestMethod.GET, value = "/type")
    @ApiOperation(value = "获取实例类型列表", response = HealthyType.class)
    public Object parent() {
        return BaseRestControl.tranReturnValue(healthyTypeDao.getAll(HealthyType.class));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/type/{id}")
    @ApiOperation(value = "获取单项实例类型", response = HealthyType.class)
    public Object parent(@PathVariable String id) {
        HealthyType entity = healthyTypeDao.findById(HealthyType.class, id);
        if(entity == null) {
            return BaseRestControl.tranReturnValue(ResultType.not_exist);
        }
        return BaseRestControl.tranReturnValue(entity);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "分页获取实例列表", response = HealthyInstance.class)
    public Object list() {
        PageEntity entity = this.beforeList();
        PageModel<HealthyInstance> pageModel = healthyInstanceBiz.findForPage(entity);
        PageList<HealthyInstance> page = new PageList<>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/data")
    @ApiOperation(value = "获取实例健康度数据", response = HealthyValue.class)
    public Object data(@PathVariable String id,
                       @RequestParam(value = "startDate") Long startDate,
                       @RequestParam(value = "endDate") Long endDate) {
        HealthyInstance entity = healthyInstanceBiz.findById(HealthyInstance.class, id);
        if(entity == null) {
            return BaseRestControl.tranReturnValue(ResultType.not_exist);
        }
        return BaseRestControl.tranReturnValue(this.healthyInstanceBiz.data(id, new Date(startDate), new Date(endDate)));
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ApiOperation(value = "获取单项实例", response = HealthyInstance.class)
    public Object get(@PathVariable String id) {
        HealthyInstance entity = healthyInstanceBiz.findById(HealthyInstance.class, id);
        if(entity == null) {
            return BaseRestControl.tranReturnValue(ResultType.not_exist);
        }
        return BaseRestControl.tranReturnValue(entity);
    }

    @Override
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ApiOperation(value = "删除单项实例（逻辑删除，只是做状态修改为0）", response = HealthyInstance.class)
    public Object delete(@PathVariable String id) {
        HealthyInstance entity = healthyInstanceBiz.findById(HealthyInstance.class, id);
        if(entity == null) {
            return BaseRestControl.tranReturnValue(ResultType.not_exist);
        }
        entity.setStatus(ConfigProperty.NO);
        this.healthyInstanceBiz.update(entity);
        return BaseRestControl.tranReturnValue(ResultType.success);
    }

    @Override
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ApiOperation(value = "修改单项实例", response = HealthyInstance.class)
    public Object update(@PathVariable String id, @RequestBody HealthyInstance before) throws IOException {
        HealthyInstance entity = healthyInstanceBiz.findById(HealthyInstance.class, id);
        if(entity == null) {
            return BaseRestControl.tranReturnValue(ResultType.not_exist);
        }
        HealthyType typeEntity = healthyTypeDao.findById(HealthyType.class, entity.getType());
        if(typeEntity == null) {
            return BaseRestControl.tranReturnValue(ResultType.not_found_parent);
        }
        InvokeSetForm.copyFormProperties(before, entity);
        boolean checkResult = this.check(entity, typeEntity);
        if(!checkResult) {
            return BaseRestControl.tranReturnValue(ResultType.config_parameter_error);
        }
        entity.updatedUser(this.getLoginUser());
        try {
            ZabbixApi zabbixApi = ZabbixApi.createAdmin();
            String scriptId = ZabbixApi.getScriptId(id, zabbixApi);
            if(scriptId == null) {
                this.info("删除脚本结果: " + zabbixApi.delete(ZabbixDefine.SCRIPT, scriptId));
            }

            this.healthyInstanceBiz.update(entity);
            return BaseRestControl.tranReturnValue(ResultType.success);
        } catch (Exception e) {
            this.exception(e, "Update HealthyInstance failure, value:" + StrUtils.toJSONString(entity) + ", error :" + e.getMessage());
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @Override
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "新增单项实例", response = HealthyInstance.class)
    public Object save(@RequestBody HealthyInstance entity) {
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);
        if (!validatorMap.isEmpty()) {
            return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
        }
        HealthyType typeEntity = healthyTypeDao.findById(HealthyType.class, entity.getType());
        if(typeEntity == null) {
            return BaseRestControl.tranReturnValue(ResultType.not_found_parent);
        }
        boolean checkResult = this.check(entity, typeEntity);
        if(!checkResult) {
            return BaseRestControl.tranReturnValue(ResultType.config_parameter_error);
        }
        entity.setTenantId(this.getProjectId());
        entity.createdUser(this.getLoginUser());
        try {
            String id = this.healthyInstanceBiz.add(entity);
            return BaseRestControl.tranReturnValue(id);
        } catch (Exception e) {
            this.exception(e, "Save HealthyInstance failure, value:" + StrUtils.toJSONString(entity) + ", error :" + e.getMessage());
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    /**
     * 验证配置参数是否正确
     * @param entity
     * @param typeEntity
     * @return
     */
    private boolean check(HealthyInstance entity, HealthyType typeEntity) {
        try {
            JSONObject source = JSONObject.parseObject(entity.getConfig());
            JSONObject check = JSONObject.parseObject(typeEntity.getConfig());
            for (Map.Entry<String, Object> map : check.entrySet()) {
                String key = map.getKey();
                if(!source.containsKey(key)) {
                    return false;
                }
                String sourceValue = source.getString(key);
                if(!StrUtils.checkParam(sourceValue)) {
                    return false;
                }

                JSONObject checkValidation = (JSONObject) map.getValue();
                String validation = checkValidation.getString("validation");
                if(StrUtils.checkParam(validation)) {
                    if("ipVd".equals(validation)) { // Ip验证
                        if(!PatternValidator.ipCheck(sourceValue)) {
                            return false;
                        }
                    } else if(validation.contains("range")) {   // 范围验证
                        String[] range = validation.replace("range[", "").replace("]", "").split("-");
                        Integer sourceIntValue = StrUtils.tranInteger(sourceValue);
                        if(Integer.valueOf(range[0]) > sourceIntValue || sourceIntValue > Integer.valueOf(range[1])) {
                            return false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
