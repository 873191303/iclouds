package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.po.bean.inside.SaveNovaVmBean;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.NovaFlavorDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.NovaFlavor;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.utils.JacksonUtil;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对外的云主机接口
 * Created by yKF7317 on 2017/5/15.
 */
@RestController
@RequestMapping("/v2/server")
public class OutServerRest extends BaseRestControl {
	
	@Resource
	private NovaVmBiz novaVmBiz;
	
	@Resource
	private UserBiz userBiz;
	
	@Resource
	private NovaFlavorDao flavorDao;
	
	@ApiOperation(value = "获取所有云主机信息")
	@RequestMapping(method = RequestMethod.GET)
	public Object serverInfo() {
		String tenantId = request.getParameter("tenantId");
		String userId = request.getParameter("userId");
		String vmType = request.getParameter("vmType");
		StringBuffer hql = new StringBuffer("FROM NovaVm WHERE 1 = 1 ");
		if (StrUtils.checkParam(userId)) {
			hql.append("AND owner = '"+ userId +"' ");
		} else if (StrUtils.checkParam(tenantId)) {
			hql.append("AND projectId = '"+ tenantId +"' ");
		}
		if (StrUtils.checkParam(vmType)) {
			hql.append("AND hostName LIKE '"+ vmType +"%' ");
		}
		List<NovaVm> servers = novaVmBiz.findByHql(hql.toString());
		return BaseRestControl.tranReturnValue(servers);
	}
	
	@ApiOperation(value = "创建云主机")
	@RequestMapping(method = RequestMethod.POST)
	public Object create(@RequestBody SaveNovaVmBean bean) {
		String userId = this.getLoginUser();
		User user = userBiz.findById(User.class, userId);
		if (!StrUtils.checkParam(user)) {
			return BaseRestControl.tranReturnValue(ResultType.user_not_exist);
		}
		bean.setProjectId(user.getProjectId());
		CloudosClient client = CloudosClient.create(user.getCloudosId(), user.getLoginName());
		int disk = bean.getRamdisk_gb();
		int ram = bean.getMemory_mb();
		int vcpus = bean.getVcpus();
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("disk", disk);
		queryMap.put("ram", ram);
		queryMap.put("vcpus", vcpus);
		NovaFlavor flavor = flavorDao.singleByClass(NovaFlavor.class, queryMap);
		if (!StrUtils.checkParam(flavor)) {
			return BaseRestControl.tranReturnValue(ResultType.novavm_flavor_not_exist);
		}
		bean.setFlavorId(flavor.getId());
		ResultType rs = novaVmBiz.verify(bean, client);
		if (!ResultType.success.equals(rs)) {
			return BaseRestControl.tranReturnValue(rs);
		}
		try {
			// 返回提示信息
			rs = novaVmBiz.save(bean, userId, client);
			return BaseRestControl.tranReturnValue(rs);
		} catch (Exception e) {
			if (e instanceof MessageException) {
				if (StrUtils.checkParam(bean.getError())) {
					return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode(),
							bean.getError().toJSONString());
				} else {
					return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
				}
			}
			this.exception(NovaVm.class, e, JacksonUtil.toJSon(bean));
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
}
