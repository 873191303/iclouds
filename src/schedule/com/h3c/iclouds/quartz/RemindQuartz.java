package com.h3c.iclouds.quartz;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.ListPriceBiz;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.StrategyBiz;
import com.h3c.iclouds.biz.VolumeBiz;
import com.h3c.iclouds.dao.NovaVmDao;
import com.h3c.iclouds.dao.RenewalDao;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Renewal;
import com.h3c.iclouds.po.Strategy;
import com.h3c.iclouds.po.Volume;

/**
 * 云主机/云硬盘过期策略提醒类
 * 
 * @author Gwong
 *
 */
public class RemindQuartz {

	private StrategyBiz strategyBiz;
	private NovaVmDao novaVmDao;// 云主机

	 
	private NovaVmBiz novaVmBiz; 
	private RenewalDao renewalDao; 
	private VolumeBiz volumeBiz;
	public Logger log = LoggerFactory.getLogger(RemindQuartz.class);

	// @Test
	public void begin() { 
		log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>策略租期提醒功能执行");
		strategyBiz = SpringContextHolder.getBean("strategyBiz");
		novaVmDao = SpringContextHolder.getBean("novaVmDao");// 云主机
		novaVmBiz = SpringContextHolder.getBean("novaVmBiz");
		renewalDao = SpringContextHolder.getBean("renewalDao");
		// 查询策略表找到云主机/云硬盘对应的策略
		List<Strategy> list = strategyBiz.selStrategyAll();
		// 云主机过期提醒天数
		String vmday = "";
		// 云硬盘过期提醒天数
		String hdday = "";
		// 获取过期策略设置的天数
		for (Strategy dto : list) {
			if ("1".equals(dto.getType())) {// 1表示云主机
				vmday = dto.getDay();
			} else if ("2".equals(dto.getType())) {// 2表示云硬盘
				hdday = dto.getDay();
			}
		}
		// 查询租期时间表
		List<Renewal> listDto = renewalDao.getAll(Renewal.class);
		Map<String, String> map = new HashMap<String, String>();
		for (Renewal dto : listDto) {
			Date endTime = dto.getEndTime();
			String resourceUuid = dto.getResourceUuid();
			String serviceUuid = dto.getServiceUuid();
			String email = dto.getEmail();
			// 是否删除状态
			boolean isdelete = dto.isDeleted();
			// 是否过期
			boolean isdue = dto.isIsdue();
			// 获取当前系统时间(云主机)
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
			Calendar vm = Calendar.getInstance();
			vm.add(Calendar.DAY_OF_YEAR, Integer.parseInt(vmday));
			//System.out.println("云主机开始提醒的时间:" + df.format(vm.getTime()));
			Calendar hd = Calendar.getInstance();
			hd.add(Calendar.DAY_OF_YEAR, Integer.parseInt(hdday));
			//System.out.println("云硬盘开始提醒的时间:" + df.format(hd.getTime()));
			int vmresumt = compare_date(df.format(vm.getTime()), endTime);// 过期时间是后一个参数
			map.put("uuid", resourceUuid);
			if (1 == vmresumt) {
				System.err.println("假设发送了邮件" + dto.getUuid());
				// 发送邮件
				// 调用方式 ， emails 是邮箱，title 是标题 ， mailContent 是内容
				// SendMailThread sendMailThread = SendMailThread.create();
				// String mailContent = "测试邮件";
				// String emails = "873191303@qq.com";
				// String title = "云主机过期提醒";
				// sendMailThread.setAttachment("").setContent(mailContent.toString()).setEmails(emails).setTitle(title);
				// CacheSingleton.getInstance().startThread(sendMailThread);
			} else if (1 == compare_date(df.format(new Date()), endTime) && !isdue) {
				log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>开始清理过期");
				// 已经过期云主机放入回收站
				if ("17037211-2c0c-4703-98e8-81d0202a46e7".equals(serviceUuid)) {
					// 云主机放入回收站
					// 17037211-2c0c-4703-98e8-81d0202a46e7
					List<NovaVm> novaVm = novaVmDao.findByMap(NovaVm.class, map);
					novaVmBiz.delete(novaVm.get(0).getId());
					dto.setIsdue(true);// 修改云主机过期状态
					renewalDao.update(dto);
					System.err.println("云主机过期更新了状态");
				} else if ("1c13c9ba-3ee3-4547-9d0d-79a83447cdd0".equals(serviceUuid)) {
					// 云硬盘删除
					List<Volume> listvol = volumeBiz.findByMap(Volume.class, map);
					volumeBiz.delete(listvol.get(0).getId());
					dto.setIsdue(true);// 修改云硬盘过期状态
					renewalDao.update(dto);
					System.err.println("云硬盘过期更新了状态");
				}
				map.clear();

			}
		}
	}

	// 封装时间大小比较方法
	public int compare_date(String date1, Date dt2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date dt1 = df.parse(date1);
			// Date dt2 = df.parse(date2);
			if (dt1.getTime() > dt2.getTime()) {
				// System.out.println("dt1 在dt2前");
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				// System.out.println("dt1在dt2后");
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
