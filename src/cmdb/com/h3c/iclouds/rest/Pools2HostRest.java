package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.Pools2HostBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.Pools2Host;
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
 * Created by yKF7317 on 2016/11/8.
 */
@Api(value = "资源配置服务器主机池", description = "资源配置服务器主机池")
@RestController
@RequestMapping("/pools2Host")
public class Pools2HostRest extends BaseRest<Pools2Host> {

    @Resource
    private Pools2HostBiz pools2HostBiz;

    @Override
    @ApiOperation(value = "获取服务器主机池信息列表", response = Pools2Host.class)
    @RequestMapping(method = RequestMethod.GET)
    public Object list() {
        PageEntity entity = this.beforeList();
        PageModel<Pools2Host> pageModel = pools2HostBiz.findForPage(entity);//分页查询
        PageList<Pools2Host> page = new PageList<Pools2Host>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

    @Override
    @ApiOperation(value = "获取服务器主机池详细信息", response = Pools2Host.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String id) {
        Pools2Host pools2host = pools2HostBiz.findById(Pools2Host.class, id);
        if (StrUtils.checkParam(pools2host)){
            pools2HostBiz.addParam(pools2host);
            return BaseRestControl.tranReturnValue(pools2host);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    @ApiOperation(value = "保存服务器主机池", response = Pools2Host.class)
    @RequestMapping(method = RequestMethod.POST)
    public Object save(@RequestBody Pools2Host entity) {
        try {
            return pools2HostBiz.save(entity);
        } catch (Exception e) {
            this.exception(getClass(), e);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @Override
    @ApiOperation(value = "删除服务器主机池", response = Pools2Host.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Object delete(@PathVariable String id) {
        try {
            return pools2HostBiz.delete(id);
        }catch (Exception e){
            this.exception(getClass(), e);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @Override
    @ApiOperation(value = "修改服务器主机池", response = Pools2Host.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Object update(@PathVariable String id, @RequestBody Pools2Host entity) throws IOException {
        try {
            return pools2HostBiz.update(id, entity);
        }catch (Exception e){
            this.exception(getClass(), e);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }
    
    @ApiOperation(value = "获取所有服务器主机池信息列表", response = Pools2Host.class)
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Object getAll() {
        List<Pools2Host> pools2HostList = pools2HostBiz.getAll(Pools2Host.class);
        if (StrUtils.checkCollection(pools2HostList)) {
            for (Pools2Host pools2Host : pools2HostList) {
                pools2HostBiz.addParam(pools2Host);
            }
        }
        return BaseRestControl.tranReturnValue(pools2HostList);
    }
}
