package com.h3c.iclouds.operate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.AzoneBiz;
import com.h3c.iclouds.biz.ListPriceBiz;
import com.h3c.iclouds.po.Azone;
import com.h3c.iclouds.po.NovaFlavor;
import com.h3c.iclouds.po.bean.cloudos.AzoneDetail;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.StrUtils;

import java.util.ArrayList;
import java.util.List;

public class CloudosBase {

	protected CloudosClient client;
	
	protected String uri; 
	
	protected JSONObject param; 
	
	Integer ok = 200;

	private List<String> list = new ArrayList<String>();
	
	AzoneBiz azoneBiz = SpringContextHolder.getBean("azoneBiz");
	
	BaseDAO<NovaFlavor> baseDAO = SpringContextHolder.getBean("baseDAO");
	
	ListPriceBiz listPriceBiz = SpringContextHolder.getBean("listPriceBiz");

	public CloudosBase() {
		// 初始化内容
		
	}

	public JSONArray syn() {
		List<Azone> azones = azoneBiz.getAll(Azone.class);
		for (Azone azone : azones) {
			list.add(azone.getUuid());
		}
		CloudosClient client = CloudosClient.createAdmin();
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_AZONES);
		if (StrUtils.checkParam(client)) {
			JSONObject result = client.get(uri);
			if (ResourceHandle.judgeResponse(result)) {
				JSONArray array = result.getJSONArray("record");
				if (StrUtils.checkParam(array)) {
					List<AzoneDetail> azoneDetails=new ArrayList<>();
					for (int i = 0; i < array.size(); i++) {
						JSONObject azone = array.getJSONObject(i);
						AzoneDetail azoneDetail=new AzoneDetail();
						InvokeSetForm.settingForm(azone, azoneDetail);
						azoneDetails.add(azoneDetail);
						String id = azone.getString("id");
						if (!list.contains(id)) {
							Azone azone2 = new Azone();
							InvokeSetForm.settingForm(azone, azone2);
							azone2.setUuid(id);
							azone2.setLableName(azone.getString("labelName"));
							azone2.setDescription(azone.getString("description"));
							azone2.setCreatedByName("h3c");
							azone2.createdUser("h3c");
							azone2.setDeleted("0");
							//有可能要做记录
							LogUtils.warn(this.getClass(), "新增的"+azone2.getLableName());
							azoneBiz.add(azone2);
							//todo 同步云主机可用域时创建相应的云主机定价的虚拟数据(待删除)
							/*if ("nova".equals(azone2.getResourceType())) {
								List<NovaFlavor> novaFlavors = baseDAO.getAll(NovaFlavor.class);
								if (StrUtils.checkCollection(novaFlavors)) {
									for (NovaFlavor novaFlavor : novaFlavors) {
										listPriceBiz.saveByNovaFlavor(novaFlavor,
												"989116e3-79a2-426b-bfbe-668165104885", azone2.getUuid());
									}
								}
							}*/
						}
					}
					for (Azone azone : azones) {
						if (!check(azone,azoneDetails)) {
							if(!"1".equals(azone.getDeleted())) {
								azone.setDeleted("1");
								azoneBiz.update(azone);	
							}
						} else {
							if(!"0".equals(azone.getDeleted())) {
								azone.setDeleted("0");
								azoneBiz.update(azone);	
							}
						}
					}
				}
				return array;
			}else {
//				LogUtils.exception(Azone.class, new MessageException(ResultType.cloudos_api_error));
//				MailUtils.sendEmail("KF.yangzailang@h3c.com", "获取数据失败信息", result.toJSONString());
			}
		}else {
//			List<String> targetList = new ArrayList<>();
//			LogUtils.exception(Azone.class, new MessageException(ResultType.cloudos_create_failure));
//			targetList.add("KF.yangzailang@h3c.com");
//			MailUtils.sendEmail(targetList, "可用域同步任务异常", "可用域同步任务异常");
		}
		
		return null;
		
	}

	private boolean check(Azone azone, List<AzoneDetail> azoneDetails) {
		for (AzoneDetail azoneDetail : azoneDetails) {
			if (azone.getUuid().equals(azoneDetail.getId())) {
				return true;
			}
		}
		return false;
		
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public JSONObject getParam() {
		return param;
	}

	public void setParam(JSONObject param) {
		this.param = param;
	}
	
	public JSONObject getAll(String uri,CloudosClient client) {
		JSONObject data=client.get(uri);
		return data;
	}
	
}
