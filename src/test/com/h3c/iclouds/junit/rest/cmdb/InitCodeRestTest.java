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
import com.h3c.iclouds.base.BaseJunitTest;
import com.h3c.iclouds.po.InitCode;
import com.h3c.iclouds.rest.InitCodeRest;

/**
 * @author zhzh 2015-4-7
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath:spring/*.xml" })
// true做数据库回滚，false不做回滚
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class InitCodeRestTest extends BaseJunitTest {
	
	private MockHttpServletRequest request;

//	private HttpServletResponse response;

	@Resource
	private InitCodeRest initCodeRest;
	
	public static void main(String[] args) {
		InitCode entity = new InitCode();
		entity.setCodeTypeId("cmdb.assert.host.type");
		entity.setTypeDesc("资产服务器类型");
		entity.setCodeId("1");
		entity.setCodeName("刀片服务器");
		entity.setCodeSeq(1);
		entity.setStatus("0");
		entity.setEffectiveDate(new Date());
		entity.setExpiryDate(new Date());
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
	
	@Override
	@Test
	public void save() {
		int i = 0;
		InitCode entity = new InitCode();
		entity.setCodeTypeId("cmdb.assert.type");
		entity.setTypeDesc("资产类型");
		entity.setCodeId("server");
		entity.setCodeName("服务器");
		entity.setCodeSeq(++i);
		entity.setStatus("0");
		entity.setEffectiveDate(new Date());
		entity.setExpiryDate(new Date());
		initCodeRest.save(entity).toString();
		
		entity = new InitCode();
		entity.setCodeTypeId("cmdb.assert.type");
		entity.setTypeDesc("资产类型");
		entity.setCodeId("router");
		entity.setCodeName("路由器");
		entity.setCodeSeq(++i);
		entity.setStatus("0");
		entity.setEffectiveDate(new Date());
		entity.setExpiryDate(new Date());
		initCodeRest.save(entity).toString();
		
		entity = new InitCode();
		entity.setCodeTypeId("cmdb.assert.type");
		entity.setTypeDesc("资产类型");
		entity.setCodeId("switch");
		entity.setCodeName("交换机");
		entity.setCodeSeq(++i);
		entity.setStatus("0");
		entity.setEffectiveDate(new Date());
		entity.setExpiryDate(new Date());
		initCodeRest.save(entity).toString();
		
		entity = new InitCode();
		entity.setCodeTypeId("cmdb.assert.type");
		entity.setTypeDesc("资产类型");
		entity.setCodeId("storage");
		entity.setCodeName("存储");
		entity.setCodeSeq(++i);
		entity.setStatus("0");
		entity.setEffectiveDate(new Date());
		entity.setExpiryDate(new Date());
		initCodeRest.save(entity).toString();
		
		entity = new InitCode();
		entity.setCodeTypeId("cmdb.assert.type");
		entity.setTypeDesc("资产类型");
		entity.setCodeId("board");
		entity.setCodeName("板卡");
		entity.setCodeSeq(++i);
		entity.setStatus("0");
		entity.setEffectiveDate(new Date());
		entity.setExpiryDate(new Date());
		initCodeRest.save(entity).toString();
		
		entity = new InitCode();
		entity.setCodeTypeId("cmdb.assert.type");
		entity.setTypeDesc("资产类型");
		entity.setCodeId("other");
		entity.setCodeName("其他");
		entity.setCodeSeq(++i);
		entity.setStatus("0");
		entity.setEffectiveDate(new Date());
		entity.setExpiryDate(new Date());
		initCodeRest.save(entity).toString();
	}

	@SuppressWarnings("unchecked")
	@Override
//	@Test
	public void get() {
		String id = "8a8a726057790ebc0157790edd8a0000";
		Map<String, Object> map = (Map<String, Object>) initCodeRest.get(id);
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
