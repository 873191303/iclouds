package com.h3c.iclouds.po.business;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yKF7317 on 2017/1/12.
 */
@ApiModel(value = "云管理产品规格组成属性表")
public class Specs2Key extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @Length(max = 36)
    @ApiModelProperty(value = "归属类别id即产品id（NotNull")
    @NotNull
    private String classId;

    @Length(max = 500)
    @ApiModelProperty(value = "属性名称（NotNull")
    @NotNull
    private String keyName;

    @Length(max = 100)
    @ApiModelProperty(value = "属性单位")
    private String unit;

    @ApiModelProperty(value="校验规则")
    private String validate;
	
    @NotNull
    @ApiModelProperty(value="数据类型(NotNull){1-整型,2-浮点型,3-字符串型,4-整型数组,5-浮点型数组,6-字符型数组}" )
	@CheckPattern(type = PatternType.CONTAINS, values = {"1", "2", "3", "4", "5", "6"})
    private String dataType;
    
    @ApiModelProperty(value="是否必须")
    private String isMust = "0";
    
    @ApiModelProperty(value="是否显示")
    private String isShow = "0";
    
    @ApiModelProperty(value="属性英文名(NotNull)")
	@NotNull
    private String key;
    
    private Set<Specs2KeyValue> values = new HashSet<>();
    
    public String getValidate() {
		return validate;
	}

	public void setValidate(String validate) {
		this.validate = validate;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getIsMust() {
		return isMust;
	}

	public void setIsMust(String isMust) {
		this.isMust = isMust;
	}

	public String getIsShow() {
		return isShow;
	}

	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
	
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
	
	public Set<Specs2KeyValue> getValues () {
		return values;
	}
	
	public void setValues (Set<Specs2KeyValue> values) {
		this.values = values;
	}
}
