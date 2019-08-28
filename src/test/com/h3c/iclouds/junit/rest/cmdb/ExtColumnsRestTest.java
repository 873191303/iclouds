package com.h3c.iclouds.junit.rest.cmdb;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
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
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.base.BaseJunitTest;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.ExtColumns;
import com.h3c.iclouds.rest.ExtColumnsRest;
import com.h3c.iclouds.utils.InvokeSetForm;

/**
 * @author zhzh 2015-4-7
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath:spring/*.xml" })
// true做数据库回滚，false不做回滚
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class ExtColumnsRestTest extends BaseJunitTest {

	private MockHttpServletRequest request;

//	private HttpServletResponse response;

	@Resource
	private ExtColumnsRest extColumnsRest;

	public static void main(String[] args) {
		ExtColumns entity = new ExtColumns();
		entity.setXcName("name");
		entity.setXcType("type");
		entity.setXcLength(5);
		entity.setDefaultValue("1");
		entity.setSeq(1);
//		entity.setCreatedBy("1");
//		test(entity);
//		System.out.println(JSONObject.toJSONString(entity));
		
		
//		AsmMaster entity1 = new AsmMaster();
//		entity1.setAssMode(null);
//		
//		AsmMaster entity2 = new AsmMaster();
//		entity2.setAssMode("112311");
//		
//		InvokeSetForm.copyFormProperties(entity1, entity2);
//		System.out.println(entity2);
		
		try {
			ttt();
		} catch (MessageException e) {
			System.out.println("DrawsException:" + e.getResultCode());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Exception:" + e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public static void ttt() {
		throw new MessageException(ResultType.parameter_error);
	}

	public static void test(ExtColumns entity) {
		System.out.println(JSONObject.toJSONString(entity));
//		String json = JSONObject.toJSONString(entity);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("xcName", "name");
		map.put("xcType", "type");
		map.put("xcLength", "5");
		map.put("createdDate", "2016-12-12 10:10:10");
		BaseEntity entity1 = JSONObject.parseObject(new JSONObject().toJSONString(), ExtColumns.class);
		System.out.println(JSONObject.toJSONString(entity1));
		
		InvokeSetForm.copyFormProperties(entity1, entity);
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
//	@Test
	public void save() {
		ExtColumns entity = new ExtColumns();
		entity.setAssType(pid);
		entity.setXcName("name");
		entity.setXcType("type");
		entity.setXcLength(5);
		entity.setDefaultValue("1");
		entity.setSeq(1);
		Map<String, Object> map = (Map<String, Object>) extColumnsRest.save(pid, entity);
		assertEquals("success", map.get("result").toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	@Test
	public void get() {
		String id = "8a8a7260577a518501577a51a3640000";
		Map<String, Object> map = (Map<String, Object>) extColumnsRest.delete(pid, id);
		assertEquals("success", map.get("result").toString());
	}

	@Override
	public void update() {

	}

	@Override
	public void delete() {

	}

}
