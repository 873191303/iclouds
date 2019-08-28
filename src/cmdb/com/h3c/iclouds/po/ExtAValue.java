package com.h3c.iclouds.po;

import java.io.Serializable;
import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;


@ApiModel(value="资源管理扩展列值",description="资源管理扩展列值")
public class ExtAValue extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @ApiModelProperty(value="id")
	private String id;
    
    @ApiModelProperty(value="资产id")
	private String assetID;
    
    @ApiModelProperty(value="资产序列号")
    private String serial;
    
    @ApiModelProperty(value="资产名称")
    private String assetName;
    
    @ApiModelProperty(value="扩展属列id")
    private String extID;
    
    @ApiModelProperty(value="扩展属列名")
    private String extName;
    
    @ApiModelProperty(value="扩展列属性值")
    private String extValue;

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getAssetID() {
		return assetID;
	}

	public void setAssetID(String assetID) {
		this.assetID = assetID;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public String getExtName() {
		return extName;
	}

	public void setExtName(String extName) {
		this.extName = extName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExtID() {
		return extID;
	}

	public void setExtID(String extID) {
		this.extID = extID;
	}

	public String getExtValue() {
		return extValue;
	}

	public void setExtValue(String extValue) {
		this.extValue = extValue;
	}
    
    
}
