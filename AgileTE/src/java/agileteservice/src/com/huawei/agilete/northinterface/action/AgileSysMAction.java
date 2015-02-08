package com.huawei.agilete.northinterface.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.huawei.agilete.base.common.ReadConfig;
import com.huawei.networkos.ops.response.RetRpc;

public class AgileSysMAction {

    private static final String ACTION_GET = "GET";
    private static final String ACTION_POST = "POST";
    private static final String ACTION_PUT = "PUT";
    private static final String ACTION_DELETE = "DELETE";
    
    
    public static String sysmIP = "";
    public static String sysmPort = "";
    
    
    
    
    /**
     * get,post,put,delete方法 返回url，data
     * 
     */
    public RetRpc get(String url) {
        return restcall(url, null, ACTION_GET);
    }

    public RetRpc post(String url, String data) {
        return restcall(url, data, ACTION_POST);
    }

    public RetRpc put(String url, String data) {
        return restcall(url, data, ACTION_PUT);
    }

    public RetRpc delete(String url) {
        return restcall(url, null, ACTION_DELETE);
    }
    
    
    private RetRpc restcall(String path, String data, String action) {
        long time1 = 0,time2 = 0;
        time1 = System.currentTimeMillis();

        StringBuilder urlb = new StringBuilder();
             
        buildServer(urlb);
        urlb.append(path);
        String url = urlb.toString();
            

        RetRpc r = connect(url, action, data);
//        time2 = System.currentTimeMillis() - time1;
        
//        ServiceLog serviceLog = new ServiceLog();
//        serviceLog.outOPSLog(url, r, time2, action, data);

        return r;
        
    }

    private void buildServer(StringBuilder urlb){
        if(null==sysmIP||sysmIP.equals("")){
            ReadConfig rc = new ReadConfig();
            sysmIP = rc.get().getProperty("AgileSysMIp");
            sysmPort = rc.get().getProperty("AgileSysMPort");
        }
        urlb.append(sysmIP).append(":").append(sysmPort);
    }
    
    
    /**
     * 请求
     * @param urlStr
     * @return
     */
    public RetRpc connect(String urlStr,String method,String data){
        RetRpc ret = new RetRpc();
        URL url = null;  /** 网络的url地址 */        
        BufferedReader in = null; /**//** 输入流 */ 
        OutputStream os=null;
        StringBuffer sb = new StringBuffer(); 
        try{
            url = new URL(urlStr);   
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();;  /** http连接 */  
            httpConn.setReadTimeout(300000);
            httpConn.setDoOutput(true);
            httpConn.setRequestMethod(method);
            if(null!=data){
                os = httpConn.getOutputStream();
                os.write(data.toString().getBytes("utf-8"));
                os.close();
            }
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            httpConn.connect();
            
            String str = null; 
            while((str = in.readLine()) != null) {  
                sb.append(str).append("\n");
            }
            int code = httpConn.getResponseCode();
            String msg = httpConn.getResponseMessage();
            ret.setStatusCode(code);
            ret.setMsg(msg);
            ret.setContent(sb.toString().trim());
        } catch (Exception ex) { 
            ex.printStackTrace();
            ret.setStatusCode(500);
            ret.setMsg(ex.getMessage());
            ret.setStatus("connected failed .");
            ret.setContent(ex.getStackTrace().toString());
            return ret;
        } finally{  
                 try{           
                     if(in!=null) {
                         in.close(); 
                     }   
                     if(os!=null) {
                    	 os.close(); 
                     }   
                 }catch(IOException ex) {    
                     ex.printStackTrace();
                 }   
        }
        return ret;  
    }

    
    
    
    /**
     * 连接http
     * 
     * @param url
     * @param method
     * @param data
     * @return
     */

//    private RetRpc _connect(String url, String method, String data) {
//
//        RetRpc ret = new RetRpc();
//        String address = url;
// 
//        URI uri = URI.create(address);
//        
//        // 返回新的address地址
//        // create the rest client instance
//        OpsRestClient client = new OpsRestClient();
//        // create the resource instance to interact with
//        Resource resource = null;
//         
//        resource = client.resource(uri);
//         
//        // issue the request
//        ClientResponse response = null;
//
//        try {
//            if (ACTION_POST.equals(method)) {
//                response = resource.accept("text/plain").post(data);
//                
//            } else if (ACTION_PUT.equals(method)) {
//                response = resource.accept("text/plain").put(data);
//
//            } else if (ACTION_DELETE.equals(method)) {
//                response = resource.delete();
//
//            } else {
//                response = resource.get();
//            }
//        } catch (Exception ex) {
//            
//            //ex.printStackTrace();
//            ret.setStatusCode(500);
//            ret.setMsg(ex.getMessage());
//            ret.setStatus("connected failed .");
//            //System.out.println(ret.getStatus());
//            
//            return ret;
//        }  
//        
//        int code = response.getStatusCode();
//        String msg = response.getMessage();
//        ret.setStatusCode(code);
//        ret.setStatus(response.getStatusType().getReasonPhrase());
//        ret.setMsg(msg);
//
//        // deserialize response
//        ret.setContent(response.getEntity(String.class));
//
//        return ret;
//    }

    
}
