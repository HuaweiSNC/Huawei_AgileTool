package com.huawei.agilete.northinterface.dao;

import com.huawei.agilete.base.action.DBAction;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.networkos.ops.response.RetRpc;

public final class OTVlanRangeDAO implements IOTDao{

    private static OTVlanRangeDAO single = null;
    private String domainId;
    private OTVlanRangeDAO(){

    }
    public synchronized  static OTVlanRangeDAO getInstance() {
        if (single == null) {  
            single = new OTVlanRangeDAO();
        }  
        return single;
    }

    @Override
    public RetRpc control(String domainId, String deviceId, String apiPath, RestType restType,String content){
        RetRpc result = new RetRpc();
        this.domainId = domainId;
        if(restType.equals(MyData.RestType.GET)){
                result = get(domainId);
        }else if(restType.equals(MyData.RestType.DELETE)){
            result = del(domainId);
        }else if(restType.equals(MyData.RestType.PUT)){
            result = edit(content);
        }else if(restType.equals(MyData.RestType.POST)){
            result = add(content);
        }else{
            result.setStatusCode(403);
        }
        return result;
    }

    @Override
    public RetRpc add(String content){
        RetRpc result = new RetRpc();
        if(null == content || "".equals(content)){
            result.setStatusCode(500);
            return result;
        }
        if(200 == result.getStatusCode()){
            Boolean flag = false;
            DBAction db = new DBAction();
            flag = db.insert("vlanranges",domainId, domainId, content);
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

    @Override
    public RetRpc del(String id){
        RetRpc result = new RetRpc();
        if(null != id && !"".equals(id)){
            DBAction db = new DBAction();
            Boolean flag = db.delete("vlanranges",domainId, domainId);
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
    @Override
    public RetRpc edit(String content){
        RetRpc result = add(content);
        return result;
    }
    @Override
    public RetRpc get(String apiPath){
        RetRpc result = new RetRpc();
        if(null != domainId && !"".equals(domainId)){
            DBAction db = new DBAction();
            String xml = db.get("vlanranges",domainId,domainId);
            result.setContent( xml);
        }else{
            result.setStatusCode(403);
            result.setContent("ID can not be null!");
        }
        return result;
    }

    public static void main(String[] args) {
    }
}
