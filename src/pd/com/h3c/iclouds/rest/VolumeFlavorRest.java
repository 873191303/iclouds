package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.VolumeBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.VolumeFlavorDao;
import com.h3c.iclouds.po.Volume;
import com.h3c.iclouds.po.VolumeFlavor;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * Created by yKF7317 on 2017/5/9.
 */
@Api(value = "云硬盘规格操作")
@RestController
@RequestMapping("/volumeflavor")
public class VolumeFlavorRest extends BaseRest<VolumeFlavor> {
	
	@Resource
	private VolumeFlavorDao volumeFlavorDao;
	
	@Resource
	private VolumeBiz volumeBiz;
	
	@Override
	@RequestMapping(method = RequestMethod.GET)
	@ApiOperation(value = "获取云硬盘规格列表")
	public Object list () {
		PageEntity entity = this.beforeList();
		PageModel<VolumeFlavor> pageModel = volumeFlavorDao.findForPage(entity);
		PageList<VolumeFlavor> page = new PageList<>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@Override
	public Object get (@PathVariable String id) {
		return null;
	}
	
	@Override
	public Object delete (@PathVariable String id) {
		try {
			VolumeFlavor flavor = volumeFlavorDao.findById(VolumeFlavor.class, id);
			if (!StrUtils.checkParam(flavor)) {
				return BaseRestControl.tranReturnValue(ResultType.deleted);
			}
			List<Volume> volumes = volumeBiz.findByPropertyName(Volume.class, "flavorId", id);
			if (StrUtils.checkCollection(volumes)) {
				return BaseRestControl.tranReturnValue(ResultType.used_by_volume);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			LogUtils.exception(VolumeFlavor.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@Override
	@RequestMapping(method = RequestMethod.POST)
	@ApiOperation(value = "创建云硬盘规格")
	public Object save (@RequestBody VolumeFlavor entity) {
		try {
			entity.setDeleted(ConfigProperty.VOLUME_DELETED0_NORMAL);
			entity.createdUser(this.getLoginUser());
			String volumeTypeName = "1".equals(entity.getVolumeType()) ? "普通硬盘" : ("2".equals(entity.getVolumeType()) ? "固态硬盘" :
					"创建云主机的系统盘");
			entity.setName(entity.getSize() + "*" + volumeTypeName);
			volumeFlavorDao.add(entity);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			LogUtils.exception(VolumeFlavor.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@Override
	public Object update (@PathVariable String id, @RequestBody VolumeFlavor entity) throws IOException {
		return null;
	}
}
