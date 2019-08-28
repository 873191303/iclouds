package com.h3c.iclouds.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.FloatingIp;
import com.h3c.iclouds.po.Port;

import java.util.List;

/**
* @author  zKF7420
* @date 2017年1月7日 上午9:42:25
*/
public interface FloatingIpBiz extends BaseBiz<FloatingIp> {
	
	Object update(FloatingIp ip, CloudosClient client);
	
	Object delete(FloatingIp ip, CloudosClient client);
	
	void localDelete(String id);
	
	/**
	 * 在cloudos上的删除
	 * @param cloudId 公网ip的cloudosId
	 * @param client cloudos连接
	 * @return 返回删除结果
	 */
	String cloudDelete(String cloudId, CloudosClient client);
	
	void localSave(FloatingIp floatingIp);
	
	/**
	 * 在cloudos上的新增
	 * @param floatingIp 公网ip
	 * @param client cloudos连接
	 * @return 返回新增结果
	 */
	String cloudSave(FloatingIp floatingIp, CloudosClient client, String type);
	
	void localUpdate (FloatingIp floatingIp);
	
	String cloudUpdate (FloatingIp floatingIp, CloudosClient client);
	
	/**
	 * 添加资源和资源类型
	 */
	void addResource(List<FloatingIp> floatingIps);
	
	/**
	 * 检验公网绑定的参数
	 * @param floatingIp 公网ip
	 * @param port 绑定网卡
	 * @return 返回值
	 */
	ResultType checkBind(FloatingIp floatingIp, Port port);
	
	/**
	 * 根据公网id获取该公网底下的公网ip可以分配的设备id集合
	 * @param networkId 公网id
	 * @return 返回设备id集合(云主机和负载均衡)
	 */
	List<String> getPortIds(String networkId);
	
	JSONArray getFloatingIpArray(CloudosClient client);
	
	JSONObject getFloatingIpJson(String cloudosId, CloudosClient client);
	
	FloatingIp getFloatingIpByJson(JSONObject fiJson);
	
	/**
	 * 获取已分配公网ip的数量
	 * @return
	 */
	int allotionCount();
	
	ResultType verify(FloatingIp entity, CloudosClient client);
}
