package com.huawei.overte.handle
{
	
	import com.huawei.overte.handle.DataHandleTool;
	import com.huawei.overte.model.Device;
	import com.huawei.overte.service.SdnUIService;
	import com.huawei.overte.tools.ConnUtil;
	import com.huawei.overte.tools.PopupManagerUtil;
	import com.huawei.overte.tools.SdncUtil;
	import com.huawei.overte.view.node.StateNode;
	import com.huawei.overte.view.overte.OverTEView;
	
	import flash.events.Event;
	import flash.geom.Point;
	import flash.system.Capabilities;
	import flash.utils.ByteArray;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.events.FlexEvent;
	
	import org.httpclient.events.HttpResponseEvent;
	
	import twaver.Consts;
	import twaver.ElementBox;
	import twaver.IData;
	import twaver.Node;
	import twaver.Styles;
	import twaver.network.Network;
	import twaver.network.layout.AutoLayouter;
	import twaver.networkx.NetworkX;

	/**
	 * 此类为控制面板上刷新按钮
	 * */
	public class AgileteRefresh
	{
		public function AgileteRefresh()
		{
		}
		/**Twaver network Topo组件**/
		private var network:Network;
		/**Topo组件布局对象**/
		public var autoLayouter:AutoLayouter;
		/**当前项目类型**/
		private var projectType:String;
		private var sdnService:SdnUIService = new SdnUIService();
//		private var nodeArray:Array=[];
		/**Topo组件父容器**/
		private var overteView:OverTEView;
		/**当前进入管理域ID**/
		private var curareaId:String;
		/**Topo布局中心点坐标**/
		private var centerPoint:Point=new Point(Capabilities.screenResolutionX/2,Capabilities.screenResolutionY/3);
		/**当前进入管理域XML数据（管理域信息以及Topo信息）**/
		public var topoxml:XML;
		/**存储当前管理域下每一个设备的详细信息**/
		public var MessageDevice:ArrayCollection = new ArrayCollection();
		/**建立连接的单例模式**/
		public var connUtil:ConnUtil = ConnUtil.getInstence();
		/**存储获取设备Ifm信息**/
		public var IfmArray:ArrayCollection = new ArrayCollection();
		/**当前时间**/
		private var now:Date;
		/**获取请求时间**/
		private var v1:Number;
		/**请求成功后时间**/
		private var v2:Number;
		/**设备初始化数量**/
		public var deviceInitNum:int=0;
		/**设备总量**/
		private var sumDeviceNum:int=0;
		/**数据请求opsIp**/
		private var opsIp:String=SdncUtil.opsIp;
		/**数据请求项目名称**/
		private var webname:String = SdncUtil.projectname;
		
		/**刷新先将原network上的东西全部清除，再重新获取**/
		public function init():void{
			now = new Date();
			v1 = now.valueOf();
			overteView = (SdncUtil.app.overte.topoview.selectedChild as OverTEView)
			curareaId= (SdncUtil.app.overte.topoview.selectedChild as OverTEView).ManAreasID;
			network = overteView.networkX;
			network.elementBox.clear();
			getData();
			
			DataHandleTool.showOnConsole("刷新管理域"+(SdncUtil.app.overte.topoview.selectedChild as OverTEView).ManAreasName+"Topo信息")
		}
		public function getData():void{
			if(projectType=="test"){
				
			}else{
				PopupManagerUtil.getInstence().closeLoading();
				PopupManagerUtil.getInstence().popupLoading(overteView,false);
				var uri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"+curareaId;
				sdnService.ipcoreRequest(uri, onGetAreaResult, onGetAreaFault);//查询管理域
				DataHandleTool.showOnConsole("发送当前管理域请求Topo数据");
			}
		}
		
		
		/**获取管理域下Topo信息成功方法**/	
		public function onGetAreaResult(e:HttpResponseEvent, data:String):void
		{
			if(e.response.code=="200"){
				
				topoxml = new XML(data);
				var xmlnode:XMLList = topoxml.domain.topo.toponodes.toponode
				if(topoxml.domain.topo.toponodes.children().length()!=0){
					MessageDevice.removeAll();
					for each(var deviceXml1:XML in xmlnode){
						var uri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/devices/"+deviceXml1.nodeID//+devices[0].id+"/tunnels";
						connUtil.clientQuery(uri,ConnUtil.METHOD_GET,reonGetDeviceResult,onGetDeviceFault);
						DataHandleTool.showOnConsole("查找管理域对应设备信息");
					}
				}else{
					PopupManagerUtil.getInstence().closeLoading();
					DataHandleTool.showOnConsole("当前管理域Topo数据请求失败 错误代码：  "+e.response.code);
				}
			}
			
		}
		/**获取管理域下Topo信息出错方法**/	
		private function onGetAreaFault(e:Event):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			DataHandleTool.showOnConsole("获取Topo信息出错\n"+e.toString());
			Alert.show("获取Topo信息出错","提示");
		}
//		/**获取设备详细信息成功方法**/
//		private function onGetDeviceResult(e:HttpResponseEvent,data:String):void
//		{
//			var ifms:ArrayCollection = new ArrayCollection();
//			if(e.response.code=="200"){
//				if(data == ""){
//					PopupManagerUtil.getInstence().closeLoading();
//					Alert.show("获取设备信息为空","提示");
//					return;
//				}else{
//					var devicexml:XML = new XML(data);
//					MessageDevice.addItem({
//						id:devicexml.device.id,
//						deviceName:devicexml.device.deviceName,
//						ipAddress:devicexml.device.ipAddress,
//						deviceTopoIp:devicexml.device.deviceTopoIp,
//						userName:devicexml.device.userName,
//						Passwd:devicexml.device.passwd,
//						version:devicexml.device.version,
//						state:devicexml.device.state=='200'?'normal':'sick',
//						type:devicexml.device.type
//					})
//					DataHandleTool.showOnConsole("获取设备信息成功,当前设备： "+devicexml.device.deviceName+"已获取设备数量：  "+MessageDevice.length);
//					if(MessageDevice.length==topoxml.domain.topo.toponodes.toponode.length()){
//						this.TopoForReal(MessageDevice,topoxml.domain.topo)
//					}
//				}
//			}else{
//				DataHandleTool.showOnConsole("获取设备信息失败,错误代码："+e.response.code);
//				PopupManagerUtil.getInstence().closeLoading();
//				Alert.show("获取设备信息失败","提示");
//			}
//			
//		}
		/**获取设备详细信息出错方法**/
		private function onGetDeviceFault(e:Event):void	
		{
			DataHandleTool.showOnConsole("获取设备详细信息出错\n"+e.toString());
			PopupManagerUtil.getInstence().closeLoading();
		}
		
//		public function onGetFromIfmResult(e:HttpResponseEvent,data:String):void
//		{
//			var ifms:ArrayCollection = new ArrayCollection();
//			if(e.response.code=="200"){
//				DataHandleTool.showOnConsole("设备接口获取成功；");
//				if(data == ""){
//					return;
//				}else{
//					var ifmsXml:XML = new XML(data);
//					for(var i:int=0;i<ifmsXml.ifm.length();i++){
//						ifms.addItem({
//							ifmName:ifmsXml.ifm[i].name,
//							phyType:ifmsXml.ifm[i].phyType,
//							ipAddress:ifmsXml.ifm[i].ips.ip.ipAddress,
//							subnetMask:ifmsXml.ifm[i].ips.ip.subnetMask
//						});
//					}
//					IfmArray.addItem({
//						deviceId:ifmsXml.@id,
//						ifms:ifms
//					});
//					if(IfmArray.length == topoxml.domain.topo.toponodes.toponode.length()){
//						for(var m:int=0;m<MessageDevice.length;m++){
//							for(var n:int=0;n<IfmArray.length;n++){
//								if(IfmArray[n].deviceId==MessageDevice[m].id){
//									MessageDevice[m].ifm = IfmArray[n].ifms
//								}
//							}
//						}
//						this.TopoForReal(MessageDevice,topoxml..domain.topo)
//					}
//					
//				}
//			}else{
//				PopupManagerUtil.getInstence().closeLoading();
//				Alert.show("获取设备接口信息信息失败","提示");
//				DataHandleTool.showOnConsole("设备接口获取失败，错误代码为："+e.response.code);
//			}
//			
//		}
		
		/**真实工程解析xml数据 初始化Topo视图**/
		public  function TopoForReal(devicearray:ArrayCollection,xmllist:XMLList):void
		{
			DataHandleTool.showOnConsole("初始化Topo视图");
			network.elementBox.clear();
			var linkListXml:XMLList=xmllist.topoLinks.topoLink;
			var nodeArray:Array=[];
			var devices:Array=[];
			DataHandleTool.devices=devices;
			DataHandleTool.nodeArray=nodeArray;
			var layout:LayoutTopo = new LayoutTopo;
//			for each(var devicerobj:Object in devicearray){
//				var stateNode:Node=new StateNode;
//				stateNode.setStyle(Styles.LABEL_COLOR,0xffffff);
//				stateNode.setStyle(Styles.LABEL_SIZE,"14");
//				nodeArray.push(stateNode);
//				if(SdncUtil.showdeviceName&&SdncUtil.showdeviceIp){
//					stateNode.name=devicerobj.deviceName.toString()+"\n"+devicerobj.deviceTopoIp.toString();
//				}else if(SdncUtil.showdeviceName&&SdncUtil.showdeviceIp==false){
//					stateNode.name=devicerobj.deviceName.toString();
//				}else if(SdncUtil.showdeviceName==false&&SdncUtil.showdeviceIp){
//					stateNode.name=devicerobj.deviceTopoIp.toString();
//				}
//				stateNode.setClient("username",devicerobj.userName.toString());
//				stateNode.setClient("devicename",devicerobj.deviceName.toString());
//				stateNode.setClient("passwd",devicerobj.Passwd.toString());
//				stateNode.setClient("deviceTopoIp",devicerobj.deviceTopoIp.toString());
//				stateNode.setClient("ip",devicerobj.ipAddress.toString());
//				stateNode.setClient("version",devicerobj.version.toString());
//				stateNode.setClient("productType",devicerobj.type.toString());
//				stateNode.setClient("id",devicerobj.id.toString());
//				devices.push(devicerobj);
//				stateNode.image="icon_core_ipcore";
//				network.elementBox.add(stateNode);
//			}
			DataHandleTool.pushnodes(devicearray,network)
			network.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is StateNode){
					var sn:StateNode=item as StateNode;
					var device:Device=new Device;
					device.stateNode=sn;
					device.initDevice(add);
					sumDeviceNum++;
				}
			})
			DataHandleTool.pushlinks(linkListXml)
			DataHandleTool.stateNodesArr=nodeArray;
//			var links:Array=[];
//			DataHandleTool.links=links;
//			for(var i:int=0;i<linkListXml.length();i++)
//			{
//				var linkobj:Object=new Object;
//				linkobj["linkname"]=linkListXml[i].name.toString();
//				linkobj["fromDeviceID"]=linkListXml[i].headNodeConnector.toponode.nodeID.toString();
//				linkobj["fromDevice"]=linkListXml[i].headNodeConnector.toponode.nodeType.toString();
//				linkobj["frominterface"]=linkListXml[i].headNodeConnector.Connectorid.toString();
//				linkobj["frominterfaceIP"]=linkListXml[i].headNodeConnector.Connectorip.toString();
//				linkobj["frominterfaceState"]=linkListXml[i].headNodeConnector.ConnectorState.toString();
//				linkobj["toDeviceID"]=linkListXml[i].tailNodeConnector.toponode.nodeID.toString();
//				linkobj["toDevice"]=linkListXml[i].tailNodeConnector.toponode.nodeType.toString();
//				linkobj["tointerface"]=linkListXml[i].tailNodeConnector.Connectorid.toString();
//				linkobj["tointerfaceIP"]=linkListXml[i].tailNodeConnector.Connectorip.toString();
//				linkobj["tointerfaceState"]=linkListXml[i].tailNodeConnector.ConnectorState.toString();
//				trace("刷新时："+linkListXml[i].headNodeConnector.ConnectorState.toString());
//				links.push(linkobj);
//			}		
		}
	
		private function add():void
		{
			
			deviceInitNum++;
			if(DataHandleTool.console!=null){
				DataHandleTool.showOnConsole("已经初始化的设备数：       "+deviceInitNum+"\n");
			}
			if(deviceInitNum==sumDeviceNum){
				DataHandleTool.createLinkByTnlForReal(false,network);
				now = new Date();
				v2=now.valueOf();
				var t:Number = (v2-v1)/1000;
				DataHandleTool.showOnConsole("初始化总时间："+t+"s\n");
			}
		}	
		
		
		//以下是外部调用拓扑刷新时的一系列方法
		
		public function regetData():void{
			now = new Date();
			v1 = now.valueOf();
			DataHandleTool.showOnConsole("告警刷新");
			overteView = (SdncUtil.app.overte.topoview.selectedChild as OverTEView)
				var uri:String;
			if(overteView!=null){
				PopupManagerUtil.getInstence().closeLoading();
				PopupManagerUtil.getInstence().popupLoading(overteView);
				curareaId= (SdncUtil.app.overte.topoview.selectedChild as OverTEView).ManAreasID;
				network = overteView.networkX;
				network.elementBox.clear();
				uri=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"+curareaId;
			}else{
				uri=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"
			}
			sdnService.ipcoreRequest(uri, reonGetAreaResult, reonGetAreaFault);//查询管理域
			DataHandleTool.showOnConsole("发送当前管理域请求Topo数据"+uri);
		}
		/**获取管理域下Topo信息成功方法**/	
		public function reonGetAreaResult(e:HttpResponseEvent, data:String):void
		{
			if(e.response.code=="200"){
				topoxml = new XML(data);
				var xmlnode:XMLList = topoxml.domain.topo.toponodes.toponode
				if(topoxml.domain.topo!=null){
					MessageDevice.removeAll();
					for each(var deviceXml1:XML in xmlnode){
						var uri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/devices/"+deviceXml1.nodeID//+devices[0].id+"/tunnels";
						connUtil.clientQuery(uri,ConnUtil.METHOD_GET,reonGetDeviceResult,reonGetDeviceFault);
						DataHandleTool.showOnConsole("查找管理域对应设备信息");
					}
				}else{
					DataHandleTool.showOnConsole("当前管理域Topo数据请求失败 错误代码：  "+e.response.code);
				}
			}
			
		}
		/**获取管理域下Topo信息出错方法**/	
		private function reonGetAreaFault(e:Event):void
		{
			DataHandleTool.showOnConsole("获取Topo信息出错\n"+e.toString());
		}
		
		private function reonGetDeviceResult(e:HttpResponseEvent,data:String):void
		{
			var ifms:ArrayCollection = new ArrayCollection();
			if(e.response.code=="200"){
				if(data == ""){
					Alert.show("获取设备信息为空","提示");
					return;
				}else{
					var devicexml:XML = new XML(data);
					MessageDevice.addItem({
						id:devicexml.device.id,
						deviceName:devicexml.device.deviceName,
						ipAddress:devicexml.device.ipAddress,
						deviceTopoIp:devicexml.device.deviceTopoIp,
						userName:devicexml.device.userName,
						Passwd:devicexml.device.passwd,
						version:devicexml.device.version,
						state:devicexml.device.state=='200'?'normal':'sick',
						type:devicexml.device.type
					})
					DataHandleTool.showOnConsole("刷新获取设备信息成功,当前设备： "+devicexml.device.deviceName+"已获取设备数量：  "+MessageDevice.length);
					if(MessageDevice.length==topoxml.domain.topo.toponodes.toponode.length()){
						if(overteView!=null){
							this.TopoForReal(MessageDevice,topoxml.domain.topo)
						}else{
							this.reTopoForReal(MessageDevice,topoxml.domain.topo)
						}
						DataHandleTool.showOnConsole("overteView： "+overteView);
					}
				}
			}else{
				DataHandleTool.showOnConsole("获取设备信息失败,错误代码："+e.response.code);
				Alert.show("获取设备信息失败","提示");
			}
			
		}
		/**获取设备详细信息出错方法**/
		private function reonGetDeviceFault(e:Event):void	
		{
			DataHandleTool.showOnConsole("获取设备详细信息出错\n"+e.toString());
		}
		
		public  function reTopoForReal(devicearray:ArrayCollection,xmllist:XMLList):void
		{
			DataHandleTool.showOnConsole("初始化Topo视图");
			var linkListXml:XMLList=xmllist.topoLinks.topoLink;
			var nodeArray:Array=[];
			var devices:Array=[];
			DataHandleTool.devices=devices;
			DataHandleTool.nodeArray=nodeArray;
			var layout:LayoutTopo = new LayoutTopo;
			for each(var devicerobj:Object in devicearray){
//				var stateNode:Node=new StateNode;
//				stateNode.setStyle(Styles.LABEL_COLOR,0xffffff);
//				stateNode.setStyle(Styles.LABEL_SIZE,"14");
//				nodeArray.push(stateNode);
//				if(SdncUtil.showdeviceName&&SdncUtil.showdeviceIp){
//					stateNode.name=devicerobj.deviceName.toString()+"\n"+devicerobj.deviceTopoIp.toString();
//				}else if(SdncUtil.showdeviceName&&SdncUtil.showdeviceIp==false){
//					stateNode.name=devicerobj.deviceName.toString();
//				}else if(SdncUtil.showdeviceName==false&&SdncUtil.showdeviceIp){
//					stateNode.name=devicerobj.deviceTopoIp.toString();
//				}
//				stateNode.setClient("username",devicerobj.userName.toString());
//				stateNode.setClient("devicename",devicerobj.deviceName.toString());
//				stateNode.setClient("passwd",devicerobj.Passwd.toString());
//				stateNode.setClient("deviceTopoIp",devicerobj.deviceTopoIp.toString());
//				stateNode.setClient("ip",devicerobj.ipAddress.toString());
//				stateNode.setClient("version",devicerobj.version.toString());
//				stateNode.setClient("productType",devicerobj.type.toString());
//				stateNode.setClient("id",devicerobj.id.toString());
				devices.push(devicerobj);
//				stateNode.image="icon_core_ipcore";
			}
			DataHandleTool.stateNodesArr=nodeArray;
			DataHandleTool.pushlinks(linkListXml);
//			var links:Array=[];
//			DataHandleTool.links=links;
//			for(var i:int=0;i<linkListXml.length();i++)
//			{
//				var linkobj:Object=new Object;
//				linkobj["linkname"]=linkListXml[i].name.toString();
//				linkobj["fromDeviceID"]=linkListXml[i].headNodeConnector.toponode.nodeID.toString();
//				linkobj["fromDevice"]=linkListXml[i].headNodeConnector.toponode.nodeType.toString();
//				linkobj["frominterface"]=linkListXml[i].headNodeConnector.Connectorid.toString();
//				linkobj["frominterfaceIP"]=linkListXml[i].headNodeConnector.Connectorip.toString();
//				linkobj["frominterfaceState"]=linkListXml[i].headNodeConnector.ConnectorState.toString();
//				linkobj["toDeviceID"]=linkListXml[i].tailNodeConnector.toponode.nodeID.toString();
//				linkobj["toDevice"]=linkListXml[i].tailNodeConnector.toponode.nodeType.toString();
//				linkobj["tointerface"]=linkListXml[i].tailNodeConnector.Connectorid.toString();
//				linkobj["tointerfaceIP"]=linkListXml[i].tailNodeConnector.Connectorip.toString();
//				linkobj["tointerfaceState"]=linkListXml[i].tailNodeConnector.ConnectorState.toString();
//				trace("刷新时："+linkListXml[i].headNodeConnector.ConnectorState.toString());
//				links.push(linkobj);
//			}		
			PopupManagerUtil.getInstence().closeLoading();
		}
		
	}
}