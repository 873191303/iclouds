package com.h3c.iclouds.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.utils.InvokeAnnotate;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户表
 * @author zkf5485
 *
 */
@SuppressWarnings("rawtypes")
@ApiModel(value = "用户信息", description = "用户信息")
public class User extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "用户id")
	private String id;
	
	@NotNull
	@Pattern(regexp = "^[a-zA-Z][0-9a-zA-Z_@-]{0,31}$")
	@ApiModelProperty(value = "登录名 (NotNull)", required = true)
	private String loginName;

	@Length(max = 36)
	@ApiModelProperty(value = "归属租户，不需要传递", required = true)
	private String projectId;

	@Length(max = 100)
	@NotNull
	@Pattern(regexp = "^[\\u4E00-\\u9FA50-9a-zA-Z_@-]{2,32}$")
	@ApiModelProperty(value = "用户姓名 (NotNull)", required = true)
	private String userName;

	@Length(max = 36)
	@ApiModelProperty(value = "用户归属部门 (NotNull)")
	private String deptId;
	
	@CheckPattern(type = PatternType.CONTAINS, values = {"0", "1"})	// 0:是 1:否
	@ApiModelProperty(value = "是否管理员 (NotNull)(Contain: 0-是; 1-否)", notes = "0:是 1:否, 默认或者不传都为1")
	private String isAdmin = "1";

	@Length(max = 20)
	@ApiModelProperty(value = "工号")
	private String idCard;

	@Length(max = 100)
	@ApiModelProperty(value = "密码")
	private String password;
	
	@Length(max = 100)
	@ApiModelProperty(value = "备注")
	private String remark;

	@Length(max = 100)
	@ApiModelProperty(value = "邮箱", notes = "需要满足邮箱格式")
	private String email;

	@Length(max = 100)
	@ApiModelProperty(value = "电话", notes = "需要满足电话格式")
	private String telephone;
	
	@NotNull
	@CheckPattern(type = PatternType.CONTAINS, values = {"0", "1"})
	@ApiModelProperty(value = "用户状态 (NotNull)(Contain: 0-正常; 1-锁定)", notes = "0:正常 1:被锁定, 默认或者不传都为0")
	private String status = ConfigProperty.YES;
	
	@ApiModelProperty(value = "生效时间", notes = "格式yyyy-MM-dd")
	private Date effectiveDate;
	
	@ApiModelProperty(value = "失效时间")
	private Date expireDate;
	
	@CheckPattern(type = PatternType.CONTAINS, values = {"0", "1"})
	@ApiModelProperty(value = "是否域用户 (NotNull)(Contain: 0-是; 1-否)", notes = "0:是 1:否, 默认或者不传都为1")
	private String inDomain = "1";
	
	@ApiModelProperty(value = "默认群组")
	private String defaultGroupId = "";
	
	private transient Set userToGroupSet = new HashSet();
	
	private transient Set<User2Role> userToRoleSet = new HashSet<>();
	
	private Set<String> roles;
	
	@ApiModelProperty(value = "用户归属部门名称,用于数据显示和列表查询字段使用", notes = "回选字段，修改/添加不必处理")
	private String deptName;
	
	@ApiModelProperty(value = "用户归属租户名称,用于数据显示和列表查询字段使用", notes = "回选字段，修改/添加不必处理")
	private String projectName;
	
	@ApiModelProperty(value = "最后登录时间，不需要传递")
	private Date lastLoginDate;
	
	private String cloudosId;
	
	public User() {

	}

	public User(String id, String loginName, String userName, String email, String telephone, String deptName) {
		super();
		this.id = id;
		this.loginName = loginName;
		this.userName = userName;
		this.email = email;
		this.telephone = telephone;
		this.deptName = deptName;
	}
	
	public User(String id, String loginName, String userName, String email, String telephone, String deptName,String cloudosId) {
		super();
		this.id = id;
		this.loginName = loginName;
		this.userName = userName;
		this.email = email;
		this.telephone = telephone;
		this.deptName = deptName;
		this.cloudosId=cloudosId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@InvokeAnnotate(type = PatternType.FK)
	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(String isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	@JsonIgnore
	@InvokeAnnotate(type = PatternType.UNCOPY)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public String getInDomain() {
		return inDomain;
	}

	public void setInDomain(String inDomain) {
		this.inDomain = inDomain;
	}

	public String getDefaultGroupId() {
		return defaultGroupId;
	}

	public void setDefaultGroupId(String defaultGroupId) {
		this.defaultGroupId = defaultGroupId;
	}

	@JsonIgnore
	public Set getUserToGroupSet() {
		return userToGroupSet;
	}

	public void setUserToGroupSet(Set userToGroupSet) {
		this.userToGroupSet = userToGroupSet;
	}
	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	
	public String getCloudosId() {
		return cloudosId;
	}

	public void setCloudosId(String cloudosId) {
		this.cloudosId = cloudosId;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	@JsonIgnore
	public Set<User2Role> getUserToRoleSet() {
		return userToRoleSet;
	}

	public void setUserToRoleSet(Set<User2Role> userToRoleSet) {
		this.userToRoleSet = userToRoleSet;
	}

}
