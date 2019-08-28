package com.h3c.iclouds.junit.rest;

import java.util.HashMap;
import java.util.Map;

import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.FloatingIp;
import com.h3c.iclouds.utils.JacksonUtil;

public class FloatingRestTest {

	
	public static void main(String[] args) throws InterruptedException {
		//System.out.println("novavm123".substring("novavm".length()));;
		FloatingIp floatingIp=new FloatingIp();
		floatingIp.setId("8a9240a75ab2ff78015ab5e31c230032");
		floatingIp.setFloatingPortId("8a9240a75ab2ff78015ab5e31c1e0031");
		floatingIp.setFlag("1");
		//floatingIp.setPortId("ff8080815ab63638015ab79d23b8014d");
		System.out.println(JacksonUtil.toJSon(floatingIp));
		CloudosClient client=CloudosClient.createAdmin();
		Map<String, Object> queryMap =  new HashMap<>();
        Map<String, String> innerMap = new HashMap<>();
	    innerMap.put("port_id", "8ed37171-f327-4fc2-be12-4b23916bd29a");
        //innerMap.put("port_id", null);
	    queryMap.put("floatingip", innerMap);
	    //client.put("/v2.0/floatingips/3203203c-455b-4f0b-a31b-e2cb5e2f500d", queryMap);
	    client.get("/v2.0/floatingips/3203203c-455b-4f0b-a31b-e2cb5e2f500d");
	}
}
