package com.h3c.iclouds.rest;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseChildRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.ListPriceBiz;
import com.h3c.iclouds.biz.Specs2KeyBiz;
import com.h3c.iclouds.biz.Specs2KeyValueBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.business.ListPrice;
import com.h3c.iclouds.po.business.Specs2Key;
import com.h3c.iclouds.po.business.Specs2KeyValue;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2017/1/12.
 */

@RestController
@RequestMapping("/{key}/specs2KeyValue")
@Api(value = "云管理产品规格组成属性值预备值表", description = "云管理产品规格组成属性值预备值表")
public class Specs2KeyValueRest extends BaseChildRest<Specs2KeyValue> {

    @Resource
    private Specs2KeyValueBiz specs2KeyValueBiz;

    @Resource
    private Specs2KeyBiz specs2KeyBiz;
    
    @Resource
    private ListPriceBiz listPriceBiz;

    @Override
    @ApiOperation(value = "获取云管理产品规格组成属性值预备值列表", response = Specs2KeyValue.class)
    @RequestMapping(method = RequestMethod.GET)
    public Object list(@PathVariable String key) {
        try {
            Specs2Key specs2Key = specs2KeyBiz.findById(Specs2Key.class, key);
            if (!StrUtils.checkParam(specs2Key)){
                return BaseRestControl.tranReturnValue(ResultType.deleted);
            }
            PageEntity pageEntity = this.beforeList();
            pageEntity.setSpecialParam(key);
            PageModel<Specs2KeyValue> pageModel = specs2KeyValueBiz.findForPage(pageEntity);
            PageList<Specs2KeyValue> pageList = new PageList<>(pageModel, pageEntity.getsEcho());
            return BaseRestControl.tranReturnValue(pageList);
        } catch (Exception e) {
            this.exception(Specs2KeyValue.class, e, key);
        }
        return BaseRestControl.tranReturnValue(ResultType.failure);
    }


    @Override
    @ApiOperation(value = "获取云管理产品规格组成属性值预备值详细信息", response = Specs2KeyValue.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String key, @PathVariable String id) {
        try {
            Specs2Key specs2Key = specs2KeyBiz.findById(Specs2Key.class, key);
            if (!StrUtils.checkParam(specs2Key)){
                return BaseRestControl.tranReturnValue(ResultType.deleted);
            }
            Specs2KeyValue specs2KeyValue = specs2KeyValueBiz.findById(Specs2KeyValue.class, id);
            if (!StrUtils.checkParam(specs2KeyValue)){
                return BaseRestControl.tranReturnValue(ResultType.deleted);
            }
            return BaseRestControl.tranReturnValue(specs2KeyValue);
        } catch (Exception e) {
            this.exception(Specs2KeyValue.class, e, key, id);
        }
        return BaseRestControl.tranReturnValue(ResultType.failure);
    }

    @Override
    @ApiOperation(value = "删除云管理产品规格组成属性值预备值", response = Specs2KeyValue.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Object delete(@PathVariable String key, @PathVariable String id) {
        try {
            Specs2Key specs2Key = specs2KeyBiz.findById(Specs2Key.class, key);
            if (!StrUtils.checkParam(specs2Key)){
                return BaseRestControl.tranReturnValue(ResultType.deleted);
            }
            Specs2KeyValue specs2KeyValue = specs2KeyValueBiz.findById(Specs2KeyValue.class, id);
            if (!StrUtils.checkParam(specs2KeyValue)){
                return BaseRestControl.tranReturnValue(ResultType.deleted);
            }
            List<ListPrice> listPrices = listPriceBiz.findByPropertyName(ListPrice.class, "classId", specs2Key.getClassId());
            if (StrUtils.checkCollection(listPrices)) {
                for (ListPrice listPrice : listPrices) {
                    String spec = listPrice.getSpec();
                    Map<String, Object> keyValueMap = JSONObject.parseObject(spec);
                    for (Map.Entry<String, Object> keyValue : keyValueMap.entrySet()) {
                        String value = StrUtils.tranString(keyValue.getValue());
                        if (value.equals(specs2KeyValue.getId())) {
                            return BaseRestControl.tranReturnValue(ResultType.used_in_flavor_group);
                        }
                    }
                }
            }
            specs2KeyValueBiz.delete(specs2KeyValue);
            this.info(id);
            return BaseRestControl.tranReturnValue(ResultType.success);
        } catch (Exception e) {
            this.exception(Specs2KeyValue.class, e, id);
        }
        return BaseRestControl.tranReturnValue(ResultType.failure);
    }

    @Override
    @ApiOperation(value = "保存云管理产品规格组成属性值预备值", response = Specs2KeyValue.class)
    @RequestMapping(method = RequestMethod.POST)
    public Object save(@PathVariable String key, @RequestBody Specs2KeyValue entity) {
        try {
            Specs2Key specs2Key = specs2KeyBiz.findById(Specs2Key.class, key);
            if (!StrUtils.checkParam(specs2Key)){
                return BaseRestControl.tranReturnValue(ResultType.deleted);
            }
            entity.setKey(key);
            entity.setUnit(specs2Key.getUnit());
            Map<String, String> validatorMap = ValidatorUtils.validator(entity);
            if (!validatorMap.isEmpty()){
                return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
            }
            if ("0".equals(entity.getValueType())) {//离散型值
                if (!StrUtils.checkParam(entity.getValue())) {
                    return BaseRestControl.tranReturnValue(ResultType.value_not_null);
                }
                Map<String, Object> checkMap = new HashMap<>();
                checkMap.put("value", entity.getValue());
                checkMap.put("key", key);
                if (!specs2KeyValueBiz.checkRepeat(Specs2KeyValue.class, checkMap)){
                    return BaseRestControl.tranReturnValue(ResultType.value_repeat);
                }
                if(!specs2KeyValueBiz.checkValueType(specs2Key, entity)){//校验值类型是否符合要求
                    return BaseRestControl.tranReturnValue(ResultType.value_type_error);
                }
                if(!specs2KeyValueBiz.validate(specs2Key, entity)){//校验值是否符合预设的校验规则
                    return BaseRestControl.tranReturnValue(ResultType.value_validator_error);
                }
                // 由于不能组合两个连续型值定价 因此不做离散值是否与连续值重叠的校验
//                if (ConfigProperty.ITEM_DATATYPE1_INT.equals(specs2Key.getDataType())) {
//                    if (!specs2KeyValueBiz.checkValue(key, Integer.parseInt(entity.getValue()))) {
//                        return BaseRestControl.tranReturnValue(ResultType.value_repeat);
//                    }
//                }
                entity.setMinValue(null);
                entity.setMaxValue(null);
                entity.setStep(null);
            } else {//连续型值
                if (!ConfigProperty.ITEM_DATATYPE1_INT.equals(specs2Key.getDataType()) && !ConfigProperty.ITEM_DATATYPE2_FLOAT.equals(specs2Key.getDataType())) {
                    return BaseRestControl.tranReturnValue(ResultType.attribute_type_not_be_int_or_float);
                }
                if (!StrUtils.checkParam(entity.getMinValue(), entity.getMaxValue(), entity.getStep())) {
                    return BaseRestControl.tranReturnValue(ResultType.minvalue_maxvalue_step_not_null);
                }
                if (entity.getMinValue() >= entity.getMaxValue()) {
                    return BaseRestControl.tranReturnValue(ResultType.minvalue_greater_than_maxvalue);
                }
                if (!specs2KeyValueBiz.checkValue(key, entity.getMinValue(), entity.getMaxValue())) {
                    return BaseRestControl.tranReturnValue(ResultType.value_repeat);
                }
                entity.setValue(null);
            }
            entity.createdUser(this.getLoginUser());
            specs2KeyValueBiz.add(entity);
            this.warn(entity);
            return BaseRestControl.tranReturnValue(ResultType.success);
        } catch (Exception e) {
            this.exception(Specs2KeyValue.class, e, entity);
        }
        return BaseRestControl.tranReturnValue(ResultType.failure);
    }

    @Override
    public Object update(@PathVariable String key, @PathVariable String id, @RequestBody Specs2KeyValue entity) throws IOException {
    	return null;
    }

    @ApiOperation(value = "检查属性值是否重复")
    @RequestMapping(value = "/check/{value}", method = RequestMethod.GET)
    public Object checkRepeat(@PathVariable String key, @PathVariable String value){
        boolean repeat = false;
        String id = request.getParameter("id");//修改时传入一个id则查重时会排除对象本身
        try {
            Map<String, Object> checkMap = new HashMap<>();
            checkMap.put("value", value);
            checkMap.put("key", key);
            repeat = specs2KeyValueBiz.checkRepeat(Specs2KeyValue.class, checkMap, id);
            if (!repeat){//查重(名称)
                return BaseRestControl.tranReturnValue(ResultType.repeat);
            }
            return BaseRestControl.tranReturnValue(ResultType.success);
        } catch (Exception e) {
            this.exception(Specs2KeyValue.class, e, value);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }
}
