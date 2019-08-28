package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.StorageGroups2ItemsBiz;
import com.h3c.iclouds.biz.StorageVolumsBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.StorageGroups2Items;
import com.h3c.iclouds.po.StorageVolums;
import com.h3c.iclouds.utils.InvokeSetForm;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by yKF7317 on 2016/11/14.
 */
@Api(value = "资源配置存储集群配置明细", description = "资源配置存储集群配置明细")
@RestController
@RequestMapping("/storage/groups2Items")
public class StorageGroups2ItemsRest extends BaseRest<StorageGroups2Items> {

    @Resource
    private StorageGroups2ItemsBiz storageGroups2ItemsBiz;

    @Resource
    private StorageVolumsBiz storageVolumsBiz;

    @Override
    @ApiOperation(value = "获取子存储列表", response = StorageGroups2Items.class)
    @RequestMapping(method = RequestMethod.GET)
    public Object list() {
        PageEntity entity = this.beforeList();
        PageModel<StorageGroups2Items> pageModel = storageGroups2ItemsBiz.findForPage(entity);
        PageList<StorageGroups2Items> page = new PageList<>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

    @ApiOperation(value = "获取未添加挂载的存储集群配置明细信息列表", response = StorageGroups2Items.class)
    @RequestMapping(value = "/unmount", method = RequestMethod.GET)
    public Object unmountList() {
        PageEntity entity = this.beforeList();
        PageModel<StorageGroups2Items> pageModel = storageGroups2ItemsBiz.findForPage(entity);
        PageList<StorageGroups2Items> page = new PageList<>(pageModel, entity.getsEcho());
        List<StorageGroups2Items> itemList = page.getAaData();
        List<StorageGroups2Items> removeItemList = new ArrayList<>();
        for (StorageGroups2Items item : itemList) {
            String id = item.getId();
            List<StorageVolums> volumeList = storageVolumsBiz.findByPropertyName(StorageVolums.class, "sid", id);
            if (null != volumeList && volumeList.size() > 0){
                removeItemList.add(item);
            }
        }
        itemList.removeAll(removeItemList);
        return BaseRestControl.tranReturnValue(page);
    }

    @Override
    @ApiOperation(value = "获取存储集群配置明细详细信息", response = StorageGroups2Items.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String id) {
        StorageGroups2Items storageGroups2Items = storageGroups2ItemsBiz.findById(StorageGroups2Items.class, id);
        if (null != storageGroups2Items){
            return BaseRestControl.tranReturnValue(storageGroups2Items);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    @ApiOperation(value = "删除存储集群配置明细-子存储", response = StorageGroups2Items.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Object delete(@PathVariable String id) {
        StorageGroups2Items storageGroups2Items = storageGroups2ItemsBiz.findById(StorageGroups2Items.class, id);
        if (null != storageGroups2Items && storageGroups2Items.getType().equals(ConfigProperty.NO)){
            storageGroups2ItemsBiz.deleteItem(storageGroups2Items);
            return BaseRestControl.tranReturnValue(ResultType.success);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    @ApiOperation(value = "新增存储集群配置明细", response = StorageGroups2Items.class)
    @RequestMapping(method = RequestMethod.POST)
    public Object save(@RequestBody StorageGroups2Items entity) {
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);
        if(validatorMap.isEmpty()) {
            entity.createdUser(this.getLoginUser());
            String id = UUID.randomUUID().toString();
            try {
                entity.setId(id);
                storageGroups2ItemsBiz.add(entity);
                return BaseRestControl.tranReturnValue(ResultType.success, entity);
            } catch (Exception e) {
                this.exception(getClass(), e);
                return BaseRestControl.tranReturnValue(ResultType.failure);
            }
        }
        return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
    }

    @Override
    @ApiOperation(value = "修改存储集群配置明细", response = StorageGroups2Items.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Object update(@PathVariable String id, @RequestBody StorageGroups2Items entity) throws IOException {
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);
        if(validatorMap.isEmpty()) {
            StorageGroups2Items item = storageGroups2ItemsBiz.findById(StorageGroups2Items.class, id);
            if (item != null){
                InvokeSetForm.copyFormProperties(entity, item);
                item.updatedUser(this.getLoginUser());
                storageGroups2ItemsBiz.update(item);
                return BaseRestControl.tranReturnValue(ResultType.success, entity);
            }
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
    }

}
