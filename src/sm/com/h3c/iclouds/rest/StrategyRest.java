package com.h3c.iclouds.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.StrategyBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.Strategy;
import com.h3c.iclouds.thread.SendMailThread;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Api(value = "云资源策略", description = "云资源策略")
@RestController
@RequestMapping("/strategy")
public class StrategyRest {
	
	@Resource
	private StrategyBiz strategyBiz;
	
	
	@ApiOperation(value = "查询所有策略", response = Strategy.class)
	@RequestMapping(value = "/listStrategy/getall", method = RequestMethod.GET)
	public Object getStrategy() {
		List<Strategy> list = new ArrayList<>();
		try {
			list = strategyBiz.selStrategyAll();
//			list = strategyBiz.findByClazz(Strategy.class);
//			list = strategyBiz.listByClass(Strategy.class,null);
		} catch (Exception e) {
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
		return BaseRestControl.tranReturnValue(ResultType.success,list);
	}
	@ApiOperation(value = "策略修改", response = Strategy.class)
	@RequestMapping(value = "/listStrategy/update", method = RequestMethod.POST)
	public Object updateStrategy(@RequestBody JSONObject json) {
//		//查询策略表找到云主机/云硬盘对应的策略
//		List<Strategy> list = strategyBiz.selStrategyAll();
//		//云主机过期提醒天数
//		String vmday = "";
//		//云硬盘过期提醒天数
//		String hdday = "";
//		//获取过期策略设置的天数
//		for(Strategy dto : list) {
//			if("1".equals(dto.getType())) {//1表示云主机
//				vmday = dto.getDay();
//			}else if("2".equals(dto.getType())) {//2表示云硬盘
//				hdday = dto.getDay();
//			}
//		}
//        //假设云主机有两台做两个测试数据
//        String vm1 = "2018-09-10 10:10:10";
//        String vm2 = "2018-08-27 10:10:10";
//        //假设云硬盘有两台做两个测试数据
//        String hd1 = "2018-09-09 10:10:10";
//        String hd2 = "2018-08-27 10:10:10";
//		//获取当前系统时间(云主机)
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//        Calendar vm = Calendar.getInstance();
//        vm.add(Calendar.DAY_OF_YEAR, Integer.parseInt(vmday));
//        System.out.println("云主机开始提醒的时间:"+df.format(vm.getTime()));
//        Calendar hd = Calendar.getInstance();
//        hd.add(Calendar.DAY_OF_YEAR, Integer.parseInt(hdday));
//        System.out.println("云硬盘开始提醒的时间:"+df.format(hd.getTime()));
//        //int vmresumt = compare_date(df.format(vm.getTime()),vm1);
//        if(1 == compare_date(df.format(vm.getTime()),vm1)) {
//        	//发送邮件
//			//调用方式 ， emails 是邮箱，title 是标题 ， mailContent 是内容
//			SendMailThread sendMailThread = SendMailThread.create();
//			String mailContent = "测试邮件";
//			String emails = "15562751109@163.com";
//			String title = "云主机过期提醒";
//			sendMailThread.setAttachment("天翼云").setContent(mailContent.toString()).setEmails(emails).setTitle(title);
//			CacheSingleton.getInstance().startThread(sendMailThread);
//        }else if(1 == compare_date(df.format(new Date()),vm1)) {
//        	//已经过期云主机放入回收站
//        }
//        return null;
		
		
		
		
		
		
		
		
		ResultType rst = null;
		Map<String, String> map = new HashMap<>();
		try {
			map.put("day", json.getString("vmday"));
			map.put("type", "1");//1表示云主机
			rst = strategyBiz.updateStrategy(map);
			map.clear();
			map.put("day", json.getString("hdday"));
			map.put("type", "2");//2表示云硬盘
			rst = strategyBiz.updateStrategy(map);
		} catch (Exception e) {
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
		return BaseRestControl.tranReturnValue(ResultType.success,rst);
	}
	//封装时间大小比较方法
	public int compare_date(String date1,String date2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
            if (dt1.getTime() > dt2.getTime()) {
                System.out.println("dt1 在dt2前");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1在dt2后");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
		return 0;
	}



}
