package com.h3c.iclouds.quartz;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.biz.AsmMasterBiz;
import com.h3c.iclouds.biz.Class2ItemsBiz;
import com.h3c.iclouds.biz.InterfacesBiz;
import com.h3c.iclouds.biz.NetPortsBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.Class2Items;
import com.h3c.iclouds.po.Interfaces;
import com.h3c.iclouds.po.NetPorts;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.SnmpUtils;
import com.h3c.iclouds.utils.StrUtils;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zkf5485 on 2017/7/20.
 */
public class NetworkAssetQuartz extends BaseQuartz {

    public static final String[] product = {
            "10.166.66.1",
            "10.166.66.2",
            "10.166.66.3",
            "10.166.66.5",
            "10.166.66.6",
            "10.166.66.8",
            "10.166.66.9",
            "10.166.66.10",
            "10.166.66.11",
            "10.166.66.13",
            "10.166.66.14",
            "10.166.66.15",
            "10.166.66.20",
            "10.166.66.21",
            "10.166.66.22",
            "10.166.66.23",
            "10.166.66.24"
    };

    public static final String product_octet = "zjzwyprivate";

    public static final String[] test = {
            "10.16.16.1",
    };

    public static final String test_octet = "public";

    @Resource
    private InterfacesBiz interfacesBiz;

    @Resource
    private AsmMasterBiz asmMasterBiz;

    @Resource
    private NetPortsBiz netPortsBiz;

    @Resource
    private Class2ItemsBiz class2ItemsBiz;

    private static int version = SnmpConstants.version2c;

    @Override
    public void startQuartz() {
        String type = "snmp-switch";
        List<Interfaces> list = this.interfacesBiz.listByClass(Interfaces.class, StrUtils.createMap("type", type));

        Map<String, String> oidMap = CacheSingleton.getInstance().getSnmpOIDConfigMap();
        String sysName = oidMap.get("snmp.sysName");
//        String sysDesc = oidMap.get("snmp.sysDesc");

        String assetType = CacheSingleton.getInstance().getSwitchAssetType();
        list.forEach(entity -> {
            String ip = entity.getIp();
            if(!ip.equals("10.16.16.1")) {
                return;
            }
            SnmpUtils snmpUtils = new SnmpUtils(ip, entity.getAdmin(), Integer.valueOf(entity.getPort()), version);String assetName = snmpUtils.sendRequest(sysName);
            if(!StrUtils.checkParam(assetName)) {
                LogUtils.warn(this.getClass(), "Get Asset failure.");
            } else {
                String osName = ip + "_" + assetName;
                Map<String, Object> queryMap = StrUtils.createMap("os", osName);
                queryMap.put("assetType", assetType);   // 交换机
                AsmMaster asmMaster = this.asmMasterBiz.singleByClass(AsmMaster.class, queryMap);
                if(asmMaster == null) { // 不存在则新建资产
                    asmMaster = new AsmMaster();
                    asmMaster.setAssetName(osName); // 设备名称
                    asmMaster.setSerial(osName);    // 序列号
                    asmMaster.setAssetId(osName);   // 资产编号
                    asmMaster.setOs(osName);
                    asmMaster.setDepart(ConfigProperty.SYSTEM_FLAG);
                    asmMaster.setAssetUser(ConfigProperty.SYSTEM_FLAG);
                    asmMaster.setAssetType(assetType);
                    asmMaster.setProvide(ConfigProperty.SYSTEM_FLAG);
                    asmMaster.setStatus(ConfigProperty.CMDB_ASSET_FLAG2_USE);   // 使用中状态
                    asmMaster.setIloIP(ip);
                    asmMaster.setTenantId(CacheSingleton.getInstance().getRootProject());
                    asmMaster.createdUser(ConfigProperty.SYSTEM_FLAG);
                    asmMaster.setBegDate(new Date());
                    asmMaster.setAssetTypeCode(ConfigProperty.CMDB_ASSET_TYPE_SWITCH);
                    queryMap.clear();
                    queryMap.put("itemName", assetName);
                    queryMap.put("resType", assetType);
                    Class2Items item = class2ItemsBiz.singleByClass(Class2Items.class, queryMap);
                    if(item == null) {  // 不存在设备类型则增加
                        item = new Class2Items();
                        item.setFlag(ConfigProperty.YES);
                        item.setItemId(assetName);
                        item.setItemName(assetName);
                        item.setResType(assetType);
                        item.setUnum(2);    //  默认为2U
                        item.createdUser(ConfigProperty.SYSTEM_FLAG);
                        this.class2ItemsBiz.add(item);
                    }
                    asmMaster.setAssMode(item.getId());
                    this.asmMasterBiz.add(asmMaster);
                }
                List<NetPorts> ports = this.netPortsBiz.findByPropertyName(NetPorts.class, "masterId", asmMaster.getId());
                Map<Integer, NetPorts> portMap = new HashMap<>();
                if(StrUtils.checkCollection(ports)) {
                    ports.forEach(netport -> portMap.put(netport.getSeq(), netport));
                }
                this.savePorts(snmpUtils, oidMap, portMap, asmMaster.getId());
                // TODO: 2017/7/20 保存连接信息

            }
        });
    }

    private void savePorts(SnmpUtils utils, Map<String, String> oidMap, Map<Integer, NetPorts> portMap, String masterId) {
//        String ifIndex = oidMap.get("snmp.ifIndex");
//        String ifOperStatus = oidMap.get("snmp.ifOperStatus");
        String ifDescr = oidMap.get("snmp.ifDescr");
//        String ifType = oidMap.get("snmp.ifType");
        String ifPhysAddress = oidMap.get("snmp.ifPhysAddress");
        List<TableEvent> descs = utils.snmpWalk(ifDescr, 100000);
        descs.forEach(event -> {
            int index = StrUtils.tranInteger(event.getIndex().toString());
            NetPorts port = portMap.get(index);
            if(port == null) {
                port = new NetPorts();
                port.setSeq(index);
                port.setMasterId(masterId);
                port.setEthType("0");
                port.setPortType("0");
                port.setRemark(this.getTableEventString(event));
                portMap.put(index, port);
            }
        });
        List<TableEvent> macs = utils.snmpWalk(ifPhysAddress, 100000);
        macs.forEach(event -> {
            int index = StrUtils.tranInteger(event.getIndex().toString());
            NetPorts port = portMap.get(index);
            if(port != null) {
                port.setMac(this.getTableEventString(event).replace(":", "-"));
            }
        });
        portMap.values().forEach(entity -> {
            if(entity.getId() == null) {
                entity.createdUser(ConfigProperty.SYSTEM_FLAG);
                this.netPortsBiz.add(entity);
            } else {
                entity.updatedUser(ConfigProperty.SYSTEM_FLAG);
                this.netPortsBiz.update(entity);
            }
        });
    }

    private String getTableEventString(TableEvent event) {
        String value = ConfigProperty.SYSTEM_FLAG;
        VariableBinding[] vbs = event.getColumns();
        if(vbs != null && vbs.length > 0) {
            VariableBinding vb = vbs[0];
            if(StrUtils.checkParam(vb.toString())) {
                value = vb.toValueString();
            }
        }
        return value;
    }
}
