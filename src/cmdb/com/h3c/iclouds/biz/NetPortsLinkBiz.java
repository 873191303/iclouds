package com.h3c.iclouds.biz;

import java.util.List;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.NetPortsLink;

public interface NetPortsLinkBiz extends BaseBiz<NetPortsLink> {
	
	/**
	 * 根据网口id查询网口连接对象
	 * @param netPortId
	 * @return
	 */
	public List<NetPortsLink> findByNetPortId(String netPortId);
	
	/**
	 * 保存连接
	 * @param operationId	操作的网口id
	 * @param entity		保存的对象
	 */
	public void saveLink(String operationId, NetPortsLink entity);
	
}
