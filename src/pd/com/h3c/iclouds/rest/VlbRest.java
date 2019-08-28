package com.h3c.iclouds.rest;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.QuotaUsedBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.biz.VdcBiz;
import com.h3c.iclouds.biz.VlbBiz;
import com.h3c.iclouds.biz.VlbPoolBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.HealthMonitor;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.Vlb;
import com.h3c.iclouds.po.VlbPool;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.HttpUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Api(value = "云管理负载均衡")
@RestController
@RequestMapping("/vlb")
public class VlbRest extends BaseRest<Vlb> {

    @Resource
    private VlbBiz vlbBiz;

    @Resource(name = "baseDAO")
    private BaseDAO<HealthMonitor> healthMonitorDao;

    @Resource
    private VlbPoolBiz vlbPoolBiz;

    @Resource
    private ProjectBiz projectBiz;

    @Resource
    private UserBiz userBiz;
    
    @Resource
    private VdcBiz vdcBiz;
    
    @Resource
    private QuotaUsedBiz quotaUsedBiz;
    
    @Override
    @ApiOperation(value = "获取云管理负载均衡分页列表", response = Vlb.class)
    @RequestMapping(method = RequestMethod.GET)
    public Object list() {
        PageEntity entity = this.beforeList();
        PageModel<Vlb> pageModel = vlbBiz.findForPage(entity);
        PageList<Vlb> page = new PageList<>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

    @Override
    @ApiOperation(value = "获取云管理负载均衡详细信息", response = Vlb.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String id) {
        Vlb vlb = vlbBiz.findById(Vlb.class, id);
        if (null != vlb){
            if (!projectBiz.checkLookRole(vlb.getProjectId())){
                return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
            }
            return BaseRestControl.tranReturnValue(vlb);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    @ApiOperation(value = "删除云管理负载均衡", response = Vlb.class)
    //@Perms(value = "vdc.ope.vlb")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Object delete(@PathVariable String id) {
        request.setAttribute("id", id);
        CloudosClient client = this.getSessionBean().getCloudClient();
        if (null == client){
            return BaseRestControl.tranReturnValue(ResultType.system_error);
        }
        Vlb vlb = vlbBiz.findById(Vlb.class, id);
        if (null != vlb){
            request.setAttribute("name", vlb.getName());
            String projectId = vlb.getProjectId();
            if (!projectBiz.checkOptionRole(projectId)){
                return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
            }
            List<HealthMonitor> healthMonitors = healthMonitorDao.findByPropertyName(HealthMonitor.class, "lbId", id);
            if (null != healthMonitors && healthMonitors.size() > 0){
                return BaseRestControl.tranReturnValue(ResultType.still_relate_healthMonitor);
            }
            List<VlbPool> vlbPools = vlbPoolBiz.findByPropertyName(VlbPool.class, "lbId", id);
            if (null != vlbPools && vlbPools.size() > 0){
                return BaseRestControl.tranReturnValue(ResultType.still_relate_vlbPool);
            }
            try {
                String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VLB_ACTION);//获取uri
                uri = HttpUtils.tranUrl(uri, vlb.getCloudosId());
                JSONObject getResponse = client.get(uri);
                if (ResourceHandle.judgeResponse(getResponse)){//优先判断资源在cloudos是否存在
                    //调用cloudos接口删除负载均衡组
                    JSONObject response = client.delete(uri);//删除负载均衡组
                    if (!ResourceHandle.judgeResponse(response)){//请求失败时,抛出异常
                        return BaseRestControl.tranReturnValue(ResultType.failure, response.getString("record"));
                    }
                }
                vlbBiz.delete(vlb);
                this.warn(id);
                quotaUsedBiz.change(ConfigProperty.VLB_QUOTA_CLASSCODE, projectId, false, 1);
                return BaseRestControl.tranReturnValue(ResultType.success);
            } catch (Exception e) {
                this.exception(Vlb.class, e, id);
                return BaseRestControl.tranReturnValue(ResultType.failure);
            }
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    @ApiOperation(value = "保存云管理负载均衡", response = Vlb.class)
    //@Perms(value = "vdc.ope.vlb")
    @RequestMapping(method = RequestMethod.POST)
    public Object save(@RequestBody Vlb entity) {
        if (!projectBiz.checkRole()) {
            return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
        }
        entity.setOwner(this.getLoginUser());
        return this.save(entity, this.getProjectId());
    }
    
    @ApiOperation(value = "管理员保存租户负载均衡", response = Vlb.class)
    @RequestMapping(value = "/admin/save/{userId}", method = RequestMethod.POST)
    public Object save(@PathVariable String userId, @RequestBody Vlb entity) {
        User user = userBiz.findById(User.class, userId);
        Map<String, Object> check = this.adminSave(entity.getProjectId(), user);
        if (null != check) {
            return check;
        }
        entity.setOwner(userId);
        return this.save(entity, user.getProjectId());
    }
    
    @Override
    @ApiOperation(value = "修改云管理负载均衡", response = Vlb.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    //@Perms(value = "vdc.ope.vlb")
    public Object update(@PathVariable String id, @RequestBody Vlb entity) throws IOException {
        request.setAttribute("id", id);
        CloudosClient client = this.getSessionBean().getCloudClient();
        if (null == client){
            return BaseRestControl.tranReturnValue(ResultType.system_error);
        }
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);
        if (validatorMap.isEmpty()){
            Vlb vlb = vlbBiz.findById(Vlb.class, id);
            if (null != vlb){
                request.setAttribute("name", vlb.getName());
                String projectId = vlb.getProjectId();
                if (!projectBiz.checkOptionRole(projectId)){
                    return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
                }
                Map<String, Object> checkMap = new HashMap<>();
                checkMap.put("name", entity.getName());
                checkMap.put("tenantId", this.getProjectId());
                if (!vlbBiz.checkRepeat(Vlb.class, checkMap, id)){//查重(名称)
                    return BaseRestControl.tranReturnValue(ResultType.name_repeat);
                }
                InvokeSetForm.copyFormProperties(entity, vlb);
                vlb.updatedUser(this.getLoginUser());
                try {
                    //调用cloudos接口修改负载均衡组
                    String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VLB_ACTION), vlb.getCloudosId());
                    JSONObject vlbObj = client.get(uri);
                    if (!ResourceHandle.judgeResponse(vlbObj)){
                        return BaseRestControl.tranReturnValue(vlbObj.get("record"));
                    }
                    JSONObject record = vlbObj.getJSONObject("record");
                    record.put("name", vlb.getName());
                    record.put("description", vlb.getDescription());
                    record.put("throughPut", vlb.getThroughPut());
                    JSONObject response = client.put(uri, record);//修改负载均衡组
                    if (!ResourceHandle.judgeResponse(response)){//请求失败时,抛出异常
                        return BaseRestControl.tranReturnValue(ResultType.failure, response.getString("record"));
                    }
                    vlbBiz.update(vlb);
                    this.warn(entity);
                    request.setAttribute("name", entity.getName());
                    return BaseRestControl.tranReturnValue(ResultType.success, vlb);
                } catch (Exception e) {
                    this.exception(Vlb.class, e, entity);
                    return BaseRestControl.tranReturnValue(ResultType.failure);
                }
            }
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
    }

    @ApiOperation(value = "检查名称是否重复")
    @RequestMapping(value = "/check/{name}", method = RequestMethod.GET)
    //@Perms(value = "vdc.ope.vlb.simple")
    public Object checkRepeat(@PathVariable String name){
        boolean vlbNameRepeat = false;
        String id = request.getParameter("id");//修改时传入一个id则查重时会排除对象本身
        String projectId = request.getParameter("projectId");//超级管理员操作租户资源时传入租户id进行查重
        if (!this.getSessionBean().getSuperUser() || !StrUtils.checkParam(projectId)) {
            projectId = this.getProjectId();
        }
        try {
            Map<String, Object> checkMap = new HashMap<>();
            checkMap.put("name", name);
            checkMap.put("projectId", projectId);
            vlbNameRepeat = vlbBiz.checkRepeat(Vlb.class, checkMap, id);
            if (!vlbNameRepeat){//查重(名称)
                return BaseRestControl.tranReturnValue(ResultType.name_repeat);
            }
            return BaseRestControl.tranReturnValue(ResultType.success);
        } catch (Exception e) {
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }
    
    private Object save(Vlb entity, String projectId) {
        request.setAttribute("name", entity.getName());
        CloudosClient client = this.getSessionBean().getCloudClient();
        if (null == client){
            return BaseRestControl.tranReturnValue(ResultType.system_error);
        }
        ResultType resultType = quotaUsedBiz.checkQuota(ConfigProperty.VLB_QUOTA_CLASSCODE, projectId, 1);
        if (!ResultType.success.equals(resultType)){//检查租户是否拥有配额以及是否达到最大值
            return BaseRestControl.tranReturnValue(resultType);
        }
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);
        if (!validatorMap.isEmpty()){
            return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
        }
        Map<String, Object> checkMap = new HashMap<>();
        checkMap.put("name", entity.getName());
        checkMap.put("projectId", projectId);
        if (!vlbBiz.checkRepeat(Vlb.class, checkMap, null)){//查重(名称)
            return BaseRestControl.tranReturnValue(ResultType.name_repeat);
        }
        entity.setProjectId(projectId);
        entity.createdUser(this.getLoginUser());
        entity.setVdcId(vdcBiz.getVdc(projectId).getId());
		//调用cloudos接口创建负载均衡组
		Map<String, Object> paramsMap = ResourceHandle.tranToMap(entity, ConfigProperty.RESOURCE_TYPE_VLB);
		User user = userBiz.findById(User.class, entity.getOwner());
		paramsMap.put("userId", user.getCloudosId());
		paramsMap.put("creator", this.getSessionBean().getUserName());
		paramsMap.put("createTime", entity.getCreatedDate());
		String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VLB);//获取uri
		JSONObject response = client.post(uri, paramsMap);//创建负载均衡组
		if (!ResourceHandle.judgeResponse(response)){//请求失败时,抛出异常
			return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, HttpUtils.getError(response));
		}
		String cloudLbId = ResourceHandle.getId(response, null);//获取真实id
		entity.setCloudosId(cloudLbId);
		try {
            vlbBiz.add(entity);
            this.warn("Save Vlb, vlb:" + entity);
            quotaUsedBiz.change(ConfigProperty.VLB_QUOTA_CLASSCODE, projectId, true, 1);//将资源使用配额量加1
            return BaseRestControl.tranReturnValue(ResultType.success, entity);
        } catch (Exception e) {
			uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VLB_ACTION), cloudLbId);
			client.delete(uri);
            this.exception(Vlb.class, e, entity);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }
    
}
