package com.h3c.iclouds.po;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

@ApiModel(value = "机房管理", description = "机房管理")
@SuppressWarnings("rawtypes")
public class Rooms extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "id")
	private String id;

	@Length(max = 100)
	@NotNull
	@ApiModelProperty(value = "机房名称 (NotNull)", required = true)
	private String roomName;

	@Length(max = 100)
	@ApiModelProperty(value = "机房简称")
	private String shortName;

	@NotNull
	@ApiModelProperty(value = "容纳机柜最大排数 (NotNull)", required = true)
	private Integer maxRows;

	@NotNull
	@ApiModelProperty(value = "每排容纳机柜最大个数 (NotNull)", required = true)
	private Integer maxCols;
	
	@NotNull
	@ApiModelProperty(value = "机柜默认U数 (NotNull)", required = true)
	private Integer defaultU;
	
	@ApiModelProperty(value = "机房供电容量")
	private Integer capacity;

	@Length(max = 20)
	@ApiModelProperty(value = "机房电话")
	private String telephone;

	@Length(max = 50)
	@ApiModelProperty(value = "机房归属")
	private String roomOnwer;

	@Length(max = 50)
	@ApiModelProperty(value = "现场管理员")
	private String localAdmin;

	@Length(max = 50)
	@NotNull
	@ApiModelProperty(value = "现场管理员电话 (NotNull)")
	private String localAdminTel;

	@Length(max = 50)
	@ApiModelProperty(value = "机房管理员")
	private String admin;

	@Length(max = 100)
	@NotNull
	@ApiModelProperty(value = "供应商联系方式 (NotNull)")
	private String contact;

	@Length(max = 50)
	@NotNull
	@ApiModelProperty(value = "归属区域 (NotNull)", required = true)
	private String region;

	@Length(max = 100)
	@ApiModelProperty(value = "详细地址")
	private String address;

	@Length(max = 100)
	@ApiModelProperty(value = "备注")
	private String remark;

	@Length(max = 20)
	@ApiModelProperty(value = "机房管理员电话 (NotNull)")
	private String adminTel;

	@Length(max = 100)
	@NotNull
	@ApiModelProperty(value = "供应商 (NotNull)")
	private String supplier;

	private Integer drawCount;

	private Set drawSet = new HashSet<Draws>();

	private List<Draws> draws;

	public Rooms() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Integer getMaxRows() {
		return maxRows;
	}

	public void setMaxRows(Integer maxRows) {
		this.maxRows = maxRows;
	}

	public Integer getMaxCols() {
		return maxCols;
	}

	public void setMaxCols(Integer maxCols) {
		this.maxCols = maxCols;
	}

	public Integer getDefaultU() {
		return defaultU;
	}

	public void setDefaultU(Integer defaultU) {
		this.defaultU = defaultU;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getRoomOnwer() {
		return roomOnwer;
	}

	public void setRoomOnwer(String roomOnwer) {
		this.roomOnwer = roomOnwer;
	}

	public String getLocalAdmin() {
		return localAdmin;
	}

	public void setLocalAdmin(String localAdmin) {
		this.localAdmin = localAdmin;
	}

	public String getLocalAdminTel() {
		return localAdminTel;
	}

	public void setLocalAdminTel(String localAdminTel) {
		this.localAdminTel = localAdminTel;
	}

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getDrawCount() {
		return drawCount;
	}

	public void setDrawCount(Integer drawCount) {
		this.drawCount = drawCount;
	}

	@JsonIgnore
	public Set getDrawSet() {
		return drawSet;
	}

	public void setDrawSet(Set drawSet) {
		this.drawSet = drawSet;
	}

	public List<Draws> getDraws() {
		return draws;
	}

	public void setDraws(List<Draws> draws) {
		this.draws = draws;
	}

	public String getAdminTel() {
		return adminTel;
	}

	public void setAdminTel(String adminTel) {
		this.adminTel = adminTel;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

}