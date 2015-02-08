package com.huawei.agilete.northinterface.web;


import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.annotations.Workspace;
import org.apache.wink.server.utils.LinkBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.service.ListenerManager;
import com.huawei.agilete.northinterface.service.RrdtoolManager;
import com.huawei.agilete.northinterface.util.ProcessManager;
import com.huawei.networkos.ops.response.RetRpc;

@WebListener
@Workspace(workspaceTitle = "Agile TE 0.1", collectionTitle = "Agile TE Server")
@Path("domain")
public class OTWebService  {
	long time1 = 0;
	private static final String RESTAPI = "restapipath";

	private static final String templateid = "templateId";
	private static final String PATH_templateId = "{templateId}";
	private static final String PATH_RESTAPI = "{restapipath:.*}";
	private static final Logger logger      = LoggerFactory.getLogger(OTWebService.class);



	
	
	//测试接口
	@Path ("/ceshijiekou/"+PATH_RESTAPI+"/"+PATH_templateId)
	@GET
	@Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response ceshijiekou(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath,  @PathParam(templateid) String templateId,String content) {
		logger.error("POST:{}{},body={}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters(),content});
		RetRpc result = new RetRpc();
		
		System.out.println(content);
		RrdtoolManager createLis = new RrdtoolManager();
		result.setStatusCode(200);
		result.setContent(createLis.refurbishData(apiPath));
		
		return Response.status(result.getStatusCode()).entity(result.getContent()).build();
	}
	
	//创建配置监听
	@Path ("/createListener/"+PATH_templateId)
	@POST
	@Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response postAssignment(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath,  @PathParam(templateid) String templateId,String content) {
		logger.error("POST:{}{},body={}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters(),content});
		
		RetRpc result = new RetRpc();
		if(null!= content&&!content.equals("")){
//			System.out.println("*************************");
			System.out.println(content);
			ListenerManager createLis = new ListenerManager();
			
			result.setStatusCode(200);
			result.setContent(createLis.createMrtg(content,templateId));
//			createLis.create(content);
//			System.out.println("*************************");
		}else{
			result.setStatusCode(500);
			result.setContent("Body is null!");
		}
		return Response.status(result.getStatusCode()).entity(result.getContent()).build();
	}
	
	
	//修改配置监听
	@Path ("/modifyListener/"+PATH_templateId)
	@PUT
	@Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response modifyAssignment(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath,  @PathParam(templateid) String templateId,String content) {
		logger.error("POST:{}{},body={}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters(),content});
		RetRpc result = new RetRpc();
		if(null!= content&&!content.equals("")){
//			System.out.println("*************************");
			System.out.println(content);
			ListenerManager createLis = new ListenerManager();
			result.setStatusCode(200);
			result.setContent(createLis.putMrtg(content,templateId));
//			createLis.create(content);
//			System.out.println("*************************");
		}else{
			result.setStatusCode(500);
			result.setContent("Body is null!");
		}
		return Response.status(result.getStatusCode()).entity(result.getContent()).build();
	}
	
	//修改配置监听
	@Path ("/modifyListener/"+PATH_templateId)
	@POST
	@Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response modifyAssignment_Post(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath,  @PathParam(templateid) String templateId,String content) {
		logger.error("POST:{}{},body={}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters(),content});
		RetRpc result = new RetRpc();
		MultivaluedMap<String, String> methodType = uriInfo.getQueryParameters();
//		RestType restType = MyData.RestType.POST;
		if(null != methodType.get("methodType") && null != methodType.get("methodType").get(0) && "PUT".equals(methodType.get("methodType").get(0))){
			try{
				return modifyAssignment(linksBuilders, uriInfo, apiPath, templateId, content);
			}catch (Exception e) {
			}
		}
		return Response.status(result.getStatusCode()).entity(result.getContent()).build();
	}
	
	
	//获取配置监听
	@Path ("/getListener/"+PATH_templateId+"/"+PATH_RESTAPI)
	@GET
	@Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getAssignment(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath,  @PathParam(templateid) String templateId,String content) {
		RetRpc result = new RetRpc();
		if(null!= apiPath&&!apiPath.equals("")){
			ListenerManager createLis = new ListenerManager();
			result.setStatusCode(200);
			result.setContent(createLis.getListenerToUi(apiPath,templateId).getContent());
		}else{
			result.setStatusCode(500);
			result.setContent("Id is null!");
		}
		return Response.status(result.getStatusCode()).entity(result.getContent()).build();
	}
	
	
	//获取配置监听列表
	@Path ("/getListenerList/"+PATH_templateId)
	@GET
	@Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getAssignmentList(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath,  @PathParam(templateid) String templateId,String content) {
		logger.error("POST:{}{},body={}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters(),content});
		RetRpc result = new RetRpc();
//		if(null!= content&&!content.equals("")){
			result.setStatusCode(200);
//			result.setContent("OK");
//			System.out.println("*************************");
			System.out.println(content);
			ListenerManager createLis = new ListenerManager();
			result.setContent(createLis.getListenerList(templateId));
//			createLis.create(content);
//			System.out.println("*************************");
//		}else{
//			result.setStatusCode(500);
//			result.setContent("cuowu");
//		}
//		result.setContent("<listenerList>\n	<listenerId>aaaa_aaaaa</listenerId>\n	<listenerId>bbbb_bbbbb</listenerId>\n	<listenerId>cccc_ccccc</listenerId>\n</listenerList>");
		return Response.status(result.getStatusCode()).entity(result.getContent()).build();
	}
	
	//获取配置监听列表
	@Path ("/getListenerList")
	@GET
	@Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getAssignmentListByAll(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath,  @PathParam(templateid) String templateId,String content) {
		logger.error("POST:{}{},body={}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters(),content});
		RetRpc result = new RetRpc();
			result.setStatusCode(200);
			System.out.println(content);
			ListenerManager createLis = new ListenerManager();
			result.setContent(createLis.getListenerList(""));
		return Response.status(result.getStatusCode()).entity(result.getContent()).build();
	}

	
	/**
	 * 关闭某个进程
	 * @param linksBuilders
	 * @param uriInfo
	 * @param apiPath
	 * @param templateId
	 * @param content
	 * @return
	 */
	@Path ("/shutDownListener/"+PATH_RESTAPI)
	@DELETE
	@Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response shutDownAssignment(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath,  @PathParam(templateid) String templateId,String content) {
		logger.error("POST:{}{},body={}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters(),content});
		RetRpc result = new RetRpc();
//		result = OTDeviceDAO.getInstance().get("",true);
		if(null!= apiPath&&!apiPath.equals("")){
			result.setStatusCode(200);
			result.setContent("OK");
			System.out.println("*************************");
			ProcessManager.killProcess(apiPath);//关闭进程
			System.out.println("*************************");
		}else{
			result.setStatusCode(500);
			result.setContent("cuowu");
		}
		return Response.status(result.getStatusCode()).entity(result.getContent()).build();
	}
	
	
	



}
