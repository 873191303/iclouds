package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.Config;

import java.util.List;
import java.util.Map;

public interface AsmMasterBiz extends BaseBiz<AsmMaster> {
	
	PageModel<AsmMaster> findForPage(PageEntity entity, boolean flag);
	
	/**
	 * 设备配置
	 * @param asmMaster
	 * @param map
	 * @return
	 */
	ResultType config(AsmMaster asmMaster, Map<String, Object> map);
	
	/**
	 * 添加或者修改资产信息
	 * @param map
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	ResultType saveOrUpdate(Map<String, Object> map, String id);

	/**
	 * 获取对应类型dao及泛型
	 * @param type
	 * @return
	 */
	Config getConfigByType(String type);
	
	/**
	 * 不在堆叠中的交换机、路由器
	 * @param entity
	 * @return
	 */
	PageModel<AsmMaster> without(PageEntity entity);

	int otherUseFlag();
	
	void initMonitor(List<Map<String, String>> ipMacs);
	
	/**
	 * 获取excle中数据 并导入数据库中
	 * @param path 数据所在路径
	 */
	Object getAllDate(String path);
	
}
