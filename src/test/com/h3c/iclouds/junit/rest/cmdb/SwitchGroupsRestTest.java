package com.h3c.iclouds.junit.rest.cmdb;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.h3c.iclouds.base.BaseJunitTest;
import com.h3c.iclouds.po.SwitchGroups;
import com.h3c.iclouds.rest.SwitchGroupsRest;

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
public class SwitchGroupsRestTest extends BaseJunitTest {
	
	@Resource
	private SwitchGroupsRest switchGroupsRest;
	
	Log log = LogFactory.getLog(SwitchGroupsRestTest.class);
	
	@Override
//	@Test
	public void save() {
		SwitchGroups entity = new SwitchGroups();
		entity.setStackName("test");
		entity.setRemark("remark");
		Map<String, Object> map = (Map<String, Object>) switchGroupsRest.save(entity);
		log.info(map.toString());
		assertEquals("success", map.get("result").toString());
	}
	
	@Override
//	@Test
	public void list() {
		Map<String, Object> map = (Map<String, Object>) switchGroupsRest.list();
		log.info(map.toString());
		assertEquals("success", map.get("result").toString());
	}
	
	@Override
	@Test
	public void get() {
		String id = "8a8a700d5803c20d015803c2294e0000";
		Map<String, Object> map = (Map<String, Object>) switchGroupsRest.removeLimit(id, "8a8a700d57adf6da0157adf9cf620001");
		log.info(map.toString());
		assertEquals("success", map.get("result").toString());
	}

	@Override
	public void update() {
		
	}

	@Override
	public void delete() {
		
	}

}
