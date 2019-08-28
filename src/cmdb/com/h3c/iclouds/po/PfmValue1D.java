package com.h3c.iclouds.po;

import java.io.Serializable;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by yKF7317 on 2017/3/1.
 */
@ApiModel(value = "云资源性能历史数据(1天压缩数据)")
public class PfmValue1D extends BasePfmValue2History implements Serializable {

	private static final long serialVersionUID = 1L;

	public PfmValue1D() {

	}

	public PfmValue1D(Float maxValue, Float minValue) {
		this.maxValue = maxValue;
		this.minValue = minValue;
	}

	@ApiModelProperty(value = "最大值指标值")
	private Float maxValue;

	@ApiModelProperty(value = "最大值指标值")
	private Float minValue;

	public Float getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Float maxValue) {
		this.maxValue = maxValue;
	}

	public Float getMinValue() {
		return minValue;
	}

	public void setMinValue(Float minValue) {
		this.minValue = minValue;
	}
}
