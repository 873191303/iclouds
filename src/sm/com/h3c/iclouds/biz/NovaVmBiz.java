package com.h3c.iclouds.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.Renewal;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Server2Vm;
import com.h3c.iclouds.po.bean.cloudos.CloneOrImageBean;
import com.h3c.iclouds.po.bean.inside.SaveNovaVmBean;
import com.h3c.iclouds.po.bean.outside.NovaVmDetailBean;

import java.util.List;
import java.util.Map;

public interface NovaVmBiz extends BaseBiz<NovaVm> {
	
 

	ResultType save(SaveNovaVmBean bean, String userId, CloudosClient client) throws Exception;
	//修改云主机密码
	ResultType update(SaveNovaVmBean bean,String novaVmId, String userId, String  encryptPwd) throws Exception;
	//云主机快照
	ResultType snapshot(String novaVmId);

	/**
	 * 云主机操作的动作
	 * 
	 * @param novaVm
	 * @param vmState
	 * @param params
	 * @param busType
	 */
	void action(NovaVm novaVm, String vmState, Map<String, Object> params, String busType);

	NovaVmDetailBean detail(NovaVm novaVm) throws Exception;

	// 查询云主机密码（解密）
	String selPassWord(NovaVm novaVm);

	// 查询云主机密码（加密）
	String setPassWord(NovaVm novaVm);

	Object delete(String id);

	ResultType vmToImage(NovaVm novaVm, Server2Vm entity, CloneOrImageBean bean);

	JSONArray getServerArray(CloudosClient client);

	/**
	 * 获取本月新增的云主机数量
	 * 
	 * @return
	 */
	int monthCount();

	/**
	 * 根据租户，名称前缀查询云主机
	 * 
	 * @param projectId
	 * @param profix
	 * @return
	 */
	List<NovaVm> findListByProfix(String projectId, String profix);

	ResultType verify(SaveNovaVmBean bean, CloudosClient client);

	List<Map<String, Object>> getServerPort(String subnetId);

	String getIpByUuid(String uuid, boolean flag);

	void deleteNovaVm(NovaVm novaVm, String busId, CloudosClient client);

	JSONObject getVmJson(String uuid, String projectId, CloudosClient client);

	boolean auth(String id);

	void writeBack(NovaVm novaVm, String uuid);
}
