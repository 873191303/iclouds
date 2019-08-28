package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by ykf7408 on 2017/1/10.
 */
@ApiModel(value = "云运维应用关系拓扑视图", description = "云运维应用关系拓扑视图")
public class AppRelations extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = -4887511534177292409L;

	@ApiModelProperty(value = "id")
	private String id;
	
	@ApiModelProperty(value = "前者对象id")
	private String previous;
	
	@ApiModelProperty(value = "视图ID")
	private String viewId;
	
	@ApiModelProperty(value = "appId")
	private String appId;

	@ApiModelProperty(value = "同级顺序")
	private Integer sequence;
	
	@ApiModelProperty(value = "类型")
	private String itemType;
	
	public AppRelations() {
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrevious() {
		return previous;
	}

	public void setPrevious(String previous) {
		this.previous = previous;
	}

	public String getViewId() {
		return viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	
	public String getItemType () {
		return itemType;
	}
	
	public void setItemType (String itemType) {
		this.itemType = itemType;
	}
}
