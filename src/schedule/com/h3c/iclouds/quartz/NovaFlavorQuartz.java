package com.h3c.iclouds.quartz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.bean.cloudos.NovaVmFlavorDetail;
import com.h3c.iclouds.biz.AzoneBiz;
import com.h3c.iclouds.biz.ListPriceBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.Azone;
import com.h3c.iclouds.po.NovaFlavor;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class NovaFlavorQuartz {

	@Resource
	private BaseDAO<NovaFlavor> baseDAO;
	
	@Resource
	private ListPriceBiz listPriceBiz;
	
	@Resource
	private AzoneBiz azoneBiz;
	
	public NovaFlavorQuartz() {

	}

	public void get() throws Exception {
		List<String> list = new ArrayList<String>();
		// 初始化内容
		List<NovaFlavor> novaFlavors = baseDAO.getAll(NovaFlavor.class);
		for (NovaFlavor novaFlavor : novaFlavors) {
			list.add(novaFlavor.getId());
		}
		CloudosClient client = CloudosClient.createAdmin();
		if (StrUtils.checkParam(client)) {
			String rootid = CacheSingleton.getInstance().getConfigValue("rootid");
			String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_FLAVOR);
			uri = HttpUtils.tranUrl(uri, rootid);
			JSONObject result = client.get(uri);
			if (ResourceHandle.judgeResponse(result)) {
				JSONArray array = HttpUtils.getJSONArray(result, "flavors");
				if (StrUtils.checkParam(array)) {
					List<NovaVmFlavorDetail>  novaVmFlavorDetails=new ArrayList<>();
					for (int i = 0; i < array.size(); i++) {
						JSONObject flavor = array.getJSONObject(i);
						NovaVmFlavorDetail novaVmFlavorDetail=new NovaVmFlavorDetail();
						InvokeSetForm.settingForm(flavor, novaVmFlavorDetail);
						novaVmFlavorDetail.setIsPublic(flavor.getBoolean("os-flavor-access:is_public"));
						novaVmFlavorDetails.add(novaVmFlavorDetail);
						String id = flavor.getString("id");
						String name = flavor.getString("name");
						if (id.length() == 1 || (!Character.isDigit(name.charAt(0)))) {
							continue;
						}
						if (!list.contains(id)) {
							NovaFlavor novaFlavor = new NovaFlavor();
							InvokeSetForm.settingForm(flavor, novaFlavor);
							novaFlavor.setSwap(0);
							novaFlavor.setIsPublic(flavor.getBoolean("os-flavor-access:is_public"));
							novaFlavor.createdUser("989116e3-79a2-426b-bfbe-668165104885");
							novaFlavor.setCreatedBy("H3c");
							novaFlavor.setDisabled(false);
							novaFlavor.setDiskType("0");
							novaFlavor.setDeleted("0");
							LogUtils.info(this.getClass(), "新增的规格" + novaFlavor.getName());
							baseDAO.add(novaFlavor);
							//todo 同步云主机规格时创建相应的云主机定价的虚拟数据(待删除)
//							List<Azone> azones = azoneBiz.findByPropertyName(Azone.class, "resourceType", "nova");
//							if (StrUtils.checkCollection(azones)) {
//								for (Azone azone : azones) {
//									listPriceBiz.saveByNovaFlavor(novaFlavor, "989116e3-79a2-426b-bfbe-668165104885", azone.getUuid());
//								}
//							}
						}
					}
					for (NovaFlavor novaFlavor : novaFlavors) {
						if (!check(novaFlavor,novaVmFlavorDetails)) {
							if(!"1".equals(novaFlavor.getDeleted())) {
								novaFlavor.setDeleted("1");
								baseDAO.update(novaFlavor);
							}
						} else {
							if(!"0".equals(novaFlavor.getDeleted())) {
								novaFlavor.setDeleted("0");
								baseDAO.update(novaFlavor);
							}
						}
					}
				}

			} else {
				LogUtils.exception(Azone.class, new MessageException(ResultType.cloudos_api_error));
			}
		} else {
			LogUtils.exception(Azone.class, new MessageException(ResultType.cloudos_api_error));
		}

	}
	
	private boolean check(NovaFlavor novaFlavor, List<NovaVmFlavorDetail> novaVmFlavorDetails) {
		for (NovaVmFlavorDetail novaVmFlavorDetail : novaVmFlavorDetails) {
			if (novaFlavor.getId().equals(novaVmFlavorDetail.getId())) {
				return true;
			}
		}
		return false;
	}
	
}
