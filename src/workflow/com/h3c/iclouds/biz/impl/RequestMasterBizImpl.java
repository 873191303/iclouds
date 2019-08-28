package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.DepartmentBiz;
import com.h3c.iclouds.biz.PrdClassBiz;
import com.h3c.iclouds.biz.RequestItemsBiz;
import com.h3c.iclouds.biz.RequestMasterBiz;
import com.h3c.iclouds.biz.TaskFlowBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.ContactDao;
import com.h3c.iclouds.dao.CustomDao;
import com.h3c.iclouds.dao.Request2ApproveLogDao;
import com.h3c.iclouds.dao.RequestMasterDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.Department;
import com.h3c.iclouds.po.MailTemplates;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.business.Contact;
import com.h3c.iclouds.po.business.Custom;
import com.h3c.iclouds.po.business.PrdClass;
import com.h3c.iclouds.po.business.Request2ApproveLog;
import com.h3c.iclouds.po.business.RequestItems;
import com.h3c.iclouds.po.business.RequestMaster;
import com.h3c.iclouds.thread.SendMailThread;
import com.h3c.iclouds.utils.DateUtils;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.UploadFileUtils;
import com.h3c.iclouds.validate.PatternValidator;
import com.h3c.iclouds.validate.ValidatorUtils;
import org.activiti.engine.task.Task;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("requestMasterBiz")
@SuppressWarnings("unchecked")
public class RequestMasterBizImpl extends BaseBizImpl<RequestMaster> implements RequestMasterBiz {

	@Resource
	private RequestMasterDao requestMasterDao;
	
	@Resource
	private RequestItemsBiz requestItemsBiz;

	@Resource
	private TaskFlowBiz taskFlowBiz;
	
	@Resource
	private PrdClassBiz prdClassBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<MailTemplates> mailTemplatesDao;
	
	@Resource
	private UserBiz userBiz;
	
	@Resource
	private DepartmentBiz departmentBiz;
	
	@Resource
	private Request2ApproveLogDao master2ApproveLogDao;
	
	@Resource
	private CustomDao customDao;
	
	@Resource
	private ContactDao contactDao;
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;
	
	@Override
	public PageModel<RequestMaster> findForPage(PageEntity entity) {
		return requestMasterDao.findForPage(entity);
	}
	
	@Override
	public PageModel<Map<String, Object>> findCompleteForPage(PageEntity entity, boolean needHistory) {
		return this.requestMasterDao.findCompleteForPage(entity, needHistory);
	}
	
	@Override
	public PageModel<RequestMaster> findForPageByApprover(PageEntity entity) {
		return this.requestMasterDao.findForPageByApprover(entity);
	}
	
	@Override
	public ResultType updateChange(RequestMaster entity, Map<String, Object> saveMap) throws Exception {
		this.info("Update change request apply, old entity: " + StrUtils.toJSONString(entity) + "\t----\tsaveMap:" + StrUtils.toJSONString(saveMap));
		RequestMaster oldEntity = this.findById(RequestMaster.class, entity.getSrcReqId());
		List<Map<String, Object>> itemList = (List<Map<String, Object>>) saveMap.get("items");
		if(StrUtils.checkCollection(itemList)) {
			Set<RequestItems> itemSet = entity.getItems();
			Set<RequestItems> items = this.items(itemList, entity);
			if(StrUtils.checkCollection(items)) {
				// 监控项验证通过
				if(requestItemsBiz.check(items, entity, oldEntity) == ResultType.success) {
					entity.updatedUser(this.getSessionBean().getUserId());
					entity.setItems(items);
					Map<String, Object> masterMap = (Map<String, Object>) saveMap.get("master");
					String step = ConfigProperty.MASTER_STEP_SUBMIT;
					// 验证是否是需要进入审批流程
					if(StrUtils.checkParam(masterMap) && masterMap.containsKey("step")) {
						if(ConfigProperty.MASTER_STEP_APPROVE.equals(StrUtils.tranString(masterMap.get("step")))) {
							step = ConfigProperty.MASTER_STEP_APPROVE;
						}
					}
					
					entity.setStep(ConfigProperty.MASTER_STEP_SUBMIT);	// 先设置为草稿保存
					entity.setStatus(null);
					
					if(StrUtils.checkCollection(itemSet)) {
						for (RequestItems item : itemSet) {
							this.requestItemsBiz.delete(item);
						}
					}
					
					this.update(entity);
					this.flush();	// 防止插入业务数据异常导致无用流程实例的创建
					return startWorkFlow(step, entity, saveMap);	// 开始流程判断
				}
				return ResultType.item_parameter_error;
			}
			return ResultType.item_parameter_error;
		}
		return null;
	}
	
	@Override
	public ResultType saveChange(String reqId, Map<String, Object> saveMap) throws Exception {
		this.info("Save change request apply, reqId: " + reqId + "\t----\tsaveMap:" + StrUtils.toJSONString(saveMap));
		RequestMaster oldEntity = this.findById(RequestMaster.class, reqId);
		
		// 原申请单是否属于当前用户
		if(oldEntity == null || !oldEntity.getCreatedBy().equals(this.getSessionBean().getUserId())) {
			return ResultType.old_request_not_exist;
		}
		
		// 原申请单必须未发生变更的
		if(ConfigProperty.MASTER_CHGFLAG1_YES.equals(oldEntity.getChgFlag())) {
			return ResultType.old_request_already_change;
		}
		
		// 原申请单必须是已完结状态
		if(!ConfigProperty.MASTER_STEP8_CLOSE.equals(oldEntity.getStep())) {
			return ResultType.old_request_not_close;
		}
		
		RequestMaster entity = new RequestMaster();
		InvokeSetForm.copyFormProperties(oldEntity, entity);
		entity.setChgFlag(ConfigProperty.MASTER_CHGFLAG2_NO);	// 设置为未发生变更
		entity.setStep(ConfigProperty.MASTER_STEP_SUBMIT);
		entity.setStatus(null);
		entity.createdUser(this.getSessionBean().getUserId());
		entity.setSrcReqId(reqId);
		List<Map<String, Object>> itemList = (List<Map<String, Object>>) saveMap.get("items");
		if(StrUtils.checkCollection(itemList)) {
			Set<RequestItems> items = this.items(itemList, entity);
			if(StrUtils.checkCollection(items)) {
				// 监控项验证通过
				if(requestItemsBiz.check(items, entity, oldEntity) == ResultType.success) {
					entity.createdUser(this.getSessionBean().getUserId());
					entity.setItems(items);
					String step = ConfigProperty.MASTER_STEP_SUBMIT;
					// 验证是否是需要进入审批流程
					Map<String, Object> masterMap = (Map<String, Object>) saveMap.get("master");
					if(StrUtils.checkParam(masterMap) && masterMap.containsKey("step")) {
						if(ConfigProperty.MASTER_STEP_APPROVE.equals(StrUtils.tranString(masterMap.get("step")))) {
							step = ConfigProperty.MASTER_STEP_APPROVE;
						}
					}
					entity.setStatus(null);
					String reqCode = this.requestMasterDao.findLastReqCode();
					entity.setReqCode(reqCode);
					this.warn("Change request apply [" + oldEntity.getReqCode() + "], new reqCode:" + reqCode);
					this.add(entity);
					
					// 更新原申请单为已变更
					oldEntity.setChgFlag(ConfigProperty.MASTER_CHGFLAG1_YES);
					this.update(oldEntity);
					this.flush();	// 防止插入业务数据异常导致无用流程实例的创建
					return startWorkFlow(step, entity, saveMap);		// 开始流程判断
				}
				return ResultType.item_parameter_error;
			}
			return ResultType.item_parameter_error;
		}
		return ResultType.empty_item;
	}
	
	@Override
	public ResultType save(Map<String, Object> saveMap, Map<String, String> validateMap) throws Exception {
		this.info("Save request apply, value: " + StrUtils.toJSONString(saveMap));
		Map<String, Object> masterMap = (Map<String, Object>) saveMap.get("master");
		if(masterMap != null) {
			RequestMaster master = new RequestMaster();
			BeanUtils.populate(master, masterMap);
			ValidatorUtils.validator(master, validateMap);
			if(validateMap.isEmpty()) {
				if(ConfigProperty.YES.equals(master.getIsSign())) {
					if(StrUtils.checkParam(master.getAmount(), master.getContract())) {
						if(master.getAmount() <= 0) {
							return ResultType.parameter_error;
						}
					}
				}
				
				if(master.getCusId() != null) {
					Custom custom = this.customDao.findById(Custom.class, master.getCusId());
					if(custom == null || !custom.getOwner().equals(this.getLoginUser())) {
						return ResultType.custom_error;
					}
					
					Contact contact = contactDao.findById(Contact.class, master.getContact());
					if(contact == null || !contact.getCusId().equals(custom.getId())) {
						return ResultType.contact_error;
					}
				} else {
					master.setContact(null);
					master.setCusId(null);
				}
				
				
				master.setSrcReqId(null);
				List<Map<String, Object>> itemList = (List<Map<String, Object>>) saveMap.get("items");
				if(itemList != null && !itemList.isEmpty()) {
					//items
					Set<RequestItems> items = this.items(itemList, master);
					if(StrUtils.checkCollection(items)) {
						// 监控项验证通过
						boolean isok = (requestItemsBiz.check(items, master, null) == ResultType.success); 						 
						if(isok) {
							master.createdUser(this.getSessionBean().getUserId());
							master.setItems(items);
							String step = master.getStep();
							master.setStep(ConfigProperty.MASTER_STEP_SUBMIT);
							master.setStatus(null);
							
							String reqCode = this.requestMasterDao.findLastReqCode();
							master.setReqCode(reqCode);
							this.add(master);
							this.flush();	// 防止插入业务数据异常导致无用流程实例的创建
							return startWorkFlow(step, master, saveMap);		// 开始流程判断
						}
						return ResultType.item_parameter_error;
					}
					return ResultType.parameter_error;
				}
				return ResultType.empty_item;
			}
		}
		return ResultType.parameter_error;
	}

	@Override
	public ResultType update(String id, Map<String, Object> map, Map<String, String> validateMap) throws Exception {
		this.info("Upadte request apply, reqId: " + id + "\t----\tsaveMap:" + StrUtils.toJSONString(map));
		RequestMaster master = this.findById(RequestMaster.class, id);
		if(master == null) {
			return ResultType.deleted;
		}
		// 只有待提交状态才能进行修改
		if(!master.getStep().equals(ConfigProperty.MASTER_STEP_SUBMIT)) {
			return ResultType.bus_started;
		}
		if(StrUtils.checkParam(master.getSrcReqId())) {	// 是否为变更申请单
			return this.updateChange(master, map);
		}
		
		Map<String, Object> masterMap = (Map<String, Object>) map.get("master");
		RequestMaster pageEntity = new RequestMaster();
		BeanUtils.populate(pageEntity, masterMap);
		ValidatorUtils.validator(pageEntity, validateMap);
		if(validateMap.isEmpty()) {
			if(master.getCusId() != null) {
				Custom custom = this.customDao.findById(Custom.class, pageEntity.getCusId());
				if(custom == null || !custom.getOwner().equals(this.getLoginUser())) {
					return ResultType.custom_error;
				}
				
				Contact contact = contactDao.findById(Contact.class, pageEntity.getContact());
				if(contact == null || !contact.getCusId().equals(custom.getId())) {
					return ResultType.contact_error;
				}
			} else {
				pageEntity.setContact(null);
				pageEntity.setCusId(null);
			}
			
			List<Map<String, Object>> itemList = (List<Map<String, Object>>) map.get("items");
			if(itemList != null && !itemList.isEmpty()) {
				if(master.getCreatedBy().equals(this.getSessionBean().getUserId())) {
					// 页面内容覆盖
					InvokeSetForm.copyFormProperties(pageEntity, master);
					Set<RequestItems> itemSet = master.getItems();
					Set<RequestItems> items = this.items(itemList, master);
					if(items != null) {
						// 监控项验证通过
						if(requestItemsBiz.check(items, master, null) == ResultType.success) {
							master.updatedUser(this.getSessionBean().getUserId());
							master.setStep(ConfigProperty.MASTER_STEP_SUBMIT);
							master.setItems(items);
							this.update(master);
							
							if(itemSet != null && !itemSet.isEmpty()) {
								for (RequestItems item : itemSet) {
									this.requestItemsBiz.delete(item);
								}
							}
							this.flush();	// 防止插入业务数据异常导致无用流程实例的创建
							return startWorkFlow(pageEntity.getStep(), master, map);	// 开始流程判断
						}
					}
					return ResultType.parameter_error;
				}
				return ResultType.unAuthorized;
			}
			return ResultType.empty_item;
		}
		return ResultType.parameter_error;
	}
	
	private ResultType startWorkFlow(String step, RequestMaster master, Map<String, Object> map) throws Exception {
		// 提交的表单
		if(step.equals(ConfigProperty.MASTER_STEP_APPROVE)) {
			User user = userBiz.findById(User.class, this.getSessionBean().getUserId());
			if(user.getDeptId() != null && !"".equals(user.getDeptId())) {
				Map<String, Object> flowMap = new HashMap<String, Object>();
				// 设置下一个审批人
				flowMap.put(ConfigProperty.REQUEST_APPROVER, map.get(ConfigProperty.REQUEST_APPROVER));
				ResultType resultType = this.start(flowMap, master, user);
				if(resultType != ResultType.success) {	// 流程启动失败
					throw new RuntimeException();
				}
				return resultType;
			}
			return ResultType.bus_start_applier_department;
		}
		return ResultType.success;
	}
	
	private Set<RequestItems> items(List<Map<String, Object>> itemList, RequestMaster master) throws IllegalAccessException, InvocationTargetException {
		Set<RequestItems> items = new HashSet<RequestItems>();
		for (int i = 0; i < itemList.size(); i++) {
			RequestItems itemEntity = new RequestItems();
			BeanUtils.populate(itemEntity, itemList.get(i));
			if(master.getReqtype() == 1) {
				Map<String, String> validatorMap = ValidatorUtils.validator(itemEntity);
				if(!validatorMap.isEmpty()) {
					return null;
				}
			}
			if(!StrUtils.checkParam(master.getSrcReqId())) {	// 不存在原申请单
				itemEntity.setOitemId(null);	// 设置为空
			} else {
				String reqType = itemEntity.getReqType();
				if(ConfigProperty.ITEM_REQTYPE1_INSERT.equals(reqType)) {
					itemEntity.setOitemId(null);	// 新增的内容移除原申请单id
				}
			}
			itemEntity.setId(null);
			itemEntity.setMaster(master);
			items.add(itemEntity);
		}
		return items;
	}
	
	@Override
	public ResultType start(Map<String, Object> alias, RequestMaster entity, User user) throws Exception {
		this.info("Request apply " + entity.getId() + " start workflow.");
		String createdBy = entity.getCreatedBy();
		String applyId = entity.getId();
		String workFlowId = entity.getWorkFlowId();
		ResultType resultType = taskFlowBiz.start(workFlowId, alias, applyId, createdBy);
		if(resultType == ResultType.success) {
			String instanceId = StrUtils.tranString(alias.get("instanceId"));
			entity.setInstanceId(instanceId);
			entity.setStatus(ConfigProperty.MASTER_STATUS1_CONFIRM);	// 开始审批状态
			entity.setStep(ConfigProperty.MASTER_STEP_APPROVE);
			entity.setResponsible(null);	// 当前处理人设置为空
			this.update(entity);
			
			// 开始申请单自动通过
			Map<String, Object> firstAutoCompleteMap = new HashMap<String, Object>();
			Department department = departmentBiz.findById(Department.class, user.getDeptId());
			firstAutoCompleteMap.put(ConfigProperty.REQUEST_APPROVER, alias.get(ConfigProperty.REQUEST_APPROVER));
			firstAutoCompleteMap.put(ConfigProperty.REQUEST_COMMENT, "提交业务办理申请单。");
			if(department.getDepth() > 2) {	// 处于3级部门
				firstAutoCompleteMap.put(ConfigProperty.REQUEST_PARAMETER, ConfigProperty.REQUEST_STARTFLOW_LOWER2);
				firstAutoCompleteMap.put("startFlag", ConfigProperty.MASTER_STEP2_DEPARTMENT_APPROVE);
			} else {
				firstAutoCompleteMap.put(ConfigProperty.REQUEST_PARAMETER, ConfigProperty.REQUEST_STARTFLOW_UPPER2);
				firstAutoCompleteMap.put("startFlag", ConfigProperty.MASTER_STEP3_SIGN_APPROVE);
			}
			this.complete(firstAutoCompleteMap, entity);
		}
		return resultType;
	}
	
	@Override
	public ResultType complete(Map<String, Object> alias, RequestMaster entity) {
		this.info("Complete request apply, value: [alias:" + JSONObject.toJSONString(alias) + "][" + StrUtils.toJSONString(entity) + "]");
		String createdBy = entity.getCreatedBy();
		String instanceId = entity.getInstanceId();
		String workFlowId = entity.getWorkFlowId();
		String userId = this.getSessionBean().getUserId();	// 审批人为当前用户
		
		String comment = StrUtils.tranString(alias.get(ConfigProperty.REQUEST_COMMENT));
		String requestParameter = StrUtils.tranString(alias.get(ConfigProperty.REQUEST_PARAMETER));
		// 提交的参数必须为reject或者approve
		if(!requestParameter.equals(ConfigProperty.REQUEST_PARAMETER_REJECT) && !requestParameter.equals(ConfigProperty.REQUEST_PARAMETER_APPROVE)) {
			return ResultType.parameter_error;
		}
		// 处理意见如果为空
		if(comment == null || "".equals(comment)) {
			alias.put(comment, "没有处理意见。");
		}
		
		String approver = StrUtils.tranString(alias.get(ConfigProperty.REQUEST_APPROVER));
		String segment = null;	// 当前审批环节
		if(alias.containsKey("startFlag")) {	// 开始标志
			segment = StrUtils.tranString(alias.get("startFlag"));
		} else {
			Task task = taskFlowBiz.getTask(entity.getInstanceId());
			if(task != null) {
				segment = task.getTaskDefinitionKey();
				alias.put("taskId", task.getId());	// 不存在task
			} else {
				return ResultType.bus_end;
			}
			
			boolean auth = taskFlowBiz.taskAuth(segment, userId, createdBy, workFlowId);
			if(!auth) {	// 处理人与流程不匹配
				return ResultType.task_complete_error;
			}
		}
		// 设置环节
		alias.put("segment", segment);
		
		// 清空原本指定审批人
		entity.setResponsible(null);
		String emails = null;
		String attachment = null;
		// 获取下一个节点，处理节点事件
		String nextSegment = taskFlowBiz.getNextTaskSegment(entity.getWorkFlowId(), segment, requestParameter);
		
		if(nextSegment == null) {
			throw new RuntimeException();
		} else if(ConfigProperty.MASTER_STEP5_HANDLE.equals(nextSegment)) {
			if(segment.equals(ConfigProperty.MASTER_STEP4_CONTROL)) {
				Date reqFTime = DateUtils.getDateByString(StrUtils.tranString(alias.get("reqFTime")), DateUtils.dateFormat);
				// TODO 需要判断提交的云主机uuid情况
				if(ConfigProperty.REQUEST_PARAMETER_APPROVE.equals(requestParameter)) {
					emails = StrUtils.tranString(alias.get("emails"));
					if(StrUtils.checkParam(emails) && !PatternValidator.emailsCheck(emails)) {
						this.info("邮箱格式错误：" + emails);
						return ResultType.mail_error;
					}
				}
				entity.setReqFTime(reqFTime);
				entity.setStatus(ConfigProperty.MASTER_STATUS2_HANDLE);	// 处理中状态
				if(!StrUtils.checkParam(reqFTime)) {	// 可以不指定下一个处理人
					return ResultType.reqFTime_error;
				}
			}
		} else if(ConfigProperty.MASTER_STEP6_TEST.equals(nextSegment)) {	// 下一个环节为测试环节，当前环节为处理环节
			if(approver == null || "".equals(approver)) {	// 可以不指定下一个处理人
//				throw new RuntimeException();
			}
		} else if(ConfigProperty.MASTER_STEP7_VALIDATE.equals(nextSegment)) {
			// 判断流程版本
			if("3".equals(entity.getWorkFlowId())) {	// 第二版流程
				if(ConfigProperty.REQUEST_PARAMETER_APPROVE.equals(requestParameter)) {
					// 附件必须上传
					MultipartFile file = (MultipartFile) alias.get("attachment");
					if(file != null) {
                        File targetFile = UploadFileUtils.uploadAttathmentFile(file);
                        if(targetFile == null) {
                            UploadFileUtils.deleteFile(targetFile);
                            return ResultType.attathment_error;
                        }
                        attachment = targetFile.getName();
					}
				}
			}
			entity.setActFTime(new Date());
			approver = entity.getCreatedBy();	// 测试验证环节之后不需要指定审批人
		} else if(nextSegment.contains(ConfigProperty.WORKFLOW_END)) {
			approver = null;
		}
		
		User user = null;
		if(approver != null && !"".equals(approver)) {	// 存在下一级指派人
			user = userBiz.findById(User.class, approver);
			if(user != null) {	// 指派人不存在
				if(!taskFlowBiz.taskAuth(nextSegment, approver, createdBy, workFlowId)) {
					return ResultType.next_approver_error;
				}
			} else {
				return ResultType.next_approver_not_exist;
			}
		}
		
		// 先检查处理内容是否符合标准
		Request2ApproveLog log = new Request2ApproveLog(alias, userId, entity);
		Map<String, String> validatorMap = ValidatorUtils.validator(log);
		if(!validatorMap.isEmpty()) {
			throw new MessageException(JSONObject.toJSONString(validatorMap));
		}
		
		ResultType resultType = taskFlowBiz.complete(workFlowId, instanceId, alias, createdBy, userId);
		if(resultType == ResultType.success) {
			log = new Request2ApproveLog(alias, userId, entity);
			log.setEmails(emails);
			log.setAttachment(attachment);
			this.master2ApproveLogDao.add(log);
			if(approver != null && !"".equals(approver)) {
				ResultType claimResult = taskFlowBiz.claim(workFlowId, instanceId, approver, createdBy);
				if(claimResult == ResultType.success) {
					entity.setResponsible(approver);	// 设置处理人
				}
			}
			this.setCurrentStep(entity, requestParameter);
			this.update(entity);
			StringBuffer mailContent = new StringBuffer();
			String title = "行业云系统提醒：请处理流转的申请单";
			mailContent = setMailContent(mailContent, entity, userId, entity.getStep());
			this.info("======mailContent:" + mailContent.toString());
			if(mailContent.length() > 0) {
				if(StrUtils.checkParam(emails)) {
					SendMailThread sendMailThread = SendMailThread.create();
					sendMailThread.setAttachment(attachment).setContent(mailContent.toString()).setEmails(emails).setTitle(title);
					CacheSingleton.getInstance().startThread(sendMailThread);
				}
			}
			try {
				this.taskFlowBiz.sendApplierEmail(entity.getStep(), entity.getWorkFlowId(), user, attachment, ConfigProperty.REQUEST_MASTER_MAIL_KEY,mailContent.toString());
			} catch (Exception e) {
				this.exception(e, "Send mail to handler failure. request apply :[" + entity.getId() + "][" + entity.getReqCode() + "]");
			}
		}
		return resultType;
	}
	
	/**
	 * 流程后续处理
	 * @param entity
	 * @param requestParameter
	 */
	public void setCurrentStep(RequestMaster entity, String requestParameter) {
		Task task = taskFlowBiz.getTask(entity.getInstanceId());	// 获取流程中的任务
		if(task != null) {
			// 设置下一个环节，对应返回可以在iyun_flow_workrole表中获取对应翻译
			String segment = task.getTaskDefinitionKey();
			entity.setStep(segment);
		} else {	// 任务不存在
			// 上一个任务为同意且任务不存在：流程已经结束
			if(requestParameter.equals(ConfigProperty.REQUEST_PARAMETER_APPROVE)) {
				entity.setStep(ConfigProperty.MASTER_STEP8_CLOSE);
				entity.setStatus(ConfigProperty.MASTER_STATUS3_CLOSED);
			} else {	// 任务被驳回，回到申请人手中重新填写发起申请
				entity.setStep(ConfigProperty.MASTER_STEP_SUBMIT);
				entity.setActFTime(null);
				entity.setReqFTime(null);
				entity.setInstanceId(null);
				entity.setStatus(ConfigProperty.MASTER_STATUS4_REJECT);
			}
		}
	}
	
	@Override
	public void delete(RequestMaster entity) {
		List<Request2ApproveLog> list = master2ApproveLogDao.findByPropertyName(Request2ApproveLog.class, "reqId", entity.getId());
		if(StrUtils.checkCollection(list)) {
			this.master2ApproveLogDao.delete(list);
		}
		
		// 修改原申请单为未变更状态
		if(StrUtils.checkParam(entity.getSrcReqId())) {
			RequestMaster oldEntity = this.findById(RequestMaster.class, entity.getSrcReqId());	
			if(oldEntity != null) {
				oldEntity.setChgFlag(ConfigProperty.MASTER_CHGFLAG2_NO);
				this.warn("Reply request apply, reqCode:" + oldEntity.getReqCode());
				this.update(oldEntity);
			}
		}
		this.warn("Delete request apply, reqCode:" + entity.getReqCode());
		super.delete(entity);
	}
	
	@Override
	public Map<String, Object> queryUserApplyCount() {
		String queryName = SqlQueryProperty.QUERY_REQUEST_MASTER_APPROVER_HANDLED_COUNT;
		String countSql = this.requestMasterDao.getSession().getNamedQuery(queryName).getQueryString();
		// 替换为当前用户
		countSql = countSql.replaceAll(":USERID", "'" + this.getSessionBean().getUserId() + "'");
		List<Map<String, Object>> countList = sqlQueryBiz.queryBySql(countSql);
		int alreadyCount = StrUtils.tranInteger(countList.get(0).get("ct"));
		
		StringBuffer applyCount = new StringBuffer(countSql);
		applyCount.append(" AND l.step <> 'usertask7'");
		countList = sqlQueryBiz.queryBySql(applyCount.toString());
		int apply = StrUtils.tranInteger(countList.get(0).get("ct"));
		
		String sqlCountName = SqlQueryProperty.QUERY_REQUEST_MASTER_APPROVE_AND_HISTORY_COUNT;
		countSql = this.requestMasterDao.getSession().getNamedQuery(sqlCountName).getQueryString();
		StringBuffer like = new StringBuffer();
		like.append(" WHERE REQUEST.createddate > (now() - interval '30 day') AND REQUEST.allowed <> '0' ");
		// 替换为当前用户
		countSql = countSql.replaceAll(":USERID", "'" + this.getSessionBean().getUserId() + "'");
		like.insert(0, countSql);
		countList = sqlQueryBiz.queryBySql(like.toString());
		
		int needHandleCount = StrUtils.tranInteger(countList.get(0).get("count"));
		Map<String, Object> map = StrUtils.createMap();
		map.put("apply", apply);
		map.put("already", alreadyCount);
		map.put("need", needHandleCount);
		return map;
	}


	public StringBuffer setMailContent(StringBuffer mailContent,RequestMaster entity,String userId,String nextSegment) {
		mailContent.append("需求办理信息:<br>")
		.append("<table border='1' style='border:1px solid black;text-align:center;'>").
		append("<tr style='background:#F2F2F2;height:30px;'><td>序号</td><td>项目名称</td><td>申请类型</td><td>流程编号</td>"
				+ "<td>申请时间</td><td>办理状态</td><td>申请人</td><td>链接</td></tr>");
		Set<RequestItems> items = entity.getItems();
		StringBuffer name=new StringBuffer();
		if(StrUtils.checkParam(items)){
			for(RequestItems item:items){
				PrdClass prdClass=prdClassBiz.findById(PrdClass.class, item.getClassId());
				if(StrUtils.checkParam(prdClass)){
					name.append(prdClass.getClassName()+",");
				}
			}
			String applyType="";
			if(StrUtils.checkParam(name.toString())){
				applyType=name.toString().substring(0, name.length()-1);
			}
			String step=ConfigProperty.MASTER_STEP1_SUBMIT_APPROVE.equals(nextSegment)?"待提交":
				ConfigProperty.MASTER_STEP2_DEPARTMENT_APPROVE.equals(nextSegment)?"待区域经理审批":
					ConfigProperty.MASTER_STEP3_SIGN_APPROVE.equals(nextSegment)?"待权签审批":
						ConfigProperty.MASTER_STEP4_CONTROL.equals(nextSegment)?"待调度":
							ConfigProperty.MASTER_STEP5_HANDLE.equals(nextSegment)?"待处理":
								ConfigProperty.MASTER_STEP6_TEST.equals(nextSegment)?"待测试":
									ConfigProperty.MASTER_STEP7_VALIDATE.equals(nextSegment)?"待客户验证":
										ConfigProperty.MASTER_STEP8_CLOSE.equals(nextSegment)?"关闭":"未知";
			String userName="";
			User user=userBiz.findById(User.class, userId);
			if(StrUtils.checkParam(user)){
				userName=user.getUserName();
			}

			StringBuffer sBuffer = new StringBuffer(singleton.getConfigValue("iyun.domain"));
			if(ConfigProperty.MASTER_STEP2_DEPARTMENT_APPROVE.equals(nextSegment) || ConfigProperty.MASTER_STEP3_SIGN_APPROVE.equals(nextSegment)){
				sBuffer.append(CacheSingleton.getInstance().getConfigValue("requestMaster_link_apply"));
			}else if(ConfigProperty.MASTER_STEP4_CONTROL.equals(nextSegment) || ConfigProperty.MASTER_STEP5_HANDLE.equals(nextSegment)){
				sBuffer.append(CacheSingleton.getInstance().getConfigValue("requestMaster_link_dispatch"));
			}else if(ConfigProperty.MASTER_STEP7_VALIDATE.equals(nextSegment)){
				sBuffer.append(CacheSingleton.getInstance().getConfigValue("requestMaster_link_confirm"));
			}
			
			mailContent.append("<tr style='height:30px;'><td>1</td><td>"+entity.getProjectName()+"</td><td>"+applyType+"</td><td>"+entity.getReqCode()+"</td>"
					+ "<td>"+DateUtils.getDate(entity.getCreatedDate(), DateUtils.dateFormat)+"</td><td>"+step+"</td>"
					+ "<td>"+userName+"</td><td><a href='"+sBuffer.toString()+"'>进入</a></td></tr>");
		}
		return mailContent;
	}
	public int count (String type) {
		return requestMasterDao.count(type);
	}
}
