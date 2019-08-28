package com.h3c.iclouds.po;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by ykf7317 on 2017/8/25.
 */
public class VmTop implements Serializable {
	
	private String uuid;
	
	private String id;
	
	private String hostname;
	
	private Integer vcpus;
	
	private Integer memory;
	
	private Integer ramdisk;
	
	private String owner;
	
	private String projectid;
	
	private Float cpurate;
	
	private Float memrate;
	
	private Timestamp collecttime;
	
	public String getUuid () {
		return uuid;
	}
	
	public void setUuid (String uuid) {
		this.uuid = uuid;
	}
	
	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}
	
	public String getHostname () {
		return hostname;
	}
	
	public void setHostname (String hostname) {
		this.hostname = hostname;
	}
	
	public Integer getVcpus () {
		return vcpus;
	}
	
	public void setVcpus (Integer vcpus) {
		this.vcpus = vcpus;
	}
	
	public Integer getMemory () {
		return memory;
	}
	
	public void setMemory (Integer memory) {
		this.memory = memory;
	}
	
	public Integer getRamdisk () {
		return ramdisk;
	}
	
	public void setRamdisk (Integer ramdisk) {
		this.ramdisk = ramdisk;
	}
	
	public String getOwner () {
		return owner;
	}
	
	public void setOwner (String owner) {
		this.owner = owner;
	}
	
	public String getProjectid () {
		return projectid;
	}
	
	public void setProjectid (String projectid) {
		this.projectid = projectid;
	}
	
	public Float getCpurate () {
		return cpurate;
	}
	
	public void setCpurate (Float cpurate) {
		this.cpurate = cpurate;
	}
	
	public Float getMemrate () {
		return memrate;
	}
	
	public void setMemrate (Float memrate) {
		this.memrate = memrate;
	}
	
	public Timestamp getCollecttime () {
		return collecttime;
	}
	
	public void setCollecttime (Timestamp collecttime) {
		this.collecttime = collecttime;
	}
	
}
