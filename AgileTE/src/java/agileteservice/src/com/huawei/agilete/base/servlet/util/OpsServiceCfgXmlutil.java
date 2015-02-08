package com.huawei.agilete.base.servlet.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/***
 * ops服务管理
 * 
 * @author sWX203247
 * 
 */
public class OpsServiceCfgXmlutil {

    private Map<String, OpsDevice> opsDevices = new HashMap<String, OpsDevice>();
    private String OpsServiceConfigXmlPath;// 配置文件路径
    Document document;

    public OpsServiceCfgXmlutil(String OpsServiceConfigXmlPath) {
        this.OpsServiceConfigXmlPath = OpsServiceConfigXmlPath;
        this.init();
    }

    private void init() // 加载xml文件
    {
        File file = new File(OpsServiceConfigXmlPath);
        if (!file.exists() && !file.isFile()) {
            //System.out.println("can not find file:" + file.getAbsolutePath());
            return;
        }
        try {
            document = getDoc(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // create a document from given string
    public static Document getDoc(File file) throws ParserConfigurationException, IOException, SAXException {

        Document doc = null;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        doc = builder.parse(file);

        return doc;
    }

    /**
     * 获取指定名称的device
     * @param deviceName
     * @return
     */
    public OpsDevice getDeivceById(String deviceId)
    {
        return opsDevices.get(deviceId);
    } 
    

    /**
     * 获取指定名称的device
     * @param deviceName
     * @return
     */
    public OpsDevice getDeivceByName(String deviceName)
    {
        OpsDevice retDevice = null;
        Set<String> lstKey = opsDevices.keySet();
        for(String key :lstKey)
        {
            retDevice = opsDevices.get(key);
            if (deviceName != null &&deviceName.equalsIgnoreCase(retDevice.getDeviceName()))
            {
                return retDevice;
            }
        }
        return retDevice;
    } 
    /**
     * 获取所有的device的列表map形式
     * 
     * @return
     */
    public void parseDevicesmap() {
        
        if (null == document)
        {
            return;
        }
        
        Element element = document.getDocumentElement();
        NodeList elements = element.getElementsByTagName("device");
        for (int i = 0; i < elements.getLength(); i++) {

            Node node = elements.item(i);
            Element ele = (Element) node;
            String deviceName = "";
            String sname = "";
            String userName = "";
            String id = "";
            String version = "";
            String productType = "";
            String server = "";
            String port = "";
            String passwd = "";

            NodeList childElements = ele.getChildNodes();
            for (int j = 0; j < childElements.getLength(); j++) {
                String name = childElements.item(j).getNodeName();
                String value = childElements.item(j).getTextContent();
                if ("name".equals(name)) {
                    sname = value;
                } else if ("deviceName".equals(name)) {
                    deviceName = value;
                } else if ("server".equals(name)) {
                    server = value;
                } else if ("port".equals(name)) {
                    port = value;
                } else if ("userName".equals(name)) {
                    userName = value;
                } else if ("passwd".equals(name)) {
                    passwd = value;
                } else if ("id".equals(name)) {
                    id = value;
                } else if ("version".equals(name)) {
                    version = value;
                } else if ("productType".equals(name)) {
                    productType = value;
                }

            }
             
            if (sname != null && sname.length() > 0) {
                OpsDevice device = new OpsDevice(sname, deviceName, server,
                        Integer.parseInt(port), userName, passwd, id,  version,
                        productType);
                opsDevices.put(id, device);
            }

        }
    }
}
