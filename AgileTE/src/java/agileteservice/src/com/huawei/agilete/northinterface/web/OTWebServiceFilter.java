package com.huawei.agilete.northinterface.web;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.huawei.agilete.base.common.ServiceLog;

public class OTWebServiceFilter implements Filter {
    
    
    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1,
            FilterChain arg2) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)arg0;
//        boolean flag = false;
//        flag = WhiteList.isInWhiteList(request);
//        WhiteListLog.createLog(request, flag);
//        HttpServletResponse response = (HttpServletResponse)arg1;
        long time1 = System.currentTimeMillis();
        ServiceLog serviceLog = new ServiceLog();
        serviceLog.outUIStartLog(request);
        
//        if(flag==false){
//            response.setStatus(403);
//            response.setCharacterEncoding("UTF-8");
//            PrintWriter printWriter = response.getWriter();
//            printWriter.print("NO POWER!");
//        }else{
            arg2.doFilter(arg0, arg1);
//        }
        long time = System.currentTimeMillis()-time1;
        serviceLog.outUIEndLog(request,time);
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }


    

    
    
    
}
