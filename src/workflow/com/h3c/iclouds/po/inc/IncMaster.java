package com.h3c.iclouds.po.inc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.h3c.iclouds.utils.LogUtils;
import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.utils.InvokeAnnotate;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "事件工单")
public class IncMaster extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "申请单id")
	private String id;

	@Length(max = 100)
	@ApiModelProperty(value = "申请单编号", required = true)
	private String incNo;
	
	@NotNull
	@Length(max = 100)
	@ApiModelProperty(value = "标题 （NotNull）", required = true)
	private String topic;
	
	@NotNull
	@Length(max = 500)
	@ApiModelProperty(value = "详述 （NotNull）", required = true)
	private String content;
	
	@Length(max = 36)
	@ApiModelProperty(value = "内容类别", required = true)
	private String incType;
	
	@ApiModelProperty(value = "发生时间", required = true)
	private Date causedTime;
	
	@Length(max = 36)
	@ApiModelProperty(value = "环节", required = true)
	private String step;

	@Length(max = 36)
	@ApiModelProperty(value = "当前处理人,不需要传递")
	private String responsible;
	
	@Length(max = 36)
	@ApiModelProperty(value = "客户单位")
	private String company;
	
	@Length(max = 100)
	@ApiModelProperty(value = "联系人姓名")
	private String customer;
	
	@Length(max = 100)
	@ApiModelProperty(value = "手机")
	private String telephone;

	@Length(max = 100)
	@ApiModelProperty(value = "邮箱")
	private String email;

	@Length(max = 100)
	@ApiModelProperty(value = "报障人/申请人")
	private String reporter;

	@Length(max = 36)
	@ApiModelProperty(value = "来源")
	private String fromTo;

	@Length(max = 36)
	@ApiModelProperty(value = "方式")
	private String ways;

	@ApiModelProperty(value = "要求处理完毕时间")
	private Date reqFTime;
	
	@ApiModelProperty(value = "实际处理完毕时间")
	private Date actFTime;
	
	@Length(max = 36)
	@ApiModelProperty(value = "工单级别")
	private String incLevel;
	
	@Length(max = 100)
	@ApiModelProperty(value = "工单状态")
	private String rtuFlag;
	
	@Length(max = 36)
	@ApiModelProperty(value = "流程实例ID")
	private String instanceId;

	@Length(max = 36)
	@ApiModelProperty(value = "流程镜像id (NotNull), 默认先传递【4】 ")
	@NotNull
	private String workFlowId = "5";	// 默认为4
	
	@Length(max = 100)
	@ApiModelProperty(value = "流程实例ID")
	private String attachment;

	@ApiModelProperty(value = "处理人姓名")
	private String responsibleName;
	
	private List<Inc2ApproveLog> approveLogs;
	
	@ApiModelProperty(value = "分类")
	private String incTypeValue;

	private String lasthandledate;
	
	public IncMaster() {
		
	}

	@InvokeAnnotate(type = PatternType.UNCOPY)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public List<Inc2ApproveLog> getApproveLogs() {
		return approveLogs;
	}

	public void setApproveLogs(List<Inc2ApproveLog> approveLogs) {
		this.approveLogs = approveLogs;
	}

	public String getResponsibleName() {
		return responsibleName;
	}

	public void setResponsibleName(String responsibleName) {
		this.responsibleName = responsibleName;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getIncType() {
		return incType;
	}

	public void setIncType(String incType) {
		this.incType = incType;
	}

	public Date getCausedTime() {
		return causedTime;
	}

	public void setCausedTime(Date causedTime) {
		this.causedTime = causedTime;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getReporter() {
		return reporter;
	}

	public void setReporter(String reporter) {
		this.reporter = reporter;
	}

	public String getFromTo() {
		return fromTo;
	}

	public void setFromTo(String fromTo) {
		this.fromTo = fromTo;
	}

	public String getWays() {
		return ways;
	}

	public void setWays(String ways) {
		this.ways = ways;
	}

	public String getIncLevel() {
		return incLevel;
	}

	public void setIncLevel(String incLevel) {
		this.incLevel = incLevel;
	}

	public String getRtuFlag() {
		return rtuFlag;
	}

	public void setRtuFlag(String rtuFlag) {
		this.rtuFlag = rtuFlag;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public String getIncNo() {
		return incNo;
	}

	public void setIncNo(String incNo) {
		this.incNo = incNo;
	}

	public String getIncTypeValue() {
		return incTypeValue;
	}

	public void setIncTypeValue(String incTypeValue) {
		this.incTypeValue = incTypeValue;
	}

	public String getLasthandledate() {
		return lasthandledate;
	}

	public void setLasthandledate(String lasthandledate) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = sdf.parse(lasthandledate);
			lasthandledate = sdf.format(date);
		} catch (ParseException e) {
			LogUtils.exception(IncMaster.class, e);
		}
		this.lasthandledate = lasthandledate;
	}
}
