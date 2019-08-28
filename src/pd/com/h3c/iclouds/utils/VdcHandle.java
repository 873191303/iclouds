package com.h3c.iclouds.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.FirewallBiz;
import com.h3c.iclouds.biz.NetworkBiz;
import com.h3c.iclouds.biz.RouteBiz;
import com.h3c.iclouds.biz.VlbPoolBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.po.Firewall;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.po.Route;
import com.h3c.iclouds.po.Subnet;
import com.h3c.iclouds.po.VdcInfo;
import com.h3c.iclouds.po.VdcItem;
import com.h3c.iclouds.po.VdcView;
import com.h3c.iclouds.po.VlbPool;

/**
 * Created by yKF7317 on 2016/11/30.
 */
public class VdcHandle {

    private FirewallBiz firewallBiz = SpringContextHolder.getBean("firewallBiz");

    private RouteBiz routeBiz = SpringContextHolder.getBean("routeBiz");

    private NetworkBiz networkBiz = SpringContextHolder.getBean("networkBiz");

    private VlbPoolBiz vlbPoolBiz = SpringContextHolder.getBean("vlbPoolBiz");

    private BaseDAO<VdcView> vdcViewDao = SpringContextHolder.getBean("baseDAO");

    private BaseDAO<VdcItem> vdcItemDao = SpringContextHolder.getBean("baseDAO");

    private BaseDAO<Subnet> subnetDao = SpringContextHolder.getBean("baseDAO");

    /**
     * 保存vdc视图对象表和上下级关系表
     * @param objId
     * @param uuid
     * @param objName
     * @param vdcId
     * @param previous
     * @param itemType
     * @param sequence
     * @param status
     */
    public void saveViewAndItem(String objId, String uuid, String objName, String vdcId, String previous, String itemType, Integer sequence, String status){
        VdcView vdcView = new VdcView();
        VdcItem vdcItem = new VdcItem();
        vdcView.setObjId(objId);
        vdcView.setVdcId(vdcId);
        vdcView.setPrevious(previous);
        vdcView.setSequence(sequence);
        vdcItem.setName(objName);
        vdcItem.setId(objId);//对象表的id即为对象的id
        vdcItem.setItemType(itemType);
        vdcItem.setUuid(uuid);
        vdcItem.setStatus(status);
        vdcItemDao.add(vdcItem);//保存vdc视图对应的对象的基础数据
        vdcViewDao.add(vdcView);//保存vdc视图数据
    }

    /**
     * 修改视图对象数据名称
     * @param objId
     * @param objName
     */
    public void updateItemName(String objId, String objName){
        VdcItem vdcItem = vdcItemDao.findById(VdcItem.class, objId);
        vdcItem.setName(objName);
        vdcItem.setStatus(ConfigProperty.RESOURCE_OPTION_STATUS_SUCCESS);
        vdcItemDao.update(vdcItem);
    }

    /**
     * 修改视图上下级关系数据
     * @param objId
     * @param previous
     */
    public void updateView(String objId, String previous){
        VdcView vdcView = vdcViewDao.findByPropertyName(VdcView.class, "objId", objId).get(0);
        vdcView.setPrevious(previous);
        vdcViewDao.update(vdcView);
    }

    /**
     * 删除视图对象数据和上下级数据
     * @param objId
     */
    public void deleteViewAndItem(String objId){
        Map<String, Object> deleteMap = new HashMap<>();//删除map
        deleteMap.put("objId", objId);
        VdcView vdcView = vdcViewDao.singleByClass(VdcView.class, deleteMap);
        VdcItem vdcItem = vdcItemDao.findById(VdcItem.class, objId);
        if (StrUtils.checkParam(vdcView)) {
            vdcViewDao.delete(vdcView);
        }
        if (StrUtils.checkParam(vdcItem)) {
            vdcItemDao.delete(vdcItem);
        }
    }

    /**
     * vdc视图的本地操作
     * @param vdcInfo
     */
    public void handleVdcInfo(VdcInfo vdcInfo, String vdcId, String projectId){
        String uuid = vdcInfo.getUuid();
        String option = vdcInfo.getOption();
        String type = vdcInfo.getType();
        String previousUuid = vdcInfo.getPreviousUuid();
        Map<String, Object> data = vdcInfo.getData();
        String id = getIdByUuid(uuid);//删除和修改时直接查出对象id
        switch (option){
            case ConfigProperty.RESOURCE_OPTION_ADD://新增
                data.put("uuid", uuid);//将uuid放入数据中
                switch (type){//根据类型添加
                    case ConfigProperty.RESOURCE_TYPE_FIREWALL:
                        Firewall firewall = new Firewall();
                        InvokeSetForm.settingForm(data, firewall);//将map转换成对应的对象数据类型
                        firewallBiz.vdcSave(firewall, vdcId, projectId);//本地保存
                        vdcInfo.setData(InvokeSetForm.tranClassToMap(firewall));//将vdcInfo对象的data替换为本地保存后的对象用来放入任务表中跟cloudos对接
                        break;
                    case ConfigProperty.RESOURCE_TYPE_ROUTE:
                        Route route = new Route();
                        InvokeSetForm.settingForm(data, route);//将map转换成对应的对象数据类型
                        if (!StrUtils.checkParam(route.getFwId()) && StrUtils.checkParam(previousUuid)){//判断是否关联了新创建的防火墙
                            String fwId = getIdByUuid(previousUuid);//获取关联的防火墙id
                            route.setFwId(fwId);
                        }
                        routeBiz.vdcSave(route, vdcId, projectId);//本地保存
                        vdcInfo.setData(InvokeSetForm.tranClassToMap(route));//将vdcInfo对象的data替换为本地保存后的对象用来放入任务表中跟cloudos对接
                        break;
                    case ConfigProperty.RESOURCE_TYPE_NETWORK:
                        Network network = new Network();
                        InvokeSetForm.settingForm(data, network);//将map转换成对应的对象数据类型
                        if (!StrUtils.checkParam(network.getRouteId()) && StrUtils.checkParam(previousUuid)){//判断是否关联了新创建的路由器
                            String routeId = getIdByUuid(previousUuid);//获取关联的路由器id
                            network.setRouteId(routeId);
                        }
                        networkBiz.vdcSave(network, vdcId, projectId);//本地保存
                        vdcInfo.setData(InvokeSetForm.tranClassToMap(network));
                        break;
                    case ConfigProperty.RESOURCE_TYPE_VLBPOOL:
                        VlbPool vlbPool = new VlbPool();
                        InvokeSetForm.settingForm(data, vlbPool);//将map转换成对应的对象数据类型
                        if (!StrUtils.checkParam(vlbPool.getVainSubnetId()) && StrUtils.checkParam(previousUuid)){//判断虚服务是否关联了新创建的网络
                            String vainNetworkId = getIdByUuid(previousUuid);//获取关联的虚服务网络id
                            List<Subnet> vainSubnets = subnetDao.findByPropertyName(Subnet.class, "networkId", vainNetworkId);
                            if (StrUtils.checkParam(vainSubnets)){
                                vlbPool.setVainSubnetId(vainSubnets.get(0).getId());
                            }
                        }
                        if (!StrUtils.checkParam(vlbPool.getFactSubnetId()) && StrUtils.checkParam(vdcInfo.getVlbPoolFactUuid())){//判断实服务是否关联了新创建的网络
                            String factNetworkId = getIdByUuid(vdcInfo.getVlbPoolFactUuid());//获取关联的实服务网络id
                            List<Subnet> factSubnets = subnetDao.findByPropertyName(Subnet.class, "networkId", factNetworkId);
                            if (StrUtils.checkParam(factSubnets)){
                                vlbPool.setFactSubnetId(factSubnets.get(0).getId());
                            }
                        }
                        vlbPoolBiz.vdcSave(vlbPool, vdcId, projectId);//本地保存
                        vdcInfo.setData(InvokeSetForm.tranClassToMap(vlbPool));//将vdcInfo对象的data替换为本地保存后的对象用来放入任务表中跟cloudos对接
                        break;
                    default:break;
                }
                break;
            case ConfigProperty.RESOURCE_OPTION_UPDATE://修改时本地更新资源与对应的视图对象数据的状态
                String status;
                switch (type){
                    case ConfigProperty.RESOURCE_TYPE_FIREWALL://防火墙(只能修改基本信息)
                        firewallBiz.updateStatus(id, ConfigProperty.RESOURCE_OPTION_STATUS_UPDATING);
                        break;
                    case ConfigProperty.RESOURCE_TYPE_ROUTE://路由器(不能修改基本信息，只能连接上级和断开连接上级)
                        if (StrUtils.checkParam(previousUuid)){//如果传入了父级,则表明要连接上级,即连接防火墙
                            status = ConfigProperty.RESOURCE_OPTION_STATUS_LINKING_FIREWALL;
                        }else {//如果没有传入父级,则表明要断开连接上级,即断开连接防火墙
                            status = ConfigProperty.RESOURCE_OPTION_STATUS_UNLINKING_FIREWALL;
                        }
                        routeBiz.updateStatus(id, status);
                        break;
                    case ConfigProperty.RESOURCE_TYPE_NETWORK://网络(不能修改基本信息，只能连接上级和断开连接上级)
                        if (StrUtils.checkParam(previousUuid)){//如果传入了父级,则表明要连接上级,即连接路由器
                            status = ConfigProperty.RESOURCE_OPTION_STATUS_LINKING_ROUTE;
                        }else {//如果没有传入父级,则表明要断开连接上级,即断开连接路由器
                            status = ConfigProperty.RESOURCE_OPTION_STATUS_UNLINKING_ROUTE;
                        }
                        networkBiz.updateStatus(id, status);
                        break;
                    case ConfigProperty.RESOURCE_TYPE_VLBPOOL://负载均衡(只能修改基本信息)
                        vlbPoolBiz.updateStatus(id, ConfigProperty.RESOURCE_OPTION_STATUS_UPDATING);
                        break;
                    default:break;
                }
                break;
            case ConfigProperty.RESOURCE_OPTION_DELETE://删除时本地修改资源与对应视图对象数据的状态
                switch (type){
                    case ConfigProperty.RESOURCE_TYPE_FIREWALL://防火墙
                        firewallBiz.updateStatus(id, ConfigProperty.RESOURCE_OPTION_STATUS_DELETING);
                        break;
                    case ConfigProperty.RESOURCE_TYPE_ROUTE://路由器
                        routeBiz.updateStatus(id, ConfigProperty.RESOURCE_OPTION_STATUS_DELETING);
                        break;
                    case ConfigProperty.RESOURCE_TYPE_NETWORK://网络
                        networkBiz.updateStatus(id, ConfigProperty.RESOURCE_OPTION_STATUS_DELETING);
                        break;
                    case ConfigProperty.RESOURCE_TYPE_VLBPOOL://负载均衡
                        vlbPoolBiz.updateStatus(id, ConfigProperty.RESOURCE_OPTION_STATUS_DELETING);
                        break;
                    default:break;
                }
                break;
            default:break;
        }
    }

    /**
     * 给视图操作数据规定一个操作顺序的值
     * 删除-0，增加-1，修改-2
     * 防火墙-0，路由器-1，网络-2，负载均衡-3
     * @param list
     */
    public void sortVdcInfo(List<VdcInfo> list){
        if (StrUtils.checkParam(list)){
            for (VdcInfo vdcInfo : list) {
                String option = vdcInfo.getOption();
                String type = vdcInfo.getType();
                StringBuffer sequence = new StringBuffer();
                switch (option){
                    case ConfigProperty.RESOURCE_OPTION_DELETE:
                        sequence.append(0);
                        break;
                    case ConfigProperty.RESOURCE_OPTION_ADD:
                        sequence.append(1);
                        break;
                    case ConfigProperty.RESOURCE_OPTION_UPDATE:
                        sequence.append(2);
                        break;
                    default:break;
                }
                switch (type){
                    case ConfigProperty.RESOURCE_TYPE_FIREWALL:
                        sequence.append(0);
                        break;
                    case ConfigProperty.RESOURCE_TYPE_ROUTE:
                        sequence.append(1);
                        break;
                    case ConfigProperty.RESOURCE_TYPE_NETWORK:
                        sequence.append(2);
                        break;
                    case ConfigProperty.RESOURCE_TYPE_VLBPOOL:
                        sequence.append(3);
                        break;
                    default:break;
                }
                vdcInfo.setSequence(sequence.toString());
            }
        }
    }

    /**
     * 根据uuid获取真实id
     * @param uuid
     * @return
     */
    public String getIdByUuid(String uuid){
        String id = null;
        List<VdcItem> vdcItems = vdcItemDao.findByPropertyName(VdcItem.class, "uuid", uuid);
        if (vdcItems.size() > 0){
            id = vdcItems.get(0).getId();
        }
        return id;
    }

    /**
     * 根据id获取uuid
     * @param id
     * @return
     */
    public String getUuidById(String id){
        String uuid = null;
        VdcItem vdcItem = vdcItemDao.findById(VdcItem.class, id);
        if (StrUtils.checkParam(vdcItem)){
            uuid = vdcItem.getUuid();
        }
        return uuid;
    }


}
