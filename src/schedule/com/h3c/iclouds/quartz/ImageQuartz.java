package com.h3c.iclouds.quartz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.bean.nova.ImageDetail;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Rules;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageQuartz {

	public Logger log = LoggerFactory.getLogger(ImageQuartz.class);

	@Resource
	private BaseDAO<Rules> baseDAO;

	public ImageQuartz() {

	}

	public void get() {
		LogUtils.info(this.getClass(), "执行镜像同步任务");
		List<String> list = new ArrayList<String>();
		// 初始化内容
		List<Rules> rules = baseDAO.getAll(Rules.class);
		for (Rules rules2 : rules) {
			list.add(rules2.getId());
		}
		List<ImageDetail> details = new ArrayList<>();

		CloudosClient client = CloudosClient.createAdmin();
		if (StrUtils.checkParam(client)) {
            String uri = "/v2/images?limit=1000";
			JSONObject result = client.get(uri);
			if (ResourceHandle.judgeResponse(result)) {
				JSONArray images = HttpUtils.getJSONArray(result, "images");
				if (StrUtils.checkParam(images)) {
					for (int i = 0; i < images.size(); i++) {
						JSONObject image = images.getJSONObject(i);
						ImageDetail imageDetail=new ImageDetail();
						InvokeSetForm.settingForm(image, imageDetail);
						details.add(imageDetail);
						String id = image.getString("id");
						String cas_ostype = image.getString("cas_ostype");

						String name = image.getString("name");
						Integer min_ram = image.getInteger("min_ram");
						Integer min_disk = image.getInteger("min_disk");
						Integer min_cpu = image.getInteger("min_cpu");
						String visibility = image.getString("visibility");
						String definition = "public".equals(visibility) ? "0" : "1";
						
						Rules rules2 = null;
						boolean isContain = !list.contains(id);
						if (isContain) {
							rules2 = new Rules();
							InvokeSetForm.settingForm(image, rules2);
							rules2.createdUser(ConfigProperty.SYSTEM_FLAG);
						} else {
							//兼容上个版本对于预定义自定义未处理的问题
							rules2 = baseDAO.findById(Rules.class, id);
						}
						
						rules2.setOsMirName(name);
						if (cas_ostype.contains("linux")) {
							rules2.setOsMirId("2");
						} else if (cas_ostype.contains("window")) {
							rules2.setOsMirId("1");
						}
						rules2.setOsType(cas_ostype);
						rules2.setDefinition(definition);
						rules2.setTenantId(image.getString("owner"));
						rules2.setMinSwap(1);
						rules2.setIsDefault("0");
						rules2.setMinRam(min_ram);
						rules2.setMinDisk(min_disk);
						rules2.setVcpu(min_cpu);
						rules2.setSyncTime(new Date());
						rules2.setFormat(image.getString("container_format"));
						rules2.setDefinition(definition);
						if(isContain) {
							LogUtils.info(this.getClass(), "新增的规格" + rules2.getOsMirName());
							baseDAO.add(rules2);
						} else {
							baseDAO.update(rules2);
						}
					}
				}
			} else {
				LogUtils.exception(Rules.class, new MessageException(ResultType.cloudos_api_error));
			}
			if (StrUtils.checkCollection(details)) {
				for (Rules rule : rules) {
					if (!check(rule, details)) {
						rule.setIsDefault("1");
						baseDAO.update(rule);
					}
				}
			}
		} else {
			LogUtils.exception(Rules.class, new MessageException(ResultType.cloudos_api_error));
		}
	}

	private boolean check(Rules rules,List<ImageDetail> imageDetails) {
		boolean flag=false;
		for (ImageDetail imageDetail : imageDetails) {
			if(imageDetail != null || imageDetail.getId() != null) {
				if (imageDetail.getId().equals(rules.getId())) {
					flag = true;
					break;
				}
			}
		}
		return flag;
	}
}
