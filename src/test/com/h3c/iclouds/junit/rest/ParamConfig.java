package com.h3c.iclouds.junit.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.h3c.iclouds.po.User;

@Configuration
@EnableAspectJAutoProxy
public class ParamConfig {

	@Bean
	public User getSaveUser(){
		User user=new User();
		//user.setCreatedBy("123");
		user.setLoginName("yzl_admin123");
		user.setUserName("用户名");
		user.setDeptId("用户id");
		return user;
		
	}
}
