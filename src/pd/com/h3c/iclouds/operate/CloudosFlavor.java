package com.h3c.iclouds.operate;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.dao.NovaFlavorDao;
import com.h3c.iclouds.dao.NovaVmDao;
import com.h3c.iclouds.po.Metadata;
import com.h3c.iclouds.po.NovaFlavor;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.VmExtra;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;

public class CloudosFlavor extends CloudosBase {

	public CloudosFlavor(CloudosClient client) {
		this.client = client;
	}

	private NovaFlavorDao novaFlavorDao = SpringContextHolder.getBean("novaFlavorDao");
	private NovaVmDao novaVmDao = SpringContextHolder.getBean("novaVmDao");

	private BaseDAO<Metadata> metadatDao = SpringContextHolder.getBean("baseDAO");;

	private BaseDAO<VmExtra> vmExtraDao = SpringContextHolder.getBean("baseDAO");;

	public boolean get(String projectId, String id) {
		String uri1 = "/v2/{tenant_id}/flavors/{flavor_id}";
		uri1 = HttpUtils.tranUrl(uri1, projectId, id);
		if (StrUtils.checkParam(client)) {
			JSONObject result = client.get(uri1);
			if (!ResourceHandle.judgeResponse(result)) {
				return false;
			}
			return true;
		} else {
			return true;
		}

	}

	public List<NovaFlavor> get(String projectId, List<NovaFlavor> novaFlavors) {
		List<NovaFlavor> result = new ArrayList<NovaFlavor>();
		for (NovaFlavor novaFlavor : novaFlavors) {
			if (get(projectId, novaFlavor.getId())) {
				result.add(novaFlavor);
			}
		}
		return result;
	}

	public void delete(List<NovaFlavor> list, List<Project> projects) {
		for (Project project : projects) {
			for (NovaFlavor novaFlavor : list) {
				if (get(project.getId(), novaFlavor.getId())) {

				} else {
					List<NovaVm> novaVms = novaVmDao.findByPropertyName(NovaVm.class, "flavorId", novaFlavor.getId());
					for (NovaVm novaVm : novaVms) {
						VmExtra vmExtra = vmExtraDao.findById(VmExtra.class, novaVm.getId());
						if (StrUtils.checkParam(vmExtra)) {
							vmExtraDao.delete(vmExtra);
						}
						List<Metadata> metadatas = metadatDao.findByPropertyName(Metadata.class, "instanceUuid",
								novaVm.getId());
						for (Metadata metadata : metadatas) {
							if (StrUtils.checkParam(metadata)) {
								metadatDao.delete(metadata);
							}
						}
						novaVmDao.delete(novaVm);
					}
					novaFlavorDao.delete(novaFlavor);
					LogUtils.warn(this.getClass(), "被删除的规格" + novaFlavor.getName());
				}
			}
		}

	}
	public static void main(String[] args) {
		CloudosClient client=CloudosClient.createAdmin();
		//client.get("/v3/users");
		String uri="/keystone/projects/{projectId}/users/{userId}/groups/{groupId}";
		uri=HttpUtils.tranUrl(uri, "bbe7e7e8d8a8471eab725493cd06f6a2","a8865df84911442283b89a06051d329c","67bcfea5-0428-44be-9219-37d56a5454a1");
		//client.get("/keystone/groups");
		client.get(uri);
		//client.delete(uri);
		//client.put(uri);
		
	}
}
