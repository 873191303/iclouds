package com.h3c.iclouds.junit.rest;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.h3c.iclouds.base.BaseJunitTest;
import com.h3c.iclouds.dao.Network2SubnetDao;
import com.h3c.iclouds.dao.Project2NetworkDao;
import com.h3c.iclouds.dao.Project2QuotaDao;
import com.h3c.iclouds.dao.ProjectDao;
import com.h3c.iclouds.dao.QuotaClassDao;
import com.h3c.iclouds.dao.QuotaUsedDao;
import com.h3c.iclouds.dao.UserDao;
import com.h3c.iclouds.po.Project2Quota;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath:spring/*.xml"})
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class Project2NetworkTest extends BaseJunitTest {
	
	@Resource
	private Project2NetworkDao project2NetworkDao;
	@Resource
	private Network2SubnetDao network2SubnetDao;
	
	
	@Resource
	private ProjectDao projectDao;

	@Resource
	private QuotaClassDao quotaClassDao;

	@Resource
	private Project2QuotaDao project2QuotaDao;

	@Resource
	private QuotaUsedDao quotaUsedDao;

	@Resource
	private UserDao userDao;
	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void list() {
		// TODO Auto-generated method stub
		
	}
	@Test
	@Override
	public void get() {
		List<Project2Quota> list = project2QuotaDao.findByPropertyName(Project2Quota.class, "tenantId",
				"1");
		System.out.println(list);
		
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
