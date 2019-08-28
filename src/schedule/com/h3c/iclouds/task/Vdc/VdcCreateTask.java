package com.h3c.iclouds.task.Vdc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.FirewallBiz;
import com.h3c.iclouds.biz.NetworkBiz;
import com.h3c.iclouds.biz.PolicieBiz;
import com.h3c.iclouds.biz.RouteBiz;
import com.h3c.iclouds.biz.VdcBiz;
import com.h3c.iclouds.biz.VlbPoolBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.Firewall;
import com.h3c.iclouds.po.HealthMonitor;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.po.Policie;
import com.h3c.iclouds.po.Route;
import com.h3c.iclouds.po.Subnet;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.po.Vdc;
import com.h3c.iclouds.po.VdcInfo;
import com.h3c.iclouds.po.VlbPool;
import com.h3c.iclouds.po.VlbVip;
import com.h3c.iclouds.task.BaseTask;
import com.h3c.iclouds.utils.VdcHandle;
import com.h3c.iclouds.utils.VdcValidator;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;

import java.util.Map;

/**
 * Created by yKF7317 on 2016/12/27.
 */
public class VdcCreateTask extends BaseTask {
	
	private FirewallBiz firewallBiz = SpringContextHolder.getBean("firewallBiz");
	
	private PolicieBiz policieBiz = SpringContextHolder.getBean("policieBiz");
	
	private RouteBiz routeBiz = SpringContextHolder.getBean("routeBiz");
	
	private NetworkBiz networkBiz = SpringContextHolder.getBean("networkBiz");
	
	private VlbPoolBiz vlbPoolBiz = SpringContextHolder.getBean("vlbPoolBiz");
	
	private BaseDAO<Subnet> subnetDao = SpringContextHolder.getBean("baseDAO");
	
	private BaseDAO<HealthMonitor> healthDao = SpringContextHolder.getBean("baseDAO");
	
	private BaseDAO<VlbVip> vipDao = SpringContextHolder.getBean("baseDAO");
	
	private VdcBiz vdcBiz = SpringContextHolder.getBean("vdcBiz");
	
	public VdcCreateTask (Task task) {
		super(task);
	}
	
	@Override
	protected String handle () {
		JSONArray jsonArray = JSONObject.parseArray(task.getInput());
		Gson gson = new Gson();
		if (StrUtils.checkParam(jsonArray)) {
			Map<Integer, String> sortMap = VdcValidator.getSortMap();
			for (int i = 0; i < sortMap.size(); i++) {//根据操作顺序执行
				String sequence = sortMap.get(i);
				for (int j = 0; j < jsonArray.size(); j++) {
					Map<String, Object> vdcInfoMap = jsonArray.getJSONObject(j);
					if (sequence.equals(StrUtils.tranString(vdcInfoMap.get("sequence")))) {
						@SuppressWarnings("unchecked")
						Map<String, Object> dataMap = (Map<String, Object>) vdcInfoMap.get("data");
						VdcInfo vdcInfo = gson.fromJson(gson.toJson(vdcInfoMap), VdcInfo.class);
						vdcInfo.setData(dataMap);
						handleVdcInfo(vdcInfo);
					}
				}
			}
		}
		Vdc vdc = vdcBiz.getVdc(task.getProjectId());//操作完成解除锁定
		if (StrUtils.checkParam(vdc)) {
			vdc.setUserId(null);
			vdc.setSessionId(null);
			vdc.setVersion(null);
			vdc.setLock(false);
			vdcBiz.update(vdc);
		}
		return ConfigProperty.TASK_STATUS3_END_SUCCESS;
	}
	
	/**
	 * cloudos保存
	 *
	 * @param object
	 * @param type
	 */
	public void cloudosSave (Map<String, Object> object, String type) {
		Gson gson = new Gson();
		int currentNum = 0;
		String status;
		switch (type) {
			case ConfigProperty.RESOURCE_TYPE_FIREWALL:
				String fwJson = gson.toJson(object);
				Firewall firewall = gson.fromJson(fwJson, Firewall.class);
				Firewall fwEntity = firewallBiz.findById(Firewall.class, firewall.getId());
				Policie policie = policieBiz.findById(Policie.class, fwEntity.getPolicyId());
				for (int i = 0; i < retryNum; i++) {
					currentNum = i + 1;
					String s = firewallBiz.cloudSave(firewall, policie, this.client, null);
					if ("success".equals(s)) {//成功时直接返回
						String optionResult;
						String taskResult;
						try {
							firewallBiz.backWrite(firewall, policie);
							optionResult = s;
							taskResult = ConfigProperty.TASK_STATUS3_END_SUCCESS;
						} catch (Exception e) {//本地异常时回调cloudos删除建好的资源
							status = ConfigProperty.RESOURCE_OPTION_STATUS_CREATE_EXCEPTION;
							firewallBiz.updateStatus(firewall.getId(), status);
							firewallBiz.cloudDelete(firewall.getCloudosId(), policie.getCloudosId(), this.client);
							optionResult = status;
							taskResult = ConfigProperty.TASK_STATUS4_END_FAILURE;
							LogUtils.exception(Firewall.class, e);
						}
						this.saveTask2Exec("[参数:" + fwJson + "结果:" + optionResult + "]", taskResult, currentNum);
						return;
					}
					this.saveTask2Exec("[参数:" + fwJson + "结果:" + s + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, currentNum);
				}
				status = ConfigProperty.RESOURCE_OPTION_STATUS_CREATE_EXCEPTION;
				firewallBiz.updateStatus(firewall.getId(), status);
				return;
			case ConfigProperty.RESOURCE_TYPE_ROUTE:
				String rtJson = gson.toJson(object);
				Route route = gson.fromJson(rtJson, Route.class);
				for (int i = 0; i < retryNum; i++) {
					currentNum = i + 1;
					String saRs = routeBiz.cloudSave(route, this.client, null);
					if ("success".equals(saRs)) {//成功时直接返回
						String optionResult;
						String taskResult;
						try {
							if (StrUtils.checkParam(route.getFwId())) {
								routeBiz.backWrite(route, "ACTIVE");
							} else {
								routeBiz.backWrite(route, null);
							}
							optionResult = saRs;
							taskResult = ConfigProperty.TASK_STATUS3_END_SUCCESS;
						} catch (Exception e) {//本地异常时回调cloudos删除建好的资源
							status = ConfigProperty.RESOURCE_OPTION_STATUS_CREATE_EXCEPTION;
							routeBiz.updateStatus(route.getId(), status);
							if (StrUtils.checkParam(route.getFwId())) {
								Firewall firewall1 = firewallBiz.findById(Firewall.class, route.getFwId());
								routeBiz.cloudLink(firewall1, route.getCloudosId(), "unlink", client);
								routeBiz.cloudDelete(route.getCloudosId(), this.client);
							} else {
								routeBiz.cloudDelete(route.getCloudosId(), this.client);
							}
							optionResult = status;
							taskResult = ConfigProperty.TASK_STATUS4_END_FAILURE;
							LogUtils.exception(Route.class, e);
						}
						this.saveTask2Exec("[参数:" + rtJson + "结果:" + optionResult + "]", taskResult, currentNum);
						return;
					}
					if ("failure".equals(saRs)) {//失败时直接返回
						this.saveTask2Exec("[参数:" + rtJson + "结果:" + saRs + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, currentNum);
						break;
					}
					this.saveTask2Exec("[参数:" + rtJson + "结果:" + saRs + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, currentNum);
				}
				status = ConfigProperty.RESOURCE_OPTION_STATUS_CREATE_EXCEPTION;
				routeBiz.updateStatus(route.getId(), status);//失败时修改数据状态为失败
				break;
			case ConfigProperty.RESOURCE_TYPE_NETWORK:
				String ntJson = gson.toJson(object);
				Network network = gson.fromJson(ntJson, Network.class);
				Network ntEntity = networkBiz.findById(Network.class, network.getId());
				Subnet subnet = subnetDao.findById(Subnet.class, ntEntity.getSubnetId());
				for (int i = 0; i < retryNum; i++) {
					currentNum = i + 1;
					String saRs = networkBiz.cloudSave(network, subnet, this.client, null);
					if ("success".equals(saRs)) {//成功时直接返回
						String optionResult;
						String taskResult;
						try {
							networkBiz.backWrite(network, subnet, this.client);
							optionResult = saRs;
							taskResult = ConfigProperty.TASK_STATUS3_END_SUCCESS;
						} catch (Exception e) {//本地异常时回调cloudos删除建好的资源
							status = ConfigProperty.RESOURCE_OPTION_STATUS_CREATE_EXCEPTION;
							networkBiz.updateStatus(network.getId(), status);
							if (StrUtils.checkParam(network.getRouteId())) {
								Route route1 = routeBiz.findById(Route.class, network.getRouteId());
								networkBiz.cloudLink(subnet.getCloudosId(), route1.getCloudosId(), client, "unlink");
								networkBiz.cloudDelete(network.getCloudosId(), subnet.getCloudosId(), this.client);
							} else {
								networkBiz.cloudDelete(network.getCloudosId(), subnet.getCloudosId(), this.client);
							}
							optionResult = status;
							taskResult = ConfigProperty.TASK_STATUS4_END_FAILURE;
							LogUtils.exception(Network.class, e);
						}
						this.saveTask2Exec("[参数:" + ntJson + "结果:" + optionResult + "]", taskResult, currentNum);
						return;
					}
					if ("failure".equals(saRs)) {//失败时直接返回
						this.saveTask2Exec("[参数:" + ntJson + "结果:" + saRs + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, currentNum);
						break;
					}
					this.saveTask2Exec("[参数:" + ntJson + "结果:" + saRs + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, currentNum);
				}
				status = ConfigProperty.RESOURCE_OPTION_STATUS_CREATE_EXCEPTION;
				networkBiz.updateStatus(network.getId(), status);
				break;
			case ConfigProperty.RESOURCE_TYPE_VLBPOOL:
				String vpJson = gson.toJson(object);
				VlbPool vlbPool = gson.fromJson(vpJson, VlbPool.class);
				VlbPool vpEntity = vlbPoolBiz.findById(VlbPool.class, vlbPool.getId());
				HealthMonitor health = healthDao.findById(HealthMonitor.class, vpEntity.getHmonitorId());
				VlbVip vlbVip = vipDao.findById(VlbVip.class, vpEntity.getVipId());
				for (int i = 0; i < retryNum; i++) {
					currentNum = i + 1;
					String saRs = vlbPoolBiz.cloudSave(vlbPool, health, vlbVip, this.client, null);
					if ("success".equals(saRs)) {//成功时直接返回
						String optionResult;
						String taskResult;
						try {
							JSONObject vipJson = vlbPoolBiz.getVipJson(vlbVip.getCloudosId(), client);
							vlbPool.setStatus(vipJson.getString("status"));
							vlbPoolBiz.backWrite(vlbPool, health, vlbVip, this.client);
							optionResult = saRs;
							taskResult = ConfigProperty.TASK_STATUS3_END_SUCCESS;
						} catch (Exception e) {//本地异常时回调cloudos删除建好的资源
							status = ConfigProperty.RESOURCE_OPTION_STATUS_CREATE_EXCEPTION;
							vlbPoolBiz.updateStatus(vlbPool.getId(), status);
							optionResult = status;
							taskResult = ConfigProperty.TASK_STATUS4_END_FAILURE;
							LogUtils.exception(Firewall.class, e);
						}
						this.saveTask2Exec("[参数:" + vpJson + "结果:" + optionResult + "]", taskResult, currentNum);
						return;
					}
					if ("failure".equals(saRs)) {//失败时直接返回
						this.saveTask2Exec("[参数:" + vpJson + "结果:" + saRs + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, currentNum);
						break;
					}
					this.saveTask2Exec("[参数:" + vpJson + "结果:" + saRs + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, currentNum);
				}
				status = ConfigProperty.RESOURCE_OPTION_STATUS_CREATE_EXCEPTION;
				vlbPoolBiz.updateStatus(vlbPool.getId(), status);
				break;
			default:
				break;
		}
		
	}
	
	public void handleVdcInfo (VdcInfo vdcInfo) {
		Gson gson = new Gson();
		VdcHandle handle = new VdcHandle();
		String uuid = vdcInfo.getUuid();
		String option = vdcInfo.getOption();
		String type = vdcInfo.getType();
		Map<String, Object> data = vdcInfo.getData();
		String id = handle.getIdByUuid(uuid);
		String previousUuid = vdcInfo.getPreviousUuid();
		String previousId = handle.getIdByUuid(previousUuid);
		String status;
		switch (option) {
			case ConfigProperty.RESOURCE_OPTION_ADD:
				switch (type) {//根据类型添加
					case ConfigProperty.RESOURCE_TYPE_FIREWALL:
						cloudosSave(data, ConfigProperty.RESOURCE_TYPE_FIREWALL);
						break;
					case ConfigProperty.RESOURCE_TYPE_ROUTE:
						cloudosSave(data, ConfigProperty.RESOURCE_TYPE_ROUTE);
						break;
					case ConfigProperty.RESOURCE_TYPE_NETWORK:
						cloudosSave(data, ConfigProperty.RESOURCE_TYPE_NETWORK);
						break;
					case ConfigProperty.RESOURCE_TYPE_VLBPOOL:
						cloudosSave(data, ConfigProperty.RESOURCE_TYPE_VLBPOOL);
						break;
					default:
						break;
				}
				break;
			case ConfigProperty.RESOURCE_OPTION_UPDATE://修改
				switch (type) {
					case ConfigProperty.RESOURCE_TYPE_FIREWALL://防火墙(只能修改基本信息)
						Firewall firewall = firewallBiz.findById(Firewall.class, id);
						if (null == firewall) {
							this.saveTask2Exec("[参数:" + id + "结果:" + ResultType.deleted + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, 0);
							return;
						}
						String fwJson = gson.toJson(data);
						firewall = gson.fromJson(fwJson, Firewall.class);
						for (int i = 0; i < retryNum; i++) {
							try {//修改成功
								Map<String, Object> upRs = firewallBiz.update(id, firewall, this.client);
								String result = StrUtils.tranString(upRs.get("result"));
								if (StrUtils.tranString(ResultType.cloudos_exception).equals(result)) {
									this.saveTask2Exec("[参数:" + fwJson + "结果:" + result + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, i + 1);
								} else {
									record(fwJson, result, i);
									if (!StrUtils.tranString(ResultType.success).equals(result)) {
										status = ConfigProperty.RESOURCE_OPTION_STATUS_UPDATE_FAILURE;
										firewallBiz.updateStatus(id, status);
									}
									return;
								}
							} catch (Exception e) {
								status = ConfigProperty.RESOURCE_OPTION_STATUS_UPDATE_EXCEPTION;
								firewallBiz.updateStatus(id, status);
								LogUtils.exception(Firewall.class, e);
								this.saveTask2Exec("[参数:" + fwJson + "结果:" + status + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, i);
								return;
							}
						}
						break;
					case ConfigProperty.RESOURCE_TYPE_ROUTE://路由器(不能修改基本信息，只能连接上级和断开连接上级)
						Route route = routeBiz.findById(Route.class, id);
						if (null == route) {
							this.saveTask2Exec("[参数:" + id + "结果:" + ResultType.deleted + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, 0);
							return;
						}
						String fwId = route.getFwId();
						String rtJson = JSONObject.toJSONString(route);
						for (int i = 0; i < retryNum; i++) {
							try {
								String rs = "success";
								if (StrUtils.checkParam(previousId)) {//上级id存在时判断当前上级id是否存在以及是否与传进来的上级id相等
									if (StrUtils.checkParam(fwId)) {
										if (!previousId.equals(fwId)) {//两个上级id不一致时,先断开连接原本的上级再连接新的父级
											Map<String, Object> unRs = routeBiz.unlinkFirewall(id, this.client);
											rs = StrUtils.tranString(unRs.get("result"));
											if (StrUtils.tranString(ResultType.cloudos_exception).equals(rs)) {
												this.saveTask2Exec("[参数:" + rtJson + "结果:" + rs + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, i + 1);
											} else {
												if ("success".equals(rs)) {//断开旧的上级成功
													Map<String, Object> lnRs = routeBiz.linkFirewall(id, previousId, this.client);
													rs = StrUtils.tranString(lnRs.get("result"));
													if (StrUtils.tranString(ResultType.cloudos_exception).equals(rs)) {
														this.saveTask2Exec("[参数:" + rtJson + "结果:" + rs + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, i + 1);
													} else {
														record(rtJson, rs, i);
														if (!StrUtils.tranString(ResultType.success).equals(rs)) {
															status = ConfigProperty.RESOURCE_OPTION_STATUS_LINK_FAILURE;
															routeBiz.updateStatus(id, status);
														}
														return;
													}
												} else {//断开旧的的上级失败
													this.saveTask2Exec("[参数:" + rtJson + "结果:" + rs + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, i + 1);
													return;
												}
											}
											
										} else {//两个id一致则不需操作
											this.saveTask2Exec("[参数:" + rtJson + "结果:" + rs + "]", ConfigProperty.TASK_STATUS3_END_SUCCESS, i + 1);
											return;
										}
									} else {//原本没有上级则连接上级
										Map<String, Object> lnRs = routeBiz.linkFirewall(id, previousId, this.client);
										rs = StrUtils.tranString(lnRs.get("result"));
										if (StrUtils.tranString(ResultType.cloudos_exception).equals(rs)) {
											this.saveTask2Exec("[参数:" + rtJson + "结果:" + rs + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, i + 1);
										} else {
											record(rtJson, rs, i);
											if (!StrUtils.tranString(ResultType.success).equals(rs)) {
												status = ConfigProperty.RESOURCE_OPTION_STATUS_LINK_FAILURE;
												routeBiz.updateStatus(id, status);
											}
											return;
										}
									}
								} else {
									if (StrUtils.checkParam(fwId)) {//断开上级
										Map<String, Object> unRs = routeBiz.unlinkFirewall(id, this.client);
										rs = StrUtils.tranString(unRs.get("result"));
										if (StrUtils.tranString(ResultType.cloudos_exception).equals(rs)) {
											this.saveTask2Exec("[参数:" + rtJson + "结果:" + rs + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, i + 1);
										} else {
											record(rtJson, rs, i);
											if (!StrUtils.tranString(ResultType.success).equals(rs)) {
												status = ConfigProperty.RESOURCE_OPTION_STATUS_LINK_FAILURE;
												routeBiz.updateStatus(id, status);
											}
											return;
										}
									} else {//都没有上级id则无需操作
										this.saveTask2Exec("[参数:" + rtJson + "结果:" + rs + "]", ConfigProperty.TASK_STATUS3_END_SUCCESS, i + 1);
										return;
									}
								}
							} catch (Exception e) {
								status = ConfigProperty.RESOURCE_OPTION_STATUS_UPDATE_EXCEPTION;
								routeBiz.updateStatus(id, status);
								LogUtils.exception(Route.class, e);
								this.saveTask2Exec("[参数:" + id + "结果:" + status + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, i);
								return;
							}
						}
						break;
					case ConfigProperty.RESOURCE_TYPE_NETWORK://网络(不能修改基本信息，只能连接上级和断开连接上级)
						Network network = networkBiz.findById(Network.class, id);
						if (null == network) {
							this.saveTask2Exec("[参数:" + id + "结果:" + ResultType.deleted + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, 0);
							return;
						}
						String rtId = network.getRouteId();
						String ntJson = JSONObject.toJSONString(network);
						for (int i = 0; i < retryNum; i++) {
							try {
								String rs = "success";
								if (StrUtils.checkParam(previousId)) {//与路由器同理
									if (StrUtils.checkParam(rtId)) {
										if (!previousId.equals(rtId)) {
											Map<String, Object> unRs = networkBiz.unlinkRoute(id, this.client);
											rs = StrUtils.tranString(unRs.get("result"));
											if (StrUtils.tranString(ResultType.cloudos_exception).equals(rs)) {
												this.saveTask2Exec("[参数:" + ntJson + "结果:" + rs + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, i + 1);
											} else {
												if ("success".equals(unRs)) {
													Map<String, Object> lnRs = networkBiz.linkRoute(id, previousId, this.client);
													rs = StrUtils.tranString(lnRs.get("result"));
													if (StrUtils.tranString(ResultType.cloudos_exception).equals(rs)) {
														this.saveTask2Exec("[参数:" + ntJson + "结果:" + rs + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, i + 1);
													} else {
														record(ntJson, rs, i);
														if (!StrUtils.tranString(ResultType.success).equals(rs)) {
															status = ConfigProperty.RESOURCE_OPTION_STATUS_LINK_FAILURE;
															networkBiz.updateStatus(id, status);
														}
														return;
													}
												} else {
													this.saveTask2Exec("[参数:" + ntJson + "结果:" + rs + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, i + 1);
													return;
												}
											}
										} else {
											this.saveTask2Exec("[参数:" + ntJson + "结果:" + rs + "]", ConfigProperty.TASK_STATUS3_END_SUCCESS, i + 1);
											return;
										}
									} else {
										Map<String, Object> lnRs = networkBiz.linkRoute(id, previousId, this.client);
										rs = StrUtils.tranString(lnRs.get("result"));
										if (StrUtils.tranString(ResultType.cloudos_exception).equals(rs)) {
											this.saveTask2Exec("[参数:" + ntJson + "结果:" + rs + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, i + 1);
										} else {
											record(ntJson, rs, i);
											if (!StrUtils.tranString(ResultType.success).equals(rs)) {
												status = ConfigProperty.RESOURCE_OPTION_STATUS_LINK_FAILURE;
												networkBiz.updateStatus(id, status);
											}
											return;
										}
									}
								} else {
									if (StrUtils.checkParam(rtId)) {
										Map<String, Object> unRs = networkBiz.unlinkRoute(id, this.client);
										rs = StrUtils.tranString(unRs.get("result"));
										if (StrUtils.tranString(ResultType.cloudos_exception).equals(rs)) {
											this.saveTask2Exec("[参数:" + ntJson + "结果:" + rs + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, i + 1);
										} else {
											record(ntJson, rs, i);
											if (!StrUtils.tranString(ResultType.success).equals(rs)) {
												status = ConfigProperty.RESOURCE_OPTION_STATUS_LINK_FAILURE;
												networkBiz.updateStatus(id, status);
											}
											return;
										}
									} else {
										this.saveTask2Exec("[参数:" + ntJson + "结果:" + rs + "]", ConfigProperty.TASK_STATUS3_END_SUCCESS, i + 1);
										return;
									}
								}
							} catch (Exception e) {//与路由器同理
								status = ConfigProperty.RESOURCE_OPTION_STATUS_UPDATE_EXCEPTION;
								networkBiz.updateStatus(id, status);
								LogUtils.exception(Network.class, e);
								this.saveTask2Exec("[参数:" + ntJson + "结果:" + status + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, i + 1);
								return;
							}
						}
						break;
					case ConfigProperty.RESOURCE_TYPE_VLBPOOL://负载均衡(只能修改基本信息)
						VlbPool vlbPool = vlbPoolBiz.findById(VlbPool.class, id);
						if (null == vlbPool) {
							this.saveTask2Exec("[参数:" + id + "结果:" + ResultType.deleted + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, 0);
							return;
						}
						String vpJson = gson.toJson(data);
						vlbPool = gson.fromJson(vpJson, VlbPool.class);
						for (int i = 0; i < retryNum; i++) {
							try {
								Map<String, Object> upRs = vlbPoolBiz.update(id, vlbPool, this.client);
								String result = StrUtils.tranString(upRs.get("result"));
								if (StrUtils.tranString(ResultType.cloudos_exception).equals(result)) {
									this.saveTask2Exec("[参数:" + vpJson + "结果:" + result + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, i + 1);
								} else {
									record(vpJson, result, i);
									if (!StrUtils.tranString(ResultType.success).equals(result)) {
										status = ConfigProperty.RESOURCE_OPTION_STATUS_UPDATE_FAILURE;
										vlbPoolBiz.updateStatus(id, status);
									}
									return;
								}
							} catch (Exception e) {
								status = ConfigProperty.RESOURCE_OPTION_STATUS_UPDATE_EXCEPTION;
								vlbPoolBiz.updateStatus(id, status);
								LogUtils.exception(VlbPool.class, e);
								this.saveTask2Exec("[参数:" + vpJson + "结果:" + status + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, i);
								return;
							}
						}
						break;
					default:
						break;
				}
				break;
			case ConfigProperty.RESOURCE_OPTION_DELETE:
				switch (type) {
					case ConfigProperty.RESOURCE_TYPE_FIREWALL://防火墙
						Firewall firewall = firewallBiz.findById(Firewall.class, id);
						if (null == firewall) {
							this.saveTask2Exec("[参数:" + id + "结果:" + ResultType.deleted + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, 0);
							return;
						}
						for (int i = 0; i < retryNum; i++) {
							try {
								Map<String, Object> deRs = firewallBiz.delete(id, this.client);
								String result = StrUtils.tranString(deRs.get("result"));
								if (StrUtils.tranString(ResultType.cloudos_exception).equals(result)) {
									this.saveTask2Exec("[参数:" + id + "结果:" + result + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, i + 1);
								} else {
									record(id, result, i);
									if (!StrUtils.tranString(ResultType.success).equals(result)) {
										status = ConfigProperty.RESOURCE_OPTION_STATUS_DELETE_FAILURE;
										firewallBiz.updateStatus(id, status);
									}
									return;
								}
							} catch (Exception e) {
								status = ConfigProperty.RESOURCE_OPTION_STATUS_DELETE_EXCEPTION;
								firewallBiz.updateStatus(id, status);
								LogUtils.exception(Firewall.class, e);
								this.saveTask2Exec("[参数:" + id + "结果:" + status + "]", ConfigProperty
										.TASK_STATUS4_END_FAILURE, i);
								return;
							}
						}
						break;
					case ConfigProperty.RESOURCE_TYPE_ROUTE://路由器
						Route route = routeBiz.findById(Route.class, id);
						if (null == route) {
							this.saveTask2Exec("[参数:" + id + "结果:" + ResultType.deleted + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, 0);
							return;
						}
						for (int i = 0; i < retryNum; i++) {
							try {
								String rs;
								if (StrUtils.checkParam(route.getFwId())) {//断开防火墙
									Map<String, Object> unRs = routeBiz.unlinkFirewall(id, this.client);
									rs = StrUtils.tranString(unRs.get("result"));
									if (StrUtils.tranString(ResultType.cloudos_exception).equals(rs)) {
										this.saveTask2Exec("[参数:" + id + "结果:" + rs + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, i + 1);
									} else {
										if ("success".equals(rs)) {
											Map<String, Object> deRs = routeBiz.delete(id, this.client);//删除
											rs = StrUtils.tranString(deRs.get("result"));
											if (StrUtils.tranString(ResultType.cloudos_exception).equals(rs)) {
												this.saveTask2Exec("[参数:" + id + "结果:" + rs + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, i + 1);
											} else {
												record(id, rs, i);
												if (!StrUtils.tranString(ResultType.success).equals(rs)) {
													status = ConfigProperty.RESOURCE_OPTION_STATUS_DELETE_FAILURE;
													routeBiz.updateStatus(id, status);
												}
												return;
											}
										} else {
											this.saveTask2Exec("[参数:" + id + "结果:" + rs + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, i + 1);
											return;
										}
									}
								} else {
									Map<String, Object> deRs = routeBiz.delete(id, this.client);//删除
									rs = StrUtils.tranString(deRs.get("result"));
									if (StrUtils.tranString(ResultType.cloudos_exception).equals(rs)) {
										this.saveTask2Exec("[参数:" + id + "结果:" + rs + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, i + 1);
									} else {
										record(id, rs, i);
										if (!StrUtils.tranString(ResultType.success).equals(rs)) {
											status = ConfigProperty.RESOURCE_OPTION_STATUS_DELETE_FAILURE;
											routeBiz.updateStatus(id, status);
										}
										return;
									}
								}
							} catch (Exception e) {
								status = ConfigProperty.RESOURCE_OPTION_STATUS_DELETE_EXCEPTION;
								routeBiz.updateStatus(id, status);
								LogUtils.exception(Route.class, e);
								this.saveTask2Exec("[参数:" + id + "结果:" + status + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, i);
								return;
							}
						}
						break;
					case ConfigProperty.RESOURCE_TYPE_NETWORK://网络
						Network network = networkBiz.findById(Network.class, id);
						if (null == network) {
							this.saveTask2Exec("[参数:" + id + "结果:" + ResultType.deleted + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, 0);
							return;
						}
						for (int i = 0; i < retryNum; i++) {
							try {
								String rs;
								if (StrUtils.checkParam(network.getRouteId())) {//断开路由器
									Map<String, Object> unRs = networkBiz.unlinkRoute(id, this.client);
									rs = StrUtils.tranString(unRs.get("result"));
									if (StrUtils.tranString(ResultType.cloudos_exception).equals(rs)) {
										this.saveTask2Exec("[参数:" + id + "结果:" + rs + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, i + 1);
									} else {
										Map<String, Object> deRs = networkBiz.delete(id, this.client, Boolean.FALSE);
										rs = StrUtils.tranString(deRs.get("result"));
										if (StrUtils.tranString(ResultType.cloudos_exception).equals(rs)) {
											this.saveTask2Exec("[参数:" + id + "结果:" + rs + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, i + 1);
										} else {
											record(id, rs, i);
											if (!StrUtils.tranString(ResultType.success).equals(rs)) {
												status = ConfigProperty.RESOURCE_OPTION_STATUS_DELETE_FAILURE;
												networkBiz.updateStatus(id, status);
											}
											return;
										}
									}
								} else {
									Map<String, Object> deRs = networkBiz.delete(id, this.client, Boolean.FALSE);
									rs = StrUtils.tranString(deRs.get("result"));
									if (StrUtils.tranString(ResultType.cloudos_exception).equals(rs)) {
										this.saveTask2Exec("[参数:" + id + "结果:" + rs + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, i + 1);
									} else {
										record(id, rs, i);
										if (!StrUtils.tranString(ResultType.success).equals(rs)) {
											status = ConfigProperty.RESOURCE_OPTION_STATUS_DELETE_FAILURE;
											networkBiz.updateStatus(id, status);
										}
										return;
									}
								}
							} catch (Exception e) {
								status = ConfigProperty.RESOURCE_OPTION_STATUS_DELETE_EXCEPTION;
								networkBiz.updateStatus(id, status);
								LogUtils.exception(Network.class, e);
								this.saveTask2Exec("[参数:" + id + "结果:" + status + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, i + 1);
								return;
							}
						}
						break;
					case ConfigProperty.RESOURCE_TYPE_VLBPOOL://负载均衡
						VlbPool vlbPool = vlbPoolBiz.findById(VlbPool.class, id);
						if (null == vlbPool) {
							this.saveTask2Exec("[参数:" + id + "结果:" + ResultType.deleted + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, 0);
							return;
						}
						for (int i = 0; i < retryNum; i++) {
							try {
								Map<String, Object> deRs = vlbPoolBiz.delete(id, this.client);
								String result = StrUtils.tranString(deRs.get("result"));
								if (StrUtils.tranString(ResultType.cloudos_exception).equals(result)) {
									this.saveTask2Exec("[参数:" + id + "结果:" + result + "]", ConfigProperty.TASK_STATUS5_END_CLOUDOS_ERROR, i + 1);
								} else {
									record(id, result, i);
									if (!StrUtils.tranString(ResultType.success).equals(result)) {
										status = ConfigProperty.RESOURCE_OPTION_STATUS_DELETE_FAILURE;
										vlbPoolBiz.updateStatus(id, status);
									}
									return;
								}
							} catch (Exception e) {
								status = ConfigProperty.RESOURCE_OPTION_STATUS_DELETE_EXCEPTION;
								vlbPoolBiz.updateStatus(id, status);
								LogUtils.exception(VlbPool.class, e);
								this.saveTask2Exec("[参数:" + id + "结果:" + status + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, i + 1);
								return;
							}
						}
						break;
					default:
						break;
				}
				break;
			default:
				break;
		}
	}
	
	private void record(String param, String result, int num) {
		String taskResult;
		if (StrUtils.tranString(ResultType.success).equals(result)) {
			taskResult = ConfigProperty.TASK_STATUS3_END_SUCCESS;
		} else {
			taskResult = ConfigProperty.TASK_STATUS4_END_FAILURE;
		}
		this.saveTask2Exec("[参数:" + param + "结果:" + result + "]", taskResult, num + 1);
	}
	
}
