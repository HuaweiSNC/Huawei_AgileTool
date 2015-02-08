package com.huawei.agilete.northinterface.dao;

import com.huawei.networkos.ops.response.RetRpc;
import com.huawei.agilete.data.MyData.RestType;

public interface IOTDao {

	public RetRpc control(String domainId, String deviceId, String apiPath, RestType restType,String content);
	public RetRpc edit(String content);
	public RetRpc del(String apiPath);
	public RetRpc get(String apiPath);
	public RetRpc add(String content);
}
