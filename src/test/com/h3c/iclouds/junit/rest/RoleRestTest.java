package com.h3c.iclouds.junit.rest;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseJunitTest;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.rest.RoleRest;

/**
 * @author zhzh 2015-4-7
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath:spring/*.xml"})
@TransactionConfiguration(defaultRollback = true)
@Transactional
@SuppressWarnings("unchecked")
public class RoleRestTest extends BaseJunitTest {

	@Resource
	private RoleRest roleRest;
	
	public static void main(String[] args) {
		User user = new User();
		user.setDeptId("8a92408456ba52ab0156ba5339330000");
		user.setEmail("");
		user.setPassword("test");
		user.setLoginName("tssres");
		user.setUserName("133112");
		System.out.println(JSONObject.toJSONString(user));
	}

	/**
	 * 
	 * @Title：testLogin
	 * @Description: 测试用户登录
	 */
	
//	@Test
	public void save() {
//		String result = "success";
//		Role role = new Role();
//		role.setProjectId("1122");	// test
//		role.setRoleName("testRole");
//		role.setRoleDesc("desc");
//		role.setFlag("0");
//		this.eqs(result, roleRest.save(role));
		
		User user = new User();
		user.setDeptId("8a92408456ba52ab0156ba5339330000");
		user.setEmail("");
		user.setPassword("test");
		user.setLoginName("tssres");
		user.setUserName("133112");
		System.out.println(JSONObject.toJSONString(user));
	}

	@Override
	@Test
	public void list() {
		Map<String, Object> map = (Map<String, Object>) roleRest.list();
		assertEquals("success", map.get("result").toString());
	}

	@Override
	public void get() {
		String roleId = "8a92408456fd5c800156fd606a6d0001";
		Map<String, Object> map = (Map<String, Object>) this.roleRest.delete(roleId);
		assertEquals("success", map.get("result").toString());
	}

	@Override
	public void update() {

		
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		
	}
}
