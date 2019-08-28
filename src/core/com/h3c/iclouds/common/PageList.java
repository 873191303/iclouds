package com.h3c.iclouds.common;

import java.util.List;

import com.h3c.iclouds.utils.StrUtils;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 用于后台分页
 * 定义格式参照JQuery.DataTable的json解析格式
 * @author zkf5485
 *
 * @param <T>
 */
public class PageList<T> {
	
	@SuppressWarnings("unchecked")
	public PageList (PageModel<T> pageModel, String sEcho) {
		int secho = StrUtils.tranInteger(sEcho);
		this.sEcho = secho++;
		this.iPageSize = pageModel.getPageSize();
		this.aaData = pageModel.getDatas();
		this.iPageNo = pageModel.getPageNo();
		if(iPageSize != -1) {
			this.iTotalDisplayRecords = (int)pageModel.getTotalRecords();
		} else {
			if(this.aaData == null) {	// 列表数据为空
				this.iTotalDisplayRecords = 0;
				this.aaData = Collections.emptyList();
			} else {
				this.iTotalDisplayRecords = this.aaData.size();
			}
		}
		this.iTotalRecords = iTotalDisplayRecords;
	}
	
	public PageList() {
		
	}
	
	// 前台版本码
	private int sEcho;
	
	// 送数据条
	private int iTotalRecords;
	
	// 查询总数据数
	private int iTotalDisplayRecords;
	
	private int iPageNo;
	
	private int iPageSize;
	
	// 数据列表
	private List<T> aaData;
	
	public int getsEcho() {
		return sEcho;
	}

	public void setsEcho(int sEcho) {
		this.sEcho = sEcho;
	}

	public int getiTotalRecords() {
		return iTotalRecords;
	}

	public void setiTotalRecords(int iTotalRecords) {
		this.iTotalRecords = iTotalRecords;
	}

	public int getiTotalDisplayRecords() {
		return iTotalDisplayRecords;
	}

	public void setiTotalDisplayRecords(int iTotalDisplayRecords) {
		this.iTotalDisplayRecords = iTotalDisplayRecords;
	}

	public List<T> getAaData() {
		return aaData;
	}

	public void setAaData(List<T> aaData) {
		this.aaData = aaData;
	}

	public int getiPageNo() {
		return iPageNo;
	}

	public void setiPageNo(int iPageNo) {
		this.iPageNo = iPageNo;
	}

	public int getiPageSize() {
		return iPageSize;
	}

	public void setiPageSize(int iPageSize) {
		this.iPageSize = iPageSize;
	}

}
