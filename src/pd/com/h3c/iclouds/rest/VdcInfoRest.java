package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.ArrayList;
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

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.TaskBiz;
import com.h3c.iclouds.biz.VdcBiz;
import com.h3c.iclouds.biz.VdcInfoBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.po.Vdc;
import com.h3c.iclouds.po.VdcInfo;
import com.h3c.iclouds.po.VdcView;
import com.h3c.iclouds.utils.VdcHandle;
import com.h3c.iclouds.utils.VdcValidator;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * Created by yKF7317 on 2016/11/30.
 */
@Api(value = "vdc视图功能")
@RestController
@RequestMapping("/vdcInfo")
public class VdcInfoRest extends BaseRest<VdcInfo> {

    @Resource(name = "baseDAO")
    private BaseDAO<VdcView> vdcViewDao;

    @Resource
    private VdcBiz vdcBiz;

    @Resource
    private TaskBiz taskBiz;

    @Resource
    private VdcInfoBiz vdcInfoBiz;

    @Resource
    private ProjectBiz projectBiz;

    @ApiOperation(value = "保存vdc视图信息", response = VdcView.class)
    @RequestMapping(value = "/save/{version}", method = RequestMethod.POST)
    public Object save(@RequestBody List<VdcInfo> list, @PathVariable String version){
        try {
            CloudosClient client = this.getSessionBean().getCloudClient();
            if (null == client){
                return BaseRestControl.tranReturnValue(ResultType.system_error);
            }
            if (!StrUtils.checkCollection(list)){
                unlockVdc(version);//传进空集合时解锁vdc
                return BaseRestControl.tranReturnValue(ResultType.success);
            }
            Vdc vdc = vdcBiz.getVdc(this.getProjectId());
            if (!projectBiz.checkOptionRole(vdc.getProjectId())){
                return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
            }
            request.setAttribute("id", vdc.getId());
            request.setAttribute("name", vdc.getName());
            ResultType rs = checkVdc(vdc, version, "save");//检查vdc的userId、sessionId、version是否匹配
            if(!rs.equals(ResultType.success)){
                return BaseRestControl.tranReturnValue(rs);
            }
            Map<String, Object> errorMap = VdcValidator.verifyData(list, vdc.getProjectId());
            //验证所有提交数据的参数和格式以及操作逻辑是否错误
            if (StrUtils.checkParam(errorMap)){//出错时直接返回错误
                return BaseRestControl.tranReturnValue(ResultType.file_vdc_save_validation, errorMap);
            }
            VdcHandle vdcHandle = new VdcHandle();
            vdcHandle.sortVdcInfo(list);//给操作数据规定操作顺序
            vdcInfoBiz.save(list, vdc);
            this.warn("Save vdc view, param:" + list);
            return BaseRestControl.tranReturnValue(ResultType.success);//返回操作成功
        } catch (Exception e) {
            unlockVdc(version);
            if(e instanceof MessageException) {
                return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
            }
            this.exception(VdcInfo.class, e, list);
            return BaseRestControl.tranReturnValue(ResultType.failure);//返回操作成功
        }
    }

    @ApiOperation(value = "获取某个租户vdc视图信息")
    @RequestMapping(value = "/get/{projectId}", method = RequestMethod.GET)
    public Object get(@PathVariable String projectId){
        Vdc vdc = vdcBiz.getVdc(projectId);
        Map<String, List<VdcView>> resultMap = getVdcViewByVdcId(vdc.getId());
        return BaseRestControl.tranReturnValue(ResultType.success, resultMap);
    }

    @ApiOperation(value = "获取当前租户vdc视图信息")
    @RequestMapping(method = RequestMethod.GET)
    public Object get(){
        String projectId = this.getProjectId();
        Vdc vdc = vdcBiz.getVdc(projectId);
        Map<String, List<VdcView>> resultMap = getVdcViewByVdcId(vdc.getId());
        return BaseRestControl.tranReturnValue(ResultType.success, resultMap);
    }

    @ApiOperation(value = "锁定当前vdc视图")
    @RequestMapping(value = "/lock", method = RequestMethod.GET)
    public Object lock(){
        try {
            CloudosClient client = this.getSessionBean().getCloudClient();
            if (null == client){
                return BaseRestControl.tranReturnValue(ResultType.system_error);
            }
            Vdc vdc = vdcBiz.getVdc(this.getProjectId());
            if (vdc.getLock()){//检查vdc是否已锁定
                return BaseRestControl.tranReturnValue(ResultType.vdc_option_lock);
            }
            String userId = this.getSessionBean().getUserId();
            String sessionId = this.getUserToken();
            String version = UUID.randomUUID().toString();
            Map<String, List<VdcView>> map = getVdcViewByVdcId(vdc.getId());
            Map<String, Object> result = new HashMap<>();
            result.put("data", map);
            if (StrUtils.checkParam(vdc.getUserId())){//检查是否有用户正在操作
                if (!vdc.getUserId().equals(userId)){//其他用户在操作
                    return BaseRestControl.tranReturnValue(ResultType.lock_by_other_user);
                }else {//当前用户在操作
                    if (StrUtils.checkParam(vdc.getSessionId()) && sessionId.equals(vdc.getSessionId())){//检查操作session是否与当前一致
                        vdc.setVersion(version);//一致时覆盖当前用户之前的操作版本
                        vdcBiz.update(vdc);
                        result.put("version", version);
                        return BaseRestControl.tranReturnValue(ResultType.success, result);
                    }else {
                        return BaseRestControl.tranReturnValue(ResultType.lock_by_other_address);
                    }
                }
            }else {//没有用户在操作就锁定vdc
                vdc.setUserId(userId);
                vdc.setVersion(version);
                vdc.setSessionId(sessionId);
                vdcBiz.update(vdc);
                result.put("version", version);
                return BaseRestControl.tranReturnValue(ResultType.success, result);
            }
        } catch (Exception e) {
            this.exception(Vdc.class, e);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @ApiOperation(value = "解锁当前vdc视图")
    @RequestMapping(value = "/unlock/{version}", method = RequestMethod.GET)
    public Object unlock(@PathVariable String version){
        try {
            ResultType rs = unlockVdc(version);//解除锁定
            return BaseRestControl.tranReturnValue(rs);
        } catch (Exception e) {
            this.exception(Vdc.class, e);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    /**
     * 检查vdc锁信息是否匹配
     * @param vdc
     * @param version
     * @return
     */
    public ResultType checkVdc(Vdc vdc, String version, String type){
        if (vdc.getLock()){
            return ResultType.vdc_option_lock;
        }
        String userId = this.getSessionBean().getUserId();
        String sessionId = this.getUserToken();
        if (!StrUtils.checkParam(vdc.getUserId())){//检查用户
            if ("save".equals(type)){//保存时没有其它操作用户即保存当前用户
                vdc.setUserId(userId);
                vdcBiz.update(vdc);
            }
        }else {
            if (!userId.equals(vdc.getUserId())){//检测用户是否匹配
                return ResultType.mapping_user_error;
            }
        }
        if (!StrUtils.checkParam(vdc.getSessionId())){//检查session
            if ("save".equals(type)){//保存时没有其它session操作时即保存当前session
                vdc.setSessionId(sessionId);
                vdcBiz.update(vdc);
            }
        }else {
            if (!sessionId.equals(vdc.getSessionId())){//检测session是否匹配
                return ResultType.mapping_session_error;
            }
        }
        if (!StrUtils.checkParam(vdc.getVersion())){//检查版本
            if ("save".equals(type)){//保存当前操作版本
                vdc.setVersion(version);
                vdcBiz.update(vdc);
            }
        }else {
            if (!version.equals(vdc.getVersion())){//检测版本是否匹配
                return ResultType.mapping_version_error;
            }
        }
        return ResultType.success;
    }

    /**
     * 解锁vdc
     * @param version
     */
    public ResultType unlockVdc(String version){
        String projectId = this.getProjectId();
        Vdc vdc = vdcBiz.getVdc(projectId);
        ResultType rs = checkVdc(vdc, version, "unlock");//检查user、session、版本是否匹配
        if (ResultType.success.equals(rs)){
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("projectId", projectId);
            queryMap.put("busId", vdc.getId());
            List<Task> tasks = taskBiz.findByMap(Task.class, queryMap);
            if (!StrUtils.checkParam(tasks)){//检查当前租户是否有任务正在执行，没有则解除锁定
                vdc.setUserId(null);
                vdc.setVersion(null);
                vdc.setSessionId(null);
                vdc.setLock(false);
                vdcBiz.update(vdc);
                return ResultType.success;
            }else {
                return ResultType.task_handling;
            }
        }else {
            return rs;
        }
    }

    /**
     * 获取节点的子节点id集合
     * @param vdcViews
     * @param objId
     * @return
     */
    public List<String> getChild(List<VdcView> vdcViews, String objId){
        List<String> childIds = new ArrayList<>();
        for (VdcView vdcView : vdcViews) {
            String previousId = vdcView.getPrevious();
            if (objId.equals(previousId)){
                childIds.add(vdcView.getObjId());
            }
        }
        return childIds;
    }

    @Override
    public Object delete(@PathVariable String id) {
        return null;
    }

    @Override
    public Object save(@RequestBody VdcInfo entity) {
        return null;
    }

    @Override
    public Object update(@PathVariable String id, @RequestBody VdcInfo entity) throws IOException {
        return null;
    }

    @Override
    public Object list() {
        return null;
    }

    public Map<String, List<VdcView>> getVdcViewByVdcId(String vdcId){
        List<VdcView> vdcViews = vdcViewDao.findByPropertyName(VdcView.class, "vdcId", vdcId);//找出租户对应vdc下的所有数据
        Map<String, List<VdcView>> resultMap = new HashMap<>();
        if (null != vdcViews){
            List<VdcView> firewallList = new ArrayList<>();
            List<VdcView> networkList = new ArrayList<>();
            List<VdcView> routeList = new ArrayList<>();
            List<VdcView> vlbList = new ArrayList<>();
            for (VdcView vdcView : vdcViews) {//遍历集合
                String objId = vdcView.getObjId();//得出对象id
                List<String> childIds = getChild(vdcViews, objId);//得出该对象底下的子集id集合
                vdcView.setChildIds(childIds);
                vdcView.setChildCount(childIds.size());//子集数量
                String objType = vdcView.getObjType();//根据对象类型分组
                if  ("1".equals(objType)){//1代表防火墙
                    firewallList.add(vdcView);
                }
                if ("2".equals(objType)){//2代表路由器
                    routeList.add(vdcView);
                }
                if ("3".equals(objType)){//3代表网络
                    networkList.add(vdcView);
                }
                if ("4".equals(objType)){//4代表负载均衡
                    vlbList.add(vdcView);
                }
            }
            resultMap.put("firewall", firewallList);
            resultMap.put("route", routeList);
            resultMap.put("network", networkList);
            resultMap.put("vlb", vlbList);
        }
        return resultMap;
    }

}
