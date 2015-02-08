package com.huawei.agilete.northinterface.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.OpsRestClient;
import org.apache.wink.client.Resource;

import com.huawei.agilete.northinterface.util.XPathToBean;
import com.huawei.networkos.ops.response.RetRpc;

public class GetTimingData {

	public String[] getTimeData(String url,String xPath){
		String[] returnData = new String[4];
		RetRpc ret = new RetRpc();
		ret = getDataInUrl(url,xPath);
		if(ret.getStatusCode()!=200){
			returnData[0] = "0";
			returnData[1] = "0";
			returnData[2] = ((Long)System.nanoTime()).toString();
			returnData[3] = "my";
			return returnData;
		}
		if(null==ret.getContent()||ret.getContent().trim().equals("")){
			ret.setContent("0");
		}
//		XPathToBean xpathToBean = new XPathToBean();
//		DataModel dataModel = xpathToBean.getModelList(ret.getContent(),xPath);
		returnData[0] = ret.getContent();
		returnData[1] = ret.getContent();
		returnData[2] = ((Long)System.nanoTime()).toString();
		returnData[3] = "my";
		return returnData;
		
	}
	
	/**注销未使用
	 * @param url
	 * @return
	 */
	public RetRpc getDataInUrl_(String url){
		RetRpc ret = new RetRpc();
		String address = url;
 
		URI uri = URI.create(address);
		
		// 返回新的address地址
		// create the rest client instance
		OpsRestClient client = new OpsRestClient();
		// create the resource instance to interact with
		Resource resource = null;
		 
		resource = client.resource(uri);
		 
		// issue the request
		ClientResponse response = null;

		try {
			response = resource.accept("text/plain").get();
		} catch (Exception ex) {
			
			//ex.printStackTrace();
			ret.setStatusCode(500);
			ret.setMsg(ex.getMessage());
			ret.setStatus("connected failed .");
//			System.out.println(ret.getStatus());
			
			return ret;
		}  
		
		int code = response.getStatusCode();
		String msg = response.getMessage();
		ret.setStatusCode(code);
		ret.setStatus(response.getStatusType().getReasonPhrase());
		ret.setMsg(msg);

		// deserialize response
		ret.setContent(response.getEntity(String.class));

		return ret;
	}
	
	
	public RetRpc getData(String url1,String url2,String url3,String body,String xpath){
		RetRpc ret = new RetRpc();
		pingDataInUrl(url1, body);
		try {
			Thread.sleep(3500);
		} catch (InterruptedException e) {
			//e.printStackTrace();
		}
		ret = getDataInUrl(url2,xpath);
		delDataInUrl(url3);
		
		
		return ret;
	}
	
	/**
	 * 流量
	 * @param urlStr
	 * @return
	 */
	public RetRpc getDataInUrl(String urlStr,String xpath){
		RetRpc ret = new RetRpc();
		URL url = null;  /** 网络的url地址 */        
        BufferedReader in = null; /**//** 输入流 */ 
        StringBuffer sb = new StringBuffer(); 
        long start= System.currentTimeMillis();
        try{
            url = new URL(urlStr);   
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();;  /** http连接 */     
        	httpConn.setDoOutput(true);
        	httpConn.setRequestMethod("GET");
//        	OutputStream os = httpConn.getOutputStream();
//        	os.close();
//            in = new BufferedReader( new InputStreamReader(url.openStream(),"UTF-8") ); 
        	in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
        	httpConn.connect();
        	
            String str = null; 
            while((str = in.readLine()) != null) {  
                sb.append(str).append("\n");
                
            }
            int code = httpConn.getResponseCode();
    		String msg = httpConn.getResponseMessage();
    		ret.setStatusCode(code);
//    		ret.setStatus(httpConn.getr);
    		ret.setMsg(msg);
    		XPathToBean xp = new XPathToBean();
    		ret.setContent(xp.getModelList(sb.toString(),xpath).getData());
//            httpConn.get
        } catch (Exception ex) { 
            ex.printStackTrace();
            ret.setStatusCode(500);
			ret.setMsg(ex.getMessage());
			ret.setStatus("connected failed .");
//			System.out.println(ret.getStatus());
			return ret;
        } finally{  
                 try{           
                     if(in!=null) {
                         in.close(); 
                     }   
                 }catch(IOException ex) {    
                     ex.printStackTrace();
                 }   
        }
//        httpConn
//        System.out.println(result);   
        return ret;  
	}
	
	/**
	 * 流量
	 * @param urlStr
	 * @return
	 */
	public RetRpc pingDataInUrl(String urlStr,String tunnel){
		String body = "<testName>ADMIN</testName><tunnelName>"+tunnel+"</tunnelName>";
		sendPost(urlStr, "", body);
		return null;
	}
	
	public RetRpc delDataInUrl(String urlStr){
		RetRpc ret = new RetRpc();
		sendPost(urlStr, "", ""); 
        return ret;  
	}
	
	public Boolean sendPost(String url, String param, String postData) {
//		postData = "<testName>ADMIN</testName><tunnelName>"+postData+"</tunnelName>";
		PrintWriter out = null;
		BufferedReader in = null;
		Boolean result = true;
		try {
			String urlNameString = url;
			URL realUrl = new URL(urlNameString);
			URLConnection conn = realUrl.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			OutputStream os=conn.getOutputStream();
			DataOutputStream dos=new DataOutputStream(os);
			dos.write(postData.getBytes());
			dos.flush();
			dos.close();
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			//            String line;
			//            while ((line = in.readLine()) != null) {
				//                result += line;
			//            }
		} catch (Exception e) {
			result = false;
			// e.printStackTrace();
		}finally{
			try{
				if(out!=null){
					out.close();
				}
				if(in!=null){
					in.close();
				}
			}
			catch(IOException ex){
//				ex.printStackTrace();
			}
		}
		return result;
	}    
	
	public static void main(String[] args){
		GetTimingData get = new GetTimingData();
//		RetRpc ret = get.getDataInUrl("http://10.111.69.127:8080/devices/4/mpls/mplsTe/rsvpTeTunnels/rsvpTeTunnel?tunnelName,mplsTunnelIngressLSRId,mplsTunnelEgressLSRId,mplsTunnelIndex,adminStatus,tunnelState,hotStandbyWtr,workingLspType&tunnelPaths/tunnelPath?pathType,explicitPathName,lspState");
//		RetRpc ret = get.getDataInUrl("http://10.111.69.127:8080/devices/4/ifm/interfaces/interface?ifName,ifDynamicInfo&ifStatistics?receiveByte,sendByte");
//		System.out.println(ret);
//		get.getTimeData("http://10.111.69.127:8080/devices/4/mpls/mplsTe/rsvpTeTunnels/rsvpTeTunnel?tunnelName,mplsTunnelIngressLSRId,mplsTunnelEgressLSRId,mplsTunnelIndex,adminStatus,tunnelState,hotStandbyWtr,workingLspType&tunnelPaths/tunnelPath?pathType,explicitPathName,lspState","");
		RetRpc aaa = get.getData("http://10.111.69.127:8080/devices/1/_action/dgntl/lsp/startLspPing", "http://10.111.69.127:8080/devices/1/dgntl/lsp/lspPingResults/lspPingResult?testName=ADMIN", "http://10.111.69.127:8080/devices/1/_action/dgntl/lsp/deleteLspPing?testName=ADMIN", "Tunnel0","xpath");
		System.out.println(aaa);
	}
}
