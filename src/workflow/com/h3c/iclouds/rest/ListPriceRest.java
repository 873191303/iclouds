package com.h3c.iclouds.rest;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseChildRest;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.AzoneBiz;
import com.h3c.iclouds.biz.ListPriceBiz;
import com.h3c.iclouds.biz.Specs2KeyBiz;
import com.h3c.iclouds.biz.Specs2KeyValueBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.NovaFlavorDao;
import com.h3c.iclouds.po.Azone;
import com.h3c.iclouds.po.NovaFlavor;
import com.h3c.iclouds.po.business.ListPrice;
import com.h3c.iclouds.po.business.PrdClass;
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
@RequestMapping("/{classId}/listPrice")
@Api(value = "云管理产品目录定价表", description = "云管理产品目录定价表")
public class ListPriceRest extends BaseChildRest<ListPrice> {

    @Resource
    private ListPriceBiz listPriceBiz;

    @Resource
    private Specs2KeyBiz specs2KeyBiz;

    @Resource
    private Specs2KeyValueBiz specs2KeyValueBiz;

    @Resource(name = "baseDAO")
    private BaseDAO<PrdClass> prdClassDao;

    @Resource
    private AzoneBiz azoneBiz;
    
    @Resource
    private NovaFlavorDao novaFlavorDao;
    
    @Override
    @ApiOperation(value = "获取云管理产品目录定价列表", response = ListPrice.class)
    @RequestMapping(method = RequestMethod.GET)
    public Object list(@PathVariable String classId) {
        try {
            PrdClass prdClass = prdClassDao.findById(PrdClass.class, classId);
            if (!StrUtils.checkParam(prdClass)){
                return BaseRestControl.tranReturnValue(ResultType.deleted);
            }
            PageEntity pageEntity = this.beforeList();
            pageEntity.setSpecialParam(classId);
            PageModel<ListPrice> pageModel = listPriceBiz.findForPage(pageEntity);
            PageList<ListPrice> pageList = new PageList<>(pageModel, pageEntity.getsEcho());
            return BaseRestControl.tranReturnValue(pageList);
        } catch (Exception e) {
            this.exception(ListPrice.class, e, classId);
        }
        return BaseRestControl.tranReturnValue(ResultType.failure);
    }

    @Override
    @ApiOperation(value = "获取云管理产品目录定价详细信息", response = ListPrice.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String classId, @PathVariable String id) {
        try {
            PrdClass prdClass = prdClassDao.findById(PrdClass.class, classId);
            if (!StrUtils.checkParam(prdClass)){
                return BaseRestControl.tranReturnValue(ResultType.deleted);
            }
            ListPrice listPrice = listPriceBiz.findById(ListPrice.class, id);
            if (!StrUtils.checkParam(listPrice)){
                return BaseRestControl.tranReturnValue(ResultType.deleted);
            }
            return BaseRestControl.tranReturnValue(listPrice);
        } catch (Exception e) {
            this.exception(ListPrice.class, e, classId);
        }
        return BaseRestControl.tranReturnValue(ResultType.failure);
    }

    @Override
    @ApiOperation(value = "删除云管理产品目录定价", response = ListPrice.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Object delete(@PathVariable String classId, @PathVariable String id) {
        try {
            ListPrice listPrice = listPriceBiz.findById(ListPrice.class, id);
            if (!StrUtils.checkParam(listPrice)){
                return BaseRestControl.tranReturnValue(ResultType.deleted);
            }
            listPriceBiz.delete(listPrice);
            this.warn("Delete ListPrice, id:" + id);
            return BaseRestControl.tranReturnValue(ResultType.success);
        } catch (Exception e) {
            this.exception(ListPrice.class, e, id);
        }
        return BaseRestControl.tranReturnValue(ResultType.failure);
    }

    @Override
    @ApiOperation(value = "保存云管理产品目录定价", response = ListPrice.class)
    @RequestMapping(method = RequestMethod.POST)
    public Object save(@PathVariable String classId, @RequestBody ListPrice entity) {
        try {
            PrdClass prdClass = prdClassDao.findById(PrdClass.class, classId);
            if (!StrUtils.checkParam(prdClass)){
                return BaseRestControl.tranReturnValue(ResultType.prdclass_not_exist);
            }
            entity.setClassId(classId);
            Map<String, String> validateMap = ValidatorUtils.validator(entity);
            if (!validateMap.isEmpty()) {
                return BaseRestControl.tranReturnValue(ResultType.parameter_error, validateMap);
            }
            ResultType rs = checkKeyMap(entity.getKeyMap(), entity);
            if (!ResultType.success.equals(rs)) {
                return BaseRestControl.tranReturnValue(rs);
            }
            Map<String, String> checkMap = new HashMap<>();//查重
            checkMap.put("classId", classId);
            if (singleton.getVmClassId().equals(classId) || singleton.getStorageClassId().equals(classId)) {//云主机产品和云硬盘产品
                if (!StrUtils.checkParam(entity.getAzoneId())) {
                    return BaseRestControl.tranReturnValue(ResultType.azone_not_null);
                }
                Azone azone = azoneBiz.findById(Azone.class, entity.getAzoneId());
                if (!StrUtils.checkParam(azone)) {
                    return BaseRestControl.tranReturnValue(ResultType.azone_not_exist);
                }
                checkMap.put("azoneId", entity.getAzoneId());
                if (singleton.getVmClassId().equals(classId)) {//云主机产品
                    if (!"nova".equals(azone.getResourceType())) {
                        return BaseRestControl.tranReturnValue(ResultType.azone_belong_to_another_prdclass);
                    }
                    if (!StrUtils.checkParam(entity.getFlavorId())) {
                        return BaseRestControl.tranReturnValue(ResultType.flavor_not_null);
                    }
                    NovaFlavor novaFlavor = novaFlavorDao.findById(NovaFlavor.class, entity.getFlavorId());
                    if (!StrUtils.checkParam(novaFlavor)) {
                        return BaseRestControl.tranReturnValue(ResultType.flavor_not_exist);
                    }
                    Map<String, String> map = listPriceBiz.getSpecByNovaFlavor(novaFlavor, this.getLoginUser());
                    entity.setSpec(map.get("spec"));
                    entity.setName(map.get("name"));
                    checkMap.put("flavorId", novaFlavor.getId());
                } else {
                    if (!"cinder".equals(azone.getResourceType())) {
                        return BaseRestControl.tranReturnValue(ResultType.azone_belong_to_another_prdclass);
                    }
                    checkMap.put("spec", entity.getSpec());
                }
            } else {
                checkMap.put("spec", entity.getSpec());
            }
            List<ListPrice> listPrices = listPriceBiz.findByMap(ListPrice.class, checkMap);
            if (StrUtils.checkCollection(listPrices)) {
                return BaseRestControl.tranReturnValue(ResultType.listprice_repeat);
            }
            entity.createdUser(this.getLoginUser());
            listPriceBiz.add(entity);
            this.warn("Save ListPrice, param:" + JSONObject.toJSONString(entity));
            return BaseRestControl.tranReturnValue(ResultType.success);
        } catch (Exception e) {
            this.exception(ListPrice.class, e, entity);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @Override
    @ApiOperation(value = "修改云管理产品目录定价", response = ListPrice.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Object update(@PathVariable String classId, @PathVariable String id, @RequestBody ListPrice entity) throws IOException {
        PrdClass prdClass = prdClassDao.findById(PrdClass.class, classId);
        if (!StrUtils.checkParam(prdClass)){
            return BaseRestControl.tranReturnValue(ResultType.prdclass_not_exist);
        }
        ListPrice listPrice = listPriceBiz.findById(ListPrice.class, id);
        if (!StrUtils.checkParam(listPrice)){
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        double discount = null == entity.getDiscount() ? 1 : entity.getDiscount();
        if (discount < 0 || discount > 1) {
            return ResultType.discount_must_between_0_and_1;
        }
        if (discount != listPrice.getDiscount()) {
            listPrice.setDiscount(discount);
        }
        listPrice.setStepPrice(entity.getStepPrice());
        listPrice.setListPrice(entity.getListPrice());
        listPrice.setSpecPrice(entity.getListPrice() * discount);
        listPriceBiz.update(listPrice);
        return BaseRestControl.tranReturnValue(ResultType.success);
    }

    @ApiOperation(value = "检查计量规格是否重复")
    @RequestMapping(value = "/check/{azoneId}/{spec}", method = RequestMethod.GET)
    public Object checkRepeat(@PathVariable String classId, @PathVariable String azoneId, @PathVariable String spec){
        boolean repeat = false;
        String id = request.getParameter("id");//修改时传入一个id则查重时会排除对象本身
        try {
            Map<String, Object> checkMap = new HashMap<>();
            checkMap.put("spec", spec);
            checkMap.put("classId", classId);
            checkMap.put("azoneId", azoneId);
            repeat = listPriceBiz.checkRepeat(ListPrice.class, checkMap, id);
            if (!repeat){//查重(名称)
                return BaseRestControl.tranReturnValue(ResultType.repeat);
            }
            return BaseRestControl.tranReturnValue(ResultType.success);
        } catch (Exception e) {
            this.exception(ListPrice.class, e, spec);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }
    
    /**
     * 校验属性键值对是否正确
     * @param keyMap
     * @param classId
     * @return
     */
    private ResultType checkKeyMap(Map<String, String> keyMap, ListPrice listPrice) {
        String classId = listPrice.getClassId();
        double discount = null == listPrice.getDiscount() ? 1 : listPrice.getDiscount();
        if (discount < 0 || discount > 1) {//折扣在0-1之间
            return ResultType.discount_must_between_0_and_1;
        }
        listPrice.setDiscount(discount);
        listPrice.setSpecPrice(listPrice.getListPrice() * discount);
        if (!singleton.getVmClassId().equals(classId)) {
            int count = this.specs2KeyBiz.count(Specs2Key.class, StrUtils.createMap("classId", classId));
            if (count != keyMap.size()) {
                return ResultType.please_choice_all_key;
            }
        } else {//云主机产品
            return ResultType.success;
        }
        StringBuffer nameBuffer = new StringBuffer();
        int size = 0;
        for (Map.Entry<String, String> entry : keyMap.entrySet()) {
            String keyId = entry.getKey();
            Specs2Key specs2Key = specs2KeyBiz.findById(Specs2Key.class, keyId);
            if (!StrUtils.checkParam(specs2Key)) {
                return ResultType.specs2key_not_exist;
            }
            if (!specs2Key.getClassId().equals(classId)) {
                return ResultType.specs2key_not_belong_to_class;
            }
            String valueId = entry.getValue();
            Specs2KeyValue specs2KeyValue = specs2KeyValueBiz.findById(Specs2KeyValue.class, valueId);
            if (!StrUtils.checkParam(specs2KeyValue)) {
                return ResultType.specs2keyvalue_not_exist;
            }
            if (specs2KeyValue.getValueType().equals("1")) {
                if (size > 1) {//同一种产品只能包含一种连续型定价
                    return ResultType.only_can_contains_one_continuous_value;
                }
                if (!StrUtils.checkParam(listPrice.getStepPrice())) {
                    return ResultType.continuous_value_must_have_stepprice;
                }
                nameBuffer.append(specs2Key.getKey() + ":" + specs2KeyValue.getMinValue() + "-" + specs2KeyValue
                        .getMaxValue() + "|");
                size++;
            } else {
                nameBuffer.append(specs2Key.getKey() + ":" + specs2KeyValue.getValue() + "|");
            }
            if (!specs2Key.getClassId().equals(classId)) {
                return ResultType.specs2keyvalue_not_belong_to_specs2key;
            }
        }
        listPrice.setSpec(JSONObject.toJSON(keyMap).toString());
        listPrice.setName(nameBuffer.substring(0, nameBuffer.length() - 1));
        return ResultType.success;
    }
    
    
}
