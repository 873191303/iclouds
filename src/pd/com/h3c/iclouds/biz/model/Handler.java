package com.h3c.iclouds.biz.model;

public abstract class Handler {
	private Handler next;
	
	public final Response handleRequest(Request request) {
		Response response=null;
		if (getRecycleClass().above(request.getRecycleClass())) {
			response=response(request);
		}else {
			if (next!=null) {
				next.handleRequest(request);
			}else {
				//可处理默认向情况
			}
		}
		return response;
	}

	public Handler getNext() {
		return next;
	}

	public void setNext(Handler next) {
		this.next = next;
	}
	
	protected abstract RecycleClass getRecycleClass();
	
	public abstract Response  response(Request request);
	
}
