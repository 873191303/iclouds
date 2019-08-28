package com.h3c.iclouds.biz;

import java.util.List;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.IpRelation;

public interface IpRelationBiz extends BaseBiz<IpRelation> {

	/**
	 * 保存IP
	 * @param ip
	 * @param assetId
	 * @param classId
	 * @param ncid
	 * @param groupId
	 * @return
	 */
	public IpRelation saveIp(String ip, String assetId, String classId, String ncid, String groupId);
	
	/**
	 * 移除使用的IP
	 * @param ip
	 * @param entity
	 */
	public void removeIp(String ip, AsmMaster amsMaster, IpRelation entity, String ncid);

	public void removeIp(IpRelation ipRelation, String ncid);
	
	/**
	 * 查询资产关联的IP，不包括ILO口
	 * @param assetId
	 * @return
	 */
	public List<IpRelation> findAssetIp(String assetId, String classId);
	
}
