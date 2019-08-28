package com.h3c.iclouds.po.business;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.utils.InvokeAnnotate;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@SuppressWarnings("rawtypes")
@ApiModel(value = "业务办理对象")
public class RequestMaster extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "申请单id")
	private String id;

	@Length(max = 50)
	@NotNull
	@ApiModelProperty(value = "申请单编号 （NotNull）", required = true)
	private String reqCode;
	
	/**
	 * 环节
	 *	1、待提交
	 *	2、待区域经理审批
	 *	3、待权签审批
	 *	4、待调度
	 *	5、待处理
	 *	6、待测试
	 *	7、待客户验证
	 *	8、关闭
	 */
//	@CheckPattern(type = PatternType.CONTAINS, values = {"1", "2", "3", "4", "5", "6", "7", "8"})
	@NotNull
	@ApiModelProperty(value = "环节 （NotNull）", required = true)
	private String step;

	@Length(max = 36)
	@ApiModelProperty(value = "当前处理人,不需要传递")
	private String responsible;

	@Length(max = 100)
	@ApiModelProperty(value = "项目名称")
	private String projectName;

	@NotNull
	@CheckPattern(type = PatternType.CONTAINS, values = {"0", "1"})	// 0:是 1:否
	@ApiModelProperty(value = "是否签订合同 (NotNull)(Contain: 0-是; 1-否)", notes = "0:是 1:否, 默认或者不传都为1")
	private String isSign;

	@Length(max = 50)
	@ApiModelProperty(value = "合同编号")
	private String contract;

	@ApiModelProperty(value = "合同金额")
	private Double amount;

	@Length(max = 500)
	@ApiModelProperty(value = "项目简介")
	private String projectDesc;

	@Length(max = 36)
	@ApiModelProperty(value = "客户编号 (NotNull)")
	private String cusId;
	
	@ApiModelProperty(value = "客户名称, 不需要传递")
	private String cusName;

	@Length(max = 50)
	@ApiModelProperty(value = "联系人Id (NotNull)")
	private String contact;
	
	@ApiModelProperty(value = "联系人名称, 不需要传递")
	private String contactName;

	@Length(max = 50)
	@ApiModelProperty(value = "手机")
	private String iphone;

	@Length(max = 50)
	@ApiModelProperty(value = "邮箱")
	private String email;
	
	/**
	 * 状态
	 *  1.待确认，发起审批的时候修改
	 *  2.处理中，权签审批通过后修改
	 *  3.已关闭，用户验证通过后修改
	 *  4.驳回，环节审批处理被驳回后修改
	 */
	@ApiModelProperty(value = "单据状态")
	@CheckPattern(type = PatternType.CONTAINS, values = {"1", "2", "3", "4"})
	private String status;
	
	/**
	 * 已变更标识:
	 * 	1、已变更
	 * 	2、未变更
	 */
	@CheckPattern(type = PatternType.CONTAINS, values = {"1", "2"})
	@ApiModelProperty(value = "已变更标识，不需要传递")
	private String chgFlag = ConfigProperty.MASTER_CHGFLAG2_NO;	// 默认为未变更

	@Length(max = 36)
	@ApiModelProperty(value = "原申请单id")
	private String srcReqId;
	
	@ApiModelProperty(value = "原申请单号")
	private String srcReqCode;
	
	@ApiModelProperty(value = "变更后的申请单号")
	private String tgtReqCode;

	@Length(max = 36)
	@ApiModelProperty(value = "流程实例ID")
	private String instanceId;

	@Length(max = 36)
	@ApiModelProperty(value = "流程镜像id (NotNull)")
	@NotNull
	private String workFlowId;

	private Integer version;
	
	@ApiModelProperty(value = "要求处理完毕时间")
	private Date reqFTime;
	
	@ApiModelProperty(value = "实际处理完毕时间")
	private Date actFTime;

	@Length(max = 36)
	@ApiModelProperty(value = "SLA状态")
	private String slaFlag;

	@Length(max = 36)
	@ApiModelProperty(value = "SLA级别")
	private String slaLvl;

	@Length(max = 36)
	@ApiModelProperty(value = "优先级")
	private String priority;

	@ApiModelProperty(value = "处理人姓名")
	private String responsibleName;
	
	@CheckPattern(type = PatternType.CONTAINS, values = {"1", "2"})
	@ApiModelProperty(value = "客户类型：1：新客户，2：老客户")
	private String customerType;

	private String lasthandledate;
	
	
	
	private Set items = new HashSet();
	
	@ApiModelProperty(value = "处理人类别")
	private Integer reqtype;
	
	private List<Request2ApproveLog> approveLogs;
	
	public RequestMaster() {
		
	}

	@InvokeAnnotate(type = PatternType.UNCOPY)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReqCode() {
		return reqCode;
	}

	public void setReqCode(String reqCode) {
		this.reqCode = reqCode;
	}

	@InvokeAnnotate(type = PatternType.CONTAINS, values = {"1", "2"})
	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getResponsible() {
		return responsible;
	}

	public void setResponsible(String responsible) {
		this.responsible = responsible;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getIsSign() {
		return isSign;
	}

	public void setIsSign(String isSign) {
		this.isSign = isSign;
	}

	public String getContract() {
		return contract;
	}

	public void setContract(String contract) {
		this.contract = contract;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getProjectDesc() {
		return projectDesc;
	}

	public void setProjectDesc(String projectDesc) {
		this.projectDesc = projectDesc;
	}

	public String getCusId() {
		return cusId;
	}

	public void setCusId(String cusId) {
		this.cusId = cusId;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getIphone() {
		return iphone;
	}

	public void setIphone(String iphone) {
		this.iphone = iphone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@InvokeAnnotate(type = PatternType.UNCOPY)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@InvokeAnnotate(type = PatternType.UNCOPY)
	public String getChgFlag() {
		return chgFlag;
	}

	public void setChgFlag(String chgFlag) {
		this.chgFlag = chgFlag;
	}

	@InvokeAnnotate(type = PatternType.UNCOPY)
	public String getSrcReqId() {
		return srcReqId;
	}

	public void setSrcReqId(String srcReqId) {
		this.srcReqId = srcReqId;
	}

//	@JsonIgnore
	public Set getItems() {
		return items;
	}

	public void setItems(Set items) {
		this.items = items;
	}

	@JsonIgnore
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getWorkFlowId() {
		return workFlowId;
	}

	public void setWorkFlowId(String workFlowId) {
		this.workFlowId = workFlowId;
	}

	public String getSlaFlag() {
		return slaFlag;
	}

	public void setSlaFlag(String slaFlag) {
		this.slaFlag = slaFlag;
	}

	public String getSlaLvl() {
		return slaLvl;
	}

	public void setSlaLvl(String slaLvl) {
		this.slaLvl = slaLvl;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getCusName() {
		return cusName;
	}

	public void setCusName(String cusName) {
		this.cusName = cusName;
	}

	public Date getReqFTime() {
		return reqFTime;
	}

	public void setReqFTime(Date reqFTime) {
		this.reqFTime = reqFTime;
	}

	public Date getActFTime() {
		return actFTime;
	}

	public void setActFTime(Date actFTime) {
		this.actFTime = actFTime;
	}

	public List<Request2ApproveLog> getApproveLogs() {
		return approveLogs;
	}

	public void setApproveLogs(List<Request2ApproveLog> approveLogs) {
		this.approveLogs = approveLogs;
	}

	public String getResponsibleName() {
		return responsibleName;
	}

	public void setResponsibleName(String responsibleName) {
		this.responsibleName = responsibleName;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	
	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getSrcReqCode() {
		return srcReqCode;
	}

	public void setSrcReqCode(String srcReqCode) {
		this.srcReqCode = srcReqCode;
	}

	public String getTgtReqCode() {
		return tgtReqCode;
	}

	public void setTgtReqCode(String tgtReqCode) {
		this.tgtReqCode = tgtReqCode;
	}

	public String getLasthandledate() {
		return lasthandledate;
	}

	public void setLasthandledate(String lasthandledate) {
		this.lasthandledate = lasthandledate;
	}

	public Integer getReqtype() {
		return reqtype;
	}

	public void setReqtype(Integer reqtype) {
		this.reqtype = reqtype;
	}
	
	
}
