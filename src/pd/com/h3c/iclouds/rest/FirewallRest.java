package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.FirewallBiz;
import com.h3c.iclouds.biz.PolicieRuleBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.VdcBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Firewall;
import com.h3c.iclouds.po.Policie;
import com.h3c.iclouds.po.PolicieRule;
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
@Api(value = "云管理防火墙")
@RestController
@RequestMapping("/firewall")
public class FirewallRest extends BaseRest<Firewall> {
    
    @Resource
    private FirewallBiz firewallBiz;

    @Resource
    private PolicieRuleBiz policieRuleBiz;

    @Resource
    private VdcBiz vdcBiz;

    @Resource
    private ProjectBiz projectBiz;
    
    @Override
    //@Perms(value = "vdc.ope.network.firewall.simple")
    @ApiOperation(value = "获取云管理防火墙分页列表", response = Firewall.class)
    @RequestMapping(method = RequestMethod.GET)
    public Object list() {
        PageEntity entity = this.beforeList();
        PageModel<Firewall> pageModel = firewallBiz.findForPage(entity);
        PageList<Firewall> page = new PageList<Firewall>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

    @Override
    //@Perms(value = "vdc.ope.network.firewall.simple")
    @ApiOperation(value = "获取云管理防火墙详细信息", response = Firewall.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String id) {
        Firewall firewall = firewallBiz.findById(Firewall.class, id);
        if (null != firewall){
            if (!projectBiz.checkLookRole(firewall.getTenantId())){
                return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
            }
            return BaseRestControl.tranReturnValue(firewall);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    @ApiOperation(value = "删除云管理防火墙", response = Firewall.class)
    //@Perms(value = "vdc.ope.network")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Object delete(@PathVariable String id) {
        try {
            request.setAttribute("id", id);
            Firewall firewall = firewallBiz.findById(Firewall.class, id);
            if (!StrUtils.checkParam(firewall)) {//检查是否存在
                return BaseRestControl.tranReturnValue(ResultType.deleted);
            }
            request.setAttribute("name", firewall.getName());
            if (!projectBiz.checkOptionRole(firewall.getTenantId())){
                return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
            }
            if (!vdcBiz.checkLock(firewall.getTenantId())){
                return BaseRestControl.tranReturnValue(ResultType.vdc_option_lock);
            }
            CloudosClient client = this.getSessionBean().getCloudClient();
            if (null == client){
                return BaseRestControl.tranReturnValue(ResultType.system_error);
            }
            this.warn("Delete firewall, firewallId:" + id);
            return firewallBiz.delete(id, client);
        } catch (Exception e) {
            this.exception(Firewall.class, e, id);
            firewallBiz.updateStatus(id, ConfigProperty.RESOURCE_OPTION_STATUS_DELETE_EXCEPTION);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @ApiOperation(value = "保存云管理防火墙", response = Firewall.class)
    //@Perms(value = "vdc.ope.network.firewall")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Object save(@RequestBody Firewall entity) {
        if (!projectBiz.checkRole()) {
            return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
        }
        return this.save(entity, this.getProjectId());
    }
	
	@ApiOperation(value = "管理员新增租户防火墙", response = Firewall.class)
	@RequestMapping(value = "/admin/save/{projectId}", method = RequestMethod.POST)
	public Object save(@PathVariable String projectId, @RequestBody Firewall entity) {
        if (!this.getSessionBean().getSuperUser()) {
            return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
        }
		return this.save(entity, projectId);
	}
	
    @ApiOperation(value = "修改云管理防火墙", response = Firewall.class)
    //@Perms(value = "vdc.ope.network.firewall")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public Object update(@PathVariable String id, @RequestBody Firewall entity) throws IOException {
        request.setAttribute("id", id);
        Firewall firewall = firewallBiz.findById(Firewall.class, id);
        if (!StrUtils.checkParam(firewall)) {//检查是否存在
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        request.setAttribute("name", firewall.getName());
        if (!projectBiz.checkOptionRole(firewall.getTenantId())){
            return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
        }
        if (!vdcBiz.checkLock(firewall.getTenantId())){
            return BaseRestControl.tranReturnValue(ResultType.vdc_option_lock);
        }
        CloudosClient client = this.getSessionBean().getCloudClient();
        if (null == client){
            return BaseRestControl.tranReturnValue(ResultType.system_error);
        }
        try {
            this.warn("Update firewall, firewall:" + entity);
            return firewallBiz.update(id, entity, client);
        } catch (Exception e) {
            firewallBiz.updateStatus(id, ConfigProperty.RESOURCE_OPTION_STATUS_UPDATE_EXCEPTION);
            this.exception(Firewall.class, e, entity);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @ApiOperation(value = "获取当前防火墙下规则列表", response = PolicieRule.class)
    //@Perms(value = "vdc.ope.network.firewall.simple")
    @RequestMapping(value = "/{id}/policieRule", method = RequestMethod.GET)
    public Object policieRuleList(@PathVariable String id){
        PageEntity pageEntity = this.beforeList();
        pageEntity.setSpecialParams(firewallBiz.getPolicyIds(id));
        PageModel<PolicieRule> pageModel = policieRuleBiz.findForPage(pageEntity);
        PageList<PolicieRule> page = new PageList<>(pageModel, pageEntity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

    @ApiOperation(value = "检查名称是否重复")
    //@Perms(value = "vdc.ope.network.firewall.simple")
    @RequestMapping(value = "/check/{name}", method = RequestMethod.GET)
    public Object checkRepeat(@PathVariable String name){
        boolean firewallNameRepeat = false;
        String id = request.getParameter("id");//修改时传入一个id则查重时会排除对象本身
        String projectId = request.getParameter("projectId");//超级管理员操作租户资源时传入租户id进行查重
        if (!this.getSessionBean().getSuperUser() || !StrUtils.checkParam(projectId)) {
            projectId = this.getProjectId();
        }
        try {
            Map<String, Object> checkMap = new HashMap<>();
            checkMap.put("name", name);
            checkMap.put("tenantId", projectId);
            firewallNameRepeat = firewallBiz.checkRepeat(Firewall.class, checkMap, id);
            if (!firewallNameRepeat){//查重(名称)
                return BaseRestControl.tranReturnValue(ResultType.repeat);
            }
            return BaseRestControl.tranReturnValue(ResultType.success);
        } catch (Exception e) {
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    private Object save(Firewall entity, String projectId) {
        request.setAttribute("name", entity.getName());
        CloudosClient client = this.getSessionBean().getCloudClient();
        if (null == client) {
            return BaseRestControl.tranReturnValue(ResultType.system_error);
        }
        if (!vdcBiz.checkLock(projectId)){
            return BaseRestControl.tranReturnValue(ResultType.vdc_option_lock);
        }
        entity.setTenantId(projectId);
        entity.createdUser(this.getLoginUser());
        ResultType veRs = firewallBiz.verify(entity);//校验参数
        if (!ResultType.success.equals(veRs)){
            return BaseRestControl.tranReturnValue(veRs);
        }
        //默认创建一个同名的规则集
        Policie policie = new Policie(entity);
        String rs = firewallBiz.cloudSave(entity, policie, client, null);//cloudos保存
        if ("success".equals(rs)){
            String vdcId = vdcBiz.getVdc(projectId).getId();
            try {
                firewallBiz.localSave(entity, policie, vdcId, entity.getStatus());//本地保存
                request.setAttribute("id", entity.getId());
                this.warn("Save firewall, firewall:" + entity);
                return BaseRestControl.tranReturnValue(ResultType.success);
            } catch (Exception e) {//本地异常时回删cloudos资源
                firewallBiz.cloudDelete(entity.getCloudosId(), policie.getCloudosId(), client);
                this.exception(Firewall.class, e, entity);
                return BaseRestControl.tranReturnValue(ResultType.failure);
            }
        }
        return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, rs);
    }
    
    @ApiOperation(value = "防火墙数量")
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public Object count() {
    	int count=0;
       if(this.getSessionBean().getSuperUser()){ // 超级管理员显示所有
    	   count=firewallBiz.count(Firewall.class,StrUtils.createMap());
       }else{  //非超级管理员显示当前租户拥有
    	   count=firewallBiz.count(Firewall.class, StrUtils.createMap("tenantId",this.getProjectId()));
       }
       return BaseRestControl.tranReturnValue(count);
    }
}
