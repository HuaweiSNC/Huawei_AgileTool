/*********************************************************************
Copyright, 2008-2010, Huawei Tech. Co., Ltd.
All Rights Reserved
----------------------------------------------------------------------
Project Code  : OPS V2.1
 *********************************************************************/
package com.huawei.agilete.base.servlet.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Helps mapping the XML to object by instantiating and initializing beans.
 * defines the operations to be performed.
 */
public class OpsXMLHelper {

    private static final Logger LOG = Logger.getLogger(OpsXMLHelper.class);

    public OpsXMLHelper() {
        
    }

    class RestApiHelper
    {
        private String fullUrlPath = "";
        private Map<String, String> setMothod = new LinkedHashMap<String, String>();
        private List<Element> childBodys = new ArrayList<Element>();
        private List<JSONArray> childJsonBodys = new ArrayList<JSONArray>();
        public String getFullUrlPath() {
            return fullUrlPath;
        }
        
        public List<JSONArray> getChildJsonBodys() {
            return childJsonBodys;
        }

        public Map<String, String> getSetMothod() {
            return setMothod;
        }
 
        public List<Element> getChildBodys() {
            return childBodys;
        }
  
        public void setFullUrlPath(String fullUrlPath) {
            this.fullUrlPath = fullUrlPath;
        }
        
        public void addBody(JSONArray body)
        {
            if (body != null)
            {
                childJsonBodys.add(body);
            }
        }
        
        public void addBody(Element body)
        {
            if (body != null)
            {
                childBodys.add(body);
            }
        }
        
        public void addMothod(String name, String value)
        {
            if (RestUtil.isNotEmpty(name))
            {
                setMothod.put(name, value);
            }
        }
        
        public RestApiHelper clone()
        {
            
            RestApiHelper help = new RestApiHelper();
            for(Element element : childBodys)
            {
                help.addBody(element);
            }
            
            for (String key: setMothod.keySet())
            {
                help.addMothod(key, setMothod.get(key));
            }
            
            help.setFullUrlPath(fullUrlPath);
            
            return help;
            
        }
        
    }
    
    /***
     * 
     * @param objectJson
     * @param schemaApiPath
     * @param motherClass
     * @param pluginClass
     * @return
     * @throws Exception
     */
    public Object configure(JSONObject objectJson, String schemaApiPath,  Object motherClass, Class pluginClass) throws Exception {
 
           List<Object> objects = new ArrayList<Object>();
           if (RestUtil.isEmpty(schemaApiPath))
           {
               Object object = configure(objectJson.getJSONArray("null"), pluginClass, motherClass);
               objects.add(object);
               // 设置body方法
               addBody(motherClass, objects, pluginClass);
               return motherClass;
               
           }
           
           // 进行schema api解析
           RestApiHelper restApi = getRestApiHelperFromElement(objectJson, schemaApiPath);
           restApi.getSetMothod();
           
           Map<String, String> setMethodValue = restApi.getSetMothod();
           List<JSONArray> arrayJson = restApi.getChildJsonBodys();

           // 读取每个body的方法
           Object pluginInstance = null;
           for(JSONArray body : arrayJson)
           {
               pluginInstance = configure(body, pluginClass, motherClass);
               if (null != pluginInstance){
                   objects.add(pluginInstance);
               }
           }
           
           // 设置body方法
           addBody(motherClass, objects, pluginClass);
           // 设置set方法
           setMothod(motherClass, setMethodValue);
           
           return motherClass;
    }
    
    /**
     * 
     * @param objectElement
     *            the JDOM Element defining the plugin configuration
     * @param pluginClass
     *            the class to instantiate
     * @param skipChildElements
     *            <code>false</code> to recurse the configuration,
     *            <code>true</code> otherwise
     * @return fully configured Object
     * @see #configure(org.jdom.Element, Object, boolean)
     * @throws Exception
     *             if the plugin class cannot be instantiated, if the
     *             configuration fails
     */
    public Object configure(Element objectElement, String schemaApiPath,  Object motherClass, Class pluginClass) throws Exception {

       List<Element> oneBodys = new ArrayList<Element>();
       List<Object> objects = new ArrayList<Object>();
       if (RestUtil.isEmpty(schemaApiPath))
       {
           Object object = configure(objectElement, pluginClass, motherClass);
           objects.add(object);
           // 设置body方法
           addBody(motherClass, objects, pluginClass);
           return motherClass;
           
       }
       
       // 进行schema api解析
       RestApiHelper restApi = getRestApiHelperFromElement(objectElement, schemaApiPath);
       restApi.getSetMothod();
       
       Map<String, String> setMethodValue = restApi.getSetMothod();
       oneBodys.addAll(restApi.getChildBodys());

       // 读取每个body的方法
       Object pluginInstance = null;
       for(Element body : oneBodys)
       {
           pluginInstance = configure(body, pluginClass, motherClass);
           if (null != pluginInstance){
               objects.add(pluginInstance);
           }
       }
       
       // 设置body方法
       addBody(motherClass, objects, pluginClass);
       
       // 设置set方法
       setMothod(motherClass, setMethodValue);

       return motherClass;
       
    }
 
    /***
     * 获取xml中的解析对象
     * @param objectElement
     * @param schemaApiPath
     * @return
     */
    public RestApiHelper getRestApiHelperFromElement(JSONObject objectElement, String schemaApiPath) {
        
        
        RestApiHelper root = new RestApiHelper();
        String[] schemaApiPaths = schemaApiPath.split("[/]");
        JSONObject rootElement = objectElement;
        JSONArray jsonArray = null;
        String apiPath  = "";
        root.setFullUrlPath(schemaApiPath);
        boolean isRoot = true;
        for(int j = 0; j < schemaApiPaths.length; j++)
        {
            apiPath = schemaApiPaths[j];
            if (RestUtil.isEmpty(apiPath))
            {
                continue;
            }
            
            String elementName = "";
            String elementAttrib = "";
            String attribName = "";
            String attribValue = "";
            String[] elementArg = apiPath.split("[?]");
            if (elementArg.length == 1)
            {
                elementName = apiPath;
            } else if (elementArg.length == 2) {
                elementName = elementArg[0];
                elementAttrib = elementArg[1];
            }
            
            if(RestUtil.isNotEmpty(elementAttrib))
            {
                String[] lstArg = elementAttrib.split("[&]");
                for(String strParam: lstArg)
                {
                    String[] nameValue = strParam.split("[=]");
                    if (nameValue.length == 2)
                    {
                        attribName = nameValue[0];
                        attribValue = nameValue[1];
                        root.addMothod(attribName, attribValue);
                    }
                }
            }
            
            // 如果是根结点
            if (isRoot)
            {
                if (!rootElement.has(elementName))
                {
                    break;
                }
 
                jsonArray = rootElement.getJSONArray(elementName);
                isRoot = false;
                continue; 
            }
            
            // 获取node 节点列表
            if(jsonArray.size() > 0)
            {
                if(j + 1 == schemaApiPaths.length)
                {
                    for(int i =0; i < jsonArray.size(); i++)
                    {
                        JSONObject node = jsonArray.getJSONObject(i);
                        JSONArray jsonArrayChild = node.getJSONArray(elementName);
                        if (jsonArrayChild != null)
                        {
                            root.addBody(jsonArrayChild);
                        }
                    }
                    
                } else {
                    rootElement = jsonArray.getJSONObject(0);
                    jsonArray = rootElement.getJSONArray(elementName);
                }
            }
        }  
        
        return root;
    }
    
    
    /***
     * 获取xml中的解析对象
     * @param objectElement
     * @param schemaApiPath
     * @return
     */
    public RestApiHelper getRestApiHelperFromElement(Element objectElement, String schemaApiPath) {
        
        
        RestApiHelper root = new RestApiHelper();
        String[] schemaApiPaths = schemaApiPath.split("[/]");
        Element rootElement = objectElement;
        String apiPath  = "";
        root.setFullUrlPath(schemaApiPath);
        boolean isRoot = true;
        for(int j = 0; j < schemaApiPaths.length; j++)
        {
            apiPath = schemaApiPaths[j];
            if (RestUtil.isEmpty(apiPath))
            {
                continue;
            }
            
            String elementName = "";
            String elementAttrib = "";
            String attribName = "";
            String attribValue = "";
            String[] elementArg = apiPath.split("[?]");
            if (elementArg.length == 1)
            {
                elementName = apiPath;
            } else if (elementArg.length == 2) {
                elementName = elementArg[0];
                elementAttrib = elementArg[1];
            }
            
            if(RestUtil.isNotEmpty(elementAttrib))
            {
                String[] lstArg = elementAttrib.split("[&]");
                for(String strParam: lstArg)
                {
                    String[] nameValue = strParam.split("[=]");
                    if (nameValue.length == 2)
                    {
                        attribName = nameValue[0];
                        attribValue = nameValue[1];
                        root.addMothod(attribName, attribValue);
                    }
                }
            }
            
            // 如果是根结点
            if (isRoot)
            {
                
                if (!StringUtils.equals(elementName, rootElement.getNodeName()))
                {
                    break;
                }
 
                isRoot = false;
                continue; 
            }
            
            
            // 获取node 节点列表
            NodeList lstChildNode = rootElement.getElementsByTagName(elementName);
            if(lstChildNode.getLength() > 0)
            {
                if(j + 1 == schemaApiPaths.length)
                {
                    for(int i =0; i < lstChildNode.getLength(); i++)
                    {
                        Node node = lstChildNode.item(i);
                        Element element = (Element) node;
                        root.addBody(element);
                    }
                
                } else {
                    Node node = lstChildNode.item(0);
                    rootElement = (Element) node;
                }
            }
        }  
        
        return root;
        
    }
    
    /***
     * 增加body
     * @param motherClass
     * @param objects
     * @param pluginClass
     * @throws Exception
     */
    public void addBody(Object motherClass,    List<Object> objects, Class pluginClass) throws Exception {

        // 增加body
        Method addBody = motherClass.getClass().getMethod("addBody",
                pluginClass);
 
        // 增加Add方法， &&(childObject instanceof pluginClass.type)
        for (Object childObject : objects) {
            
            if (addBody != null) {
                try {
                    LOG.debug("treating child with adder " + " adding ");
                    addBody.invoke(motherClass, new Object[] { childObject });
                } catch (Exception e) {
                    LOG.fatal("Error configuring plugin.", e);
                }
            } else {
//                throw new Exception(
//                        "Nested element: 'addBody' is not supported for the <"
//                                + childObject + "> tag.");
            }
        }
    }
    

    /***
     * 设置mothod
     * @param motherClass
     * @param setMethod
     * @throws Exception
     */
    public void setMothod(Object motherClass, Map<String, String> setMethod) throws Exception {

        // 获取set所有的操作
        Map setters = new HashMap();
        Method[] methods = motherClass.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            final String name = method.getName();

            if (name.startsWith("set")) {
                setters.put(name.substring("set".length()).toLowerCase(),
                        method);
            }
        }
     
        // 设置set方法
        for (String key : setMethod.keySet()) {
            String value = setMethod.get(key);
            if (RestUtil.isNotEmpty(key)) {
                callSetter(key, value, setters, motherClass);
            }
        }
    }

    /**
     * Instantiate a plugin
     * 
     * @param pluginClass
     * @return The instantiated plugin
     * @throws Exception
     *             if the plugin class cannot be instantiated
     */
    private Object instantiatePlugin(Class pluginClass, Object motherClass) throws Exception {
        Object pluginInstance = null;

        try {
             ////System.out.println(pluginClass);   
              //查看class是否有构造函数   
              ////System.out.println(pluginClass.getConstructors().length);   
              //获取第一个构造函数   
              ////System.out.println(pluginClass.getConstructors()[0]);   
              //用构造函数初始化内部类   
              pluginInstance = pluginClass.getConstructors()[0].newInstance(motherClass);   
            //pluginInstance = Class.forName(pluginClass.toString());
            //Class<?> cls = Class.forName("package.ClassName$InnerClass");
            //pluginInstance = pluginClass.getConstructor(new Class[] {})
            //        .newInstance(new Object[] {});
        } catch (Exception e) {
            LOG.fatal("Could not instantiate class", e);
            e.printStackTrace();
//            throw new Exception("Could not instantiate class: "
//                    + pluginClass.getName());
            
        }
        return pluginInstance;
    }

    
    public Object configure(JSONArray objectElement, Class pluginClass, Object motherClass)
            throws Exception {

        Object pluginInstance = instantiatePlugin(pluginClass, motherClass);
        LOG.debug("configure " + pluginClass + " instance "
                + pluginInstance.getClass() + " self configuring: "
                + (pluginInstance instanceof SelfConfiguringPlugin));

        if (pluginInstance instanceof SelfConfiguringPlugin) {
            ((SelfConfiguringPlugin) pluginInstance).configure(objectElement);
        } else {
            configureObject(objectElement, pluginInstance);
        }
        return pluginInstance;
    }
    
    /**
     * Same as {@link #configure(org.jdom.Element, Class, boolean)}, except that
     * the client already has a pluginInstance.
     * 
     * @throws Exception
     *             if the configuration fails
     */
    public Object configure(Element objectElement, Class pluginClass, Object motherClass)
            throws Exception {

        Object pluginInstance = instantiatePlugin(pluginClass, motherClass);
        LOG.debug("configure " + objectElement.getNodeName() + " instance "
                + pluginInstance.getClass() + " self configuring: "
                + (pluginInstance instanceof SelfConfiguringPlugin));

        if (pluginInstance instanceof SelfConfiguringPlugin) {
            ((SelfConfiguringPlugin) pluginInstance).configure(objectElement);
        } else {
            configureObject(objectElement, pluginInstance);
        }
        return pluginInstance;
    }

    /**
     * Configure the specified plugin object given the JDOM Element defining the
     * plugin configuration.
     * 
     * <ul>
     * <li>calls setters that corresponds to element attributes</li>
     * <li>calls <code>public Yyy createXxx()</code> methods that corresponds to
     * non-plugins child elements (i.e. known by the instance class). The
     * returned instance must be assignable to the Yyy type</li>
     * <li>calls <code>public void add(Xxx)</code> methods that corresponds to
     * child elements which are plugins themselves, e.g. which will require
     * asking the ProjectXMLHelper to
     * {@link ProjectXMLHelper#configurePlugin(org.jdom.Element, boolean)
     * configure the plugin}</li>
     * </ul>
     * 
     * @param objectElement
     *            the JDOM Element defining the plugin configuration
     * @param object
     *            the instance to configure to instantiate
     * @param skipChildElements
     *            <code>false</code> to recurse the configuration,
     *            <code>true</code> otherwise
     */
    protected void configureObject(Element objectElement, Object object)
            throws Exception {

        LOG.debug("configuring object " + objectElement.getNodeName()
                + " object " + object.getClass());

        Map<String, Method> setters = getSetters(object);

        // 设置 set 方法
        setFromElement(objectElement, setters, object);

    }
    

    public Map<String, Method> getGetters(Object object)
    {
        Map<String, Method> getters = new HashMap<String, Method>();

        Method[] methods = object.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            final String name = method.getName();

            if (name.startsWith("get")) {
                getters.put(name.substring("get".length()).toLowerCase(),
                        method);
            }  
        }

        return getters;
    }
    
    public Map<String, Method> getSetters(Object object)
    {
        Map<String, Method> setters = new HashMap<String, Method>();

        Method[] methods = object.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            final String name = method.getName();

            if (name.startsWith("set")) {
                setters.put(name.substring("set".length()).toLowerCase(),
                        method);
            } else if (name.startsWith("create")) {
                // creators.put(name.substring("create".length()).toLowerCase(),
                // method);
            } else if (name.equals("add")
                    && method.getParameterTypes().length == 1) {
                // adders.add(method);
            }
        }

        return setters;
    }
    
    
    protected void configureObject(JSONArray objectElement, Object object)
            throws Exception {

        LOG.debug("configuring object " + object.getClass());

        Map<String, Method> setters = getSetters(object);
        // 设置 set 方法
        setFromElement(objectElement, setters, object);

    }
    
    private void setFromElement(JSONArray objectElement, Map<String, Method> setters,
            Object object) throws Exception {

        for(int i = 0; i < objectElement.size(); i++)
        {
            JSONObject jobject = objectElement.getJSONObject(i);
            for(Object key : jobject.keySet())
            {
                String keyValue = key.toString();
                String value = jobject.getString(keyValue);
                callSetter(keyValue, value, setters,
                        object);
            }
        }
    }
    
    private void setFromElement(Element objectElement, Map<String, Method> setters,
            Object object) throws Exception {

        NodeList list = objectElement.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType() == node.ELEMENT_NODE) {
                if (StringUtils.isNotBlank(node.getTextContent()))
                {
                    callSetter(node.getNodeName(), node.getTextContent(), setters,
                            object);
                }
            }
        }
    }

    private boolean validateString(String source) {

        if (source != null && source.length() > 0) {
            return true;
        }
        return false;
    }

    /****
     * 设置当前类的set方法
     * 
     * @param propName  IdentifyIndex
     * @param getters  getters
     * @param object
     * @throws Exception
     */
    public String callGetter(String propName, Map<String, Method> getters, Object object)   {

        if (getters.containsKey(propName.toLowerCase())) {
            LOG.debug("getting " + propName.toLowerCase() );
            try {
                Method method = (Method) getters.get(propName.toLowerCase());
                Class[] parameters = method.getParameterTypes();
                if (parameters.length == 0)
                {
                    Object retGet = method.invoke(object, new Object[] { });
                    return retGet.toString();
                }
                
            } catch (Exception e) {
                LOG.error("Error configuring plugin.", e);
            }
        } else {
            LOG.info("Attribute: '" + propName + "' is not supported for class: '" + object.getClass().getName() + "'.");
            //throw new Exception("Attribute: '" + propName
            //        + "' is not supported for class: '"
            //        + object.getClass().getName() + "'.");
        }
        
        return null;
    }
    
    /****
     * 设置当前类的set方法
     * 
     * @param propName
     * @param propValue
     * @param setters
     * @param object
     * @throws Exception
     */
    private void callSetter(String propName, String propValue, Map<String, Method> setters,
            Object object) throws Exception {

        if (setters.containsKey(propName.toLowerCase())) {
            LOG.debug("Setting " + propName.toLowerCase() + " to " + propValue);
            try {
                Method method = (Method) setters.get(propName.toLowerCase());
                Class[] parameters = method.getParameterTypes();
                if (String.class.isAssignableFrom(parameters[0])) {
                    method.invoke(object, new Object[] { propValue });
                } else if (int.class.isAssignableFrom(parameters[0])) {
                    if (validateString(propValue)) {

                        try {
                            Integer tmpValue = Integer.valueOf(propValue);
                            method.invoke(object,
                                    new Object[] { Integer.valueOf(propValue) });
                        } catch (NumberFormatException e) {
                            LOG.error("Error configuring plugin.", e);
                        }

                    }

                } else if (long.class.isAssignableFrom(parameters[0])) {

                    if (validateString(propValue)) {

                        try {
                            Long tmpValue = Long.valueOf(propValue);
                            method.invoke(object,
                                    new Object[] { Long.valueOf(propValue) });
                        } catch (NumberFormatException e) {
                            LOG.error("Error configuring plugin.", e);
                        }
                    }

                } else if (boolean.class.isAssignableFrom(parameters[0])) {
                    method.invoke(object,
                            new Object[] { Boolean.valueOf(propValue) });

                } else {
                    LOG.error("rCouldn't invoke setter "
                            + propName.toLowerCase());
                }
            } catch (Exception e) {
                LOG.error("Error configuring plugin.", e);
            }
        } else {
            LOG.info("Attribute: '" + propName + "' is not supported for class: '" + object.getClass().getName() + "'.");
            //throw new Exception("Attribute: '" + propName
            //        + "' is not supported for class: '"
            //        + object.getClass().getName() + "'.");
        }
    }
    

}
