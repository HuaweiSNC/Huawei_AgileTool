package com.huawei.agilete.base.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

import org.apache.cassandra.thrift.Cassandra.AsyncProcessor.system_add_column_family;
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
		
		logs.append(" [").append(ip).append("] End ").append("{expend time=").append(time).append("ms} ")
		.append(request.getMethod()).append(" ").append("url:").append(request.getRequestURI()).append(" Body={").append(MyIO.characterFormat("")).append("}");
		logger.info(logs);
//		System.out.println("******结束*****");	
//		System.out.println(logs);
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
//		String body = null;
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
//        	e.printStackTrace();
//        }
//        body = stringBuilder.toString();
        /*
		 * 输入的body内容 end
		 */
        
        /*
         * 组装LOG
         */
        StringBuffer logs = new StringBuffer();
		Logger logger =Logger.getLogger("UI");
		logs.append(" [").append(ip).append("] Start ").append(request.getMethod()).append(" ").append("url:")
		.append(request.getRequestURI()).append(" Body={").append(MyIO.characterFormat("")).append("}");
		logger.info(logs);
//		System.out.println(logs);
	}
	
	/**
	 * ops日志
	 * @param url
	 * @param retrpc
	 * @param time
	 */
	public void outOPSLog(String url,RetRpc retrpc,long time,String action,String data){
		Logger logger =Logger.getLogger("OPS");
		StringBuffer logs = new StringBuffer();
		logs.append("OPS_").append(action);
		logs.append("{");
		logs.append("expend time=").append(time).append("ms ");
		logs.append("url:").append(url).append(" ");
		logs.append("message:").append(MyIO.characterFormat(data)).append(" ");
		logs.append("state:").append(retrpc.getStatusCode()).append(" ");
//		logs.append("Content:").append(MyIO.characterFormat(retrpc.getContent())).append(" ");
		if(200 != retrpc.getStatusCode()){
			logs.append("error:{").append(retrpc.getContent()).append("} ");
		}
		logs.append("}");
		logger.info(logs);
//		System.out.println(logs);
	}
	
	
	/**
	 * 对代理IP等特殊IP可以起到作用	获取IP
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
