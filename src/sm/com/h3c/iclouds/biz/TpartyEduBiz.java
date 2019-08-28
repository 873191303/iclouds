package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.TpartyEdu;

import java.util.List;

/**
 * Created by yKF7317 on 2016/11/19.
 */
public interface TpartyEduBiz extends BaseBiz<TpartyEdu> {

	List<String> getProjectIds();
}
