package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.BillBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.BillDao;
import com.h3c.iclouds.po.business.Bill;
import com.h3c.iclouds.po.business.ListPrice2Imag;
import com.h3c.iclouds.po.business.MeasureDetail;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("billBiz")
public class BillBizImpl extends BaseBizImpl<Bill> implements BillBiz {
	
	@Resource
	private BillDao billDao;
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<ListPrice2Imag> listPrice2ImagDao;
	
	@Override
	public PageModel<Bill> findForPage (PageEntity entity) {
		return this.billDao.findForPage(entity);
	}
	
	@Override
	public Bill lastByMeasureId (String measureId) {
		List<Map<String, Object>> list = this.sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_MEASURE_BILL, StrUtils
				.createMap("measureId", measureId));
		if (StrUtils.checkCollection(list)) {
			Bill bill = new Bill();
			Map<String, Object> map = list.get(0);
			InvokeSetForm.settingForm(map, bill);
			return bill;
		}
		return null;
	}
	
	@Override
	public void create (MeasureDetail measureDetail, String createdBy, String billType, Long num) {
		ListPrice2Imag listPrice2Imag = this.listPrice2ImagDao.findById(ListPrice2Imag.class, measureDetail.getSpecId());
		String measureType = listPrice2Imag.getMeasureType();//扣费周期 0-每天扣费 1-每月扣费 2-每年扣费
		boolean isYear = ConfigProperty.BILL_TYPE_YEAR.equals(measureType);
		boolean isMonth = ConfigProperty.BILL_TYPE_MONTH.equals(measureType);
		boolean isDay = ConfigProperty.BILL_TYPE_DAY.equals(measureType);
		Calendar now = Calendar.getInstance();//当前时间
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DAY_OF_MONTH);
		if (ConfigProperty.BILL_TYPE_AUTO.equals(billType)) {// 自动扣费时检查是否到扣费时间
			Map<String, Object> queryMap = new HashMap<>();
			queryMap.put("myear", year);
			if (isMonth) {
				queryMap.put("mmonth", month);
			}
			if (isDay) {
				queryMap.put("mmonth", month);
				queryMap.put("mday", day);
			}
			queryMap.put("measureId", measureDetail.getId());
			//检查当天、当月或当年是否已经扣除过费用
			Bill bill = this.singleByClass(Bill.class, queryMap);
			if (StrUtils.checkParam(bill)) {
				return;
			}
			if (isYear && (1 != month || 1 != day)) {
				return;
			}
			if (isMonth && 1 != day) {
				return;
			}
		}
		Date endDate = measureDetail.getEndDate();//账单结束计费时间
		Date date = now.getTime();//该次扣费的扣费日期
		long startCalcu; //扣费计算开始时间
		long endCalcu = date.getTime(); //扣费计算结束时间
		if (StrUtils.checkParam(endDate)) {//如果该账单已经结束计费
			if (date.getTime() >= endDate.getTime()) {//该次扣费时账单已经停止计费 则按照停止计费时间计算
				endCalcu = endDate.getTime();
			}
		}

		// num -计费具体规格值  1、定时汇总的数据 2、创建步长类规格产品时记录的具体规格值
		if (null == num && null != measureDetail.getNum()) {
			num = (long) measureDetail.getNum();
		}
		// price -计费的单价 1、组合规格单间 2、根据具体规格值和步长价计算
		double price = measureDetail.getPrice();
		if (StrUtils.checkParam(listPrice2Imag.getStepPrice(), num) && num > listPrice2Imag.getMinValue()) {
			price = price + listPrice2Imag.getStepPrice() * (num - listPrice2Imag.getMinValue())/listPrice2Imag.getStep();
		}
		double amount = 0.0;
		
		//查询最近一次的扣费记录
		Bill bill = this.lastByMeasureId(measureDetail.getId());
		if (StrUtils.checkParam(bill)) {//之前有过扣费记录
			Date billDate = bill.getBillDate();//最近一次的扣费日期 也为该次扣费的起始计算时间
			if (StrUtils.checkParam(endDate) && billDate.getTime() >= endDate.getTime()) {
				//如果该账单已经结束计费且最后一次扣费日期在结束计费日期之后代表已经不需要再扣费
				return;
			}
			startCalcu = billDate.getTime();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(billDate);
			int billYear = calendar.get(Calendar.YEAR);
			int billMonth = calendar.get(Calendar.MONTH) + 1;
			int billDay = calendar.get(Calendar.DAY_OF_MONTH);
			if (isYear && month == billMonth && day == billDay && year > billYear) {
				amount = (year - billYear) * price;
			}
			if (isMonth && billDay == day && (year > billYear || month > billMonth)) {
				amount = (year > billYear ? (price * (12 * (year - billYear - 1 ) + 12 - billMonth + month)) : (price * (month - billMonth)));
			}
			if (isDay && (year > billYear || month > billMonth || day > billDay)) {
				amount = price * (endCalcu - startCalcu)/(24 * 3600 * 1000);
			}
		} else {//该次为第一次扣费
			startCalcu = measureDetail.getBegDate().getTime();
		}
		bill = new Bill();
		bill.setMeasureId(measureDetail.getId());
		bill.setInstanceId(measureDetail.getInstanceId());
		bill.setNum(num);
		bill.setMyear(year);
		bill.setMmonth(month);
		bill.setMday(day);
		bill.setBillDate(date);
		bill.setBillType(billType);
		int totalDay;
		if (isYear) {
			totalDay = now.getActualMaximum(Calendar.DAY_OF_YEAR);
		} else if (isMonth) {
			totalDay = now.getActualMaximum(Calendar.DAY_OF_MONTH);
		} else {
			totalDay = 1;
		}
		if (amount == 0) {
			System.out.println(((float)(endCalcu - startCalcu))/((float)24 * 3600 * 1000 * totalDay));
			amount = price * ((float)(endCalcu - startCalcu))/((float)24 * 3600 * 1000 * totalDay);
			System.out.println(amount);
		}
		bill.setAmount(amount);
		bill.createdUser(createdBy);
		this.add(bill);
	}
	
	public List<Map<String, Object>> totalMessage(PageEntity entity) {
		String sql = this.billDao.getSql(entity);
		List<Map<String, Object>> totalMessages = sqlQueryBiz.queryBySql(sql);
		return totalMessages;
	}
	
}
