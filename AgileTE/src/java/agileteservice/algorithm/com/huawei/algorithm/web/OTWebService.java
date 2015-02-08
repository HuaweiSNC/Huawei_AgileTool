package com.huawei.algorithm.web;


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

import com.huawei.agilete.base.common.MyIO;
import com.huawei.algorithm.controller.BeiAlgorithmController;
import com.huawei.networkos.ops.response.RetRpc;

@Workspace(workspaceTitle = "Agile TE 0.1", collectionTitle = "Agile TE Server")
@Path("algorithmManage")
public class OTWebService {

    private static final String RESTAPI = "restapipath";

    private static final String algorithmId = "algorithmId";
    private static final String PATH_algorithmId = "{algorithmId}";
    private static final Logger logger      = LoggerFactory.getLogger(OTWebService.class);

    BeiAlgorithmController beiAlgorithmController = new BeiAlgorithmController();


    //管理域操作//
    /**
     * getall管理域
     * 
     */

    @Path (PATH_algorithmId)
    @PUT
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response putDomain(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath,  @PathParam(algorithmId) String domainId,String content) {
        logger.error("PUT:{}{},body={}",new Object[]{uriInfo.getPath(),uriInfo.getQueryParameters(),content});
        
        RetRpc result = new RetRpc();
//        if(domainId.equals("ceshi")){
            result = beiAlgorithmController.getBeiAlgorithm(content);
//        }
//        result.setStatusCode(200);
//        result.setContent("<paths><path><Source>51</Source><Destination>52</Destination><wrong></wrong><result><Primary name=\"Primary\"><node>51</node><node>52</node></Primary><Backup name=\"Backup\"><node>51</node><node>53</node><node>52</node></Backup></result></path></paths>");//OTDomainDAO.getInstance().edit(content);
        return Response.status(result.getStatusCode()).entity(result.getContent()).build();
    }

    @Path (PATH_algorithmId)
    @POST
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response posyDomain(@Context LinkBuilders linksBuilders, @Context UriInfo uriInfo, @PathParam(RESTAPI) String apiPath,  @PathParam(algorithmId) String domainId,String content) {
        RetRpc result = new RetRpc();
        MultivaluedMap<String, String> methodType = uriInfo.getQueryParameters();
//        RestType restType = MyData.RestType.POST;
        if(null != methodType.get("methodType") && null != methodType.get("methodType").get(0) && "PUT".equals(methodType.get("methodType").get(0))){
            try{
                return putDomain(linksBuilders, uriInfo, apiPath, domainId, content);
            }catch (Exception e) {
            }
        }else{
            result = beiAlgorithmController.getBeiAlgorithm(MyIO.characterFormat(content));
        }
        return Response.status(result.getStatusCode()).entity(result.getContent()).build();

    }


}
