package com.h3c.iclouds.junit.rest.cmdb;

import static org.junit.Assert.assertEquals;

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

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseJunitTest;
import com.h3c.iclouds.po.Class2Items;
import com.h3c.iclouds.rest.Class2ItemsRest;

/**
 * @author zhzh 2015-4-7
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath:spring/*.xml" })
// true做数据库回滚，false不做回滚
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class Class2ItemsRestTest extends BaseJunitTest {
	
	private MockHttpServletRequest request;

//	private HttpServletResponse response;

	@Resource
	private Class2ItemsRest class2ItemsRest;
	
	public static void main(String[] args) {
		Class2Items entity = new Class2Items();
		entity.setItemId("test");
		entity.setItemName("itemName");
		entity.setUnum(2);
		entity.setFlag("0");
		System.out.println(JSONObject.toJSONString(entity));
	}
	
	@Before
	public void setUp() {
		request = new MockHttpServletRequest();
		request.setCharacterEncoding("UTF-8");
		response = new MockHttpServletResponse();
	}

	@Override
	public void list() {

	}
	
	String pid = "8a8a726057790ebc0157790edd8a0000";
	
	@SuppressWarnings("unchecked")
	@Override
	@Test
	public void save() {
		Class2Items entity = new Class2Items();
		entity.setItemId("test");
		entity.setItemName("itemName");
		entity.setUnum(2);
		entity.setFlag("0");
		Map<String, Object> map = (Map<String, Object>) class2ItemsRest.save(pid, entity);
		assertEquals("success", map.get("result").toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	@Test
	public void get() {
		String id = "8a8a7260577a1f2101577a1f3e2f0000";
		Map<String, Object> map = (Map<String, Object>) class2ItemsRest.delete(pid, id);
		assertEquals("success", map.get("result").toString());
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub

	}

}
