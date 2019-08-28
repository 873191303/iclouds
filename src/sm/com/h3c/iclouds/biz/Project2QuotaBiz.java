package com.h3c.iclouds.biz;

import java.util.Map;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.bean.inside.SaveNovaVmBean;
import com.h3c.iclouds.po.NovaFlavor;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Project2Quota;

public interface Project2QuotaBiz extends BaseBiz<Project2Quota>{

	Project2Quota get(String projectId, String classCode);

	Map<String, Integer> check(Project Project,Project2Quota project2Quota);

	void checkQuota(String classCode, String projectId);

	void checkQuota(String classCode, String projectId, int resourceCount,SaveNovaVmBean bean);

	void novaQuota(NovaFlavor novaFlavor, String projectId,int count);

	void novaQuota(SaveNovaVmBean bean);

	void addQuota(String projectId, String type, Integer value);

}
