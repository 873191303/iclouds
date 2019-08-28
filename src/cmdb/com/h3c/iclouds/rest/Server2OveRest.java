package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.Server2OveBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.Server2Ove;
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
 * 资源配置主机配置
 * Created by yKF7317 on 2017/2/27.
 */
@Api(value = "资源配置主机配置", description = "资源配置主机配置")
@RestController
@RequestMapping("/cloud/server2Ove")
public class Server2OveRest extends BaseRest<Server2Ove> {

    @Resource
    private Server2OveBiz server2OveBiz;

    @Override
    @ApiOperation(value = "获取主机配置信息列表", response = Server2Ove.class)
    @RequestMapping(method = RequestMethod.GET)
    public Object list() {
        PageEntity entity = this.beforeList();
        PageModel<Server2Ove> pageModel = server2OveBiz.findForPage(entity);
        PageList<Server2Ove> page = new PageList<>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

    @ApiOperation(value = "获取主机池或集群下主机配置信息列表", response = Server2Ove.class)
    @RequestMapping(value = "/{previousId}/pool", method = RequestMethod.GET)
    public Object list(@PathVariable String previousId) {
        PageEntity entity = this.beforeList();
        entity.setSpecialParam(previousId);
        PageModel<Server2Ove> pageModel = server2OveBiz.findForPage(entity);
        PageList<Server2Ove> page = new PageList<>(pageModel, entity.getsEcho());
        List<Server2Ove> server2Oves = page.getAaData();
        if (StrUtils.checkCollection(server2Oves)) {
            for (Server2Ove server2Ove : server2Oves) {
                server2OveBiz.addParam(server2Ove);
            }
        }
        return BaseRestControl.tranReturnValue(page);
    }

    @Override
    @ApiOperation(value = "获取主机详细信息", response = Server2Ove.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String id) {
        Server2Ove server2Ove = server2OveBiz.findById(Server2Ove.class, id);
        if (StrUtils.checkParam(server2Ove)){
            server2OveBiz.addParam(server2Ove);
            return BaseRestControl.tranReturnValue(server2Ove);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    public Object delete(@PathVariable String id) {
        return null;
    }

    @Override
    public Object save(@RequestBody Server2Ove entity) {
        return null;
    }

    @Override
    public Object update(@PathVariable String id, @RequestBody Server2Ove entity) throws IOException {
        return null;
    }
    
}
