package com.h3c.iclouds.po.bean.cloudos;

import com.h3c.iclouds.validate.NotNull;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

/**
 * Created by zkf5485 on 2017/4/12.
 */
public class CloneOrImageBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Min(1)
    private Integer count;

    @NotNull
    @Length(max = 50)
//    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$")
    private String name;

    private String remark;

    private String label;

    private Boolean cloneMode = false;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getCloneMode() {
        return cloneMode;
    }

    public void setCloneMode(Boolean cloneMode) {
        this.cloneMode = cloneMode;
    }
}
