package com.h3c.iclouds.biz;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.business.RequestMaster;

public interface RequestMasterBiz extends BaseBiz<RequestMaster> {
	
	/**
	 * 根据审批人枫叶查询
	 * @param entity
	 * @return
	 */
	PageModel<RequestMaster> findForPageByApprover(PageEntity entity);

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
	 * 修改业务办理申请单
	 * @param map
	 * @return
	 * @throws Exception
	 */
	ResultType update(String id, Map<String, Object> map, Map<String, String> validateMap) throws Exception;
	
	/**
	 * 获取业务申请单审批列表
	 * @param entity
	 * @return
	 */
	PageModel<Map<String, Object>> findCompleteForPage(PageEntity entity, boolean needHistory);

	/**
	 *
	 * @param alias		参数
	 * @param entity	业务办理实体对象
	 * @param user		开始用户
	 * @return
    * @throws Exception
    */
	ResultType start(Map<String, Object> alias, RequestMaster entity, User user) throws Exception;
	
	/**
	 * 审批业务办理流程
	 * @param alias		参数
	 * @param entity	业务办理对象实体
     * @return
     */
	ResultType complete(Map<String, Object> alias, RequestMaster entity);
	
	/**
	 * 变更申请保存
	 * @param id
	 * @param map
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws Exception 
	 */
	ResultType saveChange(String reqId, Map<String, Object> map) throws Exception;
	
	/**
	 * 变更申请修改
	 * @param id
	 * @param map
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws Exception 
	 */
	public ResultType updateChange(RequestMaster entity, Map<String, Object> map) throws Exception;

	/**
	 * 获取审批情况
	 * need			需要处理
	 * already		已经处理
	 * @return
	 */
	Map<String, Object> queryUserApplyCount(); 
	
	/**
	 * 获取需求工单本月新增数量
	 * @return
	 */
	int count (String type);
}
