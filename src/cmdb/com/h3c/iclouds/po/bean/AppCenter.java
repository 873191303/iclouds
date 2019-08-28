package com.h3c.iclouds.po.bean;

import java.io.Serializable;

public class AppCenter implements Serializable{

	private static final long serialVersionUID = 267966128784796103L;

	private Integer cores;
	
	private Integer use_cores;
	
	private Integer ram;
	
	private Integer use_ram;
	
	private Integer gigabytes;
	
	private Integer use_gigabytes;
	
	private Integer num_app;
	
	private Integer num_db;
	
	private Integer num_nova;
	
	private Integer num_ips;

	public Integer getCores() {
		return cores;
	}

	public void setCores(Integer cores) {
		this.cores = cores;
	}

	public Integer getUse_cores() {
		return use_cores;
	}

	public void setUse_cores(Integer use_cores) {
		this.use_cores = use_cores;
	}

	public Integer getRam() {
		return ram;
	}

	public void setRam(Integer ram) {
		this.ram = ram;
	}

	public Integer getUse_ram() {
		return use_ram;
	}

	public void setUse_ram(Integer use_ram) {
		this.use_ram = use_ram;
	}

	public Integer getGigabytes() {
		return gigabytes;
	}

	public void setGigabytes(Integer gigabytes) {
		this.gigabytes = gigabytes;
	}

	public Integer getUse_gigabytes() {
		return use_gigabytes;
	}

	public void setUse_gigabytes(Integer use_gigabytes) {
		this.use_gigabytes = use_gigabytes;
	}

	public Integer getNum_app() {
		return num_app;
	}

	public void setNum_app(Integer num_app) {
		this.num_app = num_app;
	}

	public Integer getNum_db() {
		return num_db;
	}

	public void setNum_db(Integer num_db) {
		this.num_db = num_db;
	}

	public Integer getNum_nova() {
		return num_nova;
	}

	public void setNum_nova(Integer num_nova) {
		this.num_nova = num_nova;
	}

	public Integer getNum_ips() {
		return num_ips;
	}

	public void setNum_ips(Integer num_ips) {
		this.num_ips = num_ips;
	} 
	
	
	
	
	
	
	
	
}
