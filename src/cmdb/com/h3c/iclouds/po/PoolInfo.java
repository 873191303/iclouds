package com.h3c.iclouds.po;

import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/12/5.
 */
@ApiModel(value = "主机池视图信息")
public class PoolInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "对象id")
    private String id;

    @NotNull
    @ApiModelProperty(value = "对象uuid(NotNull)")
    private String uuid;

    @NotNull
    @ApiModelProperty(value = "对象类型(NotNull)", notes = "1-主机池，2-集群，3-宿主机")
    @CheckPattern(type = PatternType.CONTAINS, values = {"1", "2", "3"})
    private String type;

    @ApiModelProperty(value = "对象数据")
    private Map<String, Object> data;

    @ApiModelProperty(value = "父集id")
    private String previousId;

    @ApiModelProperty(value = "父集uuid")
    private String previousUuid;

    @NotNull
    @ApiModelProperty(value = "操作(NotNull)", notes = "0-删除, 1-添加, 2-修改")
    @CheckPattern(type = PatternType.CONTAINS, values = {"0", "1", "2"})
    private String option;

    @NotNull
    @ApiModelProperty(value = "操作顺序(NotNull)")
    private String sequence;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getPreviousId() {
        return previousId;
    }

    public void setPreviousId(String previousId) {
        this.previousId = previousId;
    }

    public String getPreviousUuid() {
        return previousUuid;
    }

    public void setPreviousUuid(String previousUuid) {
        this.previousUuid = previousUuid;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
}
