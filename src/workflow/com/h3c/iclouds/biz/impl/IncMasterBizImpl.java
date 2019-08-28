package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.IncMasterBiz;
import com.h3c.iclouds.biz.PrdClassBiz;
import com.h3c.iclouds.biz.TaskFlowBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SimpleCache;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.common.UploadFileModal;
import com.h3c.iclouds.dao.Inc2ApproveLogDao;
import com.h3c.iclouds.dao.IncMasterDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.MailTemplates;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.inc.Inc2ApproveLog;
import com.h3c.iclouds.po.inc.IncMaster;
import com.h3c.iclouds.utils.DateUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.UploadFileUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import org.activiti.engine.task.Task;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("incMasterBiz")
public class IncMasterBizImpl extends BaseBizImpl<IncMaster> implements IncMasterBiz {

	@Resource
	private IncMasterDao incMasterDao;
	
	@Resource
	private TaskFlowBiz taskFlowBiz;
	
	@Resource
	private UserBiz userBiz;
	
	@Resource
	private PrdClassBiz prdClassBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<MailTemplates> mailTemplatesDao;
	
	@Resource
	private Inc2ApproveLogDao inc2ApproveLogDao;
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;
	
	@Override
	public PageModel<IncMaster> findForPage(PageEntity entity) {
		return incMasterDao.findForPage(entity);
	}
	
	@Override
	public PageModel<Map<String, Object>> findCompleteForPage(PageEntity entity) {
		return this.incMasterDao.findCompleteForPage(entity);
	}
	
	@Override
	public PageModel<IncMaster> findForPageByApprover(PageEntity entity) {
		return this.incMasterDao.findForPageByApprover(entity);
	}
	
	@Override
	public ResultType save(Map<String, Object> saveMap, Map<String, String> validateMap) throws Exception {
		IncMaster master = new IncMaster();
		BeanUtils.populate(master, saveMap);
		ValidatorUtils.validator(master, validateMap);
		if(validateMap.isEmpty()) {
			master.createdUser(this.getSessionBean().getUserId());
			master.setStep(ConfigProperty.INC_STEP_SUBMIT);
			String incCode = this.incMasterDao.findLastIncNo();
			master.setIncNo(incCode);
			this.add(master);
			return startWorkFlow(master, saveMap);		// 开始流程判断
		}	
		return ResultType.parameter_error;
	}

	private ResultType startWorkFlow(IncMaster master, Map<String, Object> map) throws Exception {
		// 提交的表单
		Map<String, Object> flowMap = new HashMap<String, Object>();
		// 设置下一个审批人
		flowMap.put(ConfigProperty.REQUEST_APPROVER, map.get(ConfigProperty.REQUEST_APPROVER));
		// 设置下一个审批人
		flowMap.put(ConfigProperty.FILE_KEY, map.get(ConfigProperty.FILE_KEY));
		ResultType resultType = this.start(flowMap, master);
		if(resultType != ResultType.success) {	// 流程启动失败
			throw new RuntimeException();
		}
		return resultType;
	}
	
	@Override
	public ResultType start(Map<String, Object> alias, IncMaster entity) throws Exception {
		this.info("Inc apply " + entity.getId() + " start workflow.");
		String createdBy = entity.getCreatedBy();
		String applyId = entity.getId();
		String workFlowId = entity.getWorkFlowId();
		ResultType resultType = taskFlowBiz.start(workFlowId, alias, applyId, createdBy);
		if(resultType == ResultType.success) {
			String instanceId = StrUtils.tranString(alias.get("instanceId"));
			entity.setInstanceId(instanceId);
			entity.setStep(ConfigProperty.INC_STEP1_SUBMIT_APPROVE);
			entity.setResponsible(createdBy);	// 当前处理人设置为空
			this.update(entity);
			
			// 开始申请单自动通过
			Map<String, Object> firstAutoCompleteMap = new HashMap<String, Object>();
			// 设置附件key,用于匹配上传的附件
			firstAutoCompleteMap.put(ConfigProperty.FILE_KEY, alias.get(ConfigProperty.FILE_KEY));
			firstAutoCompleteMap.put(ConfigProperty.REQUEST_APPROVER, alias.get(ConfigProperty.REQUEST_APPROVER));
			firstAutoCompleteMap.put(ConfigProperty.REQUEST_COMMENT, entity.getContent());
			firstAutoCompleteMap.put(ConfigProperty.REQUEST_PARAMETER, ConfigProperty.REQUEST_PARAMETER_APPROVE);
			this.complete(firstAutoCompleteMap, entity);
		}
		return resultType;
	}
	
	@Override
	public ResultType complete(Map<String, Object> alias, IncMaster entity) {
		this.info("Complete inc apply, value: [alias:" + StrUtils.toJSONString(alias) + "][" + StrUtils.toJSONString(entity) + "]");
		String createdBy = entity.getCreatedBy();
		String instanceId = entity.getInstanceId();
		String workFlowId = entity.getWorkFlowId();
		String userId = this.getSessionBean().getUserId();	// 审批人为当前用户
		String incType = StrUtils.tranString(alias.get("incType"));
		if(StrUtils.checkParam(incType)) {
			entity.setIncType(incType);
			this.update(entity);
		}
		
		String comment = StrUtils.tranString(alias.get(ConfigProperty.REQUEST_COMMENT));
		String requestParameter = StrUtils.tranString(alias.get(ConfigProperty.REQUEST_PARAMETER));
		String fileKey = StrUtils.tranString(alias.get(ConfigProperty.FILE_KEY));
		// 提交的参数必须为reject或者approve
		if(!requestParameter.equals(ConfigProperty.REQUEST_PARAMETER_REJECT) && !requestParameter.equals(ConfigProperty.REQUEST_PARAMETER_APPROVE)) {
			return ResultType.parameter_error;
		}
		if(!StrUtils.checkParam(comment)) {
			return ResultType.parameter_error;
		}
		
		String approver = StrUtils.tranString(alias.get(ConfigProperty.REQUEST_APPROVER));
		String segment = null;	// 当前审批环节
		Task task = taskFlowBiz.getTask(entity.getInstanceId());
		if(task != null) {
			segment = task.getTaskDefinitionKey();
			alias.put("segment", segment);	// 设置为申请人环节
			alias.put("taskId", task.getId());	// 不存在task
		} else {
			return ResultType.bus_end;
		}
		
		UploadFileModal fileModal = null;
		if(StrUtils.checkParam(fileKey)) {
			fileModal = SimpleCache.UPLOAD_FILE_MAP.get(fileKey);
		}
		Inc2ApproveLog log = new Inc2ApproveLog(alias, userId, entity, fileModal);
		Map<String, String> validatorMap = ValidatorUtils.validator(log);
		if(!validatorMap.isEmpty()) {
			UploadFileUtils.deleteFile(fileKey, fileModal);
			throw new MessageException(JSONObject.toJSONString(validatorMap));
		}
		
		// 当前环节不是申请人环节，申请人可以提交处理意见,不做流程环节上的处理
		if(entity.getCreatedBy().equals(userId) && !ConfigProperty.INC_STEP5_CLOSE.equals(segment) 
				&& !ConfigProperty.INC_STEP1_SUBMIT_APPROVE.equals(segment)
				&& !ConfigProperty.INC_STEP4_VALIDATE.equals(segment)) {
			this.info("Inc apply " + entity.getId() + " create user add message.");
			alias.put("segment", ConfigProperty.INC_STEP1_SUBMIT_APPROVE);	// 设置为申请人环节
			alias.put("taskId", "-1");	// 不存在task
			alias.put(ConfigProperty.REQUEST_PARAMETER, ConfigProperty.REQUEST_PARAMETER_APPROVE);
			this.inc2ApproveLogDao.add(log);
			return ResultType.success;
		}
		// 清空原本指定审批人
		entity.setResponsible(null);
		
		String nextSegment = ConfigProperty.INC_STEP2_FIRST_LINE;
		if(ConfigProperty.INC_STEP1_SUBMIT_APPROVE.equals(segment)) {
			approver = null;
		} else {
			// 获取下一个节点，处理节点事件
			nextSegment = taskFlowBiz.getNextTaskSegment(entity.getWorkFlowId(), segment, requestParameter);
			if(ConfigProperty.INC_STEP2_FIRST_LINE.equals(nextSegment)) {	// 用户到一线，不能指定处理人
				approver = null;
			} else if(nextSegment.contains(ConfigProperty.WORKFLOW_END)) {
				approver = null;
			}
		}

		User user = null;
		if(StrUtils.checkParam(approver)) {	// 存在下一级指派人
			user = userBiz.findById(User.class, approver);
			if(user != null) {	// 指派人不存在
				if(!taskFlowBiz.taskAuth(nextSegment, approver, createdBy, workFlowId)) {
					throw new RuntimeException();
				}
			} else {
				throw new RuntimeException();
			}
		}

		ResultType resultType = taskFlowBiz.complete(workFlowId, instanceId, alias, createdBy, userId);
		if(resultType == ResultType.success) {
			log.setStep(StrUtils.tranString(alias.get("segment")));
			log.setTaskId(StrUtils.tranString(alias.get("taskId")));
			this.inc2ApproveLogDao.add(log);
			if(approver != null && !"".equals(approver)) {
				ResultType claimResult = taskFlowBiz.claim(workFlowId, instanceId, approver, createdBy);
				if(claimResult == ResultType.success) {
					entity.setResponsible(approver);	// 设置处理人
				}
			}
			this.setCurrentStep(entity, requestParameter);
			this.update(entity);
			
			try {
				StringBuffer mailContent = new StringBuffer();
				setMailContent(mailContent, entity, userId, entity.getStep());
				this.taskFlowBiz.sendApplierEmail(entity.getStep(), entity.getWorkFlowId(), user, null, ConfigProperty.INC_MASTER_MAIL_KEY, mailContent.toString());
			} catch (Exception e) {
				this.exception(e, "Send mail to handler failure. request apply :[" + entity.getId() + "][" + entity.getIncNo() + "]");
			}
		}
		return resultType;
	}
	
	/**
	 * 流程后续处理
	 * @param entity
	 * @param requestParameter
	 */
	public void setCurrentStep(IncMaster entity, String requestParameter) {
		Task task = taskFlowBiz.getTask(entity.getInstanceId());	// 获取流程中的任务
		if(task != null) {
			// 设置下一个环节，对应返回可以在iyun_flow_workrole表中获取对应翻译
			String segment = task.getTaskDefinitionKey();
			entity.setStep(segment);
		} else {	// 任务不存在
			// 上一个任务为同意且任务不存在：流程已经结束
			if(requestParameter.equals(ConfigProperty.REQUEST_PARAMETER_APPROVE)) {
				entity.setStep(ConfigProperty.INC_STEP5_CLOSE);
			} else {	// 任务被驳回，回到申请人手中重新填写发起申请
				entity.setStep(ConfigProperty.INC_STEP_SUBMIT);
				entity.setActFTime(null);
				entity.setReqFTime(null);
				entity.setInstanceId(null);
			}
		}
	}
	
	@Override
	public void delete(IncMaster entity) {
		List<Inc2ApproveLog> list = inc2ApproveLogDao.findByPropertyName(Inc2ApproveLog.class, "reqId", entity.getId());
		if(StrUtils.checkCollection(list)) {
			this.inc2ApproveLogDao.delete(list);
		}
		super.delete(entity);
	}

	@Override
	public Inc2ApproveLog close(Map<String, Object> alias, IncMaster entity) {
		entity.setStep(ConfigProperty.INC_STEP5_CLOSE);
		alias.put("taskId", "-1");
		alias.put(ConfigProperty.REQUEST_PARAMETER, ConfigProperty.REQUEST_PARAMETER_APPROVE);
		alias.put("segment", ConfigProperty.INC_STEP4_VALIDATE);	// 最后一个环节
		String comment = StrUtils.tranString(alias.get(ConfigProperty.REQUEST_COMMENT));
		if(!StrUtils.checkParam(comment)) {
			comment = "关闭了事件工单。";
			alias.put(ConfigProperty.REQUEST_COMMENT, comment);
		}
		Inc2ApproveLog log = new Inc2ApproveLog(alias, this.getLoginUser(), entity, null);
		this.inc2ApproveLogDao.add(log);
		this.update(entity);
		return log;
	}

	@Override
	public Map<String, Object> queryUserApplyCount() {
		String queryName = SqlQueryProperty.QUERY_INC_MASTER_APPROVER_HANDLED_COUNT;
		String countSql = this.incMasterDao.getSession().getNamedQuery(queryName).getQueryString();
		// 替换为当前用户
		countSql = countSql.replaceAll(":USERID", "'" + this.getSessionBean().getUserId() + "'");
		List<Map<String, Object>> countList = sqlQueryBiz.queryBySql(countSql);
		int alreadyCount = StrUtils.tranInteger(countList.get(0).get("ct"));
		
		String sqlCountName = SqlQueryProperty.QUERY_INC_MASTER_APPROVE_AND_HISTORY_COUNT;
		countSql = this.incMasterDao.getSession().getNamedQuery(sqlCountName).getQueryString();
		StringBuffer like = new StringBuffer();
		like.append(" WHERE REQUEST.createddate > (now() - interval '30 day') AND REQUEST.allowed <> '0' ");
		// 替换为当前用户
		countSql = countSql.replaceAll(":USERID", "'" + this.getSessionBean().getUserId() + "'");
		
		like.insert(0, countSql);
		countList = sqlQueryBiz.queryBySql(like.toString());
		
		int needHandleCount = StrUtils.tranInteger(countList.get(0).get("count"));
		Map<String, Object> map = StrUtils.createMap();
		map.put("already", alreadyCount);
		map.put("need", needHandleCount);
		return map;
	}
	
	@Override
	public int count (String type) {
		return incMasterDao.count(type);
	}
	
	public StringBuffer setMailContent(StringBuffer mailContent,IncMaster entity,String userId,String nextSegment) {
		mailContent.append("工单处理信息:<br>")
		.append("<table border='1' style='border:1px solid black;text-align:center;'>").
		append("<tr style='background:#F2F2F2;height:30px;'><td>序号</td><td>工单主题</td><td>工单描述</td>"
				+ "<td>申请时间</td><td>办理状态</td><td>申请人</td><td>链接</td></tr>");
		
		String step=ConfigProperty.INC_STEP1_SUBMIT_APPROVE.equals(nextSegment)?"待提交":
			ConfigProperty.INC_STEP2_FIRST_LINE.equals(nextSegment)?"待一线处理":
				ConfigProperty.INC_STEP3_SECOND_LINE.equals(nextSegment)?"待二线处理":
					ConfigProperty.INC_STEP4_VALIDATE.equals(nextSegment)?"待用户确认":
						ConfigProperty.INC_STEP5_CLOSE.equals(nextSegment)?"关闭":"未知";
		String userName="";
		User user=userBiz.findById(User.class, userId);
		if(StrUtils.checkParam(user)){
			userName=user.getUserName();
		}
		StringBuffer sBuffer = new StringBuffer(singleton.getConfigValue("iyun.domain"));
		sBuffer.append(CacheSingleton.getInstance().getConfigValue("incMaster_link_handle"));
		
		mailContent.append("<tr style='height:30px;'><td>1</td><td>"+entity.getTopic()+"</td><td>"+entity.getContent()+"</td>"
				+ "<td>"+DateUtils.getDate(entity.getCreatedDate(), DateUtils.dateFormat)+"</td><td>"+step+"</td>"
				+ "<td>"+userName+"</td><td><a href='"+sBuffer.toString()+"'>进入</a></td></tr>");
		return mailContent;
	}
}
