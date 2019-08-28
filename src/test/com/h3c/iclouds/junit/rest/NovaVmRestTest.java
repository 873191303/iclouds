package com.h3c.iclouds.junit.rest;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.po.bean.inside.SaveNovaVmBean;
import com.h3c.iclouds.po.bean.nova.Server;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.JacksonUtil;
import com.h3c.iclouds.utils.StrUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath:spring/*.xml" })
// true做数据库回滚，false不做回滚
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class NovaVmRestTest {

	@Test
	public void getByid() {
		System.out.println("novavm".substring(1));;
		// novaVmDao.findById(NovaVm.class, "2");
		// NovaVm novaVm =(NovaVm)
		// novaVmDao.findByPropertyName(NovaVm.class,"uuid","5d24d10f-d186-42d4-b43a-83299ac24f4c");
		//NovaVm novaVm = (NovaVm) novaVmDao.queryObject("id", "5d24d10f-d186-42d4-b43a-83299ac24f4c");
	}

	public static void main(String[] args) throws InterruptedException {
		
		SaveNovaVmBean saveNovaVmBean = new SaveNovaVmBean();
		saveNovaVmBean.setAzoneId("086d3078-fa6a-46a3-abaf-417f6c4a0bdb");
		saveNovaVmBean.setNetworkId("097bd323-5f3f-4ee1-8125-83658ea2e9fa");
		saveNovaVmBean.setOsType("操作系统类型");
		saveNovaVmBean.setImageRef("2fd3a37d-fbf2-4f89-89a6-b469e095b0db");
		saveNovaVmBean.setHostName("test");
		saveNovaVmBean.setSetPasswordWay("密码设置方式");
		saveNovaVmBean.setOsPasswd("123");
		// cpu
		saveNovaVmBean.setVcpus(10);
		saveNovaVmBean.setMemory_mb(20);
		// ramdisk
		saveNovaVmBean.setRamdisk_gb(10);
		saveNovaVmBean.setFlavorId("b8584e87-805f-4470-86be-d4ff8b232714");
		// 06a58545-3416-4ec8-9a52-52bffcc86366
		saveNovaVmBean.setIsBindPublicIp("公网IP");
		// poolIP
		saveNovaVmBean.setPublicIpPool("网络池");

		// tenancyDate
		// saveNovaVmBean.setTenancyDate(new Date());
		saveNovaVmBean.setMonth(9L);
		saveNovaVmBean.setCount(3);
		NovaVm novaVm = new NovaVm();
		InvokeSetForm.copyFormProperties1(saveNovaVmBean, novaVm);
		// System.out.println(novaVm.getHostName());
		// System.out.println(JacksonUtil.toJSon(saveNovaVmBean));
		Map<String, Object> server = paramsCreate(saveNovaVmBean);
		String input = new JSONObject(server).toJSONString();
		System.out.println("传入的参数为" + input);
		JSONObject map = JSONObject.parseObject(input);
		map = map.getJSONObject("server");
		CloudosClient client = CloudosClient.createAdmin();
		JSONObject result = client.post("/v2/" + CacheSingleton.getInstance().getRootProject() + "/servers", server);
		// 获得云主机id
		JSONObject serverObj = HttpUtils.getJSONObject(result, "server");
		String uuid = serverObj.getString("id");
		String uri = "/v2/" + CacheSingleton.getInstance().getRootProject() + "/servers/{server_id}/action";
		uri = HttpUtils.tranUrl(uri, uuid);
		//云主机关机
		Map<String, Object> map2=StrUtils.createMap(CloudosParams.SERVER_ACTION_OS_START, "null");
		result = client.post(uri,map2);
		System.out.println(result);
		String uri1 = "/v2/" + CacheSingleton.getInstance().getRootProject() + "/servers/{server_id}";
		uri1 = HttpUtils.tranUrl(uri1, uuid);
		for (int j = 0; j < 3; j++) {
			JSONObject vmstate = client.get(uri1);
			String state = HttpUtils.getJSONObject(vmstate, "server").getString("status");
			if ("SHUTOFF".equals(state)) {

				break;
			}
			TimeUnit.SECONDS.sleep(5);
		}
		for (int j = 0; j < 3; j++) {
			JSONObject vmstate = client.get(uri1);
			if (vmstate.getString("result").equals("404")) {
				break;
			}
			TimeUnit.SECONDS.sleep(5);
		}
		Server server1 = new Server();
		InvokeSetForm.settingForm(map, server1);
		// Server server1=(Server) JSONObject.parse(input);
		System.out.println(JacksonUtil.toJSon(server1));
	}

	private static Map<String, Object> paramsCreate(SaveNovaVmBean bean) {
		Map<String, Object> server = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		params.put("name", bean.getHostName());
		params.put("imageRef", bean.getImageRef());
		params.put("flavorRef", bean.getFlavorId());
		params.put("max_count", 1);
		params.put("min_count", 1);
		List<Object> networks = new ArrayList<Object>();
		Map<String, Object> subnet = new HashMap<>();
		subnet.put("uuid", bean.getNetworkId());
		networks.add(subnet);
		params.put("networks", networks);
		List<Object> security_groups = new ArrayList<Object>();
		Map<String, Object> user = new HashMap<>();
		user.put("name", "default");
		// user.put("name1", "another-secgroup-name");
		security_groups.add(user);
		params.put("security_groups", security_groups);
		server.put("server", params);
		return server;

	}
}
