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


public class VdcTnlEndPoint {

    public final static String urlBody = "/vdc/tnlEndPoints/tnlEndPoint";
    public final static String fullUrlbody = "/vdc/tnlEndPoints/tnlEndPoint";
    public final static String S_ID ="id";
    public final static String S_HOST ="host";
    public final static String S_VNIMAPPINGTYPE ="vniMappingType";
    public final static String S_TYPE ="type";
    public final static String S_ADMINIP ="adminIp";
    public final static String S_IPADDRESS ="ipAddress";

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
        criterions.put("id", "{'data-type':'STRING','min':'1','max':'36','example':'string','key':'true','access':'readCreate'}");        
        criterions.put("host", "{'data-type':'STRING','min':'1','max':'255','example':'string','access':'readCreate'}");        
        criterions.put("vniMappingType", "{'data-type':'UINT32','min-val':'0','max-val':'1','example':'1','access':'readCreate'}");        
        criterions.put("type", "{'data-type':'ENUM','expected-values':'OVS;TOR;L3GW','example':'OVS','access':'readCreate'}");        
        criterions.put("adminIp", "{'data-type':'IPV4','min':'0','max':'255','example':'127.0.0.1','access':'readCreate'}");        
        criterions.put("ipAddress", "{'data-type':'IPV4','min':'0','max':'255','example':'127.0.0.1','access':'readCreate'}");        
	}
    
    public class OneBody{
    
        private String id ="";
        private String host ="";
        private String vniMappingType ="";
        private String type ="";
        private String adminIp ="";
        private String ipAddress ="";

        public void setId(String id){
    	
           if(RestUtil.validata(getCriterion(S_ID), id))
           {
               this.id = id;
               if(!checkAccessRead(S_ID))
               {
                   readAccess = false;
                   System.out.println("set id success");
               }
           }else{
               System.out.println( "id validation failed, please check again");
           }
        }
    	
    	public String getId()
        {
            return this.id;
        }
        public void setHost(String host){
    	
           if(RestUtil.validata(getCriterion(S_HOST), host))
           {
               this.host = host;
               if(!checkAccessRead(S_HOST))
               {
                   readAccess = false;
                   System.out.println("set host success");
               }
           }else{
               System.out.println( "host validation failed, please check again");
           }
        }
    	
    	public String getHost()
        {
            return this.host;
        }
        public void setVniMappingType(String vniMappingType){
    	
           if(RestUtil.validata(getCriterion(S_VNIMAPPINGTYPE), vniMappingType))
           {
               this.vniMappingType = vniMappingType;
               if(!checkAccessRead(S_VNIMAPPINGTYPE))
               {
                   readAccess = false;
                   System.out.println("set vniMappingType success");
               }
           }else{
               System.out.println( "vniMappingType validation failed, please check again");
           }
        }
    	
    	public String getVniMappingType()
        {
            return this.vniMappingType;
        }
        public void setType(String type){
    	
           if(RestUtil.validata(getCriterion(S_TYPE), type))
           {
               this.type = type;
               if(!checkAccessRead(S_TYPE))
               {
                   readAccess = false;
                   System.out.println("set type success");
               }
           }else{
               System.out.println( "type validation failed, please check again");
           }
        }
    	
    	public String getType()
        {
            return this.type;
        }
        public void setAdminIp(String adminIp){
    	
           if(RestUtil.validata(getCriterion(S_ADMINIP), adminIp))
           {
               this.adminIp = adminIp;
               if(!checkAccessRead(S_ADMINIP))
               {
                   readAccess = false;
                   System.out.println("set adminIp success");
               }
           }else{
               System.out.println( "adminIp validation failed, please check again");
           }
        }
    	
    	public String getAdminIp()
        {
            return this.adminIp;
        }
        public void setIpAddress(String ipAddress){
    	
           if(RestUtil.validata(getCriterion(S_IPADDRESS), ipAddress))
           {
               this.ipAddress = ipAddress;
               if(!checkAccessRead(S_IPADDRESS))
               {
                   readAccess = false;
                   System.out.println("set ipAddress success");
               }
           }else{
               System.out.println( "ipAddress validation failed, please check again");
           }
        }
    	
    	public String getIpAddress()
        {
            return this.ipAddress;
        }
        public void clear()
		{
		
            this.id="";
            this.host="";
            this.vniMappingType="";
            this.type="";
            this.adminIp="";
            this.ipAddress="";
		}
		
		public OneBody clone()
		{
		    OneBody cloneBody= new OneBody();
            cloneBody.setId(this.id);
            cloneBody.setHost(this.host);
            cloneBody.setVniMappingType(this.vniMappingType);
            cloneBody.setType(this.type);
            cloneBody.setAdminIp(this.adminIp);
            cloneBody.setIpAddress(this.ipAddress);
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
                if(!isWrite || checkAccessWrite(S_ID))
				buf.append("{\"id\": \"").append(this.id).append("\"}");
                if(!isWrite || checkAccessWrite(S_HOST))
	            buf.append(",{\"host\": \"").append(this.host).append("\"}");
                if(!isWrite || checkAccessWrite(S_VNIMAPPINGTYPE))
	            buf.append(",{\"vniMappingType\": \"").append(this.vniMappingType).append("\"}");
                if(!isWrite || checkAccessWrite(S_TYPE))
	            buf.append(",{\"type\": \"").append(this.type).append("\"}");
                if(!isWrite || checkAccessWrite(S_ADMINIP))
	            buf.append(",{\"adminIp\": \"").append(this.adminIp).append("\"}");
                if(!isWrite || checkAccessWrite(S_IPADDRESS))
	            buf.append(",{\"ipAddress\": \"").append(this.ipAddress).append("\"}");
			}
			 
		    if (bodyType == BodyType.APPLICATION_XML)
		    {
		    	if (!isWrite || (RestUtil.isNotEmpty(this.id)) && checkAccessWrite("id"))
		    	{ buf.append("<id>").append(this.id).append("</id>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.host)) && checkAccessWrite("host"))
		    	{ buf.append("<host>").append(this.host).append("</host>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.vniMappingType)) && checkAccessWrite("vniMappingType"))
		    	{ buf.append("<vniMappingType>").append(this.vniMappingType).append("</vniMappingType>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.type)) && checkAccessWrite("type"))
		    	{ buf.append("<type>").append(this.type).append("</type>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.adminIp)) && checkAccessWrite("adminIp"))
		    	{ buf.append("<adminIp>").append(this.adminIp).append("</adminIp>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.ipAddress)) && checkAccessWrite("ipAddress"))
		    	{ buf.append("<ipAddress>").append(this.ipAddress).append("</ipAddress>\n"); }
		    }
			 
		    return buf.toString();
		}
				
		public boolean validate()
		{
		
		     if (checkIsKey(S_ID) && RestUtil.isEmpty(this.id)) return false;
		     if (checkIsKey(S_HOST) && RestUtil.isEmpty(this.host)) return false;
		     if (checkIsKey(S_VNIMAPPINGTYPE) && RestUtil.isEmpty(this.vniMappingType)) return false;
		     if (checkIsKey(S_TYPE) && RestUtil.isEmpty(this.type)) return false;
		     if (checkIsKey(S_ADMINIP) && RestUtil.isEmpty(this.adminIp)) return false;
		     if (checkIsKey(S_IPADDRESS) && RestUtil.isEmpty(this.ipAddress)) return false;
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
	
    public VdcTnlEndPoint(OpsRestCaller rest) {
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
          throw new Exception(restfulJson+" error in VdcTnlEndPoint.java");
        }
        if(200==restfulJson.getStatusCode())
        {
        	if (BodyType.APPLICATION_JSON == bodyType)
        	{
        		OpsXMLHelper xmlHelper = new OpsXMLHelper();
        		JSONObject jsonObject = JSONObject.fromObject(restfulJson.getContent());
        		xmlHelper.configure(jsonObject, fullUrlbody, this, VdcTnlEndPoint.OneBody.class);
        		
        	} else {
        		Document doc = RestUtil.getDoc(restfulJson.getContent());
        		OpsXMLHelper xmlHelper = new OpsXMLHelper();
        		xmlHelper.configure(doc.getDocumentElement(), fullUrlbody, this, VdcTnlEndPoint.OneBody.class);
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
			
	         
            if(json.containsKey(S_ID))
            {body.setId(json.getString(S_ID));}
 	         
            if(json.containsKey(S_HOST))
            {body.setHost(json.getString(S_HOST));}
 	         
            if(json.containsKey(S_VNIMAPPINGTYPE))
            {body.setVniMappingType(json.getString(S_VNIMAPPINGTYPE));}
 	         
            if(json.containsKey(S_TYPE))
            {body.setType(json.getString(S_TYPE));}
 	         
            if(json.containsKey(S_ADMINIP))
            {body.setAdminIp(json.getString(S_ADMINIP));}
 	         
            if(json.containsKey(S_IPADDRESS))
            {body.setIpAddress(json.getString(S_IPADDRESS));}
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
            if (object.get(S_ID) != null)
	    	{
	    	    body.setId(object.getString(S_ID));
	    	}
            if (object.get(S_HOST) != null)
	    	{
	    	    body.setHost(object.getString(S_HOST));
	    	}
            if (object.get(S_VNIMAPPINGTYPE) != null)
	    	{
	    	    body.setVniMappingType(object.getString(S_VNIMAPPINGTYPE));
	    	}
            if (object.get(S_TYPE) != null)
	    	{
	    	    body.setType(object.getString(S_TYPE));
	    	}
            if (object.get(S_ADMINIP) != null)
	    	{
	    	    body.setAdminIp(object.getString(S_ADMINIP));
	    	}
            if (object.get(S_IPADDRESS) != null)
	    	{
	    	    body.setIpAddress(object.getString(S_IPADDRESS));
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
         this.readAccess = (this.readAccess && this.checkAccessRead(S_ID));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_HOST));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_VNIMAPPINGTYPE));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_TYPE));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_ADMINIP));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_IPADDRESS));
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
        VdcTnlEndPoint newond = new VdcTnlEndPoint(OpsServiceManager.getInstance().getDefaultOpsRestCall());
        try {
		   System.out.println(newond.getall());
        } catch (Exception e) 
        {
	       System.out.println(e.getMessage());
        }
    }
}  
    