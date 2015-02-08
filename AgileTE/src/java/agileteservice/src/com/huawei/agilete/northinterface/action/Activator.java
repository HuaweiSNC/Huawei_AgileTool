package com.huawei.agilete.northinterface.action;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.java_websocket.drafts.Draft_17;

import com.huawei.agilete.data.MyData;
import com.huawei.agilete.northinterface.dao.OTDomainDAO;
import com.huawei.agilete.northinterface.dao.OTTfDAO;
import com.huawei.networkos.ops.response.RetRpc;

public class Activator {

    private static Activator single = null;
    public  static AutobahnServer autobahnServer = null;
    public Timer timer;
    public Timer timer1;
    public Activator(){
    }
    public synchronized  static Activator getInstance() {
        if (single == null) {  
            single = new Activator();
        }  
        return single;
    }

    public void contextInitialized(long delay, long period)
    {
        timer = new Timer();
        timer.schedule(new TunnelTreeTask(), delay * 1000, period * 1000);
        
        //流量统计功能绑定端口功能
//        timer.schedule(new StatTask(), (delay+1) * 1000, 3 * 1000);
        int port = MyData.getServerSocketPort();

        autobahnServer = new AutobahnServer(port, new Draft_17());
        autobahnServer.start();

    }




    class TunnelTreeTask extends TimerTask
    {
        @Override
        public void run() {
        	/*
        	 * 用于备服务器静默
        	 */
        	if(getPrimaryBackupStatus()){
        		
        	
	            List<String[]> domainList =OTDomainDAO.getInstance().getAllList();
	            if(null != domainList && !domainList.isEmpty()){
	                if(null != timer1){
	                    timer1.cancel();
	                    timer1 = null ;
	                }
	                getTFs(domainList);
	                
	                
	            }else{
	                if(timer1 == null){
	                    timer1 = new Timer();
	                    timer1.schedule(new TunnelTreeTask(), 1000, 2000);
	                }
	            }
        	}
        }

        public void getTFs(List<String[]> domainList){
            try{
                if(null != domainList && !domainList.isEmpty()){
                    for(int i=0;i<domainList.size();i++){
                        MyData.InitState.put(domainList.get(i)[0], false);
                        DomainTreeTask domainTreeTask  = new DomainTreeTask();
                        domainTreeTask.domainId = domainList.get(i)[0];
                        Thread t = new Thread(domainTreeTask);
                        t.start();
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        class DomainTreeTask implements Runnable{
            String domainId = "";
            @Override
            public void run() {
                OTTfDAO oTTfDAO = new OTTfDAO();
                RetRpc retRpc = oTTfDAO.get(domainId,0);
                //RetRpc retRpc = OTTfDAO.getInstance().get(domainId,0);
                StringBuffer xml = new StringBuffer();
                xml.append("<massage type='tfs'>").append(retRpc.getContent()).append("</massage>");
                if(null != autobahnServer){
                    autobahnServer.sendToAll(xml.toString());
                }
                MyData.InitState.put(domainId, true);
            }
            
            
        }
        
//        class PingTask extends TimerTask{
//            
//            
//            
//        }

    }
    class StatTask extends TimerTask
    {
        @Override
        public void run() {
            MyData.setStat(OTTfDAO.getInstance().getStat());
        }
    }
	/**获取主备状态 主为true 备为fasle
	 * @return
	 */
	public boolean getPrimaryBackupStatus(){
		boolean status = false;
		AgileSysMAction httpAction = new AgileSysMAction();
		RetRpc retRpc = httpAction.get(AgileSysMBean.SYSM_SERVERSTATUS_URL);
		if(retRpc.getStatusCode()==200){
			if(null!=retRpc.getContent()&&(retRpc.getContent().equals("main")||retRpc.getContent().equals("free"))){
				status = true;
			}
		}else{
			status = true;
		}
		return status;
	}
}
