package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.utils.InvokeSetForm;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.AsmMasterBiz;
import com.h3c.iclouds.biz.Asset2DrawerBiz;
import com.h3c.iclouds.biz.DrawsBiz;
import com.h3c.iclouds.biz.RoomsBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.Asset2Drawer;
import com.h3c.iclouds.po.Draws;
import com.h3c.iclouds.po.Rooms;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * 机房模块还缺少机柜查询生成机位图的API
 * @author zkf5485
 *
 */
@Api(value = "机房", description = "机房")
@RestController
@RequestMapping("/rooms")
public class RoomsRest extends BaseRest<Rooms> {

	@Resource
	private RoomsBiz roomsBiz;
	
	@Resource
	private DrawsBiz drawsBiz;
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;

	@Resource
	private AsmMasterBiz asmMasterBiz;
	
	@Resource
	private Asset2DrawerBiz asset2drawerBiz;
	
	@Override
	@ApiOperation(value = "获取机房列表")
	@RequestMapping(method = RequestMethod.GET)
	public Object list() {
		PageEntity entity = this.beforeList();
		PageModel<Rooms> pageModel = roomsBiz.findForPage(entity);
		PageList<Rooms> page = new PageList<Rooms>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取机房")
	public Object get(@PathVariable String id) {
		Rooms entity = roomsBiz.findById(Rooms.class, id);
		if(entity != null) {
			List<Draws> draws = this.drawsBiz.findByPropertyName(Draws.class, "roomId", id);
			if(draws!=null && !draws.isEmpty()){
				for(Draws d:draws){
					List<Asset2Drawer> a2ds = asset2drawerBiz.findByPropertyName(Asset2Drawer.class, "drawsId", d.getId());
					if(a2ds!=null && !a2ds.isEmpty()){
						for(Asset2Drawer a2d:a2ds){
							AsmMaster asmMaster = asmMasterBiz.findById(AsmMaster.class, a2d.getId());
							a2d.setMaster(asmMaster);
						}
					}
					d.setAsset2Drawers(a2ds);
				}
			}
			entity.setDraws(draws);
			entity.setDrawCount(draws.size());
			return BaseRestControl.tranReturnValue(entity);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除机房")
	public Object delete(@PathVariable String id) {
		Rooms entity = roomsBiz.findById(Rooms.class, id);
		if(entity != null) {
			try {
				// 检查机房中是否还有设备
				Map<String, Object> map = StrUtils.createMap("ROOMID", entity.getId());
				int ct = sqlQueryBiz.queryByNameForCount(SqlQueryProperty.QUERY_ASM_IN_ROOM_COUNT, map);
				if(ct > 0) {
					return BaseRestControl.tranReturnValue(ResultType.draw_has_asm);
				}
				roomsBiz.delete(id);
				return BaseRestControl.tranReturnValue(ResultType.success);
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	@ApiOperation(value = "创建机房")
	public Object save(@RequestBody Rooms entity) {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			entity.createdUser(this.getLoginUser());
			try {
				roomsBiz.add(entity);
				return BaseRestControl.tranReturnValue(ResultType.success, entity);	
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "更新机房")
	public Object update(@PathVariable String id, @RequestBody Rooms entity) throws IOException {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			entity.setId(id);
			Rooms before = this.roomsBiz.findById(Rooms.class, id);
			if(before != null) {
				try {
					entity.setMaxCols(before.getMaxCols());
					entity.setMaxRows(before.getMaxRows());
					InvokeSetForm.copyFormProperties(entity, before);
					before.updatedUser(this.getSessionBean().getUserId());
					roomsBiz.update(before);
					return BaseRestControl.tranReturnValue(ResultType.success);
				} catch (Exception e) {
					this.exception(this.getClass(), e);
					return BaseRestControl.tranReturnValue(ResultType.failure);
				}
			}
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
	}

	@RequestMapping(value = "/{id}/flavor", method = RequestMethod.PUT)
	@ApiOperation(value = "更新机房规格")
	public Object updateFlavor(@PathVariable String id) throws IOException {
		Rooms rooms = this.roomsBiz.findById(Rooms.class, id);
		if(null == rooms) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		try {
			Integer row = rooms.getMaxRows();
			Integer column = rooms.getMaxCols();
			if (StrUtils.checkParam(request.getParameter("row"))){
				row = Integer.parseInt(request.getParameter("row"));
			}
			if (StrUtils.checkParam(request.getParameter("column"))){
				column = Integer.parseInt(request.getParameter("column"));
			}
			if (row <= 0 || column <= 0){
				return BaseRestControl.tranReturnValue(ResultType.row_and_column_must_bigger_than_one);
			}
			roomsBiz.updateFlavor(rooms, row, column);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			if (e instanceof MessageException){
				return BaseRestControl.tranReturnValue(((MessageException)e).getResultCode());
			}
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
}
