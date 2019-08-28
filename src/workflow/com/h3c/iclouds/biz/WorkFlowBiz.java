package com.h3c.iclouds.biz;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.WorkFlow;

public interface WorkFlowBiz extends BaseBiz<WorkFlow> {

	/**
	 * 保存
	 * @param entity
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public ResultType save(WorkFlow entity, MultipartFile file) throws IOException;
	
	/**
	 * 修改
	 * @param entity
	 * @param before
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public ResultType update(WorkFlow entity, WorkFlow before, MultipartFile file) throws IOException;
	
	/**
	 * 部署流程
	 * @param entity
	 * @return
	 * @throws FileNotFoundException 
	 */
	public ResultType deploy(WorkFlow entity) throws FileNotFoundException;
	
	/**
	 * 部署流程
	 * @param entity
	 * @return
	 * @throws FileNotFoundException 
	 */
	public ResultType undeploy(WorkFlow entity);
}
