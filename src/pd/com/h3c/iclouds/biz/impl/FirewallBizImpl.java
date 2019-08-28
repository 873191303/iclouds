package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.FirewallBiz;
import com.h3c.iclouds.biz.QuotaUsedBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.FirewallDao;
import com.h3c.iclouds.dao.PolicieDao;
import com.h3c.iclouds.dao.PolicieRuleDao;
import com.h3c.iclouds.dao.RouteDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.Firewall;
import com.h3c.iclouds.po.Fw2Policie;
import com.h3c.iclouds.po.Policie;
import com.h3c.iclouds.po.PolicieRule;
import com.h3c.iclouds.po.Route;
import com.h3c.iclouds.po.Vdc2Fw;
import com.h3c.iclouds.po.VdcItem;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.VdcHandle;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Service("firewallBiz")
public class FirewallBizImpl extends BaseBizImpl<Firewall> implements FirewallBiz {

    @Resource
    private FirewallDao firewallDao;

    @Resource(name = "baseDAO")
    private BaseDAO<Fw2Policie> fw2PolicieDao;

    @Resource(name = "baseDAO")
    private BaseDAO<Vdc2Fw> vdc2FwDao;

    @Resource
    private RouteDao routeDao;

    @Resource
    private PolicieDao policieDao;

    @Resource(name = "baseDAO")
    private BaseDAO<VdcItem> vdcItemDao;

    @Resource
    private PolicieRuleDao policieRuleDao;
    
    @Resource
    private QuotaUsedBiz quotaUsedBiz;
    
    @Override
    public PageModel<Firewall> findForPage(PageEntity entity) {
        return firewallDao.findForPage(entity);
    }

    @Override
    public Map<String, Object> update(String id, Firewall entity, CloudosClient client) {
        Firewall firewall = firewallDao.findById(Firewall.class, id);
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);//验证数据
        if(!validatorMap.isEmpty()) {//校验参数
            return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
        }
        if (!BaseRestControl.checkStatus(firewall.getStatus())){//检查状态
            return BaseRestControl.tranReturnValue(ResultType.status_exception);
        }
        entity.updatedUser(this.getLoginUser());
        JSONObject fwJson = getFirewallJson(firewall.getCloudosId(), client);
        String status = fwJson.getString("status");
        entity.setStatus(status);
        if (!StrUtils.checkParam(fwJson)) {//检查cloudos是否存在
            return BaseRestControl.tranReturnValue(ResultType.firewall_not_exist_in_cloudos);
        }
        String upRs = cloudUpdate(entity, id, client);//cloudos修改
        if ("success".equals(upRs)){
            localUpdate(id, entity);//本地修改
            return BaseRestControl.tranReturnValue(ResultType.success);
        } else {
            return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, upRs);
        }
    }

    @Override
    public Map<String, Object> delete(String id, CloudosClient client) {
        Firewall firewall = firewallDao.findById(Firewall.class, id);
        List<Route> routes = routeDao.findByPropertyName(Route.class, "fwId", id);
        if (null != routes && routes.size() > 0) {//检查是否关联路由器
            return BaseRestControl.tranReturnValue(ResultType.still_relate_route);
        }
        String [] policyIds = getPolicyIds(id);
        if (policyIds != null && policyIds.length > 0){
            for (String policyId : policyIds) {
                List<PolicieRule> policieRules = policieRuleDao.findByPropertyName(PolicieRule.class, "policyId",
                        policyId);
                if (StrUtils.checkParam(policieRules)){
                    return BaseRestControl.tranReturnValue(ResultType.still_relate_policieRule);
                }
            }
        }
        String fwfCdId = firewall.getCloudosId();
        JSONObject fwJson = getFirewallJson(fwfCdId, client);
        if (StrUtils.checkParam(fwJson)) {
            if (StrUtils.checkParam(fwJson.get("router_ids"))){
                fwJson.put("router_ids", new ArrayList<String>());
                client.put(HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_FIREWALL_ACTION), fwfCdId),
                        fwJson);
            }
            String deRs = cloudDelete(fwfCdId, firewall.getPolicyCloudosId(), client);
            if (!"success".equals(deRs)){
                return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, deRs);
            }
        }
        localDelete(firewall);
        return BaseRestControl.tranReturnValue(ResultType.success);
    }

    public void localSave(Firewall entity, Policie policie, String vdcId, String status){
        entity.setStatus(status);
        policie.setStatus(status);
        String policieId = policieDao.add(policie);
        String firewallId = firewallDao.add(entity);//保存防火墙
        Vdc2Fw vdc2Fw = new Vdc2Fw();
        vdc2Fw.setFirewallId(firewallId);//vdc与防火墙关联关系属性赋值
        vdc2Fw.createdUser(entity.getCreatedBy());
        vdc2Fw.setVdcId(vdcId);
        vdc2Fw.setThroughPut(entity.getThroughPut());
        vdc2Fw.setSequence(entity.getSequence());
        vdc2FwDao.add(vdc2Fw);//增加防火墙与vdc的映射关系表
        addFw2Policie(firewallId, policieId);//创建规则集与防火墙关系数据
        quotaUsedBiz.change(ConfigProperty.FIREWALL_QUOTA_CLASSCODE, entity.getTenantId(), true, 1);//将资源使用配额量加1
        new VdcHandle().saveViewAndItem(firewallId, entity.getUuid(), entity.getName(), vdcId, vdcId, ConfigProperty.RESOURCE_TYPE_FIREWALL, null, status);//创建vdc在vdc视图和视图对象里面的数据);//数据同步至视图相关表
    }

    public String cloudSave(Firewall entity, Policie policie, CloudosClient client, String type){
        String plDeUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_POLICIE_ACTION);
        String fwDeUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_FIREWALL_ACTION);
        //调用cloudos接口创建规则集
        Map<String, Object> plDataMap = ResourceHandle.tranToMap(policie, ConfigProperty.RESOURCE_TYPE_FIREWALL_POLICY);
        Map<String, Object> plParams = ResourceHandle.getParamMap(plDataMap, "firewall_policy");//转换成跟cloudos对接的参数
        String plUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_POLICIE);//获取uri
        JSONObject plResponse = client.post(plUri, plParams);//创建规则集
        if (!ResourceHandle.judgeResponse(plResponse)){//请求失败时,抛出异常
            return HttpUtils.getError(plResponse);
        }
        String cloudPoliId = ResourceHandle.getId(plResponse, "firewall_policy");//获取规则集真实id
        plDeUri = HttpUtils.tranUrl(plDeUri, cloudPoliId);
        //调用cloudos接口创建防火墙
        Map<String, Object> fwDataMap = ResourceHandle.tranToMap(entity, ConfigProperty.RESOURCE_TYPE_FIREWALL);
        fwDataMap.put("firewall_policy_id", cloudPoliId);
        Map<String, Object> fwParams = ResourceHandle.getParamMap(fwDataMap, "firewall");//转换成跟cloudos对接的参数
        String fwUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_FIREWALL);//获取uri
        JSONObject fwResponse = client.post(fwUri, fwParams);//创建规则集
        if (!ResourceHandle.judgeResponse(fwResponse)){//请求失败时,删除cloudos创建的规则集并抛出异常
            client.delete(plDeUri);//删除cloudos创建的规则集
            return HttpUtils.getError(fwResponse);
        }
        String cloudFwId = ResourceHandle.getId(fwResponse, "firewall");//获取防火墙真实id
        String status = ResourceHandle.getParam(fwResponse, "firewall", "status");//获取防火墙真实id
        fwDeUri = HttpUtils.tranUrl(fwDeUri, cloudFwId);
        //调用cloudos接口创建防火墙吞吐量
        Map<String, Object> normParams = new HashMap<>();
        normParams.put("firewallId", cloudFwId);
        normParams.put("name", entity.getName());
        normParams.put("throughPut", entity.getThroughPut());
        normParams.put("tenantId", entity.getTenantId());
        String fwNormUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_FIREWALL_NORMS);
        JSONObject normResponse = client.post(fwNormUri, normParams);
        if (!ResourceHandle.judgeResponse(normResponse)){//请求失败时,删除cloudos创建的规则集和防火墙并抛出异常
            client.delete(fwDeUri);//删除防火墙
            client.delete(plDeUri);//删除cloudos创建的规则集
            return HttpUtils.getError(normResponse);
        }
        entity.setCloudosId(cloudFwId);
        policie.setCloudosId(cloudPoliId);
        entity.setStatus(status);
        policie.setStatus(status);
        
        if (StrUtils.checkParam(type) && "vdc".equals(type)){
            firewallDao.update(entity);
            policieDao.update(policie);
            VdcItem vdcItem = vdcItemDao.findById(VdcItem.class, entity.getId());
            vdcItem.setStatus(status);
            vdcItemDao.update(vdcItem);
        }
        return "success";
    }

    public String cloudDelete(String cloudFwId, String cloudPyId, CloudosClient client){
        //与cloudos对接
        String fwUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_FIREWALL_ACTION);
        fwUri = HttpUtils.tranUrl(fwUri, cloudFwId);
        JSONObject response = client.delete(fwUri);//删除防火墙
        if (!ResourceHandle.judgeResponse(response)){//请求失败时,抛出异常
            return HttpUtils.getError(response);
        }
        String plDeUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_POLICIE_ACTION);
        plDeUri = HttpUtils.tranUrl(plDeUri, cloudPyId);
        client.delete(plDeUri);//删除规则集
        String normAcUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_FIREWALL_NORMS_ACTION);
        normAcUri = HttpUtils.tranUrl(normAcUri, cloudFwId);
        client.delete(normAcUri);
        return "success";
    }

    public void localDelete(Firewall firewall){
        //本地操作
        Map<String, Object> deleteMap = new HashMap<>();//删除map
        deleteMap.put("firewallId", firewall.getId());//添加删除条件
        fw2PolicieDao.delete(deleteMap, Fw2Policie.class);//删除防火墙与规则集关系数据
        vdc2FwDao.delete(deleteMap, Vdc2Fw.class);//删除该防火墙与vdc对应关系表
        firewallDao.delete(firewall);//删除防火墙
        policieDao.deleteById(Policie.class, firewall.getPolicyId());//删除规则集
        quotaUsedBiz.change(ConfigProperty.FIREWALL_QUOTA_CLASSCODE, firewall.getTenantId(), false, 1);//将资源使用配额量减1
        new VdcHandle().deleteViewAndItem(firewall.getId());//同时删除视图表和视图对象表
    }

    public String cloudUpdate(Firewall entity, String id, CloudosClient client){
        Firewall firewall = firewallDao.findById(Firewall.class, id);
        //与cloudos对接
        Map<String, Object> fwDataMap = ResourceHandle.tranToMap(entity, ConfigProperty.RESOURCE_TYPE_FIREWALL);
        fwDataMap.put("router_ids", getRouteIds(id));
        fwDataMap.remove("tenant_id");
        Map<String, Object> fwParams = ResourceHandle.getParamMap(fwDataMap, "firewall");//转换成跟cloudos对接的参数
        String fwUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_FIREWALL_ACTION);//获取uri
        fwUri = HttpUtils.tranUrl(fwUri, firewall.getCloudosId());
        JSONObject fwResponse = client.put(fwUri, fwParams);//修改防火墙
        if (!ResourceHandle.judgeResponse(fwResponse)){//请求失败时,抛出异常
            return HttpUtils.getError(fwResponse);
        }
        if (!entity.getThroughPut().equals(firewall.getThroughPut())){//如果吞吐量改变则调用cloudos接口修改吞吐量
            String normAcUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_FIREWALL_NORMS_ACTION);
            normAcUri = HttpUtils.tranUrl(normAcUri, firewall.getCloudosId());
            Map<String, Object> normParams = new HashMap<>();
            normParams.put("firewallId", firewall.getCloudosId());
            normParams.put("name", entity.getName());
            normParams.put("throughPut", entity.getThroughPut());
            JSONObject normResponse = client.put(normAcUri, normParams);//修改防火墙吞吐量
            if (!ResourceHandle.judgeResponse(normResponse)){//请求失败时,抛出异常
                return HttpUtils.getError(normResponse);
            }
        }
        return "success";
    }

    public void localUpdate(String id, Firewall entity){
        Firewall firewall = firewallDao.findById(Firewall.class, id);
        Vdc2Fw vdc2Fw = vdc2FwDao.findByPropertyName(Vdc2Fw.class, "firewallId", firewall.getId()).get(0);
        vdc2Fw.setThroughPut(entity.getThroughPut());
        vdc2FwDao.update(vdc2Fw);
        InvokeSetForm.copyFormProperties(entity, firewall);
        firewallDao.update(firewall);//更新防火墙
        updateStatus(id, entity.getStatus());
    }

    public void vdcSave(Firewall entity, String vdcId, String projectId) {
        entity.setTenantId(projectId);
        entity.createdUser(this.getLoginUser());
        entity.setVdcId(vdcId);
        ResultType veRs = verify(entity);
        if (!ResultType.success.equals(veRs)){
            throw new MessageException(veRs);
        }
        //默认创建一个同名的规则集
        Policie policie = new Policie(entity);
        localSave(entity, policie, vdcId, ConfigProperty.RESOURCE_OPTION_STATUS_CREATING);
    }

    public ResultType verify(Firewall entity){
        String projectId = entity.getTenantId();
        Map<String, Object> checkMap = new HashMap<>();
        checkMap.put("name", entity.getName());
        checkMap.put("tenantId", projectId);
        if (!firewallDao.checkRepeat(Firewall.class, checkMap)){//名称验重
            return ResultType.name_repeat;
        }
        ResultType resultType = quotaUsedBiz.checkQuota(ConfigProperty.FIREWALL_QUOTA_CLASSCODE, entity.getTenantId(), 1);
        if (!ResultType.success.equals(resultType)){//检查租户是否拥有配额以及是否达到最大值
            return resultType;
        }
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);//验证数据
        if(!validatorMap.isEmpty()) {
            return ResultType.parameter_error;
        }
        return ResultType.success;
    }

    /**
     * 增加防火墙和规则集的映射关系表
     * @param id
     * @param policyId
     */
    public void addFw2Policie(String id, String policyId){
        Fw2Policie fw2Policie = new Fw2Policie();
        fw2Policie.setFirewallId(id);
        fw2Policie.setPolicieId(policyId);
        fw2Policie.createdUser(this.getLoginUser());
        fw2PolicieDao.add(fw2Policie);
    }

    /**
     * 获取防火墙下挂载路由器的id集合
     * @param fwId
     * @return
     */
    public List<String> getRouteIds(String fwId){
        List<String> routeIds = new ArrayList<>();
        List<Route> routes = routeDao.findByPropertyName(Route.class, "fwId", fwId);
        if (StrUtils.checkParam(routeIds)){
            for (Route route : routes) {
                String cloudRouteId = route.getCloudosId();
                if (StrUtils.checkParam(cloudRouteId)){
                    routeIds.add(cloudRouteId);
                }
            }
        }
        return routeIds;
    }

    @Override
    public void updateStatus(String id, String status) {
        Firewall firewall = firewallDao.findById(Firewall.class, id);
        firewall.setStatus(status);
        firewallDao.update(firewall);
        Policie policie = policieDao.findById(Policie.class, firewall.getPolicyId());
        policie.setStatus(status);
        policieDao.update(policie);
        VdcItem vdcItem = vdcItemDao.findById(VdcItem.class, id);
        vdcItem.setStatus(status);
        vdcItemDao.update(vdcItem);
    }

    /**
     * 获取与该防火墙关联的规则集id
     * @param firewallId
     * @return
     */
    public String [] getPolicyIds(String firewallId){
        List<Fw2Policie> fw2Policies = fw2PolicieDao.findByPropertyName(Fw2Policie.class, "firewallId", firewallId);
        if (null != fw2Policies && fw2Policies.size() > 0){
            String [] ids = new String[fw2Policies.size()];
            for (Fw2Policie fw2Policy : fw2Policies) {
                String policieId = fw2Policy.getPolicieId();
                ids[fw2Policies.indexOf(fw2Policy)] = policieId;
            }
            return ids;
        }
        return null;
    }
    
    public JSONArray getFirewallArray(CloudosClient client) {
        String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_FIREWALL);
        JSONArray jsonArray = HttpUtils.getArray(uri, "firewalls", null, client);
        return jsonArray;
    }
    
    public JSONObject getFirewallJson(String cloudosId, CloudosClient client) {
        if (!StrUtils.checkParam(cloudosId)) {
            return null;
        }
        String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_FIREWALL_ACTION), cloudosId);
        JSONObject json = HttpUtils.getJson(uri, "firewall", client);
        return json;
    }
    
    @Override
    public JSONObject getNormJson (String cloudosId, CloudosClient client) {
        if (!StrUtils.checkParam(cloudosId)) {
            return null;
        }
        String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_FIREWALL_NORMS_ACTION), cloudosId);
        JSONObject json = HttpUtils.getJson(uri, "norms", client);
        return json;
    }
    
    public Firewall getFirewallByJson (JSONObject fwJson) {
        Firewall firewall = new Firewall();
        String fwCdId = fwJson.getString("id");
        String fwName = fwJson.getString("name");
        String fwDescription = fwJson.getString("description");
        String tenantId = fwJson.getString("tenant_id");
        Boolean stateUp = fwJson.getBoolean("admin_state_up");
        String status = fwJson.getString("status");
        firewall.setName(fwName);
        firewall.setTenantId(tenantId);
        firewall.setAdminStateUp(stateUp);
        firewall.setCloudosId(fwCdId);
        firewall.setDescription(fwDescription);
        firewall.setStatus(status);
        return firewall;
    }
    
    @Override
    public void backWrite (Firewall entity, Policie policie) {
        firewallDao.update(entity);
        policie.setStatus(entity.getStatus());
        policieDao.update(policie);
        VdcItem vdcItem = vdcItemDao.findById(VdcItem.class, entity.getId());
        vdcItem.setStatus(entity.getStatus());
        vdcItemDao.update(vdcItem);
    }
}
