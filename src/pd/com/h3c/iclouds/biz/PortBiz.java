package com.h3c.iclouds.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Port;

import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/26.
 */
public interface PortBiz extends BaseBiz<Port> {
	
	Object save(Port entity, CloudosClient client);

	Port get(NovaVm novaVm);
	
	Object dettach(String id, String serverId, CloudosClient client);
	
	Object delete(String id, CloudosClient client);
	
	Object attachHost(String id, String serverId, CloudosClient client);
	
	List<Port> getPorts();
	
	JSONArray getPortArray(String deviceId, String deviceOwner, CloudosClient client);
	
	JSONObject getPortJson(String cloudosId, CloudosClient client);
	
	Port getPortByJson(JSONObject portJson);
	
	void deleteLocalPort(Port port);
	
	boolean checkVip(String ip, String subnetId);
	
	void subPortQuota(String uuid, String projectId, CloudosClient client);
	
	Map<String, Object> getIpsByTenant(String tenantId, int pageSize, int pageNo);
	
	JSONObject cloudosSave(Port entity, String userCdId, CloudosClient client);
	
	void localSave(Port entity, JSONObject response, String subnetId);
	
}
