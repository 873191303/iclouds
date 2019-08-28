package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.RecycleItemsBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.RecycleItems;
import com.h3c.iclouds.po.bean.RecoveryRecycleItemsBean;
import com.h3c.iclouds.utils.JacksonUtil;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/recycle")
public class RecycleRest extends BaseRestControl {

	@Resource
	private RecycleItemsBiz recycleItemsBiz;
	
	@Resource
	private NovaVmBiz novaVmBiz;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "回收站列表查询")
	public Object list() {
		PageEntity entity = this.beforeList();
		String classId = request.getParameter("classId");
		entity.setSpecialParam(classId);
		PageModel<Map<String, Object>> pageModel = recycleItemsBiz.findCompleteForPage(entity);
		PageList<Map<String, Object>> page = new PageList<Map<String, Object>>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除云主机")
	public Object delete(@PathVariable String id) {// 云主机id
		if (StrUtils.checkParam(id)) {
			try {
				recycleItemsBiz.delete(id);
				
				return BaseRestControl.tranReturnValue(ResultType.success);
			} catch (Exception e) {
				this.exception(NovaVm.class, e, "云主机id["+id+"]"+"非任务部分异常");
				if (e instanceof MessageException) {
					return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
				}
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error);
	}

	@RequestMapping(value = "/volume/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除云硬盘(id - resId)")
	public Object deleteVolume(@PathVariable String id) {
		try {
			recycleItemsBiz.deleteVolume(id);
			this.warn("Delete volume from recycle: " + id + "].");
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(ResultType.failure, ((MessageException) e).getResultCode());
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@RequestMapping(value = "/volume/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "恢复云硬盘")
	public Object recoverVolume(@PathVariable String id) {
		try {
			recycleItemsBiz.recoverVolume(id);
			this.warn("Recover volume from recycle: [" + id + "].");
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(ResultType.failure, ((MessageException) e).getResultCode());
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@RequestMapping(value = "", method = RequestMethod.PUT)
	@ApiOperation(value = "恢复云主机")
	public Object recovery(@RequestBody RecoveryRecycleItemsBean bean) {// 云主机id
		RecycleItems recycleItems = bean.getRecycleItems();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("resId", recycleItems.getResId());
		NovaVm novaVm = novaVmBiz.findById(NovaVm.class, recycleItems.getResId());
		// 判断是否已经恢复
		RecycleItems recycleItem = recycleItemsBiz.singleByClass(RecycleItems.class, params);
		try {
			// 还原
			if ("0".equals(recycleItems.getRecycleType())) {
				if (StrUtils.checkParam(recycleItem)) {
					try {
						Object result = recycleItemsBiz.recovery(recycleItem);
						return BaseRestControl.tranReturnValue(result);
					} catch (Exception e) {
						this.exception(this.getClass(), e);
						if (e instanceof MessageException) {
							return BaseRestControl.tranReturnValue(ResultType.failure, ((MessageException) e).getResultCode());
						}
						return BaseRestControl.tranReturnValue(ResultType.failure);
					}
					
				}
				return BaseRestControl.tranReturnValue(ResultType.failure);
			} else if ("1".equals(recycleItems.getRecycleType())) {
				if (StrUtils.checkParam(recycleItem)) {
					recycleItemsBiz.work(recycleItem);// 工单
					return BaseRestControl.tranReturnValue(ResultType.success);
				}
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		} catch (Exception e) {
			this.exception(NovaVm.class, e, JacksonUtil.toJSon(bean));
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}

		return BaseRestControl.tranReturnValue(ResultType.parameter_error);

	}
	
}
