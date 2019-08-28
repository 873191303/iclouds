package com.h3c.iclouds.rest;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseChildRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.PolicieBiz;
import com.h3c.iclouds.biz.PolicieRuleBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.RouteBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.Policie;
import com.h3c.iclouds.po.PolicieRule;
import com.h3c.iclouds.po.Route;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.IpValidator;
import com.h3c.iclouds.utils.ResourceHandle;
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
@Api(value = "云管理防火墙策略规则")
@RestController
@RequestMapping("/{policieId}/policieRule")
public class PolicieRuleRest extends BaseChildRest<PolicieRule> {

    @Resource
    private PolicieRuleBiz policieRuleBiz;
    
    @Resource
    private PolicieBiz policieBiz;

    @Resource
    private ProjectBiz projectBiz;

    @Resource
    private RouteBiz routeBiz;

    @Override
    @ApiOperation(value = "获取云管理防火墙策略规则列表", response = PolicieRule.class)
    @RequestMapping(method = RequestMethod.GET)
    public Object list(@PathVariable String policieId) {
        PageEntity entity = this.beforeList();
        entity.setSpecialParams(new String[]{policieId});
        PageModel<PolicieRule> pageModel = policieRuleBiz.findForPage(entity);
        PageList<PolicieRule> page = new PageList<PolicieRule>(pageModel, entity.getsEcho());
        List<PolicieRule> data = page.getAaData();
        if (StrUtils.checkParam(data)){
            for (PolicieRule policieRule : data) {
                addParam(policieRule);
            }
        }
        return BaseRestControl.tranReturnValue(page);
    }

    @Override
    @ApiOperation(value = "获取云管理防火墙策略规则详细信息", response = PolicieRule.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String policieId, @PathVariable String id) {
        PolicieRule policieRule = policieRuleBiz.findById(PolicieRule.class, id);
        if (null != policieRule){
            if (!projectBiz.checkLookRole(policieRule.getTenantId())){
                return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
            }
            addParam(policieRule);
            return BaseRestControl.tranReturnValue(policieRule);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    @ApiOperation(value = "删除云管理防火墙策略规则", response = PolicieRule.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Object delete(@PathVariable String policieId, @PathVariable String id) {
        request.setAttribute("id", id);
        CloudosClient client = this.getSessionBean().getCloudClient();
        if (null == client){
            return BaseRestControl.tranReturnValue(ResultType.system_error);
        }
        PolicieRule policieRule = policieRuleBiz.findById(PolicieRule.class, id);
        if (!StrUtils.checkParam(policieRule)) {
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        request.setAttribute("name", policieRule.getName());
        String projectId = policieRule.getTenantId();
        if (!projectBiz.checkOptionRole(projectId)){
            return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
        }
        Policie policie = policieBiz.findById(Policie.class, policieId);
        if (null == policie){
            return BaseRestControl.tranReturnValue(ResultType.policie_not_exist);
        }
        try {
            String ruleCdId = policieRule.getCloudosId();
            String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_POLICIE_RULE_ACTION), ruleCdId);
            JSONObject getResponse = client.get(uri);//优先判断资源在cloudos是否存在
            if (ResourceHandle.judgeResponse(getResponse)){
                //调用cloudos接口删除规则集(先将规则从规则集移除)
                String rs = policieRuleBiz.cloudDelete(ruleCdId, policie.getCloudosId(), client);
                if (!"success".equals(rs)) {
                    return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, rs);
                }
            }
            policieRuleBiz.delete(id);
            this.warn("Delete policie rule, ruleId:" + id);
            return BaseRestControl.tranReturnValue(ResultType.success);
        } catch (Exception e) {
            this.exception(PolicieRule.class, e, id);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @Override
    @ApiOperation(value = "保存云管理防火墙策略规则", response = PolicieRule.class)
    @RequestMapping(method = RequestMethod.POST)
    public Object save(@PathVariable String policieId, @RequestBody PolicieRule entity) {
        request.setAttribute("name", entity.getName());
        CloudosClient client = this.getSessionBean().getCloudClient();
        if (null == client){
            return BaseRestControl.tranReturnValue(ResultType.system_error);
        }
        if (!projectBiz.checkRole() && !this.getSessionBean().getSuperUser()){
            return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
        }
        Policie policie = policieBiz.findById(Policie.class, policieId);//验证规则集是否存在
        if (null == policie){
            return BaseRestControl.tranReturnValue(ResultType.policie_not_exist);
        }
        List<Route> routes = routeBiz.findByPropertyName(Route.class, "fwId", policie.getFwId());
        if (!StrUtils.checkParam(routes)){
            return BaseRestControl.tranReturnValue(ResultType.not_relate_route);
        }
        if (!BaseRestControl.checkStatus(policie.getStatus())){
            return BaseRestControl.tranReturnValue(ResultType.parent_status_exception);
        }
        JSONObject pyJson = policieBiz.getPolicyJson(policie.getCloudosId(), client);
        if (!StrUtils.checkParam(pyJson)) {
            return BaseRestControl.tranReturnValue(ResultType.firewall_not_exist_in_cloudos);
        }
        Map<String, Object> checkMap = new HashMap<>();
        checkMap.put("name", entity.getName());
        checkMap.put("policyId", policieId);
        if (!policieRuleBiz.checkRepeat(PolicieRule.class, checkMap, null)){//查重(名称)
            return BaseRestControl.tranReturnValue(ResultType.name_repeat);
        }
        entity.setPolicyId(policieId);//插入属性值（必须先插入后验证，因为该字段有非空验证）
        try {
            ResultType rs = checkParam(entity);
            if (!ResultType.success.equals(rs)){
				return BaseRestControl.tranReturnValue(rs);
			}
        } catch (NumberFormatException e) {
            this.exception(PolicieRule.class, e);
            return BaseRestControl.tranReturnValue(ResultType.port_format_error);
        }
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);//验证数据
        if (!validatorMap.isEmpty()){
            return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
        }
        entity.setTenantId(policie.getTenantId());
        entity.createdUser(this.getLoginUser());
        entity.setPosition(getPosition(entity.getBeforeId(), entity.getAfterId()));
        //调用cloudos接口创建规则集规则
        String saveRs = policieRuleBiz.cloudSave(entity, policie.getCloudosId(), client);
        if (!"success".equals(saveRs)) {
            return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, saveRs);
        } else {
            try {
                policieRuleBiz.save(entity);
                request.setAttribute("id", entity.getId());
                this.warn("Save policie rule, rule:" + entity);
                return BaseRestControl.tranReturnValue(ResultType.success, entity);
            } catch (Exception e) {
                policieRuleBiz.cloudDelete(entity.getCloudosId(), policie.getCloudosId(), client);
                this.exception(PolicieRule.class, e, entity);
                return BaseRestControl.tranReturnValue(ResultType.failure);
            }
        }
    }

    @Override
    public Object update(@PathVariable String policieId, @PathVariable String id, @RequestBody PolicieRule entity) throws IOException {
        return null;
    }

    @ApiOperation(value = "检查名称是否重复")
    @RequestMapping(value = "/check/{name}", method = RequestMethod.GET)
    public Object checkRepeat(@PathVariable String policieId, @PathVariable String name){
        boolean ruleNameRepeat = false;
        String id = request.getParameter("id");//修改时传入一个id则查重时会排除对象本身
        try {
            Map<String, Object> checkMap = new HashMap<>();
            checkMap.put("name", name);
            checkMap.put("policyId", policieId);
            ruleNameRepeat = policieRuleBiz.checkRepeat(PolicieRule.class, checkMap, id);
            if (!ruleNameRepeat){//查重(名称)
                return BaseRestControl.tranReturnValue(ResultType.repeat);
            }
            return BaseRestControl.tranReturnValue(ResultType.success);
        } catch (Exception e) {
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    /**
     * 校验参数
     * @param policieRule
     * @return
     */
    public ResultType checkParam(PolicieRule policieRule){
        Map<String, Object> checkMap = new HashMap<>();
        checkMap.put("name", policieRule.getName());
        checkMap.put("tenantId", this.getProjectId());
        if (!policieRuleBiz.checkRepeat(PolicieRule.class, checkMap, null)){//查重(名称)
            return ResultType.name_repeat;
        }
        String sourcePort = policieRule.getSourcePort();
        String destinationPort = policieRule.getDestinationPort();
        String sourceIp = policieRule.getSourceIp();
        String destinationIp = policieRule.getDestinationIp();
        String beforeId = policieRule.getBeforeId();
        String afterId = policieRule.getAfterId();
        if (StrUtils.checkParam(beforeId)){//检查前于规则
            PolicieRule beforeRule = policieRuleBiz.findById(PolicieRule.class, beforeId);
            if (!StrUtils.checkParam(beforeRule)){
                return ResultType.before_rule_not_exist;
            }
        }else {
            if (StrUtils.checkParam(afterId)){//检查后于规则
                PolicieRule afterRule = policieRuleBiz.findById(PolicieRule.class, afterId);
                if (!StrUtils.checkParam(afterRule)){
                    return ResultType.after_rule_not_exist;
                }
            }
        }
        if (StrUtils.checkParam(sourceIp)){//校验源ip
            if (!IpValidator.checkIpOrCidr(sourceIp)){
                return ResultType.source_ip_format_error;
            }
        }
        if (StrUtils.checkParam(destinationIp)){//校验目标ip
            if (!IpValidator.checkIpOrCidr(destinationIp)){
                return ResultType.destination_ip_format_error;
            }
        }
        if ("TCP".equals(policieRule.getProtocol()) || "UDP".equals(policieRule.getProtocol()))
        {//协议为tcp或udp时校验源端口和目标端口格式
            if (StrUtils.checkParam(sourcePort)){//校验源端口
                if (!checkPort(sourcePort, policieRule, "source")){
                    return ResultType.port_format_error;
                }
            }
            if (StrUtils.checkParam(destinationPort)){//校验目标端口
                if (!checkPort(destinationPort, policieRule, "destination")){
                    return ResultType.port_format_error;
                }
            }
        }else {
            policieRule.setSourcePort(null);
            policieRule.setDestinationPort(null);
        }
        return ResultType.success;
    }

    /**
     * 校验端口参数
     * @param port
     * @return
     */
    public boolean checkPort(String port, PolicieRule policieRule, String type){
        String [] portAttr = port.split(":");
        if (portAttr.length == 1){
            int portInt = Integer.parseInt(port);
            if (portInt >= 0 && portInt <= 65535){
                if ("source".equals(type)){
                    policieRule.setSourcePortRangeMin(portInt);
                    policieRule.setSourcePortRangeMax(portInt);
                }
                if ("destination".equals(type)){
                    policieRule.setDestinationPortRangeMin(portInt);
                    policieRule.setDestinationPortRangeMax(portInt);
                }
                return true;
            }
        }
        if (portAttr.length == 2){
            String startPort = portAttr[0];
            String endPort = portAttr[1];
            int startInt = Integer.parseInt(startPort);
            int endInt = Integer.parseInt(endPort);
            if (startInt >= 0 && startInt <= 65535){
                if (endInt >= 0 && endInt <= 65535){
                    if (startInt <= endInt){
                        if ("source".equals(type)){
                            policieRule.setSourcePortRangeMin(startInt);
                            policieRule.setSourcePortRangeMax(endInt);
                        }
                        if ("destination".equals(type)){
                            policieRule.setDestinationPortRangeMin(startInt);
                            policieRule.setDestinationPortRangeMax(endInt);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取规则位置
     * @param beforeId
     * @param afterId
     * @return
     */
    public int getPosition(String beforeId, String afterId){
        int position = 1;//默认为1
        if (StrUtils.checkParam(beforeId)){//如果有前于规则将位置放入前于规则的位置,其后的规则位置都加1
            PolicieRule beforeRule = policieRuleBiz.findById(PolicieRule.class, beforeId);
            position = beforeRule.getPosition();
        }else {
            if (StrUtils.checkParam(afterId)){//如果有后于规则将位置放入后于规则的位置后面,其后的规则位置都加1
                PolicieRule afterRule = policieRuleBiz.findById(PolicieRule.class, afterId);
                position = afterRule.getPosition() + 1;
            }
        }
        return position;
    }

    public void addParam(PolicieRule policieRule){
        Integer minSPort = policieRule.getSourcePortRangeMin();
        Integer maxSPort = policieRule.getSourcePortRangeMax();
        if (StrUtils.checkParam(minSPort)){
            if (StrUtils.checkParam(maxSPort) && !minSPort.equals(maxSPort)){
                policieRule.setSourcePort(minSPort + ":" + maxSPort);
            }else {
                policieRule.setSourcePort(minSPort + "");
            }
        }
        Integer minDPort = policieRule.getDestinationPortRangeMin();
        Integer maxDPort = policieRule.getDestinationPortRangeMax();
        if (StrUtils.checkParam(minDPort)){
            if (StrUtils.checkParam(maxDPort) && !minDPort.equals(maxDPort)){
                policieRule.setDestinationPort(minDPort + ":" + maxDPort);
            }else {
                policieRule.setDestinationPort(minDPort + "");
            }
        }
    }

}
