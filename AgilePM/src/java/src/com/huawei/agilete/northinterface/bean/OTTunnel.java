package com.huawei.agilete.northinterface.bean;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.huawei.agilete.base.common.MyIO;
import com.huawei.agilete.base.servlet.MyMplsExplicitPath;
import com.huawei.agilete.data.MyData;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.templet.VMTempletManager;

public class OTTunnel {

	private String name = "";
	private String identifyIndex = "";
	private String interfaceName = "";
	private String ingressIp = "";  //出口IP
	private String egressIp = ""; //入口IP
	private String hotStandbyTime = "";
	private String isDouleConfig = "";
	private String desDeviceName = "";
	private String state = "";
	private String statePri = "";
	private String stateBack = "";
	private String whereFlow = "";
	private String deviceId = "";

	private List<OTTunnel> tunnels = new ArrayList<OTTunnel>();
	private List<OTTunnelPath> tunnelPaths = new ArrayList<OTTunnelPath>();
	private List<OTPath> paths = new ArrayList<OTPath>();

	private String check = "";

	public OTTunnel(){

	}
	public OTTunnel(String content){
		try {
			Document doc = DocumentHelper.parseText(content);
			Element el = doc.getRootElement();
			if("tunnels".equals(el.getName())){
				for(Iterator<Element> i=el.elementIterator();i.hasNext();){
					OTTunnel tunnel = new OTTunnel();
					Element domain = i.next();
					if(null != domain.elementText("name") && !"".equals(domain.elementText("name"))){
						tunnel.setName(domain.elementText("name"));
						tunnel.setInterfaceName(domain.elementText("name"));
					}
					//					if(null != domain.elementText("interfaceName") && !"".equals(domain.elementText("interfaceName"))){
					//						tunnel.setInterfaceName(domain.elementText("interfaceName"));
					//					}
					if(null != domain.elementText("identifyIndex") && !"".equals(domain.elementText("identifyIndex"))){
						tunnel.setIdentifyIndex(domain.elementText("identifyIndex"));
					}
					if(null != domain.elementText("ingressIp") && !"".equals(domain.elementText("ingressIp"))){
						tunnel.setIngressIp(domain.elementText("ingressIp"));
					}
					if(null != domain.elementText("egressIp") && !"".equals(domain.elementText("egressIp"))){
						tunnel.setEgressIp(domain.elementText("egressIp"));
					}
					if(null != domain.elementText("hotStandbyTime") && !"".equals(domain.elementText("hotStandbyTime"))){
						tunnel.setHotStandbyTime(domain.elementText("hotStandbyTime"));
					}
					if(null != domain.elementText("isDouleConfig") && !"".equals(domain.elementText("isDouleConfig"))){
						tunnel.setIsDouleConfig(domain.elementText("isDouleConfig"));
					}
					if(null != domain.elementText("desDeviceName") && !"".equals(domain.elementText("desDeviceName"))){
						tunnel.setDesDeviceName(domain.elementText("desDeviceName"));
					}
					if(null != domain.elementText("deviceId") && !"".equals(domain.elementText("deviceId"))){
						tunnel.setDeviceId(domain.elementText("deviceId"));
					}

					Element ElTunnelPaths =domain.element("tunnelPaths");
					if(null != ElTunnelPaths){
						tunnel.tunnelPaths = new ArrayList<OTTunnelPath>();

						for(Iterator<Element> j=ElTunnelPaths.elementIterator();j.hasNext();){
							Element tunnelPath = j.next();
							OTTunnelPath oTTunnelPath = new OTTunnelPath();
							if(null != tunnelPath.elementText("pathType") && !"".equals(tunnelPath.elementText("pathType"))){
								String value = "";
								if("backup".equals(tunnelPath.elementText("pathType"))){
									value = "hot_standby";
								}else{
									value = "primary";
								}


								oTTunnelPath.setPathType(value);
							}
							if(null != tunnelPath.elementText("pathName") && !"".equals(tunnelPath.elementText("pathName"))){
								oTTunnelPath.setPathName(tunnelPath.elementText("pathName"));
							}
							tunnel.tunnelPaths.add(oTTunnelPath);
						}
					}else{

					}


					Element ElPaths =domain.element("paths");
					if(null != ElPaths){
						//paths = new ArrayList<OTPath>();

						for(Iterator<Element> j=ElPaths.elementIterator();j.hasNext();){
							Element ElPath = j.next();
							OTPath oTPath = new OTPath();
							if(null != ElPath.elementText("name") && !"".equals(ElPath.elementText("name"))){
								oTPath.setName(ElPath.elementText("name"));
							}

							Element nextHops =ElPath.element("nextHops");
							if(null != nextHops){
								for(Iterator<Element> k=nextHops.elementIterator();k.hasNext();){
									Element nextHop = k.next();
									OTPathHop oTPathHop = new OTPathHop();
									if(null != nextHop.elementText("id") && !"".equals(nextHop.elementText("id"))){
										oTPathHop.setId(nextHop.elementText("id"));
									}
									if(null != nextHop.elementText("nextIp") && !"".equals(nextHop.elementText("nextIp"))){
										oTPathHop.setNextIp(nextHop.elementText("nextIp"));
									}
									oTPath.getPathHops().add(oTPathHop);
								}
							}


							tunnel.paths.add(oTPath);
						}
					}
					tunnels.add(tunnel);
				}
			}else{
				return;
			}
		} catch (DocumentException e) {
			//e.printStackTrace();
		}
	}

	public List<OTTunnel> parseOpsToUi(String content,OpsRestCaller client,Boolean allNot){
		List<OTTunnel> list = new ArrayList<OTTunnel>();
		try {
			Document doc = DocumentHelper.parseText(content);
			Element el = doc.getRootElement();
			Element ele = el.element("mplsTe").element("rsvpTeTunnels");

			for(Iterator<Element> i=ele.elementIterator();i.hasNext();){
				OTTunnel oTTunnel = new OTTunnel();
				Element domain = i.next();
				if(null != domain.elementText("tunnelName") && !"".equals(domain.elementText("tunnelName"))){
					oTTunnel.setName(domain.elementText("tunnelName"));
					setInterfaceName(domain.elementText("name"));
				}
				//				if(null != domain.elementText("interfaceName") && !"".equals(domain.elementText("interfaceName"))){
				//					oTTunnel.setInterfaceName(domain.elementText("interfaceName"));
				//				}
				if(null != domain.elementText("mplsTunnelIndex") && !"".equals(domain.elementText("mplsTunnelIndex"))){
					oTTunnel.setIdentifyIndex(domain.elementText("mplsTunnelIndex"));
				}
				if(null != domain.elementText("mplsTunnelIngressLSRId") && !"".equals(domain.elementText("mplsTunnelIngressLSRId"))){
					oTTunnel.setIngressIp(domain.elementText("mplsTunnelIngressLSRId"));
				}
				if(null != domain.elementText("mplsTunnelEgressLSRId") && !"".equals(domain.elementText("mplsTunnelEgressLSRId"))){
					oTTunnel.setEgressIp(domain.elementText("mplsTunnelEgressLSRId"));
				}
				if(null != domain.elementText("hotStandbyWtr") && !"".equals(domain.elementText("hotStandbyWtr"))){
					oTTunnel.setHotStandbyTime(domain.elementText("hotStandbyWtr"));
				}

				//state
				String tunnelState = "";
				if(null != domain.elementText("tunnelState") && !"".equals(domain.elementText("tunnelState"))){
					tunnelState = domain.elementText("tunnelState");
				}
				String workingLspType = "";
				if(null != domain.elementText("workingLspType") && !"".equals(domain.elementText("workingLspType"))){
					workingLspType = domain.elementText("workingLspType");
					oTTunnel.setWhereFlow(workingLspType);
				}
				//主处理状态，TE处于热备份状态，TE处于回切状态 primary hot_standby  turnround   
				if("hot_standby".equalsIgnoreCase(workingLspType)){
					if("AllPathReady".equalsIgnoreCase(tunnelState) || "UP".equalsIgnoreCase(tunnelState)){
						oTTunnel.setState("turnround");
					}else if(!"AllPathReady".equalsIgnoreCase(tunnelState) && !"UP".equalsIgnoreCase(tunnelState)){
						oTTunnel.setState("hot_standby");
					}else{
					}
				}else if("Primary".equalsIgnoreCase(workingLspType)){
					if("AllPathReady".equalsIgnoreCase(tunnelState) || "UP".equalsIgnoreCase(tunnelState)){
						oTTunnel.setState("primary");
					}
				}else{
				}

				if("".equals(oTTunnel.getState())){
					oTTunnel.setState("DOWN");
				}




				//					if(null != domain.elementText("desDeviceName") && !"".equals(domain.elementText("desDeviceName"))){
				//						setDesDeviceName(domain.elementText("desDeviceName"));
				//					}

				Element ElTunnelPaths =domain.element("tunnelPaths");

				if(null != ElTunnelPaths){
					oTTunnel.tunnelPaths = new ArrayList<OTTunnelPath>();
				}
				for(Iterator<Element> j=ElTunnelPaths.elementIterator();j.hasNext();){
					Element tunnelPath = j.next();
					OTTunnelPath oTTunnelPath = new OTTunnelPath();
					if(null != tunnelPath.elementText("pathType") && !"".equals(tunnelPath.elementText("pathType"))){
						oTTunnelPath.setPathType(tunnelPath.elementText("pathType"));
					}
					if(null != tunnelPath.elementText("explicitPathName") && !"".equals(tunnelPath.elementText("explicitPathName"))){
						oTTunnelPath.setPathName(tunnelPath.elementText("explicitPathName"));
					}
					String lspState = "";
					if(null != tunnelPath.elementText("lspState") && !"".equals(tunnelPath.elementText("lspState"))){
						lspState = tunnelPath.elementText("lspState");
					}
					if("primary".equalsIgnoreCase(oTTunnelPath.getPathType())){
						oTTunnel.setStatePri(lspState);
					}else if("hot_standby".equalsIgnoreCase(oTTunnelPath.getPathType())){
						oTTunnel.setStateBack(lspState);
					}else{
					}

					oTTunnel.tunnelPaths.add(oTTunnelPath);
				}
				if("primary".equalsIgnoreCase(workingLspType)){
					oTTunnel.setStatePri(oTTunnel.getStatePri()+"(A)");
				}else if("hot_standby".equalsIgnoreCase(workingLspType)){
					oTTunnel.setStateBack(oTTunnel.getStateBack()+"(A)");
				}else{
				}
				if(allNot){
					List<OTPath> paths =new ArrayList<OTPath>();
					List<OTTunnelPath> TunnelPathsList =oTTunnel.getTunnelPaths();
					MyMplsExplicitPath myMplsExplicitPath = new MyMplsExplicitPath(client);
					for(int k=0;k<TunnelPathsList.size();k++){
						OTTunnelPath oTTunnelPath = TunnelPathsList.get(k);
						OTPath oTPath = new OTPath();
						oTPath.setName(oTTunnelPath.getPathName());

						String flagPath = myMplsExplicitPath.get(new String[]{oTTunnelPath.getPathName()}).getContent();
						try {
							Document doc1 = DocumentHelper.parseText(flagPath);
							Element el1 = doc1.getRootElement();
							Element ele1 = el1.element("mplsTe").element("explicitPaths");
							for(Iterator<Element> i1=ele1.elementIterator();i1.hasNext();){
								Element explicitPathHops = i1.next().element("explicitPathHops");
								if(null != explicitPathHops){
									for(Iterator<Element> i2=explicitPathHops.elementIterator();i2.hasNext();){
										Element explicitPathHop = i2.next();
										OTPathHop oTPathHop= new OTPathHop();
										if(null != explicitPathHop.elementText("mplsTunnelHopIndex") && !"".equals(explicitPathHop.elementText("mplsTunnelHopIndex"))){
											oTPathHop.setId(explicitPathHop.elementText("mplsTunnelHopIndex"));
										}
										if(null != explicitPathHop.elementText("mplsTunnelHopIpAddr") && !"".equals(explicitPathHop.elementText("mplsTunnelHopIpAddr"))){
											oTPathHop.setNextIp(explicitPathHop.elementText("mplsTunnelHopIpAddr"));
										}
										oTPath.getPathHops().add(oTPathHop);
									}
								}
							}
						} catch (DocumentException e1) {
							//e.printStackTrace();
						}
						paths.add(oTPath);
					}
					oTTunnel.setPaths(paths);
				}
				list.add(oTTunnel);
			}
		} catch (DocumentException e) {
			//e.printStackTrace();
		}



		return list;
	}


	public String getOTTunnelOpsMessage(){
		String result = "";
		VMTempletManager templet = VMTempletManager.getInstance();
		Map<String, Object> mapContext = null;
		mapContext = new LinkedHashMap<String, Object>();
		List<OTTunnel> oTTunnel = new ArrayList<OTTunnel>();
		oTTunnel.add(this);
		mapContext.put("T_Tunnel", oTTunnel);
		String templetPath = templet.getResource("/templet/agilete")	;
		StringWriter write = templet.process("TempleTunnel.tpl", mapContext, templetPath);
		result = write.toString();
		return result;
	}

	public String[] getOTPathOpsMessage(){
		List<OTPath> pathsList = getPaths();
		String[] result = new String[pathsList.size()];
		VMTempletManager templet = VMTempletManager.getInstance();
		Map<String, Object> mapContext = null;
		for(int i=0;i<pathsList.size();i++){
			mapContext = new LinkedHashMap<String, Object>();
			List<OTPath> paths = new ArrayList<OTPath>();
			paths.add(pathsList.get(i));
			mapContext.put("T_ExplicitPath", paths);
			String templetPath = templet.getResource("/templet/agilete")	;
			StringWriter write = templet.process("TempleExplicitPath.tpl", mapContext, templetPath);
			result[i] = write.toString();
		}

		return result;
	}

	public String getOTTunnelUiMessage(List<OTTunnel> list){
		String result = "";
		if(null == list || list.size() == 0){
			return result;
		}
		VMTempletManager templet = VMTempletManager.getInstance();
		Map<String, Object> mapContext = null;
		mapContext = new LinkedHashMap<String, Object>();
		List<OTTunnel> oTTunnel = new ArrayList<OTTunnel>();
		oTTunnel.add(this);
		mapContext.put("T_Tunnel", list);
		String templetPath = templet.getResource("/templet/agilete")	;
		StringWriter write = templet.process("UiTempleTunnel.tpl", mapContext, templetPath);
		result = write.toString();
		return result;
	}

	public String getOTTunnelPathUiMessage(List<OTPath> list){
		String result = "";
		if(null == list || list.size() == 0){
			return result;
		}
		VMTempletManager templet = VMTempletManager.getInstance();
		Map<String, Object> mapContext = null;
		mapContext = new LinkedHashMap<String, Object>();
		List<OTTunnel> oTTunnel = new ArrayList<OTTunnel>();
		oTTunnel.add(this);
		mapContext.put("T_TunnelPath", list);
		String templetPath = templet.getResource("/templet/agilete")	;
		StringWriter write = templet.process("UiTempleTunnelPath.tpl", mapContext, templetPath);
		result = write.toString();
		return result;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public String getIdentifyIndex() {
		return identifyIndex;
	}
	public void setIdentifyIndex(String identifyIndex) {
		this.identifyIndex = identifyIndex;
	}
	public String getIngressIp() {
		return ingressIp;
	}
	public void setIngressIp(String ingressIp) {
		this.ingressIp = ingressIp;
	}
	public String getEgressIp() {
		return egressIp;
	}
	public void setEgressIp(String egressIp) {
		this.egressIp = egressIp;
	}
	public String getHotStandbyTime() {
		return hotStandbyTime;
	}
	public void setHotStandbyTime(String hotStandbyTime) {
		this.hotStandbyTime = hotStandbyTime;
	}
	public String getIsDouleConfig() {
		return isDouleConfig;
	}
	public void setIsDouleConfig(String isDouleConfig) {
		this.isDouleConfig = isDouleConfig;
	}
	public String getDesDeviceName() {
		return desDeviceName;
	}
	public void setDesDeviceName(String desDeviceName) {
		this.desDeviceName = desDeviceName;
	}
	public List<OTTunnelPath> getTunnelPaths() {
		return tunnelPaths;
	}
	public void setTunnelPaths(List<OTTunnelPath> tunnelPaths) {
		this.tunnelPaths = tunnelPaths;
	}
	public List<OTPath> getPaths() {
		return paths;
	}
	public void setPaths(List<OTPath> paths) {
		this.paths = paths;
	}
	public String getCheck() {
		return check;
	}
	public void setCheck(String check) {
		this.check = check;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getStatePri() {
		return statePri;
	}
	public void setStatePri(String statePri) {
		this.statePri = statePri;
	}
	public String getStateBack() {
		return stateBack;
	}
	public void setStateBack(String stateBack) {
		this.stateBack = stateBack;
	}
	public String getWhereFlow() {
		return whereFlow;
	}
	public void setWhereFlow(String whereFlow) {
		this.whereFlow = whereFlow;
	}
	public List<OTTunnel> getTunnels() {
		return tunnels;
	}
	public void setTunnels(List<OTTunnel> tunnels) {
		this.tunnels = tunnels;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}







}
