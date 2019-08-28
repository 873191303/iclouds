package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.FloatingIpBiz;
import com.h3c.iclouds.biz.PortBiz;
import com.h3c.iclouds.biz.QuotaUsedBiz;
import com.h3c.iclouds.biz.SyncVdcDataBiz;
import com.h3c.iclouds.biz.VlbPoolBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.NetworkDao;
import com.h3c.iclouds.dao.VlbDao;
import com.h3c.iclouds.dao.VlbMemberDao;
import com.h3c.iclouds.dao.VlbPoolDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.FloatingIp;
import com.h3c.iclouds.po.HealthMonitor;
import com.h3c.iclouds.po.IpAllocation;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.po.Pool2HealthMonitor;
import com.h3c.iclouds.po.Port;
import com.h3c.iclouds.po.Subnet;
import com.h3c.iclouds.po.VdcItem;
import com.h3c.iclouds.po.Vlb;
import com.h3c.iclouds.po.VlbMember;
import com.h3c.iclouds.po.VlbPool;
import com.h3c.iclouds.po.VlbVip;
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
import java.util.UUID;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Service("vlbPoolBiz")
public class VlbPoolBizImpl extends BaseBizImpl<VlbPool> implements VlbPoolBiz {
    
    @Resource
    private VlbPoolDao vlbPoolDao;

    @Resource
    private VlbMemberDao vlbMemberDao;

    @Resource(name = "baseDAO")
    private BaseDAO<Pool2HealthMonitor> pool2HealthDao;

    @Resource(name = "baseDAO")
    private BaseDAO<HealthMonitor> healthMonitorDao;

    @Resource(name = "baseDAO")
    private BaseDAO<VlbVip> vipDao;

    @Resource
    private PortBiz portBiz;
    
    @Resource
    private SyncVdcDataBiz syncVdcDataBiz;

    @Resource(name = "baseDAO")
    private BaseDAO<IpAllocation> ipUsedDao;

    @Resource(name = "baseDAO")
    private BaseDAO<Subnet> subnetDao;

    @Resource
    private NetworkDao networkDao;

    @Resource
    private VlbDao vlbDao;

    @Resource(name = "baseDAO")
    private BaseDAO<VdcItem> vdcItemDao;
    
    @Resource
    private FloatingIpBiz floatingIpBiz;
    
    @Resource
    private QuotaUsedBiz quotaUsedBiz;
    
    @Override
    public PageModel<VlbPool> findForPage(PageEntity entity) {
        return vlbPoolDao.findForPage(entity);
    }

    @Override
    public Map<String, Object> update(String id, VlbPool vlbPool, CloudosClient client) {
        HealthMonitor healMonitor = new HealthMonitor(vlbPool);
        if (healMonitor.getDelay() < healMonitor.getTimeout()){
            return BaseRestControl.tranReturnValue(ResultType.delay_must_equal_or_greater_than_timeout);
        }
        VlbVip vlbVip = new VlbVip(vlbPool);
        VlbPool poolEntity = vlbPoolDao.findById(VlbPool.class, id);
        if (null == poolEntity){
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        if (!BaseRestControl.checkStatus(vlbPool.getStatus())){
            return BaseRestControl.tranReturnValue(ResultType.status_exception);
        }
        Pool2HealthMonitor p2h = pool2HealthDao.findByPropertyName(Pool2HealthMonitor.class, "poolId", id).get(0);
        HealthMonitor healEntity = healthMonitorDao.findById(HealthMonitor.class, p2h.getHmonitorId());
        VlbVip vipEntity = vipDao.findById(VlbVip.class, poolEntity.getVipId());
        String vip = vlbPool.getVip();
        String subnetId = vlbPool.getVainSubnetId();
        if (!portBiz.checkVip(vip, subnetId)){//检查ip是否在关联子网可用ip段内
            return BaseRestControl.tranReturnValue(ResultType.ipPool_notIn_range);
        }
        if (!poolEntity.getVip().equals(vip)){
            List<IpAllocation> ipUseds = ipUsedDao.findByPropertyName(IpAllocation.class, "ipAddress", vip);
            if (null == ipUseds || ipUseds.size() > 0){//判断ip是否被占用
                return BaseRestControl.tranReturnValue(ResultType.ip_was_used);
            }
        }
        InvokeSetForm.copyFormProperties(vlbPool, poolEntity);
        InvokeSetForm.copyFormProperties(healMonitor, healEntity);
        InvokeSetForm.copyFormProperties(vlbVip, vipEntity);
        JSONObject poolJson = getPoolJson(poolEntity.getCloudosId(), client);
        if (!StrUtils.checkParam(poolJson)) {
            return BaseRestControl.tranReturnValue(ResultType.vlbpool_not_exist_in_cloudos);
        }
        JSONObject vipJson = getVipJson(vipEntity.getCloudosId(), client);
        String status = vipJson.getString("status");
        poolEntity.setStatus(status);
        String upRs = this.cloudUpdate(poolEntity, healEntity, vipEntity, client);
        if (!"success".equals(upRs)){
            return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, upRs);
        }
        localUpdate(poolEntity, healEntity, vipEntity);
        return BaseRestControl.tranReturnValue(ResultType.success);
    }
    
    @Override
    public Map<String, Object> delete(String id, CloudosClient client) {
        VlbPool vlbPool = vlbPoolDao.findById(VlbPool.class, id);
        List<VlbMember> vlbMemberList = vlbMemberDao.findByPropertyName(VlbMember.class, "poolId", id);
        if (null != vlbMemberList && vlbMemberList.size() > 0){//检查是否有实例成员关联着当前实例池
            return BaseRestControl.tranReturnValue(ResultType.still_relate_vlbMember);
        }
        List<FloatingIp> floatingIps = floatingIpBiz.findByPropertyName(FloatingIp.class, "fixedPortId", vlbPool.getPortId());
        if (StrUtils.checkCollection(floatingIps)) {
            return BaseRestControl.tranReturnValue(ResultType.already_allocation_by_floatingip);
        }
        String plCdId = vlbPool.getCloudosId();
        JSONObject plJson = getPoolJson(plCdId, client);
        if (StrUtils.checkParam(plJson)){
            String deRs = cloudDelete(vlbPool.getLbId(), plCdId, vlbPool.getHmonitorCloudId(), vlbPool
                    .getVipCloudId(), client);
            if (!"success".equals(deRs)){
                return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, deRs);
            }
        }
        localDelete(vlbPool);
        return BaseRestControl.tranReturnValue(ResultType.success);
    }

    @Override
    public String cloudSave(VlbPool vlbPool, HealthMonitor healMonitor, VlbVip vlbVip, CloudosClient client, String type) {
        String plDeUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VLBPOOL_ACTION);
        String hlDeUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_HEALTHMONITOR_ACTION);
        String vpDeUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VIPPOOL_ACTION);
        String lbAcUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VLB_ACTION);
        //调用cloudos接口创建负载均衡
        //创建负载均衡池
        Subnet factSubnet = subnetDao.findById(Subnet.class, vlbPool.getFactSubnetId());
        if (!StrUtils.checkParam(factSubnet.getCloudosId())){
            return "failure";
        }
        Map<String, Object> plDataMap = ResourceHandle.tranToMap(vlbPool, ConfigProperty.RESOURCE_TYPE_VLBPOOL);
        plDataMap.put("subnet_id", factSubnet.getCloudosId());
        Map<String, Object> plParams = ResourceHandle.getParamMap(plDataMap, "pool");//转换成跟cloudos对接的参数
        String plUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VLBPOOL);//获取uri
        JSONObject plResponse = client.post(plUri, plParams);//创建负载均衡池
        if (!ResourceHandle.judgeResponse(plResponse)){//请求失败时,抛出异常
            return plResponse.getString("record");
        }
        String cloudPoolId = ResourceHandle.getId(plResponse, "pool");//获取负载均衡池真实id
        plDeUri = HttpUtils.tranUrl(plDeUri, cloudPoolId);
        vlbPool.setCloudosId(cloudPoolId);
        //创建健康监听器
        Map<String, Object> hlDataMap = ResourceHandle.tranToMap(healMonitor, ConfigProperty.RESOURCE_TYPE_HEALTHMONITOR);
        Map<String, Object> hlParams = ResourceHandle.getParamMap(hlDataMap, "health_monitor");//转换成跟cloudos对接的参数
        String hlUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_HEALTHMONITOR);//获取uri
        JSONObject hlResponse = client.post(hlUri, hlParams);//创建负载均衡池
        if (!ResourceHandle.judgeResponse(hlResponse)){//请求失败时,抛出异常并删除负载均衡池
            client.delete(plDeUri);
            return hlResponse.getString("record");
        }
        String cloudHealId = ResourceHandle.getId(hlResponse, "health_monitor");//获取负载均衡池真实id
        hlDeUri = HttpUtils.tranUrl(hlDeUri, cloudHealId);
        healMonitor.setCloudosId(cloudHealId);
        //监听器关联资源池
        Map<String, Object> linkMap = new HashMap<>();
        String linkUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_HEALTH_LINK_POOL);
        Map<String, Object> idMap = new HashMap<>();
        idMap.put("id", cloudHealId);
        linkMap.put("health_monitor", idMap);
        linkUri = HttpUtils.tranUrl(linkUri, cloudPoolId);
        JSONObject linkResponse = client.post(linkUri, linkMap);
        if (!ResourceHandle.judgeResponse(linkResponse)){//请求失败时,抛出异常并删除负载均衡池和监听器
            client.delete(plDeUri);
            client.delete(hlDeUri);
            return linkResponse.getString("record");
        }
        //创建vipPool
        Subnet subnet = subnetDao.findById(Subnet.class, vlbPool.getVainSubnetId());
        if (!StrUtils.checkParam(subnet.getCloudosId())){
            client.delete(plDeUri);
            client.delete(hlDeUri);
            return "failure";
        }
        Map<String, Object> vpDataMap = ResourceHandle.tranToMap(vlbVip, ConfigProperty.RESOURCE_TYPE_VLBVIP);
        vpDataMap.put("subnet_id", subnet.getCloudosId());
        vpDataMap.put("pool_id", cloudPoolId);
        Map<String, Object> cookieMap = new HashMap<>();
        if ("APP_COOKIE".equals(vlbPool.getCookieType()) && StrUtils.checkParam(vlbPool.getCookieName())){
            cookieMap.put("cookie_name", vlbPool.getCookieName());
        }
        cookieMap.put("type", vlbPool.getCookieType());
        vpDataMap.put("session_persistence", cookieMap);
        Map<String, Object> vpParams = ResourceHandle.getParamMap(vpDataMap, "vip");//转换成跟cloudos对接的参数
        String vpUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VIPPOOL);//获取uri
        JSONObject vpResponse = client.post(vpUri, vpParams);//创建vipPool
        if (!ResourceHandle.judgeResponse(vpResponse)){//请求失败时,抛出异常并删除负载均衡池和监听器
            client.delete(plDeUri);
            client.delete(hlDeUri);
            return vpResponse.getString("record");
        }
        String cloudVipId = ResourceHandle.getId(vpResponse, "vip");//获取负载均衡池真实id
        String status = ResourceHandle.getParam(vpResponse, "vip", "status");
        vlbPool.setStatus(status);
        String portCdId = ResourceHandle.getParam(vpResponse, "vip", "port_id");
        vlbPool.setPortCdId(portCdId);
        vpDeUri = HttpUtils.tranUrl(vpDeUri, cloudVipId);
        vlbVip.setCloudosId(cloudVipId);
        String portCloudId = ResourceHandle.getParam(vpResponse, "vip", "port_id");
        vlbPool.setPortCdId(portCloudId);
        if (!StrUtils.checkParam(vlbPool.getVip())){
            String vip = ResourceHandle.getParam(vpResponse, "vip", "address");
            vlbVip.setVipAddress(vip);
        }
        //负载均衡组插入负载均衡
        String lbId = vlbPool.getLbId();
        Vlb vlb = vlbDao.findById(Vlb.class, lbId);
        String lbCloudId = vlb.getCloudosId();//负载均衡组真实id
        JSONObject record = getRecord(lbCloudId, cloudPoolId, cloudVipId, cloudHealId, client, "save");
        lbAcUri = HttpUtils.tranUrl(lbAcUri, lbCloudId);
        JSONObject lbUpresponse = client.put(lbAcUri, record);
        if (!ResourceHandle.judgeResponse(lbUpresponse)){
            client.delete(vpDeUri);
            client.delete(plDeUri);
            client.delete(hlDeUri);
            return lbUpresponse.getString("record");
        }
        String extra = record.getString("extra");
        vlb.setExtra(extra);
        vlbDao.update(vlb);
        if (StrUtils.checkParam(type) && "vdc".equals(type)){
            vlbPoolDao.update(vlbPool);
            healthMonitorDao.update(healMonitor);
            vipDao.update(vlbVip);
            Port port = portBiz.findById(Port.class, vlbVip.getPortId());
            port.setCloudosId(vlbPool.getPortCdId());
            portBiz.update(port);
            VdcItem vdcItem = vdcItemDao.findById(VdcItem.class, vlbPool.getId());
            vdcItem.setStatus(status);
            vdcItemDao.update(vdcItem);
        }
        return "success";
    }

    public void localSave(VlbPool vlbPool, HealthMonitor healMonitor, VlbVip vlbVip, String vdcId, String status){
        String projectId = this.getProjectId();
        Pool2HealthMonitor pool2Heal = new Pool2HealthMonitor();
        String healthId = null;
        String vipId = null;
        String vlbPoolId = null;
        Subnet subnet = subnetDao.findById(Subnet.class, vlbPool.getVainSubnetId());
        
        String portId;
        /**
         * 创建端口和ip使用数据并获取端口id
         */
        if (ConfigProperty.RESOURCE_OPTION_STATUS_CREATING.equals(status)) {
            Port port = new Port();
            port.setTenantId(vlbPool.getTenantId());
            port.setName(UUID.randomUUID().toString());
            port.setStatus(status);
            port.setDeviceOwner("neutron:LOADBALANCER");
            port.createdUser(vlbPool.getCreatedBy());
            portId = portBiz.add(port);
            IpAllocation ipAllocation = new IpAllocation();
            ipAllocation.setIpAddress(vlbVip.getVipAddress());
            ipAllocation.setSubnetId(subnet.getId());
            ipAllocation.setPortId(portId);
            ipUsedDao.add(ipAllocation);
        } else {
            CloudosClient client = this.getSessionBean().getCloudClient();
            JSONObject portJson = portBiz.getPortJson(vlbPool.getPortCdId(), client);
            portId = syncVdcDataBiz.syncPort(portJson, null, null);
        }
        
        vlbVip.setPortId(portId);
        vlbVip.setVainSubnetId(subnet.getId());
        vlbVip.setName(vlbPool.getName());
        vlbVip.setStatus(status);
        
        /**
         * 创建监听器并获取监听器id
         */
        healthId = healthMonitorDao.add(healMonitor);
        pool2Heal.setHmonitorId(healthId);
        
        /**
         * 创建vip池并获取vip池id
         */
        vipId = vipDao.add(vlbVip);
        vlbPool.setVipId(vipId);
        
        /**
         * 创建实例池并获取实例池id
         */
        vlbPool.setStatus(status);
        vlbPoolId = vlbPoolDao.add(vlbPool);
        
        Port port = portBiz.findById(Port.class, portId);
        port.setDeviceId(vlbPoolId);
        portBiz.update(port);
        
        pool2Heal.setPoolId(vlbPoolId);
        pool2HealthDao.add(pool2Heal);//创建实例池与监听器映射关系
    
        quotaUsedBiz.change(ConfigProperty.VLBPOOL_QUOTA_CLASSCODE, vlbPool.getTenantId(), true, 1);
        new VdcHandle().saveViewAndItem(vlbPoolId, vlbPool.getUuid(), vlbPool.getName(), vdcId, subnet.getNetworkId(), ConfigProperty.RESOURCE_TYPE_VLBPOOL, null, status);//同步更新vdc视图和视图对象数据
    }

    public String cloudDelete(String lbId, String poolCdId, String healCdId, String vipCdId, CloudosClient client){
        String plDeUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VLBPOOL_ACTION);
        String hlDeUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_HEALTHMONITOR_ACTION);
        String vpDeUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VIPPOOL_ACTION);
        String lbAcUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VLB_ACTION);
        //调用cloudos接口删除负载均衡池
        vpDeUri = HttpUtils.tranUrl(vpDeUri, vipCdId);
        JSONObject vpDeresponse = client.delete(vpDeUri);
        if (!ResourceHandle.judgeResponse(vpDeresponse)){
            return HttpUtils.getError(vpDeresponse);
        }
        plDeUri = HttpUtils.tranUrl(plDeUri, poolCdId);
        client.delete(plDeUri);
        hlDeUri = HttpUtils.tranUrl(hlDeUri, healCdId);
        client.delete(hlDeUri);
        Vlb vlb = vlbDao.findById(Vlb.class, lbId);
        JSONObject record = getRecord(vlb.getCloudosId(), poolCdId, vipCdId, healCdId, client, "delete");
        lbAcUri = HttpUtils.tranUrl(lbAcUri, vlb.getCloudosId());
        client.put(lbAcUri, record);
        vlb.setExtra(record.getString("extra"));
        vlbDao.update(vlb);
        return "success";
    }

    public void localDelete(VlbPool vlbPool){
        deleteRelateData(vlbPool);//删除相关数据
        new VdcHandle().deleteViewAndItem(vlbPool.getId());//同步更新vdc视图和视图对象数据
        quotaUsedBiz.change(ConfigProperty.VLBPOOL_QUOTA_CLASSCODE, vlbPool.getTenantId(), false, 1);
    }

    public String cloudUpdate(VlbPool poolEntity, HealthMonitor healEntity, VlbVip vipEntity, CloudosClient client){
        //调用cloudos接口修改负载均衡池
        Map<String, Object> plDataMap = ResourceHandle.tranToMap(poolEntity, ConfigProperty.RESOURCE_TYPE_VLBPOOL);
        plDataMap.remove("protocol");
        plDataMap.remove("tenant_id");
        Map<String, Object> plParams = ResourceHandle.getParamMap(plDataMap, "pool");//转换成跟cloudos对接的参数
        String plUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VLBPOOL_ACTION);//获取uri
        plUri = HttpUtils.tranUrl(plUri, poolEntity.getCloudosId());
        JSONObject plResponse = client.put(plUri, plParams);//创建负载均衡池
        if (!ResourceHandle.judgeResponse(plResponse)){//请求失败时,抛出异常
            return plResponse.getString("record");
        }
        //调用cloudos接口修改负载均衡监听器
        Map<String, Object> hlDataMap = ResourceHandle.tranToMap(healEntity, ConfigProperty.RESOURCE_TYPE_HEALTHMONITOR);
        hlDataMap.remove("type");
        hlDataMap.remove("tenant_id");
        Map<String, Object> hlParams = ResourceHandle.getParamMap(hlDataMap, "health_monitor");//转换成跟cloudos对接的参数
        String hlUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_HEALTHMONITOR_ACTION);//获取uri
        hlUri = HttpUtils.tranUrl(hlUri, healEntity.getCloudosId());
        JSONObject hlResponse = client.put(hlUri, hlParams);//修改负载均衡池
        if (!ResourceHandle.judgeResponse(hlResponse)){//请求失败时,抛出异常
            return hlResponse.getString("record");
        }
        //调用cloudos接口修改负载均衡vip池
        Map<String, Object> vpDataMap = ResourceHandle.tranToMap(vipEntity, ConfigProperty.RESOURCE_TYPE_VLBVIP);
        vpDataMap.remove("protocol");
        vpDataMap.remove("address");
        vpDataMap.remove("protocol_port");
        vpDataMap.remove("tenant_id");
        Map<String, Object> cookieMap = new HashMap<>();
        if ("APP_COOKIE".equals(poolEntity.getCookieType()) && StrUtils.checkParam(poolEntity.getCookieName())){
            cookieMap.put("cookie_name", poolEntity.getCookieName());
        }
        cookieMap.put("type", poolEntity.getCookieType());
        vpDataMap.put("session_persistence", cookieMap);
        Map<String, Object> vpParams = ResourceHandle.getParamMap(vpDataMap, "vip");//转换成跟cloudos对接的参数
        String vpUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VIPPOOL_ACTION);//获取uri
        vpUri = HttpUtils.tranUrl(vpUri, vipEntity.getCloudosId());
        JSONObject vpResponse = client.put(vpUri, vpParams);
        if (!ResourceHandle.judgeResponse(vpResponse)){//请求失败时,抛出异常
            return vpResponse.getString("record");
        }
        return "success";
    }
    
    @Override
    public void localUpdate(VlbPool poolEntity, HealthMonitor healEntity, VlbVip vipEntity){
        vlbPoolDao.update(poolEntity);
        healthMonitorDao.update(healEntity);
        vipDao.update(vipEntity);
        updateStatus(poolEntity.getId(), poolEntity.getStatus());
    }

    @Override
    public void vdcSave(VlbPool vlbPool, String vdcId, String projectId) {
        vlbPool.setTenantId(projectId);
        vlbPool.createdUser(this.getLoginUser());
        HealthMonitor healMonitor = new HealthMonitor(vlbPool);
        VlbVip vlbVip = new VlbVip(vlbPool);
        ResultType veRs = verify(vlbPool);
        if (!ResultType.success.equals(veRs)){
            throw new MessageException(veRs);
        }
        localSave(vlbPool, healMonitor, vlbVip, vdcId, ConfigProperty.RESOURCE_OPTION_STATUS_CREATING);
    }
    
    @Override
    public ResultType verify(VlbPool vlbPool){
        String projectId = vlbPool.getTenantId();
        ResultType rs = verifyParam(vlbPool);
        if (!ResultType.success.equals(rs)){
            return rs;
        }
        Map<String, Object> checkRepeatMap = new HashMap<>();
        checkRepeatMap.put("name", vlbPool.getName());
        checkRepeatMap.put("tenantId", projectId);
        if (!vlbPoolDao.checkRepeat(VlbPool.class, checkRepeatMap)){
            return ResultType.name_repeat;
        }
        Map<String, String> validatorMap = ValidatorUtils.validator(vlbPool);
        if (!validatorMap.isEmpty()){
            return ResultType.parameter_error;
        }
        if (vlbPool.getDelay() < vlbPool.getTimeout()){
            return ResultType.delay_must_equal_or_greater_than_timeout;
        }
        String lbId = vlbPool.getLbId();
        Vlb vlb = vlbDao.findById(Vlb.class, lbId);
        if (!StrUtils.checkParam(vlb)){
            return ResultType.vlb_not_exist;
        }
        if (!projectId.equals(vlb.getProjectId())) {
            return ResultType.tenant_inconformity;
        }
        String vip = vlbPool.getVip();
        String vainSubnetId = vlbPool.getVainSubnetId();
        Subnet subnet = subnetDao.findById(Subnet.class, vainSubnetId);
        if (null == subnet){//判断关联的网络是否存在
            return ResultType.network_not_exist;
        }
        if (!projectId.equals(subnet.getTenantId())) {
            return ResultType.tenant_inconformity;
        }
        Network network = networkDao.findById(Network.class, subnet.getNetworkId());
        if (!BaseRestControl.checkStatus(network.getStatus())){
            return ResultType.parent_status_exception;
        }
        if (!vainSubnetId.equals(vlbPool.getFactSubnetId())){
            Subnet factSubnet = subnetDao.findById(Subnet.class, vlbPool.getFactSubnetId());
            if (null == factSubnet){//判断关联的网络是否存在
                return ResultType.network_not_exist;
            }
            if (!projectId.equals(factSubnet.getTenantId())) {
                return ResultType.tenant_inconformity;
            }
            Network factNetwork = networkDao.findById(Network.class, factSubnet.getNetworkId());
            if (!BaseRestControl.checkStatus(factNetwork.getStatus())){
                return ResultType.parent_status_exception;
            }
        }
        ResultType resultType1 = quotaUsedBiz.checkQuota(ConfigProperty.VLBPOOL_QUOTA_CLASSCODE, projectId, 1);
        if (!ResultType.success.equals(resultType1)){//检查租户是否拥有配额以及是否达到最大值
            return resultType1;
        }
        if (StrUtils.checkParam(vip)){
            Map<String, String> querymap = new HashMap<>();
            querymap.put("ipAddress", vip);
            querymap.put("subnetId", vainSubnetId);
            List<IpAllocation> ipUseds = ipUsedDao.findByMap(IpAllocation.class, querymap);
            if (StrUtils.checkParam(ipUseds) || subnet.getGatewayIp().equals(vip)){//判断ip是否被占用
                return ResultType.ip_was_used;
            }
        }
        if (!portBiz.checkVip(vip, vainSubnetId)){//检查ip是否在关联子网可用ip段内
            return ResultType.ip_notIn_range;
        }
        return ResultType.success;
    }

    /**
     * 创建端口和ip使用数据并获取端口id
     */
    public Port savePortAndIpUsed(String vip, String subnetId, String routeId, String cloudPortId,
                                    String status){
        Port port = new Port();
        String uuidName = UUID.randomUUID().toString();
        port.setName(uuidName);
        port.setCloudosId(cloudPortId);
        port.setStatus(status);
        port.createdUser(this.getLoginUser());
        port.setTenantId(this.getProjectId());
        port.setRouteId(routeId);
        String portId = portBiz.add(port);
        IpAllocation ipUsed = new IpAllocation();
        ipUsed.setPortId(portId);
        ipUsed.setSubnetId(subnetId);
        ipUsed.setIpAddress(vip);
        ipUsedDao.add(ipUsed);
        return port;
    }
    
    /**
     * 删除vlbPool关联的数据（注意删除顺序，必须先把外键关联的数据删掉再删被关联的数据）
     * @param vlbPool
     */
    public void deleteRelateData(VlbPool vlbPool){
        String id = vlbPool.getId();
        String vipId = vlbPool.getVipId();
        String hmonitorId = vlbPool.getHmonitorId();
        String portId = vlbPool.getPortId();
        String subnetId = vlbPool.getVainSubnetId();
        Map<String, Object> deleteMap = new HashMap<>();
        if (StrUtils.checkParam(portId)) {
            deleteMap.put("portId", portId);
            deleteMap.put("subnetId", subnetId);
            ipUsedDao.delete(deleteMap, IpAllocation.class);//删除ip使用记录数据
            portBiz.deleteById(Port.class, portId);//删除端口数据
        }
        vlbPoolDao.delete(vlbPool);//删除vlbPool数据
        vipDao.deleteById(VlbVip.class, vipId);//删除vip
        deleteMap.clear();
        deleteMap.put("poolId", id);
        pool2HealthDao.delete(deleteMap, Pool2HealthMonitor.class);//删除vlbPool和监听器关联数据
        healthMonitorDao.deleteById(HealthMonitor.class, hmonitorId);//删除监听器数据
    }

    /**
     * 检查负载均衡高级选项里面的参数
     * @param vlbPool
     * @return
     */
    public ResultType verifyParam(VlbPool vlbPool){
        if ("APP_COOKIE".equals(vlbPool.getCookieType()) && !StrUtils.checkParam(vlbPool.getCookieName())){
            return ResultType.cookie_name_not_null;//app_cookie必须有会话名称
        }
        if (!"APP_COOKIE".equals(vlbPool.getCookieType()) && StrUtils.checkParam(vlbPool.getCookieName())){
            vlbPool.setCookieName(null);//当多传时，置空
        }
        if ("HTTP".equals(vlbPool.getType()) || "HTTPS".equals(vlbPool.getType())){
            if (!StrUtils.checkParam(vlbPool.getUrlPath()) || !StrUtils.checkParam(vlbPool.getHttpMethod())){
                return ResultType.url_and_method_not_null;//http和https健康检查类型必须有url和http方法
            }
            if (!vlbPool.getUrlPath().startsWith("/")){//url必须以/开头
                return ResultType.url_param_error;
            }
        }else {
            if (StrUtils.checkParam(vlbPool.getUrlPath()) || StrUtils.checkParam(vlbPool.getHttpMethod())){
                vlbPool.setUrlPath(null);//当多传时，置空
                vlbPool.setHttpMethod(null);
            }
        }
        return ResultType.success;
    }

    @SuppressWarnings("unchecked")
	public JSONObject getRecord(String lbCloudId, String cloudPoolId, String cloudVipId, String cloudHealId, CloudosClient client, String type){
        String lbAcUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VLB_ACTION);
        lbAcUri = HttpUtils.tranUrl(lbAcUri, lbCloudId);
        JSONObject lbJson = client.get(lbAcUri);//获取负载均衡组信息
        Map<String, String> healMap = new HashMap<>();
        healMap.put("poolId", cloudPoolId);
        healMap.put("vipId", cloudVipId);
        healMap.put("monitorId", cloudHealId);
        Gson gson = new Gson();
        JSONObject record = lbJson.getJSONObject("record");
        String extra = record.getString("extra");
        Map<String, Object> extraMap = new HashMap<>();
        List<Map<String, String>> healList = new ArrayList<>();
        if (StrUtils.checkParam(extra)){
            extraMap = gson.fromJson(extra, new TypeToken<HashMap<String, Object>>(){}.getType());
            healList = (List<Map<String, String>>)extraMap.get("pool_vip_monitors");
        }
        if ("save".equals(type)){
            healList.add(healMap);
        }
        if ("delete".equals(type)){
            if (StrUtils.checkParam(healList)){
                for (Map<String, String> map : healList) {
                    if (cloudPoolId.equals(map.get("poolId"))){
                        healList.remove(map);
                        break;
                    }
                }
            }
        }
        extraMap.put("pool_vip_monitors", healList);
        record.put("extra", gson.toJson(extraMap));
        return record;
    }

    @Override
    public void updateStatus(String id, String status) {
        VlbPool vlbPool = vlbPoolDao.findById(VlbPool.class, id);
        vlbPool.setStatus(status);
        vlbPoolDao.update(vlbPool);
        VlbVip vlbVip = vipDao.findById(VlbVip.class, vlbPool.getVipId());
        vlbVip.setStatus(status);
        vipDao.update(vlbVip);
        Port port = portBiz.findById(Port.class, vlbVip.getPortId());
        port.setStatus(status);
        portBiz.update(port);
        VdcItem vdcItem = vdcItemDao.findById(VdcItem.class, id);
        vdcItem.setStatus(status);
        vdcItemDao.update(vdcItem);
    }
    
    @Override
    public JSONArray getVipArray (CloudosClient client) {
        String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VIPPOOL));
        JSONArray array = HttpUtils.getArray(uri, "vips", null, client);
        return array;
    }
    
    @Override
    public JSONObject getPoolJson (String cloudosId, CloudosClient client) {
	    if (!StrUtils.checkParam(cloudosId)) {
	        return null;
        }
        String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VLBPOOL_ACTION), cloudosId);
        JSONObject json = HttpUtils.getJson(uri, "pool", client);
        return json;
    }
    
    @Override
    public JSONObject getVipJson (String cloudosId, CloudosClient client) {
        if (!StrUtils.checkParam(cloudosId)) {
            return null;
        }
        String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VIPPOOL_ACTION), cloudosId);
        JSONObject json = HttpUtils.getJson(uri, "vip", client);
        return json;
    }
    
    @Override
    public JSONObject getHealthJson (String cloudosId, CloudosClient client) {
        if (!StrUtils.checkParam(cloudosId)) {
            return null;
        }
        String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_HEALTHMONITOR_ACTION), cloudosId);
        JSONObject json = HttpUtils.getJson(uri, "health_monitor", client);
        return json;
    }
    
    @Override
    public VlbPool getPoolByJson (JSONObject plJson) {
        VlbPool vlbPool = new VlbPool();
        String plCdId = plJson.getString("id");
        String plName = plJson.getString("name");
        Boolean stateUp = plJson.getBoolean("admin_state_up");
        String tenantId = plJson.getString("tenant_id");
        String status = plJson.getString("status");
        String protocol = plJson.getString("protocol");
        String description = plJson.getString("description");
        String lbAlgorithm = plJson.getString("lb_algorithm");
        if(lbAlgorithm == null) {
            lbAlgorithm = plJson.getString("lb_method");
        }
        vlbPool.setProtocol(protocol);
        vlbPool.setCloudosId(plCdId);
        vlbPool.setTenantId(tenantId);
        vlbPool.setDescription(description);
        vlbPool.setName(plName);
        vlbPool.setAdminStateUp(stateUp);
        vlbPool.setLbMethod(lbAlgorithm);
        vlbPool.setStatus(null == status ? "3": status);
        return vlbPool;
    }
    
    @Override
    public VlbVip getVipByJson (JSONObject vpJson) {
        VlbVip vlbVip = new VlbVip();
        String vpCdId = vpJson.getString("id");
        String vpName = vpJson.getString("name");
        String status = vpJson.getString("status");
        String tenantId = vpJson.getString("tenant_id");
        String protocol = vpJson.getString("protocol");
        String description = vpJson.getString("description");
        Integer protocolPort = vpJson.getInteger("protocol_port");
        Integer connectLimit = vpJson.getInteger("connection_limit");
        String address = vpJson.getString("address");
        Boolean stateUp = vpJson.getBoolean("admin_state_up");
        String cookieName = null;
        String cookieType = null;
        JSONObject sessionObject = vpJson.getJSONObject("session_persistence");
        if (StrUtils.checkParam(sessionObject)){
            cookieName = sessionObject.getString("cookie_name");
            cookieType = sessionObject.getString("type");
        }
        vlbVip.setTenantId(tenantId);
        if(!StrUtils.checkParam(vpName)) {
            vpName = UUID.randomUUID().toString();
        }
        vlbVip.setName(vpName);
        vlbVip.setAdminStateUp(stateUp);
        vlbVip.setCloudosId(vpCdId);
        vlbVip.setConnectionLimit(connectLimit);
        vlbVip.setCookieName(cookieName);
        vlbVip.setCookieType(cookieType);
        vlbVip.setDescription(description);
        vlbVip.setProtocol(protocol);
        vlbVip.setProtocolPort(protocolPort);
        vlbVip.setStatus(status);
        vlbVip.setVipAddress(address);
        return vlbVip;
    }
    
    @Override
    public HealthMonitor gethealthByJson (JSONObject hlJson) {
        HealthMonitor monitor = new HealthMonitor();
        String hlCdId = hlJson.getString("id");
        Integer delay = hlJson.getInteger("delay");
        Integer retrys = hlJson.getInteger("max_retries");
        String tenantId = hlJson.getString("tenant_id");
        Integer timeout = hlJson.getInteger("timeout");
        String type = hlJson.getString("type");
        String httpMethod = hlJson.getString("http_method");
        String urlPath = hlJson.getString("url_path");
        String expectedCodes = hlJson.getString("expected_codes");
        Boolean stateUp = hlJson.getBoolean("admin_state_up");
        monitor.setAdminStateUp(stateUp);
        monitor.setTenantId(tenantId);
        monitor.setCloudosId(hlCdId);
        monitor.setDelay(delay);
        monitor.setMaxRetries(retrys);
        monitor.setType(type);
        monitor.setTimeout(timeout);
        monitor.setUrlPath(urlPath);
        monitor.setHttpMethod(httpMethod);
        monitor.setExpectedCodes(expectedCodes);
        return monitor;
    }
    
    @Override
    public void backWrite (VlbPool pool, HealthMonitor heal, VlbVip vip, CloudosClient client) {
        vlbPoolDao.update(pool);
        healthMonitorDao.update(heal);
        vip.setStatus(pool.getStatus());
        vipDao.update(vip);
        Port port = portBiz.findById(Port.class, vip.getPortId());
        port.setCloudosId(pool.getPortCdId());
        port.setStatus(pool.getStatus());
        JSONObject portJson = portBiz.getPortJson(pool.getPortCdId(), client);
        String mac = portJson.getString("mac_address");
        String ptName = portJson.getString("name");
        Boolean stateUp = portJson.getBoolean("admin_state_up");
        if (StrUtils.checkParam(ptName)) {
            port.setName(ptName);
        }
        port.setMacAddress(mac);
        port.setAdminStateUp(stateUp);
        portBiz.update(port);
        VdcItem vdcItem = vdcItemDao.findById(VdcItem.class, pool.getId());
        vdcItem.setStatus(pool.getStatus());
        vdcItemDao.update(vdcItem);
    }
}
