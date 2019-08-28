package com.h3c.iclouds.rest;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseChildRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.AsmMasterBiz;
import com.h3c.iclouds.biz.ExtAValueBiz;
import com.h3c.iclouds.biz.ExtColumnsBiz;
import com.h3c.iclouds.biz.InitCodeBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.ExtAValue;
import com.h3c.iclouds.po.ExtColumns;
import com.h3c.iclouds.po.InitCode;
import com.h3c.iclouds.utils.DateUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Api(value = "资产管理扩展列值", description = "资产管理扩展列值")
@RestController
@RequestMapping("/extColumns")
public class ExtAValueRest extends BaseChildRest<ExtAValue> {

	@Resource
	private ExtAValueBiz extAValueBiz;

	@Resource
	private ExtColumnsBiz extColumnsBiz;

	@Resource
	private AsmMasterBiz asmMasterBiz;
	
	@Resource
	private InitCodeBiz initCodeBiz;

	@Override
	@ApiOperation(value = "资源管理扩展列列列值")
	@RequestMapping(value = "/{pid}/columns", method = RequestMethod.GET)
	public Object list(@PathVariable String pid) {
		AsmMaster asm=asmMasterBiz.findById(AsmMaster.class, pid);
		if(asm==null){
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		List<ExtColumns> list1=extColumnsBiz.findByPropertyName(ExtColumns.class, "assType", asm.getAssetType());
		List<ExtAValue> list2=extAValueBiz.findByPropertyName(ExtAValue.class, "assetID", pid);
		List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
		if(StrUtils.checkParam(list1)){
			Map<String,Object> map=null;
			for(ExtColumns columns:list1){
				map=new HashMap<String ,Object>();
				map.put("seq", columns.getSeq());
				map.put("assType", columns.getAssType());
				map.put("assTypeCode", columns.getAssTypeCode());
				map.put("xcType", columns.getXcType());
				map.put("xcLength", columns.getXcLength());
				map.put("defaultValue",columns.getDefaultValue());
				map.put("extID", columns.getId());
				map.put("extName", columns.getXcName());
				map.put("assetID", asm.getId());
				map.put("assetName", asm.getAssetName());
				if(StrUtils.checkParam(list2)){
					for(ExtAValue value:list2){
						if(columns.getId().equals(value.getExtID())){
							map.put("extValue", value.getExtValue());
							map.put("id", value.getId());
						}
					}
				}else{
					map.put("extValue", null);
					map.put("id", null);
				}
				list.add(map);
			}
		}
		return BaseRestControl.tranReturnValue(list);
	}

	/**
	 * 通过资产类型获取资产类型id
	 * @param pid
	 * @return
	 */
	public Object getAssetType(String pid) {
		List<InitCode> initCodes=initCodeBiz.findByPropertyName(InitCode.class, "codeName", pid);
		if(StrUtils.checkCollection(initCodes)){
			return initCodes.get(0).getId();
		}
		return null;
	}
	
	@ApiOperation(value = "资产基本信息及扩展信值")
	@RequestMapping(value = "/{pid}/allColumns", method = RequestMethod.GET)
	public Object listAll(@PathVariable String pid) {
		AsmMaster asmMaster = asmMasterBiz.findById(AsmMaster.class, pid);
		if (asmMaster == null) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		List<ExtAValue> list = extAValueBiz.findByPropertyName(ExtAValue.class, "assetID", pid);
		if (list == null || list.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("asmMaster", asmMaster);
		map.put("extAValue", list);
		return BaseRestControl.tranReturnValue(map);
	}

	@Override
	@ApiOperation(value = "资源管理扩展列值")
	@RequestMapping(value = "/{pid}/columns/{id}", method = RequestMethod.GET)
	public Object get(@PathVariable String pid, @PathVariable String id) {
		ExtAValue entity = extAValueBiz.findById(ExtAValue.class, id);
		if (entity != null) {
			if (entity.getAssetID().equals(pid)) {
				return BaseRestControl.tranReturnValue(entity);
			}
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@Override
	@ApiOperation(value = "删除资源管理扩展列值")
	@RequestMapping(value = "/{pid}/columns/{id}", method = RequestMethod.DELETE)
	public Object delete(@PathVariable String pid, @PathVariable String id) {
		ExtAValue entity = extAValueBiz.findById(ExtAValue.class, id);
		if (entity != null) {
			if (!entity.getAssetID().equals(pid)) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
			extAValueBiz.delete(entity);
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@ApiOperation(value = "添加或更新资源管理扩展列值")
	@RequestMapping(value = "/{pid}/columns", method = RequestMethod.PUT)
	public Object save(@PathVariable String pid, @RequestBody Map<String, String> map) {
		AsmMaster asmMaster = asmMasterBiz.findById(AsmMaster.class, pid);
		if (asmMaster == null) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		List<ExtColumns> list = extColumnsBiz.findByPropertyName(ExtColumns.class, "assType", asmMaster.getAssetType());
		if (!StrUtils.checkCollection(list)) {
			return BaseRestControl.tranReturnValue(ResultType.assetType_error);
		}
//		// 验证属列类型和长度
//		if (!checkColumnsType(map, list)) {
//			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
//		}
		ExtAValue entity = null;
		try {
			if (map != null && !map.isEmpty()) {
				for (String key : map.keySet()) {
					for (ExtColumns e : list) {
						if (e.getXcName().equals(key)) {
							Map<String, String> queryMap=new HashMap<>();
							queryMap.put("extName", e.getXcName());
							queryMap.put("assetID", pid);
							List<ExtAValue> list2=extAValueBiz.findByMap(ExtAValue.class, queryMap);
							if(StrUtils.checkCollection(list2)){
								entity=list2.get(0);
								entity.setExtValue(map.get(key));
								entity.updatedUser(this.getLoginUser());
								extAValueBiz.update(entity);
							}else{
								entity = new ExtAValue();
								entity.createdUser(this.getLoginUser());
								entity.setExtID(e.getId());
								entity.setAssetID(pid);
								entity.setExtValue(map.get(key));
								entity.setExtName(key);
								extAValueBiz.add(entity);
							}
							
						}
					}
				}
				return BaseRestControl.tranReturnValue(ResultType.success);
			}
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@ApiOperation(value = "添加资源管理基本信息及扩展列")
	@RequestMapping(value = "/{pid}/allColumns", method = RequestMethod.POST) // pid:资产类型id
	public Object add(@PathVariable String pid, @RequestBody Map<String, Object> map) {
		if (map == null || map.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		AsmMaster entity = new AsmMaster();
		entity.setAssetType(pid);
		List<ExtColumns> list = extColumnsBiz.findByPropertyName(ExtColumns.class, "assetType", pid);
		if (list == null || list.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		List<ExtAValue> list2 = new ArrayList<ExtAValue>();
		Field[] fields = entity.getClass().getDeclaredFields();
		// 设置资产基本属值
		for (String key : map.keySet()) {// 遍历参数
			for (Field f : fields) { // 遍历AsmMaster 属值 并赋值
				String name = f.getName();
				if (name.equals(key)) {
					name = name.substring(0, 1).toUpperCase() + name.substring(0);
					Method m = null;
					try {
						m = entity.getClass().getMethod("set" + name, String.class);
						if (m != null) {
							m.invoke(entity, map.get(key));
						}
					} catch (Exception e) {
						e.printStackTrace();
						return BaseRestControl.tranReturnValue(ResultType.parameter_error);
					}
				}
			}
		}
		entity.createdUser(this.getLoginUser());
		entity.setId(UUID.randomUUID().toString());
		asmMasterBiz.add(entity);
		for(String key : map.keySet()){
			// 设置扩展属列值
			for (ExtColumns e : list) {
				if (e.getXcName().equals(key)) {
					ExtAValue extAValue = new ExtAValue();
					extAValue.setExtName(key);
					extAValue.setExtID(e.getId());
					extAValue.setAssetID(entity.getId());
					extAValue.setExtValue(map.get(key).toString());
					extAValue.createdUser(this.getLoginUser());
					list2.add(extAValue);
					extAValueBiz.add(extAValue);
				}
			}
		}
		return BaseRestControl.tranReturnValue(BaseRestControl.tranReturnValue(ResultType.success));
	}

	/*@RequestMapping(value = "/{pid}/columns", method = RequestMethod.PUT)
	@ApiOperation(value = "更新资产管理扩展属列列值")
	public Object update(@PathVariable String pid, @PathVariable String id, @RequestBody Map<String, String> map)
			throws IOException {
		AsmMaster asmMaster = asmMasterBiz.findById(AsmMaster.class, pid);
		if (asmMaster == null) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		List<ExtColumns> columns = extColumnsBiz.findByPropertyName(ExtColumns.class, "assType",
				asmMaster.getAssetType());
		if (!StrUtils.checkCollection(columns)) {
			return BaseRestControl.tranReturnValue(ResultType.assetType_error);
		}
		// 验证属列类型和长度
		if (!checkColumnsType(map, columns)) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		List<ExtAValue> list = extAValueBiz.findByPropertyName(ExtAValue.class, "assetID", pid);
		try {
			if (list != null && list.size() > 0 && map != null && !map.isEmpty()) {
				for (ExtAValue e : list) {
					for (String key : map.keySet()) {
						if (e.getExtName().equals(key)) {
							e.setExtValue(map.get(key));
							e.updatedUser(this.getLoginUser());
							extAValueBiz.update(e);
						}
					}
				}
				return BaseRestControl.tranReturnValue(ResultType.success, list);
			}
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}*/

	/**
	 * 验证属列列的类型和长度
	 * 
	 * @param map
	 * @param list
	 * @return
	 */
	public boolean checkColumnsType(Map<String, String> map, List<ExtColumns> list) {

		if (map == null || map.isEmpty() || list.isEmpty() || list == null) {
			return false;
		}
		for (String key : map.keySet()) {
			for (ExtColumns e : list) {
				if (e.getXcName().equals(key)) {
					// 判断属列长值
					int length = e.getXcLength();
					if (map.get(key).length() > length) {
						return false;
					}
					// 判断是否Integet类型
					if ((e.getXcType().toLowerCase().indexOf("int") > -1
							|| e.getXcType().toLowerCase().indexOf("integer") > -1)
							&& !StrUtils.isInteger(map.get(key))) {
						return false;
					}
					// Double 类型
					if ((e.getXcType().toLowerCase().indexOf("double") > -1
							|| e.getXcType().toLowerCase().indexOf("double") > -1)) {
						try {
							Double.parseDouble(map.get(key));
							return true;
						} catch (NumberFormatException e1) {
							return false;
						}
					}
					// 判断时间类型
					Date date = DateUtils.getDateByString(map.get(key));
					if ((e.getXcType().toLowerCase().indexOf("date") > -1
							|| e.getXcType().toLowerCase().indexOf("timestamp") > -1) && date == null) {
						return false;
					}
					// boolean类型
					if (e.getXcType().toLowerCase().indexOf("boolean") > -1 && !"true".equals(map.get(key))
							&& !"false".equals(map.get(key))) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public Object save(String pid, ExtAValue entity) {
		return null;
	}

	@Override
	public Object update(String pid, String id, ExtAValue entity) throws IOException {
		return null;
	}

}
