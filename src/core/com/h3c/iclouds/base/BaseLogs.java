package com.h3c.iclouds.base;

import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zkf5485 on 2017/9/5.
 */
public abstract class BaseLogs {

    public Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 需要记录的信息日志
     * @param obj
     */
    public void info(Object obj) {
        log.info(LogUtils.print(obj));
    }

    /**
     * 异常内容日志打印
     * @param e
     */
    public void exception(Exception e, Object...objects) {
        if(!(e instanceof MessageException)) {	// message异常不做打印
            e.printStackTrace();
        }
        log.error(LogUtils.print(e.getMessage()));
        if(objects != null && objects.length > 0) {
            for (Object obj : objects) {
                log.error(LogUtils.print(obj));
            }
        }
    }

    /**
     * 重要内容日志打印
     * @param obj
     */
    public void warn(Object obj) {
        log.warn(LogUtils.print(obj));
    }

}
