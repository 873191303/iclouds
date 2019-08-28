package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseChildRest;
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
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.PatternValidator;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;


/**
 * Created by yKF7317 on 2016/11/8.
 */
@Api(value = "资源配置虚拟机网口配置", description = "资源配置虚拟机网口配置")
@RestController
@RequestMapping("/cloud/vm/{vmId}/vNetPorts")
public class VNetPortsRest extends BaseChildRest<VNetports> {

    @Resource
    private VNetPortsBiz vNetPortsBiz;

    @Resource
    private IpRelationBiz ipRelationBiz;

    @Resource
    private Server2VmBiz server2VmBiz;

    @Override
    @ApiOperation(value = "获取服务器集群虚拟机网口配置信息列表", response = VNetports.class)
    @RequestMapping(method = RequestMethod.GET)
    public Object list(@PathVariable String vmId) {
        PageEntity entity = this.beforeList();
        entity.setSpecialParam(vmId);
        PageModel<VNetports> pageModel = vNetPortsBiz.findForPage(entity);
        PageList<VNetports> page = new PageList<VNetports>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

    @ApiOperation(value = "网口对应IP列表")
    @RequestMapping(value = "/{id}/ip", method = RequestMethod.POST)
    public Object ip(@PathVariable String id) {
        PageEntity entity = this.beforeList();
        entity.setSpecialParam(id);
        PageModel<VNetports> pageModel = vNetPortsBiz.findForPage(entity);
        PageList<VNetports> page = new PageList<VNetports>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

    @ApiOperation(value = "网口绑定IP")
    @RequestMapping(value = "/{id}/ip/link", method = RequestMethod.POST)
    public Object link(@PathVariable String id, @RequestBody Map<String, Object> map) {
        String ip = StrUtils.tranString(map.get("ip"));	// IP地址
        if(!StrUtils.checkParam(ip) || !PatternValidator.ipCheck(ip)) {
            return BaseRestControl.tranReturnValue(ResultType.parameter_error, StrUtils.createMap("ip", "ip为空或者格式错误"));
        }
        VNetports vNetports = vNetPortsBiz.findById(VNetports.class, id);
        IpRelation ipRelation = ipRelationBiz.saveIp(ip, vNetports.getVmId(), CacheSingleton.getInstance().getVmType(), id, vNetports.getGroupId());
        if(ipRelation == null) {
            return BaseRestControl.tranReturnValue(ResultType.ip_was_used);
        }
        return BaseRestControl.tranReturnValue(ResultType.success);
    }

    @ApiOperation(value = "网口解除绑定IP")
    @RequestMapping(value = "/{id}/ip/unlink", method = RequestMethod.POST)
    public Object unlink(@PathVariable String id, @RequestBody Map<String, Object> map) {
        String ip = StrUtils.tranString(map.get("ip"));	// IP地址
        if(!StrUtils.checkParam(ip) || !PatternValidator.ipCheck(ip)) {
            return BaseRestControl.tranReturnValue(ResultType.parameter_error, StrUtils.createMap("ip", "ip为空或者格式错误"));
        }
        List<IpRelation> list = ipRelationBiz.findByPropertyName(IpRelation.class, "ip", ip);
        IpRelation ipRelation = null;
        if(list != null && !list.isEmpty()) {
            ipRelation = list.get(0);
        }
        if(ipRelation != null) {
            ipRelationBiz.removeIp(ipRelation, id);
        }
        return BaseRestControl.tranReturnValue(ResultType.success);
    }

    @Override
    @ApiOperation(value = "获取虚拟机网口配置详细信息", response = VNetports.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String vmId, @PathVariable String id) {
        VNetports vNetports = vNetPortsBiz.findById(VNetports.class, id);
        if (vNetports != null){
            if (!vmId.equals(vNetports.getVmId())){
                return BaseRestControl.tranReturnValue(ResultType.parameter_error);
            }
            return BaseRestControl.tranReturnValue(vNetports);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    @ApiOperation(value = "保存虚拟机网口配置", response = VNetports.class)
    @RequestMapping(method = RequestMethod.POST)
    public Object save(@PathVariable String vmId, @RequestBody VNetports entity) {
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);
        if(validatorMap.isEmpty()) {
            Server2Vm server2Vm = server2VmBiz.findById(Server2Vm.class, vmId);
            if (null == server2Vm){
                return BaseRestControl.tranReturnValue(ResultType.parent_code_not_exist);
            }
            Integer seq = entity.getSeq();
            List<VNetports> vNetPortses = vNetPortsBiz.findByPropertyName(VNetports.class, "vmId", vmId);
            for (VNetports vNetPortse : vNetPortses) {
                if (StrUtils.tranString(vNetPortse.getSeq()).equals(StrUtils.tranString(seq))){
                    return BaseRestControl.tranReturnValue(ResultType.repeat);
                }
            }
            entity.setVmId(vmId);
            entity.createdUser(this.getLoginUser());
            try {
                vNetPortsBiz.add(entity);
                return BaseRestControl.tranReturnValue(ResultType.success, entity);
            } catch (Exception e) {
                this.exception(getClass(), e);
                return BaseRestControl.tranReturnValue(ResultType.failure);
            }
        }
        return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
    }

    @Override
    @ApiOperation(value = "删除虚拟机网口配置", response = VNetports.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Object delete(@PathVariable String vmId, @PathVariable String id) {
        VNetports vNetports = vNetPortsBiz.findById(VNetports.class, id);
        if (vNetports != null){
            if (!vmId.equals(vNetports.getVmId())){
                return BaseRestControl.tranReturnValue(ResultType.parameter_error);
            }
            List<IpRelation> ipRelations = ipRelationBiz.findByPropertyName(IpRelation.class, "ncid", id);
            if (null != ipRelations && ipRelations.size() > 0){
                for (IpRelation ipRelation : ipRelations) {
                    ipRelationBiz.removeIp(ipRelation, id);
                }
            }
            vNetPortsBiz.delete(vNetports);
            return BaseRestControl.tranReturnValue(ResultType.success);
        }
        return BaseRestControl.tranReturnValue(ResultType.deleted);
    }

    @Override
    @ApiOperation(value = "修改虚拟机网口配置", response = VNetports.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Object update(@PathVariable String vmId, @PathVariable String id, @RequestBody VNetports entity) throws IOException {
        Map<String, String> validatorMap = ValidatorUtils.validator(entity);
        if(validatorMap.isEmpty()) {
            VNetports vNetports = vNetPortsBiz.findById(VNetports.class, id);
            if (vNetports != null){
                if (!vmId.equals(vNetports.getVmId())){
                    return BaseRestControl.tranReturnValue(ResultType.parameter_error);
                }
                InvokeSetForm.copyFormProperties(entity, vNetports);
                vNetports.updatedUser(this.getLoginUser());
                vNetPortsBiz.update(vNetports);
                return BaseRestControl.tranReturnValue(ResultType.success, entity);
            }
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
    }
}
