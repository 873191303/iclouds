package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.PfmValue2HistoryBiz;
import com.h3c.iclouds.biz.PfmValueBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.po.PfmValue;
import com.h3c.iclouds.po.PfmValue2History;
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
 * Created by yKF7317 on 2017/3/1.
 */
@Api(value = "云资源指标数据")
@RestController
@RequestMapping("/pfmValue")
public class PfmValueRest extends BaseRest<PfmValue2History> {

    @Resource
    private PfmValue2HistoryBiz pfmValue2HistoryBiz;

    @Resource
    private PfmValueBiz pfmValueBiz;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "获取云资源指标数据")
    @RequestMapping(method = RequestMethod.GET, value = "/condense")
    public Object condense() {
        PageEntity entity = this.beforeList();
        PageModel pageModel = pfmValue2HistoryBiz.findHistoryCondense(entity, 1);
        PageList page = new PageList(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    @ApiOperation(value = "获取云资源指标数据")
    @RequestMapping(method = RequestMethod.GET)
    public Object list() {
        PageEntity entity = this.beforeList();
        Integer hour=StrUtils.tranInteger(entity.getQueryMap().get("hour"));
        int condenseType=0;
		if(hour<=24 || hour==null){
			condenseType=0;
			PageModel<PfmValue2History> pageModel = pfmValue2HistoryBiz.findForPage(entity);
		    PageList<PfmValue2History> page = new PageList<PfmValue2History>(pageModel, entity.getsEcho());
		    return BaseRestControl.tranReturnValue(page);
		}else if(hour>24 && hour<=168){ // 一周
			condenseType=1;
		}else if(hour>168 && hour <=1440){ // 一个月和二个月
			condenseType=6;
		}else if(hour>1440){ // 二个月以上
			condenseType=24;
		}
		PageModel pageModel = pfmValue2HistoryBiz.findHistoryCondense(entity, condenseType);
	    PageList page = new PageList(pageModel, entity.getsEcho());
	    return BaseRestControl.tranReturnValue(page);
    }

    @ApiOperation(value = "获取某个云资源当前指标数据")
    @RequestMapping(value = "/vm/{uuid}", method = RequestMethod.GET)
    public Object list(@PathVariable String uuid) {
        List<PfmValue> pfmValues = pfmValueBiz.findByPropertyName(PfmValue.class, "uuid", uuid);
        return BaseRestControl.tranReturnValue(pfmValues);
    }

    @Override
    public Object get(@PathVariable String id) {
        return null;
    }

    @Override
    public Object delete(@PathVariable String id) {
        return null;
    }

    @Override
    public Object save(@RequestBody PfmValue2History entity) {
        return null;
    }

    @Override
    public Object update(@PathVariable String id, @RequestBody PfmValue2History entity) throws IOException {
        return null;
    }
}
