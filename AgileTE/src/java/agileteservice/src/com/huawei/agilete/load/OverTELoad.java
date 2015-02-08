package com.huawei.agilete.load;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServlet;

import com.huawei.agilete.base.action.DBAction;
import com.huawei.agilete.base.common.StringEncrypt;
import com.huawei.agilete.northinterface.action.Activator;
import com.huawei.agilete.northinterface.dao.OTRestPingDAO;
 
/**
 * 程序初始化
 * @author lWX200287
 *
 */
@SuppressWarnings("serial")
public class OverTELoad  extends HttpServlet{
    private DBAction db = new DBAction();
    public void init(){
    	
        // 对符合的地址进行过滤，如果地址不符合，就不进行认证
        javax.net.ssl.HttpsURLConnection
                .setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {

                    public boolean verify(String hostname,
                            javax.net.ssl.SSLSession sslSession) {

                        // 这里可以放类似白名单的认证，如果没有在白名单中的，就不继续
                        if (hostname.equalsIgnoreCase(sslSession.getPeerHost())) {
                            return true;
                        }

                        return true;
                    }

                });

        
        //数据初始化
        //MyData mydata = new MyData();
//        MyData.TaskMainControl = new BuildController();
//        MyData.TaskMainControl.resume();
//        
//        MyData.TaskChildControl = new BuildController();
//        MyData.TaskChildControl.resume();
        
        //ping devices
        OTRestPingDAO.getInstance().pingDevicesTest();
        
        //启动tunnelTree定时器
        Activator.getInstance().contextInitialized(30, 600);
        addAdmin("admin","admin");
        initUser("admin","admin");
    }
    
    //添加admin用户
    public void addAdmin(String userName, String passWd) {
        String key = db.get("user", "user", "admin");
        if (null== key||"".equals(key)) {
            String content = "<user><userId>admin</userId><userName>"+userName+"</userName><userPasswd>"+StringEncrypt.encrypt(passWd)+"</userPasswd><registTime>"+new SimpleDateFormat("yyyy-MM-dd dd-hh-mm").format(new Date()).toString()+"</registTime><desc>管理员</desc></user>";
            db.insert("user", "user","admin",content);
        } 
    }
    
    public void initUser(String userName, String passWd) {
        String key = db.get("inituser", "user", "admin");
        if (null== key||"".equals(key)) {
            String content = "<user><userId>admin</userId><userName>"+userName+"</userName><userPasswd>"+StringEncrypt.encrypt(passWd)+"</userPasswd><registTime>"+new SimpleDateFormat("yyyy-MM-dd dd-hh-mm").format(new Date()).toString()+"</registTime><desc>管理员</desc></user>";
            db.insert("inituser", "user","admin",content);
        } 
    }
    
    
    public static void main(String[] args) {
        OverTELoad a = new OverTELoad();
        a.addAdmin("admin","admin");
        
    }
    
}
