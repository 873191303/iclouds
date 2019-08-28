package com.h3c.iclouds.task.novavm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.PortBiz;
import com.h3c.iclouds.biz.RenewalBiz;
import com.h3c.iclouds.biz.SyncVdcDataBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.TaskTypeProperty;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosNovaVm;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Port;
import com.h3c.iclouds.po.Rules;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.bean.nova.Server;
import com.h3c.iclouds.task.novavm.special.WriteFreeBSDIP;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.IpValidator;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.StrUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class NovaVmCreateTask extends NovaVmState {

	private NovaVmBiz novaVmBiz = SpringContextHolder.getBean("novaVmBiz");

	private UserBiz userBiz = SpringContextHolder.getBean("userBiz");

	private PortBiz portBiz = SpringContextHolder.getBean("portBiz");

	private SyncVdcDataBiz syncVdcDataBiz = SpringContextHolder.getBean("syncVdcDataBiz");

	private BaseDAO<Rules> rulesDao = SpringContextHolder.getBean("baseDAO");
	// 租期过期
	private RenewalBiz renewalBiz = SpringContextHolder.getBean("renewalBiz");

	private String uri = null;

	private JSONObject param = null;
	// 租期时间
	private String time = "";

	private CloudosNovaVm cloudosNovaVm = null;

	private Integer create_query_num = 100;

	public NovaVmCreateTask(Task task) {
		super(task);
		JSONObject re = JSONObject.parseObject(task.getInput());
		JSONObject map = re;
		map = map.getJSONObject("params").getJSONObject("server");
		Server server = new Server();
		if (StrUtils.checkParam(map)) {
			InvokeSetForm.settingForm(map, server);
		}
		uri = re.getString("uri");
		param = re.getJSONObject("params");
		time = re.getString("time");
		CacheSingleton instance = CacheSingleton.getInstance();
		create_query_num = Integer.parseInt(instance.getConfigValue("create_query_num"));
	}

	@Override
	protected String handle() {
		cloudosNovaVm = new CloudosNovaVm(client);
		busId = task.getBusId();
		// 执行操作的租户
		NovaVm novaVm = novaVmBiz.findById(NovaVm.class, busId);
		if (!StrUtils.checkParam(novaVm)) {
			LogUtils.warn(NovaVm.class, "操作用户id[" + task.getCreatedBy() + "]" + "未查询到云主机[" + busId + "]");
			this.saveTask2Exec("操作用户id[" + task.getCreatedBy() + "]" + "未查询到云主机[" + busId + "]",
					ConfigProperty.TASK_STATUS4_END_FAILURE, 0);
			return ConfigProperty.TASK_STATUS4_END_FAILURE;
		}
		for (int i = 0; i < retryNum; i++) {
			User user = userBiz.findById(User.class, novaVm.getOwner());
			CloudosClient cloudosClient = CloudosClient.create(user.getCloudosId(), user.getLoginName());
			try {
				novaVm = restoreUuid(novaVm);
				JSONObject result = cloudosClient.post(uri, param);
				if (!accepted.equals(result.getInteger("result"))) {
					// 记录请求错误信息
					LogUtils.warn(NovaVm.class, "输入参数:" + param.toJSONString() + "cloudos返回:" + result.toJSONString());
					// 保存失败的任务记录
					this.saveTask2Exec("输入参数:" + param.toJSONString() + "cloudos返回:" + result.toJSONString(),
							ConfigProperty.TASK_STATUS4_END_FAILURE, i);
					if (i + 1 < retryNum) {
						TimeUnit.MILLISECONDS.sleep(10000);
					}
					continue;
				}
				// 获得云主机id
				JSONObject serverObj = HttpUtils.getJSONObject(result, "server");
				String uuid = serverObj.getString("id");
				novaVm.setUuid(uuid);
				novaVmBiz.update(novaVm);
				final String stateUri = uri + "/" + uuid;
				// 轮询是否成功的标志
				boolean flag = false;
				JSONObject json = null;
				LogUtils.warn(NovaVmCreateTask.class, "轮询次数" + create_query_num);
				for (int j = 0; j < create_query_num; j++) {
					JSONObject vmstate = cloudosClient.get(stateUri);
					if (cloudosNovaVm.isCorrect(vmstate)) {
						// 正常情况下是含有
						json = HttpUtils.getJSONObject(vmstate, "server");
						if (null != json) {
							// 异常和未知的情况下
							String state = json.getString("status");
							if (null != state) {
								if ("ACTIVE".equals(state)) {
									flag = true;
									break;
								} else if ("ERROR".equals(state)) {
									LogUtils.warn(NovaVm.class,
											"操作用户id[" + task.getCreatedBy() + "]" + "云主机[" + busId + "]" + state);
									break;
								} else if ("Unknown".equals(state)) {
									LogUtils.warn(NovaVm.class,
											"操作用户id[" + task.getCreatedBy() + "]" + "云主机[" + busId + "]" + state);
									break;
								}
							}
						}
					}
					LogUtils.warn(NovaVmCreateTask.class, "轮询时间间隔" + 5000);
					TimeUnit.MILLISECONDS.sleep(5000);
				}
				if (flag) {

					// 回写uuid
					novaVmBiz.writeBack(novaVm, uuid);

					StringBuffer buffer = new StringBuffer();
					// 查看云主机的虚拟网卡id
					buffer.append(uri).append("/").append(uuid).append("/").append("os-interface");
					String netUrl = buffer.toString();
					TimeUnit.SECONDS.sleep(5);
					JSONObject net = cloudosClient.get(netUrl);
					Map<String, String> portMap = null;
					if (ResourceHandle.judgeResponse(net)) {
						JSONArray interfaceAttachments = HttpUtils.getJSONArray(net, "interfaceAttachments");
						if (StrUtils.checkParam(interfaceAttachments)) {
							for (int j = 0; j < interfaceAttachments.size(); j++) {
								JSONObject interfaceAttachment = interfaceAttachments.getJSONObject(j);
								portMap = cloudosNovaVm.savePort(novaVm, interfaceAttachment, task.getProjectId());
							}
						}
					} else {
						LogUtils.warn(Port.class, "操作用户id[" + task.getCreatedBy() + "]" + "获取cloudos网卡记录异常" + net);
					}
					LogUtils.warn(this.getClass(), "操作用户id[" + task.getCreatedBy() + "]" + "cloudos云主机创建成功");

					this.specialHandle(novaVm, portMap);

					 

					this.saveTask2Exec("success", ConfigProperty.TASK_STATUS3_END_SUCCESS, i);
					return ConfigProperty.TASK_STATUS3_END_SUCCESS;
				} else {
					// 删除创建过程异常云主机
					LogUtils.warn(NovaVm.class, novaVm.getVmState() + novaVm);
					novaVm.setVmState(ConfigProperty.novaVmState.get(4));// 异常
					novaVmBiz.update(novaVm);
					portBiz.subPortQuota(novaVm.getUuid(), novaVm.getProjectId(), cloudosClient);
					saveTask2Exec(json.toJSONString(), ConfigProperty.TASK_STATUS4_END_FAILURE, i);
					return ConfigProperty.TASK_STATUS4_END_FAILURE;
				}
			} catch (Exception e) {
				// 异常是否删除本地数据库云主机记录
				novaVm.setVmState(ConfigProperty.novaVmState.get(4));// 异常
				novaVmBiz.update(novaVm);
				portBiz.subPortQuota(novaVm.getUuid(), novaVm.getProjectId(), cloudosClient);
				
				e.printStackTrace();
				LogUtils.exception(this.getClass(), e, "操作用户id[" + task.getCreatedBy() + "]", task.getInput(), novaVm);
				saveTask2Exec(e.toString(), ConfigProperty.TASK_STATUS4_END_FAILURE, i);
				return ConfigProperty.TASK_STATUS4_END_FAILURE;
			} finally {
				// 保存本地租期
				renewalBiz.addRenewal(renewalBiz, userBiz, cloudosClient, "17037211-2c0c-4703-98e8-81d0202a46e7",
						time, novaVm.getProjectId(), novaVm.getUuid(), novaVm.getHostName());
				// 如果云主机成功回写了uuid则同步云主机网卡
				if (StrUtils.checkParam(novaVm.getUuid())) {
					JSONArray portArray = portBiz.getPortArray(novaVm.getUuid(), null, cloudosClient);
					if (StrUtils.checkCollection(portArray)) {
						for (int j = 0; j < portArray.size(); j++) {
							JSONObject portJson = portArray.getJSONObject(j);
							syncVdcDataBiz.syncPort(portJson, novaVm.getUuid(), novaVm.getOwner());
						}
					}
				}
			}
		}
		LogUtils.warn(this.getClass(), "操作用户id[" + task.getCreatedBy() + "]" + "cloudos云主机请求创建失败");
		novaVm.setVmState(ConfigProperty.novaVmState.get(6));// 创建失败
		novaVm.setPowerState(TaskTypeProperty.VM_POWER_STATE_SHUTDOWN);
		saveTask2Exec(task.getInput(), ConfigProperty.TASK_STATUS4_END_FAILURE, 3);
		novaVmBiz.update(novaVm);
		return ConfigProperty.TASK_STATUS4_END_FAILURE;
	}
 

	private NovaVm restoreUuid(NovaVm novaVm) {
		if (StrUtils.checkParam(novaVm.getUuid())) {
			novaVm.setUuid(null);
			novaVmBiz.update(novaVm);
		}
		return novaVm;
	}

	private String specialHandle(NovaVm entity, Map<String, String> portMap) {
		Rules rule = rulesDao.findById(Rules.class, entity.getImageRef());
		// 是否为freebsd镜像
		if (StrUtils.isFreeBSDImage(rule.getOsType())) {
			LogUtils.warn(this.getClass(), "云主机[id:" + entity.getId() + "]" + "为FreeBSD类型的云主机");

			String ipAddr = portMap.get("ipAddr");
			Map<String, String> map = new HashMap<>();
			map.put("hostname", entity.getHostName());
			map.put("ip", ipAddr);

			String cidr = portMap.get("cidr");
			String netmask = IpValidator.getMaskbyLen(IpValidator.getMask(cidr));
			map.put("netmask", netmask);

			try {
				List<String> list = WriteFreeBSDIP.handle(entity, map);
				this.addOtherMsg("Modify freeBSD success, result:" + JSONObject.toJSONString(list));
			} catch (MessageException e) {
				if (e.getResultCode() == null) {
					this.addOtherMsg(e.getMessage());
				} else {
					this.addOtherMsg("修改FreeBSD信息异常，结果为：" + e.getResultCode());
				}
			} catch (Exception e) {
				LogUtils.exception(this.getClass(), e, "修改FreeBSD信息异常");
				this.addOtherMsg("修改FreeBSD信息异常");
				e.printStackTrace();
			}
		}
		return ResultType.success.toString();
	}

}
