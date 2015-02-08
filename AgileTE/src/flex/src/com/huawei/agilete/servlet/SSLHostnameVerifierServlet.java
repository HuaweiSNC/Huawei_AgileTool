package com.huawei.agilete.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class SSLHostnameVerifierServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9150437891380102789L;

	@Override
	public void init() throws ServletException {
		
		// 对符合的地址进行过滤，如果地址不符合，就不进行认证
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier(){
			public boolean verify(String hostname,javax.net.ssl.SSLSession sslSession) {
				// 这里可以放类似白名单的认证，如果没有在白名单中的，就不继续
				if (hostname.equalsIgnoreCase(sslSession.getPeerHost())) {
					return true;
				}
				return true;
			}
		});
		
	}
}
