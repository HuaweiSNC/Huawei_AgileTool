package com.huawei.agilete.base.common;

import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.spi.LoggingEvent;

public class LogManager extends RollingFileAppender  {
    
    private String logIP;
    private String logbody;
    private String logurl;
    private String logmethod;
    private String logtime;
    private String logcode;
    private String logStatus;
    private String loginputbody;
    private String logoutputbody;
    @Override
    public void close() {
        super.close();
    }

    @Override
    public boolean requiresLayout() {
        return super.requiresLayout();
    }

    @Override
    public void append(LoggingEvent arg0) {
        StringBuffer message = getMessageForCondition(arg0.getMessage().toString());
        LoggingEvent arg1 = new LoggingEvent(arg0.getFQNOfLoggerClass(), arg0.getLogger(), arg0.getTimeStamp(), arg0.getLevel(), message, arg0.getThreadName(), arg0.getThrowableInformation(), arg0.getNDC(), arg0.getLocationInformation(), arg0.getProperties());
        super.append(arg1);
    }
    
    private StringBuffer getMessageForCondition(String log){
        StringBuffer message = new StringBuffer(log);
        message.append("{ ");
        if(null==getLogStatus()||"".equals(getLogStatus())){
            return message;
        }
        
        if(getLogStatus().contains("method")){
            message.append("method=[")
                    .append(getLogmethod())
                    .append("] ");
        }
        if(getLogStatus().contains("code")){
            message.append("code=[")
            .append(getLogcode())
            .append("] ");
        }
        if(getLogStatus().contains("IP")){
            message.append("IP=[")
            .append(getLogIP())
            .append("] ");
        }
        if(getLogStatus().contains("time")){
            message.append("time=[")
            .append(getLogtime())
            .append("ms] ");
        }
        if(getLogStatus().contains("url")){
            message.append("url=[")
            .append(getLogurl())
            .append("] ");
        }
        if(getLogStatus().contains("mbody")){
            message.append("inputbody=[")
            .append(getLogstartbody())
            .append("] ")
            .append("outputbody=[")
            .append(getLogendbody())
            .append("] ");
        }else if(getLogStatus().contains("body")){
            message.append("body=[")
            .append(getLogbody())
            .append("] ");
        }else if(getLogStatus().contains("error")&&(null!=getLogcode())&&(!getLogcode().equals("200"))){
            message.append("error=[")
            .append(getLogendbody())
            .append("] ");
        }
        message.append("}");
        return message;
    }
    
    

    public String getLogIP() {
        return logIP;
    }

    public void setLogIP(String logIP) {
        this.logIP = logIP;
    }

    public String getLogbody() {
        return logbody;
    }

    public void setLogbody(String logbody) {
        this.logbody = logbody;
    }

    public String getLogurl() {
        return logurl;
    }

    public void setLogurl(String logurl) {
        this.logurl = logurl;
    }

    public String getLogmethod() {
        return logmethod;
    }

    public void setLogmethod(String logmethod) {
        this.logmethod = logmethod;
    }

    public String getLogtime() {
        return logtime;
    }

    public void setLogtime(String logtime) {
        this.logtime = logtime;
    }

    public String getLogcode() {
        return logcode;
    }

    public void setLogcode(String logcode) {
        this.logcode = logcode;
    }

    public String getLogStatus() {
        return logStatus;
    }

    public void setLogStatus(String logStatus) {
        this.logStatus = logStatus;
    }

    public String getLogstartbody() {
        return loginputbody;
    }

    public void setLogstartbody(String logstartbody) {
        this.loginputbody = logstartbody;
    }

    public String getLogendbody() {
        return logoutputbody;
    }

    public void setLogendbody(String logendbody) {
        this.logoutputbody = logendbody;
    }

    public static void main(String[] args) {  
        Logger log = Logger.getLogger("logthis");
        LogManager logManager = (LogManager) log.getAppender("logthis");
//        LogManager logManager = (LogManager) log.getRootLogger().getAppender("com.huawei.agilete.base.common.LogManager");
//        log.getAppender(name)
//        LogManager.
//        log.getName();
//        Priority priorty = new Priority(0);
//        log.setAdditivity(additive)
//        log.setPriority(priority);
//        log.setPriority("");
        log.info("I am ready.") ;  
    }  
    
}
