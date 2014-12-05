package serviceflow;
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


public class ServiceflowsFlowFilter {

    public final static String urlBody = "/serviceflows/serviceflow/flows/flow/flowFilters/flowFilter";
    public final static String fullUrlbody = "/serviceflows/serviceflow?id=%s/flows/flow?flowId=%s/flowFilters/flowFilter";
    public final static String S_SEQNUM ="seqNum";
    public final static String S_DESTIPADDRESS ="destIpAddress";
    public final static String S_SRCIPADDRESS ="srcIpAddress";
    public final static String S_DESTIPADDRESSMASK ="destIpAddressMask";
    public final static String S_SRCIPADDRESSMASK ="srcIpAddressMask";

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
    private String id ="";
	// Keyword must be assigned
    private String flowId ="";

    static {
	    criterions = new HashMap<String, String>();
        criterions.put("id", "{'data-type':'STRING','min':'1','max':'36','example':'string','key':'true','access':'readCreate'}");
        criterions.put("flowId", "{'data-type':'STRING','min':'1','max':'36','example':'string','key':'true','access':'readCreate'}");
        criterions.put("seqNum", "{'data-type':'UINT32','min-val':'0','max-val':'4294967294','example':'7','key':'true','access':'readCreate'}");        
        criterions.put("destIpAddress", "{'data-type':'IPV4','min':'0','max':'255','example':'127.0.0.1','access':'readCreate'}");        
        criterions.put("srcIpAddress", "{'data-type':'IPV4','min':'0','max':'255','example':'127.0.0.1','access':'readCreate'}");        
        criterions.put("destIpAddressMask", "{'data-type':'IPV4','min':'0','max':'255','example':'127.0.0.1','access':'readCreate'}");        
        criterions.put("srcIpAddressMask", "{'data-type':'IPV4','min':'0','max':'255','example':'127.0.0.1','access':'readCreate'}");        
	}
    
    public class OneBody{
    
        private String seqNum ="";
        private String destIpAddress ="";
        private String srcIpAddress ="";
        private String destIpAddressMask ="";
        private String srcIpAddressMask ="";

        public void setSeqNum(String seqNum){
    	
           if(RestUtil.validata(getCriterion(S_SEQNUM), seqNum))
           {
               this.seqNum = seqNum;
               if(!checkAccessRead(S_SEQNUM))
               {
                   readAccess = false;
                   System.out.println("set seqNum success");
               }
           }else{
               System.out.println( "seqNum validation failed, please check again");
           }
        }
    	
    	public String getSeqNum()
        {
            return this.seqNum;
        }
        public void setDestIpAddress(String destIpAddress){
    	
           if(RestUtil.validata(getCriterion(S_DESTIPADDRESS), destIpAddress))
           {
               this.destIpAddress = destIpAddress;
               if(!checkAccessRead(S_DESTIPADDRESS))
               {
                   readAccess = false;
                   System.out.println("set destIpAddress success");
               }
           }else{
               System.out.println( "destIpAddress validation failed, please check again");
           }
        }
    	
    	public String getDestIpAddress()
        {
            return this.destIpAddress;
        }
        public void setSrcIpAddress(String srcIpAddress){
    	
           if(RestUtil.validata(getCriterion(S_SRCIPADDRESS), srcIpAddress))
           {
               this.srcIpAddress = srcIpAddress;
               if(!checkAccessRead(S_SRCIPADDRESS))
               {
                   readAccess = false;
                   System.out.println("set srcIpAddress success");
               }
           }else{
               System.out.println( "srcIpAddress validation failed, please check again");
           }
        }
    	
    	public String getSrcIpAddress()
        {
            return this.srcIpAddress;
        }
        public void setDestIpAddressMask(String destIpAddressMask){
    	
           if(RestUtil.validata(getCriterion(S_DESTIPADDRESSMASK), destIpAddressMask))
           {
               this.destIpAddressMask = destIpAddressMask;
               if(!checkAccessRead(S_DESTIPADDRESSMASK))
               {
                   readAccess = false;
                   System.out.println("set destIpAddressMask success");
               }
           }else{
               System.out.println( "destIpAddressMask validation failed, please check again");
           }
        }
    	
    	public String getDestIpAddressMask()
        {
            return this.destIpAddressMask;
        }
        public void setSrcIpAddressMask(String srcIpAddressMask){
    	
           if(RestUtil.validata(getCriterion(S_SRCIPADDRESSMASK), srcIpAddressMask))
           {
               this.srcIpAddressMask = srcIpAddressMask;
               if(!checkAccessRead(S_SRCIPADDRESSMASK))
               {
                   readAccess = false;
                   System.out.println("set srcIpAddressMask success");
               }
           }else{
               System.out.println( "srcIpAddressMask validation failed, please check again");
           }
        }
    	
    	public String getSrcIpAddressMask()
        {
            return this.srcIpAddressMask;
        }
        public void clear()
		{
		
            this.seqNum="";
            this.destIpAddress="";
            this.srcIpAddress="";
            this.destIpAddressMask="";
            this.srcIpAddressMask="";
		}
		
		public OneBody clone()
		{
		    OneBody cloneBody= new OneBody();
            cloneBody.setSeqNum(this.seqNum);
            cloneBody.setDestIpAddress(this.destIpAddress);
            cloneBody.setSrcIpAddress(this.srcIpAddress);
            cloneBody.setDestIpAddressMask(this.destIpAddressMask);
            cloneBody.setSrcIpAddressMask(this.srcIpAddressMask);
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
                if(!isWrite || checkAccessWrite(S_SEQNUM))
				buf.append("{\"seqNum\": \"").append(this.seqNum).append("\"}");
                if(!isWrite || checkAccessWrite(S_DESTIPADDRESS))
	            buf.append(",{\"destIpAddress\": \"").append(this.destIpAddress).append("\"}");
                if(!isWrite || checkAccessWrite(S_SRCIPADDRESS))
	            buf.append(",{\"srcIpAddress\": \"").append(this.srcIpAddress).append("\"}");
                if(!isWrite || checkAccessWrite(S_DESTIPADDRESSMASK))
	            buf.append(",{\"destIpAddressMask\": \"").append(this.destIpAddressMask).append("\"}");
                if(!isWrite || checkAccessWrite(S_SRCIPADDRESSMASK))
	            buf.append(",{\"srcIpAddressMask\": \"").append(this.srcIpAddressMask).append("\"}");
			}
			 
		    if (bodyType == BodyType.APPLICATION_XML)
		    {
		    	if (!isWrite || (RestUtil.isNotEmpty(this.seqNum)) && checkAccessWrite("seqNum"))
		    	{ buf.append("<seqNum>").append(this.seqNum).append("</seqNum>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.destIpAddress)) && checkAccessWrite("destIpAddress"))
		    	{ buf.append("<destIpAddress>").append(this.destIpAddress).append("</destIpAddress>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.srcIpAddress)) && checkAccessWrite("srcIpAddress"))
		    	{ buf.append("<srcIpAddress>").append(this.srcIpAddress).append("</srcIpAddress>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.destIpAddressMask)) && checkAccessWrite("destIpAddressMask"))
		    	{ buf.append("<destIpAddressMask>").append(this.destIpAddressMask).append("</destIpAddressMask>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.srcIpAddressMask)) && checkAccessWrite("srcIpAddressMask"))
		    	{ buf.append("<srcIpAddressMask>").append(this.srcIpAddressMask).append("</srcIpAddressMask>\n"); }
		    }
			 
		    return buf.toString();
		}
				
		public boolean validate()
		{
		
		     if (checkIsKey(S_SEQNUM) && RestUtil.isEmpty(this.seqNum)) return false;
		     if (checkIsKey(S_DESTIPADDRESS) && RestUtil.isEmpty(this.destIpAddress)) return false;
		     if (checkIsKey(S_SRCIPADDRESS) && RestUtil.isEmpty(this.srcIpAddress)) return false;
		     if (checkIsKey(S_DESTIPADDRESSMASK) && RestUtil.isEmpty(this.destIpAddressMask)) return false;
		     if (checkIsKey(S_SRCIPADDRESSMASK) && RestUtil.isEmpty(this.srcIpAddressMask)) return false;
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
	
    public ServiceflowsFlowFilter(OpsRestCaller rest) {
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
  
    public void setId(String id){
        this.id = id;
    }
	
	public String getId()
    {
        return this.id;
    }
    public void setFlowId(String flowId){
        this.flowId = flowId;
    }
	
	public String getFlowId()
    {
        return this.flowId;
    }
 
    public String getUrlBody() {
     
           String urlpath = this.fullUrlbody;
           List<String> lstKeyArr = new ArrayList<String>();
           lstKeyArr.add(this.id);
           lstKeyArr.add(this.flowId);
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
          throw new Exception(restfulJson+" error in ServiceflowsFlowFilter.java");
        }
        if(200==restfulJson.getStatusCode())
        {
        	if (BodyType.APPLICATION_JSON == bodyType)
        	{
        		OpsXMLHelper xmlHelper = new OpsXMLHelper();
        		JSONObject jsonObject = JSONObject.fromObject(restfulJson.getContent());
        		xmlHelper.configure(jsonObject, fullUrlbody, this, ServiceflowsFlowFilter.OneBody.class);
        		
        	} else {
        		Document doc = RestUtil.getDoc(restfulJson.getContent());
        		OpsXMLHelper xmlHelper = new OpsXMLHelper();
        		xmlHelper.configure(doc.getDocumentElement(), fullUrlbody, this, ServiceflowsFlowFilter.OneBody.class);
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
			
	         
            if(json.containsKey(S_SEQNUM))
            {body.setSeqNum(json.getString(S_SEQNUM));}
 	         
            if(json.containsKey(S_DESTIPADDRESS))
            {body.setDestIpAddress(json.getString(S_DESTIPADDRESS));}
 	         
            if(json.containsKey(S_SRCIPADDRESS))
            {body.setSrcIpAddress(json.getString(S_SRCIPADDRESS));}
 	         
            if(json.containsKey(S_DESTIPADDRESSMASK))
            {body.setDestIpAddressMask(json.getString(S_DESTIPADDRESSMASK));}
 	         
            if(json.containsKey(S_SRCIPADDRESSMASK))
            {body.setSrcIpAddressMask(json.getString(S_SRCIPADDRESSMASK));}
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
            if (object.get(S_SEQNUM) != null)
	    	{
	    	    body.setSeqNum(object.getString(S_SEQNUM));
	    	}
            if (object.get(S_DESTIPADDRESS) != null)
	    	{
	    	    body.setDestIpAddress(object.getString(S_DESTIPADDRESS));
	    	}
            if (object.get(S_SRCIPADDRESS) != null)
	    	{
	    	    body.setSrcIpAddress(object.getString(S_SRCIPADDRESS));
	    	}
            if (object.get(S_DESTIPADDRESSMASK) != null)
	    	{
	    	    body.setDestIpAddressMask(object.getString(S_DESTIPADDRESSMASK));
	    	}
            if (object.get(S_SRCIPADDRESSMASK) != null)
	    	{
	    	    body.setSrcIpAddressMask(object.getString(S_SRCIPADDRESSMASK));
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
         this.readAccess = (this.readAccess && this.checkAccessRead(S_SEQNUM));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_DESTIPADDRESS));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_SRCIPADDRESS));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_DESTIPADDRESSMASK));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_SRCIPADDRESSMASK));
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
        ServiceflowsFlowFilter newond = new ServiceflowsFlowFilter(OpsServiceManager.getInstance().getDefaultOpsRestCall());
        try {
		   System.out.println(newond.getall());
        } catch (Exception e) 
        {
	       System.out.println(e.getMessage());
        }
    }
}  
    