package com.h3c.iclouds.po.inc;

import java.util.Date;
import java.util.Map;
import java.util.Vector;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.UploadFileModal;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "事件工单处理记录")
public class Inc2ApproveLog extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "记录ID")
	private String id;

	@Length(max = 36)
	@ApiModelProperty(value = "工单ID")
	private String reqId;

	@Length(max = 36)
	@ApiModelProperty(value = "流程实例ID")
	private String insId;

	@Length(max = 100)
	@ApiModelProperty(value = "环节")
	private String step;

	@Length(max = 36)
	@ApiModelProperty(value = "任务id")
	private String taskId;

	@Length(max = 36)
	@ApiModelProperty(value = "审批结果")
	private String option;

	@Length(max = 500)
	@ApiModelProperty(value = "审批意见")
	private String comment;

	@Length(max = 36)
	@ApiModelProperty(value = "审批人id (NotNull)")
	private String approver;

	@ApiModelProperty(value = "审批人名称")
	private String approverName;
	
	@ApiModelProperty(value = "通知人")
	private String emails;

	@ApiModelProperty(value = "附件")
	private String attachment;

	public Inc2ApproveLog() {
		
	}
	
	public Inc2ApproveLog(Map<String, Object> alias, String userId, IncMaster master, UploadFileModal entity) {
		super();
		this.setApprover(userId);
		this.setCreatedBy(userId);
		this.setCreatedDate(new Date());
		this.setUpdatedBy(userId);
		this.setUpdatedDate(new Date());
		this.setComment(StrUtils.tranString(alias.get(ConfigProperty.REQUEST_COMMENT)));
		this.setInsId(master.getInstanceId());
		this.setReqId(master.getId());
		this.setStep(StrUtils.tranString(alias.get("segment")));
		if(StrUtils.checkParam(this.step)) {
			this.setStep(ConfigProperty.INC_STEP1_SUBMIT_APPROVE);
		}
		this.setTaskId(StrUtils.tranString(alias.get("taskId")));
		this.setOption(StrUtils.tranString(alias.get(ConfigProperty.REQUEST_PARAMETER)));
		
		// 保存附件名
		if(entity != null) {
			Vector<String> list = entity.getFileNames();
			if(StrUtils.checkCollection(list)) {
				StringBuffer attachment = new StringBuffer();
				for (String fileName : list) {
					attachment.append(fileName + "|");
				}
				// 移除最后的符号
				attachment.deleteCharAt(attachment.length() - 1);
				this.attachment = attachment.toString();
			}
		}
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public String getInsId() {
		return insId;
	}

	public void setInsId(String insId) {
		this.insId = insId;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getApprover() {
		return approver;
	}

	public void setApprover(String approver) {
		this.approver = approver;
	}

	public String getApproverName() {
		return approverName;
	}

	public void setApproverName(String approverName) {
		this.approverName = approverName;
	}

	public String getEmails() {
		return emails;
	}

	public void setEmails(String emails) {
		this.emails = emails;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

}
