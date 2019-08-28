package com.h3c.iclouds.biz;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.inc.Inc2ApproveLog;
import com.h3c.iclouds.po.inc.IncMaster;

public interface IncMasterBiz extends BaseBiz<IncMaster> {
	
	/**
	 * 根据审批人枫叶查询
	 * @param entity
	 * @return
	 */
	PageModel<IncMaster> findForPageByApprover(PageEntity entity);

	/**
	 * 保存业务办理申请单
	 * @param map
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws Exception 
	 */
	ResultType save(Map<String, Object> map, Map<String, String> validateMap) throws Exception;
	
	/**
	 * 获取业务申请单审批列表
	 * @param entity
	 * @return
	 */
	PageModel<Map<String, Object>> findCompleteForPage(PageEntity entity);

	/**
	 *
	 * @param alias		参数
	 * @param entity	业务办理实体对象
	 * @param user		开始用户
	 * @return
    * @throws Exception
    */
	ResultType start(Map<String, Object> alias, IncMaster entity) throws Exception;
	
	/**
	 * 审批业务办理流程
	 * @param alias		参数
	 * @param entity	业务办理对象实体
     * @return
     */
	ResultType complete(Map<String, Object> alias, IncMaster entity);
	
	/**
	 * 关闭任务
	 * @param alias
	 * @param entity
	 * @return
	 */
	Inc2ApproveLog close(Map<String, Object> alias, IncMaster entity);
	
	/**
	 * 获取本月新增的数量
	 * @return
	 */
	int count(String type);

	Map<String, Object> queryUserApplyCount();

}
