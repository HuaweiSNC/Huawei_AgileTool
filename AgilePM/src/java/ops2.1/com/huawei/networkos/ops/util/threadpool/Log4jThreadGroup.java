/*********************************************************************
Copyright, 2008-2010, Huawei Tech. Co., Ltd.
All Rights Reserved
----------------------------------------------------------------------
Project Code  : VIBE V3.0
*********************************************************************/
/**
* @file  
* @author xilequan 00106601
* @brief
* @detail
* @date 2009-01-08
* @version 1.0
* @see 
* @note
*/

package com.huawei.networkos.ops.util.threadpool;

import org.apache.log4j.Logger;

/**
 * @version $Id: ThreadQueue.java 2577 2006-07-07 21:41:33Z jfredrick $
 */

public class Log4jThreadGroup extends ThreadGroup {
    private Logger logger;

    public Log4jThreadGroup(String name, Logger logger) {
        super(name);
        this.logger = logger;
    }

    public void uncaughtException(Thread t, Throwable e) {
         logger.error("uncaught exception in " + t.getName(), e);
    }
}
