package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.biz.model.Handler;
import com.h3c.iclouds.biz.model.RecycleClass;
import com.h3c.iclouds.biz.model.Request;
import com.h3c.iclouds.biz.model.Response;

public class VolumeHandler extends Handler {

	@Override
	protected RecycleClass getRecycleClass() {
		// TODO Auto-generated method stub
		return new RecycleClass("2");
	}

	@Override
	public Response response(Request request) {
		// TODO Auto-generated method stub
		return null;
	}

}
