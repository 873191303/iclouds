package com.h3c.iclouds.junit.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.h3c.iclouds.po.User;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes=ParamConfig.class)


public class UserRestTest {
	
	@Autowired
	private User user;
	
	@Test
	public void demo1(){
		System.out.println(user.getLoginName());
	}
}
