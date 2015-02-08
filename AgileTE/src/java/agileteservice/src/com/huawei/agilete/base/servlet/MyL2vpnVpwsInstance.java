package com.huawei.agilete.base.servlet;

import java.util.HashMap;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.huawei.agilete.northinterface.bean.OTFlow;
import com.huawei.networkos.ops.client.IOpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;

public class MyL2vpnVpwsInstance extends OpsInterface {
     public String url = "/l2vpn/l2vpnvpws/vpwsInstances/vpwsInstance";
     //原来fullurl原来有instanceType条件，因为加上无法查询到数据，所以去除public String fullUrl = "/l2vpn/l2vpnvpws/vpwsInstances/vpwsInstance?instanceName=%s&instanceType=%s";
     public String fullUrl = "/l2vpn/l2vpnvpws/vpwsInstances/vpwsInstance?instanceName=%s";
     public String param="instanceName=%s&instanceType=%s";
     
    public MyL2vpnVpwsInstance(IOpsRestCaller restcall){
        super(restcall);
        super.url = url;
        super.fullUrl = fullUrl;
        super.param = param;
    }

    public RetRpc deleteEnable(String[] content){
    	super.body = "<instanceType>vpwsLdp</instanceType><encapsulateType>vlan</encapsulateType><vpwsPws><vpwsPw><pwRole>primary</pwRole><trafficStatisticsEnable>false</trafficStatisticsEnable></vpwsPw></vpwsPws>";
    	return super.modify(content);
    }
    
    public RetRpc delete(String[] content){
//        RetRpc result = new RetRpc();
        super.fullUrl = "/l2vpn/l2vpnvpws/vpwsInstances/vpwsInstance?instanceName=%s&instanceType=%s";
        return super.delete(content);
    }
    
    public HashMap<String, String> getFlux(String[] content){
    	HashMap<String, String> flux = new HashMap<String, String>();
    	if(null == content){
            super.url = "/l2vpn/l2vpnvpws/vpwsInstances/vpwsInstance";
        }
    	RetRpc retRpc  = super.get(content);
        if(200 == retRpc.getStatusCode() && !"".equals(retRpc.getContent())){

            try {
                Document doc = DocumentHelper.parseText(retRpc.getContent());
                Element ele = doc.getRootElement();
                Element interfaces = ele.element("l2vpnvpws").element("vpwsInstances");
                for(Iterator<Element> i=interfaces.elementIterator();i.hasNext();){
                    StringBuffer result = new StringBuffer();
                    Element domain = i.next();

                    if(null != domain.element("vpwsPws") && null != domain.element("vpwsPws").element("vpwsPw") && null != domain.element("vpwsPws").element("vpwsPw").element("vpwsLdpPwTraffStat")){
                    	Element nextdomain = domain.element("vpwsPws").element("vpwsPw").element("vpwsLdpPwTraffStat");
//                        for(Iterator<Element> j=nextdomain.elementIterator();j.hasNext();){
//                            Element vpwsPw = j.next();
                            result.append(nextdomain.elementText("inputBytes")).append("_");
                            result.append(nextdomain.elementText("outputBytes")).append("_");
//                        }
                    }
                    result.append("1").append("_");
                    result.append(System.currentTimeMillis());
                    flux.put(domain.elementText("instanceName"), result.toString());
                }
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
    	return flux;
    }
    /**
     * @param args
     */
/*    public static void main(String[] args) {
        new HttpRequest();
        MyL2vpnVpwsInstance e  = new MyL2vpnVpwsInstance("2");
        //e.url = "/mpls/mplsTe/rsvpTeTunnels/rsvpTeTunnel";
        //e.getall();
        String aa = "<instanceName>spring3</instanceName>                <instanceType>vpwsLdp</instanceType>                <encapsulateType>vlan</encapsulateType>                <l2vpnAcs>                    <l2vpnAc operation=\"merge\">                        <interfaceName>ether0/1/9</interfaceName>                        <tagged>true</tagged>                        <accessPort>false</accessPort>                    </l2vpnAc>                </l2vpnAcs>                <vpwsPws>                    <vpwsPw operation=\"merge\">                        <pwRole>primary</pwRole>                        <pwId>100</pwId>                        <peerIp>1.1.1.1</peerIp>                        <tnlPolicyName>tp1</tnlPolicyName>                    </vpwsPw>                </vpwsPws>";
        String deleteaa = "<explicitPathName>main113</explicitPathName>";
        
        e.body = aa;
        //System.out.println(e.create(null)(new String[]{"spring3","vpwsLdp"}));
    }*/

}
