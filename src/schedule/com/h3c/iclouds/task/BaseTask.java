package com.h3c.iclouds.task;

import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.listen.StartTaskQueenListen;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.po.Task2Exec;
import com.h3c.iclouds.po.TaskHis;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.ThreadContext;

public abstract class BaseTask implements Runnable {
	
	protected Task task;

	private BaseDAO<Task2Exec> task2ExecDao;

	private BaseDAO<Task> taskDao;

	private BaseDAO<TaskHis> taskHisDao;
	
	private UserBiz userBiz;
	
	protected final int retryNum = 3;	// 默认最多执行3次
	
	protected CloudosClient client;

	private StringBuffer buffer;
	
	public BaseTask(Task task) {
		this.task = task;
		task2ExecDao = SpringContextHolder.getBean("baseDAO");
		taskDao = SpringContextHolder.getBean("baseDAO");
		taskHisDao = SpringContextHolder.getBean("baseDAO");
		userBiz = SpringContextHolder.getBean("userBiz");
	}

	public void initThreadContext() {
		ThreadContext.clear();
		// 设置任务执行前缀
		ThreadContext.set(ConfigProperty.LOGS_TOP_FLAG, "[Task:" + task.getId() + "]");
	}
	
	@Override
	public void run() {
		this.initThreadContext();
		LogUtils.info(this.getClass(), "Init task:" + task.getId() + ", busType:" + task.getBusType());
		this.init();
		String status = ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR;
		if(this.client != null) {
			LogUtils.info(this.getClass(), "Handle task:" + task.getId() + ", busType:" + task.getBusType());
			try {
				status = this.handle();
			} catch (Exception e) {
				LogUtils.exception(getClass(), e, "Task exec error");
			}
		} else {	// 未获取到cloudos连接
			this.saveTask2Exec(ResultType.cloudos_api_error.toString(), ConfigProperty.TASK_STATUS3_END_SUCCESS, 0);
		}
		LogUtils.info(this.getClass(), "Last handle task:" + task.getId() + ", busType:" + task.getBusType());
		this.last(status);
		ThreadContext.clear();
	}
	
	/**
	 * 任务开始前处理:处理任务状态
	 */
	protected void init() {
		this.task.setStackTime(new Date());
		this.task.setStackIp(CacheSingleton.getInstance().getIpAddr());
		this.task.setStatus(ConfigProperty.TASK_STATUS2_HANDLE);
		this.taskDao.update(this.task);
		this.initCloudosClient();
	}

	/**
	 * 真正处理任务
	 */
	protected abstract String handle();

	/**
	 * 任务结束后处理
	 */
	protected void last(String status) {
		this.saveTaskHis(status);
		--StartTaskQueenListen.CURRENT_EXEC_TASK_NUM;
		// TODO 增加Q2队列执行推送，目前暂时采用前端轮询的方式
		
		// 清空内容
		ThreadContext.clear();
	}
	
	/**
	 * 
	 * @param status
	 */
	public void saveTaskHis(String status) {
		TaskHis his = new TaskHis();
		try {
			InvokeSetForm.copyFormProperties(task, his);	// 数据拷贝
			his.setId(task.getId());
			his.setStatus(status);
			his.setFinishTime(new Date());	// 设置结束时间
			his.setCreatedBy(task.getCreatedBy());
			his.setCreatedDate(task.getCreatedDate());
			his.setUpdatedBy(task.getUpdatedBy());
			his.setUpdatedDate(task.getUpdatedDate());
			this.taskHisDao.add(his);
			this.taskDao.delete(task);	// 移除执行表
		} catch (Exception e) {
			LogUtils.exception(this.getClass(), e);
			LogUtils.info(this.getClass(), "Save handle history record failure, value:" + JSONObject.toJSONString(his));
		}
	}
	
	/**
	 * 保存执行记录
	 * @param result		处理结果，包括API处理结果，异常情况
	 * @param status		
	 * @param currentNum
	 */
	public void saveTask2Exec(String result, String status, int currentNum) {
		Task2Exec taskHis = new Task2Exec();
		try {
			taskHis.setTaskId(task.getId());
			if(this.buffer != null && this.buffer.length() > 0) {
				taskHis.setResult(result + ". 追加信息：" + buffer.toString());
				this.buffer.delete(0, buffer.length());
			} else {
				taskHis.setResult(result);
			}
			taskHis.setStatus(status);
			taskHis.setRetryNum(currentNum);
			taskHis.setCreatedDate(new Date());
			this.task2ExecDao.add(taskHis);
		} catch (Exception e) {
			LogUtils.exception(this.getClass(), e);
			LogUtils.info(this.getClass(), "Save handle exec record failure, value:" + JSONObject.toJSONString(taskHis));
		}
	}

	public void initCloudosClient() {
		LogUtils.info(this.getClass(), "Init task:" + task.getId() + " cloudos client start");
		User user = userBiz.findById(User.class, this.task.getCreatedBy());
		this.client = CloudosClient.create(user.getCloudosId(), user.getLoginName());
		LogUtils.info(this.getClass(), "Init task:" + task.getId() + " cloudos client " + this.client == null ? "failure" : "success");
	}

	/**
	 * 增加同步调用，防止如果多线程调用可能引起的错误
	 * @param type
	 * @param uri
	 * @param params
	 * @return
	 */
	public synchronized JSONObject clientQuery(String type, String uri, Map<String, Object> params) {
		switch (type) {
			case CloudosParams.POST:
				return this.client.post(uri, params);
			case CloudosParams.GET:
				return this.client.get(uri, params);
			default:
				break;
		}
		return new JSONObject();
	}

	/**
	 * 追加信息，保存后既清空
	 * @param msg
     */
	public void addOtherMsg(String msg) {
		if(this.buffer == null) {
			buffer = new StringBuffer();
		}
		buffer.append(msg);
	}

}

