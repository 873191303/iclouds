package com.h3c.iclouds.po.bean.cloudos;

import java.io.Serializable;

public class Quota implements Serializable{

	
	private static final long serialVersionUID = 945263636702134642L;

	private Storage block_storage;
	
	private Compute compute;

	public Storage getBlock_storage() {
		return block_storage;
	}

	public void setBlock_storage(Storage block_storage) {
		this.block_storage = block_storage;
	}

	public Compute getCompute() {
		return compute;
	}

	public void setCompute(Compute compute) {
		this.compute = compute;
	}
}
