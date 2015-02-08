package com.huawei.agilete.northinterface.dao;

import com.huawei.agilete.data.MyData.RestType;
import com.huawei.networkos.ops.response.RetRpc;

public interface IOTDao {

    /****
     * DAO 模块提供的服务
     * @param domainId  域ID号
     * @param deviceId  设备ID号
     * @param apiPath   输入 REST api路径
     * @param restType  输入REST 类型 : GET, DELETE, PUT, POST
     * @param content   输入需要处理的body体 
     * @return
     */
    public RetRpc control(String domainId, String deviceId, String apiPath, RestType restType,String content);
    
    public RetRpc edit(String content);
    public RetRpc del(String apiPath);
    public RetRpc get(String apiPath);
    public RetRpc add(String content);
}
