package com.h3c.iclouds.junit.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.comet4j.core.util.JSONUtil;
import org.junit.Test;

import com.h3c.iclouds.po.bean.AppInfo;
import com.h3c.iclouds.po.bean.ApplicationBean;
import com.h3c.iclouds.po.ApplicationMaster;
import com.h3c.iclouds.po.FloatingIp;
import com.h3c.iclouds.utils.JacksonUtil;

public class AppRestTest {
	@Test
	public void add() {
		ApplicationMaster applicationMaster=new ApplicationMaster("test", "mode", "dns", "www.baidu.com", "8080", "0", "ff8080815819e81801581dabee9d0034", "remark");
		applicationMaster.setAppName("test");
		System.out.println(JacksonUtil.toJSon(applicationMaster));
	}
	@Test
	public void update() {
		String appId="21b8873a-f6e0-4322-86a2-1875eef37784";
		AppInfo appInfo=new AppInfo();
		appInfo.setAppId(appId);
		List<ApplicationBean> data=new ArrayList<>();
		ApplicationBean bean1=new ApplicationBean();
		//bean1.setAppName("应用一");
		bean1.setId(appId);
		List<String> pids=Arrays.asList(new String[]{"-1"});
		bean1.setPid(pids);
		bean1.setOption("2");
		//bean1.setSequence("0");
		bean1.setType("0");
		Map<String, Object> data1=new HashMap<>();
		data1.put("appName", "被修改的应用");
		bean1.setData(data1);
		
		ApplicationBean bean2=new ApplicationBean();
		//bean1.setAppName("应用一");
		String id2=UUID.randomUUID().toString();
		bean2.setId(id2);
		List<String> pids2=Arrays.asList(new String[]{appId});
		bean2.setPid(pids2);
		bean2.setOption("1");
		//bean2.setSequence("1");
		bean2.setType("12");
		Map<String, Object> data2=new HashMap<>();
		data2.put("cname", "新增的中间件集群1");
		data2.put("relation", "12");
		bean2.setData(data2);
		
		ApplicationBean bean21=new ApplicationBean();
		//bean1.setAppName("应用一");
		String id21=UUID.randomUUID().toString();
		bean21.setId(id21);
		List<String> pids21=Arrays.asList(new String[]{appId});
		bean21.setPid(pids21);
		bean21.setOption("1");
		//bean21.setSequence("1");
		bean21.setType("12");
		Map<String, Object> data21=new HashMap<>();
		data21.put("cname", "新增的中间件集群2");
		data21.put("relation", "12");
		bean21.setData(data21);
		
		ApplicationBean bean3=new ApplicationBean();
		//bean1.setAppName("应用一");
		String id3=UUID.randomUUID().toString();
		bean3.setId(id3);
		List<String> pids3=Arrays.asList(new String[]{id2});
		bean3.setPid(pids3);
		bean3.setOption("1");
		//bean3.setSequence("2");
		bean3.setType("2");
		Map<String, Object> data3=new HashMap<>();
		data3.put("name", "新增的中间件");
		bean3.setData(data3);
		
		ApplicationBean bean4=new ApplicationBean();
		String id4=UUID.randomUUID().toString();
		//bean1.setAppName("应用一");
		bean4.setId(id4);
		List<String> pids4=Arrays.asList(new String[]{id2,id21});
		bean4.setPid(pids4);
		bean4.setOption("1");
		//bean4.setSequence("2");
		bean4.setType("2");
		Map<String, Object> data4=new HashMap<>();
		data4.put("name", "新增的中间件");
		bean4.setData(data4);
		
		ApplicationBean bean5=new ApplicationBean();
		//bean1.setAppName("应用一");
		String id5=UUID.randomUUID().toString();
		bean5.setId(id5);
		List<String> pids5=Arrays.asList(new String[]{id4});
		bean5.setPid(pids5);
		bean5.setOption("1");
		//bean5.setSequence("2");
		bean5.setType("13");
		Map<String, Object> data5=new HashMap<>();
		data5.put("cname", "新增的数据库集群");
		bean5.setData(data5);
		
		ApplicationBean bean6=new ApplicationBean();
		//bean1.setAppName("应用一");
		String id6=UUID.randomUUID().toString();
		bean6.setId(id6);
		List<String> pids6=Arrays.asList(new String[]{id5});
		bean6.setPid(pids6);
		bean6.setOption("1");
		//bean5.setSequence("2");
		bean6.setType("3");
		Map<String, Object> data6=new HashMap<>();
		data6.put("dbname", "新增的数据库");
		bean6.setData(data6);
		
		data.add(bean6);
		data.add(bean5);
		data.add(bean4);
		data.add(bean3);
		data.add(bean2);
		data.add(bean1);
		data.add(bean21);
		appInfo.setData(data);
		System.out.println(JSONUtil.convertToJson(appInfo));
		//System.out.println(JacksonUtil.toJSon(appInfo));
	}
	public static void main(String[] args) throws InterruptedException {
		//System.out.println("novavm123".substring("novavm".length()));;
		FloatingIp floatingIp=new FloatingIp();
		floatingIp.setId("8a9240a75ab2ff78015ab5e31c230032");
		floatingIp.setFloatingPortId("8a9240a75ab2ff78015ab5e31c1e0031");
	}
	
}
