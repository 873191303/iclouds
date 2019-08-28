package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.BillBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.po.business.Bill;
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
 * 流水账单接口
 * Created by yKF7317 on 2017/8/10.
 */
@RestController
@RequestMapping("/bill")
@Api(value = "流水账单")
public class BillRest extends BaseRest<Bill> {
	
	@Resource
	private BillBiz billBiz;
	
	@Override
	@ApiOperation(value = "获取流水账单列表")
	@RequestMapping(method = RequestMethod.GET)
	public Object list () {
		PageEntity pageEntity = this.beforeList();
		PageModel<Bill> model = this.billBiz.findForPage(pageEntity);
		PageList<Bill> page = new PageList<>(model, pageEntity.getsEcho());
		Map<String, Object> result = BaseRestControl.tranReturnValue(page);
		result.put("message", this.billBiz.totalMessage(pageEntity));
		return result;
	}
	
	@ApiOperation(value = "获取流水账单汇总信息")
	@RequestMapping(value = "/total", method = RequestMethod.GET)
	public Object total () {
		PageEntity pageEntity = this.beforeList();
		return BaseRestControl.tranReturnValue(this.billBiz.totalMessage(pageEntity));
	}
	
	@Override
	public Object get (@PathVariable String id) {
		return null;
	}
	
	@Override
	public Object delete (@PathVariable String id) {
		return null;
	}
	
	@Override
	public Object save (@RequestBody Bill entity) {
		return null;
	}
	
	@Override
	public Object update (@PathVariable String id, @RequestBody Bill entity) throws IOException {
		return null;
	}
}
