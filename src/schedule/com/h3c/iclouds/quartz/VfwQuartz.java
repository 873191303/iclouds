package com.h3c.iclouds.quartz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.biz.InitCodeBiz;
import com.h3c.iclouds.biz.NetGroupBiz;
import com.h3c.iclouds.biz.VdeviceBiz;
import com.h3c.iclouds.client.SdnClient;
import com.h3c.iclouds.client.SdnParams;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.po.NetGroup;
import com.h3c.iclouds.po.Vdevice;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 同步虚拟防火墙数据
 * Created by ykf7317 on 2017/9/13.
 */
public class VfwQuartz extends BaseQuartz {
	
	private static CacheSingleton singleton = CacheSingleton.getInstance();

	private static String ip = singleton.getSdnApi(SdnParams.SDN_IP);
	
	@Resource
	private NetGroupBiz netGroupBiz;
	
	@Resource
	private VdeviceBiz vdeviceBiz;
	
	@Resource
	private InitCodeBiz initCodeBiz;
	
	@Override
	public void startQuartz () {
		//获取防火墙资产类型id
		String assetTypeId = initCodeBiz.getByTypeCode(ConfigProperty.CMDB_ASSET_TYPE_FIREWALL, ConfigProperty.CMDB_ASSET_TYPE).getId();
		Map<String, Object> queryMap = StrUtils.createMap("assType", assetTypeId);
		queryMap.put("ip", ip);
		NetGroup netGroup = netGroupBiz.singleByClass(NetGroup.class, queryMap);//查询是否存在该sdn的ip的堆叠
		if (!StrUtils.checkParam(netGroup)) {//没有则新建
			netGroup = new NetGroup();
			netGroup.setAssType(assetTypeId);
			netGroup.setIp(ip);
			netGroup.setIsAlone(ConfigProperty.NO);
			netGroup.setStackName("sdn_fw_group_" + ip);
			netGroup.createdUser(ConfigProperty.SYSTEM_FLAG);
			netGroupBiz.add(netGroup);
		}
		SdnClient sdnClient = SdnClient.createClient();//获取连接
		if (null == sdnClient) {
			LogUtils.warn(this.getClass(), "Get Sdn Client Failure");
		}
		//待删除的虚拟防火墙集合
		List<Vdevice> delete = vdeviceBiz.listByClass(Vdevice.class, StrUtils.createMap("stackId", netGroup.getId()));
		List<String> ids = new ArrayList<>();
		for (Vdevice vdevice : delete) {
			ids.add(vdevice.getId());
		}
		//获取虚拟防火墙数据
		JSONObject jsonObject = sdnClient.get(SdnClient.tranUrl(SdnParams.SDN_API_GET_FIREWALL));
		JSONObject record = SdnClient.getRecord(jsonObject);//record内容
		if (!SdnClient.checkResult(jsonObject)) {
			LogUtils.warn(this.getClass(), "Get VFW Data Failure! error:" + JSONObject.toJSONString(record));
		}
		if (record.containsKey("resources")) {
			JSONArray jsonArray = record.getJSONArray("resources");
			for (int i = 0; i < jsonArray.size(); i++) {
				record = jsonArray.getJSONObject(i);
				String type = record.getString("type");
				if ("VFW".equals(type)) {//虚拟防火墙类型数据
					String tenantId = record.getString("tenant_id");
					JSONArray vfArray = record.getJSONArray("nf_list");
					for (int j = 0; j < vfArray.size(); j++) {
						JSONObject vfJson = vfArray.getJSONObject(j);
						String name = vfJson.getString("name");
						String manageIp = vfJson.getString("management_ip");
						String portName = vfJson.getString("downlink_name");
						queryMap.clear();
						queryMap.put("stackId", netGroup.getId());
						queryMap.put("vassetName", name);
						Vdevice vdevice = vdeviceBiz.singleByClass(Vdevice.class, queryMap);
						if (!StrUtils.checkParam(vdevice)) {
							vdevice = new Vdevice();
							vdevice.setStackId(netGroup.getId());
							vdevice.setVassetName(name);
							vdevice.setIp(manageIp);
							vdevice.createdUser(ConfigProperty.SYSTEM_FLAG);
							vdevice.setPortName(portName);
							vdevice.setTenant(tenantId.replace("-", ""));
							vdeviceBiz.add(vdevice);
						}
						if (ids.contains(vdevice.getId())) {//移除需保留的数据
							ids.remove(vdevice.getId());
						}
					}
				}
			}
		}
		for (String id : ids) {
			vdeviceBiz.deleteById(Vdevice.class, id);
		}
	}
}
