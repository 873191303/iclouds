package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.WorkFlowBiz;
import com.h3c.iclouds.biz.WorkRoleBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.WorkFlowDao;
import com.h3c.iclouds.po.WorkFlow;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.UploadFileUtils;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("workFlowBiz")
public class WorkFlowBizImpl extends BaseBizImpl<WorkFlow> implements WorkFlowBiz {

	@Resource
	private WorkFlowDao workFlowDao;
	
	@Resource
	private RepositoryService repositoryService;
	
	@Resource
	private WorkRoleBiz workRoleBiz;
	
	@Override
	public PageModel<WorkFlow> findForPage(PageEntity entity) {
		return workFlowDao.findForPage(entity);
	}
	
	@Override
	public void delete(WorkFlow entity) {
		String path = UploadFileUtils.getWorkFlowUploadPath();
		UploadFileUtils.deleteFile(entity.getFileName(), path);
		super.delete(entity);
	}

	@Override
	public ResultType save(WorkFlow entity, MultipartFile file) throws IOException {
		this.add(entity);
		if (file != null && !file.isEmpty()){	// 上传了流程文件
			UploadFileUtils.uploadWorkFlowFile(entity, file, request);
			this.update(entity);
		}
		return ResultType.success;
	}

	@Override
	public ResultType update(WorkFlow entity, WorkFlow before, MultipartFile file) throws IOException {
		String path = UploadFileUtils.getWorkFlowUploadPath();
		if (file != null && !file.isEmpty()){	// 上传了流程文件
			if(before.getFileName() != null && !"".equals(before.getFileName())) {
				UploadFileUtils.deleteFile(before.getFileName(), path);
			}
			UploadFileUtils.uploadWorkFlowFile(before, file, request);
		}
		entity.setFileName(before.getFileName());
		InvokeSetForm.copyFormProperties(entity, before);
		before.setUpdatedBy(this.getSessionUserId());
		before.setUpdatedDate(new Date());
		this.update(before);
		return ResultType.success;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultType deploy(WorkFlow entity) throws FileNotFoundException {
		String fileName = entity.getFileName();
		if(fileName != null && !"".equals(fileName)) {
			String path = UploadFileUtils.getWorkFlowUploadPath();
			File file = new File(path + fileName);
			if(file == null || !file.exists()) {
				path = this.getClass().getResource("/").getPath() + File.separator + "workflow" + File.separator;
				file = new File(path + fileName);
			}

			if(file != null && file.exists()) {
				Map<String, Object> map = UploadFileUtils.analysisTaskFile(file);
				String id = StrUtils.tranString(map.get("id"));
				if(id.equals(entity.getKey() + entity.getVersion())) {
					workRoleBiz.saveRoleByList((List<Map<String, String>>)map.get("roleList"), entity.getId(), entity);
					FileInputStream fis = new FileInputStream(file);
					Deployment deployment = repositoryService.createDeployment().addInputStream(fileName, fis).deploy();
					if(deployment != null) {
						entity.setDeploymentId(deployment.getId());
						entity.setStatus(ConfigProperty.WORKFLOW_STATUS2_DEPLOY);	// 已部署
						entity.setDeployDate(new Date());
						entity.setDeployer(this.getSessionUserId());
						workFlowDao.update(entity);
						return ResultType.success;
					}
					return ResultType.failure;	
				}
				return ResultType.deploy_error;
			}
		}
		return ResultType.deploy_file_exist;
	}

	@Override
	public ResultType undeploy(WorkFlow entity) {
		this.repositoryService.deleteDeployment(entity.getDeploymentId(), true);
		entity.setStatus(ConfigProperty.WORKFLOW_STATUS1_UNDEPLOY);
		entity.setDeployDate(null);
		entity.setDeployer(null);
		entity.setDeploymentId(null);
		entity.setStatus(ConfigProperty.WORKFLOW_STATUS1_UNDEPLOY);
		this.update(entity);
		return ResultType.success;
	}

}
