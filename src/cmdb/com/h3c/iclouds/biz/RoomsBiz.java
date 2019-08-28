package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.Rooms;

public interface RoomsBiz extends BaseBiz<Rooms> {

	void updateFlavor(Rooms rooms, Integer row, Integer column);

	void delete(String id);

}
