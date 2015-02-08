package com.huawei.agilete.northinterface.dao;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.huawei.agilete.base.action.DBAction;
import com.huawei.agilete.base.servlet.MyIfmInterface;
import com.huawei.agilete.base.servlet.MyL2vpnVpwsInstance;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.bean.OTDevice;
import com.huawei.agilete.northinterface.bean.OTFlux;
import com.huawei.agilete.northinterface.bean.OTNqa;
import com.huawei.agilete.northinterface.bean.OTTunnel;
import com.huawei.agilete.task.TaskFluxTunnel;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;


public final class OTFluxNowDAO  implements IOTDao{

    private static OTFluxNowDAO single = null;
    private OpsRestCaller client = null;
    public String domainId;
    private OTFluxNowDAO(){

    }
    public synchronized  static OTFluxNowDAO getInstance() {
        if (single == null) {  
            single = new OTFluxNowDAO();
        }  
        return single;
    }

    public RetRpc control(String domainId, String deviceId, String apiPath, RestType restType,String content){
        RetRpc result = new RetRpc();
        this.domainId = domainId;
        client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), deviceId);
        if(restType.equals(MyData.RestType.GET)){
            result = get(apiPath);
        }/*else if(restType.equals(MyData.RestType.DELETE)){
            result  = del(apiPath);
        }else if(restType.equals(MyData.RestType.POST)){
            result = add(content);
        }*/else if(restType.equals(MyData.RestType.PUT)){
            result  = edit(apiPath,content);
        }else{

        }

        return result;
    }


    public RetRpc add(String content){
        RetRpc result = new RetRpc();


        return result;
    }

    public RetRpc edit(String apiPath,String content){
    	String value = "";
    	if(null!=content&&!content.equals("")&&!content.equals("put")){
//    		System.out.println(content);
	    	OTFlux oTFlux = new OTFlux(content);
	    	value = oTFlux.getReceibveByte()+"_"+oTFlux.getSendByte()+"_1_"+oTFlux.getTime();
//	    	try {
//				FileWriter nodeFile = new FileWriter("D:/log/aaaaaaaaaa.txt",true);
//				nodeFile.append(oTFlux.getRttAverage()).append("________").append(value).append("\n");
////				nodeFile.flush();
//				nodeFile.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
    	}
        return get(apiPath,value);
    }

    public RetRpc del(String apiPath){
        RetRpc result = new RetRpc(403);
        return result;
    }

    public RetRpc get(String apiPath){
        return get(apiPath, "");
    }

    /***
     * @param apiPath: tunnel_tunnelName
     * value
     * 
     */
    public RetRpc get(String apiPath,String value,String... vlueNew){//代码优化修改 流量不再单独获取 04 可变数组方法
        RetRpc opsresult = new RetRpc();
        String type = "";
        String name  = "";
        //分解id判断类型
        if(2 == apiPath.split("_").length){
            type = apiPath.split("_")[0];
            name = apiPath.split("_")[1];
        }
        //声明新旧对象
        String oldReceibveByte = "0", newReceibveByte = "0";
        String oldSendByte = "0", newSendByte = "0";
        String oldIfoperspeed = "0", newIfoperspeed = "0";
        String oldTime = "0", newTime = "0";
        //声明新旧值
        String newContent = "";
        String oldContent = "";
      /*
       * 代码优化修改 流量不再单独获取 05 start
       */
        if(null != vlueNew && vlueNew.length==1 && null != value && !"".equals(value) && !value.equals("__1_")){
        	oldContent = value;
        	newContent = vlueNew[0];
        }else{
        	/*
             * 代码优化修改 流量不再单独获取 05 end
             */
        	
	        //判断类型获取不同的值
	        if(type.equals("tunnel")){//管道
	            if(null == value || "".equals(value)||value.equals("__1_")){//如果传入值为空，获取一遍数据
	            	MyIfmInterface myIfmInterface  =new MyIfmInterface(client);
	                HashMap<String, String> flux = myIfmInterface.getFlux(new String[]{name});
	                oldContent = flux.get(name);
	            }else{//否则使用传入的值
	            	oldContent = value;
	            }
	            //获取新值
	        	MyIfmInterface myIfmInterface  =new MyIfmInterface(client);
	            HashMap<String, String> flux = myIfmInterface.getFlux(new String[]{name});
	            newContent = flux.get(name);
	            
	        }else{//流
	        	if(null == value || "".equals(value)||value.equals("__1_")){//如果传入值为空，获取一遍数据
	        		MyL2vpnVpwsInstance myL2vpnVpwsInstance  =new MyL2vpnVpwsInstance(client);
	                HashMap<String, String> flux = myL2vpnVpwsInstance.getFlux(new String[]{name});
	                oldContent = flux.get(name);
	            }else{//否则使用传入的值
	            	oldContent = value;
	            }
	            //获取新值
	        	MyL2vpnVpwsInstance myL2vpnVpwsInstance  =new MyL2vpnVpwsInstance(client);
	            HashMap<String, String> flux = myL2vpnVpwsInstance.getFlux(new String[]{name});
	            newContent = flux.get(name);
	        }
        }
        String averageSum = "0";
        if(null!=newContent&&!newContent.equals("")&&null!=oldContent&&!"".equals(oldContent)&&!oldContent.equals("__1_")){
        	//声明计算对象
            BigDecimal receibveByte ;
            BigDecimal sendByte;
            BigDecimal time;
            
            //赋值
        	oldReceibveByte = oldContent.split("_")[0];
        	oldSendByte = oldContent.split("_")[1];
        	oldIfoperspeed = oldContent.split("_")[2];
        	oldTime = oldContent.split("_")[3];
        	
        	newReceibveByte = newContent.split("_")[0];
        	newSendByte = newContent.split("_")[1];
        	newIfoperspeed = newContent.split("_")[2];
        	newTime = newContent.split("_")[3];
        	
        	if((oldReceibveByte.equals("0") && oldSendByte.equals("0"))||oldReceibveByte.equals("")||oldSendByte.equals("")||oldTime.equals("")){
        		averageSum = "0";
        	}else{
	            try{//计算
		        	receibveByte = new BigDecimal(newReceibveByte).subtract(new BigDecimal(oldReceibveByte));
		            sendByte = new BigDecimal(newSendByte).subtract(new BigDecimal(oldSendByte));
		            time = new BigDecimal(newTime).subtract(new BigDecimal(oldTime)).divide(new BigDecimal(1000),2); 
		            if(time.equals(new BigDecimal("0"))||(sendByte.equals(new BigDecimal("0"))&&receibveByte.equals(new BigDecimal("0")))){//如果时间差为零则平均为零
		            	averageSum = "0";
		            }else{
		            	averageSum = receibveByte.add(sendByte).divide(time,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(8)).toString();
		            }
	            }catch (Exception e) {
	            	averageSum = "NA";
				}
        	}
        }
        List<OTFlux> list = new ArrayList<OTFlux>();
        OTFlux oTFlux = new OTFlux();
        oTFlux.setTime(String.valueOf(System.currentTimeMillis()));
        oTFlux.setRttAverage(averageSum);
        oTFlux.setReceibveByte(newReceibveByte);
        oTFlux.setSendByte(newSendByte);
        list.add(oTFlux);
        opsresult.setContent(oTFlux.getUiMessage(list));
        
        return opsresult;
    }
/*    *//***
     * @param apiPath: tunnel_tunnelName
     *//*
    public RetRpc get(String apiPath,String value){
    	RetRpc opsresult = new RetRpc();
    	String type = "";
    	String name  = "";
    	String body = "";
    	String statSpeed = "0";
    	if(2 == apiPath.split("_").length){
    		type = apiPath.split("_")[0];
    		name = apiPath.split("_")[1];
    	}
    	//读取stat.xml
    	List<String[]> statList = MyData.getStat();
    	for(int statCount=0;statCount<statList.size();statCount++){
    		String[] stat = statList.get(statCount);
    		if(null != stat[0] && null != stat[1] && stat[0].equals(domainId) && stat[1].equals(client.deviceId) && stat[2].equals(name)){
    			name = stat[3];
    			statSpeed = stat[4];
    			break;
    		}
    	}
    	
    	String content = "";
    	String content2 = "";
    	if("-1".equals(MyData.getSimulantData())){
    		if(null == value || "".equals(value)){
    			MyIfmInterface myIfmInterface  =new MyIfmInterface(client);
    			HashMap<String, String> flux = myIfmInterface.getFlux(new String[]{name});
    			content = flux.get(name);
    		}else{
    			content = value;
    		}
    		
    		if(null == value || "".equals(value)){
    			MyIfmInterface myIfmInterface  =new MyIfmInterface(client);
    			HashMap<String, String> flux = myIfmInterface.getFlux(new String[]{name});
    			content2 = flux.get(name);
    		}else{
    			content2 = value;
    		}
    	}
    	BigDecimal receibveByte ;
    	BigDecimal sendByte;
    	BigDecimal ifoperspeed;
    	BigDecimal time;
    	String twoValue = "NA";
    	//currBandwith = (new BigDecimal(Bandwidth).subtract(new BigDecimal(lastBandwidth.get(lbId)))).divide((new BigDecimal(timeInterval)).divide(new BigDecimal(1000)),2).multiply(new BigDecimal(8));
    	String receibveByte1 = "0", receibveByte2 = "0";
    	String sendByte1 = "0", sendByte2 = "0";
    	String ifoperspeed1 = "0", ifoperspeed2 = "0";
    	String time1 = "0", time2 = "0";
    	if(null != content && !"".equals(content) && null != content2 && !"".equals(content2)){
    		try{
    			receibveByte1 = content.split("_")[0];
    			sendByte1 = content.split("_")[1];
    			ifoperspeed1 = content.split("_")[2];
    			time1 = content.split("_")[3];
    			
    			receibveByte2 = content2.split("_")[0];
    			sendByte2 = content2.split("_")[1];
    			ifoperspeed2 = content2.split("_")[2];
    			time2 = content2.split("_")[3];
    			
    			receibveByte = new BigDecimal(receibveByte2).subtract(new BigDecimal(receibveByte1));
    			sendByte = new BigDecimal(sendByte2).subtract(new BigDecimal(sendByte1));
    			time = new BigDecimal(time2).subtract(new BigDecimal(time1)).divide(new BigDecimal(1000),2); 
    			if(null == statSpeed || "0".equals(statSpeed) || "".equals(statSpeed)){
    				if(!"0".equals(ifoperspeed1) && !"".equals(ifoperspeed1)){
    					statSpeed = ifoperspeed1;
    				}else if(!"0".equals(ifoperspeed2) && !"".equals(ifoperspeed2)){
    					statSpeed = ifoperspeed2;
    				}else{
    					statSpeed = "1";
    				}
    			}
    			if(receibveByte.equals(new BigDecimal("0")) && sendByte.equals(new BigDecimal("0"))){
    				twoValue = "0";
    			}else{
    				twoValue = receibveByte.add(sendByte).divide(time,10,BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(statSpeed),BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(8)).toString();
    			}
    		}catch(Exception e){
    			twoValue = "NA";
    			//e.printStackTrace();
    		}
    	}
    	
    	List<OTNqa> list = new ArrayList<OTNqa>();
    	OTNqa nqa = new OTNqa();
    	//        MyData.setSimulantData("10");
    	if("-2".equals(MyData.getSimulantData())){
    		nqa.setTime(String.valueOf(System.currentTimeMillis()));
    		nqa.setRttAverage(String.valueOf((int)(Math.random()*100)));
    	}else if("-1".equals(MyData.getSimulantData())){
    		//long ave = Long.valueOf(receibveByte) +Long.valueOf(sendByte);
    		nqa.setTime(String.valueOf(System.currentTimeMillis()));
    		nqa.setRttAverage(twoValue);
    	}else{
    		nqa.setTime(String.valueOf(System.currentTimeMillis()));
    		nqa.setRttAverage(MyData.getSimulantData());
    	}
    	//String aa =RrdTool.getInstance().update_fetchRrd("ifmflux", nqa.getTime(), nqa.getRttAverage());
    	////System.out.println("rrdtool="+aa);
    	list.add(nqa);
    	opsresult.setContent(nqa.getUiMessage(list));
    	return opsresult;
    }
*/


    public RetRpc runTaskMain(String domainId,String type){

        RetRpc opsresult = new RetRpc();
        List<OTDevice> deviceList = OTDeviceDAO.getInstance().getByDomain(domainId);
        if(null == deviceList || deviceList.size() == 0){
            return opsresult;
        }
        for(int index=0;index<deviceList.size();index++){
            OTDevice oTDevice = deviceList.get(index);
            OpsRestCaller client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), oTDevice.getId());
            //根据当前设备所有tunnel
            OTTunnelDAO.getInstance().setClient(client);
            List<OTTunnel> tunnelList = OTTunnelDAO.getInstance().getOps("",false);
            for(int i=0;i<tunnelList.size();i++){
                OTTunnel tunnel = tunnelList.get(i);
                TaskFluxTunnel tunnelTask = new TaskFluxTunnel(oTDevice.getId());
                long t = 0;
                try{
                    t = Long.parseLong("0");
                }catch(Exception e){
                }
                tunnelTask.client = client;
                tunnelTask.setInterval(t);
                tunnelTask.setTitle("FLUX"+"_"+type+"_"+client.deviceId+"_"+tunnel.getName()); //NQA CE12800_A TUNNEL1
                OTNqa tunnelNqa = new OTNqa();
                tunnelNqa.setName(tunnelTask.getTitle());
                tunnelTask.body = tunnel.getName();
                if("tunnel".equals(type)){
                    MyData.TaskChildControl.addTask(tunnelTask);
                }
            }
        }
        return opsresult;
    }


    public RetRpc runTask(OpsRestCaller client,TaskFluxTunnel taskWrapper){
        RetRpc opsresult = new RetRpc();
        MyIfmInterface myIfmInterface  =new MyIfmInterface(client);
        HashMap<String, String> flux = myIfmInterface.getFlux(new String[]{taskWrapper.body});
        String content= flux.get(taskWrapper.body);
        DBAction db = new DBAction();
        Boolean dbFlag = db.insert("flux", taskWrapper.getTitle(), String.valueOf(System.currentTimeMillis()), content);
        if(dbFlag){
            List<String[]> allList = db.getAll("flux", taskWrapper.getTitle());
            int maxlengh = 100;
            if(allList.size() >= maxlengh){
                for(int i=0;i<=allList.size()-maxlengh;i++){
                    String[] flagData = allList.get(i);
                    dbFlag = db.delete("flux", taskWrapper.getTitle(), flagData[0]);
                }
            }
        }

        return opsresult;
    }


    public String getOpsBody(String type,OTNqa nqa){
        OTNqa oTNqa = nqa;
        String body = oTNqa.getOpsMessage();
        return body;
    }




    public OpsRestCaller getClient() {
        return client;
    }
    public void setClient(OpsRestCaller client) {
        this.client = client;
    }
	@Override
	public RetRpc edit(String content) {
		RetRpc result = new RetRpc(403);
        return result;
	}
    public static void main(String[] args) {
    	
        //String content = "<tunnels><tunnel><name>Tunnel4</name><interfaceName>Tunnel4</interfaceName><identifyIndex>502</identifyIndex><ingressIp>4.4.4.4</ingressIp><egressIp>2.2.2.2</egressIp><hotStandbyTime>15</hotStandbyTime><isDouleConfig>false</isDouleConfig><desDeviceName>123</desDeviceName><tunnelPaths><tunnelPath><pathType>hot_standby</pathType>backup<pathName>path</pathName><lspState>down</lspState></tunnelPath></tunnelPaths><paths><path><name>path</name><nextHops><nextHop><id>1</id><nextIp>10.1.1.1</nextIp></nextHop></nextHops></path></paths></tunnel></tunnels>";
        //OTNqaDAO.getInstance().get("1", "5", "");
        System.out.println(OTFluxNowDAO.getInstance().control("2", "3", "vlanif_Vlanif801", MyData.RestType.GET, ""));
    }


}
