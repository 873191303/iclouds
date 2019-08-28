package com.h3c.iclouds.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.bean.outside.TenantNetworkBean;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.po.Subnet;

import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/23.
 */
public interface NetworkBiz extends BaseBiz<Network> {
	
	Map<String, Object> delete(String id, CloudosClient client, boolean external);
	
	Map<String, Object> linkRoute(String id, String routeId, CloudosClient client);
	
	Map<String, Object> unlinkRoute(String id, CloudosClient client);

    void vdcSave(Network entity, String vdcId, String projectId);

	String cloudSave(Network entity, Subnet subnet, CloudosClient client, String type);

	void localSave(Network network, Subnet subnet, String vdcId, String status);

	String cloudDelete(String ntCdId, String sbCdId, CloudosClient client);

	void localDelete(Network network);

	JSONObject cloudLink(String subCloudId, String rtCloudId, CloudosClient client, String type);

	void localLink(Network network, String routeId, String portCloudId, String type, CloudosClient client);
	
	ResultType verify (Network network, Subnet subnet);
	
    /**
     * 获取创建云主机的基础网络信息
     * @param id
     * @return
     */
    List<TenantNetworkBean> getTenantNetwork(String id);
	
	void updateStatus(String id, String status);
	
	List<Map<String, String>> getPoolList(List<Subnet> subnets, List<Map<String, String>> ipList);
	
	JSONArray getNetworkArray(CloudosClient client);
	
	JSONObject getNetworkJson(String cloudosId, CloudosClient client);
	
	JSONObject getSubnetJson(String cloudosId, CloudosClient client);
	
	Network getNetworkByJson(JSONObject networkJson);
	
	Subnet getSubnetByJson(JSONObject subnetJson);
	
	void addNetworkParam(Network network, JSONObject subnetJson);
	
	void backWrite(Network network, Subnet subnet, CloudosClient client);
	
	ResultType checkNetworkConflict(String publicSubnetId, List<Subnet> privateSubnets);
	
	/**
	 * 查询ip属于租户的哪个网络
	 * @param ip
	 * @param tenantId
	 * @return
	 */
	String getNetworkIdByIp(String ip, String tenantId);
}
