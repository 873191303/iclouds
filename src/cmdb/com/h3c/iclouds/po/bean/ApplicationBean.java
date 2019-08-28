package com.h3c.iclouds.po.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ApplicationBean implements Serializable {

	private static final long serialVersionUID = -2378036252992440678L;

	@ApiModelProperty(value = "父集id")
	private List<String> pid;
	
	public List<String> getPid() {
		return pid;
	}

	public void setPid(List<String> pid) {
		this.pid = pid;
	}

	private String puuid;
	
	private String uuid;
	
	private String appName;

	@ApiModelProperty(value = "自身id")
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@NotNull
	@ApiModelProperty(value = "操作(NotNull)", notes = "0-删除, 1-添加, 2-修改")
	private String option;
	
	private String sequence;
	@NotNull
	@ApiModelProperty(value = "元素图(NotNull)", notes = "应用是0，中间件集群是12，数据库集群是13，中间件是2，数据库是3")
	private String type;
	
	private Map<String, Object> data;
	
	
	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	@JsonIgnore
	public String getPuuid() {
		return puuid;
	}

	public void setPuuid(String puuid) {
		this.puuid = puuid;
	}
	@JsonIgnore
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	@JsonIgnore
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

}
