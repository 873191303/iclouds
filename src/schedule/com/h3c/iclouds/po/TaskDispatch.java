package com.h3c.iclouds.po;

import java.util.Date;

import com.h3c.iclouds.base.BaseEntity;

public class TaskDispatch extends BaseEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private String dbKey;
	private String syncType;
	private Date startTime;
	private Date startDate;
	private String endType;
	private Date endDate;
	private Integer endTimes;
	private Integer endOverTimes;
	private String safeType;
	private Integer safeTimes;
	private Integer periodType;
	private Integer dayInterval;
	private String weekInterval;
	private Integer monthInterval;
	private String monthType;
	private String monthDay;
	private Integer yearInterval;
	private String yearType;
	private String yearDay;
	private String status;
	private Date nextSyncTime;
	private String remark;
	private String mail;
	private Date lastSyncTime;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDbKey() {
		return dbKey;
	}

	public void setDbKey(String dbKey) {
		this.dbKey = dbKey;
	}

	public String getSyncType() {
		return syncType;
	}

	public void setSyncType(String syncType) {
		this.syncType = syncType;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getEndType() {
		return endType;
	}

	public void setEndType(String endType) {
		this.endType = endType;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getEndTimes() {
		return endTimes;
	}

	public void setEndTimes(Integer endTimes) {
		this.endTimes = endTimes;
	}

	public Integer getEndOverTimes() {
		return endOverTimes;
	}

	public void setEndOverTimes(Integer endOverTimes) {
		this.endOverTimes = endOverTimes;
	}

	public String getSafeType() {
		return safeType;
	}

	public void setSafeType(String safeType) {
		this.safeType = safeType;
	}

	public Integer getSafeTimes() {
		return safeTimes;
	}

	public void setSafeTimes(Integer safeTimes) {
		this.safeTimes = safeTimes;
	}

	public Integer getPeriodType() {
		return periodType;
	}

	public void setPeriodType(Integer periodType) {
		this.periodType = periodType;
	}

	public Integer getDayInterval() {
		return dayInterval;
	}

	public void setDayInterval(Integer dayInterval) {
		this.dayInterval = dayInterval;
	}

	public String getWeekInterval() {
		return weekInterval;
	}

	public void setWeekInterval(String weekInterval) {
		this.weekInterval = weekInterval;
	}

	public Integer getMonthInterval() {
		return monthInterval;
	}

	public void setMonthInterval(Integer monthInterval) {
		this.monthInterval = monthInterval;
	}

	public String getMonthType() {
		return monthType;
	}

	public void setMonthType(String monthType) {
		this.monthType = monthType;
	}

	public String getMonthDay() {
		return monthDay;
	}

	public void setMonthDay(String monthDay) {
		this.monthDay = monthDay;
	}

	public Integer getYearInterval() {
		return yearInterval;
	}

	public void setYearInterval(Integer yearInterval) {
		this.yearInterval = yearInterval;
	}

	public String getYearType() {
		return yearType;
	}

	public void setYearType(String yearType) {
		this.yearType = yearType;
	}

	public String getYearDay() {
		return yearDay;
	}

	public void setYearDay(String yearDay) {
		this.yearDay = yearDay;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getNextSyncTime() {
		return nextSyncTime;
	}

	public void setNextSyncTime(Date nextSyncTime) {
		this.nextSyncTime = nextSyncTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public Date getLastSyncTime() {
		return lastSyncTime;
	}

	public void setLastSyncTime(Date lastSyncTime) {
		this.lastSyncTime = lastSyncTime;
	}

}
