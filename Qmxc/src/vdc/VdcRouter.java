package vdc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.BodyType;
import util.OpsRestCaller;
import util.OpsServiceManager;
import util.OpsXMLHelper;
import util.RestUtil;
import util.RetRpc;


public class VdcRouter {

    public final static String urlBody = "/vdc/routers/router";
    public final static String fullUrlbody = "/vdc/routers/router";
    public final static String S_ROUTERNAME ="routerName";
    public final static String S_ROUTERID ="routerID";
    public final static String S_TENANTID ="tenantID";
    public final static String S_OPERSTATUS ="operStatus";
    public final static String S_ADMINSTATUS ="adminStatus";
    public final static String S_EXTERNALNETWORKID ="externalNetworkID";

    private Boolean readAccess = true;
    private OpsRestCaller rest = null;
	private static Map<String, String> criterions = null;
    private String errorstr = "";
	private String content = "";
    private int indexint = 0;
	private OneBody body = new OneBody();
	private List<OneBody> multiBody = new ArrayList<OneBody>();
	private Boolean bMultiOperate = false;
	private BodyType bodyType = BodyType.APPLICATION_XML;
	private Map<String, JSONObject> criterionsBuffer = new LinkedHashMap<String, JSONObject>();
	

    static {
	    criterions = new HashMap<String, String>();
        criterions.put("routerName", "{'data-type':'STRING','min':'1','max':'255','example':'string','access':'readCreate'}");        
        criterions.put("routerID", "{'data-type':'STRING','min':'1','max':'36','example':'string','key':'true','access':'readCreate'}");        
        criterions.put("tenantID", "{'data-type':'STRING','min':'1','max':'36','example':'string','access':'readCreate'}");        
        criterions.put("operStatus", "{'data-type':'STRING','min':'1','max':'16','example':'string','access':'readCreate'}");        
        criterions.put("adminStatus", "{'data-type':'UINT32','min-val':'0','max-val':'4294967295','example':'7','access':'readCreate'}");        
        criterions.put("externalNetworkID", "{'data-type':'STRING','min':'1','max':'36','example':'string','access':'readCreate'}");        
	}
    
    public class OneBody{
    
        private String routerName ="";
        private String routerID ="";
        private String tenantID ="";
        private String operStatus ="";
        private String adminStatus ="";
        private String externalNetworkID ="";

        public void setRouterName(String routerName){
    	
           if(RestUtil.validata(getCriterion(S_ROUTERNAME), routerName))
           {
               this.routerName = routerName;
               if(!checkAccessRead(S_ROUTERNAME))
               {
                   readAccess = false;
                   System.out.println("set routerName success");
               }
           }else{
               System.out.println( "routerName validation failed, please check again");
           }
        }
    	
    	public String getRouterName()
        {
            return this.routerName;
        }
        public void setRouterID(String routerID){
    	
           if(RestUtil.validata(getCriterion(S_ROUTERID), routerID))
           {
               this.routerID = routerID;
               if(!checkAccessRead(S_ROUTERID))
               {
                   readAccess = false;
                   System.out.println("set routerID success");
               }
           }else{
               System.out.println( "routerID validation failed, please check again");
           }
        }
    	
    	public String getRouterID()
        {
            return this.routerID;
        }
        public void setTenantID(String tenantID){
    	
           if(RestUtil.validata(getCriterion(S_TENANTID), tenantID))
           {
               this.tenantID = tenantID;
               if(!checkAccessRead(S_TENANTID))
               {
                   readAccess = false;
                   System.out.println("set tenantID success");
               }
           }else{
               System.out.println( "tenantID validation failed, please check again");
           }
        }
    	
    	public String getTenantID()
        {
            return this.tenantID;
        }
        public void setOperStatus(String operStatus){
    	
           if(RestUtil.validata(getCriterion(S_OPERSTATUS), operStatus))
           {
               this.operStatus = operStatus;
               if(!checkAccessRead(S_OPERSTATUS))
               {
                   readAccess = false;
                   System.out.println("set operStatus success");
               }
           }else{
               System.out.println( "operStatus validation failed, please check again");
           }
        }
    	
    	public String getOperStatus()
        {
            return this.operStatus;
        }
        public void setAdminStatus(String adminStatus){
    	
           if(RestUtil.validata(getCriterion(S_ADMINSTATUS), adminStatus))
           {
               this.adminStatus = adminStatus;
               if(!checkAccessRead(S_ADMINSTATUS))
               {
                   readAccess = false;
                   System.out.println("set adminStatus success");
               }
           }else{
               System.out.println( "adminStatus validation failed, please check again");
           }
        }
    	
    	public String getAdminStatus()
        {
            return this.adminStatus;
        }
        public void setExternalNetworkID(String externalNetworkID){
    	
           if(RestUtil.validata(getCriterion(S_EXTERNALNETWORKID), externalNetworkID))
           {
               this.externalNetworkID = externalNetworkID;
               if(!checkAccessRead(S_EXTERNALNETWORKID))
               {
                   readAccess = false;
                   System.out.println("set externalNetworkID success");
               }
           }else{
               System.out.println( "externalNetworkID validation failed, please check again");
           }
        }
    	
    	public String getExternalNetworkID()
        {
            return this.externalNetworkID;
        }
        public void clear()
		{
		
            this.routerName="";
            this.routerID="";
            this.tenantID="";
            this.operStatus="";
            this.adminStatus="";
            this.externalNetworkID="";
		}
		
		public OneBody clone()
		{
		    OneBody cloneBody= new OneBody();
            cloneBody.setRouterName(this.routerName);
            cloneBody.setRouterID(this.routerID);
            cloneBody.setTenantID(this.tenantID);
            cloneBody.setOperStatus(this.operStatus);
            cloneBody.setAdminStatus(this.adminStatus);
            cloneBody.setExternalNetworkID(this.externalNetworkID);
            return cloneBody;
		}
		
		public String toString()
		{  
		     return toString(BodyType.APPLICATION_JSON, false);
		}
		
		public String toString(BodyType bodyType, boolean isWrite)
		{  
		    StringBuffer buf = new StringBuffer();
			if (bodyType == BodyType.APPLICATION_JSON)
			{
                if(!isWrite || checkAccessWrite(S_ROUTERNAME))
				buf.append("{\"routerName\": \"").append(this.routerName).append("\"}");
                if(!isWrite || checkAccessWrite(S_ROUTERID))
	            buf.append(",{\"routerID\": \"").append(this.routerID).append("\"}");
                if(!isWrite || checkAccessWrite(S_TENANTID))
	            buf.append(",{\"tenantID\": \"").append(this.tenantID).append("\"}");
                if(!isWrite || checkAccessWrite(S_OPERSTATUS))
	            buf.append(",{\"operStatus\": \"").append(this.operStatus).append("\"}");
                if(!isWrite || checkAccessWrite(S_ADMINSTATUS))
	            buf.append(",{\"adminStatus\": \"").append(this.adminStatus).append("\"}");
                if(!isWrite || checkAccessWrite(S_EXTERNALNETWORKID))
	            buf.append(",{\"externalNetworkID\": \"").append(this.externalNetworkID).append("\"}");
			}
			 
		    if (bodyType == BodyType.APPLICATION_XML)
		    {
		    	if (!isWrite || (RestUtil.isNotEmpty(this.routerName)) && checkAccessWrite("routerName"))
		    	{ buf.append("<routerName>").append(this.routerName).append("</routerName>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.routerID)) && checkAccessWrite("routerID"))
		    	{ buf.append("<routerID>").append(this.routerID).append("</routerID>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.tenantID)) && checkAccessWrite("tenantID"))
		    	{ buf.append("<tenantID>").append(this.tenantID).append("</tenantID>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.operStatus)) && checkAccessWrite("operStatus"))
		    	{ buf.append("<operStatus>").append(this.operStatus).append("</operStatus>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.adminStatus)) && checkAccessWrite("adminStatus"))
		    	{ buf.append("<adminStatus>").append(this.adminStatus).append("</adminStatus>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.externalNetworkID)) && checkAccessWrite("externalNetworkID"))
		    	{ buf.append("<externalNetworkID>").append(this.externalNetworkID).append("</externalNetworkID>\n"); }
		    }
			 
		    return buf.toString();
		}
				
		public boolean validate()
		{
		
		     if (checkIsKey(S_ROUTERNAME) && RestUtil.isEmpty(this.routerName)) return false;
		     if (checkIsKey(S_ROUTERID) && RestUtil.isEmpty(this.routerID)) return false;
		     if (checkIsKey(S_TENANTID) && RestUtil.isEmpty(this.tenantID)) return false;
		     if (checkIsKey(S_OPERSTATUS) && RestUtil.isEmpty(this.operStatus)) return false;
		     if (checkIsKey(S_ADMINSTATUS) && RestUtil.isEmpty(this.adminStatus)) return false;
		     if (checkIsKey(S_EXTERNALNETWORKID) && RestUtil.isEmpty(this.externalNetworkID)) return false;
             return true;
		}
		
    }
    
    public Boolean getbMultiOperate() {
		return bMultiOperate;
	}

	public void setbMultiOperate(Boolean bMultiOperate) {
		this.bMultiOperate = bMultiOperate;
	}

	public BodyType getBodyType() {
		return bodyType;
	}

	public void setBodyType(BodyType bodyType) {
		this.bodyType = bodyType;
	}
	
    public VdcRouter(OpsRestCaller rest) {
        this.rest = rest;
    }
    
    public OpsRestCaller getRest() {
        return rest;
    }

    public void setRest(OpsRestCaller rest) {
        this.rest = rest;
    }
  
    private String getContent() {
        return body.toString();
    }

    private int getIndexint() {
        return indexint;
    }

    private void setIndexint(int index) {
        this.indexint = index;
    }
 

    public String getErrorstr() {
        return errorstr;
    }

    public void setErrorstr(String errorstr) {
        this.errorstr = errorstr;
    }

    private Map<String, String> getCriterions() {
        return criterions;
    }
  
 
    public String getUrlBody() {
     
           return this.fullUrlbody;
    } 
	
    public void parseRestful(RetRpc restfulJson) throws Exception
    {
    
       this.setIndexint(0);
        this.multiBody.clear();
        if(null == restfulJson)
        {
          throw new Exception(restfulJson+" error in VdcRouter.java");
        }
        if(200==restfulJson.getStatusCode())
        {
        	if (BodyType.APPLICATION_JSON == bodyType)
        	{
        		OpsXMLHelper xmlHelper = new OpsXMLHelper();
        		JSONObject jsonObject = JSONObject.fromObject(restfulJson.getContent());
        		xmlHelper.configure(jsonObject, fullUrlbody, this, VdcRouter.OneBody.class);
        		
        	} else {
        		Document doc = RestUtil.getDoc(restfulJson.getContent());
        		OpsXMLHelper xmlHelper = new OpsXMLHelper();
        		xmlHelper.configure(doc.getDocumentElement(), fullUrlbody, this, VdcRouter.OneBody.class);
        	}
        }
        else
        {
            throw new Exception(restfulJson.getContent()+",in IfmInterface.java");  
        }
 
    }
    
    public void reset()
    {
    	indexint = 0;
    }
    
    public void next()
    {
        body = null;
    	if (indexint >= 0 && indexint < multiBody.size())
    	{
    		body = multiBody.get(indexint);
    		indexint++;
    	}
    }

    public boolean hasNext()
    {
        return (getIndexint() < multiBody.size() && getIndexint() >= 0);
    }
    
    private void setAttributes(JSONArray attributeArray)
    {
	    
        Iterator iter = attributeArray.iterator();
        while(iter.hasNext())
        {
            JSONObject json = (JSONObject) iter.next();
			
	         
            if(json.containsKey(S_ROUTERNAME))
            {body.setRouterName(json.getString(S_ROUTERNAME));}
 	         
            if(json.containsKey(S_ROUTERID))
            {body.setRouterID(json.getString(S_ROUTERID));}
 	         
            if(json.containsKey(S_TENANTID))
            {body.setTenantID(json.getString(S_TENANTID));}
 	         
            if(json.containsKey(S_OPERSTATUS))
            {body.setOperStatus(json.getString(S_OPERSTATUS));}
 	         
            if(json.containsKey(S_ADMINSTATUS))
            {body.setAdminStatus(json.getString(S_ADMINSTATUS));}
 	         
            if(json.containsKey(S_EXTERNALNETWORKID))
            {body.setExternalNetworkID(json.getString(S_EXTERNALNETWORKID));}
         }
     }
     
    public void clearAll()
    {
         body.clear();
         multiBody.clear();
         this.readAccess=true; 
    }
	
    public boolean addBody(OneBody myBody)
    {
         if( myBody == null)
         {
             return false;
         }
		 
         if (!myBody.validate())
         {
        	 return false;
         }
		 
		 multiBody.add(myBody);
		 return true;
    }
	
	public boolean setBody(OneBody myBody)
    {
         if( myBody == null)
         {
             return false;
         }
		 
         if (!myBody.validate())
         {
        	 return false;
         }
         this.body = myBody;
		 return true;
    }
  
    public boolean setBody(JSONArray array)
    {
         if( array == null)
         {
             return false;
         }
          
         List<JSONObject > lstJson = new ArrayList<JSONObject>();
         int icount = array.size();
         for(int i  =0; i <  icount; i ++ )
	     {
	        JSONObject object =  array.getJSONObject(i);
            if (object.get(S_ROUTERNAME) != null)
	    	{
	    	    body.setRouterName(object.getString(S_ROUTERNAME));
	    	}
            if (object.get(S_ROUTERID) != null)
	    	{
	    	    body.setRouterID(object.getString(S_ROUTERID));
	    	}
            if (object.get(S_TENANTID) != null)
	    	{
	    	    body.setTenantID(object.getString(S_TENANTID));
	    	}
            if (object.get(S_OPERSTATUS) != null)
	    	{
	    	    body.setOperStatus(object.getString(S_OPERSTATUS));
	    	}
            if (object.get(S_ADMINSTATUS) != null)
	    	{
	    	    body.setAdminStatus(object.getString(S_ADMINSTATUS));
	    	}
            if (object.get(S_EXTERNALNETWORKID) != null)
	    	{
	    	    body.setExternalNetworkID(object.getString(S_EXTERNALNETWORKID));
	    	}
 
         }
		 
		 return this.body.validate();
    }
    
    public boolean setBodyFromJson(String content)
    {
         JSONArray array = new JSONArray();
         if (null != content && content.length() > 0)   
         { 
             array = JSONArray.fromObject(content);
         }
         return setBody(array);
    }
	

    
	/**
     * modify api, equal to method post of rest
     * @param attributes : URL parameters, for example "?Xxx=yy&aa=b"
     * @return get : url 
     * @throws Exception
     */
    public boolean modify() throws Exception
    {
        if (!validateBody()) {
            return false;
        }
        
        String urlPath = getUrl();
        RetRpc response = this.getRest().put(urlPath, getParameBody());
        int status = response.getStatusCode();
        if (status == 200) {
            return true;
        }
        if (status != 200)
            this.setErrorstr(response.toString());
        return false;
    }

	/**
     * create api, equal to method post of rest
     * @param attributes : URL parameters, for example "?Xxx=yy&aa=b"
     * @return get : url 
     * @throws Exception
     */
    public boolean create() throws Exception
    {
        if (!validateBody()) {
            return false;
        }
        
        String urlPath = getUrl();
        RetRpc response = this.getRest().post(urlPath, getParameBody());
        int status = response.getStatusCode();
        if (status == 200) {
            return true;
        }
        if (status != 200)
            this.setErrorstr(response.toString());
        return false;
    }
	
	/**
     * get api, equal to method get of rest
     * @param attributes : URL parameters, for example "?Xxx=yy&aa=b"
     * @return get : url 
     * @throws Exception
     */
    public boolean get() throws Exception
    {
        String urlPath = getUrl();
        RetRpc  response = this.getRest().get(urlPath);
        int status = response.getStatusCode();
        if (status == 200) {
            return true;
        }
        if (status != 200)
            this.setErrorstr(response.toString());
        return false;
    }

	/**
     * delete api, equal to method delete of rest
     * @param attributes : URL parameters, for example "?Xxx=yy&aa=b"
     * @return get : url 
     * @throws Exception
     */
    public boolean delete() throws Exception
    {
        String urlPath = getUrl();
        RetRpc response = this.getRest().delete(urlPath);
        int status = response.getStatusCode();
        if (status == 200) {
            return true;
        }
        if (status != 200)
            this.setErrorstr(response.toString());
        return false;
    }
	
	/**
     * getall api, equal to method get of rest
     * @param attributes : URL parameters, for example "?Xxx=yy&aa=b"
     * @return get : url 
     * @throws Exception
     */
    public RetRpc getall() throws Exception
    {
     
        if(this.getAllAccessWrite())
        {
            String urlPath = getUrl();
            RetRpc response = this.getRest().get(urlPath);
            return response; 
        }else
        {
            throw new Exception("You do not have permmition");
        }
    }
    
    public boolean validateBody()
    {
    	if (bMultiOperate)
    	{
    		if (multiBody.size() == 0)
    		{
    		    errorstr = "Error : case Multi-body much be have one body , please call addbody().";
    			return false;
    		}
    		
    		for (OneBody body : multiBody)
    		{
    			if (body != null && !body.validate())
    			{
    			    errorstr = "Error : case Multi-body, one body validate failed, please check it.";
    				return false;
    			}
    		}
    	}
    	
    	boolean ret = body.validate();
    	if(!ret)
    	{
    	     errorstr = "Error : case one-body, body validate failed, please check it.";
    		 return false;
    	}
    	return ret;
    }
    
	private JSONObject getCriterion(String name)
    {
    	JSONObject criterion = criterionsBuffer.get(name);
        if (null == criterion)
        {
        	if (null == criterions.get(name))
        	{
        		return null;
        	}
        	
        	criterion = JSONObject.fromObject(criterions.get(name));
        	if (criterion != null)
        	{
        		criterionsBuffer.put(name, criterion);
        	}
        }
        
        return criterion;
    }
    
    private Boolean checkIsKey(String name)
    {
    	JSONObject criterion = getCriterion(name);
        if (criterion != null && criterion.containsKey("key") && "true".equals(criterion.get("key")))
        {
            return true;
        }
        return false;
    }
	
    private Boolean checkAccessWrite(String name)
    {
    	JSONObject criterion = getCriterion(name);
        if (criterion != null && criterion.containsKey("access") && ("writeOnly".equals(criterion.get("access"))
                || "readCreate".equals(criterion.get("access")) 
                || "readWrite".equals(criterion.get("access"))))
        {
            return true;
        }
        return false;
    }

    private boolean checkAccessRead(String name)
    {

    	JSONObject criterion = getCriterion(name);
        if (criterion != null && criterion.containsKey("access") && ("readOnly".equals(criterion.get("access"))
                || "readCreate".equals(criterion.get("access"))  
                 || "readWrite".equals(criterion.get("access"))  ))
        {
            return true;
        }
        return false;
    }
    
    private Boolean getAllAccessWrite()
    {
         this.readAccess = (this.readAccess && this.checkAccessRead(S_ROUTERNAME));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_ROUTERID));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_TENANTID));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_OPERSTATUS));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_ADMINSTATUS));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_EXTERNALNETWORKID));
         return this.readAccess;
    } 
    
    /**
     * parse str to list
     * @param str
     * @return
     */   
    private List<String> strToList(String str)
    {
        List<String> resultList = new ArrayList<String>();
        String[] strArr = str.split("]");
        for (String s : strArr) {
            if (s.startsWith("['"))
            {
                resultList.add(s.substring(s.indexOf("['") + 2, s.length() - 1));
            }
            else if (s.startsWith("["))
            {
                resultList.add(s.substring(s.indexOf("[") + 1, s.length()));
            }
        }
        return resultList;
    }
 
	
    /**
    * parse para for body
    * @return
    */
    private String getParameBody() {
    
        String urlPath = getUrlBody();
	    StringBuffer buf = new StringBuffer();
	    String bodyPrefix = RestUtil.getBodyPrefix(this.fullUrlbody, bMultiOperate, this.bodyType);
	    String bodysuffix = RestUtil.getBodySuffix(this.fullUrlbody, bMultiOperate, this.bodyType);
	    String prefix = RestUtil.getPrefix(urlPath, bMultiOperate, this.bodyType);
	    String sufffix = RestUtil.getSuffix(urlPath, bMultiOperate, this.bodyType);
	    
		// multi-body 
	    if (bMultiOperate)
		{
		    buf.append(prefix);
		    for(OneBody myBody : multiBody)
			{
			    buf.append(bodyPrefix);
			    buf.append(myBody.toString(this.bodyType, true));
				buf.append(bodysuffix);
			}
			buf.append(sufffix);
			return buf.toString();
		}
		
		// one body 
		buf.append(prefix);
		buf.append(bodyPrefix);
		buf.append(body.toString(this.bodyType, true));
		buf.append(bodysuffix);
		buf.append(sufffix);
		return buf.toString();
    }
	
     /**
     * 
     * @param attributes para of restful url
     * @return get url 
     * @throws Exception
     */
    private String getUrl() throws Exception {
		
        String urlPath = getUrlBody();

		// case mulit-body 
    	if(bMultiOperate)
    	{
    		int argIndex = urlPath.indexOf("?");
    		if (argIndex == -1){
    			int lastUrlPath = urlPath.lastIndexOf("/");
    			if (lastUrlPath != -1)
    			{
    				urlPath = urlPath.substring(0, lastUrlPath);
    			}
    		} else {
    			urlPath = urlPath.substring(0, argIndex);
    		}
    	}
		
    	// if body application/type is json
    	if (BodyType.APPLICATION_JSON == bodyType)
    	{
    		urlPath = urlPath + "/json";
    	}
    	
    	return urlPath;
    }
     
    public OneBody getBody() {
		return body;
	}

	public List<OneBody> getMultiBody() {
		return multiBody;
	}

	//main test
    public static void main(String[] args) 
	{
        VdcRouter newond = new VdcRouter(OpsServiceManager.getInstance().getDefaultOpsRestCall());
        try {
		   System.out.println(newond.getall());
        } catch (Exception e) 
        {
	       System.out.println(e.getMessage());
        }
    }
}  
    