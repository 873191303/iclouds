package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.Server2OveBiz;
import com.h3c.iclouds.po.Server2Ove;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.IpRelationBiz;
import com.h3c.iclouds.biz.Server2VmBiz;
import com.h3c.iclouds.biz.VNetPortsBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.IpRelation;
import com.h3c.iclouds.po.Server2Vm;
import com.h3c.iclouds.po.VNetports;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;


/**
 * Created by yKF7317 on 2016/11/8.
 */
@Api(value = "资源配置云主机配置", description = "资源配置云主机配置")
@RestController
@RequestMapping("/cloud/server2Vm")
public class Server2VmRest extends BaseRest<Server2Vm> {

    @Resource
    private Server2VmBiz server2VmBiz;

    @Resource
    private IpRelationBiz ipRelationBiz;

    @Resource
    private VNetPortsBiz vNetPortsBiz;

    @Resource
    private Server2OveBiz server2OveBiz;
    
    @Resource
    private NovaVmBiz novaVmBiz;

    @Override
    @ApiOperation(value = "获取云主机配置信息列表", response = Server2Vm.class)
    @RequestMapping(method = RequestMethod.GET)
    public Object list() {
        PageEntity entity = this.beforeList();
        PageModel<Server2Vm> pageModel = server2VmBiz.findForPage(entity);
        PageList<Server2Vm> page = new PageList<Server2Vm>(pageModel, entity.getsEcho());
        List<Server2Vm> list = page.getAaData();
        for (Server2Vm server2Vm : list) {
            //String ips = findIpById(server2Vm.getId());
            String ips = novaVmBiz.getIpByUuid(server2Vm.getUuid(), false);
            server2Vm.setIps(ips);
        }
        return BaseRestControl.tranReturnValue(page);
    }

    @ApiOperation(value = "获取主机下云主机配置信息列表", response = Server2Vm.class)
    @RequestMapping(value = "/{previousId}/vm", method = RequestMethod.GET)
    public Object list(@PathVariable String previousId) {
        PageEntity entity = this.beforeList();
        Map<String, Object> queryMap = entity.getQueryMap();
        if (StrUtils.checkParam(queryMap.get("flag"))){
            List<Server2Ove> server2Oves = new ArrayList<>();
            if ("pool".equals(queryMap.get("flag").toString())){
                server2Oves = server2OveBiz.findByPropertyName(Server2Ove.class, "poolId", previousId);
            }
            if ("cluster".equals(queryMap.get("flag").toString())){
                server2Oves = server2OveBiz.findByPropertyName(Server2Ove.class, "custerId", previousId);
            }
            if (StrUtils.checkParam(server2Oves)){
                String [] hostIds = new String[server2Oves.size()];
                for (int i = 0; i < server2Oves.size(); i++) {
                    hostIds[i] = server2Oves.get(i).getId();
                }
                entity.setSpecialParams(hostIds);
            }else {
                entity.setSpecialParams(new String[] {"null"});
            }
        }else {
            entity.setSpecialParam(previousId);
        }
        PageModel<Server2Vm> pageModel = server2VmBiz.findForPage(entity);
        PageList<Server2Vm> page = new PageList<Server2Vm>(pageModel, entity.getsEcho());
        List<Server2Vm> list = page.getAaData();
        for (Server2Vm server2Vm : list) {
            //String ips = findIpById(server2Vm.getId());
            String ips = novaVmBiz.getIpByUuid(server2Vm.getUuid(), false);
            server2Vm.setIps(ips);
        }
        return BaseRestControl.tranReturnValue(page);
    }

    @Override
    @ApiOperation(value = "获取云主机配置详细信息", response = Server2Vm.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String id) {
        Server2Vm server2Vm = server2VmBiz.findById(Server2Vm.class, id);
        if (StrUtils.checkParam(server2Vm)){
            //String ips = findIpById(id);
            String ips = novaVmBiz.getIpByUuid(server2Vm.getUuid(), false);
            server2Vm.setIps(ips);
            return BaseRestControl.tranReturnValue(server2Vm);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    /**
     * 找出虚拟机下的所有ip并拼接成一个字符串
     * @param id
     * @return
     */
    public String findIpById(String id){
        List<VNetports> vNetportss = vNetPortsBiz.findByPropertyName(VNetports.class, "vmId", id);
        if (StrUtils.checkCollection(vNetportss)){
            StringBuffer ips = new StringBuffer();
            for (VNetports vNetports : vNetportss) {
                List<IpRelation> ipRelations = ipRelationBiz.findByPropertyName(IpRelation.class, "ncid", vNetports.getId());
                if (StrUtils.checkCollection(ipRelations)){
                    for (IpRelation ipRelation : ipRelations) {
                        String ip = ipRelation.getIp();
                        ips.append(ip + ",");
                    }
                }
            }
            if (ips.length() > 0){
                return ips.substring(0, ips.length() - 1);
            }
        }
        return null;
    }

    @Override
    public Object save(@RequestBody Server2Vm entity) {
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);
        if(validatorMap.isEmpty()) {
            entity.createdUser(this.getLoginUser());
            String id = UUID.randomUUID().toString();
            entity.setId(id);
            try {
                server2VmBiz.add(entity);
                return BaseRestControl.tranReturnValue(ResultType.success, entity);
            } catch (Exception e) {
                this.exception(getClass(), e);
                return BaseRestControl.tranReturnValue(ResultType.failure);
            }
        }
        return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
    }

    @Override
    public Object delete(@PathVariable String id) {
        Server2Vm server2Vm = server2VmBiz.findById(Server2Vm.class, id);
        if (server2Vm != null){//删除虚拟机时
            List<VNetports> vNetportses = vNetPortsBiz.findByPropertyName(VNetports.class, "vmId", id);
            if (null != vNetportses && vNetportses.size() > 0){//删除虚拟机下的所有网口
                for (VNetports vNetportse : vNetportses) {
                    vNetPortsBiz.delete(vNetportse);
                }
            }
            List<IpRelation> ipRelations = ipRelationBiz.findByPropertyName(IpRelation.class, "assetId", id);
            if (null != ipRelations && ipRelations.size() > 0){//将虚拟机下的所有ip解除绑定
                for (IpRelation ipRelation : ipRelations) {
                    ipRelationBiz.removeIp(ipRelation, ipRelation.getNcid());
                }
            }
            server2VmBiz.delete(server2Vm);
            return BaseRestControl.tranReturnValue(ResultType.success);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    public Object update(@PathVariable String id, @RequestBody Server2Vm entity) throws IOException {
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);
        if(validatorMap.isEmpty()) {
            Server2Vm server2Vm = server2VmBiz.findById(Server2Vm.class, id);
            if (server2Vm != null){
                InvokeSetForm.copyFormProperties(entity, server2Vm);
                server2Vm.updatedUser(this.getLoginUser());
                server2VmBiz.update(server2Vm);
                return BaseRestControl.tranReturnValue(ResultType.success, entity);
            }
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
    }

    @ApiOperation(value = "获取虚拟机下的数据中心IP使用信息列表", response = IpRelation.class)
    @RequestMapping(value = "/{id}/ip", method = RequestMethod.GET)
    public Object getIp(@PathVariable String id) {
        PageEntity entity = this.beforeList();
        entity.setSpecialParam(id);
        PageModel<IpRelation> pageModel = ipRelationBiz.findForPage(entity);
        PageList<IpRelation> page = new PageList<IpRelation>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }
}
