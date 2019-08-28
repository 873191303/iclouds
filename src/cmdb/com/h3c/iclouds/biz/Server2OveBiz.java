package com.h3c.iclouds.biz;

import java.util.List;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.Server2Ove;

/**
 * Created by yKF7317 on 2016/11/9.
 */
public interface Server2OveBiz extends BaseBiz<Server2Ove> {

    void synAdd(Server2Ove entity);

    void synDelete(Server2Ove entity);
    
    List<Server2Ove> findTop5(String previousId);
    
    List<Server2Ove> findTop5();
    void addParam(Server2Ove server2Ove);
    
    /**
     * 获取cvk的cpu超配值前五的数据
     * @return
     */
    List<Server2Ove> cpuTopList();
    
    /**
     * 获取cvk的内存超配值前五的数据
     * @return
     */
    List<Server2Ove> memoryTopList();
}
