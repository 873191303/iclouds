package com.h3c.iclouds.junit.rest.cmdb;

import static org.junit.Assert.assertEquals;

import java.util.Date;
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
import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.base.BaseJunitTest;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.Master2Router;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.rest.AsmMasterRest;

/**
 * @author zhzh 2015-4-7
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath:spring/*.xml" })
// true做数据库回滚，false不做回滚
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class AsmMasterRestTest extends BaseJunitTest {
	
	private MockHttpServletRequest request;

//	private HttpServletResponse response;
	

	@Resource
	private AsmMasterRest asmMasterRest;
	
	@SuppressWarnings("unchecked")
	@Override
	@Test
	public void update() {
		AsmMaster entity = new AsmMaster();
		entity.setAssetId("test000101111a0222");
		entity.setSerial("serial0001111a222101");
		entity.setAssetTypeCode("router");
		entity.setAssetUser("989116e3-79a2-426b-bfbe-668165104885");
		entity.setUserId("989116e3-79a2-426b-bfbe-668165104885");
		entity.setAssUser("989116e3-79a2-426b-bfbe-668165104885");
		entity.setSysUser("989116e3-79a2-426b-bfbe-668165104885");
		entity.setProvide("ProvideId");
		entity.setStatus(ConfigProperty.CMDB_ASSET_FLAG2_USE);
		entity.setAssMode("8a8a7260577a333a01577a399b580000");
		entity.setDepart("8a92408456ba52ab0156ba5339330000");
		entity.setBegDate(new Date());
		entity.setIloIP("10.10.10.12");
		String jsonStr = JSONObject.toJSONString(entity);
		Map<String, Object> map = new JSONObject(JSONObject.parseObject(jsonStr));
//		map.put("drawsId", "8a8a700d579df17501579dfa895b0022");
//		map.put("unumb", 14);
		Map<String, Object> resultMap = (Map<String, Object>) this.asmMasterRest.update("8a8a700d581de73301581de752c20000", map);
		System.out.println(resultMap.toString());
		assertEquals("success", resultMap.get("result").toString());
	}
	
	public static void main(String[] args) {
		Master2Router entity = new Master2Router();
		entity.setCpu("ttt");
		entity.setRam("ttt");
		entity.setIpv4RRate("ttt");
		entity.setIpv6RRate("ttt");
		entity.setMpuSlots("ttt");
		entity.setBaseSlots("ttt");
		entity.setSwCapacity("ttt");
		entity.setPacRate("ttt");
		String jsonStr = JSONObject.toJSONString(entity);
		System.out.println(jsonStr);
		
		AsmMaster entity1 = new AsmMaster();
		entity1.setAssetId("test000101a0");
		entity1.setSerial("serial0001a101");
		entity1.setAssetTypeCode("router");
		entity1.setAssetUser("989116e3-79a2-426b-bfbe-668165104885");
		entity1.setUserId("989116e3-79a2-426b-bfbe-668165104885");
		entity1.setAssUser("989116e3-79a2-426b-bfbe-668165104885");
		entity1.setSysUser("989116e3-79a2-426b-bfbe-668165104885");
		entity1.setProvide("ProvideId");
		entity1.setStatus("1");
		entity1.setAssMode("8a8a7260577a333a01577a399b580000");
		entity1.setDepart("8a92408456ba52ab0156ba5339330000");
		entity1.setBegDate(new Date());
		entity1.setIloIP("10.10.0.112");
		String jsonStr22 = JSONObject.toJSONString(entity1);
		Map<String, Object> map = new JSONObject(JSONObject.parseObject(jsonStr22));
		map.put("drawsId", "8a8a700d579df17501579dfa895b0022");
		map.put("unumb", 14);
		
		jsonStr = JSONObject.toJSONString(entity1);
		System.out.println(jsonStr);
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
	public void list() {

	}
	
	String pid = "8a8a726057790ebc0157790edd8a0000";
	
	@SuppressWarnings("unchecked")
	@Override
	public void save() {
		AsmMaster entity = new AsmMaster();
		entity.setAssetId("test000101111a0222");
		entity.setSerial("serial0001111a222101");
		entity.setAssetTypeCode("router");
		entity.setAssetUser("989116e3-79a2-426b-bfbe-668165104885");
		entity.setUserId("989116e3-79a2-426b-bfbe-668165104885");
		entity.setAssUser("989116e3-79a2-426b-bfbe-668165104885");
		entity.setSysUser("989116e3-79a2-426b-bfbe-668165104885");
		entity.setProvide("ProvideId");
		entity.setAssMode("8a8a7260577a333a01577a399b580000");
		entity.setDepart("8a92408456ba52ab0156ba5339330000");
		entity.setBegDate(new Date());
		entity.setStatus(ConfigProperty.CMDB_ASSET_FLAG2_USE);
		entity.setIloIP("10.10.0.112");
		String jsonStr = JSONObject.toJSONString(entity);
		Map<String, Object> map = new JSONObject(JSONObject.parseObject(jsonStr));
		map.put("drawsId", "8a8a700d579df17501579dfa895b0022");
		map.put("unumb", 14);
		Map<String, Object> resultMap = (Map<String, Object>) this.asmMasterRest.save(map);
		System.out.println(resultMap.toString());
		assertEquals("success", resultMap.get("result").toString());
	}
	
	@SuppressWarnings("unchecked")
	public void config() {
		Master2Router entity = new Master2Router();
		entity.setCpu("ttt");
		entity.setRam("ttt");
		entity.setIpv4RRate("ttt");
		entity.setIpv6RRate("ttt");
		entity.setMpuSlots("ttt");
		entity.setBaseSlots("ttt");
		entity.setSwCapacity("ttt");
		entity.setPacRate("ttt");
		String jsonStr = JSONObject.toJSONString(entity);
		Map<String, Object> map = new JSONObject(JSONObject.parseObject(jsonStr));
		String id = "8a8a700d57ae62d40157ae62f5930000";
		Map<String, Object> resultMap = (Map<String, Object>) this.asmMasterRest.config(id, map);
		assertEquals("success", resultMap.get("result").toString());
	}

	@Override
	public void get() {
		
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub

	}

}
