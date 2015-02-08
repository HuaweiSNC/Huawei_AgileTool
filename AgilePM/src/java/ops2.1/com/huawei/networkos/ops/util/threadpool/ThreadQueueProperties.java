/*********************************************************************
Copyright, 2008-2010, Huawei Tech. Co., Ltd.
All Rights Reserved
----------------------------------------------------------------------
Project Code  :  
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


/** A wrapper for ThreadPool to the config file.
 * Makes the properties in the server properties
 * file available to the entire server.
 *
 * NOTE: This class has been gutted and turned into a thin
 * wrapper around ServerXMLHelper.java, which accesses the
 * CruiseControl XML configuration files.
 *
 * @author Jared Richardson
 */

public final class ThreadQueueProperties {
	
    private static int maxThreadCount = 20;
    
    private ThreadQueueProperties() {
    }

    public static int getMaxThreadCount() {
        return maxThreadCount;
    }

    public static void setMaxThreadCount(int threadCount) {
        if (threadCount < 1) {
            throw new IllegalArgumentException("max thread count must be >= 1");
        }
        maxThreadCount = threadCount;
    }


}
