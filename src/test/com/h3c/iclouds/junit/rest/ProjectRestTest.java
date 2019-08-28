package com.h3c.iclouds.junit.rest;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.po.bean.inside.SaveProjectCustomBean;
import com.h3c.iclouds.po.bean.inside.UpdateProject2QuotaBean;
import com.h3c.iclouds.po.bean.inside.UpdateTenantBean;
import com.h3c.iclouds.po.bean.model.AzoneBean;
import com.h3c.iclouds.po.bean.model.Network2SubnetBean;
import com.h3c.iclouds.po.bean.model.NetworksBean;
import com.h3c.iclouds.po.bean.model.Project2NetworkBean;
import com.h3c.iclouds.po.bean.model.Project2QuotaBean;
import com.h3c.iclouds.po.bean.model.QuotaBean;
import com.h3c.iclouds.po.bean.model.TenantBean;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.dao.ProjectDao;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Project2Quota;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.JacksonUtil;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ProjectRestTest {
	
	@Resource
	private ProjectDao projectDao;

	public void list() {
		Collections.sort(ConfigProperty.class_computer_resource);
		System.out.println();
	}
	

	@Test
	public  void compute() {
		UpdateProject2QuotaBean bean=new UpdateProject2QuotaBean();
		bean.setProjectId(CacheSingleton.getInstance().getRootProject());
		List<Project2QuotaBean> quotas=new ArrayList<>();
		Project2QuotaBean project2Quota=new Project2QuotaBean();
		project2Quota.setClassCode("ram");
		project2Quota.setHardLimit(20);
		
		quotas.add(project2Quota);
//		bean.setQuotas(quotas);
		System.out.println(JacksonUtil.toJSon(bean));

	}
	
	@Test
	public  void updateProject() {
		UpdateTenantBean bean=new UpdateTenantBean();
	
	
		List<AzoneBean> azoneBeans=new ArrayList<>();
		AzoneBean azoneBean=new AzoneBean();
		azoneBean.setLableName("CAS202");
		azoneBean.setUuid("6fe5cdd0-a21c-4c6d-89d3-e0ec2c5d4fbd");
		TenantBean terantBean=new  TenantBean();
		terantBean.setCusId("ff8080815850e5810158510779b80016");
		terantBean.setDescription("客户描述");
		terantBean.setName("客户名称");
		terantBean.setParentId("父id");
		azoneBeans.add(azoneBean);
		bean.setAzones(azoneBeans);
	
		bean.setProject(terantBean);
		System.out.println(JacksonUtil.toJSon(bean));

	}
	


	
	@Test
	public void demo1() {
		//ApplicationContext applicationContext=SpringContextHolder.getApplicationContext();
		//ProjectDao projectDao=(ProjectDao) getContext().getBean("projectDao");
		List<Project> list=projectDao.getAll(Project.class);
		for (Project project : list) {
			System.out.println(project.getCusId());
		}
		
	}
	
	public static List<NetworksBean> createNetwork() {
				//网段
				List<NetworksBean> networksBeans=new ArrayList<>();
				for (int i = 0; i < 2; i++) {
					NetworksBean networksBean=new NetworksBean();
					Project2NetworkBean cidr=new Project2NetworkBean();
					cidr.setCidr("子网掩码"+i);
					String networkId=UUID.randomUUID().toString();
					cidr.setId(networkId);
					
					List<Network2SubnetBean> subnets=new ArrayList<>();
					Network2SubnetBean network2Subnet=new Network2SubnetBean();
					network2Subnet.setId(UUID.randomUUID().toString());
					network2Subnet.setStartIp("起始IP"+i);
					network2Subnet.setEndIp("末尾IP"+i);
					network2Subnet.setNetworkId(networkId);
					subnets.add(network2Subnet);
					
					networksBean.setCidr(cidr);
					networksBean.setSubnets(subnets);
					networksBeans.add(networksBean);
				}
				
				
				return networksBeans;
	}
	@Test
	public  void updateProject2QuotaBean() {
		UpdateProject2QuotaBean bean=new UpdateProject2QuotaBean();
		bean.setProjectId("647b9f0500f14673aedc5624c870160a");
		List<Project2QuotaBean> quotas=new ArrayList<>();
		for (String a1 : ConfigProperty.class_computer_resource) {
			Project2QuotaBean bean2=new Project2QuotaBean();
			bean2.setClassCode(a1);
			bean2.setHardLimit(2);
			quotas.add(bean2);
		}
		for (String a1 : ConfigProperty.class_storage_resource) {
			Project2QuotaBean bean2=new Project2QuotaBean();
			bean2.setClassCode(a1);
			bean2.setHardLimit(2);
			quotas.add(bean2);
		}
		for (String a1 : ConfigProperty.class_network_resource) {
			Project2QuotaBean bean2=new Project2QuotaBean();
			bean2.setClassCode(a1);
			bean2.setHardLimit(2);
			quotas.add(bean2);
		}
		//bean.setQuotas(quotas);
		//System.out.println(JSONObject.parseObject(JacksonUtil.toJSon(bean)));
		//System.out.println(InvokeSetForm.tranClassToMap(bean));
		System.out.println(JacksonUtil.toJSon(bean));
	}
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		String uri="/v2/" + CacheSingleton.getInstance().getRootProject() + "/servers/25d1a85d-a1c4-4960-b522-612f07b2f398/action/action";
		System.out.println(uri.substring(0, uri.indexOf("/action")));
		System.out.println(System.getProperty("user.dir"));
		String  path=System.getProperty("user.dir")+"\\src\\test\\portlist.json";
		File file =new File(path);
		System.out.println(path);
		System.out.println(file.exists());
		
		/*ProjectRestTest test=new ProjectRestTest();
		test.update();*/
		TenantBean bean= new  TenantBean();
		Project2QuotaBean bean2= new Project2QuotaBean();
		bean2.setClassCode("12");
		Project2Quota project2Quota=new  Project2Quota();
		InvokeSetForm.copyFormProperties(bean2, project2Quota);
		//System.out.println(project2Quota.getClassCode());
		
		String ipPools="192.10.18.39-192.10.18.100;192.10.18.127-192.10.18.254";
		String cidr="192.10.18.1/24";
	}
	@Test
	public void projectCustomCreate() {
		TenantBean project = new TenantBean();
		project.setName("租户名");
		project.setDescription("租户描述");
		//cloud根租户
		project.setParentId(CacheSingleton.getInstance().getRootProject());
		project.setCusId("ff8080815850e5810158510779b80016");
		//project.set
		SaveProjectCustomBean projectBean=new SaveProjectCustomBean();
		projectBean.setProject(project);
		projectBean.setUserName("admin");
		projectBean.setPassword("admin");
		
		
		List<AzoneBean> azones=new ArrayList<AzoneBean>();
		AzoneBean azone=new AzoneBean();
		azone.setUuid("dcf6f9c3-becc-42ea-8b3c-08d349ead155");
		azone.setLableName("CAS1");
		azones.add(azone);
		AzoneBean azone1=new AzoneBean();
		azone1.setUuid("6fe5cdd0-a21c-4c6d-89d3-e0ec2c5d4fbd");
		azone1.setLableName("CAS2");
		azones.add(azone1);
		projectBean.setAzone(azones);
		System.out.println(JacksonUtil.toJSon(projectBean));
	}
	@Test
	public void quotaBean() {
		QuotaBean bean = new QuotaBean();
		QuotaBean.Storage block_storage=new QuotaBean.Storage();
		Map<String, Object> quota_set=new HashMap<>();
		quota_set.put("gigabytes", "2s");
		quota_set.put("snapshots", "212s");
		block_storage.setQuota_set(quota_set);
		bean.setBlock_storage(block_storage);
		//InvokeSetForm.tranClassToMap(bean);
		System.out.println(InvokeSetForm.tranClassToMap(bean));
	}
	@Test
	public void name1() {
		CloudosClient client=CloudosClient.createAdmin();
		String uri="/v2/" + CacheSingleton.getInstance().getRootProject() + "/os-quota-sets/83967ec0068141fe9bc175fc56c198c0";
		client.setHeaderLocal("H3CloudOS-Core-Target", "compute");
		client.setHeaderLocal("H3CloudOS-Core-Target", "block_storage");

		JSONObject result=client.get(uri);
		//result=HttpUtils.getJSONObject(result);
		result=result.getJSONObject("record");
		result.getJSONObject("quota_set").put("gigabytes", 30);
		result.getJSONObject("quota_set").put("snapshots", 21);
		result.getJSONObject("quota_set").put("volumes", 20);
//		result.getJSONObject("quota_set").put("ram", 20*1024);
		
		System.out.println(result);
		client.put(uri,result);
		
	}
	
	@Test
	public void check() {
		Project2Quota project2Quota=new Project2Quota();
		project2Quota.setId("ff8080815985f28e015986fb9684005a");
		project2Quota.setHardLimit(1000);
		project2Quota.setClassCode("instances");
		System.out.println(JacksonUtil.toJSon(project2Quota));
	}

}
