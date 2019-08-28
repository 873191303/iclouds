package com.h3c.iclouds.junit.rest;

import org.junit.Test;

import com.h3c.iclouds.po.bean.RecoveryRecycleItemsBean;
import com.h3c.iclouds.po.RecycleItems;
import com.h3c.iclouds.utils.JacksonUtil;

public class RecycleRestTest {
		String param="";
		/* param=+"{                                                     ";
		 param=+"    "updatedby": "8a9240a759438afa015943f6b2800005",  ";
		 param=+"    "createddate": 1482979980634,                     ";
		 param=+"    "resid": "8a9240a759438afa01594465ecd20038",      ";
		 param=+"    "ips": null,                                      ";
		 param=+"    "novaid": "8a9240a759438afa01594465ecd20038",     ";
		 param=+"    "inboundtime": 1482979980634,                     ";
		 param=+"    "recycletime": null,                              ";
		 param=+"    "inboundtime1": "2016-12-29 10:53:00",            ";
		 param=+"    "classid": "1",                                   ";
		 param=+"    "hostname": "yzl_admin",                          ";
		 param=+"    "createdby": "8a9240a759438afa015943f6b2800005",  ";
		 param=+"    "recycleaction": "用户删除动作",                  ";
		 param=+"    "updateddate": 1482979980634,                     ";
		 param=+"    "id": "8a9240a759487bbc0159487eb55b0008",         ";
		 param=+"    "recycletype": "0"                                ";
		 param=+"  }                                                   ";*/
	
	@Test
	public void recover() {
		RecoveryRecycleItemsBean bean=new RecoveryRecycleItemsBean();
		RecycleItems recycleItems=new RecycleItems();
		recycleItems.setResId("8a9240a759438afa01594465ecd20038");
		recycleItems.setRecycleType("0");
		bean.setRecycleItems(recycleItems);
		System.out.println(JacksonUtil.toJSon(bean));
	}
	
}
