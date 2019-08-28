package com.h3c.iclouds.junit.rest.cmdb;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import com.h3c.iclouds.po.Rooms;
import com.h3c.iclouds.rest.RoomsRest;

/**
 * @author zhzh 2015-4-7
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath:spring/*.xml" })
// true做数据库回滚，false不做回滚
@TransactionConfiguration(defaultRollback = false)
@Transactional
@SuppressWarnings("unchecked")
public class RoomsRestTest extends BaseJunitTest {
	
	private MockHttpServletRequest request;

//	private HttpServletResponse response;
	@Resource
	private RoomsRest roomsRest;
	
	Log log = LogFactory.getLog(RoomsRestTest.class);
	
	public static void main(String[] args) {
		Rooms entity = new Rooms();
		entity.setRoomName("roomName");
		entity.setShortName("shortName");
		entity.setMaxCols(5);
		entity.setMaxRows(5);
		entity.setDefaultU(43);
		entity.setCapacity(2000);
		entity.setTelephone("1111111");
		entity.setLocalAdminTel("localAdminTel");
		entity.setAdmin("admin");
		entity.setLocalAdmin("localAdmin");
		entity.setLocalAdminTel("localAdminTel");
		entity.setRegion("region");
		entity.setAddress("address");
		entity.setRemark("remark");
		System.out.println(JSONObject.toJSONString(entity));
	}

	@Before
	public void setUp() {
		request = new MockHttpServletRequest();
		request.setCharacterEncoding("UTF-8");
		response = new MockHttpServletResponse();
	}

	@Override
	@Test
	public void list() {
		Map<String, Object> map = (Map<String, Object>) roomsRest.list();
		log.info(map.toString());
		assertEquals("success", map.get("result").toString());
	}
	
	@Override
//	@Test
	public void save() {
		Rooms entity = new Rooms();
		entity.setRoomName("roomName");
		entity.setShortName("shortName");
		entity.setMaxCols(5);
		entity.setMaxRows(5);
		entity.setDefaultU(43);
		entity.setCapacity(2000);
		entity.setTelephone("1111111");
		entity.setLocalAdminTel("localAdminTel");
		entity.setAdmin("admin");
		entity.setLocalAdmin("localAdmin");
		entity.setLocalAdminTel("localAdminTel");
		entity.setRegion("region");
		entity.setAddress("address");
		entity.setRemark("remark");
		Map<String, Object> map = (Map<String, Object>) roomsRest.save(entity);
		log.info(map.toString());
		assertEquals("success", map.get("result").toString());
	}

	@Override
//	@Test
	public void get() {
		String id = "8a8a700d579cfdd601579cfdf3610000";
		Map<String, Object> map = (Map<String, Object>) roomsRest.get(id);
		log.info(map.toString());
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
