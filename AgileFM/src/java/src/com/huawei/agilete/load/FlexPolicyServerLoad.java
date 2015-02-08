package com.huawei.agilete.load;

import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.huawei.agilete.northinterface.web.SecurityXMLServer;
 
/**
 * 程序初始化
 * @author lWX200287
 *
 */
@SuppressWarnings("serial")
public class FlexPolicyServerLoad  extends HttpServlet{

	private SecurityXMLServer security = null;
	private static final Log log = LogFactory.getLog(FlexPolicyServerLoad.class);
	
	public void init(){
 
		security = new SecurityXMLServer();
		Boolean initSocket = security.createServerSocket();
		if (initSocket) {
			log.info("Success to loaded Flex Security Server on port 843 . ");
			new Thread(security).start();
			
		} else {
			
			log.error("Failed to load Flex Security Server on port 843 . ");
		}
		
 
	}
 
	
}
