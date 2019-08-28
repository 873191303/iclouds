package com.h3c.iclouds.junit.rest.cmdb;

import com.h3c.iclouds.base.BaseJunitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath:spring/*.xml" })
// true做数据库回滚，false不做回滚
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class DataImportTest extends BaseJunitTest {
	
	
	@Test
	public void getAllDate() {
		
	}

	
	@Override
	public void save() {
		
	}


	@Override
	public void list() {
		
	}

	@Override
	public void get() {
		
	}

	@Override
	public void update() {
		
	}

	@Override
	public void delete() {
		
	}
	
	

	
	
}
