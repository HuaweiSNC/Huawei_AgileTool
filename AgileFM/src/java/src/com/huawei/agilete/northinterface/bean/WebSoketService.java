package com.huawei.agilete.northinterface.bean;

import java.net.URI;

import com.huawei.agilete.base.servlet.util.HttpRequest;
import com.huawei.agilete.base.servlet.util.WebSocketClent;
import com.huawei.agilete.data.MyData;
import com.huawei.networkos.ops.response.RetRpc;

public class WebSoketService {
	private static WebSoketService ws = null;

	public synchronized static WebSoketService getInstance() {
		if (ws == null) {
			ws = new WebSoketService();
		}
		return ws;
	}

	private WebSoketService() {

	}

	// 打开一个连接ops的通道
	public RetRpc cretaeChanel() {
		RetRpc result = null;
		HttpRequest request = new HttpRequest();
		StringBuffer str = new StringBuffer();
		str.append("http://").append(MyData.getOpsServerIp()).append(":")
				.append(MyData.getAlarmOpsServerPort()).append("/channels/2");
		String uri2 = str.toString();
		result = request.connect(uri2, "POST", null);
		if (result.getStatusCode() == 202 || result.getStatusCode() == 200) {
			result.setStatusCode(200);
		}
		return result;
	}
	
	
	// 与ops推送服务器建立连接的客户端，并且建立一个websoket服务端，该服务端 向外提供的连接为：ws://serviceIP:port 具体的配置 在my.ini里面
	public int connectToOpsWebsocketService() {
		int result = 0;
		String uriStr = "ws://" + MyData.getOpsServerIp() + ":"
				+ MyData.getAlarmOpsServerPort() + "/channels/2/data";
		URI uri = URI.create(uriStr);
		//WebSocketClent包含一个websoket服务端
		WebSocketClent client = WebSocketClent.getInstance(uri);
		result = client.getReadyState();
		return result;
	}

}
