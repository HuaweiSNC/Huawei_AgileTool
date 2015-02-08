package com.huawei.agilete.northinterface.web;


import java.io.IOException;
import java.net.URI;

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

import com.huawei.agilete.base.common.VerifyCodeUtils;
import com.huawei.agilete.base.servlet.util.WebSocketOtherClient;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.beanfactory.OTDaoFactory;
import com.huawei.agilete.northinterface.dao.IOTDao;
import com.huawei.agilete.northinterface.dao.OTDeviceDAO;
import com.huawei.agilete.northinterface.dao.OTDomainDAO;
import com.huawei.agilete.northinterface.dao.OTLoginDao;
import com.huawei.agilete.northinterface.dao.OTRegistDao;
import com.huawei.networkos.ops.response.RetRpc;


@Workspace(workspaceTitle = "Agile TE 0.1", collectionTitle = "Agile TE Server")
@Path("agilete/domains")
public class OTWebService{
    long time1 = 0;
    private static final String RESTAPI = "restapipath";

    private static final String domainid = "domainId";
    private static final String PATH_domainId = "{domainId}";
    private static final String DEVICEID = "deviceid";
    private static final String PATH_DEVICEID = "{deviceid}";
    private static final String PATH_RESTAPI = "{restapipath:.*}";
    private static final Logger logger      = LoggerFactory.getLogger(OTWebService.class);


    //管理域操作//
    /**
     * getall管理域
     * 
     */
    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDomains(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath) {
        logger.error("GET:{}",new Object[]{uriInfo.getPath()});
        RetRpc result = new RetRpc();
        result = OTDomainDAO.getInstance().getAll();
        return Response.status(result.getStatusCode()).entity(result.getContent()).build();
    }

    @Path (PATH_domainId)
    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDomain(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath, @PathParam(domainid) String domainId) {
        logger.error("GET:{}{}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters()});
        RetRpc result = new RetRpc();
        result = OTDomainDAO.getInstance().get(domainId);
        return Response.status(result.getStatusCode()).entity(result.getContent()).build();
    }

    @PUT
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response putDomain(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath,  @PathParam(domainid) String domainId,String content) {
        logger.error("PUT:{}{},body={}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters(),content});
        RetRpc result = new RetRpc();
        result = OTDomainDAO.getInstance().edit(content);
        return Response.status(result.getStatusCode()).entity(result.getContent()).build();
    }

    @POST
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postDomain(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath,  @PathParam(domainid) String domainId,String content) {
        logger.error("POST:{}{},body={}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters(),content});
        RetRpc result = new RetRpc();
        MultivaluedMap<String, String> methodType = uriInfo.getQueryParameters();
        if(null != methodType.get("methodType")){
            if("PUT".equals(methodType.get("methodType").get(0))){
                result = OTDomainDAO.getInstance().edit(content);
            }else if("DELETE".equals(methodType.get("methodType").get(0))){
                result = OTDomainDAO.getInstance().del(content);
            }else{
                return Response.status(Response.Status.BAD_REQUEST).entity("false").build();
            }
        }else{
            result = OTDomainDAO.getInstance().add(content);
        }
        return Response.status(result.getStatusCode()).entity(result.getContent()).build();
    }
    @Path (PATH_domainId)
    @DELETE
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response delDomain(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath,  @PathParam(domainid) String domainId,String content) {
        logger.error("DELETE:{}{},body={}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters(),content});
        RetRpc result = new RetRpc();
        if(null == uriInfo.getQueryParameters()){
            return Response.status(Response.Status.BAD_REQUEST).entity("false").build();
        }
        result = OTDomainDAO.getInstance().del(domainId);
        return Response.status(result.getStatusCode()).entity(result.getContent()).build();
    }

    @Path (PATH_domainId)
    @POST
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postDelDomain(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath,  @PathParam(domainid) String domainId,String content) {
        logger.error("DELETE:{}{},body={}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters(),content});
        RetRpc result = new RetRpc();
        MultivaluedMap<String, String> methodType = uriInfo.getQueryParameters();
        if(null != methodType.get("methodType")){
            if("DELETE".equals(methodType.get("methodType").get(0))){
                result = OTDomainDAO.getInstance().del(domainId);
            }else{
                return Response.status(Response.Status.BAD_REQUEST).entity("false").build();
            }
        }
        return Response.status(result.getStatusCode()).entity(result.getContent()).build();
    }
    
    //设备操作//
    @Path ("/devices")
    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDevices(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath, @PathParam(domainid) String domainId,@PathParam(DEVICEID) String deviceid) {
        logger.error("GET:{}{}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters()});
        RetRpc result = new RetRpc();
        result = OTDeviceDAO.getInstance().get("",true);
        return Response.status(result.getStatusCode()).entity(result.getContent()).build();
    }

    @Path ("/devices/"+PATH_DEVICEID)
    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDevice(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath, @PathParam(domainid) String domainId,@PathParam(DEVICEID) String deviceid) {
        logger.error("GET:{}{}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters()});
        RetRpc result = new RetRpc();
        result = OTDeviceDAO.getInstance().get(deviceid,false);
        return Response.status(result.getStatusCode()).entity(result.getContent()).build();
    }

    @Path ("/devices")
    @PUT
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response putDevice(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath,String content) {
        logger.error("PUT:{}{},body={}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters(),content});
        RetRpc result = OTDeviceDAO.getInstance().edit(content);

        return Response.status(result.getStatusCode()).entity(result.getContent()).build();
    }

    @Path ("/devices")
    @POST
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postDevice(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath, String content) {
        logger.error("POST:{}{},body={}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters(),content});
        RetRpc result = new RetRpc();
        MultivaluedMap<String, String> methodType = uriInfo.getQueryParameters();
        if(null != methodType.get("methodType")){
            if("PUT".equals(methodType.get("methodType").get(0))){
                result = OTDeviceDAO.getInstance().edit(content);
            }else if("DELETE".equals(methodType.get("methodType").get(0))){
                try{
                    String deviceId = methodType.get("name").get(0);
                    result = OTDeviceDAO.getInstance().del(deviceId);
                }catch(Exception e){
                }
            }else{
                return Response.status(Response.Status.BAD_REQUEST).entity("false").build();
            }
        }else{
            result = OTDeviceDAO.getInstance().add("",content);
        }
        return Response.status(result.getStatusCode()).entity(result.getContent()).build();
    }

    @Path (PATH_domainId+"/devices")
    @POST
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postDeviceOfDomain(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath, @PathParam(domainid) String domainId,String content) {
        logger.error("POST:{}{},body={}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters(),content});
        RetRpc result = new RetRpc();

        result = OTDeviceDAO.getInstance().add(domainId,content);
        return Response.status(result.getStatusCode()).entity(result.getContent()).build();
    }

    @Path ("/devices/"+PATH_DEVICEID)
    @DELETE
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response delDevice(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath,@PathParam(DEVICEID) String deviceid,String content) {
        logger.error("DELETE:{}{}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters()});
        //        if(null == uriInfo.getQueryParameters()){
        //            return Response.status(Response.Status.BAD_REQUEST).entity(false).build();
        //        }

        RetRpc result = OTDeviceDAO.getInstance().del(deviceid);
        return Response.status(result.getStatusCode()).entity(result.getContent()).build();
    }

    @Path ("/devices/"+PATH_DEVICEID)
    @POST
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postDelDevice(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath,@PathParam(DEVICEID) String deviceid,String content) {
        logger.error("DELETE:{}{}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters()});
        RetRpc result = new RetRpc();
        MultivaluedMap<String, String> methodType = uriInfo.getQueryParameters();
        if(null != methodType.get("methodType")){
            if("DELETE".equals(methodType.get("methodType").get(0))){
                result = OTDeviceDAO.getInstance().del(deviceid);
            }else{
                return Response.status(Response.Status.BAD_REQUEST).entity("false").build();
            }
        }
        return Response.status(result.getStatusCode()).entity(result.getContent()).build();
    }

    @Path (PATH_domainId+"/" + PATH_RESTAPI )
    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDomainApi(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath, @PathParam(domainid) String domainId,@PathParam(DEVICEID) String deviceid) {
        logger.error("GET:{}{}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters()});

        return control(domainId, "", apiPath,uriInfo.getQueryParameters(), MyData.RestType.GET,"");
    }

    @Path (PATH_domainId+"/" + PATH_RESTAPI )
    @PUT
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response putDomainApi(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath, @PathParam(domainid) String domainId,@PathParam(DEVICEID) String deviceid,String content) {
        logger.error("PUT:{}{},body={}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters(),content});
        return control(domainId, "", apiPath,uriInfo.getQueryParameters(), MyData.RestType.PUT,content);
    }

    @Path (PATH_domainId+"/" + PATH_RESTAPI )
    @POST
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postDomainApi(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath, @PathParam(domainid) String domainId,@PathParam(DEVICEID) String deviceid,String content) {
        MultivaluedMap<String, String> methodType = uriInfo.getQueryParameters();
        RestType restType = MyData.RestType.POST;
        if(null != methodType.get("methodType") && null != methodType.get("methodType").get(0) && !"".equals(methodType.get("methodType").get(0))){
            try{
                restType = MyData.RestType.valueOf(methodType.get("methodType").get(0));
            }catch (Exception e) {
            }
        }
        logger.error("POST:{}{},body={}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters(),content});
        return control(domainId, "", apiPath,uriInfo.getQueryParameters(), restType,content);
    }

    @Path (PATH_domainId+"/" + PATH_RESTAPI )
    @DELETE
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response delDomainApi(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath, @PathParam(domainid) String domainId,@PathParam(DEVICEID) String deviceid,String content) {
        logger.error("DELETE:{}{}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters()});
        if(null == uriInfo.getQueryParameters()){
            return Response.status(Response.Status.BAD_REQUEST).entity(false).build();
        }

        return control(domainId, "", apiPath,uriInfo.getQueryParameters(), MyData.RestType.DELETE,content);
    }


    //其它操作//

    @Path (PATH_domainId+"/devices/"+PATH_DEVICEID+ "/" + PATH_RESTAPI )
    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getSchemaApi(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath, @PathParam(domainid) String domainId,@PathParam(DEVICEID) String deviceid) {
        logger.error("GET:{}{}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters()});

        return control(domainId, deviceid, apiPath,uriInfo.getQueryParameters(), MyData.RestType.GET,"");
    }

    @Path (PATH_domainId+"/devices/"+PATH_DEVICEID+ "/" + PATH_RESTAPI )
    @PUT
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response putSchemaApi(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath, @PathParam(domainid) String domainId,@PathParam(DEVICEID) String deviceid,String content) {
        logger.error("PUT:{}{},body={}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters(),content});
        return control(domainId, deviceid, apiPath,uriInfo.getQueryParameters(), MyData.RestType.PUT,content);
    }

    @Path (PATH_domainId+"/devices/"+PATH_DEVICEID+ "/" + PATH_RESTAPI )
    @POST
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postSchemaApi(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath, @PathParam(domainid) String domainId,@PathParam(DEVICEID) String deviceid,String content) {
        logger.error("POST:{}{},body={}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters(),content});
        MultivaluedMap<String, String> methodType = uriInfo.getQueryParameters();
        RestType restType = MyData.RestType.POST;
        if(null != methodType.get("methodType") && null != methodType.get("methodType").get(0) && !"".equals(methodType.get("methodType").get(0))){
            try{
                restType = MyData.RestType.valueOf(methodType.get("methodType").get(0));
            }catch (Exception e) {
            }
        }
        return control(domainId, deviceid, apiPath,uriInfo.getQueryParameters(), restType,content);
    }

    @Path (PATH_domainId+"/devices/"+PATH_DEVICEID+ "/" + PATH_RESTAPI )
    @DELETE
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response delSchemaApi(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath, @PathParam(domainid) String domainId,@PathParam(DEVICEID) String deviceid,String content) {
        logger.error("DELETE:{}{}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters()});
        if(null == uriInfo.getQueryParameters()){
            return Response.status(Response.Status.BAD_REQUEST).entity(false).build();
        }

        return control(domainId, deviceid, apiPath,uriInfo.getQueryParameters(), MyData.RestType.DELETE,content);
    }

    private Response control(String domainId, String deviceid, String apiPath,MultivaluedMap<String, String> map, RestType restType,String content){
        RetRpc result = new RetRpc();
        String key = "";
        if("ifms".equals(apiPath)){
            key = (null ==map.get("type")?"":map.get("type").get(0));
        }else if("vlanmappings".equals(apiPath)){
            key = (null ==map.get("interfaceName")?"":map.get("interfaceName").get(0));
        }else if("vlanmappings1To1".equals(apiPath)){
            key = (null ==map.get("interfaceName")?"":map.get("interfaceName").get(0));
        }else if("tpath".equals(apiPath)){
            apiPath = "tunnels"; 
            key = (null ==map.get("name")?"":map.get("name").get(0));
            restType = MyData.RestType.GETOTHER;
        }/*else if("nqas".equals(apiPath)){
            result.setContent("<nqa><data><schedule>10</schedule><value>10</value></data><data><schedule>11</schedule><value>11</value></data><data><schedule>12</schedule><value>12</value></data><data><schedule>13</schedule><value>13</value></data><data><schedule>14</schedule><value>14</value></data><data><schedule>15</schedule><value>15</value></data><data><schedule>16</schedule><value>16</value></data></nqa>");
            return  Response.status(result.getStatusCode()).entity(result.getContent()).build();

        }*/else{
            key = (null ==map.get("name")?"":map.get("name").get(0));
        }

        IOTDao dao = OTDaoFactory.getRestClient(apiPath);
        if (null == dao){
            result.setStatusCode(404);
            result.setContent("The server not found!");
        }else{
            result = dao.control(domainId, deviceid, key, restType,content);
        }
        return Response.status(result.getStatusCode()).entity(result.getContent()).build();
    }





    /**
     * 
     * 新加 接受报警的服务，访问该连接启动一个客户端WebSocketOtherClient
     * 专门用来接受报警类别为linkdown的报警
     * 推送报警服务地址为10.111.92.248（配置文件my.ini）
     * 推送报警服务端口号为9038
     * AlarmServerIp=10.111.92.248
     * AlarmServerPort=9038
     * 
     * */
    @Path ("/alarm" )
    @POST
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postOterAlarm(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo,String content) {
        try {
            //该连接表示该客户端只是接受linkdown告警信息
            String uri ="ws://"+MyData.getAlarmServerIp()+":"+MyData.getAlarmServerPort()+"/alarm/other";
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
    
    

    
    //注册
    @Path ("/regist")
    @POST
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response regist(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo,String content) {
        //System.out.println("进入注册。。。。。。。。。。");
        RetRpc result = new RetRpc();
        OTRegistDao dao = OTRegistDao.getInstance();
        result = dao.regist(content);
        return Response.status(result.getStatusCode()).entity(result.getContent()).build();
    }
    
    
    
    //用户管理
    @Path ("/userManage" )
    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response userManage(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo,String content) {
        //System.out.println("用户列表。。。。。。。。。。");
        RetRpc result = new RetRpc();
        String message = "";
        OTRegistDao dao = OTRegistDao.getInstance();
        message = dao.getAllUserInfoToFlex();
        result.setStatusCode(200);
        result.setContent(message);
        return Response.status(result.getStatusCode()).entity(result.getContent()).build();
    }
    
    
    //删除用户
    @Path ("/userManage" )
    @DELETE
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response deleteUser(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo,String content) {
        //System.out.println("用户列表。。。。。。。。。。");
        RetRpc result = new RetRpc();
        MultivaluedMap<String, String> map = uriInfo.getQueryParameters();
        String userId = map.getFirst("userId");
        OTRegistDao dao = OTRegistDao.getInstance();
        boolean flag = dao.deleteUserInfo(userId);
        String message = "";
        if(flag==true){
            message ="<delteUser>success</delteUser>";
        }else{
            message ="<delteUser>fail</delteUser>";
        }
        result.setStatusCode(200);
        result.setContent(message);
        return Response.status(result.getStatusCode()).entity(result.getContent()).build();
    }
    

    
    //修改密码
    @Path ("/modifyPw" )
    @POST
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response modifyPw(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo,String content) {
        //System.out.println("用户列表。。。。。。。。。。");
        RetRpc result = new RetRpc();
        OTLoginDao loginDao = OTLoginDao.getInstance();
        result = loginDao.modifyPw(content);
        return Response.status(result.getStatusCode()).entity(result.getContent()).build();
    }
    
    
    
    //登陆
    @Path ("/login" )
    @POST
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response login(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo,String content) {
        //System.out.println("进入登陆。。。。。。。。。。");
        RetRpc result = new RetRpc();
        OTLoginDao loginDao = OTLoginDao.getInstance();
        result = loginDao.login(content);
        return Response.status(result.getStatusCode()).entity(result.getContent()).build();
    }
    
    
    
    
    
    //验证码图片
    @Path ("/verifyCode" )
    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response verifyCodePic(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo,String content) {
        //System.out.println("进入生成验证码。。。。。。。。。。");
        MultivaluedMap<String, String> map = uriInfo.getQueryParameters();
        String clientBs = map.getFirst("clientBs");
        //System.out.println(clientBs);
        int w = 200, h = 80;
        //生成验证码
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        MyData.getClientVerifyCode().put(clientBs,verifyCode);
        byte[] code = null;
        try {
            //生成验证码图片，转换成字节
            code = VerifyCodeUtils.outputImage(w, h,verifyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Response.ok(MediaType.APPLICATION_OCTET_STREAM).entity(code).build();
    }
    
    
    
    /**
     * 初次登陆出现错误，先请求此地址
     * @param linksBuilders
     * @param uriInfo
     * @param content
     * @return
     */
    @Path ("/loginStatus" )
    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response loginStatus(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo,String content) {
    	return Response.status(200).entity("loading").build();
    }
    
}
