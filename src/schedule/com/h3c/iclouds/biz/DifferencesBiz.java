package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.CompareEnum;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Differences;

import java.util.List;

public interface DifferencesBiz extends BaseBiz<Differences> {

	/**
	 * 保存差异部分
	 * @param uuid	
	 * 			根据差异类型，记录对应部分的id
	 * 			在cloudos存在则记录的为uuid
	 * 			在iyun存在则记录的为id
	 * @param projectName
	 * 			租户名称
	 * @param description
	 * 			内容描述，可能是路由器连接问题，也可能是云主机缺少问题
	 * @param diffType
	 * 			在cloudos中存在，iyun不存在：CompareEnum.IN_CLOUDOS
	 * 			在cloudos中不存在，iyun存在：CompareEnum.IN_IYUN
	 * 			两端都存在，需要做数据差异：CompareEnum.DATA_DIFF
	 * @param dataType
	 * 			同步数据类型：参考 CompareEnum
	 * @return
	 */
	String addDiff(String uuid, String projectName, String description, CompareEnum diffType, CompareEnum dataType);
	
	/**
	 * 处理差异部分的内容
	 * @param entity
	 * @param handleType	处理类型：删除（OPE_DEL）修复（OPE_SYNC）同步差异（OPE_DIFF）
	 * @return
	 */
	ResultType handleDiff(Differences entity, CompareEnum handleType);
	
	String getFixedPortId(String fixPortCdId);
	
	String getRouterId(String routeCdId);
	
	boolean checkLocalResource (CompareEnum type, String userId, String tenantId);
	
	String getGroupId(List<String> roleIds, String projectId);
	
	/**
	 * 同步配额到iyun
	 * @param tenantId 租户id
	 */
	public void syncIyunQuota (String tenantId, CloudosClient client);
	
	public void syncIyunAzone (String tenantId, CloudosClient client);
}
