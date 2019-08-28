package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseChildRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.AsmMasterBiz;
import com.h3c.iclouds.biz.Asset2DrawerBiz;
import com.h3c.iclouds.biz.Class2ItemsBiz;
import com.h3c.iclouds.biz.DrawsBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.Asset2Drawer;
import com.h3c.iclouds.po.Class2Items;
import com.h3c.iclouds.po.Draws;
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
 * Created by yKF7317 on 2017/2/17.
 */
@Api(value = "机柜", description = "机柜")
@RestController
@RequestMapping("/rooms/{roomsId}/draws")
public class DrawsRest extends BaseChildRest<Draws> {

    @Resource
    private DrawsBiz drawsBiz;

    @Resource
    private Asset2DrawerBiz asset2DrawerBiz;

    @Resource
    private AsmMasterBiz asmMasterBiz;

    @Resource
    private Class2ItemsBiz class2ItemsBiz;

    @Override
    public Object list(@PathVariable String roomsId) {
        return null;
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取机柜详细信息")
    public Object get(@PathVariable String roomsId, @PathVariable String id) {
        Draws draws = drawsBiz.findById(Draws.class, id);
        if (!StrUtils.checkParam(draws)){
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        List<Asset2Drawer> asset2Drawers = asset2DrawerBiz.findByPropertyName(Asset2Drawer.class, "drawsId", id);
        draws.setAsset2Drawers(asset2Drawers);
        return BaseRestControl.tranReturnValue(ResultType.success, draws);
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机柜")
    public Object delete(@PathVariable String roomsId, @PathVariable String id) {
        Draws draws = this.drawsBiz.findById(Draws.class, id);
        if (null == draws){
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        if(!draws.getRoomId().equals(roomsId)){
            return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
        }
        List<Asset2Drawer> asset2Drawers = asset2DrawerBiz.findByPropertyName(Asset2Drawer.class, "drawsId", id);
        if (StrUtils.checkParam(asset2Drawers)){
            return BaseRestControl.tranReturnValue(ResultType.draw_has_asm);
        }
        try {
            drawsBiz.delete(draws);
            return BaseRestControl.tranReturnValue(ResultType.success);
        } catch (Exception e) {
            this.exception(Draws.class, e);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @Override
    public Object save(@PathVariable String roomsId, @RequestBody Draws entity) {
        return null;
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改机柜")
    public Object update(@PathVariable String roomsId, @PathVariable String id, @RequestBody Draws entity) throws
            IOException {
        try {
            Draws draws = this.drawsBiz.findById(Draws.class, id);
            if (null == draws){
                return BaseRestControl.tranReturnValue(ResultType.deleted);
            }
            if(!draws.getRoomId().equals(roomsId)){
                return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
            }
            Integer max = draws.getMaxU();
            if (entity.getMaxU() != max){
                if(max <= 0) {
                    return BaseRestControl.tranReturnValue(ResultType.parameter_error);
                }
                Asset2Drawer draw = asset2DrawerBiz.findMaxUByDrawId(id);
                if(draw != null) {
                    AsmMaster asmMaster = this.asmMasterBiz.findById(AsmMaster.class, draw.getId());
                    if(asmMaster != null && asmMaster.getAssMode() != null) {
                        Class2Items item = class2ItemsBiz.findById(Class2Items.class, asmMaster.getAssMode());
                        int maxU = item.getUnum() + draw.getUnumb() - 1;
                        if(maxU > max) {	// 机柜最大U数大于当前U数
                            return BaseRestControl.tranReturnValue(ResultType.draw_less_than_origin);
                        }
                    }
                }
                draws.setMaxU(entity.getMaxU());
            }
            if (!draws.getIsStandard().equals(entity.getIsStandard())){
                draws.setIsStandard(entity.getIsStandard());
            }
            if (!draws.getRemark().equals(entity.getRemark())){
                draws.setRemark(entity.getRemark());
            }
            draws.updatedUser(this.getLoginUser());
            drawsBiz.update(draws);
            return BaseRestControl.tranReturnValue(ResultType.success);
        } catch (Exception e) {
            this.exception(Draws.class, e);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @RequestMapping(value = "/{id}/use", method = RequestMethod.PUT)
    @ApiOperation(value = "机柜启用停用")
    public Object use(@PathVariable String roomsId, @PathVariable String id) throws
            IOException {
        Draws entity = this.drawsBiz.findById(Draws.class, id);
        if (null == entity){
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        if(!entity.getRoomId().equals(roomsId)){
            return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
        }
        if (ConfigProperty.YES.equals(entity.getIsUse()) && StrUtils.checkCollection(asset2DrawerBiz.listByClass(Asset2Drawer.class, StrUtils.createMap("drawsId", id)))) {
            return BaseRestControl.tranReturnValue(ResultType.draw_has_asm);
        }
        try {
            // 设置为相反
            entity.setIsUse(ConfigProperty.YES.equals(entity.getIsUse()) ? ConfigProperty.NO : ConfigProperty.YES);
            this.drawsBiz.update(entity);
            return BaseRestControl.tranReturnValue(ResultType.success);
        } catch (NumberFormatException e) {
            this.exception(this.getClass(), e);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

}
