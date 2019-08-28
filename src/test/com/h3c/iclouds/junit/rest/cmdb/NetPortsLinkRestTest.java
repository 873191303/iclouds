package com.h3c.iclouds.junit.rest.cmdb;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
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
import com.h3c.iclouds.rest.NetPortsLinkRest;

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
public class NetPortsLinkRestTest extends BaseJunitTest {
	
	@Resource
	private NetPortsLinkRest netPortsLinkRest;
	
	Log log = LogFactory.getLog(NetPortsLinkRestTest.class);
	
	@Override
	@Test
	public void save() {
		String id = "1";
		Map<String, Object> saveMap = new HashMap<String, Object>();
		saveMap.put("linkPort", "2");
		saveMap.put("linkType", "access");
		saveMap.put("vlan", "vlan");
		saveMap.put("remark", "remark");
		Map<String, Object> map = (Map<String, Object>) netPortsLinkRest.link(id, saveMap);
		log.info(map.toString());
		assertEquals("success", map.get("result").toString());
	}
	
	@Override
//	@Test
	public void list() {
		Map<String, Object> map = (Map<String, Object>) netPortsLinkRest.list();
		log.info(map.toString());
		assertEquals("success", map.get("result").toString());
	}
	
	@Override
//	@Test
	public void get() {
		
	}

	@Override
	public void update() {
		
	}

	@Override
	public void delete() {
		
	}

}
