package com.h3c.iclouds.junit.rest.cmdb;

import static org.junit.Assert.assertEquals;

import java.util.Date;
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
import com.h3c.iclouds.po.Software;
import com.h3c.iclouds.rest.SoftwareRest;

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
public class SoftwareRestTest extends BaseJunitTest {
	
	@Resource
	private SoftwareRest softwareRest;
	
	Log log = LogFactory.getLog(SoftwareRestTest.class);
	
	@Override
//	@Test
	public void save() {
		Software entity = new Software();
		entity.setShortName("shortName");
		entity.setSname("sname");
		entity.setScode("scode");
		entity.setRemark("remark");
		entity.setSoftwareVersion("softwareVersion");
		entity.setPatch("patch");
		entity.setPosition("position");
		entity.setReleaseDate(new Date());
		entity.setSowner("sowner");
		entity.setTotalAuth(5);
		entity.setTotalUser(5);
		entity.setSupId("supId");
		entity.setStatus("3");
		Map<String, Object> map = (Map<String, Object>) softwareRest.save(entity);
		log.info(map.toString());
		assertEquals("success", map.get("result").toString());
	}
	
	@Override
	@Test
	public void list() {
		Map<String, Object> map = (Map<String, Object>) softwareRest.list();
		log.info(map.toString());
		assertEquals("success", map.get("result").toString());
	}
	
	@Override
//	@Test
	public void get() {
		String id = "8a8a700d579cfdd601579cfdf3610000";
		Map<String, Object> map = (Map<String, Object>) softwareRest.get(id);
		log.info(map.toString());
		assertEquals("success", map.get("result").toString());
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete() {
		

	}

}
