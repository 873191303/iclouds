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
import com.h3c.iclouds.po.Master2Log;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.rest.Master2LogRest;

/**
 * @author zhzh 2015-4-7
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath:spring/*.xml" })
// true做数据库回滚，false不做回滚
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class Master2LogRestTest extends BaseJunitTest {
	
	private MockHttpServletRequest request;

	String pid = "8a8a700d57adf6da0157adfa5be00002";


	@Resource
	private Master2LogRest master2LogRest;
	

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
		Map<String, Object> resultMap = (Map<String, Object>) this.master2LogRest.list(pid);
		System.out.println(resultMap.toString());
		assertEquals("success", resultMap.get("result").toString());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Test
	public void save() {
		Master2Log entity = new Master2Log();
		entity.setId("1");
		entity.setChgCause("1212");
		entity.setFpropValue("1");
		entity.setApropValue("2");
		entity.setChOwner("yyy");
		entity.setGroupId("1");
		Map<String, Object> map = (Map<String, Object>) master2LogRest.save(pid, entity);
		System.out.println(map.toString());
		assertEquals("success", map.get("result").toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	@Test
	public void update() {
		Master2Log entity = new Master2Log();
		entity.setChgCause("6565");
		try {
			Map<String, Object> resultMap = (Map<String, Object>) this.master2LogRest.update(pid, "1", entity);
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
		Map<String, Object> resultMap = (Map<String, Object>) this.master2LogRest.get(pid, "1");
		System.out.println(resultMap.toString());
		assertEquals("success", resultMap.get("result").toString());
	}

	@Override
	@Test
	public void delete() {
		// TODO Auto-generated method stub
		@SuppressWarnings("unchecked")
		Map<String, Object> resultMap = (Map<String, Object>) this.master2LogRest.delete(pid, "1");
		System.out.println(resultMap.toString());
		assertEquals("success", resultMap.get("result").toString());
	}

}
