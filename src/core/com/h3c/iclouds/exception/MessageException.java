package com.h3c.iclouds.exception;

import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.utils.StrUtils;

public class MessageException extends RuntimeException {
	
    private static final long serialVersionUID = 1L;

    /**
     * 异常代码
     */
    private ResultType resultCode = null;

    public MessageException() {
    	
    }

    public static final MessageException create(ResultType resultCode) {
        return create(null, resultCode);
    }

    public static final MessageException create(String message) {
        return create(message, null);
    }

    public static final MessageException create(String message, ResultType resultCode) {
        MessageException me = new MessageException(message);
        me.setResultCode(resultCode);
        return me;
    }
    
    public MessageException(ResultType resultCode) {
    	super(resultCode.toString());
    	this.resultCode = resultCode;
    }

    public MessageException(String message) {
        super(message);
    }

    public MessageException(String message, ResultType resultCode) {
        super(message);
        this.resultCode = resultCode;
    }

    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 获取主动异常信息
     * @return
     */
	public ResultType getResultCode() {
		return resultCode;
	}

	public void setResultCode(ResultType resultCode) {
		this.resultCode = resultCode;
	}

    public Object getException(){
	    if (StrUtils.checkParam(resultCode)){
	        return resultCode;
        } else {
	        return this.getMessage();
        }
    }
}