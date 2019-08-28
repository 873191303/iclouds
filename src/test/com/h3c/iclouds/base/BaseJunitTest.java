package com.h3c.iclouds.base;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.h3c.iclouds.auth.CacheSingleton;

public abstract class BaseJunitTest extends AbstractJUnit4SpringContextTests {
	
	public BaseJunitTest() {
		CacheSingleton.getInstance();
		CacheSingleton.getInstance().setJUnit(true);
	}
	
	public HttpServletRequest request;
	
	public HttpServletResponse response;

	@Before
	public void setUp() throws UnsupportedEncodingException {
		request = new MockHttpServletRequest();
		request.setCharacterEncoding("");
		response = new MockHttpServletResponse();
	}
	
	public <T> T getBean(Class<T> type) {
		return applicationContext.getBean(type);
		}
		 
		public Object getBean(String beanName) {
		return applicationContext.getBean(beanName);
		}
		 
		protected ApplicationContext getContext() {
		return applicationContext;
		}
	public abstract void save();
	
	public abstract void list();
	
	public abstract void get();
	
	public abstract void update();
	
	public abstract void delete();
	
	@SuppressWarnings("unchecked")
	public void eqs(String result, Object obj) {
		Map<String, Object> map = (Map<String, Object>) obj;
		System.out.println(map.toString());
		assertEquals("success", map.get("result").toString());
	}
}
