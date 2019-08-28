package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.bean.nova.MonitorInitBean;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.PortBiz;
import com.h3c.iclouds.biz.SpePortBiz;
import com.h3c.iclouds.client.zabbix.ZabbixApi;
import com.h3c.iclouds.client.zabbix.ZabbixDefine;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Port;
import com.h3c.iclouds.po.Rules;
import com.h3c.iclouds.po.SpePort;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service("speNetworksBiz")
public class SpePortBizImpl extends BaseBizImpl<SpePort> implements SpePortBiz {

    @Resource(name = "baseDAO")
    private BaseDAO<Rules> rulesDao;

    @Resource
    private PortBiz portBiz;
    
    @Resource
    private NovaVmBiz novaVmBiz;
    
    @Override
    public void initMonitor(MonitorInitBean bean, NovaVm novaVm) {
        Port monitorPort = this.portBiz.findById(Port.class, bean.getPortId());
        if(monitorPort == null) {
            throw MessageException.create(ResultType.port_not_exist);
        }
        if(monitorPort.getDeviceId() == null) {
            throw MessageException.create(ResultType.port_not_attach_server);
        }
        if(!StrUtils.equals(novaVm.getUuid(), monitorPort.getDeviceId())) {
            throw MessageException.create(ResultType.port_not_belong_vm);
        }

        String monitorId = null;

        ZabbixApi zabbixApi = null;
        try {
            zabbixApi = ZabbixApi.createAdmin();
            if(zabbixApi == null) {
                throw MessageException.create(ResultType.connection_monitor_failure);
            }

            Map<String, Object> queryMacMap = StrUtils.createMap("deviceId", novaVm.getUuid());
            queryMacMap.put("isinit", true);
            Port defaultPort = this.portBiz.singleByClass(Port.class, queryMacMap);
            if(defaultPort == null) {
                throw MessageException.create(ResultType.not_found_init_port);
            }
            String mac = defaultPort.getMacAddress();
            mac = mac.replace(":", "").toUpperCase();

            Rules image = rulesDao.findById(Rules.class, novaVm.getImageRef());
            String templateId = singleton.getZabbixConfig("zabbix.tonghuashun.centos.template");
            if(image != null && StrUtils.isFreeBSDImage(image.getOsType())) {
                templateId = singleton.getZabbixConfig("zabbix.tonghuashun.freebsd.template");
            }

            // mac地址作为主机名称使用
            Map<String, Object> createMap = StrUtils.createMap("host", mac);
            // 设置代理id
            createMap.put("proxy_hostid", singleton.getZabbixConfig("zabbix.tonghuashun.proxy"));

            // 设置链接方式，默认为agent链接方式
            String interfaceStr = ZabbixApi.replace(
                    ZabbixApi.INTERFACE_ARRAY_STRING,
                    monitorPort.getAddress(),
                    StrUtils.tranString(bean.getPort())
            );
            createMap.put("interfaces", JSONArray.parseArray(interfaceStr));

            // 设置主机群组
            String groups = ZabbixApi.replace(
                    ZabbixApi.GROUP_ARRAY_STRING,
                    singleton.getZabbixConfig("zabbix.tonghuashun.group")
            );
            createMap.put("groups", JSONArray.parseArray(groups));

            // 设置主机模板
            String templates = ZabbixApi.replace(ZabbixApi.TEMPLATE_ARRAY_STRING, templateId);
            createMap.put("templates", JSONArray.parseArray(templates));
            JSONObject jsonObject = zabbixApi.create(ZabbixDefine.HOST, createMap);
            monitorId = ZabbixApi.getCreateId(jsonObject, "hostids");
            if(monitorId != null) {
                SpePort entity = new SpePort();
                entity.setIp(monitorPort.getAddress());
                entity.setMac(monitorPort.getMacAddress());
                entity.setPortId(monitorPort.getId());
                entity.setTenantId(novaVm.getProjectId());
                entity.setUuid(novaVm.getUuid());
                entity.setHostId(novaVm.getId());
                entity.setMonitorId(monitorId);
                entity.setPort(bean.getPort());
                entity.createdUser(this.getLoginUser());
                this.add(entity);
                novaVm.setManageIp(monitorPort.getAddress());
                novaVmBiz.update(novaVm);
            } else {
                String errorMsg = ZabbixApi.getErrorMsg(jsonObject);
                throw MessageException.create(errorMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(monitorId != null) {
                JSONObject object = zabbixApi.delete(ZabbixDefine.HOST, monitorId);
                this.warn("删除监控结果：" + object.toString());
            }
            if(e instanceof  MessageException) {
                throw e;
            } else {
                this.exception(e, "初始化云主机监控异常");
                throw MessageException.create(ResultType.failure);
            }
        } finally {
            if(zabbixApi != null) {
                zabbixApi.logout();
            }
        }
    }

    @Override
    public void removeMonitor(SpePort entity) {
        ZabbixApi zabbixApi = null;
        try {
            String monitorId = entity.getMonitorId();
            this.delete(entity);
            NovaVm novaVm = novaVmBiz.findById(NovaVm.class, entity.getHostId());
            novaVm.setManageIp(null);
            novaVmBiz.update(novaVm);
            zabbixApi = ZabbixApi.createAdmin();
            if(zabbixApi == null) {
                throw MessageException.create(ResultType.connection_monitor_failure);
            }
            JSONObject jsonObject = zabbixApi.delete(ZabbixDefine.HOST, monitorId);
            if(jsonObject.containsKey("error")) {
                throw MessageException.create(ZabbixApi.getErrorMsg(jsonObject));
            }
        } catch (MessageException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            this.exception(e, "Remove monitor failure.");
            throw MessageException.create(ResultType.failure);
        } finally {
            if(zabbixApi != null) {
                zabbixApi.logout();
            }
        }

    }
}
