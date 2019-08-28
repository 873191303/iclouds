package com.h3c.iclouds.po;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Dcheck implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long dcheckId;
	
	private Long druleId;
	
	private Long dhostId;
	
	private Integer type;
	
	private String key;
	
	private String snmpCommunity;
	
	private String ports;
	
	private String snmpv3SecurityName;
	
	private Long snmpv3SecurityLevel;
	
	private String snmpv3AuthPassphrase;
	
	private String snmpv3PrivPassphrase;
	
	private Integer uniq;
	
	private Integer snmpv3AuthProtocol;
	
	private Integer snmpv3PrivProtocol;
	
	private String snmpv3ContextName;
	
	public Long getDcheckId () {
		return dcheckId;
	}
	
	public void setDcheckId (Long dcheckId) {
		this.dcheckId = dcheckId;
	}
	
	public Long getDruleId () {
		return druleId;
	}
	
	public void setDruleId (Long druleId) {
		this.druleId = druleId;
	}
	
	public Long getDhostId () {
		return dhostId;
	}
	
	public void setDhostId (Long dhostId) {
		this.dhostId = dhostId;
	}
	
	public Integer getType () {
		return type;
	}
	
	public void setType (Integer type) {
		this.type = type;
	}
	
	public String getKey () {
		return key;
	}
	
	public void setKey (String key) {
		this.key = key;
	}
	
	public String getSnmpCommunity () {
		return snmpCommunity;
	}
	
	public void setSnmpCommunity (String snmpCommunity) {
		this.snmpCommunity = snmpCommunity;
	}
	
	public String getPorts () {
		return ports;
	}
	
	public void setPorts (String ports) {
		this.ports = ports;
	}
	
	public String getSnmpv3SecurityName () {
		return snmpv3SecurityName;
	}
	
	public void setSnmpv3SecurityName (String snmpv3SecurityName) {
		this.snmpv3SecurityName = snmpv3SecurityName;
	}
	
	public Long getSnmpv3SecurityLevel () {
		return snmpv3SecurityLevel;
	}
	
	public void setSnmpv3SecurityLevel (Long snmpv3SecurityLevel) {
		this.snmpv3SecurityLevel = snmpv3SecurityLevel;
	}
	
	public String getSnmpv3AuthPassphrase () {
		return snmpv3AuthPassphrase;
	}
	
	public void setSnmpv3AuthPassphrase (String snmpv3AuthPassphrase) {
		this.snmpv3AuthPassphrase = snmpv3AuthPassphrase;
	}
	
	public String getSnmpv3PrivPassphrase () {
		return snmpv3PrivPassphrase;
	}
	
	public void setSnmpv3PrivPassphrase (String snmpv3PrivPassphrase) {
		this.snmpv3PrivPassphrase = snmpv3PrivPassphrase;
	}
	
	public Integer getUniq () {
		return uniq;
	}
	
	public void setUniq (Integer uniq) {
		this.uniq = uniq;
	}
	
	public Integer getSnmpv3AuthProtocol () {
		return snmpv3AuthProtocol;
	}
	
	public void setSnmpv3AuthProtocol (Integer snmpv3AuthProtocol) {
		this.snmpv3AuthProtocol = snmpv3AuthProtocol;
	}
	
	public Integer getSnmpv3PrivProtocol () {
		return snmpv3PrivProtocol;
	}
	
	public void setSnmpv3PrivProtocol (Integer snmpv3PrivProtocol) {
		this.snmpv3PrivProtocol = snmpv3PrivProtocol;
	}
	
	public String getSnmpv3ContextName () {
		return snmpv3ContextName;
	}
	
	public void setSnmpv3ContextName (String snmpv3ContextName) {
		this.snmpv3ContextName = snmpv3ContextName;
	}
}
