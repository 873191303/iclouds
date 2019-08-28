package com.h3c.iclouds.rest;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseChildRest;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.ListPriceBiz;
import com.h3c.iclouds.biz.Specs2KeyBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.business.ListPrice;
import com.h3c.iclouds.po.business.PrdClass;
import com.h3c.iclouds.po.business.Specs2Key;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2017/1/12.
 */

@RestController
@RequestMapping("/{classId}/specs2Key")
@Api(value = "云管理产品规格组成属性表", description = "云管理产品规格组成属性表")
public class Specs2KeyRest extends BaseChildRest<Specs2Key> {

	@Resource
	private Specs2KeyBiz specs2KeyBiz;

	@Resource(name = "baseDAO")
	private BaseDAO<PrdClass> prdClassDao;

	@Resource
	private ListPriceBiz listPriceBiz;

	@Override
	@ApiOperation(value = "获取云管理产品规格组成属性列值", response = Specs2Key.class)
	@RequestMapping(method = RequestMethod.GET)
	public Object list(@PathVariable String classId) {
		try {
			PrdClass prdClass = prdClassDao.findById(PrdClass.class, classId);
			if (!StrUtils.checkParam(prdClass)) {
				return BaseRestControl.tranReturnValue(ResultType.deleted);
			}
			PageEntity pageEntity = this.beforeList();
			pageEntity.setSpecialParam(classId);
			PageModel<Specs2Key> pageModel = specs2KeyBiz.findForPage(pageEntity);
			PageList<Specs2Key> pageList = new PageList<>(pageModel, pageEntity.getsEcho());
			return BaseRestControl.tranReturnValue(pageList);
		} catch (Exception e) {
			LogUtils.exception(Specs2Key.class, e, classId);
		}
		return BaseRestControl.tranReturnValue(ResultType.failure);
	}

	@Override
	@ApiOperation(value = "获取云管理产品规格组成属性详细信息", response = Specs2Key.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object get(@PathVariable String classId, @PathVariable String id) {
		try {
			PrdClass prdClass = prdClassDao.findById(PrdClass.class, classId);
			if (!StrUtils.checkParam(prdClass)) {
				return BaseRestControl.tranReturnValue(ResultType.deleted);
			}
			Specs2Key specs2Key = specs2KeyBiz.findById(Specs2Key.class, id);
			if (!StrUtils.checkParam(specs2Key)) {
				return BaseRestControl.tranReturnValue(ResultType.deleted);
			}
			return BaseRestControl.tranReturnValue(specs2Key);
		} catch (Exception e) {
			LogUtils.exception(Specs2Key.class, e, classId, id);
		}
		return BaseRestControl.tranReturnValue(ResultType.failure);
	}

	@Override
	@ApiOperation(value = "删除云管理产品规格组成属性", response = Specs2Key.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Object delete(@PathVariable String classId, @PathVariable String id) {
		try {
			PrdClass prdClass = prdClassDao.findById(PrdClass.class, classId);
			if (!StrUtils.checkParam(prdClass)) {
				return BaseRestControl.tranReturnValue(ResultType.deleted);
			}
			Specs2Key specs2Key = specs2KeyBiz.findById(Specs2Key.class, id);
			if (!StrUtils.checkParam(specs2Key)) {
				return BaseRestControl.tranReturnValue(ResultType.deleted);
			}
			List<ListPrice> listPrices = listPriceBiz.findByPropertyName(ListPrice.class, "classId", classId);
			if (StrUtils.checkCollection(listPrices)) {
				for (ListPrice listPrice : listPrices) {
					String spec = listPrice.getSpec();
					Map<String, Object> keyValueMap = JSONObject.parseObject(spec);
					for (Map.Entry<String, Object> keyValue : keyValueMap.entrySet()) {
						String key = keyValue.getKey();
						if (key.equals(specs2Key.getId())) {
							return BaseRestControl.tranReturnValue(ResultType.used_in_flavor_group);
						}
					}
				}
			}
			specs2KeyBiz.delete(id);
			LogUtils.warn(Specs2Key.class, id);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			LogUtils.exception(Specs2Key.class, e, id);
		}
		return BaseRestControl.tranReturnValue(ResultType.failure);
	}

	@Override
	@ApiOperation(value = "保存云管理产品规格组成属性", response = Specs2Key.class)
	@RequestMapping(method = RequestMethod.POST)
	public Object save(@PathVariable String classId, @RequestBody Specs2Key entity) {
		try {
			PrdClass prdClass = prdClassDao.findById(PrdClass.class, classId);
			if (!StrUtils.checkParam(prdClass)) {
				return BaseRestControl.tranReturnValue(ResultType.prdclass_not_exist);
			}
			entity.setClassId(classId);
			if (null == entity.getKey()) {
				entity.setKey(entity.getKeyName());
			}
			Map<String, Object> checkMap = new HashMap<>();
			checkMap.put("classId", classId);
			checkMap.put("key", entity.getKey());
			if (!specs2KeyBiz.checkRepeat(Specs2Key.class, checkMap)) {
				return BaseRestControl.tranReturnValue(ResultType.key_repeat);
			}
			checkMap.remove("key");
			checkMap.put("keyName", entity.getKeyName());
			if (!specs2KeyBiz.checkRepeat(Specs2Key.class, checkMap)) {
				return BaseRestControl.tranReturnValue(ResultType.name_repeat);
			}
			Map<String, String> validatorMap = ValidatorUtils.validator(entity);
			if (!validatorMap.isEmpty()) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
			}
			if (!specs2KeyBiz.checkUnit(entity.getKey(), entity.getUnit())) {
				return BaseRestControl.tranReturnValue(ResultType.unit_error);
			}
			entity.createdUser(this.getLoginUser());
			specs2KeyBiz.add(entity);
			LogUtils.warn(Specs2Key.class, entity);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			LogUtils.exception(Specs2Key.class, e, classId, entity);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@Override
	public Object update(@PathVariable String classId, @PathVariable String id, @RequestBody Specs2Key entity)
			throws IOException {
		return null;
	}
	
	// TODO: 2017/8/14 兼容上个版本接口 待删除
	@ApiOperation(value = "检查key值是否重复")
	@RequestMapping(value = "/check/{key}", method = RequestMethod.GET)
	public Object checkRepeat(@PathVariable String classId, @PathVariable String key) {
		boolean repeat = false;
		String id = request.getParameter("id");// 修改时传入一个id则查重时会排除对象本身
		try {
			Map<String, Object> checkMap = new HashMap<>();
			checkMap.put("classId", classId);
			checkMap.put("key", key);
			repeat = specs2KeyBiz.checkRepeat(Specs2Key.class, checkMap, id);
			if (!repeat) {// 查重(key值)
				return BaseRestControl.tranReturnValue(ResultType.key_repeat);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			LogUtils.exception(Specs2Key.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@ApiOperation(value = "检查名称和key值是否重复")
	@RequestMapping(value = "/check", method = RequestMethod.GET)
	public Object checkRepeat(@PathVariable String classId) {
		boolean repeat = false;
		String id = request.getParameter("id");// 修改时传入一个id则查重时会排除对象本身
		String name = request.getParameter("name");
		String key = request.getParameter("key");
		try {
			Map<String, Object> checkMap = new HashMap<>();
			checkMap.put("classId", classId);
			if (StrUtils.checkParam(key)) {
				checkMap.put("key", key);
				repeat = specs2KeyBiz.checkRepeat(Specs2Key.class, checkMap, id);
				if (!repeat) {// 查重(key值)
					return BaseRestControl.tranReturnValue(ResultType.key_repeat);
				}
			}
			checkMap.remove("key");
			if (StrUtils.checkParam(name)) {
				checkMap.put("keyName", name);
				repeat = specs2KeyBiz.checkRepeat(Specs2Key.class, checkMap, id);
				if (!repeat) {// 查重(key值)
					return BaseRestControl.tranReturnValue(ResultType.name_repeat);
				}
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			LogUtils.exception(Specs2Key.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

}
