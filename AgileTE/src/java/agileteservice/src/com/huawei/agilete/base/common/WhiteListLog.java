package com.huawei.agilete.base.common;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * 白名单
 *
 */
public class WhiteListLog {

    /**
     * 
     * @param request
     */
    public static void createLog(HttpServletRequest request,boolean flag){
        //获取ip
        String ip = getRequestToIP(request);
        StringBuffer logs = new StringBuffer();
        Logger logger =Logger.getLogger("WhiteList");
        String isInter = ""; 
        if(flag==false){
            isInter = "Intercept!";
        }else{
            isInter = "Pass!";
        }
        logs.append(" IP:").append(ip).append("------result:").append(isInter).append("\r\n");
        logger.info(logs);

    }
    

    
    
    /**
     * 对代理IP等特殊IP可以起到作用    获取IP
     * @param request
     * @return
     */
    private static String getRequestToIP(HttpServletRequest request){
        
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) 
        {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) 
        { 
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) 
        {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
