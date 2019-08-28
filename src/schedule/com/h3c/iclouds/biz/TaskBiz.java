package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Task;

public interface TaskBiz extends BaseBiz<Task> {
	
	/**
	 * 保存执行任务记录
	 * @param busId		业务id，主业务表id
	 * @param busType	执行业务类型
	 * @param input		业务执行参数	{url:url, params:{}}
	 * @return
	 */
	Task save(String busId, String busType, String input);

	Task save(String busId, String busType, String input, String userId, String projectId);

	boolean check(String busType, NovaVm novaVm);

	void save(Task task);

	/**
	 * 保存执行任务记录
	 * @param busId		业务id，主业务表id
	 * @param busType	执行业务类型
	 * @param input		业务执行参数	{url:url, params:{}}
	 * @return
	 */
	Task saveImageTask(String busId, String busType, String input);

}
