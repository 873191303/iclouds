package com.h3c.iclouds.operate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.po.bean.cloudos.InterfaceAttachment;
import com.h3c.iclouds.biz.NetworkBiz;
import com.h3c.iclouds.dao.PortDao;
import com.h3c.iclouds.po.IpAllocation;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Port;
import com.h3c.iclouds.po.Subnet;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.StrUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CloudosNovaVm extends CloudosBase {

	public boolean isCorrect(JSONObject result) {
		if ("200".equals(result.getString("result")) || "203".equals(result.getString("result"))) {
			return true;
		} else {
			return false;
		}
	}

	public CloudosNovaVm(CloudosClient client) {
		this.client = client;
	}

	public List<InterfaceAttachment> getOsInterface(String tenantId, String serverId, Port port) {
		List<InterfaceAttachment> interfaceAttachments1 = new ArrayList<InterfaceAttachment>();
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_OSINTERFACE);
		uri = HttpUtils.tranUrl(uri, tenantId, serverId);
		JSONObject vmstate = client.get(uri);
		JSONArray interfaceAttachments = HttpUtils.getJSONArray(vmstate, "interfaceAttachments");
		if (StrUtils.checkCollection(interfaceAttachments)) {
			for (Object object : interfaceAttachments) {
				if (StrUtils.checkParam(object)) {
					InterfaceAttachment interfaceAttachment2 = new InterfaceAttachment();
					JSONObject interfaceAttachment = (JSONObject) object;
					if (port.getCloudosId().equals(interfaceAttachment.getString("port_id"))) {
						continue;
					}
					String net_id = interfaceAttachment.getString("net_id");
					interfaceAttachment2.setNet_id(net_id);
					String port_id = interfaceAttachment.getString("port_id");
					interfaceAttachment2.setPort_id(port_id);
					String port_state = interfaceAttachment.getString("port_state");
					interfaceAttachment2.setPort_tate(port_state);
					interfaceAttachments1.add(interfaceAttachment2);
				}
			}

		}
		return interfaceAttachments1;
	}

	public JSONObject delete(String uri) {
		return null;
	}

	public boolean check(NovaVm novaVm) {
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_SERVER_GET);
		uri = HttpUtils.tranUrl(uri, novaVm.getProjectId(), novaVm.getUuid());
		JSONObject vmstate = client.get(uri);
		JSONObject server = HttpUtils.getJSONObject(vmstate, "server");
		// 关机中被删除
		if (StrUtils.checkParam(server)) {
			String state = server.getString("status");
			if ("ACTIVE".equals(state)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	public String getState(NovaVm novaVm) {
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_SERVER_GET);
		String uuid=novaVm.getUuid();
		if(!StrUtils.checkParam(uuid)) {
			return null;
		}
		uri = HttpUtils.tranUrl(uri, novaVm.getProjectId(), uuid);
		JSONObject vmstate = client.get(uri);
		JSONObject server = HttpUtils.getJSONObject(vmstate, "server");
		// 关机中被删除
		if (StrUtils.checkParam(server)) {
			String state = server.getString("status");
			return state;
		}
		return null;
	}

	public Map<String, String> savePort(NovaVm novaVm, JSONObject interfaceAttachment, String projectId) {
		String networkId = interfaceAttachment.getString("net_id");
		PortDao portDao = SpringContextHolder.getBean("portDao");
		NetworkBiz networkBiz = SpringContextHolder.getBean("networkBiz");
		BaseDAO<Subnet> subnetDao = SpringContextHolder.getBean("baseDAO");
		BaseDAO<IpAllocation> ipAllocationDao = SpringContextHolder.getBean("baseDAO");
		// 插入端口表
		Port port = new Port();
		port.setTenantId(projectId);
		port.setDeviceId(novaVm.getUuid());
		port.setDeviceOwner(interfaceAttachment.getString("device_owner"));
		port.createdUser(novaVm.getOwner());
		port.setUserId(novaVm.getOwner());
		port.setNetWorkId(networkId);
		// String subnetId = interfaceAttachment.getString("net_id");
		String port_id = interfaceAttachment.getString("port_id");// 获得网卡id
		String macAddress = interfaceAttachment.getString("mac_addr"); // 网卡mac地址
		port.setMacAddress(macAddress);
		port.setCloudosId(port_id);
		this.syncPortName(port_id, port, novaVm); // 同步网卡名称
		// 云主机创建的自带网卡标志
		port.setIsinit(true);
		portDao.add(port);
        Map<String, String> map = new HashMap<>();
        map.put("macAddr", macAddress);
		JSONArray fixed_ips = interfaceAttachment.getJSONArray("fixed_ips");
		for (int k = 0; k < fixed_ips.size(); k++) {
			JSONObject fixed_ip = fixed_ips.getJSONObject(k);
			String ipAddress = fixed_ip.getString("ip_address");
            map.put("ipAddr", ipAddress);
			// String subnet_id = fixed_ip.getString("subnet_id");
			// 插入Ip使用表
			IpAllocation ipAllocation = new IpAllocation();
			// 防止外键插不进去
			List<Port> ports = portDao.findByPropertyName(Port.class, "deviceId", novaVm.getUuid());
			ipAllocation.setPortId(ports.get(0).getId());
			ipAllocation.setIpAddress(ipAddress);
			List<Network> networks2 = networkBiz.findByPropertyName(Network.class, "cloudosId", networkId);
			Network network = networks2.get(0);
			map.put("cidr", network.getCidr());

			List<Subnet> subnets = subnetDao.findByPropertyName(Subnet.class, "networkId", network.getId());
			Subnet subnet = subnets.get(0);
			ipAllocation.setSubnetId(subnet.getId());// 外键
			ipAllocationDao.add(ipAllocation);
		} // for循环
		return map;
	}
	
	private void syncPortName(String portId, Port port, NovaVm novaVm) {
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_PORTS_ACTION);
		uri = HttpUtils.tranUrl(uri, portId);
		JSONObject response = client.get(uri);
		if (ResourceHandle.judgeResponse(response)) {
			response=HttpUtils.getJSONObject(response, "port");
			port.setDeviceOwner(response.getString("device_owner"));
			port.setStatus(response.getString("status"));
			Boolean admin_state_up=response.getBoolean("admin_state_up");
			if (StrUtils.checkParam(admin_state_up)) {
				port.setAdminStateUp(admin_state_up);
			}
			port.setPortType(response.getString("binding:vnic_type"));
			String name = response.getString("name");
			if (StrUtils.checkParam(name)) {
				port.setName(name);
			} else {
				port.setName(generate(novaVm.getHostName()));
			}
		}
	}
	
	private String generate(String hostName) {
		String result = "";
		String uuid = UUID.randomUUID().toString().substring(0, 6);
		result += hostName + "_VirtualNIC_" + uuid;
		return result;
	}
}
