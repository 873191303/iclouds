package com.h3c.iclouds.po;

import javax.validation.constraints.Pattern;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

/**
 * Created by Administrator on 2017/1/10.
 */
@ApiModel(value = "云运维服务关系表", description = "云运维服务关系表")
public class ServiceCluster extends BaseEntity implements java.io.Serializable{
	
	private static final long serialVersionUID = -6728567866121748411L;
	@ApiModelProperty(value = "关系ID")
	private String id;
	
	@ApiModelProperty(value = "名称")
	@Length(max = 20)
    private String cname;
	
	@CheckPattern(type = PatternType.CONTAINS, values = {"12","13"})
	@ApiModelProperty(value = "关系,中间件集群传入0，数据库集群传入1 ")
    private String relation;
	
	@Pattern(regexp = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))")
	@ApiModelProperty(value = "虚拟IP")
    private String vip;
	
	@ApiModelProperty(value = "备注")
	
    private String remark;
	@ApiModelProperty(value = "租户")
    private String projectId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCname() {
        return cname;
    }

    public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
