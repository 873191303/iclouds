package com.h3c.iclouds.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.utils.InvokeAnnotate;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("rawtypes")
@ApiModel(value = "资产管理设备资产信息", description = "资产管理设备资产信息")
public class AsmMaster extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private String id;

	@Length(max = 36)
	@NotNull
	@ApiModelProperty(value = "资产编号 (NotNull)")
	private String assetId;

	@Length(max = 100)
	@NotNull
	@ApiModelProperty(value = "设备序列号 (NotNull)")
	private String serial;

	@Length(max = 100)
	@NotNull
	@ApiModelProperty(value = "设备名称 (NotNull)")
	private String assetName;
	
	@ApiModelProperty(value = "设备类型id，不需要传递")
	private String assetType;

	@Length(max = 36)
	@ApiModelProperty(value = "设备型号")
	private String assMode;

	@ApiModelProperty(value = "型号名称")
	private String modeName;
	
	@Length(max = 50)
	@NotNull
	@ApiModelProperty(value = "归属部门 (NotNull)")
	private String depart;

	@Length(max = 36)
	@NotNull
	@ApiModelProperty(value = "资产管理员 (NotNull)")
	private String assetUser;

	@Length(max = 36)
	@ApiModelProperty(value = "设备管理员")
	private String assUser;

	@Length(max = 36)
	@ApiModelProperty(value = "系统管理员")
	private String sysUser;

	@Length(max = 50)
	@ApiModelProperty(value = "当前使用人")
	private String userId;

	@Length(max = 50)
	@ApiModelProperty(value = "环境类型")
	private String useFlag;

	@Length(max = 50)
	@ApiModelProperty(value = "操作系统")
	private String os;

	@Length(max = 50)
	@NotNull
	@ApiModelProperty(value = "品牌(NotNull)")
	private String provide;

	@Length(max = 50)
	@ApiModelProperty(value = "父资产ID")
	private String parentId;

	@Length(max = 100)
	@NotNull
	@CheckPattern(type = PatternType.CONTAINS, values = {"1", "2", "3"})
	@ApiModelProperty(value = "资产状态 (NotNull) (Contains设备状态 1：草稿，2：使用中，3：已退库)")
	private String status;

	@Length(max = 100)
	@ApiModelProperty(value = "管理MAC")
	private String mmac;

	@Length(max = 100)
	@ApiModelProperty(value = "管理IP")
	@CheckPattern(type = PatternType.IP)
	private String iloIP;

	@NotNull
	@ApiModelProperty(value = "启用日期 (NotNull)")
	private Date begDate;

	@ApiModelProperty(value = "停用日期")
	private Date endDate;

	@ApiModelProperty(value = "折旧期限")
	private Double lifeYears;

	@ApiModelProperty(value = "报废日期")
	private Date retirementDate;

	@Length(max = 600)
	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "设备类型名称，不需要传递")
	private String assetTypeName;

	@NotNull
	@CheckPattern(type = PatternType.CONTAINS, values = {"router", "switch", "server", "other", "boards", "stock",
			"firewall"})
	@ApiModelProperty(value = "设备类型代码 Contains(router:路由器, switch:交换机, server:服务器, other:其他, boards:板卡, stock:存储)")
	private String assetTypeCode;
	
	@ApiModelProperty(value = "部门名称，不需要传递")
	private String departName;
	
	@ApiModelProperty(value = "资产管理员名称，不需要传递")
	private String assetUserName;
	
	@ApiModelProperty(value = "设备管理员名称，不需要传递")
	private String assUserName;
	
	@ApiModelProperty(value = "系统管理员名称，不需要传递")
	private String sysUserName;
	
	@ApiModelProperty(value = "当前使用人名称，不需要传递")
	private String userIdName;
	
	@ApiModelProperty(value = "设备机柜信息，不需要传递")
	private Asset2Drawer draw;
	
	@ApiModelProperty(value = "设备配置信息，不需要传递")
	private BaseEntity config;
	
	private String stackId;
	
	private List<NetPorts> netPorts;

	@ApiModelProperty(value = "设备设置机柜id")
	private String drawsId;

	@Min(1)
	@ApiModelProperty(value = "设备机柜起始U数 (NotNull)(min >= 1)")
	private String unumb;
	
	private Set asset2Drawers = new HashSet();
	
	@ApiModelProperty(value="CPU个数")
	private Integer ncpu;
	
	@ApiModelProperty(value="CPU核数")
	private Integer ncore;
	
	@ApiModelProperty(value="内存条数")
	private Integer ram;
	
	@ApiModelProperty(value="内存总量")
	private Integer memTotal;
	
	@ApiModelProperty(value="硬盘个数")
	private Integer ndisks;
	
	@ApiModelProperty(value="硬盘总量")
	private Integer diskTotal;
	
	@ApiModelProperty(value="电源数量")
	private Integer npower;
	
	@ApiModelProperty(value="功率")
	private Integer pkws;
	
	@ApiModelProperty(value="端口数")
	private Integer switchPort;
	
	@ApiModelProperty(value="来源项目")
	private String comeFromPrj;
	
	@ApiModelProperty(value="上线时间")
	private Date upTime;
	
	@ApiModelProperty(value="下线时间")
	private Date downTime;
	
	@ApiModelProperty(value="租户id")
	private String tenantId = CacheSingleton.getInstance().getRootProject();
	
	@ApiModelProperty(value="是否加入监控标识->0加入监控")
	private Integer count;
	
	public Integer getNdisks() {
		return ndisks;
	}

	public void setNdisks(Integer ndisks) {
		this.ndisks = ndisks;
	}

	public Integer getNcpu() {
		return ncpu;
	}

	public void setNcpu(Integer ncpu) {
		this.ncpu = ncpu;
	}

	public Integer getNcore() {
		return ncore;
	}

	public void setNcore(Integer ncore) {
		this.ncore = ncore;
	}

	public Integer getRam() {
		return ram;
	}

	public void setRam(Integer ram) {
		this.ram = ram;
	}

	public Integer getMemTotal() {
		return memTotal;
	}

	public void setMemTotal(Integer memTotal) {
		this.memTotal = memTotal;
	}

	public Integer getDiskTotal() {
		return diskTotal;
	}

	public void setDiskTotal(Integer diskTotal) {
		this.diskTotal = diskTotal;
	}
	
	public Integer getNpower() {
		return npower;
	}

	public void setNpower(Integer npower) {
		this.npower = npower;
	}

	public Integer getPkws() {
		return pkws;
	}

	public void setPkws(Integer pkws) {
		this.pkws = pkws;
	}

	public Integer getSwitchPort() {
		return switchPort;
	}

	public void setSwitchPort(Integer switchPort) {
		this.switchPort = switchPort;
	}

	public String getComeFromPrj() {
		return comeFromPrj;
	}

	public void setComeFromPrj(String comeFromPrj) {
		this.comeFromPrj = comeFromPrj;
	}

	public Date getUpTime() {
		return upTime;
	}

	public void setUpTime(Date upTime) {
		this.upTime = upTime;
	}

	public Date getDownTime() {
		return downTime;
	}

	public void setDownTime(Date downTime) {
		this.downTime = downTime;
	}

	public AsmMaster() {
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getModeName() {
		return modeName;
	}

	public void setModeName(String modeName) {
		this.modeName = modeName;
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	@InvokeAnnotate(type = PatternType.UNCOPY)
	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	@InvokeAnnotate(type = PatternType.FK)
	public String getAssMode() {
		return assMode;
	}

	public void setAssMode(String assMode) {
		this.assMode = assMode;
	}

	public String getDepart() {
		return depart;
	}

	public void setDepart(String depart) {
		this.depart = depart;
	}

	public String getAssetUser() {
		return assetUser;
	}

	public void setAssetUser(String assetUser) {
		this.assetUser = assetUser;
	}

	public String getAssUser() {
		return assUser;
	}

	public void setAssUser(String assUser) {
		this.assUser = assUser;
	}

	public String getSysUser() {
		return sysUser;
	}

	public void setSysUser(String sysUser) {
		this.sysUser = sysUser;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUseFlag() {
		return useFlag;
	}

	public void setUseFlag(String useFlag) {
		this.useFlag = useFlag;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getProvide() {
		return provide;
	}

	public void setProvide(String provide) {
		this.provide = provide;
	}

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public String getAssetUserName() {
		return assetUserName;
	}

	public void setAssetUserName(String assetUserName) {
		this.assetUserName = assetUserName;
	}

	public String getAssUserName() {
		return assUserName;
	}

	public void setAssUserName(String assUserName) {
		this.assUserName = assUserName;
	}

	public String getSysUserName() {
		return sysUserName;
	}

	public void setSysUserName(String sysUserName) {
		this.sysUserName = sysUserName;
	}

	public String getUserIdName() {
		return userIdName;
	}

	public void setUserIdName(String userIdName) {
		this.userIdName = userIdName;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

	public String getAssetTypeName() {
		return assetTypeName;
	}

	public void setAssetTypeName(String assetTypeName) {
		this.assetTypeName = assetTypeName;
	}

	public String getMmac() {
		return mmac;
	}

	public void setMmac(String mmac) {
		this.mmac = mmac;
	}

	public String getIloIP() {
		return iloIP;
	}

	public void setIloIP(String iloIP) {
		this.iloIP = iloIP;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getBegDate() {
		return begDate;
	}

	public void setBegDate(Date begDate) {
		this.begDate = begDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Double getLifeYears() {
		return lifeYears;
	}

	public void setLifeYears(Double lifeYears) {
		this.lifeYears = lifeYears;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getRetirementDate() {
		return retirementDate;
	}

	public void setRetirementDate(Date retirementDate) {
		this.retirementDate = retirementDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAssetTypeCode() {
		return assetTypeCode;
	}

	public void setAssetTypeCode(String assetTypeCode) {
		this.assetTypeCode = assetTypeCode;
	}

	public Asset2Drawer getDraw() {
		return draw;
	}

	public void setDraw(Asset2Drawer draw) {
		this.draw = draw;
	}
	
	public BaseEntity getConfig() {
		return config;
	}

	public void setConfig(BaseEntity config) {
		this.config = config;
	}

	public String getStackId() {
		return stackId;
	}

	public void setStackId(String stackId) {
		this.stackId = stackId;
	}

	public List<NetPorts> getNetPorts() {
		return netPorts;
	}

	public void setNetPorts(List<NetPorts> netPorts) {
		this.netPorts = netPorts;
	}

	public String getDrawsId() {
		return drawsId;
	}

	public void setDrawsId(String drawsId) {
		this.drawsId = drawsId;
	}

	public String getUnumb() {
		return unumb;
	}

	public void setUnumb(String unumb) {
		this.unumb = unumb;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	@JsonIgnore
	public Set getAsset2Drawers() {
		return asset2Drawers;
	}

	public void setAsset2Drawers(Set asset2Drawers) {
		this.asset2Drawers = asset2Drawers;
	}
	
	public String getTenantId () {
		return tenantId;
	}
	
	public void setTenantId (String tenantId) {
		this.tenantId = tenantId;
	}
	
	public Integer getCount () {
		return count;
	}
	
	public void setCount (Integer count) {
		this.count = count;
	}
}