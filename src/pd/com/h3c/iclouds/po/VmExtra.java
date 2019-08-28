package com.h3c.iclouds.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@ApiModel(value = "云管理云主机配置扩展表", description = "云管理云主机配置扩展表")
public class VmExtra implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "密钥串")
    @Length(max = 50)
    private String sshKey;

    @ApiModelProperty(value = "系统用户")
    @Length(max = 50)
    private String osUser;

    @ApiModelProperty(value = "初始密码")
    @Length(max = 50)
    private String osPasswd;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSshKey() {
        return sshKey;
    }

    public void setSshKey(String sshKey) {
        this.sshKey = sshKey;
    }

    public String getOsUser() {
        return osUser;
    }

    public void setOsUser(String osUser) {
        this.osUser = osUser;
    }

    public String getOsPasswd() {
        return osPasswd;
    }

    public void setOsPasswd(String osPasswd) {
        this.osPasswd = osPasswd;
    }


}
