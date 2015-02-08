package com.huawei.agilete.northinterface.dao;

import com.huawei.agilete.base.action.DBAction;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.networkos.ops.response.RetRpc;

public final class OTWtrDAO implements IOTDao{

    private static OTWtrDAO single = null;
    private String domainId;
    private OTWtrDAO(){

    }
    public synchronized  static OTWtrDAO getInstance() {
        if (single == null) {  
            single = new OTWtrDAO();
        }  
        return single;
    }

    public RetRpc control(String domainId, String deviceId, String apiPath, RestType restType,String content){
        RetRpc result = new RetRpc();
        this.domainId = domainId;
        if(restType.equals(MyData.RestType.GET)){
            result = get(apiPath);
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
        DBAction db = new DBAction();
        Boolean flag = db.insert("variable","wtr", domainId, content);
        if(!flag){
            result.setStatusCode(500);
            result.setContent("db error");
        }else{
            result.setStatusCode(200);
            result.setContent("true");
        }
        return result;
    }

    public RetRpc del(String content){
        RetRpc result = new RetRpc();

        DBAction db = new DBAction();
        Boolean flag = db.delete("variable","wtr", domainId);
        if(!flag){
            result.setStatusCode(500);
            result.setContent("db error");
        }else{
            result.setStatusCode(200);
            result.setContent("true");
        }

        //        if(result){
        //            DBAction db = new DBAction();
        //            result = db.delete("links",domainId, linkName);
        //        }

        return result;
    }
    public RetRpc edit(String content){
        return add(content);
    }
    public RetRpc get(String apiPath){
        RetRpc result = new RetRpc();
        if(null != domainId && !"".equals(domainId)){
            DBAction db = new DBAction();
            String flag = db.get("variable","wtr", domainId);
            result.setContent( flag);
        }else{
            result.setStatusCode(403);
            result.setContent("ID is null!");
        }
        return result;
    }


    public static void main(String[] args) {
        //OTLinkDAO.getInstance().add("1", "<topoLinks><topoLink><name>12</name><headNodeConnector><Connectorid>1</Connectorid><toponode><nodeID>1</nodeID><nodeType>1.1.1.1</nodeType></toponode></headNodeConnector><tailNodeConnector><Connectorid>3</Connectorid><toponode><nodeID>2</nodeID><nodeType>2.2.2.2</nodeType></toponode></tailNodeConnector><cost>1</cost><bandwidth>10</bandwidth></topoLink></topoLinks>");
        ////System.out.println(OTLinkDAO.getInstance().get("1"));
        ////System.out.println(OTLinkDAO.getInstance().del("3",""));

    }



}
