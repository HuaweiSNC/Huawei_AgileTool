package com.huawei.agilete.northinterface.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.huawei.agilete.base.action.DBAction;
import com.huawei.agilete.base.servlet.MyOspfv2;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.action.AgileSysMAction;
import com.huawei.agilete.northinterface.action.AgileSysMBean;
import com.huawei.agilete.northinterface.bean.OTDevice;
import com.huawei.agilete.northinterface.bean.OTLink;
import com.huawei.agilete.northinterface.bean.OTTopoLink;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;

public final class OTLinkDAO implements IOTDao{

    private static OTLinkDAO single = null;
    private String domainId;
    private OTLinkDAO(){

    }
    public synchronized  static OTLinkDAO getInstance() {
        if (single == null) {  
            single = new OTLinkDAO();
        }  
        return single;
    }

    public RetRpc control(String domainId, String deviceId, String apiPath, RestType restType,String content){
        RetRpc result = new RetRpc();
        this.domainId = domainId;
        if(restType.equals(MyData.RestType.GET)){
            if("ospf".equals(apiPath)){
                result = getOspf(apiPath);
            }else{
                result = get(apiPath);
            }
        }else if(restType.equals(MyData.RestType.DELETE)){
            result = del(apiPath);
        }else if(restType.equals(MyData.RestType.PUT)){
            result = edit(content);
        }else if(restType.equals(MyData.RestType.POST)){
            result = add(content);
        }else{

        }
        return result;
    }

    public RetRpc add(String content){
        RetRpc result = new RetRpc();
        OTTopoLink oTTopoLink = new OTTopoLink(content);
        if(!"".equals(oTTopoLink.getCheck()) && "".equals(oTTopoLink.getName())){
            result.setStatusCode(500);
            return result;
        }
        if(200 == result.getStatusCode()){
            
            /*
             * 修改访问SYSM  SNC添加线
             */
            AgileSysMAction agileSysMAction = new AgileSysMAction();
            result = agileSysMAction.post(AgileSysMBean.SYSM_LINK_SNC_URL,content);
            /*
             * 
             */
            Boolean flag = false;
            if(result.getStatusCode()==200||result.getStatusCode()==202){
                DBAction db = new DBAction();
                flag = db.insert("links",domainId, oTTopoLink.getName(), content);
            }
            if(!flag){
                result.setStatusCode(500);
                result.setContent("db error");
            }else{
                result.setStatusCode(200);
                result.setContent("true");
            }
        }
        return result;
    }

    public RetRpc del(String linkName){
        RetRpc result = new RetRpc();

        //        OTTopoLink oTTopoLink = new OTTopoLink(content);
        //        if(!"".equals(oTTopoLink.getCheck()) && "".equals(oTTopoLink.getName())){
        //            result = false;
        //        }
        if(null != linkName && !"".equals(linkName)){
        	/*
             * 修改访问SYSM  SNC删除线
             */
            AgileSysMAction agileSysMAction = new AgileSysMAction();
            result = agileSysMAction.delete(AgileSysMBean.SYSM_LINK_SNC_URL+"/"+linkName);
            if(result.getStatusCode()==200||result.getStatusCode()==202){
            	
            
	            DBAction db = new DBAction();
	            Boolean flag = db.delete("links",domainId, linkName);
	            
	            /*
	             * 
	             */
	            if(!flag){
	                result.setStatusCode(500);
	                result.setContent("db error");
	            }else{
	                result.setStatusCode(200);
	                result.setContent("true");
	            }
            }
        }

        //        if(result){
        //            DBAction db = new DBAction();
        //            result = db.delete("links",domainId, linkName);
        //        }

        return result;
    }
    public RetRpc edit(String content){
        RetRpc result = new RetRpc();
        result.setStatusCode(403);


        return result;
    }
    public RetRpc get(String apiPath){
        RetRpc result = new RetRpc();
        if(null != domainId && !"".equals(domainId)){
            DBAction db = new DBAction();
            List<String[]> list = db.getAll("links",domainId);
            StringBuffer xml = new StringBuffer();
            xml.append("<topoLinks>");
            for(int i=0;i<list.size();i++){
                String flag = list.get(i)[1];
                flag = flag.replace("<topoLinks>", "");
                flag = flag.replace("</topoLinks>", "");
                xml.append(flag);
            }
            xml.append("</topoLinks>");
            result.setContent( xml.toString());
        }else{
            result.setStatusCode(403);
            result.setContent("ID is null!");
        }
        return result;
    }

    public RetRpc getOspf(String apiPath){
        RetRpc opsresult = new RetRpc();
        List<OTLink> list = getList(apiPath);
        for(int i=0;i<list.size()-1;i++){
            for(int j=list.size()-1;j>i;j--){
                if (list.get(j).getHeadNodeConnectorip().equals(list.get(i).getHeadNodeConnectorip()) 
                        && list.get(j).getTailNodeConnectorip().equals(list.get(i).getTailNodeConnectorip())) {
                    list.remove(j);
                } 
            } 
        } 

        if(null != list){
            OTLink oTLink = new OTLink();
            String flag = oTLink.getUiMessage(list);
            opsresult.setContent(flag);
        }
        return opsresult;
    }

    public List<OTLink> getList(String apiPath){
        List<OTLink> list = new ArrayList<OTLink>();
        List<OTDevice> deviceList = OTDeviceDAO.getInstance().getByDomain(domainId);
        OTLink oTLink = new OTLink();
        if(null == deviceList || deviceList.size() == 0){
            MyData.InitState .put(domainId, true);
            return null;
        }
        HashMap<String, OTDevice> devicesData = new HashMap<String, OTDevice>();
        for(int index=0;index<deviceList.size();index++){
            OTDevice oTDevice = deviceList.get(index);
            OpsRestCaller client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), oTDevice.getId());
            MyOspfv2 myOspfv2 = new MyOspfv2(client);
            RetRpc opsresult = myOspfv2.get(null);
            if(opsresult.getStatusCode()==200){
                oTLink.getOtlinkList(deviceList,oTDevice,list,opsresult.getContent(),devicesData);
            }
        }
        return list;
    }



    public static void main(String[] args) {
        //OTLinkDAO.getInstance().add("1", "<topoLinks><topoLink><name>12</name><headNodeConnector><Connectorid>1</Connectorid><toponode><nodeID>1</nodeID><nodeType>1.1.1.1</nodeType></toponode></headNodeConnector><tailNodeConnector><Connectorid>3</Connectorid><toponode><nodeID>2</nodeID><nodeType>2.2.2.2</nodeType></toponode></tailNodeConnector><cost>1</cost><bandwidth>10</bandwidth></topoLink></topoLinks>");
        ////System.out.println(OTLinkDAO.getInstance().get("1"));
        ////System.out.println(OTLinkDAO.getInstance().del("3",""));
        //System.out.println(OTLinkDAO.getInstance().control("2", "", "", MyData.RestType.GET, ""));
    }



}
