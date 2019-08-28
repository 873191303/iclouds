package com.h3c.iclouds.biz.impl;

import java.util.List;

import javax.annotation.Resource;

import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import org.springframework.stereotype.Service;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.biz.IpRelationBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.dao.IpRelationDao;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.IpRelation;
import com.h3c.iclouds.po.IpRelationH;

@Service("ipRelationBiz")
public class IpRelationBizImpl extends BaseBizImpl<IpRelation> implements IpRelationBiz {

	@Resource(name = "baseDAO")
	private BaseDAO<IpRelationH> ipRelationHDao;
	
	@Resource
	private IpRelationDao ipRelationDao;
	
	@Override
	public IpRelation saveIp(String ip, String assetId, String classId, String ncid, String groupId) {
		List<IpRelation> list = this.findByPropertyName(IpRelation.class, "ip", ip);
		IpRelation entity = null;
		
		if(list != null && !list.isEmpty()) {
			entity = list.get(0);
			if(entity.getAssetId() != null) {	// 正在被使用，判断IP是否被使用
				return checkNetPort(entity, assetId, classId, ncid);
			}
		}
		
		// 设置IP操作历史
		String operation = ConfigProperty.IP_HISTORY_USE;
		if(entity == null) {	// 存在IP
			operation = ConfigProperty.IP_HISTORY_CREATE;
			entity = new IpRelation();
			entity.setIp(ip);
		}
		entity.setAssetId(assetId);
		entity.setClassId(classId);
		entity.setIsIlop(Integer.valueOf((ncid == null ? ConfigProperty.YES : ConfigProperty.NO)));
		entity.setNcid(ncid);
		entity.setGroupId(groupId);
		entity.createdUser(this.getLoginUser());
		if(operation.equals(ConfigProperty.IP_HISTORY_USE)) {
			this.update(entity);//如果ip已经存在且未绑定网口则绑定ip到这个网口
		} else {
			this.add(entity);//如果ip不存在则创建ip
		}
		
		// 保存IP使用历史
		IpRelationH entityH = new IpRelationH();
		entityH.setAssetId(assetId);
		entityH.setClassId(classId);
		entityH.setGroupId(groupId);
		entityH.setIp(ip);
		entityH.setNcid(ncid);
		entityH.createdUser(this.getLoginUser());
		entityH.setOperation(operation);
		ipRelationHDao.add(entityH);
		return entity;
	}
	
	/**
	 * 匹配端口信息
	 * @param entity
	 * @param assetId
	 * @param classId
	 * @param ncid
	 * @return
	 */
	private IpRelation checkNetPort(IpRelation entity, String assetId, String classId, String ncid) {
		if(!assetId.equals(entity.getAssetId())) {
			return null;
		} 
		if(!classId.equals(entity.getClassId())) {
			return null;
		}
		String entityNcid = entity.getNcid();
		// 其中有一个网卡id为空则表示网口不匹配
		if((ncid == null && entityNcid != null) || (ncid != null && entityNcid == null)) {
			return null;
		} else if(ncid != null && entityNcid != null) {
			if(!ncid.equals(entityNcid)) {
				return null;
			}
		}
		return entity;	// 与原本设备ID一致则返回成功
	}

	@Override
	public void removeIp(String ip, AsmMaster asmMaster, IpRelation entity, String ncid) {
		if(entity == null) {
			List<IpRelation> list = this.findByPropertyName(IpRelation.class, "ip", ip);
			if(list != null && !list.isEmpty()) {
				entity = list.get(0);
			}
		}
		if(entity != null) {
			if(entity.getAssetId() != null) {
				try {
					IpRelation checkResult = checkNetPort(entity, asmMaster.getId(), asmMaster.getAssetType(), ncid);
					if(checkResult == null) {
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				
				// 保存IP使用历史,先设置原本的信息
				IpRelationH entityH = new IpRelationH();
				entityH.setAssetId(entity.getAssetId());
				entityH.setClassId(entity.getClassId());
				entityH.setGroupId(entity.getGroupId());
				entityH.setIp(ip);
				entityH.setNcid(entity.getNcid());
				entityH.createdUser(this.getLoginUser());
				entityH.setOperation(ConfigProperty.IP_HISTORY_DELETE);
				ipRelationHDao.add(entityH);
				
				entity.setAssetId(null);
				entity.setClassId(null);
				entity.setNcid(null);
				entity.setGroupId(CacheSingleton.getInstance().getDefaultGroupId());
				entity.setIsIlop(null);
				entity.setUpdatedBy(getLoginUser());
				this.update(entity);//接触绑定
			}
		}
	}

	@Override
	public void removeIp(IpRelation ipRelation, String ncid){
		if(ipRelation.getAssetId() != null) {
			try {
				IpRelation checkResult = checkNetPort(ipRelation, ipRelation.getAssetId(), ipRelation.getClassId(), ncid);
				if(checkResult == null) {
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			// 保存IP使用历史,先设置原本的信息
			IpRelationH entityH = new IpRelationH();
			entityH.setAssetId(ipRelation.getAssetId());
			entityH.setClassId(ipRelation.getClassId());
			entityH.setGroupId(ipRelation.getGroupId());
			entityH.setIp(ipRelation.getIp());
			entityH.setNcid(ipRelation.getNcid());
			entityH.createdUser(this.getLoginUser());
			entityH.setOperation(ConfigProperty.IP_HISTORY_DELETE);
			ipRelationHDao.add(entityH);

			ipRelation.setAssetId(null);
			ipRelation.setClassId(null);
			ipRelation.setNcid(null);
			ipRelation.setGroupId(CacheSingleton.getInstance().getDefaultGroupId());
			ipRelation.setIsIlop(null);
			ipRelation.setUpdatedBy(getLoginUser());
			this.update(ipRelation);
		}
	}

	@Override
	public List<IpRelation> findAssetIp(String assetId, String classId) {
		return ipRelationDao.findAssetIp(assetId, classId);
	}

	@Override
	public PageModel<IpRelation> findForPage(PageEntity entity) {
		return ipRelationDao.findForPage(entity);
	}
}
