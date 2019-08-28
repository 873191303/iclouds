package com.h3c.iclouds.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;

@ApiModel(value = "资产设备机架信息", description = "资产设备机架信息")
public class Asset2Drawer extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "id")
	private String id;

	@Length(max = 36)
	@ApiModelProperty(value = "机柜编号")
	private String drawsId;

	@Min(1)
	@ApiModelProperty(value = "起始U数 (NotNull)(min >= 1)")
	private Integer unumb;

	@Length(max = 600)
	@ApiModelProperty(value = "备注")
	private String remark;
	
	@ApiModelProperty(value = "机房名称")
	private String roomName;
	
	@ApiModelProperty(value="机房id")
	private String roomId;
	
	@ApiModelProperty(value = "坐标行号")
	private Integer rowNum;
	
	@ApiModelProperty(value = "坐标列号")
	private Integer colNum;

	@ApiModelProperty(value = "总U数")
	private Integer unum;

	private AsmMaster master;
	
	private AsmMaster asmMaster;
	
	private Draws draws;
	
	public AsmMaster getMaster() {
		return master;
	}

	public void setMaster(AsmMaster master) {
		this.master = master;
	}

	public Asset2Drawer() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDrawsId() {
		return drawsId;
	}

	public void setDrawsId(String drawsId) {
		this.drawsId = drawsId;
	}

	public Integer getUnumb() {
		return unumb;
	}

	public void setUnumb(Integer unumb) {
		this.unumb = unumb;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public Integer getRowNum() {
		return rowNum;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
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

	@JsonIgnore
	public Draws getDraws() {
		return draws;
	}

	public void setDraws(Draws draws) {
		this.draws = draws;
	}

	@JsonIgnore
	public AsmMaster getAsmMaster() {
		return asmMaster;
	}

	public void setAsmMaster(AsmMaster asmMaster) {
		this.asmMaster = asmMaster;
	}

	public Integer getUnum() {
		return unum;
	}

	public void setUnum(Integer unum) {
		this.unum = unum;
	}
}
