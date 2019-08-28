package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.VdeviceBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Vdevice;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * Created by ykf7317 on 2017/9/5.
 */
@Api(value = "配置管理堆叠虚拟设备信息")
@RestController
@RequestMapping(value = "/net/vdevice")
public class VdeviceRest extends BaseRest<Vdevice> {
	
	@Resource
	private VdeviceBiz vdeviceBiz;
	
	@Resource
	private ProjectBiz projectBiz;
	
	@Override
	@ApiOperation(value = "获取堆叠虚拟设备列表")
	@RequestMapping(method = RequestMethod.GET)
	public Object list () {
		PageEntity entity = this.beforeList();
		PageModel<Vdevice> pageModel = vdeviceBiz.findForPage(entity);
		PageList<Vdevice> page = new PageList<>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@ApiOperation(value = "获取堆叠的虚拟设备列表")
	@RequestMapping(value = "/group/{id}", method = RequestMethod.GET)
	public Object list (@PathVariable String id) {
		PageEntity entity = this.beforeList();
		entity.setSpecialParam(id);
		PageModel<Vdevice> pageModel = vdeviceBiz.findForPage(entity);
		PageList<Vdevice> page = new PageList<>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@Override
	@ApiOperation(value = "获取堆叠虚拟设备详细信息")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object get (@PathVariable String id) {
		Vdevice vdevice = vdeviceBiz.findById(Vdevice.class, id);
		if (null == vdevice) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		return BaseRestControl.tranReturnValue(vdevice);
	}
	
	@ApiOperation(value = "堆叠虚拟设备关联租户")
	@RequestMapping(value = "/link/{id}/{tenantId}", method = RequestMethod.PUT)
	public Object link (@PathVariable String id, @PathVariable String tenantId) {
		Vdevice vdevice = vdeviceBiz.findById(Vdevice.class, id);
		if (null == vdevice) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		Project project = projectBiz.findById(Project.class, tenantId);
		if (null == project) {
			return BaseRestControl.tranReturnValue(ResultType.project_not_exist);
		}
		vdevice.setTenant(tenantId);
		vdevice.updatedUser(this.getLoginUser());
		vdeviceBiz.update(vdevice);
		return BaseRestControl.tranReturnValue(vdevice);
	}
	
	@Override
	@ApiOperation(value = "删除堆叠虚拟设备")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Object delete (@PathVariable String id) {
		return null;
	}
	
	@Override
	@ApiOperation(value = "新增堆叠虚拟设备")
	@RequestMapping(method = RequestMethod.POST)
	public Object save (@RequestBody Vdevice entity) {
		return null;
	}
	
	@Override
	@ApiOperation(value = "修改堆叠虚拟设备信息")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Object update (@PathVariable String id, @RequestBody Vdevice entity) throws IOException {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(!validatorMap.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
		}
		Vdevice vdevice = vdeviceBiz.findById(Vdevice.class, id);
		if (null == vdevice) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		InvokeSetForm.copyFormProperties(entity, vdevice);
		vdevice.updatedUser(this.getLoginUser());
		vdeviceBiz.update(vdevice);
		return BaseRestControl.tranReturnValue(ResultType.success, vdevice);
	}
}
