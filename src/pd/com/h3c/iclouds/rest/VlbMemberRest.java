package com.h3c.iclouds.rest;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseChildRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.SpePortBiz;
import com.h3c.iclouds.biz.VlbMemberBiz;
import com.h3c.iclouds.biz.VlbPoolBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.SpePort;
import com.h3c.iclouds.po.VlbMember;
import com.h3c.iclouds.po.VlbPool;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.HttpUtils;
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
@Api(value = "云管理负载均衡成员池")
@RestController
@RequestMapping("/{poolId}/vlbMember")
public class VlbMemberRest extends BaseChildRest<VlbMember> {

    @Resource
    private VlbMemberBiz vlbMemberBiz;

    @Resource
    private VlbPoolBiz vlbPoolBiz;

    @Resource
    private ProjectBiz projectBiz;

    @Resource
    private SpePortBiz spePortBiz;
    
    @Override
    @ApiOperation(value = "获取云管理负载均衡成员池分页列表", response = VlbMember.class)
    @RequestMapping(method = RequestMethod.GET)
    public Object list(@PathVariable String poolId) {
        PageEntity entity = this.beforeList();
        entity.setSpecialParams(new String[]{poolId});
        PageModel<VlbMember> pageModel = vlbMemberBiz.findForPage(entity);
        PageList<VlbMember> page = new PageList<VlbMember>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

    @Override
    @ApiOperation(value = "获取云管理负载均衡成员池详细信息", response = VlbMember.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String poolId, @PathVariable String id) {
        VlbMember vlbMember = vlbMemberBiz.findById(VlbMember.class, id);
        if (null != vlbMember){
            if (!projectBiz.checkLookRole(vlbMember.getTenantId())){
                return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
            }
            return BaseRestControl.tranReturnValue(vlbMember);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @ApiOperation(value = "批量保存云管理负载均衡成员池", response = VlbMember.class)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Object save(@PathVariable String poolId, @RequestBody List<VlbMember> vlbMembers) {
        CloudosClient client = this.getSessionBean().getCloudClient();
        if (null == client){
            return BaseRestControl.tranReturnValue(ResultType.system_error);
        }
        if (!projectBiz.checkRole() && !this.getSessionBean().getSuperUser()){
            return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
        }
        VlbPool vlbPool = vlbPoolBiz.findById(VlbPool.class, poolId);
        if (null == vlbPool){
            return BaseRestControl.tranReturnValue(ResultType.vlbPool_not_exist);
        }
        if (!BaseRestControl.checkStatus(vlbPool.getStatus())) {
            return BaseRestControl.tranReturnValue(ResultType.parent_status_exception);
        }
        JSONObject poolJson = vlbPoolBiz.getPoolJson(vlbPool.getCloudosId(), client);
        if (!StrUtils.checkParam(poolJson)) {
            return BaseRestControl.tranReturnValue(ResultType.vlbpool_not_exist_in_cloudos);
        }
        for (VlbMember entity : vlbMembers) {
            entity.setPoolId(poolId);
            Map<String, String> validatorMap = ValidatorUtils.validator(entity);
            if (!validatorMap.isEmpty()){
                return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
            }
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("ip", entity.getAddress());
            queryMap.put("tenantId", vlbPool.getTenantId());
            int count = spePortBiz.count(SpePort.class, queryMap);
            if (count > 0) {//检查该ip的网卡是不是被限制使用
                return BaseRestControl.tranReturnValue(ResultType.port_restricted);
            }
            entity.setTenantId(vlbPool.getTenantId());
            entity.createdUser(this.getLoginUser());
            //调用cloudos接口创建实服务
            Map<String, Object> dataMap = ResourceHandle.tranToMap(entity, ConfigProperty.RESOURCE_TYPE_VLBMEMBER);
            dataMap.put("pool_id", vlbPool.getCloudosId());
            Map<String, Object> params = ResourceHandle.getParamMap(dataMap, "member");//转换成跟cloudos对接的参数
            String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VLBMEMBER);//获取uri
            JSONObject response = client.post(uri, params);//创建规则集
            if (!ResourceHandle.judgeResponse(response)){//请求失败时,抛出异常
                return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, HttpUtils.getError(response));
            }
            String cloudMemId = ResourceHandle.getId(response, "member");//获取实服务真实id
            entity.setCloudosId(cloudMemId);
            entity.setStatus(ConfigProperty.TASK_STATUS3_END_SUCCESS);
            try {
                vlbMemberBiz.add(entity);
                request.setAttribute("id", entity.getId());
                this.warn("Save vlbMember, vlbMembers:" + vlbMembers);
            } catch (Exception e) {
                String deUri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams
                        .CLOUDOS_API_VLBMEMBER_ACTION), cloudMemId);
                client.delete(deUri);//cloudos删除
                this.exception(VlbMember.class, e, vlbMembers);
                return BaseRestControl.tranReturnValue(ResultType.failure);
            }
            
        }
        return BaseRestControl.tranReturnValue(ResultType.success);
    }

    @Override
    @ApiOperation(value = "删除云管理负载均衡成员池", response = VlbMember.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Object delete(@PathVariable String poolId, @PathVariable String id) {
        request.setAttribute("id", id);
        CloudosClient client = this.getSessionBean().getCloudClient();
        if (null == client){
            return BaseRestControl.tranReturnValue(ResultType.system_error);
        }
        VlbMember vlbMember = vlbMemberBiz.findById(VlbMember.class, id);
        if (null != vlbMember){
            String projectId = vlbMember.getTenantId();
            if (!projectBiz.checkOptionRole(projectId)){
                return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
            }
            try {
                String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VLBMEMBER_ACTION), vlbMember.getCloudosId());
                JSONObject getResponse = client.get(uri);
                if (ResourceHandle.judgeResponse(getResponse)){//优先判断资源在cloudos是否存在
                    JSONObject response = client.delete(uri);//cloudos删除
                    if (!ResourceHandle.judgeResponse(response)){//请求失败时,抛出异常
                        return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, HttpUtils.getError(response));
                    }
                }
                vlbMemberBiz.delete(vlbMember);
                this.warn("Delete vlbMember, vlbMemberId:" + id);
                return BaseRestControl.tranReturnValue(ResultType.success);
            } catch (Exception e) {
                this.exception(VlbMember.class, e, id);
                return BaseRestControl.tranReturnValue(ResultType.failure);
            }
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    public Object save(@PathVariable String poolId, @RequestBody VlbMember entity) {
        return null;
    }

    @Override
    public Object update(@PathVariable String poolId, @PathVariable String id, @RequestBody VlbMember entity) throws IOException {
        return null;
    }




}
