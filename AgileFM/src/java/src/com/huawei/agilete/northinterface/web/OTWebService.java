package com.huawei.agilete.northinterface.web;
import java.net.URI;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.wink.common.annotations.Workspace;
import org.apache.wink.server.utils.LinkBuilders;
import com.huawei.agilete.base.servlet.util.HttpRequest;
import com.huawei.agilete.base.servlet.util.WebSocketClent;
import com.huawei.agilete.base.servlet.util.WebSocketOtherClient;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.northinterface.bean.WebSoketService;
import com.huawei.networkos.ops.response.RetRpc;

@Workspace(workspaceTitle = "Agile TE 0.1", collectionTitle = "Agile TE Server")
@Path("agilete/alarm")
public class OTWebService {
	@POST
	@Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response postCommonAlarm2(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo,String content) {
		try {
			//打开一个连接ops的通道
			WebSoketService service = WebSoketService.getInstance();
			RetRpc result = service.cretaeChanel();
			if(200!=result.getStatusCode()){
				return Response.status(result.getStatusCode()).entity(result.getContent()).build();
			}
			// 与ops推送服务器建立连接的客户端，并且建立一个websoket服务端，该服务端 向外提供的连接为：ws://serviceIP:port 具体的配置 在my.ini里面
			service.connectToOpsWebsocketService();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
		return Response.status(200).entity("<ok></ok>").build();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Path ("/other/" )
	@POST
	@Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response postOterAlarm(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo,String content) {
		try {
			//该连接表示该客户端只是接受linkdown告警信息
			String uri ="ws://"+MyData.getAlarmServerIp()+":"+MyData.getAlarmServerPort()+"/alarm/other";
			//String uri ="ws://localhost:9038/alarm/other";
			URI uri2  = URI.create(uri);
			//创建客户端   （接受的消息为WebSocketOtherClient类中onMessage(String arg0)中的参数arg0）
			WebSocketOtherClient socketTest = new WebSocketOtherClient(uri2);
			//与推送服务器进行连接
			socketTest.connect();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
		
		return Response.status(200).build();
	}
	
	
}
