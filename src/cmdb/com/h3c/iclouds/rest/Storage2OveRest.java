package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.Storage2OveBiz;
import com.h3c.iclouds.po.Storage2Ove;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Api(value = "资源配置存储配置", description = "资源配置存储配置")
@RestController
@RequestMapping("/cloud/storage2ove")
public class Storage2OveRest extends BaseRest<Storage2Ove> {
	@Resource
	Storage2OveBiz storage2OveBiz;

	@ApiOperation(value = "存储概况")
	@RequestMapping(value = "/situation", method = RequestMethod.GET)
	public Object storageSituation() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		List<Storage2Ove> storage2Oves = storage2OveBiz.findByPropertyName(Storage2Ove.class);
		int used = 0;
		int free = 0;
		if (StrUtils.checkParam(storage2Oves)) {
			for (Storage2Ove entity : storage2Oves) {
				used += entity.getUsedCapa();
				free += entity.getFreeCapa();
			}
		}
		map.put("storageUsed", used);
		map.put("storageFree", free);
		return BaseRestControl.tranReturnValue(map);
	}

	@Override
	public Object get(String id) {
		return null;
	}

	@Override
	public Object delete(String id) {
		return null;
	}

	@Override
	public Object save(Storage2Ove entity) {
		return null;
	}

	@Override
	public Object update(String id, Storage2Ove entity) throws IOException {
		return null;
	}

	@Override
	public Object list() {
		// TODO Auto-generated method stub
		return null;
	}

}
