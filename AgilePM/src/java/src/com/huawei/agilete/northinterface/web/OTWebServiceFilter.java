package com.huawei.agilete.northinterface.web;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.huawei.agilete.northinterface.service.ListenerManager;
import com.huawei.agilete.northinterface.service.RrdtoolManager;
import com.huawei.agilete.northinterface.util.ProcessManager;

public class OTWebServiceFilter implements Filter {
	@Override
	public void destroy() {
		ProcessManager.killProcess("mrtg");
		System.out.println("sfdsdf");
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,FilterChain arg2) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)arg0;
//		arg2.doFilter(arg0, arg1);
		String path = request.getRequestURI().toString();
		String[] pathList = path.split("/");
		if(null!=pathList&&pathList.length==5&&pathList[2].equals("statisticsPage")&&pathList[4].contains(".html")){
			String id = pathList[4].replace(".html", "");
//			html.
			RrdtoolManager rrdtoolManager = new RrdtoolManager();
			rrdtoolManager.refurbishData(id);
		}
		System.out.println(request.getPathInfo());
//		request.getRequestDispatcher("").forward(arg0, arg1);
		arg2.doFilter(arg0, arg1);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		ListenerManager listenerManager = new ListenerManager();
		listenerManager.runMrtg("mrtg");
	}


	

	
	
	
}
