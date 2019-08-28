package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/14.
 */
@ApiModel(value = "资源配置挂载卷与主机关系", description = "资源配置挂载卷与主机关系")
public class Volume2Host extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @NotNull
    @Length(max = 50)
    @ApiModelProperty(value = "挂载卷Id")
    private String volumeId;

    @Length(max = 50)
    @ApiModelProperty(value = "类型")
    private String type;

    @NotNull
    @Length(max = 50)
    @ApiModelProperty(value = "主机Id")
    private String hostId;

    @Length(max = 50)
    @ApiModelProperty(value = "主机存储网IP")
    private String targetIp;

    public Volume2Host() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(String volumeId) {
        this.volumeId = volumeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getTargetIp() {
        return targetIp;
    }

    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }
}
