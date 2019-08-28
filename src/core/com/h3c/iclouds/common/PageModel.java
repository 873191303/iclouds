package com.h3c.iclouds.common;

import java.util.List;

/**
 * 分頁模型
 * 
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
public class PageModel<T> implements java.io.Serializable {

	/**
	 * RestFul框架返回数据模型，包含分页
	 * 
	 * @param pageNo
	 *            当前页码
	 * @param pageSize
	 *            页面大小
	 * @param totalRecords
	 *            总记录数
	 * @param datas
	 *            返回的数据集合
	 * @param data
	 *            返回的数据
	 * @param resopnseStatus
	 *            状态 Success 、Failure
	 * @param resopnseMsg
	 *            返回消息
	 * @param resopnseCode
	 *            返回状态码
	 */
	public PageModel(int pageNo, int pageSize, long totalRecords, List<T> datas, T data, String resopnseMsg,
			String resopnseCode) {
		super();
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.totalRecords = totalRecords;
		this.datas = datas;
		this.data = data;
		this.resopnseMsg = resopnseMsg;
		this.resopnseCode = resopnseCode;
	}

	public PageModel() {
		super();
	}

	/**
	 * 總記錄數
	 */
	private long totalRecords = 0;

	/**
	 * 每頁大小
	 */
	private int pageSize = 20;

	/**
	 * 頁碼
	 */
	private int pageNo = 1;

	/**
	 * 返回數據
	 */
	private List<T> datas;

	private T data;

	private String resopnseMsg;

	private String resopnseCode;

	public long getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public List<T> getDatas() {
		return datas;
	}

	public void setDatas(List<T> datas) {
		this.datas = datas;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getResopnseMsg() {
		return resopnseMsg;
	}

	public void setResopnseMsg(String resopnseMsg) {
		this.resopnseMsg = resopnseMsg;
	}

	public String getResopnseCode() {
		return resopnseCode;
	}

	public void setResopnseCode(String resopnseCode) {
		this.resopnseCode = resopnseCode;
	}
}
