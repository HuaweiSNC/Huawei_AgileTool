package com.huawei.overte.control
{
	import com.huawei.overte.control.OpenService;
	import com.huawei.overte.event.SdncEvt;
	import com.huawei.overte.handle.DataHandleTool;
	import com.huawei.overte.handle.LayoutTopo;
	import com.huawei.overte.model.Data;
	import com.huawei.overte.model.Device;
	import com.huawei.overte.service.SdnUIService;
	import com.huawei.overte.tools.ConnUtil;
	import com.huawei.overte.tools.PopupManagerUtil;
	import com.huawei.overte.tools.SdncUtil;
	import com.huawei.overte.view.common.topo.NewNodeButton;
	import com.huawei.overte.view.link.FlowLink;
	import com.huawei.overte.view.link.MyNewLink;
	import com.huawei.overte.view.node.StateNode;
	import com.huawei.overte.view.overte.OverTEView;
	import com.huawei.overte.view.overte.OverTeNavPanel;
	import com.huawei.overte.view.overte.com.OverTEData;
	import com.huawei.overte.view.overte.titlewindows.ADD_Link;
	import com.huawei.overte.view.overte.titlewindows.ADD_VlanMapping;
	import com.huawei.overte.view.overte.titlewindows.AddDevice;
	import com.huawei.overte.view.overte.titlewindows.SystemRollback;
	import com.huawei.overte.view.overte.titlewindows.TW_ManagerAc;
	import com.huawei.overte.view.overte.titlewindows.TW_ManagerFlow;
	import com.huawei.overte.view.overte.titlewindows.TW_ManagerMapping;
	import com.huawei.overte.view.overte.titlewindows.TW_ManagerTunnel;
	
	import flash.events.ContextMenuEvent;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.events.TimerEvent;
	import flash.geom.Point;
	import flash.ui.ContextMenu;
	import flash.ui.ContextMenuItem;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.controls.Menu;
	import mx.core.DragSource;
	import mx.events.DragEvent;
	import mx.managers.DragManager;
	import mx.managers.PopUpManager;
	import mx.resources.IResourceManager;
	import mx.resources.ResourceManager;
	
	import org.httpclient.events.HttpDataEvent;
	import org.httpclient.events.HttpResponseEvent;
	
	import twaver.DataBoxChangeEvent;
	import twaver.ICollection;
	import twaver.IData;
	import twaver.IElement;
	import twaver.ISubNetwork;
	import twaver.Link;
	import twaver.Node;
	import twaver.Styles;
	import twaver.network.Network;
	import twaver.network.layout.AutoLayouter;

	public class OverTECtrl
	{
		/**OverTEView视图对象**/
		[Bindable]public var page:OverTEView;  
		/**数据回归页面对象**/
		private var systemRollbackWindow:SystemRollback;
		/**系统主应用程序 对象**/
		private var __app:overTegui2;//主应用程序
		/**当前项目类型**/
		private var projectType:String;//项目类型
		/**Topo组件对象**/
		private var networkX:Network;//Topo组件对象
		/**管道流状态监测界面 对象**/
		private var navPancel:OverTeNavPanel;
		/**实例化 服务请求**/
		private var sdnService:SdnUIService = new SdnUIService();
		/**实例化 数据连接**/
		public var connUtil:ConnUtil = ConnUtil.getInstence();
		private var format:String="twaver";
		/**拖动连线实例**/
		private var Mylink:MyNewLink;//创建新建MyNewLink实例
		/**拖动新建Node 对象**/
		public var ipage:NewNodeButton
		/**拖动新建Node 坐标**/
		public var newDeviceLocation:Point;//新建设备坐标位置
		/**拖动新建Node类型实例**/
		public var newNode:StateNode;//新建设备对象.
		/**当前选择管理域对象**/
		private var curarea:Object;//当前管理域
		/**数据单例模式**/
		public var mydata:Data = Data.getInstence();//当前管理域
		/**设备数组**/
		public var devices:Array=[];
		/**右键菜单对象**/
		private var menu:Menu;
		/**设备信息内存对象**/
		public var MessageDevice:ArrayCollection = new ArrayCollection();
		/**设备接口信息内存对象**/
		public var IfmArray:ArrayCollection = new ArrayCollection();
		/**系统服务地址**/
		private var opsIp:String=SdncUtil.opsIp;
		/**系统服务项目名称**/
		private var webname:String = SdncUtil.projectname;
		/**当前管理域下Topo数据对象**/
		public var topoxml:XML;
		/**当前设备数**/
		public var deviceInitNum:int=0;
		/**当前设备总数**/
		private var sumDeviceNum:int=0;
		/**系统当前时间对象*/
		private var now:Date;
		private var v1:Number;
		private var v2:Number;
		/**Topo自动布局对象*/
		public var autoLayouter:AutoLayouter;
		private var resourceManager:IResourceManager = ResourceManager.getInstance();
		public function OverTECtrl()
		{
		}
		/**组件内部结构初始化方法**/
		public function onInit():void
		{
			/**系统初始化数据**/
			__app = SdncUtil.app;//初始化主应用程序
			curarea = SdncUtil.cuArea;//当前管理域
			projectType = SdncUtil.cuProjectType;//初始化项目类型
			navPancel = page.navPanel
			DataHandleTool.devices=devices;//初始化设备数组
			networkX=page.networkX;//初始化Topo组件
			autoLayouter = new AutoLayouter(networkX)
			networkX.keyboardRemoveEnabled = false
//			SdncUtil.network = page.networkX
				
				
			/**主应用程序启动监听**/
			__app.addEventListener(SdncEvt.NEW_LINK,NewLinkHandle);//监听新建Link
			__app.addEventListener(SdncEvt.NEW_NODE,NewNodeHandle);//监听新建Node
			__app.addEventListener(SdncEvt.CLICK_ADG_PANCEL,AdgPanel_click);//监听左侧Advancedatagrid点击事件
			__app.addEventListener(SdncEvt.CLICK_ADGFLOW_PANCEL,AdgflowPanel_click);//监听左侧Advancedatagrid点击事件
			
			__app.addEventListener(SdncEvt.OPEN_TUNNEL_EVENT,OpenTunnelState);//监听启动管道监听事件
			__app.addEventListener(SdncEvt.CLOSE_TUNNEL_EVENT,CloseTunnelState);//监听关闭管道监听事件
			
			page.addEventListener(SdncEvt.OPEN_TUNNEL_WINDOWS,OpenTunnelWindows);//监听弹出定义管道事件
			page.addEventListener(SdncEvt.OPEN_FLOW_WINDOWS,OpenFlowWindows);//监听弹出流定义事件
			page.addEventListener(SdncEvt.OPEN_VLANMAPPING_WINDOWS,OpenVlanMappingWindows);//监听弹出VlanMapping事件
			
			__app.addEventListener(SdncEvt.OPEN_SYSTEM_ROLLBACK_WINDOW,openSystemRollbackWindow);
			__app.addEventListener(SdncEvt.CLOSE_SYSTEM_ROLLBACK_WINDOW,closeSystemRollbackWindow);
			/**设置Topo组件模式 启动监听拖动  拖入事件**/
			networkX.setEditInteractionHandlers(true);//设置network模式
			networkX.addEventListener(DragEvent.DRAG_ENTER,Drag_Enter);
			networkX.addEventListener(DragEvent.DRAG_DROP,Drag_Drop);
			/**监听networkbox元素变化事件 监听画布所有元素的变化**/
			networkX.elementBox.addDataBoxChangeListener(function(e:DataBoxChangeEvent):void{
				var kind:String = e.kind;
				/**判断变化类型、是否为数据添加*/
				if(kind == DataBoxChangeEvent.ADD){
					var data:IData = e.data;
					/**如果增加的是Link 先删除创建时的MyNewLink 再新建MyNewLink*/
					if((data is Link)&& !(data is MyNewLink)&& !(data is FlowLink)){
						var mynewlk:MyNewLink = new MyNewLink((data as Link).fromNode,(data as Link).toNode)
						networkX.elementBox.remove(data);
						AddLink(mynewlk);
					}
				}
			});
			/**初始化Topo右键菜单**/
			networkX.contextMenu = new ContextMenu();
			networkX.contextMenu.hideBuiltInItems();	
			networkX.contextMenu.addEventListener(ContextMenuEvent.MENU_SELECT, function(e:ContextMenuEvent):void{			
				var p:Point = new Point(e.mouseTarget.mouseX / networkX.zoom, e.mouseTarget.mouseY / networkX.zoom);		 	 	
				var datas:ICollection = networkX.getElementsByLocalPoint(p);
				if (datas.count > 0) {
					networkX.selectionModel.setSelection(datas.getItemAt(0));
				}else{
					networkX.selectionModel.clearSelection();
				}				
				var item1:ContextMenuItem = new ContextMenuItem("定义管道");
				item1.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, handler);
				var item2:ContextMenuItem = new ContextMenuItem("定义流", true);
				item2.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, handler);
				var item3:ContextMenuItem = new ContextMenuItem("AC侧管理");
				item3.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, handler);
				networkX.contextMenu.customItems = [item1, item2, item3];  
			});
			Styles.setStyle(Styles.SELECT_COLOR,0xffffff);
			var handler:Function = function(e:ContextMenuEvent):void{
				var i:int = 0;
				var element:IElement = null;
				var item:ContextMenuItem = ContextMenuItem(e.target);
				if(item.caption == "Remove Selection"){
					networkX.removeSelection();					
				}
				else{
					var p:Point = new Point(e.mouseTarget.mouseX / networkX.zoom, e.mouseTarget.mouseY / networkX.zoom);
					var datas:ICollection = networkX.getElementsByLocalPoint(p);
					if(datas.count>0)
						element = datas.getItemAt(0);
					else
						element=null;
					if(item.caption == "定义管道"){
						(__app.overte.topoview.selectedChild as OverTEView).dispatchEvent(new SdncEvt(SdncEvt.OPEN_TUNNEL_WINDOWS));
					}
					else if(item.caption == "定义流"){
						(__app.overte.topoview.selectedChild as OverTEView).dispatchEvent(new SdncEvt(SdncEvt.OPEN_FLOW_WINDOWS));
					}
					else if(item.caption == "AC侧管理"){
						//打开流导入
						(__app.overte.topoview.selectedChild as OverTEView).dispatchEvent(new SdncEvt(SdncEvt.OPEN_VLANMAPPING_WINDOWS));
					}
				}
			}
			DataHandleTool.showOnConsole("初始化Agile TE View");
			//启动告警服务
			openService();
				
		}
		
		
		/** 打开告警服务    xc*/
		public function openService(){
			trace("打开告警服务");
			var openservice:OpenService = new OpenService();
			openservice.page = this.page;
			openservice.realForStartService();
		}
		
		
		/**界面初始化方法*/
		public function onCreate():void
		{
			now = new Date();
			v1 = now.valueOf();
			IfmArray.removeAll();
			SdncUtil.addOverview(networkX);
			if(projectType=="test"){
									
			}else{
				trace("进入初始化时间"+new Date().time)
				PopupManagerUtil.getInstence().closeLoading();
				PopupManagerUtil.getInstence().popupLoading(__app);
				/**请求设备的详细信息*/
				GetdevicesMessage()//请求设备详细信息;
			}
		}
		/**界面初始化 发送请求获取所有设备详细信息 方法*/
		public function GetdevicesMessage():void{
			var xmlnode:XMLList = page.TopoXML;
			if(xmlnode.toponodes.children().length()!=0){
				MessageDevice.removeAll();
				for each(var deviceXml1:XML in xmlnode.toponodes.toponode){
					trace("请求设详细信息 时间"+new Date().time+"====="+deviceXml1.nodeID)
					var uri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/devices/"+deviceXml1.nodeID//+devices[0].id+"/tunnels";
					connUtil.clientQuery(uri,ConnUtil.METHOD_GET,onGetDeviceResult,onGetDeviceFault);
					DataHandleTool.showOnConsole("发送请求设备详细信息，设备ID：  "+ deviceXml1.nodeID)
				}
			}else{
				PopupManagerUtil.getInstence().closeLoading();
				DataHandleTool.showOnConsole("当前管理域初始化无设备");
			}
		}
//		/**获取管理域下Topo信息连接成功方法*/	
//		public function onResultForClient(e:HttpResponseEvent,data:String):void
//		{
//			if(e.response.code=="200"){
//				topoxml = new XML(data);
//				DataHandleTool.showOnConsole("当前管理域Topo数据请求成功");
//				var xmlnode:XMLList = topoxml.domain.topo.toponodes.toponode
//				if(topoxml.domain.topo.toponodes.children().length()!=0){
//					MessageDevice.removeAll();
//					for each(var deviceXml1:XML in xmlnode){
//						var opsIp:String=SdncUtil.opsIp;
//						var webname:String = SdncUtil.projectname;
//						var uri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/devices/"+deviceXml1.nodeID//+devices[0].id+"/tunnels";
//						connUtil.clientQuery(uri,ConnUtil.METHOD_GET,onGetDeviceResult,onGetDeviceFault);
//						DataHandleTool.showOnConsole("查找管理域对应设备信息");
//						__app.log.logger.info("根据管理域设备ID请求设备全部信息 当前设备ID为="+deviceXml1.nodeID+"url="+uri)	
//					}
//				}else{
//					PopupManagerUtil.getInstence().closeLoading();
//					DataHandleTool.showOnConsole("当前管理域初始化无设备");
//				}
//			}else{
//				DataHandleTool.showOnConsole("当前管理域Topo数据请求失败 错误代码：  "+e.response.code);
//			}
//			
//		}
//		/**获取管理域下Topo数据连接出错*/		
//		private function onRequestOpsDeafalt(e:Event):void
//		{
//			PopupManagerUtil.getInstence().closeLoading();
//			DataHandleTool.showOnConsole("当前管理域Topo数据请求连接出错"+e.toString());
//			Alert.show("当前管理域Topo数据请求连接出错","提示");
//		}
		/**获取设备详细信息连接成功方法**/
		private function onGetDeviceResult(e:HttpResponseEvent,data:String):void
		{
			var ifms:ArrayCollection = new ArrayCollection();
			if(e.response.code=="200"){
				var devicexml:XML = new XML(data);
//				for(var i:int=0;i<devicexml.device.ifms.ifm.length();i++){
//					ifms.addItem({
//						ifmName:devicexml.device.ifms.ifm[i].name,
//						phyType:devicexml.device.ifms.ifm[i].phyType,
//						ipAddress:devicexml.device.ifms.ifm[i].ips.ip.ipAddress,
//						subnetMask:devicexml.device.ifms.ifm[i].ips.ip.subnetMask
//					});
//				}
				MessageDevice.addItem({
					id:devicexml.device.id,
					deviceName:devicexml.device.deviceName,
					ipAddress:devicexml.device.ipAddress,
					deviceTopoIp:devicexml.device.deviceTopoIp,
					userName:devicexml.device.userName,
					Passwd:devicexml.device.passwd,
					state:devicexml.device.state=='200'?'normal':'sick',
					version:devicexml.device.version,
					type:devicexml.device.type
				})
				trace("设备信息请求成功"+new Date().time+"====="+devicexml.device.id)
				DataHandleTool.showOnConsole(devicexml.device.deviceName+"  设备信息Get成功,");
				/**所有设备信息获取成功后开始初始化Topo界面**/
				if(MessageDevice.length== page.TopoXML.toponodes.toponode.length()){
					this.TopoForReal(MessageDevice,page.TopoXML)
				}
			}else{
				DataHandleTool.showOnConsole("获取设备信息失败,错误代码："+e.response.code);
				PopupManagerUtil.getInstence().closeLoading();
				Alert.show("获取设备信息失败,错误代码："+e.response.code,"提示");
			}
			
		}
		/**获取设备详细信息连接出错方法**/
		private function onGetDeviceFault(e:Event):void	
		{
			DataHandleTool.showOnConsole("获取设备信息连接出错"+e.toString());
			PopupManagerUtil.getInstence().closeLoading();
		}
		/**获取设备接口信息连接成功方法   目前代码已经挪位置**/
		public function onGetFromIfmResult(e:HttpResponseEvent,data:String):void
		{
			var ifms:ArrayCollection = new ArrayCollection();
			if(e.response.code=="200"){
					var ifmsXml:XML = new XML(data);
					DataHandleTool.showOnConsole("设备接口获取成功；");
					for(var i:int=0;i<ifmsXml.ifm.length();i++){
						ifms.addItem({
							ifmName:ifmsXml.ifm[i].name,
							phyType:ifmsXml.ifm[i].phyType,
							ipAddress:ifmsXml.ifm[i].ips.ip.ipAddress,
							subnetMask:ifmsXml.ifm[i].ips.ip.subnetMask,
							state:ifmsXml.ifm[i].ips.ip.state
						});
						DataHandleTool.showOnConsole("设备接口状态："+ifmsXml.ifm[i].ips.ip.state);
					}
					IfmArray.addItem({
						deviceId:ifmsXml.@id,
						ifms:ifms
					});
					
					if(IfmArray.length == page.TopoXML.toponodes.toponode.length()){
						for(var m:int=0;m<MessageDevice.length;m++){
							for(var n:int=0;n<IfmArray.length;n++){
								if(IfmArray[n].deviceId==MessageDevice[m].id){
									MessageDevice[m].ifm = IfmArray[n].ifms
								}
							}
						}
						
						this.TopoForReal(MessageDevice,page.TopoXML)
					}
					
			}else{
				PopupManagerUtil.getInstence().closeLoading();
				Alert.show("获取设备接口信息信息失败","提示");
				DataHandleTool.showOnConsole("设备接口获取失败，错误代码为："+e.response.code);
			}
			
		}
		/**获取设备接口信息连接出错方法**/
		private function onGetIfmFault(e:Event):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			DataHandleTool.showOnConsole("获取设备接口信息连接出错");
			Alert.show("数据连接出错","提示");
		}
		/**解析TopoXMl 初始化Topo界面**/
		public  function TopoForReal(devicearray:ArrayCollection,xmllist:XMLList):void
		{	
			trace("开始topo界面"+new Date().time)
			DataHandleTool.showOnConsole("初始化Topo视图");
			networkX.elementBox.clear();
			var linkListXml:XMLList=xmllist.topoLinks.topoLink;
			var devices:Array=[];
			DataHandleTool.devices=devices;
			var layout:LayoutTopo = new LayoutTopo;
			for each(var devicerobj:Object in devicearray){
				var stateNode:Node=new StateNode;
				var deviceobj:Object=new Object;
				stateNode.setStyle(Styles.LABEL_COLOR,0xffffff);
				stateNode.setStyle(Styles.LABEL_SIZE,"14");
				if(SdncUtil.showdeviceName&&SdncUtil.showdeviceIp){
					stateNode.name=devicerobj.deviceName.toString()+"\n"+devicerobj.deviceTopoIp.toString();
				}else if(SdncUtil.showdeviceName&&SdncUtil.showdeviceIp==false){
					stateNode.name=devicerobj.deviceName.toString();
				}else if(SdncUtil.showdeviceName==false&&SdncUtil.showdeviceIp){
					stateNode.name=devicerobj.deviceTopoIp.toString();
				}
				stateNode.setClient("username",devicerobj.userName.toString());
				stateNode.setClient("devicename",devicerobj.deviceName.toString());
				stateNode.setClient("passwd",devicerobj.Passwd.toString());
				stateNode.setClient("deviceTopoIp",devicerobj.deviceTopoIp.toString());
				stateNode.setClient("ip",devicerobj.ipAddress.toString());
				stateNode.setClient("version",devicerobj.version.toString());
				stateNode.setClient("state",devicerobj.state.toString());
				stateNode.setClient("productType",devicerobj.type.toString());
				stateNode.setClient("id",devicerobj.id.toString());
				devices.push(devicerobj);
//				if(devicerobj.state.toString()!="normal"){
//					stateNode.setStyle(Styles.INNER_COLOR,0xea6060);
//				}
				stateNode.image="icon_core_ipcore";
				DataHandleTool.stateNodesArr.push(stateNode)
				networkX.elementBox.add(stateNode);
			}
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is StateNode){
					var sn:StateNode=item as StateNode;
					var device :Device=new Device;
					device.stateNode=sn;
					device.initDevice(add);
					sumDeviceNum++;
				}
			})
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
//				links.push(linkobj);
//			}		
		}
		/**设备节点初始化完成回调方法**/
		private function add():void
		{
			
			deviceInitNum++;
			if(DataHandleTool.console!=null){
				DataHandleTool.showOnConsole("已经初始化的设备数：       "+deviceInitNum+"\n");
			}
			if(deviceInitNum==sumDeviceNum){
				PopupManagerUtil.getInstence().closeLoading();
				DataHandleTool.createLinkByTnlForReal(false,networkX);
				now = new Date();
				v2=now.valueOf();
				var t:Number = (v2-v1)/1000;
				DataHandleTool.showOnConsole("初始化总时间："+t+"s\n");
			}
		}						
		/**设置新建Link  Topo组件模式**/
		public function NewLinkHandle(e:SdncEvt):void
		{
			networkX.setCreateLinkInteractionHandlers();
		}
		/**设置新建点  Topo组件模式**/
		public function NewNodeHandle(e:SdncEvt):void
		{
			var dragSource:DragSource = new DragSource();
			dragSource.addData(ipage, format );
			DragManager.doDrag(ipage, dragSource,e.params as MouseEvent);
		}
		/**管道流监听状态界面拉开点击管道方法**/
		public function AdgPanel_click(e:SdncEvt):void
		{
			if(SdncUtil.cuProjectType=="test"){
		
			}else{
				DataHandleTool.llxml = null
				var selected_obj:XML = e.params as XML;
				var statestr:String = selected_obj.@flow;
				mydata.priState = selected_obj.@priState;
				mydata.backState = selected_obj.@backState;
				var devicetunnelname:String = selected_obj.@name;
				var tunnelname:Array =devicetunnelname.split("-")
				tunnelName = tunnelname[0]
				tunnelstate = selected_obj.@flow;
				deviceId = selected_obj.@deviceId
				if(statestr.length>0){
					PopupManagerUtil.getInstence().closeLoading();
					PopupManagerUtil.getInstence().popupLoading(__app);
					var opsIp:String=SdncUtil.opsIp;
					var webname:String = SdncUtil.projectname;
					var uri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"+curarea["id"]+"/devices/"+deviceId+"/tpath?name="+tunnelname[0];
					connUtil.clientQuery(uri,ConnUtil.METHOD_GET,onGetPathResult,onGePathFault);
				}
			}
		}
		public var patharray:ArrayCollection;
		private function onGetPathResult(e:HttpResponseEvent,data:String):void
		{
			if(e.response.code=="200"){
				if(data == ""){
					PopupManagerUtil.getInstence().closeLoading();	
					Alert.show("获取管道信息data为空","提示");
					return;
				}else{
					
					DataHandleTool.showOnConsole("管道主备路径获取成功");
					var pathxml:XML = new XML(data);
					pathpush(pathxml)
//					patharray = new ArrayCollection();
//					for(var i:int;i<pathxml.path.length();i++){
//						var hotarray:ArrayCollection = new ArrayCollection()
//						for(var j:int=0;j<pathxml.path[i].nextHops.nextHop.length();j++){
//							hotarray.addItem({
//								id:pathxml.path[i].nextHops.nextHop[j].id,
//								nextIp:pathxml.path[i].nextHops.nextHop[j].nextIp
//							})
//						}
//						patharray.addItem({
//							name:pathxml.path[i].name,
//							pathType:pathxml.path[i].pathType,
//							name:pathxml.path[i].name,
//							nextHops:hotarray
//						})
//					}
					DataHandleTool.drawLineByTunnel(tunnelstate,patharray,SdncUtil.network);
				}
				PopupManagerUtil.getInstence().closeLoading();
			}else{
				DataHandleTool.showOnConsole("管道主备路径获取失败、错误代码"+e.response.code+"\n"+e.toString())
				PopupManagerUtil.getInstence().closeLoading();
			}
			DataHandleTool.timer.start();
		}
		/**真实工程：请求路径信息连接出错方法**/
		private static function onGetPathFault(e:Event):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			trace("获取流信息连接出错"+e.toString(),"提示");
		}
		/**管道流监听状态界面拉开点击流方法**/
		public var tunnelstate:String="";
		/**管道所在设备ID**/
		public var deviceId:String="";
		/**点击管道名称**/
		public var tunnelName:String="";
		/**点击流名称**/
		public var flowName:String="";
		
		public function AdgflowPanel_click(e:SdncEvt):void
		{
			if(SdncUtil.cuProjectType=="test"){
				
			}else{
				DataHandleTool.llxml = null
				var selected_obj:XML = e.params as XML;
				tunnelstate = selected_obj.@flow;
				mydata.priState = selected_obj.@priState;
				mydata.backState = selected_obj.@backState;
				var devicetunnelname:String = selected_obj.@name;
				var tunnelname:Array = devicetunnelname.split("-")
				tunnelName = tunnelname[0];
				deviceId = selected_obj.@deviceId
				PopupManagerUtil.getInstence().closeLoading();
				PopupManagerUtil.getInstence().popupLoading(__app);
				var opsIp:String=SdncUtil.opsIp;
				var webname:String = SdncUtil.projectname;
				var uri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"+curarea["id"]+"/devices/"+deviceId+"/tpath?name="+tunnelname[0];
				connUtil.clientQuery(uri,ConnUtil.METHOD_GET,onGetFlowResult,onGePathFault);
			}
		}
		/**真实工程：请求路径信息连接成功方法**/
		private function onGetFlowResult(e:HttpResponseEvent,data:String):void
		{
			if(e.response.code=="200"){
				if(data == ""){
					PopupManagerUtil.getInstence().closeLoading();	
					Alert.show("获取管道信息data为空","提示");
					return;
				}else{
					var pathxml:XML = new XML(data);
					pathpush(pathxml)
					DataHandleTool.drawLineByTunnel(tunnelstate,patharray,SdncUtil.network);
				}
				DataHandleTool.drawLineByFlow(tunnelstate,patharray,networkX);		
				PopupManagerUtil.getInstence().closeLoading();
			}else{
				DataHandleTool.showOnConsole("流主备路径获取失败、错误代码"+e.response.code+"\n"+e.toString())
				PopupManagerUtil.getInstence().closeLoading();
			}
			DataHandleTool.timer.start();
		}
		
		
		public function pathpush(pathxml:XML):void{
			patharray = new ArrayCollection();
			for(var i:int;i<pathxml.path.length();i++){
				var hotarray:ArrayCollection = new ArrayCollection()
				for(var j:int=0;j<pathxml.path[i].nextHops.nextHop.length();j++){
					hotarray.addItem({
						id:pathxml.path[i].nextHops.nextHop[j].id,
						nextIp:pathxml.path[i].nextHops.nextHop[j].nextIp
					})
				}
				patharray.addItem({
					name:pathxml.path[i].name,
					pathType:pathxml.path[i].pathType,
					nextHops:hotarray
				})
			}
		}
		/**真实工程：请求路径信息连接出错方法 **/
		private function onGePathFault(e:Event):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			trace("获取路径信息连接出错"+e.toString(),"提示");
		}
		/**右键弹出管道定义界面**/
		public function OpenTunnelWindows(e:SdncEvt):void
		{
			var manager:TW_ManagerTunnel=TW_ManagerTunnel(PopUpManager.createPopUp(__app,TW_ManagerTunnel,true));
			manager.title="管道定义"
			PopUpManager.centerPopUp(manager);
		}
		/**右键弹出流定义界面**/
		public function OpenFlowWindows(e:SdncEvt):void
		{
			var manager:TW_ManagerFlow=TW_ManagerFlow(PopUpManager.createPopUp(__app,TW_ManagerFlow,true));
			manager.title="流定义"
			PopUpManager.centerPopUp(manager);
		}
		/**弹出VlanMapping界面**/
		public function OpenVlanMappingWindows(e:SdncEvt):void
		{
			var AddAc:TW_ManagerAc=TW_ManagerAc(PopUpManager.createPopUp(__app,TW_ManagerAc,true));
			PopUpManager.centerPopUp(AddAc);
		}
		
		/*开始管道监听事件触发函数*/
		public function OpenTunnelState(e:SdncEvt):void
		{
//			page.time.start();

		}
		/*关闭定义管道windows*/
		public function CloseTunnelState(e:SdncEvt):void{
//			page.time.stop();
		}
		/**添加设备拖动进入视图方法**/
		private function Drag_Enter(evt:DragEvent):void{
			if(evt.dragSource.hasFormat(format) )
			{
				DragManager.acceptDragDrop(networkX);
			}
		}
		/**添加设备拖动进入鼠标放下方法**/
		private function Drag_Drop(evt:DragEvent):void{
			newDeviceLocation=networkX.getLogicalPoint(evt as MouseEvent);
			AddNode()
		}
		/**添加Link时触发的事件函数  @param MyNewLink**/	
		private function AddLink(link:MyNewLink):void{
			Mylink = link;
			if(projectType=="test"){
				addlinkForTest(link)
			}else{
				addlinkForReal(link)
			}
		}
		/**
		 * 测试工程用添加Link时触发的事件函数
		 * @param MyNewLink
		 */	
		private function addlinkForTest(link:MyNewLink):void{
			var AddLink:ADD_Link=ADD_Link(PopUpManager.createPopUp(__app,ADD_Link,true));//弹出增加链路窗口
			
			AddLink.fromDeviceName.text = (link.fromNode as Node).getClient("devicename");//初始化新建链路的开始节点IP
			var frominterface:Array =  (link.fromNode as StateNode).getClient("ifms");//初始化新建链路的开始节点上节点的端口集合
			AddLink.frominterface.dataProvider = frominterface;//绑定下拉框数据源为节点端口集合
			
			AddLink.toDeviceName.text = (link.toNode as StateNode).getClient("devicename");//初始化新建链路的结束节点IP
			var tointerface:Array =  (link.toNode as StateNode).getClient("ifms");//初始化新建链路的结束节点上节点的端口集合
			AddLink.tointerface.dataProvider = tointerface;//绑定下拉框数据源为节点端口集合
			
			AddLink.addEventListener(SdncEvt.SAVE_LINK,SaveLink);//监听添加节点界面保存 Link事件
			
			PopUpManager.centerPopUp(AddLink);//弹出窗口居中
			networkX.setDefaultInteractionHandlers();//设置network访问模式为默认模式
		
		}
		/**
		 * 真实工程用   添加Link时触发的事件函数  弹出连线界面
		 * @param MyNewLink
		 */	
		private function addlinkForReal(link:MyNewLink):void{
			
			var AddLink:ADD_Link=ADD_Link(PopUpManager.createPopUp(__app,ADD_Link,true));//弹出增加链路窗口
			AddLink.newlink = link
//				fromNode = link.fromNode as StateNode
//			AddLink.toNode = link.toNode as StateNode
			AddLink.addEventListener(SdncEvt.SAVE_LINK,SaveLink);//监听添加节点界面保存 Link事件
			DataHandleTool.showOnConsole("获取拖动连线  From："+link.fromNode.getClient("devicename")+ "To："+link.toNode.getClient("devicename"));
			PopUpManager.centerPopUp(AddLink);//弹出窗口居中
			networkX.setDefaultInteractionHandlers();//设置network访问模式为默认模式
		}
		
		/**保存连线信息回调方法   向后台Post请求*/	
		private function SaveLink(e:SdncEvt):void{
			var getlinkdata:Array = e.params as Array//保存数据Array
			var lk:MyNewLink = getlinkdata[6] as  MyNewLink
			
			lk.setStyle(Styles.LINK_COLOR,0x60c6fb);//设置MyNewLink 链路颜色
			
			lk.setStyle(Styles.LABEL_COLOR,0xffffff)//设置MyNewLink 显示字体颜色
			if(projectType=="test"){
				OverTEData.PathArray.addItem(getlinkdata);
				networkX.elementBox.add(Mylink)
			}else{
				var bool:Boolean=DataHandleTool.OrhaveLink(lk,networkX)
				if(bool==true){
//					Alert.show("Topo中已经存在该链路","提示")
					Alert.show(resourceManager.getString('global','link.topohavelink'),
						resourceManager.getString('global','all.prompt'))
					
				}else{
					PopupManagerUtil.getInstence().closeLoading();
					var opsIp:String=SdncUtil.opsIp;
					var webname:String = SdncUtil.projectname;
					var uri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"+curarea["id"]+"/links";
					connUtil.clientQuery(uri,ConnUtil.METHOD_POST,onPostLinkResult,onPostLinkFault,getlinkdata[5]);
					PopupManagerUtil.getInstence().popupLoading(__app);
				}
			}
		}
		/**连线保存 请求成功方法  */
		private function onPostLinkResult(e:HttpDataEvent):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			if(e.bytes.toString()=="true"){
				DataHandleTool.showOnConsole("Topo拖动连线信息保存成功");
				networkX.elementBox.add(Mylink)
				Alert.show("Topo链路成功","提示");
			}else{
				DataHandleTool.showOnConsole("Topo拖动连线信息保存失败");
				Alert.show("Topo链路失败","提示");
			}
		}
		/**连线保存 请求连接错误方法 */
		private function onPostLinkFault(e:Event):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			trace("添加链路失败");
		}
		/**添加Node时触发的事件函数  弹出添加设备窗口*/	
		private function AddNode():void{
			var AddNode:AddDevice=AddDevice(PopUpManager.createPopUp(__app,AddDevice,true));//弹出增加设备窗口
			AddNode.flag = "add";
			AddNode.productType.enabled = false;
			AddNode.productType.selectedIndex =0;
			AddNode.overtectrl = this;
			AddNode.addEventListener(SdncEvt.SAVE_NODE,SaveNode);//监听添加节点界面保存 Link事件
			PopUpManager.centerPopUp(AddNode);//弹出窗口居中
			networkX.setDefaultInteractionHandlers();//设置network访问模式为默认模式
		}
		/**添加设备保存回调方法**/
		public function SaveNode(data:ArrayCollection):void
		{	
			PopupManagerUtil.getInstence().closeLoading();
			PopupManagerUtil.getInstence().popupLoading(__app);
//			var bodys:String = "<devices></devices>"
//			var bodyx:XML = XML(bodys);
//			var devices:String = "<device><deviceName></deviceName><ipAddress></ipAddress><deviceTopoIp></deviceTopoIp><userName></userName><passwd></passwd><version></version><type></type></device>"
//			var devicex:XML = XML(devices);
//			devicex.deviceName.children()[0] = data[0].devicename;
//			devicex.ipAddress.children()[0] = data[0].ip;
//			devicex.deviceTopoIp.children()[0] = data[0].topoip;
//			devicex.userName.children()[0] = data[0].username;
//			devicex.passwd.children()[0] = data[0].passwd;
//			devicex.version.children()[0] = data[0].version;
//			devicex.type.children()[0] = data[0].productType;
//			bodyx.appendChild(devicex)
//			var body:XML = bodyx;
//			body = body.replace("&lt;","<")
//			body = body.replace("&gt;",">")
				
			var body:XML = DataHandleTool.pushdevicebody(data)
			var opsIp:String=SdncUtil.opsIp;
			var url:String = ConnUtil.protocolHeader+opsIp+"/AgileTeService/agilete/domains/"+(__app.overte.topoview.selectedChild as OverTEView).ManAreasID+"/devices";
			DataHandleTool.showOnConsole("添加设备请求")
			connUtil.clientQuery(url,ConnUtil.METHOD_POST,postReslut,getFault,body.toString());
		}
		/**添加设备保存连接成功方法**/
		private function postReslut(e:HttpDataEvent):void
		{
			var str:String=e.bytes.toString();
			if(Number(str))
			{
				DataHandleTool.showOnConsole("添加设备保存成功、请求"+str+"设备接口信息"+new Date().valueOf()/1000)
				var opsIp:String=SdncUtil.opsIp;
				var webname:String = SdncUtil.projectname;
				mydata.newdevices[0].id = str;
				var fromuri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"+curarea["id"]+"/devices/"+str+ "/ifms?type=Vlanif";
				connUtil.clientQuery(fromuri,ConnUtil.METHOD_GET,GetFromIfmResult,onGetIfmFault);
			}
			else
			{
				PopupManagerUtil.getInstence().closeLoading();
				Alert.show("添加失败！错误信息："+str,"提示");
			}
		}
		/**添加设备保存连接失败方法**/
		public function getFault(e:Event):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			Alert.show("连接失败！","提示")
		}
		/**获取新添设备接口信息成功方法**/
		public function GetFromIfmResult(e:HttpResponseEvent,data:String):void
		{
			var ifms:ArrayCollection = new ArrayCollection();
			var ifmsXml:XML = new XML(data);
			var stateNode:StateNode=new StateNode;
			if(e.response.code=="200"){
				if(ifmsXml.children().length()!=0){
					for(var i:int=0;i<ifmsXml.ifm.length();i++){
						ifms.addItem({
							ifmName:ifmsXml.ifm[i].name,
							phyType:ifmsXml.ifm[i].phyType,
							ipAddress:ifmsXml.ifm[i].ips.ip.ipAddress,
							subnetMask:ifmsXml.ifm[i].ips.ip.subnetMask
						});
						stateNode.setClient("ifm",ifms);
					}
				}
				var device:Object = new Object;
				device.userName = mydata.newdevices[0].username
				device.deviceName = mydata.newdevices[0].devicename
				device.Passwd = mydata.newdevices[0].passwd
				device.deviceTopoIp = mydata.newdevices[0].ip
				device.ipAddress = mydata.newdevices[0].topoip
				device.version = mydata.newdevices[0].version
				device.type = mydata.newdevices[0].productType
				device.id = mydata.newdevices[0].id
				device.ifm = ifms
				DataHandleTool.devices.push(device)
				var deviceobj:Device = new Device
				deviceobj.stateNode = stateNode
				deviceobj.pingTest(device.id.toString(),null)
					
				if(SdncUtil.showdeviceName&&SdncUtil.showdeviceIp){
					stateNode.name=device.deviceName.toString()+"\n"+device.deviceTopoIp.toString();
				}else if(SdncUtil.showdeviceName&&SdncUtil.showdeviceIp==false){
					stateNode.name=device.deviceName.toString();
				}else if(SdncUtil.showdeviceName==false&&SdncUtil.showdeviceIp){
					stateNode.name=device.deviceTopoIp.toString();
				}
				stateNode.setClient("username",device.userName.toString());
				stateNode.setClient("devicename",device.deviceName.toString());
				stateNode.setClient("passwd",device.Passwd.toString());
				stateNode.setClient("deviceTopoIp",device.deviceTopoIp.toString());
				stateNode.setClient("ip",device.ipAddress.toString());
				stateNode.setClient("version",device.version.toString());
				stateNode.setClient("productType",device.type.toString());
				stateNode.setClient("id",device.id.toString());
				
				stateNode.centerLocation=newDeviceLocation;
				var parentNode:ISubNetwork=networkX.currentSubNetwork;
				stateNode.parent=parentNode;
				stateNode.image="icon_core_ipcore";
				stateNode.setStyle(Styles.LABEL_COLOR,0xffffff);
				stateNode.setStyle(Styles.LABEL_SIZE,"14");
				devices.push(device);
				
				DataHandleTool.stateNodesArr.push(stateNode)
				networkX.elementBox.add(stateNode);
				PopupManagerUtil.getInstence().closeLoading();
			}else{
				PopupManagerUtil.getInstence().closeLoading();
				Alert.show("获取设备接口信息信息失败","提示");
				DataHandleTool.showOnConsole("设备接口获取失败，错误代码为："+e.response.code);
			}
			
		}
		
		
		
		
		private function onPostDeviceResult(e:HttpDataEvent):void
		{
			if(e.bytes.toString().search("error")== -1 ){
				networkX.elementBox.add(newNode)
				Alert.show("设备增加成功","提示");
			}else{
				PopupManagerUtil.getInstence().closeLoading();
				Alert.show("设备增加失败","提示");
			}
		}
		private function onPostDeviceFault(e:Event):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			trace("添加设备失败");
		}
		/***
		 * 真实工程：请求Tunnel与flow关系tree连接成功
		 * */
		private function onGetTunnelFlowResult(e:HttpResponseEvent,data:String):void
		{
			if(e.response.code=="200"){
				if(data == ""){
					PopupManagerUtil.getInstence().closeLoading();	
					return;
				}else{
					DataHandleTool.tunnelFlowxml = new XML("<Tunnel>"+data+"</Tunnel>");
					PopupManagerUtil.getInstence().closeLoading();
					try
					{
						navPancel.g.expandAll();
						if(navPancel.adg_select.hasComplexContent()||navPancel.tunnelstate.length>0){
							__app.dispatchEvent(new SdncEvt(SdncEvt.CLICK_ADG_PANCEL,navPancel.adg_select));
						}else{
							__app.dispatchEvent(new SdncEvt(SdncEvt.CLICK_ADGFLOW_PANCEL,navPancel.adg_select.parent()))
						}
					} 
					catch(error:Error) 
					{
						
					}
				}
			}else{
				trace(e.type.toString())
			}
		}
		/***
		 * 真实工程：请求Tunnel与flow关系tree连接失败
		 * */
		private function onGetTunnelFlowFault(e:Event):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			trace("获取管道信息连接出错"+e.toString(),"提示");
		}
		protected function openSystemRollbackWindow(event:SdncEvt):void
		{
			if(systemRollbackWindow!=null)
			{
				return;
			}
			systemRollbackWindow= new SystemRollback;
			systemRollbackWindow.horizontalCenter="0";
			systemRollbackWindow.verticalCenter="0";
			page.addElement(systemRollbackWindow);
		}
		protected function closeSystemRollbackWindow(event:SdncEvt):void
		{
			if(systemRollbackWindow!=null)
			{
				page.removeElement(systemRollbackWindow);
				page.mouseChildren=true;
				systemRollbackWindow=null;
			}
		}
		/**
		 *更改有无地图的状态 
		 */
		public function switchState():void{
			if(page.currentState=="widthoutMap"){
				page.currentState="withMap";
				page.mapbackground.visible=true;
			}else{
				page.currentState="widthoutMap";
				page.mapbackground.visible=false;
			}
		}
	}
	
	
}