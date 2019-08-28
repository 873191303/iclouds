package com.h3c.iclouds.po.bean.inside;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.po.bean.BaseBean;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class SaveNovaVmBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = -2170334192988392545L;

	//从cloudos获的创建的主机id
	private String id;
	
	@NotNull
	@ApiModelProperty(value = "资源区域")
	private String azoneId;
	
	private String azoneName;

	@NotNull
	@ApiModelProperty(value = "基础网络")
	private String networkId;
	
	// 操作系统
	@NotNull
	//@CheckPattern(type = PatternType.CONTAINS, values = {"0","1"})
	@ApiModelProperty(value = "操作系统类型")
	private String osType;

	@NotNull
	@ApiModelProperty(value = "镜像")
	private String imageRef;
	
	@Pattern(regexp = "^[a-zA-Z][0-9a-zA-Z_-]{0,31}$")
	@NotNull
	@Length(max = 255)
	@ApiModelProperty(value = "主机名")
	private String hostName;
	
	@ApiModelProperty(value = "密码设置方式")
	private String setPasswordWay;
	
	@ApiModelProperty(value = "密码")
	private String osPasswd;
	
	@ApiModelProperty(value = "cpu核数")
	private Integer vcpus;

	@ApiModelProperty(value = "内存")
	private Integer memory_mb;

	@ApiModelProperty(value = "系统盘")
	private Integer ramdisk_gb;

	@ApiModelProperty(value = "配置规格")
	private String flavorId;

	@ApiModelProperty(value = "绑定公网IP")
	private String isBindPublicIp;

	@ApiModelProperty(value = "公网IP池")
	private String publicIpPool;

	@Max(60)
	@Min(1)
	@ApiModelProperty(value = "使用租期")
	private Long month;

	@Max(5)
	@Min(1)
	@ApiModelProperty(value = "主机数量")
	private Integer count;
	
	@ApiModelProperty(value = "租户id")
	private String projectId;
	
	//预备资源多类型处理
	//private QuotaClass quotaClass;
	
	private JSONObject error;
	
	public Map<String, Object> bean2Map(SaveNovaVmBean bean) {
		Map<String, Object> map= new HashMap<String, Object>();
		map.put("network_id", bean.getNetworkId());
		if (bean.getIsBindPublicIp().equals("private-port")) {
			map.put("name", "private-port");
		}
		
		map.put("admin_state_up", "true");
		return map;
		
	}
	
	public String getNetworkId() {
		return networkId;
	}

	public Long getMonth() {
		return month;
	}

	public void setMonth(Long month) {
		this.month = month;
	}

	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}

	public String getImageRef() {
		return imageRef;
	}

	public void setImageRef(String imageRef) {
		this.imageRef = imageRef;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getFlavorId() {
		return flavorId;
	}

	public void setFlavorId(String flavorId) {
		this.flavorId = flavorId;
	}

	public String getAzoneId() {
		return azoneId;
	}

	public void setAzoneId(String azoneId) {
		this.azoneId = azoneId;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public String getSetPasswordWay() {
		return setPasswordWay;
	}

	public void setSetPasswordWay(String setPasswordWay) {
		this.setPasswordWay = setPasswordWay;
	}

	public String getOsPasswd() {
		return osPasswd;
	}

	public void setOsPasswd(String osPasswd) {
		this.osPasswd = osPasswd;
	}

	public Integer getVcpus() {
		return vcpus;
	}

	public void setVcpus(Integer vcpus) {
		this.vcpus = vcpus;
	}

	public Integer getMemory_mb() {
		return memory_mb;
	}

	public void setMemory_mb(Integer memory_mb) {
		this.memory_mb = memory_mb;
	}

	public String getIsBindPublicIp() {
		return isBindPublicIp;
	}

	public void setIsBindPublicIp(String isBindPublicIp) {
		this.isBindPublicIp = isBindPublicIp;
	}

	public String getPublicIpPool() {
		return publicIpPool;
	}

	public void setPublicIpPool(String publicIpPool) {
		this.publicIpPool = publicIpPool;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getRamdisk_gb() {
		return ramdisk_gb;
	}

	public void setRamdisk_gb(Integer ramdisk_gb) {
		this.ramdisk_gb = ramdisk_gb;
	}

	@Override
	public Map<String, String> getValidatorMap() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getProjectId() {
		return projectId;
	}
	
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public JSONObject getError() {
		return error;
	}
	
	public void setError(JSONObject error) {
		this.error = error;
	}
	
	public String getAzoneName() {
		return azoneName;
	}
	
	public void setAzoneName(String azoneName) {
		this.azoneName = azoneName;
	}
}
