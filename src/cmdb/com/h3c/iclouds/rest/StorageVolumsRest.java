package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseChildRest;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.StorageVolumsBiz;
import com.h3c.iclouds.biz.VolumeBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.StorageVolums;
import com.h3c.iclouds.po.Volume;
import com.h3c.iclouds.po.Volume2Host;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * Created by yKF7317 on 2016/11/14.
 */
@Api(value = "资源配置存储卷配置", description = "资源配置存储卷配置")
@RestController
@RequestMapping("/storage")
public class StorageVolumsRest extends BaseChildRest<StorageVolums> {

    @Resource(name = "baseDAO")
    private BaseDAO<Volume2Host> volume2HostDao;

    @Resource
    private StorageVolumsBiz storageVolumsBiz;

    @Resource
    private VolumeBiz volumeBiz;
    
    @Override
    @ApiOperation(value = "获取存储卷信息列表", response = StorageVolums.class)
    @RequestMapping(value = "/{sid}/volums", method = RequestMethod.GET)
    public Object list(@PathVariable String sid) {
        PageEntity entity = this.beforeList();
        entity.setSpecialParam(sid);
        PageModel<StorageVolums> pageModel = storageVolumsBiz.findForPage(entity);
        PageList<StorageVolums> page = new PageList<>(pageModel, entity.getsEcho());
        page = storageVolumsBiz.transPage(page);
        return BaseRestControl.tranReturnValue(page);
    }
    
    @ApiOperation(value = "获取所有存储卷信息列表", response = StorageVolums.class)
    @RequestMapping(value = "/volums/all", method = RequestMethod.GET)
    public Object all() {
        PageEntity entity = this.beforeList();
        PageModel<StorageVolums> pageModel = storageVolumsBiz.findForPage(entity);
        PageList<StorageVolums> page = new PageList<>(pageModel, entity.getsEcho());
        page = storageVolumsBiz.transPage(page);
        return BaseRestControl.tranReturnValue(page);
    }
    
    @Override
    @ApiOperation(value = "获取存储卷详细信息", response = StorageVolums.class)
    @RequestMapping(value = "/{sid}/volums/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String sid, @PathVariable String id) {
        StorageVolums storageVolums = storageVolumsBiz.findById(StorageVolums.class, id);
        if (null != storageVolums){
            String hostName = storageVolumsBiz.findHostName(storageVolums);
            storageVolums.setHostName(hostName);
            return BaseRestControl.tranReturnValue(storageVolums);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @ApiOperation(value = "批量删除存储卷", response = StorageVolums.class)
    @RequestMapping(value = "/{sid}/volums/deleteList", method = RequestMethod.DELETE)
    public Object deleteList(@RequestBody List<String> ids) {
        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            StorageVolums volums = storageVolumsBiz.findById(StorageVolums.class, id);
            if (null != volums){
                List<Volume2Host> volume2Hosts = volume2HostDao.findByPropertyName(Volume2Host.class, "volumeId", id);
                if (null != volume2Hosts && volume2Hosts.size() > 0){//删除挂载时删除其与主机关联的数据
                    volume2HostDao.delete(volume2Hosts.get(0));
                }
                storageVolumsBiz.delete(volums);
            }
        }
        return BaseRestControl.tranReturnValue(ResultType.success);
    }

    @ApiOperation(value = "删除存储卷", response = StorageVolums.class)
    @RequestMapping(value = "/{sid}/volums/{id}", method = RequestMethod.DELETE)
    public Object delete(@PathVariable String sid, @PathVariable String id) {
        StorageVolums volums = storageVolumsBiz.findById(StorageVolums.class, id);
        if (null != volums){
            List<Volume2Host> volume2Hosts = volume2HostDao.findByPropertyName(Volume2Host.class, "volumeId", id);
            if (null != volume2Hosts && volume2Hosts.size() > 0){//删除挂载时删除其与主机关联的数据
                volume2HostDao.delete(volume2Hosts.get(0));
            }
            storageVolumsBiz.delete(volums);
            return BaseRestControl.tranReturnValue(ResultType.success);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    @ApiOperation(value = "新增存储卷", response = StorageVolums.class)
    @RequestMapping(value = "/{sid}/volums", method = RequestMethod.POST)
    public Object save(@PathVariable String sid, @RequestBody StorageVolums entity) {
        entity.setSid(sid);
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);
        if(validatorMap.isEmpty()) {
            entity.createdUser(this.getLoginUser());
            entity.setId(UUID.randomUUID().toString());
            try {
                storageVolumsBiz.add(entity);
                return BaseRestControl.tranReturnValue(ResultType.success, entity);
            } catch (Exception e) {
                this.exception(getClass(), e);
                return BaseRestControl.tranReturnValue(ResultType.failure);
            }
        }
        return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
    }

    @ApiOperation(value = "分配挂载主机", response = StorageVolums.class)
    @RequestMapping(value = "/{sid}/volums/{id}/mount/{type}/{hostId}", method = RequestMethod.POST)
    public Object mountHost(@PathVariable String id, @PathVariable String type, @PathVariable String hostId){
        Volume2Host volume2Host = new Volume2Host();
        String vhId = UUID.randomUUID().toString();
        volume2Host.setId(vhId);
        volume2Host.setVolumeId(id);
        volume2Host.setType(type);
        volume2Host.setHostId(hostId);
        try {
            volume2Host.createdUser(this.getLoginUser());
            volume2HostDao.add(volume2Host);
            return BaseRestControl.tranReturnValue(ResultType.success);
        }catch (Exception e){
            this.exception(Volume2Host.class, e);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @ApiOperation(value = "解除挂载主机", response = StorageVolums.class)
    @RequestMapping(value = "/{sid}/volums/{id}/unmount", method = RequestMethod.POST)
    public Object unmountHost(@PathVariable String id){
        List<Volume2Host> volume2Host = volume2HostDao.findByPropertyName(Volume2Host.class, "volumeId", id);
        if (null != volume2Host && volume2Host.size() > 0){
            volume2HostDao.delete(volume2Host.get(0));
            return BaseRestControl.tranReturnValue(ResultType.success);
        }
        return BaseRestControl.tranReturnValue(ResultType.failure);
    }

    @Override
    @ApiOperation(value = "修改存储卷", response = StorageVolums.class)
    @RequestMapping(value = "/{sid}/volums/{id}", method = RequestMethod.PUT)
    public Object update(@PathVariable String sid, @PathVariable String id, @RequestBody StorageVolums entity) throws IOException {
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);
        if(validatorMap.isEmpty()) {
            StorageVolums volums = storageVolumsBiz.findById(StorageVolums.class, id);
            if (null != volums){
                InvokeSetForm.copyFormProperties(entity, volums);
                volums.updatedUser(this.getLoginUser());
                storageVolumsBiz.update(volums);
                return BaseRestControl.tranReturnValue(ResultType.success, entity);
            }
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
    }
    
    @ApiOperation(value="用户拥有存储卷")
    @RequestMapping(value="/{userId}/volums/volumes",method=RequestMethod.GET)
    public Object listByUser(@PathVariable String userId) {
    	List<StorageVolums> svList = null;
    	List<StorageVolums> storageVolums=null;
    	List<Volume> volumes = null;
    	if(this.getSessionBean().getSuperUser()){
    		storageVolums=storageVolumsBiz.findByPropertyName(StorageVolums.class);
    	}else if(this.getSessionBean().getSuperRole()){
    		volumes= volumeBiz.findByPropertyName(Volume.class, "projectId",this.getProjectId());
    	}else {
    		volumes= volumeBiz.findByPropertyName(Volume.class, "owner2", userId);
    	}
    	if(volumes!=null && !volumes.isEmpty()){
    		storageVolums=new ArrayList<>();
    		for(Volume v:volumes){
    			String volumeName=v.getName();
    			svList = storageVolumsBiz.findByPropertyName(StorageVolums.class, "volumeName", volumeName);
    			if(svList!=null && !svList.isEmpty()){
    				storageVolums.add(svList.get(0));
    			}
    		}
    	}
		return BaseRestControl.tranReturnValue(storageVolums);
	}
}
