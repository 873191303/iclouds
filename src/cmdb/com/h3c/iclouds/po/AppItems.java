package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by ykf7408 on 2017/1/10.
 */
@ApiModel(value = "云运维应用", description = "云运维应用")
public class AppItems  extends BaseEntity implements java.io.Serializable {
	
	
	private static final long serialVersionUID = -660355125637404502L;
	
	@ApiModelProperty(value = "id")
    private String id;
	
	@ApiModelProperty(value = "对象UUID")
    private String uuid;
	
	@ApiModelProperty(value = "对象属性")
    private String option;
	
	@ApiModelProperty(value = "类型")
    private String itemtype;
    
    @ApiModelProperty(value = "视图id")
	private String viewId;
    
    @ApiModelProperty(value = "名称")
    private String name;
	
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getItemtype() {
        return itemtype;
    }

    public void setItemtype(String itemtype) {
        this.itemtype = itemtype;
    }
    
    public String getViewId () {
        return viewId;
    }
    
    public void setViewId (String viewId) {
        this.viewId = viewId;
    }
    
    public String getName () {
        return name;
    }
    
    public void setName (String name) {
        this.name = name;
    }
}
