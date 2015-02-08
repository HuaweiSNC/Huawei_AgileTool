package com.huawei.agilete.base.common;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.huawei.agilete.data.MyData;


public class WhiteList {

    /**
     * 判断当前IP  是否属于白名单
     * @param request
     * @return
     */
    
    public static boolean isInWhiteList(HttpServletRequest request){
        boolean flag = false;
        //获取ip
        String[] whitelist = MyData.getIps();
        //读到的配置文件为空时（表明不存在），则返回true，所有人都放行
        if(null==whitelist){
            flag = true;
            return flag;
        }
        
        String ip = getRequestToIP(request);
        
        //本机放行
        
        List<String> localIps = null;
        try {
            localIps = getDefaultHostIp();
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(localIps!=null){
            for(String s:localIps){
                if(ip.equals(s)){
                    flag = true;
                    return flag;
                }
            }
        }
        
        //过滤，看访问的ip是否在白名单之内
        for(String s:whitelist){
            if(s.equalsIgnoreCase(ip)){
                flag = true;
                break;
            }
        }
        return flag;
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
    
    /**
     * 通过物理网卡获得本机的ip
     * 
     * */
    
     public static List<String> getDefaultHostIp() throws SocketException {
         List<String> ips = new ArrayList<String>();
         Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();
                Enumeration<InetAddress> addresses = nif.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    ips.add(address.getHostAddress());
                }
            }
            return ips;
        }
    
    
}
