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


public class VdcPortBinding {

    public final static String urlBody = "/vdc/networks/network/ports/port/portBindings/portBinding";
    public final static String fullUrlbody = "/vdc/networks/network?networkID=%s/ports/port?portID=%s/portBindings/portBinding";
    public final static String S_VIFTYPE ="vifType";
    public final static String S_HOST ="host";
    public final static String S_SEGMENT ="segment";
    public final static String S_CAPPORTFILTER ="capPortFilter";
    public final static String S_DRIVER ="driver";

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
	// Keyword must be assigned
    private String portID ="";

    static {
	    criterions = new HashMap<String, String>();
        criterions.put("networkID", "{'data-type':'STRING','min':'1','max':'36','example':'string','key':'true','access':'readCreate'}");
        criterions.put("portID", "{'data-type':'STRING','min':'1','max':'36','example':'string','key':'true','access':'readCreate'}");
        criterions.put("vifType", "{'data-type':'STRING','min':'1','max':'64','example':'string','access':'readCreate'}");        
        criterions.put("host", "{'data-type':'STRING','min':'1','max':'255','example':'string','key':'true','access':'readCreate'}");        
        criterions.put("segment", "{'data-type':'STRING','min':'1','max':'36','example':'string','access':'readCreate'}");        
        criterions.put("capPortFilter", "{'data-type':'UINT32','min-val':'0','max-val':'4294967295','example':'7','access':'readCreate'}");        
        criterions.put("driver", "{'data-type':'STRING','min':'1','max':'64','example':'string','access':'readCreate'}");        
	}
    
    public class OneBody{
    
        private String vifType ="";
        private String host ="";
        private String segment ="";
        private String capPortFilter ="";
        private String driver ="";

        public void setVifType(String vifType){
    	
           if(RestUtil.validata(getCriterion(S_VIFTYPE), vifType))
           {
               this.vifType = vifType;
               if(!checkAccessRead(S_VIFTYPE))
               {
                   readAccess = false;
                   System.out.println("set vifType success");
               }
           }else{
               System.out.println( "vifType validation failed, please check again");
           }
        }
    	
    	public String getVifType()
        {
            return this.vifType;
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
        public void setSegment(String segment){
    	
           if(RestUtil.validata(getCriterion(S_SEGMENT), segment))
           {
               this.segment = segment;
               if(!checkAccessRead(S_SEGMENT))
               {
                   readAccess = false;
                   System.out.println("set segment success");
               }
           }else{
               System.out.println( "segment validation failed, please check again");
           }
        }
    	
    	public String getSegment()
        {
            return this.segment;
        }
        public void setCapPortFilter(String capPortFilter){
    	
           if(RestUtil.validata(getCriterion(S_CAPPORTFILTER), capPortFilter))
           {
               this.capPortFilter = capPortFilter;
               if(!checkAccessRead(S_CAPPORTFILTER))
               {
                   readAccess = false;
                   System.out.println("set capPortFilter success");
               }
           }else{
               System.out.println( "capPortFilter validation failed, please check again");
           }
        }
    	
    	public String getCapPortFilter()
        {
            return this.capPortFilter;
        }
        public void setDriver(String driver){
    	
           if(RestUtil.validata(getCriterion(S_DRIVER), driver))
           {
               this.driver = driver;
               if(!checkAccessRead(S_DRIVER))
               {
                   readAccess = false;
                   System.out.println("set driver success");
               }
           }else{
               System.out.println( "driver validation failed, please check again");
           }
        }
    	
    	public String getDriver()
        {
            return this.driver;
        }
        public void clear()
		{
		
            this.vifType="";
            this.host="";
            this.segment="";
            this.capPortFilter="";
            this.driver="";
		}
		
		public OneBody clone()
		{
		    OneBody cloneBody= new OneBody();
            cloneBody.setVifType(this.vifType);
            cloneBody.setHost(this.host);
            cloneBody.setSegment(this.segment);
            cloneBody.setCapPortFilter(this.capPortFilter);
            cloneBody.setDriver(this.driver);
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
                if(!isWrite || checkAccessWrite(S_VIFTYPE))
				buf.append("{\"vifType\": \"").append(this.vifType).append("\"}");
                if(!isWrite || checkAccessWrite(S_HOST))
	            buf.append(",{\"host\": \"").append(this.host).append("\"}");
                if(!isWrite || checkAccessWrite(S_SEGMENT))
	            buf.append(",{\"segment\": \"").append(this.segment).append("\"}");
                if(!isWrite || checkAccessWrite(S_CAPPORTFILTER))
	            buf.append(",{\"capPortFilter\": \"").append(this.capPortFilter).append("\"}");
                if(!isWrite || checkAccessWrite(S_DRIVER))
	            buf.append(",{\"driver\": \"").append(this.driver).append("\"}");
			}
			 
		    if (bodyType == BodyType.APPLICATION_XML)
		    {
		    	if (!isWrite || (RestUtil.isNotEmpty(this.vifType)) && checkAccessWrite("vifType"))
		    	{ buf.append("<vifType>").append(this.vifType).append("</vifType>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.host)) && checkAccessWrite("host"))
		    	{ buf.append("<host>").append(this.host).append("</host>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.segment)) && checkAccessWrite("segment"))
		    	{ buf.append("<segment>").append(this.segment).append("</segment>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.capPortFilter)) && checkAccessWrite("capPortFilter"))
		    	{ buf.append("<capPortFilter>").append(this.capPortFilter).append("</capPortFilter>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.driver)) && checkAccessWrite("driver"))
		    	{ buf.append("<driver>").append(this.driver).append("</driver>\n"); }
		    }
			 
		    return buf.toString();
		}
				
		public boolean validate()
		{
		
		     if (checkIsKey(S_VIFTYPE) && RestUtil.isEmpty(this.vifType)) return false;
		     if (checkIsKey(S_HOST) && RestUtil.isEmpty(this.host)) return false;
		     if (checkIsKey(S_SEGMENT) && RestUtil.isEmpty(this.segment)) return false;
		     if (checkIsKey(S_CAPPORTFILTER) && RestUtil.isEmpty(this.capPortFilter)) return false;
		     if (checkIsKey(S_DRIVER) && RestUtil.isEmpty(this.driver)) return false;
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
	
    public VdcPortBinding(OpsRestCaller rest) {
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
    public void setPortID(String portID){
        this.portID = portID;
    }
	
	public String getPortID()
    {
        return this.portID;
    }
 
    public String getUrlBody() {
     
           String urlpath = this.fullUrlbody;
           List<String> lstKeyArr = new ArrayList<String>();
           lstKeyArr.add(this.networkID);
           lstKeyArr.add(this.portID);
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
          throw new Exception(restfulJson+" error in VdcPortBinding.java");
        }
        if(200==restfulJson.getStatusCode())
        {
        	if (BodyType.APPLICATION_JSON == bodyType)
        	{
        		OpsXMLHelper xmlHelper = new OpsXMLHelper();
        		JSONObject jsonObject = JSONObject.fromObject(restfulJson.getContent());
        		xmlHelper.configure(jsonObject, fullUrlbody, this, VdcPortBinding.OneBody.class);
        		
        	} else {
        		Document doc = RestUtil.getDoc(restfulJson.getContent());
        		OpsXMLHelper xmlHelper = new OpsXMLHelper();
        		xmlHelper.configure(doc.getDocumentElement(), fullUrlbody, this, VdcPortBinding.OneBody.class);
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
			
	         
            if(json.containsKey(S_VIFTYPE))
            {body.setVifType(json.getString(S_VIFTYPE));}
 	         
            if(json.containsKey(S_HOST))
            {body.setHost(json.getString(S_HOST));}
 	         
            if(json.containsKey(S_SEGMENT))
            {body.setSegment(json.getString(S_SEGMENT));}
 	         
            if(json.containsKey(S_CAPPORTFILTER))
            {body.setCapPortFilter(json.getString(S_CAPPORTFILTER));}
 	         
            if(json.containsKey(S_DRIVER))
            {body.setDriver(json.getString(S_DRIVER));}
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
            if (object.get(S_VIFTYPE) != null)
	    	{
	    	    body.setVifType(object.getString(S_VIFTYPE));
	    	}
            if (object.get(S_HOST) != null)
	    	{
	    	    body.setHost(object.getString(S_HOST));
	    	}
            if (object.get(S_SEGMENT) != null)
	    	{
	    	    body.setSegment(object.getString(S_SEGMENT));
	    	}
            if (object.get(S_CAPPORTFILTER) != null)
	    	{
	    	    body.setCapPortFilter(object.getString(S_CAPPORTFILTER));
	    	}
            if (object.get(S_DRIVER) != null)
	    	{
	    	    body.setDriver(object.getString(S_DRIVER));
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
         this.readAccess = (this.readAccess && this.checkAccessRead(S_VIFTYPE));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_HOST));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_SEGMENT));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_CAPPORTFILTER));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_DRIVER));
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
        VdcPortBinding newond = new VdcPortBinding(OpsServiceManager.getInstance().getDefaultOpsRestCall());
        try {
		   System.out.println(newond.getall());
        } catch (Exception e) 
        {
	       System.out.println(e.getMessage());
        }
    }
}  
    