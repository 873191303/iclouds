package com.h3c.iclouds.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 服务器连接存储作为上联口，其他作为下联口
 * 存储只允许作为下联口
 * 交换机、路由器可以连接自身网口
 * @author zkf5485
 *
 */
@SuppressWarnings("rawtypes")
@ApiModel(value = "资产管理网口配置信息", description = "资产管理网口配置信息")
public class NetPorts extends BaseEntity implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private String id;

	@Length(max = 50)
	@ApiModelProperty(value = "资产编号,不需要传递")
	private String masterId;
	
	@NotNull
	@Min(value = 0)
	@Max(value = 32767)
	@ApiModelProperty(value = "网口序号 (NotNull) (Min >= 0)")
	private Integer seq;

	@Length(max = 50)
	@ApiModelProperty(value = "MAC地址")
	@Pattern(regexp = "^([0-9a-fA-F]{2})(([/\\s:-][0-9a-fA-F]{2}){5})$")
	private String mac;
	
	@NotNull
	@CheckPattern(type = PatternType.CONTAINS, values = {"1", "2", "0"})
	@ApiModelProperty(value = "端口类型 (NotNull) (Contain: 1-电口; 2-光口; 0 未知)")
	private String portType;
	
	@NotNull
	@CheckPattern(type = PatternType.CONTAINS, values = {"1", "2", "0"})
	@ApiModelProperty(value = "网口类型 (NotNull) (Contain: 1-物理网口; 2-虚拟网口; 0 未知)")
	private String ethType;

	@Length(max = 600)
	@ApiModelProperty(value = "备注")
	private String remark;
	
	@ApiModelProperty(value = "网口连接类型")
	private String linkType;
	
	@ApiModelProperty(value = "access连接id")
	private String accessTo;

	@ApiModelProperty(value = "trunk连接id")
	private String trunkTo;
	
	@ApiModelProperty(value = "access连接vlan")
	private String accessVlan;
	
	@ApiModelProperty(value = "trunk连接vlan")
	private String trunkVlan;
	
	@ApiModelProperty(value = "access连接序号")
	private String accessSeq;
	
	@ApiModelProperty(value = "access连接mac")
	private String accessMac;
	
	@ApiModelProperty(value = "access连接名称")
	private String accessName;
	
	@ApiModelProperty(value = "trunk连接序号")
	private String trunkSeq;
	
	@ApiModelProperty(value = "trunk连接mac")
	private String trunkMac;
	
	@ApiModelProperty(value = "trunk连接名称")
	private String trunkName;
	
	@ApiModelProperty(value = "trunk连接资源id")
	private String trunkMasterId;
	
	@ApiModelProperty(value = "access连接资源id")
	private String accessMasterId;
	
	private Set accessLink = new HashSet<Draws>();
	
	private Set trunkLink = new HashSet<Draws>();
	
	private List<IpRelation> ips;

	@ApiModelProperty(value = "设备名称")
	private String assetName;

	@ApiModelProperty(value = "设备序列号")
	private String serial;

	public NetPorts() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMasterId() {
		return masterId;
	}

	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getPortType() {
		return portType;
	}

	public void setPortType(String portType) {
		this.portType = portType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<IpRelation> getIps() {
		return ips;
	}

	public void setIps(List<IpRelation> ips) {
		this.ips = ips;
	}

	@JsonIgnore
	public Set getAccessLink() {
		return accessLink;
	}

	public void setAccessLink(Set accessLink) {
		this.accessLink = accessLink;
	}

	@JsonIgnore
	public Set getTrunkLink() {
		return trunkLink;
	}

	public void setTrunkLink(Set trunkLink) {
		this.trunkLink = trunkLink;
	}

	public String getEthType() {
		return ethType;
	}

	public void setEthType(String ethType) {
		this.ethType = ethType;
	}

	public String getLinkType() {
		return linkType;
	}

	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

	public String getAccessTo() {
		return accessTo;
	}

	public void setAccessTo(String accessTo) {
		this.accessTo = accessTo;
		if(accessTo != null) {
			this.linkType = ConfigProperty.CMDB_NETPORT_LINK_ACCESS;
		}
	}

	public String getTrunkTo() {
		return trunkTo;
	}

	public void setTrunkTo(String trunkTo) {
		this.trunkTo = trunkTo;
		if(trunkTo != null) {
			this.linkType = ConfigProperty.CMDB_NETPORT_LINK_TRUNK;
		}
	}

	public String getAccessVlan() {
		return accessVlan;
	}

	public void setAccessVlan(String accessVlan) {
		this.accessVlan = accessVlan;
	}

	public String getTrunkVlan() {
		return trunkVlan;
	}

	public void setTrunkVlan(String trunkVlan) {
		this.trunkVlan = trunkVlan;
	}

	public String getAccessSeq() {
		return accessSeq;
	}

	public void setAccessSeq(String accessSeq) {
		this.accessSeq = accessSeq;
	}

	public String getAccessMac() {
		return accessMac;
	}

	public void setAccessMac(String accessMac) {
		this.accessMac = accessMac;
	}

	public String getTrunkSeq() {
		return trunkSeq;
	}

	public void setTrunkSeq(String trunkSeq) {
		this.trunkSeq = trunkSeq;
	}

	public String getTrunkMac() {
		return trunkMac;
	}

	public void setTrunkMac(String trunkMac) {
		this.trunkMac = trunkMac;
	}

	public String getTrunkName() {
		return trunkName;
	}

	public void setTrunkName(String trunkName) {
		this.trunkName = trunkName;
	}
	
	public String getAccessName() {
		return accessName;
	}

	public void setAccessName(String accessName) {
		this.accessName = accessName;
	}

	public String getTrunkMasterId() {
		return trunkMasterId;
	}

	public void setTrunkMasterId(String trunkMasterId) {
		this.trunkMasterId = trunkMasterId;
	}

	public String getAccessMasterId() {
		return accessMasterId;
	}

	public void setAccessMasterId(String accessMasterId) {
		this.accessMasterId = accessMasterId;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}
}
