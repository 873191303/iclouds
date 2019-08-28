package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

public class MailQuenu extends BaseEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String mname;
	
	private String mfrom;
	
	private String mto;
	
	private String mcc;
	
	private String mbcc;
	
	private String mtopic;
	
	private String mcontent;
	
	private String attachments;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMname() {
		return mname;
	}

	public void setMname(String mname) {
		this.mname = mname;
	}

	public String getMfrom() {
		return mfrom;
	}

	public void setMfrom(String mfrom) {
		this.mfrom = mfrom;
	}

	public String getMto() {
		return mto;
	}

	public void setMto(String mto) {
		this.mto = mto;
	}

	public String getMcc() {
		return mcc;
	}

	public void setMcc(String mcc) {
		this.mcc = mcc;
	}

	public String getMbcc() {
		return mbcc;
	}

	public void setMbcc(String mbcc) {
		this.mbcc = mbcc;
	}

	public String getMtopic() {
		return mtopic;
	}

	public void setMtopic(String mtopic) {
		this.mtopic = mtopic;
	}

	public String getMcontent() {
		return mcontent;
	}

	public void setMcontent(String mcontent) {
		this.mcontent = mcontent;
	}

	public String getAttachments() {
		return attachments;
	}

	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}
	
}
