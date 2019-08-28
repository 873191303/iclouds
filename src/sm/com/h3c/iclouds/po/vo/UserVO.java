package com.h3c.iclouds.po.vo;

import com.h3c.iclouds.po.User;

/**
 * iyun创建用户时同步创建用户参数
 * Created by yKF7317 on 2017/8/3.
 */
public class UserVO {
	
	private String name;
	
	private String alias;
	
	private String password;
	
	private String telephone;
	
	private String email;
	
	// 授权级别 1-普通用户 2-管理员 3-超级管理员
	private String type;
	
	public static UserVO create(String type, User user) {
		UserVO entity = new UserVO();
		entity.setName(user.getUserName());
		entity.setAlias(user.getLoginName());
		entity.setTelephone(user.getTelephone());
		entity.setEmail(user.getEmail());
		entity.setPassword(user.getPassword());
		entity.setType(type);
		return entity;
	}
	
	public static UserVO create(User user, String password) {
		UserVO entity = new UserVO();
		entity.setName(user.getUserName());
		entity.setAlias(user.getLoginName());
		entity.setTelephone(user.getTelephone());
		entity.setEmail(user.getEmail());
		entity.setPassword(password);
		return entity;
	}
	
	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public String getAlias () {
		return alias;
	}
	
	public void setAlias (String alias) {
		this.alias = alias;
	}
	
	public String getPassword () {
		return password;
	}
	
	public void setPassword (String password) {
		this.password = password;
	}
	
	public String getTelephone () {
		return telephone;
	}
	
	public void setTelephone (String telephone) {
		this.telephone = telephone;
	}
	
	public String getEmail () {
		return email;
	}
	
	public void setEmail (String email) {
		this.email = email;
	}
	
	public String getType () {
		return type;
	}
	
	public void setType (String type) {
		this.type = type;
	}
}
