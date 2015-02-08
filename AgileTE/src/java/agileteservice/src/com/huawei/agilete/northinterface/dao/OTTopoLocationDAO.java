package com.huawei.agilete.northinterface.dao;

import com.huawei.agilete.base.action.DBAction;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.networkos.ops.response.RetRpc;

public final class OTTopoLocationDAO  implements IOTDao{
    
    private static OTTopoLocationDAO single = null;
    private String domainId;
    private OTTopoLocationDAO(){
        
    }
    public synchronized  static OTTopoLocationDAO getInstance() {
         if (single == null) {  
             single = new OTTopoLocationDAO();
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
        }else if(restType.equals(MyData.RestType.POST)){
            result = add(content);
        }else if(restType.equals(MyData.RestType.PUT)){
            result = edit(content);
        }else{
            
        }
        return result;
    }
    
    public RetRpc add(String content){
        RetRpc result = new RetRpc();
        if(null == content || "".equals(content)){
            result.setStatusCode(500);
            return result;
        }
        if(200 == result.getStatusCode()){
            DBAction db = new DBAction();
            Boolean flag = db.insert("topolocation",domainId, domainId, content);
            if(!flag){
                result.setStatusCode(500);
                result.setContent("db error");
            }else{
                result.setStatusCode(200);
                result.setContent("ok");
            }
        }
        return result;
    }
    
    public RetRpc del(String apiPath){
        RetRpc result = new RetRpc();
        if(null != domainId && !"".equals(domainId)){
            DBAction db = new DBAction();
            Boolean flag = db.delete("topolocation",domainId, domainId);
            if(!flag){
                result.setStatusCode(500);
                result.setContent("db error");
            }else{
                result.setStatusCode(200);
                result.setContent("ok");
            }
        }
        return result;
    }
    public RetRpc edit(String content){
        return add(content);
    }
    public RetRpc get(String apiPath){
        RetRpc result = new RetRpc();
        if(null != domainId && !"".equals(domainId)){
            DBAction db = new DBAction();
            String flag = db.get("topolocation",domainId,domainId);
            if(null != flag){
                result.setContent(flag);
            }
        }else{
            result.setStatusCode(403);
            result.setContent("ID is null!");
        }
        return result;
    }
    
    
    public static void main(String[] args) {
        RetRpc  r=OTTopoLocationDAO.getInstance().control("2", "", "", MyData.RestType.POST, "sfgsdfgdsfgdsffg");
        //System.out.println(r);
    }
    
    

}
