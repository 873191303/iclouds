package com.h3c.iclouds.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtils {

	/**
	 * 默认格式
	 */
	public static String format = "yyyy-MM-dd HH:mm:ss";
	
	public static String dateFormat = "yyyy-MM-dd";
	
	public static String timeFormat = "HH:mm:ss";
	
	public static String apiFormat = "yyyy-MM-dd'T'HH:mm:ss";
	
	public static Date getDateByObj(Object objDate, String format) {
		String date = StrUtils.tranString(objDate);
		Date returnDate = null;
		if(!"".equals(date)) {
			returnDate = getDateByString(date);
		}
		return returnDate;
	}
	
	public static Date getDateByObj(Object objDate) {
		return getDateByObj(objDate, format);
	}
	
	public static Date getDateByString(String date, String format) {
		if(format == null) {
			return getDateByString(date);
		}
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		try {
			return formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Date getDateByString() {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = formatter.parse(getDate());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static Date getDateByString(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = formatter.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static String getDate(String format) {
		return getDate(new Date(), format);
	}
	
	public static String getDate(Date date) {
		return getDate(date, format);
	}
	
	public static String getDate() {
		return getDate(new Date(), format);
	}
	
	public static String getDate(Date date, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}
	
	public static String getDateAfterHour(int hour) {
		return getDateAfterHour(new Date(), hour);
	}
	
	public static String formatDate(long time, String format) {
		SimpleDateFormat _df = new SimpleDateFormat(format);
		return _df.format(new Date(time));
	}
	
	public static Date parseDate(String date){
		String d = date;
		if (date.indexOf("/") > 0){
			d = date.replace("/", "-");
		}
		String temp = d + " 00:00:00";
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date time = sdf.parse(temp);
			return time;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获取当月的第几个星期几
	 * @param month
	 * @param weekInMonth
	 * @param dayInWeek
	 * @return
	 */
	public static Date getWeekDay(Date date, int weekInMonth, int dayInWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, weekInMonth);
        calendar.set(Calendar.DAY_OF_WEEK, dayInWeek + 1);
        return calendar.getTime();
	}
	
	/**
	 * 获取之后的日期
	 * @param startDate
	 * @param num
	 * @param type	类型
	 * @return
	 */
	public static Date getAfterDate(Date startDate, int num, int type) {
		 Calendar calendar = Calendar.getInstance();
		 calendar.setTime(startDate);
		 calendar.add(type, num);
		 return calendar.getTime();
	}
	
	/**
     * 返回几小时后的某个时间
     * @param d
     * @param minutes
     * @return
     */
    public static String getDateAfterHour(Date d, int hour){
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.HOUR, now.get(Calendar.HOUR) + hour);
        return getDate(now.getTime());
    }
    
    public static boolean compareDate(String date1, String date2) throws ParseException{
    	Date d1 = DateUtils.getDateByString(date1, DateUtils.dateFormat);
    	Date d2 = DateUtils.getDateByString(date2, DateUtils.dateFormat);
    	return DateUtils.compareDate(d1, d2);
    }
    
    /**
     * 2个日期比较大小
     * @param date1
     * @param date2
     * @return
     */
    public static boolean compareDate(Date date1, Date date2) {
    	return date1.getTime() >= date2.getTime();
    }
    
    /**
     * 获取日期是星期几
     * @param date
     * @return
     */
    public static int getWeekOfDate(Date date) {      
        int[] weekOfDays = {7, 1, 2, 3, 4, 5, 6};        
        Calendar calendar = Calendar.getInstance();      
        if(date != null){        
             calendar.setTime(date);      
        }        
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;      
        if (w < 0){        
            w = 0;      
        }      
        return weekOfDays[w];    
    }
    
    /**
     * 判断两个时间是否是同一天
     * @param dateA
     * @param dateB
     * @return
     */
    public static boolean areSameDay(Date dateA,Date dateB) {
        Calendar calDateA = Calendar.getInstance();
        calDateA.setTime(dateA);

        Calendar calDateB = Calendar.getInstance();
        calDateB.setTime(dateB);

        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                &&  calDateA.get(Calendar.DAY_OF_MONTH) == calDateB.get(Calendar.DAY_OF_MONTH);
    }
    
    /**
     * 获取当前时间前几个月的日期
     * @param num
     * @return
     */
    public static Date getBeforDateByMonth(int num){
    	Date dNow = new Date();
    	Date dBefore = new Date();
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(dNow);
    	calendar.add(Calendar.MONTH, num); 
    	dBefore = calendar.getTime();
    	return dBefore;
    }
    
}
