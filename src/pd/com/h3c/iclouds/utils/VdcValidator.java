package com.h3c.iclouds.utils;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.NetworkBiz;
import com.h3c.iclouds.biz.QuotaUsedBiz;
import com.h3c.iclouds.biz.VlbPoolBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.Firewall;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.po.Route;
import com.h3c.iclouds.po.Subnet;
import com.h3c.iclouds.po.VdcInfo;
import com.h3c.iclouds.po.VdcItem;
import com.h3c.iclouds.po.VlbPool;
import com.h3c.iclouds.validate.ValidatorUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/12/23.
 */
public class VdcValidator {

    private static final Map<Integer, String> sortMap = new HashMap<>();
    
    private static final Map<String, String> classCodeMap = new HashMap<>();

    static {
        //操作顺序
        sortMap.put(0, "03");//删除负载均衡
        sortMap.put(1, "02");//删除网络
        sortMap.put(2, "01");//删除路由器
        sortMap.put(3, "00");//删除防火墙
        sortMap.put(4, "10");//新增防火墙
        sortMap.put(5, "11");//新增路由器
        sortMap.put(6, "21");//路由器连接防火墙
        sortMap.put(7, "12");//新增网络
        sortMap.put(8, "22");//网络连接路由器
        sortMap.put(9, "13");//新增负载均衡
        sortMap.put(10, "20");//修改防火墙
        sortMap.put(11, "23");//修改负载均衡
        classCodeMap.put(ConfigProperty.RESOURCE_TYPE_FIREWALL, ConfigProperty.FIREWALL_QUOTA_CLASSCODE);
        classCodeMap.put(ConfigProperty.RESOURCE_TYPE_ROUTE, ConfigProperty.ROUTER_QUOTA_CLASSCODE);
        classCodeMap.put(ConfigProperty.RESOURCE_TYPE_NETWORK, ConfigProperty.NETWORK_QUOTA_CLASSCODE);
        classCodeMap.put(ConfigProperty.RESOURCE_TYPE_VLBPOOL, ConfigProperty.VLBPOOL_QUOTA_CLASSCODE);
    }

    private static VlbPoolBiz vlbPoolBiz = SpringContextHolder.getBean("vlbPoolBiz");

    private static NetworkBiz networkBiz = SpringContextHolder.getBean("networkBiz");

    private static BaseDAO<Subnet> subnetDao = SpringContextHolder.getBean("baseDAO");

    private static BaseDAO<VdcItem> itemDao = SpringContextHolder.getBean("baseDAO");
    
    private static QuotaUsedBiz quotaUsedBiz = SpringContextHolder.getBean("quotaUsedBiz");

    /**
     * 检验数据
     * @param list
     * @return
     */
    public static Map<String, Object> verifyData(List<VdcInfo> list, String projectId){
        Map<String, Object> errorMap = new HashMap<>();
        if (StrUtils.checkParam(list)){
            for (VdcInfo vdcInfo : list) {
                try {
                    Map<String, String> validatorMap = ValidatorUtils.validator(vdcInfo);
                    if (!StrUtils.checkParam(validatorMap)){
                        errorMap.putAll(validatorMap);
                        return errorMap;//数据格式不正确时直接返回
                    }else {
                        List<ResultType> messageList  = verifyVdcInfo(vdcInfo, list, projectId);
                        if (StrUtils.checkParam(messageList)){//返回校验的结果
                            errorMap.put(vdcInfo.getUuid(), messageList);
                        }
                    }
                } catch (Exception e) {
                    LogUtils.exception(VdcInfo.class, e);
                }
            }
        }
        return errorMap;//返回错误信息
    }

    /**
     * 校验数据
     * @param vdcInfo
     * @param list
     * @return
     */
    public static List<ResultType> verifyVdcInfo(VdcInfo vdcInfo, List<VdcInfo> list, String projectId){
        List<ResultType> messageList = new ArrayList<>();
        String optionType = vdcInfo.getOption();
        String resourceType = vdcInfo.getType();
        String uuid = vdcInfo.getUuid();
        Map<String, Object> data = vdcInfo.getData();
        if (StrUtils.checkParam(optionType) && StrUtils.checkParam(resourceType) && StrUtils.checkParam(uuid)){
            switch (optionType){
                case ConfigProperty.RESOURCE_OPTION_DELETE:
                    String id = new VdcHandle().getIdByUuid(uuid);
                    VdcItem item = itemDao.findById(VdcItem.class, id);
                    if (!StrUtils.checkParam(item)){
                        messageList.add(ResultType.deleted);
                    }else {
                        if (!BaseRestControl.checkStatus(item.getStatus())){
                            messageList.add(ResultType.status_exception);
                        }
                    }
                    if (ConfigProperty.RESOURCE_TYPE_ROUTE.equals(resourceType)){
                        List<Network> networks = networkBiz.findByPropertyName(Network.class, "routeId", id);
                        if (StrUtils.checkParam(networks)){
                            for (Network network : networks) {
                                boolean contain = verifyVlbPool(network.getSubnetId(), list);//删除路由器时该路由器底下挂载的网络挂载的负载均衡也必须删除
                                if (!contain){
                                    messageList.add(ResultType.still_relate_vlbPool);
                                    break;
                                }
                            }
                        }
                    }
                    if (ConfigProperty.RESOURCE_TYPE_NETWORK.equals(resourceType)){
                        Network network = networkBiz.findById(Network.class, id);
                        boolean contain = verifyVlbPool(network.getSubnetId(), list);//删除网络时挂载的负载均衡必须删除
                        if (!contain){
                            messageList.add(ResultType.still_relate_vlbPool);
                        }
                    }
                    break;
                case ConfigProperty.RESOURCE_OPTION_ADD://新增时data不能为空
                    if (!StrUtils.checkParam(data)){
                        messageList.add(ResultType.data_not_null);
                    } else {
                        ResultType resultType = quotaUsedBiz.checkQuota(classCodeMap.get(resourceType), projectId, 1);
                        if (!ResultType.success.equals(resultType)){//检查租户是否拥有配额以及是否达到最大值
                            messageList.add(resultType);
                        }
                        messageList.addAll(verifyResourceData(vdcInfo, list, projectId));
                    }
                    break;
                case ConfigProperty.RESOURCE_OPTION_UPDATE:
                    if (ConfigProperty.RESOURCE_TYPE_FIREWALL.equals(resourceType) || ConfigProperty.RESOURCE_TYPE_VLBPOOL.equals(resourceType)){
                        if (!StrUtils.checkParam(data)){//修改防火墙与负载均衡时data不能为空
                            messageList.add(ResultType.data_not_null);
                        } else {
                            messageList.addAll(verifyResourceData(vdcInfo, list, projectId));
                        }
                    }else {
                        messageList.addAll(verifyResourceData(vdcInfo, list, projectId));
                    }
                    break;
                default:
                    break;
            }
        }
        return messageList;
    }

    /**
     * 校验数据参数
     * @param vdcInfo
     * @param list
     * @return
     */
    @SuppressWarnings("unchecked")
	public static List<ResultType> verifyResourceData(VdcInfo vdcInfo, List<VdcInfo> list, String projectId){
        List<ResultType> messageList = new ArrayList<>();
        String uuid = vdcInfo.getUuid();
        String optionType = vdcInfo.getOption();
        String resourceType = vdcInfo.getType();
        String previousUUid = vdcInfo.getPreviousUuid();
        Map<String, Object> data = vdcInfo.getData();
        if (StrUtils.checkParam(data)){
            data.put("uuid", uuid);//将uuid加入数据map中
        }
        switch (resourceType){
            case ConfigProperty.RESOURCE_TYPE_FIREWALL:
                Firewall firewall = new Firewall();
                InvokeSetForm.settingForm(data, firewall);//转换成对象
                Map<String, String> fwValidatorMap = ValidatorUtils.validator(firewall);//检验参数
                if (!fwValidatorMap.isEmpty()){
                    messageList.add(ResultType.parameter_error);//错误放入错误信息map中
                }
                break;
            case ConfigProperty.RESOURCE_TYPE_ROUTE:
                if (ConfigProperty.RESOURCE_OPTION_ADD.equals(optionType)){//新增时
                    Route route = new Route();
                    InvokeSetForm.settingForm(data, route);//转换成对象
                    if (!StrUtils.checkParam(route.getFwId())){//检查父级
                        verifyPrevious(previousUUid, list, messageList, ConfigProperty.RESOURCE_TYPE_FIREWALL);
                    }
                    Map<String, String> rtValidatorMap = ValidatorUtils.validator(route);//检验参数
                    if (!rtValidatorMap.isEmpty()){
                        messageList.add(ResultType.parameter_error);//错误放入错误信息map中
                    }
                }
                break;
            case ConfigProperty.RESOURCE_TYPE_NETWORK:
                if (ConfigProperty.RESOURCE_OPTION_ADD.equals(optionType)){//新增时
                    Network network = new Network();
                    InvokeSetForm.settingForm(data, network);//转换成对象
                    if (!StrUtils.checkParam(network.getRouteId())){//检查父级
                        verifyPrevious(previousUUid, list, messageList, ConfigProperty.RESOURCE_TYPE_ROUTE);
                    }
                    Map<String, String> ntValidatorMap = ValidatorUtils.validator(network);//检验参数
                    if (!ntValidatorMap.isEmpty()){
                        messageList.add(ResultType.parameter_error);//错误放入错误信息map中
                    } else {
                        //检查cidr、网关、地址池格式是否正确
                        Map<String, Object> rs = IpValidator.checkIp(network.getGatewayIp(), network.getIpPool(),
                                network.getCidr(), true);
                        if (StrUtils.checkParam(rs.get("error"))){
                            messageList.add((ResultType)rs.get("error"));
                        }
                    }
                }else {//修改时底下挂载的负载均衡也必须删除
                    String networkId = new VdcHandle().getIdByUuid(uuid);
                    Network network = networkBiz.findById(Network.class, networkId);
                    boolean contain = verifyVlbPool(network.getSubnetId(), list);//检查挂载负载均衡情况
                    if (!contain){
                        messageList.add(ResultType.still_relate_vlbPool);
                    }
                }
                break;
            case ConfigProperty.RESOURCE_TYPE_VLBPOOL:
                VlbPool vlbPool = new VlbPool();
                InvokeSetForm.settingForm(data, vlbPool);//转换成对象
                ResultType rs = vlbPoolBiz.verifyParam(vlbPool);//校验特殊参数
                if (!ResultType.success.equals(rs)){
                    messageList.add(rs);
                }
                if (ConfigProperty.RESOURCE_OPTION_UPDATE.equals(optionType)){
                    Map<String, String> vpValidatorMap = ValidatorUtils.validator(vlbPool);//校验数据
                    if (!vpValidatorMap.isEmpty()){
                        messageList.add(ResultType.parameter_error);//错误信息放入map
                    }
                }else {
                    if (StrUtils.checkParam(vlbPool.getVainSubnetId())){//检查父级虚服务网络是否连接路由器
                        verifySubnet(vlbPool.getVainSubnetId(), messageList, list);
                    }
                    if (StrUtils.checkParam(vlbPool.getFactSubnetId()) && !vlbPool.getVainSubnetId().equals(vlbPool.getFactSubnetId())){
                        verifySubnet(vlbPool.getFactSubnetId(), messageList, list);//检查父级实服务网络是否连接路由器
                    }
                    boolean vainExist = false;
                    if (!StrUtils.checkParam(vlbPool.getVainSubnetId()) && StrUtils.checkParam(previousUUid)){//检查连接的新增虚服务网络
                        vainExist = verifyPrevious(previousUUid, list, messageList, ConfigProperty.RESOURCE_TYPE_NETWORK);
                    }
                    if (vainExist){//连接的是新增网络时
                        VdcInfo vainPreviousVdc = getVdcInfoByUuid(previousUUid, list);
                        Map<String, Object> vainDataMap = vainPreviousVdc.getData();
                        String vainRouteId = StrUtils.tranString(vainDataMap.get("routeId"));
                        VdcInfo previousVdc2 = getVdcInfoByUuid(vainPreviousVdc.getPreviousUuid(), list);
                        if (null == previousVdc2 && !StrUtils.checkParam(vainRouteId)){//检查是否关联路由器
                            messageList.add(ResultType.subnet_must_link_route);
                        }
                        vlbPool.setVainSubnetId(previousUUid);
                    }
                    if (previousUUid.equals(vdcInfo.getVlbPoolFactUuid())){
                        vlbPool.setFactSubnetId(previousUUid);
                    }else {
                        boolean factExist = false;
                        if (!StrUtils.checkParam(vlbPool.getFactSubnetId()) && StrUtils.checkParam(vdcInfo.getVlbPoolFactUuid())){//检查连接的新增实服务网络
                            factExist = verifyPrevious(vdcInfo.getVlbPoolFactUuid(), list, messageList, ConfigProperty.RESOURCE_TYPE_NETWORK);
                        }
                        if (factExist){//连接的是新增网络时
                            VdcInfo factPreviousVdc = getVdcInfoByUuid(vdcInfo.getVlbPoolFactUuid(), list);
                            Map<String, Object> factDataMap = factPreviousVdc.getData();
                            String factRouteId = StrUtils.tranString(factDataMap.get("routeId"));
                            VdcInfo previousVdc3 = getVdcInfoByUuid(factPreviousVdc.getPreviousUuid(), list);
                            if (null == previousVdc3 && !StrUtils.checkParam(factRouteId)){//检查是否关联路由器
                                messageList.add(ResultType.subnet_must_link_route);
                            }
                            vlbPool.setFactSubnetId(vdcInfo.getVlbPoolFactUuid());
                        }
                    }
                    Map<String, String> vpValidatorMap = ValidatorUtils.validator(vlbPool);//校验数据
                    if (!vpValidatorMap.isEmpty()){
                        messageList.add(ResultType.parameter_error);
                    }
                    if (StrUtils.checkParam(previousUUid) && previousUUid.equals(vlbPool.getVainSubnetId())){
                        vlbPool.setVainSubnetId(null);
                    }
                    if (StrUtils.checkParam(vdcInfo.getVlbPoolFactUuid()) && vdcInfo.getVlbPoolFactUuid().equals(vlbPool.getFactSubnetId())){
                        vlbPool.setFactSubnetId(null);
                    }
                }
                break;
            default:break;
        }
        return messageList;
    }

    /**
     * 检查当前uuid的资源是否在操作集合中
     * @param uuid
     * @param list
     * @return
     */
    public static VdcInfo getVdcInfoByUuid(String uuid, List<VdcInfo> list){
        for (VdcInfo vdcInfo : list) {
            if (vdcInfo.getUuid().equals(uuid)){
                return vdcInfo;
            }
        }
        return null;
    }

    /**
     * 检测网络下是否挂载负载均衡和挂载的负载均衡是否加入删除操作
     * @param subnetId
     * @param list
     * @return
     */
    public static boolean verifyVlbPool(String subnetId, List<VdcInfo> list){
        boolean contain = true;
        List<VlbPool> vlbPools = vlbPoolBiz.findByPropertyName(VlbPool.class, "vainSubnetId", subnetId);
        List<VlbPool> vlbPools2 = vlbPoolBiz.findByPropertyName(VlbPool.class, "factSubnetId", subnetId);
        vlbPools.addAll(vlbPools2);
        if (StrUtils.checkParam(vlbPools)){
            for (VlbPool vlbPool : vlbPools) {
                String vlbPoolId = vlbPool.getId();
                String vpUuid = new VdcHandle().getUuidById(vlbPoolId);
                VdcInfo vlbPoolInfo = getVdcInfoByUuid(vpUuid, list);
                if (!StrUtils.checkParam(vlbPoolInfo) || !ConfigProperty.RESOURCE_OPTION_DELETE.equals(vlbPoolInfo.getOption())){
                    contain = false;
                    break;
                }
            }
        }
        return contain;
    }

    /**
     * 检查是否连接新增父级以及连接的新增父级是否存在
     * @param uuid
     * @param list
     * @param messageList
     * @param type
     * @return
     */
    public static boolean verifyPrevious(String uuid, List<VdcInfo> list,  List<ResultType> messageList, String type){
        boolean exist = false;
        if (StrUtils.checkParam(uuid)){
            VdcInfo previousVdc = getVdcInfoByUuid(uuid, list);
            if (null == previousVdc){
                messageList.add(ResultType.previous_not_exist);
            }else {
                if (!type.equals(previousVdc.getType())){
                    messageList.add(ResultType.previous_type_error);
                }else {
                    exist = true;
                }
            }
        }
        return exist;
    }

    /**
     * 检查网络是否连接路由器
     * @param subnetId
     * @param messageList
     * @param list
     */
    public static void verifySubnet(String subnetId, List<ResultType> messageList, List<VdcInfo> list){
        Subnet subnet = subnetDao.findById(Subnet.class, subnetId);
        boolean linkNet = false;
        if (StrUtils.checkParam(subnet)){
            Network network = networkBiz.findById(Network.class, subnet.getNetworkId());
            if(StrUtils.checkParam(network)){
                String ntUuid = new VdcHandle().getUuidById(network.getId());
                VdcInfo ntVdcInfo = getVdcInfoByUuid(ntUuid, list);
                if (StrUtils.checkParam(network.getRouteId()) && !StrUtils.checkParam(ntVdcInfo)){
                    linkNet = true;
                }else if(!StrUtils.checkParam(network.getRouteId()) && StrUtils.checkParam(ntVdcInfo) && StrUtils.checkParam(ntVdcInfo.getPreviousUuid())){
                    linkNet = true;
                }
                if (!linkNet){
                    messageList.add(ResultType.subnet_must_link_route);
                }
            }else {
                messageList.add(ResultType.subnet_not_exist);
            }
        }else {
            messageList.add(ResultType.subnet_not_exist);
        }
    }

    public static Map<Integer, String> getSortMap() {
        return sortMap;
    }
}
