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


public class VdcSubnet {

    public final static String urlBody = "/vdc/networks/network/subnets/subnet";
    public final static String fullUrlbody = "/vdc/networks/network?networkID=%s/subnets/subnet";
    public final static String S_SHARED ="shared";
    public final static String S_DHCPENABLED ="dhcpEnabled";
    public final static String S_IPVERSION ="ipVersion";
    public final static String S_GATEWAYIP ="gatewayIP";
    public final static String S_SUBNETNAME ="subnetName";
    public final static String S_CIDR ="cidr";
    public final static String S_SUBNETID ="subnetID";
    public final static String S_CIDRPREFIX ="cidrPrefix";

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
        criterions.put("shared", "{'data-type':'UINT32','min-val':'0','max-val':'4294967295','example':'7','access':'readCreate'}");        
        criterions.put("dhcpEnabled", "{'data-type':'UINT32','min-val':'0','max-val':'4294967295','example':'7','access':'readCreate'}");        
        criterions.put("ipVersion", "{'data-type':'UINT32','min-val':'0','max-val':'4294967295','example':'7','access':'readCreate'}");        
        criterions.put("gatewayIP", "{'data-type':'IPV4','min':'0','max':'255','example':'127.0.0.1','access':'readCreate'}");        
        criterions.put("subnetName", "{'data-type':'STRING','min':'1','max':'255','example':'string','access':'readCreate'}");        
        criterions.put("cidr", "{'data-type':'IPV4','min':'0','max':'255','example':'127.0.0.1','access':'readCreate'}");        
        criterions.put("subnetID", "{'data-type':'STRING','min':'1','max':'36','example':'string','key':'true','access':'readCreate'}");        
        criterions.put("cidrPrefix", "{'data-type':'INT32','min-val':'0','max-val':'255','example':'6','access':'readCreate'}");        
	}
    
    public class OneBody{
    
        private String shared ="";
        private String dhcpEnabled ="";
        private String ipVersion ="";
        private String gatewayIP ="";
        private String subnetName ="";
        private String cidr ="";
        private String subnetID ="";
        private String cidrPrefix ="";

        public void setShared(String shared){
    	
           if(RestUtil.validata(getCriterion(S_SHARED), shared))
           {
               this.shared = shared;
               if(!checkAccessRead(S_SHARED))
               {
                   readAccess = false;
                   System.out.println("set shared success");
               }
           }else{
               System.out.println( "shared validation failed, please check again");
           }
        }
    	
    	public String getShared()
        {
            return this.shared;
        }
        public void setDhcpEnabled(String dhcpEnabled){
    	
           if(RestUtil.validata(getCriterion(S_DHCPENABLED), dhcpEnabled))
           {
               this.dhcpEnabled = dhcpEnabled;
               if(!checkAccessRead(S_DHCPENABLED))
               {
                   readAccess = false;
                   System.out.println("set dhcpEnabled success");
               }
           }else{
               System.out.println( "dhcpEnabled validation failed, please check again");
           }
        }
    	
    	public String getDhcpEnabled()
        {
            return this.dhcpEnabled;
        }
        public void setIpVersion(String ipVersion){
    	
           if(RestUtil.validata(getCriterion(S_IPVERSION), ipVersion))
           {
               this.ipVersion = ipVersion;
               if(!checkAccessRead(S_IPVERSION))
               {
                   readAccess = false;
                   System.out.println("set ipVersion success");
               }
           }else{
               System.out.println( "ipVersion validation failed, please check again");
           }
        }
    	
    	public String getIpVersion()
        {
            return this.ipVersion;
        }
        public void setGatewayIP(String gatewayIP){
    	
           if(RestUtil.validata(getCriterion(S_GATEWAYIP), gatewayIP))
           {
               this.gatewayIP = gatewayIP;
               if(!checkAccessRead(S_GATEWAYIP))
               {
                   readAccess = false;
                   System.out.println("set gatewayIP success");
               }
           }else{
               System.out.println( "gatewayIP validation failed, please check again");
           }
        }
    	
    	public String getGatewayIP()
        {
            return this.gatewayIP;
        }
        public void setSubnetName(String subnetName){
    	
           if(RestUtil.validata(getCriterion(S_SUBNETNAME), subnetName))
           {
               this.subnetName = subnetName;
               if(!checkAccessRead(S_SUBNETNAME))
               {
                   readAccess = false;
                   System.out.println("set subnetName success");
               }
           }else{
               System.out.println( "subnetName validation failed, please check again");
           }
        }
    	
    	public String getSubnetName()
        {
            return this.subnetName;
        }
        public void setCidr(String cidr){
    	
           if(RestUtil.validata(getCriterion(S_CIDR), cidr))
           {
               this.cidr = cidr;
               if(!checkAccessRead(S_CIDR))
               {
                   readAccess = false;
                   System.out.println("set cidr success");
               }
           }else{
               System.out.println( "cidr validation failed, please check again");
           }
        }
    	
    	public String getCidr()
        {
            return this.cidr;
        }
        public void setSubnetID(String subnetID){
    	
           if(RestUtil.validata(getCriterion(S_SUBNETID), subnetID))
           {
               this.subnetID = subnetID;
               if(!checkAccessRead(S_SUBNETID))
               {
                   readAccess = false;
                   System.out.println("set subnetID success");
               }
           }else{
               System.out.println( "subnetID validation failed, please check again");
           }
        }
    	
    	public String getSubnetID()
        {
            return this.subnetID;
        }
        public void setCidrPrefix(String cidrPrefix){
    	
           if(RestUtil.validata(getCriterion(S_CIDRPREFIX), cidrPrefix))
           {
               this.cidrPrefix = cidrPrefix;
               if(!checkAccessRead(S_CIDRPREFIX))
               {
                   readAccess = false;
                   System.out.println("set cidrPrefix success");
               }
           }else{
               System.out.println( "cidrPrefix validation failed, please check again");
           }
        }
    	
    	public String getCidrPrefix()
        {
            return this.cidrPrefix;
        }
        public void clear()
		{
		
            this.shared="";
            this.dhcpEnabled="";
            this.ipVersion="";
            this.gatewayIP="";
            this.subnetName="";
            this.cidr="";
            this.subnetID="";
            this.cidrPrefix="";
		}
		
		public OneBody clone()
		{
		    OneBody cloneBody= new OneBody();
            cloneBody.setShared(this.shared);
            cloneBody.setDhcpEnabled(this.dhcpEnabled);
            cloneBody.setIpVersion(this.ipVersion);
            cloneBody.setGatewayIP(this.gatewayIP);
            cloneBody.setSubnetName(this.subnetName);
            cloneBody.setCidr(this.cidr);
            cloneBody.setSubnetID(this.subnetID);
            cloneBody.setCidrPrefix(this.cidrPrefix);
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
                if(!isWrite || checkAccessWrite(S_SHARED))
				buf.append("{\"shared\": \"").append(this.shared).append("\"}");
                if(!isWrite || checkAccessWrite(S_DHCPENABLED))
	            buf.append(",{\"dhcpEnabled\": \"").append(this.dhcpEnabled).append("\"}");
                if(!isWrite || checkAccessWrite(S_IPVERSION))
	            buf.append(",{\"ipVersion\": \"").append(this.ipVersion).append("\"}");
                if(!isWrite || checkAccessWrite(S_GATEWAYIP))
	            buf.append(",{\"gatewayIP\": \"").append(this.gatewayIP).append("\"}");
                if(!isWrite || checkAccessWrite(S_SUBNETNAME))
	            buf.append(",{\"subnetName\": \"").append(this.subnetName).append("\"}");
                if(!isWrite || checkAccessWrite(S_CIDR))
	            buf.append(",{\"cidr\": \"").append(this.cidr).append("\"}");
                if(!isWrite || checkAccessWrite(S_SUBNETID))
	            buf.append(",{\"subnetID\": \"").append(this.subnetID).append("\"}");
                if(!isWrite || checkAccessWrite(S_CIDRPREFIX))
	            buf.append(",{\"cidrPrefix\": \"").append(this.cidrPrefix).append("\"}");
			}
			 
		    if (bodyType == BodyType.APPLICATION_XML)
		    {
		    	if (!isWrite || (RestUtil.isNotEmpty(this.shared)) && checkAccessWrite("shared"))
		    	{ buf.append("<shared>").append(this.shared).append("</shared>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.dhcpEnabled)) && checkAccessWrite("dhcpEnabled"))
		    	{ buf.append("<dhcpEnabled>").append(this.dhcpEnabled).append("</dhcpEnabled>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.ipVersion)) && checkAccessWrite("ipVersion"))
		    	{ buf.append("<ipVersion>").append(this.ipVersion).append("</ipVersion>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.gatewayIP)) && checkAccessWrite("gatewayIP"))
		    	{ buf.append("<gatewayIP>").append(this.gatewayIP).append("</gatewayIP>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.subnetName)) && checkAccessWrite("subnetName"))
		    	{ buf.append("<subnetName>").append(this.subnetName).append("</subnetName>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.cidr)) && checkAccessWrite("cidr"))
		    	{ buf.append("<cidr>").append(this.cidr).append("</cidr>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.subnetID)) && checkAccessWrite("subnetID"))
		    	{ buf.append("<subnetID>").append(this.subnetID).append("</subnetID>\n"); }
		    	if (!isWrite || (RestUtil.isNotEmpty(this.cidrPrefix)) && checkAccessWrite("cidrPrefix"))
		    	{ buf.append("<cidrPrefix>").append(this.cidrPrefix).append("</cidrPrefix>\n"); }
		    }
			 
		    return buf.toString();
		}
				
		public boolean validate()
		{
		
		     if (checkIsKey(S_SHARED) && RestUtil.isEmpty(this.shared)) return false;
		     if (checkIsKey(S_DHCPENABLED) && RestUtil.isEmpty(this.dhcpEnabled)) return false;
		     if (checkIsKey(S_IPVERSION) && RestUtil.isEmpty(this.ipVersion)) return false;
		     if (checkIsKey(S_GATEWAYIP) && RestUtil.isEmpty(this.gatewayIP)) return false;
		     if (checkIsKey(S_SUBNETNAME) && RestUtil.isEmpty(this.subnetName)) return false;
		     if (checkIsKey(S_CIDR) && RestUtil.isEmpty(this.cidr)) return false;
		     if (checkIsKey(S_SUBNETID) && RestUtil.isEmpty(this.subnetID)) return false;
		     if (checkIsKey(S_CIDRPREFIX) && RestUtil.isEmpty(this.cidrPrefix)) return false;
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
	
    public VdcSubnet(OpsRestCaller rest) {
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
          throw new Exception(restfulJson+" error in VdcSubnet.java");
        }
        if(200==restfulJson.getStatusCode())
        {
        	if (BodyType.APPLICATION_JSON == bodyType)
        	{
        		OpsXMLHelper xmlHelper = new OpsXMLHelper();
        		JSONObject jsonObject = JSONObject.fromObject(restfulJson.getContent());
        		xmlHelper.configure(jsonObject, fullUrlbody, this, VdcSubnet.OneBody.class);
        		
        	} else {
        		Document doc = RestUtil.getDoc(restfulJson.getContent());
        		OpsXMLHelper xmlHelper = new OpsXMLHelper();
        		xmlHelper.configure(doc.getDocumentElement(), fullUrlbody, this, VdcSubnet.OneBody.class);
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
			
	         
            if(json.containsKey(S_SHARED))
            {body.setShared(json.getString(S_SHARED));}
 	         
            if(json.containsKey(S_DHCPENABLED))
            {body.setDhcpEnabled(json.getString(S_DHCPENABLED));}
 	         
            if(json.containsKey(S_IPVERSION))
            {body.setIpVersion(json.getString(S_IPVERSION));}
 	         
            if(json.containsKey(S_GATEWAYIP))
            {body.setGatewayIP(json.getString(S_GATEWAYIP));}
 	         
            if(json.containsKey(S_SUBNETNAME))
            {body.setSubnetName(json.getString(S_SUBNETNAME));}
 	         
            if(json.containsKey(S_CIDR))
            {body.setCidr(json.getString(S_CIDR));}
 	         
            if(json.containsKey(S_SUBNETID))
            {body.setSubnetID(json.getString(S_SUBNETID));}
 	         
            if(json.containsKey(S_CIDRPREFIX))
            {body.setCidrPrefix(json.getString(S_CIDRPREFIX));}
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
            if (object.get(S_SHARED) != null)
	    	{
	    	    body.setShared(object.getString(S_SHARED));
	    	}
            if (object.get(S_DHCPENABLED) != null)
	    	{
	    	    body.setDhcpEnabled(object.getString(S_DHCPENABLED));
	    	}
            if (object.get(S_IPVERSION) != null)
	    	{
	    	    body.setIpVersion(object.getString(S_IPVERSION));
	    	}
            if (object.get(S_GATEWAYIP) != null)
	    	{
	    	    body.setGatewayIP(object.getString(S_GATEWAYIP));
	    	}
            if (object.get(S_SUBNETNAME) != null)
	    	{
	    	    body.setSubnetName(object.getString(S_SUBNETNAME));
	    	}
            if (object.get(S_CIDR) != null)
	    	{
	    	    body.setCidr(object.getString(S_CIDR));
	    	}
            if (object.get(S_SUBNETID) != null)
	    	{
	    	    body.setSubnetID(object.getString(S_SUBNETID));
	    	}
            if (object.get(S_CIDRPREFIX) != null)
	    	{
	    	    body.setCidrPrefix(object.getString(S_CIDRPREFIX));
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
         this.readAccess = (this.readAccess && this.checkAccessRead(S_SHARED));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_DHCPENABLED));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_IPVERSION));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_GATEWAYIP));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_SUBNETNAME));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_CIDR));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_SUBNETID));
         this.readAccess = (this.readAccess && this.checkAccessRead(S_CIDRPREFIX));
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
        VdcSubnet newond = new VdcSubnet(OpsServiceManager.getInstance().getDefaultOpsRestCall());
        try {
		   System.out.println(newond.getall());
        } catch (Exception e) 
        {
	       System.out.println(e.getMessage());
        }
    }
}  
    