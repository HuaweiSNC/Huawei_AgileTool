package com.huawei.agilete.load;

import javax.servlet.http.HttpServlet;

import com.huawei.agilete.data.MyData;
import com.huawei.agilete.northinterface.action.Activator;
import com.huawei.agilete.northinterface.dao.OTRestPingDAO;
import com.huawei.networkos.ops.builders.BuildController;
 
/**
 * 程序初始化
 * @author lWX200287
 *
 */
@SuppressWarnings("serial")
public class OverTELoad  extends HttpServlet{

	public void init(){
		//数据初始化
		//MyData mydata = new MyData();
		MyData.TaskMainControl = new BuildController();
		MyData.TaskMainControl.resume();
		
		MyData.TaskChildControl = new BuildController();
		MyData.TaskChildControl.resume();
		
		//ping devices
		OTRestPingDAO.getInstance().pingDevicesTest();
		
		//启动tunnelTree定时器
//		Activator.getInstance().contextInitialized(2, 360);
 
	}
 
	
}
