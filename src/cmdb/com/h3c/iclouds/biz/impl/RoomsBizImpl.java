package com.h3c.iclouds.biz.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.Asset2DrawerDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.Asset2Drawer;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.biz.RoomsBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.RoomsDao;
import com.h3c.iclouds.po.Draws;
import com.h3c.iclouds.po.Rooms;

@Service("roomsBiz")
public class RoomsBizImpl extends BaseBizImpl<Rooms> implements RoomsBiz {
	
	@Resource
	private RoomsDao roomsDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Draws> drawsDao;

	@Resource
	private Asset2DrawerDao asset2DrawerDao;

	@Override
	public PageModel<Rooms> findForPage(PageEntity entity) {
		return roomsDao.findForPage(entity);
	}
	
	@Override
	public String add(Rooms entity) {
		super.add(entity);
		List<Draws> list = new ArrayList<Draws>();
		// 是否需要添加机柜
		if(entity.getMaxCols() > 0 && entity.getMaxRows() > 0) {
			for (int i = 0; i < entity.getMaxCols(); i++) {
				for (int j = 0; j < entity.getMaxRows(); j++) {
					Draws draws = new Draws(entity.getId(), j + 1, i + 1, entity.getDefaultU(), this.getSessionBean().getUserId() == null?"junitTest":this.getSessionBean().getUserId());
					draws.setGroupId(entity.getGroupId());	// 群组与机房保持一致
					draws.setIsUse(ConfigProperty.YES);
					this.drawsDao.add(draws);
					list.add(draws);
				}
			}
		}
		entity.setDraws(list);
		return entity.getId();
	}



	@Override
	public void updateFlavor(Rooms rooms, Integer row, Integer column) {
		List<Draws> draws = drawsDao.findByPropertyName(Draws.class, "roomId", rooms.getId());
		List<Draws> keeps = new ArrayList<Draws>();
		if (row != rooms.getMaxRows() || column != rooms.getMaxCols()){
			Integer minRow = row < rooms.getMaxRows() ? row : rooms.getMaxRows();
			Integer minColumn = column < rooms.getMaxCols() ? column : rooms.getMaxCols();
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < column; j++) {
					Draws draw = getDraws(draws, j + 1, i + 1);
					if(null == draw) {	// 机柜不存在，创建机柜
						if (i+1 > minRow || j+1 > minColumn){//扩充的规格自动填充机柜,原有的部分保持不变
							draw = new Draws(rooms.getId(), i + 1, j + 1, rooms.getDefaultU(), this.getSessionBean()
									.getUserId());
							draw.setGroupId(rooms.getGroupId());	// 群组与机房保持一致
							this.drawsDao.add(draw);
						}
					}else {
						keeps.add(draw);
					}
				}
			}
			for (Draws draw : draws) {
				if(!keeps.contains(draw)) {	// 不包含
					List<Asset2Drawer> asset2Drawers = asset2DrawerDao.findByPropertyName(Asset2Drawer.class,
							"drawsId", draw.getId());
					if (StrUtils.checkParam(asset2Drawers)){
						throw new MessageException(ResultType.draw_has_asm);
					}
					this.drawsDao.delete(draw);
				}
			}
			rooms.setMaxRows(row);
			rooms.setMaxCols(column);
			roomsDao.update(rooms);
		}
	}

	/**
	 * 查询对应行列机柜
	 * @param draws
	 * @param col
	 * @param row
	 * @return
	 */
	private Draws getDraws(List<Draws> draws, int col, int row) {
		for (Draws entity : draws) {
			if(entity.getColNum() == col && entity.getRowNum() == row) {
				return entity;
			}
		}
		return null;
	}

	@Override
	public void delete(String id) {
		Rooms rooms = roomsDao.findById(Rooms.class, id);
		List<Draws> draws = drawsDao.findByPropertyName(Draws.class, "roomId", rooms.getId());
		if (StrUtils.checkParam(draws)){
			for (Draws draw : draws) {
				drawsDao.delete(draw);
			}
		}
		roomsDao.delete(rooms);
	}
}
