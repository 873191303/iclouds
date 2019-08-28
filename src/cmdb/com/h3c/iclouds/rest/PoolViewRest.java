package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.PoolViewBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.PoolInfo;
import com.h3c.iclouds.po.PoolRelation;
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
 * Created by yKF7317 on 2017/2/18.
 */
@Api(value = "资源配置服务器主机池视图", description = "资源配置服务器主机池视图")
@RestController
@RequestMapping("/poolView")
public class PoolViewRest extends BaseRest<PoolInfo> {

    @Resource
    private PoolViewBiz poolViewBiz;

    @ApiOperation(value = "获取主机池视图信息")
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public Object get(){
        List<PoolRelation> poolRelations = poolViewBiz.getView();
        return BaseRestControl.tranReturnValue(ResultType.success, poolRelations);
    }

    @Override
    public Object list() {
        return null;
    }

    @Override
    public Object delete(@PathVariable String id) {
        return null;
    }

    @Override
    public Object save(@RequestBody PoolInfo entity) {
        return null;
    }

    @Override
    public Object update(@PathVariable String id, @RequestBody PoolInfo entity) throws IOException {
        return null;
    }

    @Override
    public Object get(@PathVariable String id) {
        return null;
    }
}
