package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.DepartmentBiz;
import com.h3c.iclouds.biz.TaskFlowBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SimpleCache;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.WorkFlowDao;
import com.h3c.iclouds.dao.WorkRoleDao;
import com.h3c.iclouds.po.MailTemplates;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.WorkFlow;
import com.h3c.iclouds.po.WorkRole;
import com.h3c.iclouds.po.business.RequestMaster;
import com.h3c.iclouds.thread.SendMailThread;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.PatternValidator;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("taskFlowBiz")
public class TaskFlowBizImpl extends BaseBizImpl<RequestMaster> implements TaskFlowBiz {
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;
	
	@Resource
	private WorkFlowDao workFlowDao;
	
	@Resource
	private WorkRoleDao workRoleDao;
	
	@Resource
	private TaskService taskService;
	
	@Resource
	private IdentityService identityService;
	
	@Resource
	private RuntimeService runtimeService;

	@Resource
	private RepositoryService repositoryService;
	
	@Resource
	private DepartmentBiz departmentBiz;
	
	@Resource
	private UserBiz userBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<MailTemplates> mailTemplatesDao;
	
	@Override
	public ResultType start(String workFlowId, Map<String, Object> alias, String applyId, String createdBy) {
		WorkFlow flow = this.workFlowDao.findById(WorkFlow.class, workFlowId);
		if(flow != null) {
			identityService.setAuthenticatedUserId(createdBy);	// 发起人id
			String key = flow.getKey() + flow.getVersion();
			this.info( "Start workflow, flowId:" + flow.getId() + "\t----\tbusId:" + applyId); 
			ProcessInstance instance = runtimeService.startProcessInstanceByKey(key, applyId);
			if(instance != null) {
				// 写入map，用于返回审批实例id记录
				alias.put("instanceId", instance.getId());
				return ResultType.success;
			}
			return ResultType.failure;
		}
		this.warn("Workflow " + workFlowId + " not exist.");
		return ResultType.deleted;
	}
	
	@Override
	public ResultType claim(String workFlowId, String instanceId, String approver, String createdBy) {
		Task task = this.getTask(instanceId);	// 获取流程中的任务
		if(task != null) {
			if(task.getAssignee() != null) {	// 申请单已经被签收
				if(!task.getAssignee().equals(approver)) {	// 签收的申请单是否为当前用户
					return ResultType.bus_assigne_error; // 申请单已被其它用户签收,审批失败
				}
			}
			// 获取任务节点对应ID
			String segment = task.getTaskDefinitionKey();
			boolean auth = this.taskAuth(segment, approver, createdBy, workFlowId);
			if(auth) {
				this.info( "Claim task, taskId:" + task.getId() + "----approver:" + approver);
				taskService.claim(task.getId(), approver);	// 签收申请，锁定申请单
				return ResultType.success;
			}
			this.warn("Approver " + approver + " can't claim task:" + task.getId());
		}
		return ResultType.unAuthorized;
	}

	@Override
	public Task getTask(String instanceId) {
		Task task = taskService.createTaskQuery().processInstanceId(instanceId).singleResult();	// 获取流程中的任务
		return task;
	}
	
	@Override
	public ResultType complete(String workFlowId, String instanceId, Map<String, Object> alias, String createdBy, String userId) {
		Task task = this.getTask(instanceId);	// 获取流程中的任务
		if(task != null) {	// 任务存在
			if(task.getAssignee() != null) {	// 申请单已经被签收
				if(!task.getAssignee().equals(userId)) {	// 签收的申请单是否为当前用户
					return ResultType.bus_assigne_error; // 申请单已被其它用户签收,审批失败
				}
			}
			// 获取任务节点对应ID
			String segment = task.getTaskDefinitionKey();
			boolean auth = this.taskAuth(segment, userId, createdBy, workFlowId);
			if(auth) {
				taskService.claim(task.getId(), userId);	// 签收申请，锁定申请单
				Authentication.setAuthenticatedUserId(userId);	// 设置当前用户
				String comment = StrUtils.tranString(alias.get(ConfigProperty.REQUEST_COMMENT));
				String result = StrUtils.tranString(alias.get(ConfigProperty.REQUEST_PARAMETER));
				taskService.addComment(task.getId(), instanceId, result, comment);	// 插入批注
				Map<String, Object> taskVariables = new HashMap<String, Object>();
	            taskVariables.put(ConfigProperty.REQUEST_PARAMETER, result);	// 审批结果
	            this.info( "Complete task, taskId:" + task.getId() + "----approver:" + userId + "----taskVariables:" + taskVariables.toString());
	            taskService.complete(task.getId(), taskVariables);
	            // 回写到审批记录字段
	            alias.put("segment", segment);
	            alias.put("taskId", task.getId());
	            return ResultType.success;
			} else {
				return ResultType.task_complete_error;
			}
		}
		return ResultType.unAuthorized;
	}
	
	/**
	 * 根据节点查询角色
	 * @param segment
	 * @param workFlowId
	 * @return
	 */
	private List<WorkRole> getRoleBySegment(String segment, String workFlowId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("processSegment", segment);
		map.put("workFlowId", workFlowId);
		List<WorkRole> roles = workRoleDao.listByClass(WorkRole.class, map);
		return roles;
	}
	
	@Override
	public boolean taskAuth(String segment, String userId, String createdBy, String workFlowId) {
		List<WorkRole> roles = getRoleBySegment(segment, workFlowId);
		// 当前节点是否存在需要审批的角色
		if(roles != null && !roles.isEmpty()) {
			WorkRole role = roles.get(0);	// 获取当前环节角色'
			// 判断是否需要做部门过滤，如果需要则追溯到顶端部门要保持一致
			if(role.getRoleKey().contains(ConfigProperty.REQUEST_DEPARTMENT_CONTROL)) {
				String approverDeptId = departmentBiz.getHeadDeptIdByUserId(userId);
				if(approverDeptId == null) {	// 部门id不存在
					return false;
				}
				String applierDeptId = departmentBiz.getHeadDeptIdByUserId(createdBy);
				if(!approverDeptId.equals(applierDeptId)) {	// 一级部门id不一致
					return false;
				}
			}
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("WORKFLOWID", workFlowId);
			map.put("SEGMENT", segment);
			map.put("USERID", userId);
			List<Map<String, Object>> list = this.sqlQueryBiz.queryByName(SqlQueryProperty.APPROVE_AUTH, map);
			if(list != null && !list.isEmpty()) {
				Map<String, Object> resultMap = list.get(0);
				int count = StrUtils.tranInteger(resultMap.get("ct"));
				if(count > 0) {
					// 需求工单前面2个环节需要判断是否属于同一个租户
					if(StrUtils.equals(segment, ConfigProperty.MASTER_STEP2_DEPARTMENT_APPROVE, ConfigProperty.MASTER_STEP3_SIGN_APPROVE)) {
						User user = this.userBiz.findById(User.class, createdBy);
						if(!this.getProjectId().equals(user.getProjectId())) {
							return false; 
						}
					}
					return true;
				}
			}
		} else {
			return createdBy.equals(userId);	// 当前申请人直接判断
		}
		return false;
	}

	@Override
	public String getNextTaskSegment(String workFlowId, String segment, String option) {
		WorkFlow workFlow = this.workFlowDao.findById(WorkFlow.class, workFlowId);
		if(workFlow == null) {
			return null;
		}
		String key = workFlow.getKey() + workFlow.getVersion();
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key).latestVersion().singleResult();
		if(pd == null) {
			return null;
		}
		
		String defineId = pd.getId();
		// 获取流程流转情况
		List<ActivityImpl> collections = SimpleCache.FLOW_MAP.get(defineId);
		if(collections == null) {
			ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(defineId);
			collections = processDefinition.getActivities();
			SimpleCache.FLOW_MAP.put(defineId, collections);
		}
		
		if(collections != null) {
			String exclusive = "exclusivegateway" + segment.split("usertask")[1];
			for(ActivityImpl ai : collections) {
				String aiId = ai.getId();
				if(aiId.equals(exclusive)) {
					List<PvmTransition> ptList = ai.getOutgoingTransitions();
					for (PvmTransition pt : ptList) {
						String uc = StrUtils.tranString(pt.getProperty("conditionText"));
						if(uc.contains(option)) {
							if(pt.getDestination() != null) {
								return pt.getDestination().getId();
							}
							return null;
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public void sendApplierEmail(String step, String workflowId, User user, String attachment, String key,String mailContent) {
		// 查询是否需要发送邮件通知
		String mailId = key + step;
		MailTemplates mailEntity = this.mailTemplatesDao.findById(MailTemplates.class, mailId);
		if(null != mailEntity) {
			StringBuffer mailto = new StringBuffer();
			if(null == user) {
				Map<String, Object> queryMap = new HashMap<String, Object>();	// 查询条件
				queryMap.put("WORKFLOWID", workflowId);
				queryMap.put("USERID", this.getLoginUser());
				queryMap.put("SEGMENT", step);
				List<Map<String, Object>> list = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_MASTER_APPROVER, queryMap);
				if(StrUtils.checkCollection(list)) {
					for (Map<String, Object> approverMap : list) {
						String email = StrUtils.tranString(approverMap.get("email"));
						String emailUserId = StrUtils.tranString(approverMap.get("userid"));
						String emailUserName = StrUtils.tranString(approverMap.get("username"));
						if(checkEmail(emailUserId, emailUserName, email)) {
							mailto.append(email + ";");
						}
					}
					if(mailto.length() > 0 && mailto.indexOf(";") > -1) {
						// 移除最后的符号
						mailto.deleteCharAt(mailto.length() - 1);
					}
				}
			} else {
				if(checkEmail(user.getId(), user.getUserName(), user.getEmail())) {
					mailto.append(user.getEmail());
				}
			}
			if(mailto.length() > 0) {
				String content=StrUtils.checkParam(mailContent)?mailContent:mailEntity.getContent();
				SendMailThread sendMailThread = SendMailThread.create();
				sendMailThread.setAttachment(attachment).setContent(content).setEmails(mailto.toString()).setTitle(mailEntity.getDesc());
				CacheSingleton.getInstance().startThread(sendMailThread);
			}
		}	
	}

	private boolean checkEmail(String userId, String userName, String email) {
		if(StrUtils.checkParam(email) && PatternValidator.emailsCheck(email)) {
			return true;
		}
		this.warn("User [" + userId + "][" + userName + "] email[" + email + "] error");
		return false;
	}
}
