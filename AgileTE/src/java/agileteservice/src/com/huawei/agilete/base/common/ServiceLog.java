package com.huawei.agilete.base.common;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.huawei.networkos.ops.response.RetRpc;

/**
 * 日志类
 * @author xWX202247
 *
 */
public class ServiceLog {

    /**
     * webservice结束时的log
     * @param request
     */
    public void outUIEndLog(HttpServletRequest request,long time){
        
        //获取ip
        String ip = getRequestToIP(request);

        StringBuffer logs = new StringBuffer();
        Logger logger =Logger.getLogger("UI");
        LogManager logManager = (LogManager) logger.getAppender("UI");
        logManager.setLogbody(MyIO.characterFormat(""));
        logManager.setLogmethod(request.getMethod());
        logManager.setLogIP(ip);
        logManager.setLogurl(request.getRequestURI());
        logManager.setLogtime(String.valueOf(time));
//        logManager.setLogcode(request.get);

        
//        
//        logs.append(" [").append(ip).append("] End ").append("{expend time=").append(time).append("ms} ")
//        .append(request.getMethod()).append(" ").append("url:").append(request.getRequestURI()).append(" Body={").append(MyIO.characterFormat("")).append("}");
        logger.info("UI_End");
//        //System.out.println("******结束*****");    
//        //System.out.println(logs);
    }
    
    /**
     * webservice开始时的log
     * @param request
     */
    public void outUIStartLog(HttpServletRequest request){
        //获取ip
        String ip = getRequestToIP(request);
        /*
         * 输入的body内容 start
         */
//        String body = null;
//        StringBuilder stringBuilder = new StringBuilder();
//        BufferedReader bufferedReader = null;
//        try {
//            InputStream inputStream = request.getInputStream();//获取
//            if (inputStream != null) {
//                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                char[] charBuffer = new char[128];
//                int bytesRead = -1;
//                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
//                    stringBuilder.append(charBuffer, 0, bytesRead);
//                }
//            } else {
//                stringBuilder.append("");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        body = stringBuilder.toString();
        /*
         * 输入的body内容 end
         */
        
        /*
         * 组装LOG
         */
//        StringBuffer logs = new StringBuffer();
        Logger logger =Logger.getLogger("UI");
        LogManager logManager = (LogManager) logger.getAppender("UI");
        logManager.setLogbody(MyIO.characterFormat(""));
        logManager.setLogmethod(request.getMethod());
        logManager.setLogIP(ip);
        logManager.setLogurl(request.getRequestURI());
        logManager.setLogtime("");
        
//        logs.append(" [").append(ip).append("] Start ").append(request.getMethod()).append(" ").append("url:")
//        .append(request.getRequestURI()).append(" Body={").append(MyIO.characterFormat("")).append("}");
        logger.info("UI_Start");
//        //System.out.println(logs);
    }
    
    /**
     * ops日志
     * @param url
     * @param retrpc
     * @param time
     * @param action
     * @param data
     */
    public void outOPSLog(String url,RetRpc retrpc,long time,String action,String data){
        Logger logger =Logger.getLogger("OPS");
        LogManager logManager = (LogManager) logger.getAppender("OPS");
        logManager.setLogstartbody(MyIO.characterFormat(data));
        logManager.setLogendbody(MyIO.characterFormat(retrpc.getContent()));
        logManager.setLogcode(String.valueOf(retrpc.getStatusCode()));
        logManager.setLogmethod(action);
        logManager.setLogtime(String.valueOf(time));
        logManager.setLogurl(url);
        logger.info("OPS_");
    }
    
    
    /**
     * 对代理IP等特殊IP可以起到作用    获取IP
     * @param request
     * @return
     */
    private String getRequestToIP(HttpServletRequest request){
        
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
