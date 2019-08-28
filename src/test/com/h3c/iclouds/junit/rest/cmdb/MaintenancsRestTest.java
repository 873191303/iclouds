package com.h3c.iclouds.junit.rest.cmdb;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;

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

import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.base.BaseJunitTest;
import com.h3c.iclouds.po.Maintenancs;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.rest.MaintenancsRest;

/**
 * @author zhzh 2015-4-7
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath:spring/*.xml" })
// true做数据库回滚，false不做回滚
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class MaintenancsRestTest extends BaseJunitTest {
	
	private MockHttpServletRequest request;

	@Resource
	private MaintenancsRest maintenancsRest;

	String pid = "8a8a700d57adf6da0157adfa5be00002";
	

	public static void main(String[] args) {
	}
	
	@Before
	public void setUp() {
		request = new MockHttpServletRequest();
		request.setCharacterEncoding("UTF-8");
		response = new MockHttpServletResponse();
//		HttpSession session = request.getSession();
		SessionBean sessionBean = new SessionBean(new User(), "test");
//		session.setAttribute(SessionBean.USER_SESSION, sessionBean);
		sessionBean.setUserId("junitTest");
	}

	@Override
	@Test
	public void list() {
		@SuppressWarnings("unchecked")
		Map<String, Object> resultMap = (Map<String, Object>) this.maintenancsRest.list(pid);
		System.out.println(resultMap.toString());
		assertEquals("success", resultMap.get("result").toString());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Test
	public void save() {
		Maintenancs maintenancs = new Maintenancs();
		maintenancs.setId("1");
		maintenancs.setContact("1212");
		maintenancs.setGroupId("1");
		Map<String, Object> resultMap = (Map<String, Object>) this.maintenancsRest.save(pid, maintenancs);
		System.out.println(resultMap.toString());
		assertEquals("success", resultMap.get("result").toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	@Test
	public void update() {
		Maintenancs maintenancs = new Maintenancs();
		maintenancs.setContact("3434");
		Map<String, Object> resultMap = null;
		try {
			resultMap = (Map<String, Object>) this.maintenancsRest.update(pid, "1", maintenancs);
			System.out.println(resultMap.toString());
			assertEquals("success", resultMap.get("result").toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	@Test
	public void get() {
		@SuppressWarnings("unchecked")
		Map<String, Object> resultMap = (Map<String, Object>) this.maintenancsRest.get(pid, "1");
		System.out.println(resultMap.toString());
		assertEquals("success", resultMap.get("result").toString());
	}

	@Override
	@Test
	public void delete() {
		// TODO Auto-generated method stub
		@SuppressWarnings("unchecked")
		Map<String, Object> resultMap = (Map<String, Object>) this.maintenancsRest.delete(pid, "1");
		System.out.println(resultMap.toString());
		assertEquals("success", resultMap.get("result").toString());
	}

}
