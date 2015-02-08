package com.huawei.agilete.base.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.huawei.agilete.base.bean.Device;
import com.huawei.agilete.northinterface.dao.OTDomainDAO;
import com.huawei.networkos.ops.client.IOpsRestCaller;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;

public class MyIfmInterface extends OpsInterface {
	public String url = "/ifm/interfaces/interface";
	//public String url = "/ifm/interfaces/interface?ifName,ifPhyType&ifmAm4/am4CfgAddrs/am4CfgAddr?ifIpAddr,subnetMask";
	public String fullUrl = "/ifm/interfaces/interface?ifName=%s";
	public String param="ifName"; //ifIndex *****************************************************************

	public MyIfmInterface(IOpsRestCaller restcall){
		super(restcall);
		super.url = url;
		super.fullUrl = fullUrl;
		super.param = param;
	}

	private String[] analyticInterfaces(Element _interface){
		String [] ri= new String[]{"","","",""};
		ri[0]=_interface.elementText("ifPhyType");
		ri[1]=_interface.elementText("ifName");
		Element ifmAm4 = _interface.element("ifmAm4");
		if(null == ifmAm4){
			return null;
		}
		for(Iterator<Element> j=ifmAm4.elementIterator();j.hasNext();){
			Element am4CfgAddrs = j.next();
			if(null != am4CfgAddrs){
				for(Iterator<Element> k=am4CfgAddrs.elementIterator();k.hasNext();){
					Element am4CfgAddr = k.next();
					ri[2]=am4CfgAddr.elementText("ifIpAddr");
					ri[3]=am4CfgAddr.elementText("subnetMask");
				}
			}
		}
		return ri;
	}
	public List<String[]> getAll(String type){
		super.url = "/ifm/interfaces/interface?ifName,ifPhyType&ifmAm4/am4CfgAddrs/am4CfgAddr?ifIpAddr,subnetMask";
		RetRpc resultOps = super.getall();
		List<String[]> resultInterfaces = new ArrayList<String[]>();
		if(resultOps.getStatusCode() != 200){
			return resultInterfaces;
		}

		try {
			Document doc = DocumentHelper.parseText(resultOps.getContent());
			Element ele = doc.getRootElement();
			Element interfaces = ele.element("interfaces");
			for(Iterator<Element> i=interfaces.elementIterator();i.hasNext();){
				Element _interface = i.next();
				if(_interface.elementText("ifPhyType").equalsIgnoreCase(type)){
					String [] ri = analyticInterfaces(_interface);
					if(null != ri){
						resultInterfaces.add(ri);
					}
				}else if(null == type || "".equals(type)){
					String [] ri = analyticInterfaces(_interface);
					if(null != ri){
						resultInterfaces.add(ri);
					}
				}else{
					continue;
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			return resultInterfaces;
		}
		return resultInterfaces;
	}
	public void getAll(Device device){
		RetRpc resultOps = super.getall();
		List<String[]> resultInterfaces = new ArrayList<String[]>();
		String[] loopBack = new String[3]; 
		String[] gigabitEthernet = new String[3];
		try {
			Document doc = DocumentHelper.parseText(resultOps.getContent());
			Element ele = doc.getRootElement();
			Element interfaces = ele.element("interfaces");

			for(Iterator<Element> i=interfaces.elementIterator();i.hasNext();){
				Element _interface = i.next();
				if(_interface.elementText("ifPhyType").equalsIgnoreCase("Ethernet")){
					String [] ri = analyticInterfaces(_interface);
					if(null != ri){
						resultInterfaces.add(ri);
					}
				}else if(_interface.elementText("ifPhyType").equalsIgnoreCase("Vlanif")){
					String [] ri = analyticInterfaces(_interface);
					if(null != ri){
						gigabitEthernet[0]=ri[1];
						gigabitEthernet[1]=ri[2];
						gigabitEthernet[2]=ri[3];
					}
				}else{
					continue;
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		device.setInterfaces(resultInterfaces);
		device.setGigabitEthernet(gigabitEthernet);
		device.setLoopBack(loopBack);
	}

	public  HashMap<String, String> getFlux(String[] content){
		HashMap<String, String> flux = new HashMap<String, String>();
		if(null == content){
			super.url = "/ifm/interfaces/interface?ifName,ifDynamicInfo&ifStatistics?receiveByte,sendByte";
		}
		//[(delta(receibveByte)+delta(sendByte))]/(ifoperspeed*delta(time(s)) * 8)
		RetRpc retRpc  = super.get(content);
		if(200 == retRpc.getStatusCode() && !"".equals(retRpc.getContent())){

			try {
				Document doc = DocumentHelper.parseText(retRpc.getContent());
				Element ele = doc.getRootElement();
				Element interfaces = ele.element("interfaces");
				for(Iterator<Element> i=interfaces.elementIterator();i.hasNext();){
					StringBuffer result = new StringBuffer();
					Element _interface = i.next();
					Element ifStatistics = _interface.element("ifStatistics");
					Element ifDynamicInfo = _interface.element("ifDynamicInfo");
					if(null != ifStatistics && null != ifDynamicInfo){
						if(null != ifStatistics.elementText("receiveByte") && !"".equals(ifStatistics.elementText("receiveByte"))){
							result.append(ifStatistics.elementText("receiveByte")).append("_");
						}
						if(null != ifStatistics.elementText("sendByte") && !"".equals(ifStatistics.elementText("sendByte"))){
							result.append(ifStatistics.elementText("sendByte")).append("_");
						}
						if(null != ifDynamicInfo.elementText("ifOperSpeed") && !"".equals(ifDynamicInfo.elementText("ifOperSpeed"))){
							result.append(ifDynamicInfo.elementText("ifOperSpeed")).append("_");
						}
						result.append(System.currentTimeMillis());
						flux.put(_interface.elementText("ifName"), result.toString());
					}
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
	public static void main(String[] args) {
		OpsRestCaller client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer("2"), "46");
		MyIfmInterface e  = new MyIfmInterface(client);
		HashMap<String, String> result = e.getFlux(null);
		System.out.println(result);
	}

}
