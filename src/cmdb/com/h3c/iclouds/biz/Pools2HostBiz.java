package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.Pools2Host;

/**
 * Created by yKF7317 on 2016/11/9.
 */
public interface Pools2HostBiz extends BaseBiz<Pools2Host> {

    Object save(Pools2Host entity);

    Object update(String id, Pools2Host entity);

    Object delete(String id);

    void synAdd(Pools2Host entity);

    void synDelete(Pools2Host entity);
    
    void addParam(Pools2Host pools2Host);
}
