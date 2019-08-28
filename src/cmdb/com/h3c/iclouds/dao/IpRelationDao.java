package com.h3c.iclouds.dao;

import java.util.List;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.IpRelation;

public interface IpRelationDao extends BaseDAO<IpRelation> {

	/**
	 * 查询资产关联的IP，不包括ILO口
	 * @param assetId
	 * @return
	 */
	public List<IpRelation> findAssetIp(String assetId, String classId);
}
