package com.h3c.iclouds.po;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

@SuppressWarnings("rawtypes")
@ApiModel(value = "机柜", description = "机柜")
public class Draws extends BaseEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private String id;

	@Length(max = 36)
	@ApiModelProperty(value = "机房编号")
	private String roomId;

	@NotNull
	@ApiModelProperty(value = "坐标行号 (NotNull)")
	private Integer rowNum;

	@NotNull
	@ApiModelProperty(value = "坐标列号 (NotNull)")
	private Integer colNum;

	@NotNull
	@ApiModelProperty(value = "机柜总U数 (NotNull)")
	private Integer maxU;

	@Length(max = 100)
	@ApiModelProperty(value = "备注信息")
	private String remark = ConfigProperty.SYSTEM_WRITE;

	@NotNull
	@CheckPattern(type = PatternType.CONTAINS, values = {"0", "1"})
	@ApiModelProperty(value = "是否使用 (NotNull)(Contain: 0-是; 1-否)", required = true)
	private String isUse = ConfigProperty.NO;

	@NotNull
	@CheckPattern(type = PatternType.CONTAINS, values = {"0", "1"})
	@ApiModelProperty(value = "是否标准 (NotNull)(Contain: 0-是; 1-否)", required = true)
	private String isStandard = ConfigProperty.NO;
	
	private Rooms room;
	
	private Set asset2Draws = new HashSet();

	private List<Asset2Drawer> asset2Drawers = new ArrayList<>();

	public Draws() {

	}

	public Draws(String roomId, Integer rowNum, Integer colNum, Integer maxU, String createdBy) {
		super();
		this.roomId = roomId;
		this.rowNum = rowNum;
		this.colNum = colNum;
		this.maxU = maxU;
		this.createdUser(createdBy);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public Integer getRowNum() {
		return rowNum;
	}

	public void setRowNum(Integer rowNum) {
		this.rowNum = rowNum;
	}

	public Integer getColNum() {
		return colNum;
	}

	public void setColNum(Integer colNum) {
		this.colNum = colNum;
	}

	public Integer getMaxU() {
		return maxU;
	}

	public void setMaxU(Integer maxU) {
		this.maxU = maxU;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getIsUse() {
		return isUse;
	}

	public void setIsUse(String isUse) {
		this.isUse = isUse;
	}

	public String getIsStandard() {
		return isStandard;
	}

	public void setIsStandard(String isStandard) {
		this.isStandard = isStandard;
	}

	@JsonIgnore
	public Rooms getRoom() {
		return room;
	}

	public void setRoom(Rooms room) {
		this.room = room;
	}

	@JsonIgnore
	public Set getAsset2Draws() {
		return asset2Draws;
	}

	public void setAsset2Draws(Set asset2Draws) {
		this.asset2Draws = asset2Draws;
	}

	public List<Asset2Drawer> getAsset2Drawers() {
		return asset2Drawers;
	}

	public void setAsset2Drawers(List<Asset2Drawer> asset2Drawers) {
		this.asset2Drawers = asset2Drawers;
	}
}
