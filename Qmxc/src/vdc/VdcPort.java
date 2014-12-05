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


public class VdcPort {

    public final static String urlBody = "/vdc/networks/network/ports/port";
    public final static String fullUrlbody = "/vdc/networks/network?networkID=%s/ports/port";
    public final static String S_OPERSTATUS ="operStatus";
    public final static String S_ADMINSTATUS ="adminStatus";
    public final static String S_DEVICEID ="deviceID";
    public final static String S_MACADDRESS ="macAddress";
    public final static String S_PORTNAME ="portName";
    public final static String S_DEVICEOWNER ="deviceOwner";
    public final static String S_PROFILE ="profile";
    public final static String S_PORTID ="portID";

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
	
	// Keyword must be assigned
    private String networkID ="";

    static {
	    criterions = new HashMap<String, String>();
        criterions.put("networkID", "{'data-type':'STRING','min':'1','max':'36','example':'string','key':'true','access':'readCreate'}");
        criterions.put("operStatus", "{'data-type':'STRING','min':'1','max':'16','example':'string','access':'readCreate'}");        
        criterions.put("adminStatus", "{'data-type':'UINT32','min-val':'0','max-val':'4294967295','example':'7','access':'readCreate'}");        
        criterions.put("deviceID", "{'data-type':'STRING','min':'1','max':'255','example':'string','access':'readCreate'}");        
        criterions.put("macAddress", "{'data-type':'STRING','min':'0','max':'255','example':'string','access':'readCreate'}");        
        criterions.put("portName", "{'data-type':'STRING','min':'1','max':'255','example':'string','access':'readCreate'}");        
        criterions.put("deviceOwner", "{'data-type':'STRING','min':'1','max':'255','example':'string','access':'readCreate'}");        
        criterions.put("profile", "{'data-type':'STRING','min':'1','max':'255','example':'string','access':'readCreate'}");        
        criterions.put("portID", "{'data-type':'STRING','min':'1','max':'36','example':'string','key':'true','access':'readCreate'}");        
	}
    
    public class OneBody{
    
        private String operStatus ="";
        private String adminStatus ="";
        private String deviceID ="";
        private String macAddress ="";
        private String portName ="";
        private String deviceOwner ="";
        private String profile ="";
        private String portID ="";

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
        public void setDeviceID(String deviceID){
    	
           if(RestUtil.validata(getCriterion(S_DEVICEID), deviceID))
           {
               this.deviceID = deviceID;
               if(!checkAccessRead(S_DEVICEID))
               {
                   readAccess = false;
                   System.out.println("set deviceID success");
               }
           }else{
               System.out.println( "deviceID validation failed, please check again");
           }
        }
    	
    	public String getDeviceID()
        {
            return this.deviceID;
        }
        public void setMacAddress(String macAddress){
    	
           if(RestUtil.validata(getCriterion(S_MACADDRESS), macAddress))
           {
               this.macAddress = macAddress;
               if(!checkAccessRead(S_MACADDRESS))
               {
                   readAccess = false;
                   System.out.println("set macAddress success");
               }
           }else{
               System.out.println( "macAddress validation failed, please check again");
           }
        }
    	
    	public String getMacAddress()
        {
            return this.macAddress;
        }
        public void setPortName(String portName){
    	
           if(RestUtil.validata(getCriterion(S_PORTNAME), portName))
           {
               this.portName = portName;
               if(!checkAccessRead(S_PORTNAME))
               {
                   readAccess = false;
                   System.out.println("set portName success");
               }
           }else{
               System.out.println( "portName validation failed, please check again");
           }
        }
    	
    	public String getPortName()
        {
            return this.portName;
        }
        public void setDeviceOwner(String deviceOwner){
    	
           if(RestUtil.validata(getCriterion(S_DEVICEOWNER), deviceOwner))
           {
               this.deviceOwner = deviceOwner;
               if(!checkAccessRead(S_DEVICEOWNER))
               {
                   readAccess = false;
                   System.out.println("set deviceOwner success");
               }
           }else{
               System.out.println( "deviceOwner validation failed, please check again");
           }
        }
    	
    	public String getDeviceOwner()
        {
            return this.deviceOwner;
        }
        public void setProfile(String profile){
    	
           if(RestUtil.validata(getCriterion(S_PROFILE), profile))
           {
               this.profile = profile;
               if(!checkAccessRead(S_PROFILE))
               {
                   readAccess = false;
                   System.out.println("set profile success");
               }
           }else{
               System.out.println( "profile validation failed, please check again");
           }
        }
    	
    	public String getProfile()
        {
            return this.profile;
        }
        public void setPortID(String portID){
    	
           if(RestUtil.validata(getCriterion(S_PORTID), portID))
           {
               this.portID = portID;
               if(!checkAccessRead(S_PORTID))
               {
                   readAccess = false;
                   System.out.println("set portID success");
               }
           }else{
               System.out.println( "portID validation failed, please check again");
           }
        }
    	
    	public String getPortID()
        {
            return this.portID;
        }
        public void clear()
		{
		
            this.operStatus="";
            this.adminStatus="";
            this.deviceID="";
            this.macAddress="";
            this.portName="";
            this.deviceOwner="";
            this.profile="";
            this.portID="";
		}
		
		public OneBody clone()
		{
		    OneBody cloneBody= new OneBody();
            cloneBody.setOperStatus(this.operStatus);
            cloneBody.setAdminStatus(this.adminStatus);
            cloneBody.setDeviceID(this.deviceID);
            cloneBody.setMacAddress(this.macAddress);
            cloneBody.setPortName(this.portName);
            cloneBody.setDeviceOwner(this.deviceOwner);
            cloneBody.setProfile(this.profile);
            cloneBody.setPortID(this.portID);
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
                if(!isWrite || checkAccessWrite(S_OPERSTATUS))
				buf.append("{\"operStatus\": \"").append(this.operStatus).append("\"}");
                if(!isWrite || checkAccessWrite(S_ADMINSTATUS))
	            buf.append(",{\"adminStatus\": \"").append(this.adminStatus).append("\"}");
                if(!isWrite || checkAccessWrite(S_DEVICEID))
	            buf.append(",{\"deviceID\": \"").append(this.deviceID).append("\"}");
                if(!isWrite || checkAccessWrite(S_MACADDRESS))
	            buf.append(",{\"macAddress\": \"").append(this.macAddress).append("\"}");
                if(!isWrite || checkAccessWrite(S_PORTNAME))
	            buf.append(",{\"portName\": \"").append(this.portName).append("\"}");
                if(!isWrite || checkAccessWrite(S_DEVICEOWNER))
	            buf.append(",{\"deviceOwner\": \"").append(this.deviceOwner).append("\"}");
                if(!isWrite || checkAccessWrite(S_PROFILE))
	            buf.append(",{\"profile\": \"").append(this.profile).append("\"}");
                if(!isWrite || checkAccessWrite(S_PORTID))
	            buf.append(",{\"portID\": \"").append(this.portID).append("\"}");
			}
			 
		    if (bodyType == BodyType.APPLICATION_XML)
		    {
		    	if (!isWrite || (RestUtil.isNotEmpty(this.operStatus)) && checkAccessWrite("operStatus"))
		    	{ buf.append("<operStatus>").append(this.operStatus).append("</operStatus>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.adminStatus)) && checkAccessWrite("adminStatus"))
		    	{ buf.append("<adminStatus>").append(this.adminStatus).append("</adminStatus>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.deviceID)) && checkAccessWrite("deviceID"))
		    	{ buf.append("<deviceID>").append(this.deviceID).append("</deviceID>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.macAddress)) && checkAccessWrite("macAddress"))
		    	{ buf.append("<macAddress>").append(this.macAddress).append("</macAddress>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.portName)) && checkAccessWrite("portName"))
		    	{ buf.append("<portName>").append(this.portName).append("</portName>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.deviceOwner)) && checkAccessWrite("deviceOwner"))
		    	{ buf.append("<deviceOwner>").append(this.deviceOwner).append("</deviceOwner>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.profile)) && checkAccessWrite("profile"))
		    	{ buf.append("<profile>").append(this.profile).append("</profile>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.portID)) && checkAccessWrite("portID"))
		    	{ buf.append("<portID>").append(this.portID).append("</portID>\n"); }
		    }
			 
		    return buf.toString();
		}
				
		public boolean validate()
		{
		
		     if (checkIsKey(S_OPERSTATUS) && RestUtil.isEmpty(this.operStatus)) return false;
		     if (checkIsKey(S_ADMINSTATUS) && RestUtil.isEmpty(this.adminStatus)) return false;
		     if (checkIsKey(S_DEVICEID) && RestUtil.isEmpty(this.deviceID)) return false;
		     if (checkIsKey(S_MACADDRESS) && RestUtil.isEmpty(this.macAddress)) return false;
		     if (checkIsKey(S_PORTNAME) && RestUtil.isEmpty(this.portName)) return false;
		     if (checkIsKey(S_DEVICEOWNER) && RestUtil.isEmpty(this.deviceOwner)) return false;
		     if (checkIsKey(S_PROFILE) && RestUtil.isEmpty(this.profile)) return false;
		     if (checkIsKey(S_PORTID) && RestUtil.isEmpty(this.portID)) return false;
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
	
    public VdcPort(OpsRestCaller rest) {
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
  
    public void setNetworkID(String networkID){
        this.networkID = networkID;
    }
	
	public String getNetworkID()
    {
        return this.networkID;
    }
 
    public String getUrlBody() {
     
           String urlpath = this.fullUrlbody;
           List<String> lstKeyArr = new ArrayList<String>();
           lstKeyArr.add(this.networkID);
           for(String s :lstKeyArr){
               s = s.replace("/", "%2F");
               urlpath = urlpath.replaceFirst("%s", s);
           }
           return urlpath;
    } 
	
    public void parseRestful(RetRpc restfulJson) throws Exception
    {
    
       this.setIndexint(0);
        this.multiBody.clear();
        if(null == restfulJson)
        {
          throw new Exception(restfulJson+" error in VdcPort.java");
        }
        if(200==restfulJson.getStatusCode())
        {
        	if (BodyType.APPLICATION_JSON == bodyType)
        	{
        		OpsXMLHelper xmlHelper = new OpsXMLHelper();
        		JSONObject jsonObject = JSONObject.fromObject(restfulJson.getContent());
        		xmlHelper.configure(jsonObject, fullUrlbody, this, VdcPort.OneBody.class);
        		
        	} else {
        		Document doc = RestUtil.getDoc(restfulJson.getContent());
        		OpsXMLHelper xmlHelper = new OpsXMLHelper();
        		xmlHelper.configure(doc.getDocumentElement(), fullUrlbody, this, VdcPort.OneBody.class);
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
			
	         
            if(json.containsKey(S_OPERSTATUS))
            {body.setOperStatus(json.getString(S_OPERSTATUS));}
 	         
            if(json.containsKey(S_ADMINSTATUS))
            {body.setAdminStatus(json.getString(S_ADMINSTATUS));}
 	         
            if(json.containsKey(S_DEVICEID))
            {body.setDeviceID(json.getString(S_DEVICEID));}
 	         
            if(json.containsKey(S_MACADDRESS))
            {body.setMacAddress(json.getString(S_MACADDRESS));}
 	         
            if(json.containsKey(S_PORTNAME))
            {body.setPortName(json.getString(S_PORTNAME));}
 	         
            if(json.containsKey(S_DEVICEOWNER))
            {body.setDeviceOwner(json.getString(S_DEVICEOWNER));}
 	         
            if(json.containsKey(S_PROFILE))
            {body.setProfile(json.getString(S_PROFILE));}
 	         
            if(json.containsKey(S_PORTID))
            {body.setPortID(json.getString(S_PORTID));}
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
            if (object.get(S_OPERSTATUS) != null)
	    	{
	    	    body.setOperStatus(object.getString(S_OPERSTATUS));
	    	}
            if (object.get(S_ADMINSTATUS) != null)
	    	{
	    	    body.setAdminStatus(object.getString(S_ADMINSTATUS));
	    	}
            if (object.get(S_DEVICEID) != null)
	    	{
	    	    body.setDeviceID(object.getString(S_DEVICEID));
	    	}
            if (object.get(S_MACADDRESS) != null)
	    	{
	    	    body.setMacAddress(object.getString(S_MACADDRESS));
	    	}
            if (object.get(S_PORTNAME) != null)
	    	{
	    	    body.setPortName(object.getString(S_PORTNAME));
	    	}
            if (object.get(S_DEVICEOWNER) != null)
	    	{
	    	    body.setDeviceOwner(object.getString(S_DEVICEOWNER));
	    	}
            if (object.get(S_PROFILE) != null)
	    	{
	    	    body.setProfile(object.getString(S_PROFILE));
	    	}
            if (object.get(S_PORTID) != null)
	    	{
	    	    body.setPortID(object.getString(S_PORTID));
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
         this.readAccess = (this.readAccess && this.checkAccessRead(S_OPERSTATUS));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_ADMINSTATUS));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_DEVICEID));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_MACADDRESS));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_PORTNAME));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_DEVICEOWNER));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_PROFILE));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_PORTID));
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
        VdcPort newond = new VdcPort(OpsServiceManager.getInstance().getDefaultOpsRestCall());
        try {
		   System.out.println(newond.getall());
        } catch (Exception e) 
        {
	       System.out.println(e.getMessage());
        }
    }
}  
    