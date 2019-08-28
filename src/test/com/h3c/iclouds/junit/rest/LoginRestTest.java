package com.h3c.iclouds.junit.rest;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.rest.LoginRest;

/**
 * @author zhzh 2015-4-7
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath:spring/*.xml"})
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class LoginRestTest  {

	private MockHttpServletRequest request;
	
	@SuppressWarnings("unused")
	private HttpServletResponse response;

	@Resource
	private LoginRest loginRest;

	@Before
	public void setUp() {
		request = new MockHttpServletRequest();
		request.setCharacterEncoding("UTF-8");
		response = new MockHttpServletResponse();
	}

	/**
	 * 
	 * @Title：testLogin
	 * @Description: 测试用户登录
	 */
	@Test
	public void testLogin() {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("loginName", "admin");
			map.put("password", "admin");
			String jsonStr = JSONObject.toJSONString(map);
			System.out.println(jsonStr);
			//assertEquals("{result=success}", loginRest.login(response, map).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
