package com.h3c.iclouds.utils;

import java.util.List;

import org.comet4j.core.CometContext;

public class CometUtil {


	public synchronized static boolean  check(String channel) {
		CometContext cc=CometContext.getInstance();
		List<String> list=cc.getAppModules();
		if (StrUtils.checkCollection(list)) {
			if (list.contains(channel)) {
				return true;
			}else {
				return false; 
			}
		}
		return false;
		
	}

}
