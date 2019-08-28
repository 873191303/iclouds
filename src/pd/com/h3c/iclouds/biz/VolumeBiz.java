package com.h3c.iclouds.biz;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Renewal;
import com.h3c.iclouds.po.Volume;
import java.util.List;

public interface VolumeBiz extends BaseBiz<Volume> {
	
	//云硬盘续租
	ResultType Renewal(Renewal bean, String userId,String day); 
	//云硬盘扩容
	ResultType Capacity(Volume bean,String capacity);
	
	void save(Volume entity, String userId, CloudosClient client);
	
	Volume update(String id, Volume entity);
	
	/**
	 * 卸载云硬盘
	 * @param id 云硬盘id
	 * @param serverId 主机id
	 */
	void dettach(String id, String serverId);
	
	Object delete(String id);

	/**
	 * 云硬盘挂载到主机
	 * @param id 云硬盘id
	 * @param server_id 主机 uuid
	 */
	void attach(String id, String server_id);
	
	/**
	 * 从回收站删除云硬盘
	 * @param volume
	 */
	void deleteFromRecycle(Volume volume);
	
	void recoverFromRecycle(Volume volume);
	
	/** 用于同步硬盘状态*/
	String tranState(String state);

	List<Volume> findVolume(NovaVm novaVm);
	
	/**
	 * 获取本月新增的事件工单数量
	 * @return
	 */
	int monthCount();
	
	JSONObject getVolumeJson(String uuid, String projectId, CloudosClient client);
}
