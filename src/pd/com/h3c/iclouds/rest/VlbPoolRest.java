package com.h3c.iclouds.rest;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.VdcBiz;
import com.h3c.iclouds.biz.VlbPoolBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.HealthMonitor;
import com.h3c.iclouds.po.VlbPool;
import com.h3c.iclouds.po.VlbVip;
import com.h3c.iclouds.utils.StrUtils;
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
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Api(value = "云管理负载均衡实例池")
@RestController
@RequestMapping("/vlbPool")
public class VlbPoolRest extends BaseRest<VlbPool> {

    @Resource
    private VlbPoolBiz vlbPoolBiz;

    @Resource
    private VdcBiz vdcBiz;

    @Resource
    private ProjectBiz projectBiz;
    
    @Override
    @ApiOperation(value = "获取云管理负载均衡实例池列表", response = VlbPool.class)
    @RequestMapping(method = RequestMethod.GET)
    public Object list() {
        PageEntity entity = this.beforeList();
        PageModel<VlbPool> pageModel = vlbPoolBiz.findForPage(entity);
        PageList<VlbPool> page = new PageList<VlbPool>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

    @Override
    @ApiOperation(value = "获取云管理负载均衡实例池详细信息", response = VlbPool.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String id) {
        VlbPool vlbPool = vlbPoolBiz.findById(VlbPool.class, id);
        if (null != vlbPool){
            if (!projectBiz.checkLookRole(vlbPool.getTenantId())){
                return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
            }
            return BaseRestControl.tranReturnValue(vlbPool);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    @ApiOperation(value = "删除云管理负载均衡实例池", response = VlbPool.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Object delete(@PathVariable String id) {
        request.setAttribute("id", id);
        VlbPool vlbPool = vlbPoolBiz.findById(VlbPool.class, id);
        if (null == vlbPool){
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        request.setAttribute("name", vlbPool.getName());
        if (!projectBiz.checkOptionRole(vlbPool.getTenantId())){
            return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
        }
        if (!vdcBiz.checkLock(vlbPool.getTenantId())){
            return BaseRestControl.tranReturnValue(ResultType.vdc_option_lock);
        }
        CloudosClient client = this.getSessionBean().getCloudClient();
        if (null == client){
            return BaseRestControl.tranReturnValue(ResultType.system_error);
        }
        try {
            this.warn("Delete vlbPool, + poolId:" + id);
            return vlbPoolBiz.delete(id, client);
        } catch (Exception e) {
            vlbPoolBiz.updateStatus(id, ConfigProperty.RESOURCE_OPTION_STATUS_DELETE_EXCEPTION);
            this.exception(VlbPool.class, e, id);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @ApiOperation(value = "保存云管理负载均衡实例池", response = VlbPool.class)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Object save(@RequestBody VlbPool entity) {
        if (!projectBiz.checkRole()){
            return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
        }
        return this.save(entity, this.getProjectId());
    }
    
    @ApiOperation(value = "管理员保存租户负载均衡实例池", response = VlbPool.class)
    @RequestMapping(value = "/admin/save/{projectId}", method = RequestMethod.POST)
    public Object save(@PathVariable String projectId, @RequestBody VlbPool entity) {
        if (!this.getSessionBean().getSuperUser()) {
            return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
        }
        return this.save(entity, projectId);
    }

    @ApiOperation(value = "修改云管理负载均衡实例池", response = VlbPool.class)
    @RequestMapping(value = "/{id}/update", method = RequestMethod.PUT)
    public Object update(@PathVariable String id, @RequestBody VlbPool entity) throws IOException {
        request.setAttribute("id", id);
        VlbPool vlbPool = vlbPoolBiz.findById(VlbPool.class, id);
        if (null == vlbPool){
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        request.setAttribute("name", vlbPool.getName());
        if (!projectBiz.checkOptionRole(vlbPool.getTenantId())){
            return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
        }
        if (!vdcBiz.checkLock(vlbPool.getTenantId())){
            return BaseRestControl.tranReturnValue(ResultType.vdc_option_lock);
        }
        CloudosClient client = this.getSessionBean().getCloudClient();
        if (null == client){
            return BaseRestControl.tranReturnValue(ResultType.system_error);
        }
        try {
            entity.updatedUser(this.getLoginUser());
            this.warn("Update vlbPool, pool:" + vlbPool);
            return vlbPoolBiz.update(id, entity, client);
        } catch (Exception e) {
            vlbPoolBiz.updateStatus(id, ConfigProperty.RESOURCE_OPTION_STATUS_UPDATE_EXCEPTION);
            this.exception(VlbPool.class, e, vlbPool);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @ApiOperation(value = "检查名称是否重复")
    @RequestMapping(value = "/check/{name}", method = RequestMethod.GET)
    public Object checkRepeat(@PathVariable String name){
        boolean vlbPoolNameRepeat = false;
        String id = request.getParameter("id");//修改时传入一个id则查重时会排除对象本身
        String projectId = request.getParameter("projectId");//超级管理员操作租户资源时传入租户id进行查重
        if (!this.getSessionBean().getSuperUser() || !StrUtils.checkParam(projectId)) {
            projectId = this.getProjectId();
        }
        try {
            Map<String, Object> checkMap = new HashMap<>();
            checkMap.put("name", name);
            checkMap.put("tenantId", projectId);
            vlbPoolNameRepeat = vlbPoolBiz.checkRepeat(VlbPool.class, checkMap, id);
            if (!vlbPoolNameRepeat){//查重(名称)
                return BaseRestControl.tranReturnValue(ResultType.repeat);
            }
            return BaseRestControl.tranReturnValue(ResultType.success);
        } catch (Exception e) {
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }
    
    private Object save(VlbPool entity, String projectId) {
        request.setAttribute("name", entity.getName());
        entity.setTenantId(projectId);
        entity.createdUser(this.getLoginUser());
        HealthMonitor healMonitor = new HealthMonitor(entity);
        VlbVip vlbVip = new VlbVip(entity);
        if (!vdcBiz.checkLock(projectId)){
            return BaseRestControl.tranReturnValue(ResultType.vdc_option_lock);
        }
        CloudosClient client = this.getSessionBean().getCloudClient();
        if (null == client){
            return BaseRestControl.tranReturnValue(ResultType.system_error);
        }
        ResultType veRs = vlbPoolBiz.verify(entity);
        if (!ResultType.success.equals(veRs)){
            return BaseRestControl.tranReturnValue(veRs);
        }
        String cdRs = vlbPoolBiz.cloudSave(entity, healMonitor, vlbVip, client, null);
        if ("success".equals(cdRs)){
            String vdcId = vdcBiz.getVdc(projectId).getId();
            try {
                JSONObject vipJson = vlbPoolBiz.getVipJson(vlbVip.getCloudosId(), client);
                vlbPoolBiz.localSave(entity, healMonitor, vlbVip, vdcId, vipJson.getString("status"));
                request.setAttribute("id", entity.getId());
                this.warn("Save vlbPool, pool:" + entity);
                return BaseRestControl.tranReturnValue(ResultType.success);
            } catch (Exception e) {
                vlbPoolBiz.cloudDelete(entity.getLbId(), entity.getCloudosId(), healMonitor.getCloudosId(), vlbVip
                        .getCloudosId(), client);
                this.exception(VlbPool.class, e, entity);
                return BaseRestControl.tranReturnValue(ResultType.failure);
            }
        } else {
            return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, cdRs);
        }
    }
    
    @ApiOperation(value = "负载均衡实例池数量")
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public Object count() {
       int count=0;
       if(this.getSessionBean().getSuperUser()){
    	   count=vlbPoolBiz.count(VlbPool.class, null);
       }else {
    	   count=vlbPoolBiz.count(VlbPool.class, StrUtils.createMap("tenantId", this.getProjectId()));
       }
       return BaseRestControl.tranReturnValue(count);
    }
    
}
