package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.NetPorts;

import java.util.List;

public interface NetPortsBiz extends BaseBiz<NetPorts> {
	
	/**
	 * 查询堆叠设备下的网口
	 * @param stackId
	 * @param type		switch	--	交换机堆叠；	router	--	路由器堆叠
	 * @return
	 */
	List<NetPorts> findPortByStack(String stackId, String type);

	void deleteNetports(NetPorts netPorts);

}
