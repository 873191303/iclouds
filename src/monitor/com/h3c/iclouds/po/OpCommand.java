package com.h3c.iclouds.po;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class OpCommand implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long operationId;

	private Integer type;

	private Long scriptId;

	private Integer executeOn;
	
	private String port;
	
	private Integer authType;
	
	private String userName;
	
	private String password;
	
	private String publicKey;
	
	private String privateKey;
	
	private String command;
	
	public Long getOperationId () {
		return operationId;
	}
	
	public void setOperationId (Long operationId) {
		this.operationId = operationId;
	}
	
	public Integer getType () {
		return type;
	}
	
	public void setType (Integer type) {
		this.type = type;
	}
	
	public Long getScriptId () {
		return scriptId;
	}
	
	public void setScriptId (Long scriptId) {
		this.scriptId = scriptId;
	}
	
	public Integer getExecuteOn () {
		return executeOn;
	}
	
	public void setExecuteOn (Integer executeOn) {
		this.executeOn = executeOn;
	}
	
	public String getPort () {
		return port;
	}
	
	public void setPort (String port) {
		this.port = port;
	}
	
	public Integer getAuthType () {
		return authType;
	}
	
	public void setAuthType (Integer authType) {
		this.authType = authType;
	}
	
	public String getUserName () {
		return userName;
	}
	
	public void setUserName (String userName) {
		this.userName = userName;
	}
	
	public String getPassword () {
		return password;
	}
	
	public void setPassword (String password) {
		this.password = password;
	}
	
	public String getPublicKey () {
		return publicKey;
	}
	
	public void setPublicKey (String publicKey) {
		this.publicKey = publicKey;
	}
	
	public String getPrivateKey () {
		return privateKey;
	}
	
	public void setPrivateKey (String privateKey) {
		this.privateKey = privateKey;
	}
	
	public String getCommand () {
		return command;
	}
	
	public void setCommand (String command) {
		this.command = command;
	}
}
