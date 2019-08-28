package com.h3c.iclouds.junit.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseJunitTest;
import com.h3c.iclouds.biz.PrdClassBiz;
import com.h3c.iclouds.biz.RequestItemsBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.po.business.PrdClass;
import com.h3c.iclouds.po.business.RequestItems;
import com.h3c.iclouds.rest.RequestMasterRest;
import com.h3c.iclouds.utils.DateUtils;
import com.h3c.iclouds.utils.MailUtils;
import com.h3c.iclouds.utils.StrUtils;

/**
 * @author zhzh 2015-4-7
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath:spring/*.xml"})
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class MasterRestTest extends BaseJunitTest {

	@Resource
	private RequestMasterRest masterRest;
	
	public static void main(String[] args) {
		JSONObject saveMap = new JSONObject();
		JSONObject master = new JSONObject();
		saveMap.put("master", master);
		master.put("reqCode", "reqCode");
		master.put("step", "1");
		master.put("responsible", "responsible");
		master.put("projectName", "projectName");
		master.put("isSign", "0");
		master.put("contract", "contract");
		master.put("amount", "2.5");
		master.put("projectDesc", "projectDesc");
		master.put("cusId", "ff8080815721ae5801572216b2df0005");
		master.put("contact", "contact");
		master.put("iphone", "iphone");
		master.put("email", "email");
		master.put("workFlowId", "1");
		
		JSONArray items = new JSONArray();
		saveMap.put("items", items);
		JSONObject item = new JSONObject();
		
		item.put("nums", "2");
		item.put("classId", "1");
		item.put("reqType", "1");
		
		JSONObject ajson = new JSONObject();
		ajson.put("vcpu", "1");
		ajson.put("ram", "2048");
		
		item.put("ajson", ajson.toJSONString());
		items.add(item);
		
		
		System.out.println(saveMap.toJSONString());
	}
	
	@Test
	public void save() {
//		String result = "success";
		Map<String, Object> saveMap = new HashMap<String, Object>();
		Map<String, Object> master = new HashMap<String, Object>();
		saveMap.put("master", master);
		master.put("reqCode", "reqCode");
		master.put("step", "1");
		master.put("responsible", "responsible");
		master.put("projectName", "projectName");
		master.put("isSign", "0");
		master.put("contract", "contract");
		master.put("amount", "2.5");
		master.put("projectDesc", "projectDesc");
		master.put("cusId", "ff8080815721ae5801572216b2df0005");
		master.put("contact", "contact");
		master.put("iphone", "iphone");
		master.put("email", "email");
//		this.eqs(result, this.masterRest.save(saveMap));
//		
//		
//		
//		Role role = new Role();
//		role.setProjectId("1122");	// test
//		role.setRoleName("testRole");
//		role.setRoleDesc("desc");
//		role.setFlag("0");
//		this.eqs(result, roleRest.save(role));
		
		
	}
	@Resource
	private PrdClassBiz prdClassBiz;
	@Resource
	private UserBiz userBiz;
	@Resource
	private RequestItemsBiz requestItemsBiz;
	@Test
	public void mail(){
		System.out.println("into mail");
		StringBuffer mailContent=new StringBuffer();
		String appName="申请人";
		mailContent.append("需求办理信息:<br><br>")
		.append("<table border='1' style='border:1px solid black;text-align:center;'>").
		append("<tr style='background:#F2F2F2;height:60px;'><td>序号</td><td>申请类型</td><td>流程编号</td>"
				+ "<td>申请时间</td><td>办理状态</td><td>申请人</td><td>链接</td></tr>");
		System.out.println("table template");
		System.out.println(mailContent);
		System.out.println("find items");
		List<RequestItems> items = requestItemsBiz.findByPropertyName(RequestItems.class, "reqId", "ff8080815c18f973015c2f5a5dc3026b");
		StringBuffer name=new StringBuffer();
		if(StrUtils.checkParam(items)){
			System.out.println(items);
			for(RequestItems item:items){
				System.out.println("find prd name");
				PrdClass prdClass=prdClassBiz.findById(PrdClass.class, item.getClassId());
				if(StrUtils.checkParam(prdClass)){
					name.append(prdClass.getClassName()+",");
				}
			}
			
		}
		System.out.println(name);
		String n="";
		if(StrUtils.checkParam(name.toString())){
			n=name.toString().substring(0, name.length()-1);
		}
		System.out.println(n);
		
		mailContent.append("<tr style='height:60px;'><td>1</td><td>"+n+"</td><td>"+"20170523005"+"</td>"
				+ "<td>"+DateUtils.getDate(DateUtils.dateFormat)+"</td><td>"+ConfigProperty.MASTER_STEP4_CONTROL+"</td>"
				+ "<td>"+appName+"</td><td><a href=''>进入</a></td></tr>");
		
		System.out.println(mailContent);
		MailUtils.sendEmail("kf.yujiaju@h3c.com", "test", mailContent.toString());
	}
	
	
	@Override
	@Test
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
