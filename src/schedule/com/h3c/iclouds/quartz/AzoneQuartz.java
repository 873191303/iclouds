package com.h3c.iclouds.quartz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.dao.Project2AzoneDao;
import com.h3c.iclouds.operate.CloudosBase;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Project2Azone;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AzoneQuartz {

	public Logger log = LoggerFactory.getLogger(AzoneQuartz.class);
	
	@Resource
	private Project2AzoneDao project2AzoneDao;
	
	@Resource
	private ProjectBiz projectBiz;
	
	// 执行体，与配置文件的targetMethod对应
	public void get() throws Exception {
		log.info("AzoneQuartz start");
		CloudosBase cloudosBase=new CloudosBase();
		cloudosBase.syn();
		log.info("AzoneQuartz end");
		
		CloudosClient client = CloudosClient.createAdmin();
		String uri = "/keystone/types/AzoneProject/assigments";
		JSONObject result = client.get(uri);
		if (ResourceHandle.judgeResponse(result)) {
			JSONArray array = HttpUtils.getJSONArray(result, "assigments");
			// JSONArray array = result.getJSONArray("record");
			if (StrUtils.checkParam(array)) {
				List<Project2Azone> links = project2AzoneDao.getAll(Project2Azone.class);
				if(StrUtils.checkCollection(links)) {
					project2AzoneDao.delete(links);
				}
				for (int i = 0; i < array.size(); i++) {
					JSONObject item = array.getJSONObject(i);
					String projectId = item.getString("targetId");
					String azoneId = item.getString("actorId");
					Map<String, String> map = new HashMap<>();
					map.put("id", projectId);
					map.put("iyuUuid", azoneId);
					List<Project2Azone> project2Azones = project2AzoneDao.findByMap(Project2Azone.class, map);
					if (!StrUtils.checkCollection(project2Azones) || !ConfigProperty.STOP_SET.contains(projectId)) {
						Project project = projectBiz.findById(Project.class, projectId);
						if (StrUtils.checkParam(project)) {
							Project2Azone project2Azone = new Project2Azone();
							project2Azone.setId(project.getId());
							project2Azone.setIyuUuid(azoneId);
							project2Azone.setDeleted("0");
							project2AzoneDao.add(project2Azone);
						}
					}
				}
			}
		}
	}
}
