package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.business.Prd2Templates;
import com.h3c.iclouds.po.business.PrdClass;
import com.h3c.iclouds.utils.InvokeSetForm;
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
import java.util.List;
import java.util.Map;

@Api(value = "基础数据产品", description = "基础数据产品")
@RestController
@RequestMapping("/class")
public class PrdClassRest extends BaseRest<Prd2Templates> {

	@Resource(name = "baseDAO")
	private BaseDAO<Prd2Templates> prd2TemplatesDao;

	@Resource(name = "baseDAO")
	private BaseDAO<PrdClass> prdClassDao;

	@Override
	@ApiOperation(value = "获取基础数据产品列表")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Object list() {
		List<PrdClass> list = prdClassDao.getAll(PrdClass.class);
		return BaseRestControl.tranReturnValue(list);
	}
	
	@ApiOperation(value = "获取基础数据产品列表")
	@RequestMapping(value = "/use", method = RequestMethod.GET)
	public Object useList() {
		List<PrdClass> list = prdClassDao.findByPropertyName(PrdClass.class, "flag", ConfigProperty.YES);
		return BaseRestControl.tranReturnValue(list);
	}
	
	@Override
	@ApiOperation(value = "获取基础数据产品详细信息")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object get(@PathVariable String id) {
		PrdClass entity = prdClassDao.findById(PrdClass.class, id);
		if (entity != null) {
			List<Prd2Templates> list = prd2TemplatesDao.findByPropertyName(Prd2Templates.class, "classId", id);
			entity.setTemplates(list);
			return BaseRestControl.tranReturnValue(entity);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@ApiOperation(value = "保存基础数据产品")
	@RequestMapping(value = "/prdClass", method = RequestMethod.POST)
	public Object save(@RequestBody PrdClass entity) {
		try {
			Map<String, String> validatorMap = ValidatorUtils.validator(entity);
			if (!validatorMap.isEmpty()) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
			}
			entity.createdUser(this.getLoginUser());
			entity.setFlag(ConfigProperty.YES);
			prdClassDao.add(entity);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			this.exception(PrdClass.class, e);
		}
		return BaseRestControl.tranReturnValue(ResultType.failure);
	}

	@ApiOperation(value = "保存基础数据产品属性模板")
	@RequestMapping(value = "/{classId}", method = RequestMethod.POST)
	public Object save(@RequestBody PrdClass entity, @PathVariable String classId) {
		try {
			PrdClass prdClass = prdClassDao.findById(PrdClass.class, classId);
			if (StrUtils.checkParam(prdClass)) {
				return BaseRestControl.tranReturnValue(ResultType.deleted);
			}
			Map<String, String> validatorMap = ValidatorUtils.validator(entity);
			if (!validatorMap.isEmpty()) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
			}
			List<Prd2Templates> list = entity.getTemplates();
			if (!StrUtils.checkCollection(list)) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
			for (Prd2Templates p : list) {
				p.createdUser(this.getLoginUser());
				prd2TemplatesDao.add(p);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			this.exception(PrdClass.class, e);
		}
		return BaseRestControl.tranReturnValue(ResultType.failure);
	}

	@ApiOperation(value = "保存基础数据及产品属性模板")
	@RequestMapping(value = "/prdClassAll", method = RequestMethod.POST)
	public Object saveAll(@RequestBody PrdClass entity) {
		try {
			Map<String, String> validatorMap = ValidatorUtils.validator(entity);
			if (!validatorMap.isEmpty()) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
			}

			entity.createdUser(this.getLoginUser()); // 保存基础产品
			prdClassDao.add(entity);

			List<Prd2Templates> list = entity.getTemplates();
			if (!StrUtils.checkCollection(list)) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
			for (Prd2Templates p : list) { // 保存产品属性模板
				p.createdUser(this.getLoginUser());
				prd2TemplatesDao.add(p);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			this.exception(PrdClass.class, e);
		}
		return BaseRestControl.tranReturnValue(ResultType.failure);
	}

	@Override
	@ApiOperation(value = "删除基础数据产品")
	@RequestMapping(value = "/{classId}", method = RequestMethod.DELETE)
	public Object delete(@PathVariable String classId) {
		try {
			PrdClass prdClass = prdClassDao.findById(PrdClass.class, classId);
			if (StrUtils.checkParam(prdClass)) {
				/*
				 * List<Prd2Templates>
				 * list=prd2TemplatesDao.findByPropertyName(Prd2Templates.class,
				 * "classId", classId); if(StrUtils.checkParam(list)){
				 * for(Prd2Templates p:list){ // 删除申请条目模板
				 * prd2TemplatesDao.delete(p); } }
				 */
				/*
				 * List<Specs2Key> s2ks=
				 * specs2KeyBiz.findByPropertyName(Specs2Key.class, "classId",
				 * classId); if(StrUtils.checkCollection(s2ks)){ for(Specs2Key
				 * s2k:s2ks){ // 删除规格属性及其值 
				 * specs2KeyBiz.delete(s2k); } }
				 */
				prdClass.setFlag(ConfigProperty.NO);
				prdClass.updatedUser(this.getLoginUser());
				prdClassDao.update(prdClass);
				return BaseRestControl.tranReturnValue(ResultType.success);
			}
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		} catch (Exception e) {
			this.exception(PrdClass.class, e);
		}
		return BaseRestControl.tranReturnValue(ResultType.failure);
	}
	
	@ApiOperation(value = "恢复基础数据产品")
	@RequestMapping(value = "/recover/{classId}", method = RequestMethod.PUT)
	public Object recover(@PathVariable String classId) {
		try {
			PrdClass prdClass = prdClassDao.findById(PrdClass.class, classId);
			if (StrUtils.checkParam(prdClass)) {
				prdClass.setFlag(ConfigProperty.YES);
				prdClass.updatedUser(this.getLoginUser());
				prdClassDao.update(prdClass);
				return BaseRestControl.tranReturnValue(ResultType.success);
			}
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		} catch (Exception e) {
			this.exception(PrdClass.class, e);
		}
		return BaseRestControl.tranReturnValue(ResultType.failure);
	}
	
	@ApiOperation(value = "删除基础数据产品详情")
	@RequestMapping(value = "/detail/{id}", method = RequestMethod.DELETE)
	public Object deleteDetail(@PathVariable String id) {
		try {
			Prd2Templates template = prd2TemplatesDao.findById(Prd2Templates.class, id);
			if (!StrUtils.checkParam(template)) {
				return BaseRestControl.tranReturnValue(ResultType.deleted);
			}
			prd2TemplatesDao.delete(template);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			this.exception(PrdClass.class, e);
		}
		return BaseRestControl.tranReturnValue(ResultType.failure);
	}

	@ApiOperation(value = "更新基础数据产品")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Object update(@PathVariable String id, @RequestBody PrdClass entity) {
		PrdClass prdClass = prdClassDao.findById(PrdClass.class, id);
		if (!StrUtils.checkParam(prdClass)) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if (!validatorMap.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
		}
		InvokeSetForm.copyFormProperties(entity, prdClass);
		prdClass.updatedUser(this.getLoginUser());
		prdClassDao.update(prdClass);
		return BaseRestControl.tranReturnValue(ResultType.success);
	}

	@ApiOperation(value = "更新基础数据产品详情")
	@RequestMapping(value = "/{id}/detail", method = RequestMethod.POST)
	public Object updateDetail(@PathVariable String id, @RequestBody PrdClass entity) {
		PrdClass prdClass = prdClassDao.findById(PrdClass.class, id);
		if (!StrUtils.checkParam(prdClass)) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if (!validatorMap.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
		}
		// 更新详细信息
		List<Prd2Templates> templates = entity.getTemplates();
		if (StrUtils.checkCollection(templates)) {
			for (Prd2Templates t : templates) {
				Prd2Templates template = prd2TemplatesDao.findById(Prd2Templates.class, t.getId());
				if (!StrUtils.checkParam(template)) {
					t.createdUser(this.getLoginUser());
					prd2TemplatesDao.add(t);
				}
				InvokeSetForm.copyFormProperties(t, template);
				t.updatedUser(this.getLoginUser());
				prd2TemplatesDao.update(template);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.success);
	}

	@Override
	public Object save(Prd2Templates entity) {
		return null;
	}

	@Override
	public Object update(String id, Prd2Templates entity) throws IOException {
		return null;
	}

}
