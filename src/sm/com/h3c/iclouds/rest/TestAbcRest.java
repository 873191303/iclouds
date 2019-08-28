package com.h3c.iclouds.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.client.EisooAbcClient;
import com.h3c.iclouds.client.EisooAbcParams;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.Backup2NodesBiz;
import com.h3c.iclouds.biz.BackupNode2TasksBiz;
import com.h3c.iclouds.biz.QuotaUsedBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.Backup2Nodes;
import com.h3c.iclouds.po.BackupNode2Tasks;
import com.h3c.iclouds.po.BackupNodeGroup;
import com.h3c.iclouds.po.BackupTaskGroup;
import com.h3c.iclouds.po.Firewall;
import com.h3c.iclouds.utils.HttpUtils;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/abc")
public class TestAbcRest extends BaseRestControl {
	
	@Resource
	private Backup2NodesBiz backup2NodesBiz;
	
	@Resource
	private BackupNode2TasksBiz backupNode2TasksBiz;
	
	@Resource
	private QuotaUsedBiz quotaUsedBiz;
	
	public static String userId = CacheSingleton.getInstance().getConfigMap().get("iclouds.abc.api.username.name");
	
	@RequestMapping(value = "/backup_nodes", method = RequestMethod.GET)
	@ApiOperation(value = "获取所有备份节点（AB）列表")
	public Object nodes() {
		EisooAbcClient client = new EisooAbcClient(null, null, userId);
		String uri = "/v2/backup_nodes";
		// 1. 爱数请求数据
		JSONObject result = client.get(uri);
		JSONObject record = result.getJSONObject("record");
		BackupNodeGroup nodeGroup = JSONObject.parseObject(record.toJSONString(), BackupNodeGroup.class);
		if (EisooAbcParams.ABC_SUCCESS_MESSAGE.equals(nodeGroup.getMessage()) && EisooAbcParams.ABC_SUCCESS_CODE.equals(nodeGroup.getCode())) {
			List<Backup2Nodes> nodes = nodeGroup.getData();
			//调用业务层进行新增或更新
			backup2NodesBiz.upate(nodes);
			StringBuffer buffer = new StringBuffer();
			buffer.append("Sync backup nodes success. ");
			buffer.append("Result: [" + result.toJSONString() + "]. ");
			this.warn(buffer.toString());
			//返回更新成功
			return BaseRestControl.tranReturnValue(result);
		}
		// 更新接口失败
		StringBuffer buffer = new StringBuffer();
		buffer.append("Sync backup nodes failure. ");
		buffer.append("Result: [" + result.toJSONString() + "]. ");
		this.warn(buffer.toString());
		return BaseRestControl.tranReturnValue(ResultType.failure);
	}
	

	@RequestMapping(value = "/quotas", method = RequestMethod.GET)
	@ApiOperation(value = "获取所有租户的配额信息")
	public Object quotas() {
		EisooAbcClient client = new EisooAbcClient(null, null, userId);
		String uri = "/v2/tenants/quotas";
		
		Map<String, Object> pubParams = new HashMap<String, Object>();
		pubParams.put("Start", "0");
		pubParams.put("Count", "10");
		JSONObject result = client.get(uri, pubParams);
		JSONObject record = result.getJSONObject("record");
		// 更新配额使用情况表
		// 解析result 
		String mes = record.getString("Message");
		String code = record.getString("Code");
		if (EisooAbcParams.ABC_SUCCESS_MESSAGE.equals(mes) && EisooAbcParams.ABC_SUCCESS_CODE.equals(code)) {
			JSONObject data = record.getJSONObject("Data");
			if (null != data && !data.isEmpty()) {
				JSONArray tenants = data.getJSONArray("Tenants");
				quotaUsedBiz.upate(tenants);
				StringBuffer buffer = new StringBuffer();
				buffer.append("Sync quotas info success. ");
				buffer.append("Result: [" + result.toJSONString() + "]. ");
				this.warn(buffer.toString());
				return BaseRestControl.tranReturnValue(ResultType.success);
			}
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("Sync quotas info failure. ");
		buffer.append("Result: [" + result.toJSONString() + "]. ");
		this.warn(buffer.toString());
		return BaseRestControl.tranReturnValue(ResultType.failure);
	}

	
	@RequestMapping(value = "/quota/{tenantId}", method = RequestMethod.GET)
	@ApiOperation(value = "获取租户的配额信息")
	public Object quotaById(@PathVariable String tenantId) {
		EisooAbcClient client = new EisooAbcClient(null, null, userId);
		String uri = "/v2/tenants/{tenantId}/quota";
		uri = HttpUtils.tranUrl(uri, tenantId);
		JSONObject result = client.get(uri);
		StringBuffer buffer = new StringBuffer();
		buffer.append("Sync quota info. ");
		buffer.append("Parameter: [" + tenantId + "]. ");
		buffer.append("Result: [" + result.toJSONString() + "]. ");
		this.warn(buffer.toString());
		return BaseRestControl.tranReturnValue(result);
	}
	
	
	@RequestMapping(value = "/quota/tasks/{tenantId}", method = RequestMethod.GET)
	@ApiOperation(value = "获取租户的任务列表")
	public Object quotaTasksById(@PathVariable String tenantId) {
		EisooAbcClient client = new EisooAbcClient(null, null, userId);
		String uri = "/v2/tenants/{tenantId}/tasks";
		uri = HttpUtils.tranUrl(uri, tenantId);
		
		Map<String, Object> pubParams = new HashMap<String, Object>();
		pubParams.put("Start", "0");
		pubParams.put("Count", "10");
		JSONObject result = client.get(uri, pubParams);
		StringBuffer buffer = new StringBuffer();
		buffer.append("Sync tenant tasks info. ");
		buffer.append("Parameter: [" + tenantId + "]. ");
		buffer.append("Result: [" + result.toJSONString() + "]. ");
		this.warn(buffer.toString());
		return BaseRestControl.tranReturnValue(result);
	}
	
	
	@RequestMapping(value = "/tasks", method = RequestMethod.GET)
	@ApiOperation(value = "获取所有任务列表")
	public Object tasks() {
		EisooAbcClient client = new EisooAbcClient(null, null, userId);
		String uri = "/v2/tasks";
		
		Map<String, Object> pubParams = new HashMap<String, Object>();
		pubParams.put("Start", "0");
		pubParams.put("Count", "10");
		JSONObject result = client.get(uri, pubParams);
		JSONObject record = result.getJSONObject("record");
		String code = record.getString("Code");
		String mes = record.getString("Message");
		
		if ("OK".equals(mes) && "15728640".equals(code)) {
			JSONObject data = record.getJSONObject("Data");
			if (null != data && !data.isEmpty()) {
				BackupTaskGroup taskGroup = JSONObject.parseObject(data.toJSONString(), BackupTaskGroup.class);
				List<BackupNode2Tasks> tasks = taskGroup.getTasks();
				try {
					backupNode2TasksBiz.update(tasks);
					StringBuffer buffer = new StringBuffer();
					buffer.append("Sync all tasks success. ");
					buffer.append("Result: [" + result.toJSONString() + "]. ");
					this.warn(buffer.toString());
					return BaseRestControl.tranReturnValue(ResultType.success);
				} catch (Exception e) {
					this.exception(Firewall.class, e);
		            if(e instanceof MessageException) {
		                return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
		            }
		            return BaseRestControl.tranReturnValue(ResultType.failure);
				}
			}
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("Sync all tasks failure. ");
		buffer.append("Result: [" + result.toJSONString() + "]. ");
		this.warn(buffer.toString());
		return BaseRestControl.tranReturnValue(ResultType.failure);
	}
	
	
	@RequestMapping(value = "/tasks/{taskId}", method = RequestMethod.GET)
	@ApiOperation(value = "获取任务详情")
	public Object taskDetial(@PathVariable String taskId) {
		EisooAbcClient client = new EisooAbcClient(null, null, userId);
		String uri = "/v2/tasks/{taskId}";
		uri = HttpUtils.tranUrl(uri, taskId);
		JSONObject result = client.get(uri);
		StringBuffer buffer = new StringBuffer();
		buffer.append("Sync task detail info. ");
		buffer.append("Parameter: [" + taskId + "]. ");
		buffer.append("Result: [" + result.toJSONString() + "]. ");
		this.warn(buffer.toString());
		return BaseRestControl.tranReturnValue(result);
	}
	
	
	@RequestMapping(value = "/v2/tasks/{taskId}/stats_result", method = RequestMethod.GET)
	@ApiOperation(value = "获取租户的配置信息")
	public Object statsResult(@PathVariable String taskId) {
		EisooAbcClient client = new EisooAbcClient(null, null, userId);
		String uri = "/v2/tasks/{taskId}/stats_result";
		uri = HttpUtils.tranUrl(uri, taskId);
		JSONObject result = client.get(uri);
		StringBuffer buffer = new StringBuffer();
		buffer.append("Sync task stats_result info. ");
		buffer.append("Parameter: [" + taskId + "]. ");
		buffer.append("Result: [" + result.toJSONString() + "]. ");
		this.warn(buffer.toString());
		return BaseRestControl.tranReturnValue(result);
	}

	
	@RequestMapping(value = "/v2/tasks/{taskId}/exec_info", method = RequestMethod.GET)
	@ApiOperation(value = "获取任务执行信息")
	public Object execInfo(@PathVariable String taskId) {
		EisooAbcClient client = new EisooAbcClient(null, null, userId);
		String uri = "/v2/tasks/{taskId}/exec_info";
		uri = HttpUtils.tranUrl(uri, taskId);
		JSONObject result = client.get(uri);
		StringBuffer buffer = new StringBuffer();
		buffer.append("Sync task exec info. ");
		buffer.append("Parameter: [" + taskId + "]. ");
		buffer.append("Result: [" + result.toJSONString() + "]. ");
		this.warn(buffer.toString());
		return BaseRestControl.tranReturnValue(result);
	}
	
	@ApiOperation(value = "获取abcToken，实现页面的单点登录")
	@RequestMapping(value = "/abctoken", method = RequestMethod.GET)
	public Object getAbcToken() {
		try {
			EisooAbcClient client = new EisooAbcClient(null, null, userId);
			if (!EisooAbcClient.isLoginResult()) {
				throw new MessageException(ResultType.abcClient_init_failure);
			}
			String token = client.getAbcToken();
			Map<String, String> map = new HashMap<String, String>();
			map.put("X-ABCloud-Auth-Token", token);
			return BaseRestControl.tranReturnValue(ResultType.success, map); 
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure); 
		}
	}
	

}
