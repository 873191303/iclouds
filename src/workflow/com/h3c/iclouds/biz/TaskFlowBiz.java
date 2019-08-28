package com.h3c.iclouds.biz;

import java.util.Map;

import org.activiti.engine.task.Task;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.business.RequestMaster;

public interface TaskFlowBiz extends BaseBiz<RequestMaster> {


	/**
	 * 审批流程
	 * @param workFlowId	工作流id
	 * @param procInstId	业务办理流程实例id
	 * @param alias			参数
	 * @param createdBy		创建人
	 * @param userId		审批人
     * @return
     */
	public ResultType complete(String workFlowId, String procInstId, Map<String, Object> alias, String createdBy, String userId);

	/**
	 * 判断用户是否有审批权限
	 * @param segment		节点
	 * @param userId		当前用户id
	 * @param createdBy		申请单用户id
	 * @param workFlowId	流程对象
	 * @return
	 */
	public boolean taskAuth(String segment, String userId, String createdBy, String workFlowId);
	
	/**
	 * 查询流程任务节点
	 * @param instanceId
	 * @return
	 */
	public Task getTask(String instanceId);

	/**
	 * 查询下一个任务节点
	 * @param workFlowId	流程id
	 * @param segment		节点名称
	 * @param option		结果
     * @return
     */
	public String getNextTaskSegment(String workFlowId, String segment, String option);

	/**
	 * 开始处理任务
	 * @param workFlowId
	 * @param alias
	 * @param applyId
	 * @param createdBy
	 * @return
	 */
	public ResultType start(String workFlowId, Map<String, Object> alias, String applyId, String createdBy);

	/**
	 * 指派下一级审批人
	 * @param workFlowId
	 * @param instanceId
	 * @param approver
	 * @param createdBy
	 * @return
	 */
	public ResultType claim(String workFlowId, String instanceId, String approver, String createdBy);
	
	/**
	 * 发送审批邮件
	 * @param step
	 * @param workflowId
	 * @param user
	 * @param attachment
	 * @param mailContent 
	 */
	public void sendApplierEmail(String step, String workflowId, User user, String attachment, String key,String mailContent);
}
