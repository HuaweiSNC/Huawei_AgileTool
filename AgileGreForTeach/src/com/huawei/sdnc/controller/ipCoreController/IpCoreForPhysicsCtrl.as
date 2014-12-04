package com.huawei.sdnc.controller.ipCoreController
{
	import com.huawei.sdnc.controller.SaveTopoLocation;
	import com.huawei.sdnc.event.SdncEvt;
	import com.huawei.sdnc.model.Data;
	import com.huawei.sdnc.model.Device;
	import com.huawei.sdnc.model.QosListCalculate;
	import com.huawei.sdnc.service.SdnService;
	import com.huawei.sdnc.service.SdnUIService;
	import com.huawei.sdnc.tools.ConnUtil;
	import com.huawei.sdnc.tools.PopupManagerUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.common.node.StateNode;
	import com.huawei.sdnc.view.gre.MyLink;
	import com.huawei.sdnc.view.gre.PhysicsView;
	import com.huawei.sdnc.view.gre.dataHandle.DataHandleTool;
	
	import flash.events.ContextMenuEvent;
	import flash.events.Event;
	import flash.geom.Point;
	import flash.system.Capabilities;
	import flash.ui.ContextMenu;
	import flash.ui.ContextMenuItem;
	import flash.utils.ByteArray;
	
	import mx.controls.Alert;
	import mx.events.FlexEvent;
	import mx.events.PropertyChangeEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.httpclient.events.HttpResponseEvent;
	
	import twaver.Collection;
	import twaver.Consts;
	import twaver.Defaults;
	import twaver.ElementBox;
	import twaver.ICollection;
	import twaver.IData;
	import twaver.IElement;
	import twaver.Link;
	import twaver.Node;
	import twaver.Styles;
	import twaver.network.layout.AutoLayouter;
	import twaver.networkx.NetworkX;

	public class IpCoreForPhysicsCtrl
	{
		public function IpCoreForPhysicsCtrl()
		{
		}
		public var physicsView:PhysicsView;
		public var autoLayouter:AutoLayouter;
		
		private var __app:sdncui2;
		private var projectType:String;
		private var networkX:NetworkX;
		private var sdnService:SdnUIService = new SdnUIService();
		private var nodeArray:Array=[];
		//请求过gre数据的设备数
		public var greInitNum:int=0;
		//请求过qos数据的设备数
		public var initQosNum:int=0;
		public var qosCalculate:QosListCalculate = new QosListCalculate();
		
		
		public function onInit():void
		{
			__app = SdncUtil.app;
			projectType = SdncUtil.cuProjectType;
			networkX=physicsView.networkX;
		}
		/**
		 * 物理视图初始化完成后执行
		 * @param event
		 * 
		 */		
		private var now:Date;
		private var v1:Number;
		private var v2:Number;
		private var refreshTopo:IpcoreRefresh;
		public function creationComplete(event:FlexEvent):void
		{
			now = new Date();
			v1 = now.valueOf();
			SdncUtil.addOverview(networkX);
			SdnUIService.ipcorePhysicsCtrl=this;
			TestProjectCtrl.networkX=networkX;
			TestProjectCtrl.ipcorePhysicsCtrl=this;
			
			
			var uri:String="";
			if(projectType=="test"){
				uri="assets/xml/dci/test_tnl.xml";
				sdnService.ipcoreRequest(uri, TestProjectCtrl.onResultForTest, onRequestOpsDeafalt);
			}else{
				var opsIp:String=SdncUtil.opsIp;
				uri="http://"+opsIp+"/devices";
				//查询gre
				sdnService.ipcoreRequest(uri, onResultForClient, onRequestOpsDeafalt);
				PopupManagerUtil.getInstence().popupLoading(physicsView,false);
			}
			
			autoLayouter = new AutoLayouter(networkX);
			
			/**初始化右键菜单*/
			networkX.contextMenu = new ContextMenu();
			networkX.contextMenu.hideBuiltInItems();	
//			networkX.elementBox.addDataPropertyChangeListener(function(e:PropertyChangeEvent):void
//			{
//				if (String(e.property).search("expanded") != -1)
//				{
//					var link:MyLink=e.source as MyLink;
//					if(link){
//						var expanded:Boolean = link.getStyle(Styles.LINK_BUNDLE_EXPANDED);//通过此句可以获取当前link是否是bundle状态。
//						if(link.getStyle(Styles.LINK_BUNDLE_GAP)==SdncUtil.linkGapMax){
//							link.setStyle(Styles.LINK_BUNDLE_EXPANDED,true);
//							link.setStyle(Styles.LINK_BUNDLE_GAP,SdncUtil.linkGapMin);
//						}else if(link.getStyle(Styles.LINK_BUNDLE_GAP)==SdncUtil.linkGapMin){
//							link.setStyle(Styles.LINK_BUNDLE_EXPANDED,true);
//							link.setStyle(Styles.LINK_BUNDLE_GAP,SdncUtil.linkGapMax);
//						}
//					}
//				}
//			});
			Defaults.LINK_BUNDLE_AGENT_FUNCTION = function(links:Collection):Link{
				var result:Link=links.getItemAt(0);
				for(var i:int=0;i<links.count;i++){
					var l:Link = links.getItemAt(i);
					var agent:Boolean=l.getClient("agent");
					if(agent){
						result=l;
					}
				}
				return result;
			}
			
			networkX.contextMenu.addEventListener(ContextMenuEvent.MENU_SELECT, function(e:ContextMenuEvent):void{			
				var p:Point = new Point(e.mouseTarget.mouseX / networkX.zoom, e.mouseTarget.mouseY / networkX.zoom);		 	 	
				var datas:ICollection = networkX.getElementsByLocalPoint(p);
				if (datas.count > 0) {
					networkX.selectionModel.setSelection(datas.getItemAt(0));
				}else{
					networkX.selectionModel.clearSelection();
				}				
				//if(networkX.selectionModel.count == 0){
				//		networkX.contextMenu.customItems = [];
				//}else{
//				var item1:ContextMenuItem = new ContextMenuItem("定义管道");
				var item1:ContextMenuItem = new ContextMenuItem("管理设备");
				item1.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, handler);
//				var item2:ContextMenuItem = new ContextMenuItem("定义流", true);
				var item2:ContextMenuItem = new ContextMenuItem("刷新topo", true);
				item2.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, handler);
//				var item3:ContextMenuItem = new ContextMenuItem("导流入管道");
				var item3:ContextMenuItem = new ContextMenuItem("保存布局");
				item3.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, handler);
				networkX.contextMenu.customItems = [item1, item2, item3];  
//				var item4:ContextMenuItem = new ContextMenuItem("路经计算");
//				item4.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, handler);
//				var item5:ContextMenuItem = new ContextMenuItem("清除路径");
//				item5.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, handler);
//				networkX.contextMenu.customItems = [item1, item2, item3,item4,item5];  
				networkX.contextMenu.customItems = [item1, item2, item3];  
				//}
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
					//for(i=0; i<networkX.selectionModel.selection.count; i++){
					var p:Point = new Point(e.mouseTarget.mouseX / networkX.zoom, e.mouseTarget.mouseY / networkX.zoom);
					var datas:ICollection = networkX.getElementsByLocalPoint(p);
					if(datas.count>0)
						element = datas.getItemAt(0);
					else
						element=null;
					
					if(item.caption == "定义管道"){
						SdncUtil.app.dispatchEvent(new SdncEvt(SdncEvt.OPEN_PIPELINE_DEFINE,element));
					}
					else if(item.caption == "定义流"){
						SdncUtil.app.dispatchEvent(new SdncEvt(SdncEvt.OPEN_FLOW_EDIT,element));
					}
					else if(item.caption == "导流入管道"){
						//打开流导入
						SdncUtil.app.dispatchEvent(new SdncEvt(SdncEvt.OPEN_FLOW_ENTER,element));
					}
					else if(item.caption == "管理设备"){
						//打开流导入
						SdncUtil.app.dispatchEvent(new SdncEvt(SdncEvt.OPEN_SETUP_DEVICES_WINDOW))
					}else if(item.caption == "刷新topo"){
						//打开流导入
						refreshTopo = new IpcoreRefresh();
						refreshTopo.init();
					}
					else if(item.caption == "保存布局"){
						var saveTopoLocation:SaveTopoLocation=new SaveTopoLocation;
						saveTopoLocation.saveLocation(networkX);
					}
//					else if(item.caption == "清除路径"){
//						SdncUtil.app.dispatchEvent(new SdncEvt(SdncEvt.CLEAR_PATH,element));
//					}
				}
			}
				
		}
		
		/**
		 * 从ops读取的设备信息转换成设备数组
		 */	
		public function onResultForClient(e:HttpResponseEvent, data1:ByteArray):void
		{
			var data:String=data1.toString();
			if(data==null||data==""){
				Alert.show("当前没有设备","提示");
				PopupManagerUtil.getInstence().closeLoading();
				return;
			}
			var devices:Array=DataHandleTool.handleDeviceData(data);
			if(devices == null||devices.length==0){
				Alert.show("当前没有设备","提示");
				PopupManagerUtil.getInstence().closeLoading();
				return;
			}
			nodeArray=[];
			networkX.elementBox.clear();
			for(var i:int=0;i<devices.length;i++){
				var stateNode:StateNode=new StateNode;
				stateNode.setStyle(Styles.LABEL_POSITION,Consts.POSITION_BOTTOM_BOTTOM);
				stateNode.setStyle(Styles.LABEL_COLOR,0xffffff);
				nodeArray.push(stateNode);
				var obj:Object=devices[i] as Object;
				var username:String=obj["username"];
				var devicename:String=obj["devicename"];
				var passwd:String=obj["passwd"];
				var ip:String=obj["ip"];
				var version:String=obj["version"];
				var productType:String=obj["productType"];
				var id:String=obj["id"];
				
				stateNode.nodeId=Number(id);
				stateNode.nodeType=productType;
				stateNode.systemName=devicename;
				stateNode.name=devicename;
				stateNode.setClient("username",username);
				stateNode.setClient("devicename",devicename);
				stateNode.setClient("passwd",passwd);
				stateNode.setClient("ip",ip);
				stateNode.setClient("version",version);
				stateNode.setClient("productType",productType);
				stateNode.setClient("id",id);
				stateNode.image="icon_core_ipcore";
				networkX.elementBox.add(stateNode);
			}
			var layout:LayoutTopo = new LayoutTopo;
			layout.handleLayout(networkX,function():void{
				//根据tnl创建连线
				var opsIp:String=SdncUtil.opsIp;
				__app.dispatchEvent(new SdncEvt(SdncEvt.OPEN_CONSOLE));
				sumDeviceNum=0;
				networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
					if(item is StateNode){
						sumDeviceNum++;
						var sn:StateNode=item as StateNode;
						var device:Device=new Device;
						sn.setClient("device",device);
						device.stateNode=sn;
						device.initDevice(add);
					}
				});
				
				DataHandleTool.stateNodesArr=nodeArray;
			});
			
			//qosCalculate.Calculate();
			//qosCalculate.qosStartArr;
			
		}
		//总的设备数
		private var sumDeviceNum:int=0;
		private function add():void
		{
			greInitNum++;
			trace("已经初始化的设备数：       "+greInitNum);
			if(DataHandleTool.console!=null){
				var content:String="已经初始化的设备数：       "+greInitNum+"\n";
				DataHandleTool.console.console.text+=content;
			}
			
			if(greInitNum==sumDeviceNum){
				drawLink();
			
			now = new Date();
			v2=now.valueOf();
			var t:Number = (v2-v1)/1000;
			trace("初始化总时间：：：：：：："+t+"s");
			PopupManagerUtil.getInstence().closeLoading();
			}
		}
		private var qosis:Boolean=false;
		private var greis:Boolean=false;
		private var aclis:Boolean=false;
		
		/**
		 * 当初始化所有设备完成后 开始画线
		 */
		public function drawLink():void
		{
			greInitNum=0;
			DataHandleTool.createLinkByTnlForNormal(networkX);
			PopupManagerUtil.getInstence().closeLoading();
			var opsIp1:String = SdncUtil.opsIp;
			var url:String = "http://"+opsIp1+"/StatisticsState.xml";
			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_GET,getAlgResult,getAlgFault);
		}
		private function getAlgResult(e:HttpResponseEvent,data:ByteArray):void
		{
			if(data.toString()!=""){
				var tree:XML = XML(data.toString());
				var mydata:Data= Data.getInstence();
				var delay:String = tree.delay.children()[0].toString();
				var flow:String = tree.flow.children()[0].toString();
				var cpu:String = ""//tree.cpu.children()[0].toString();
				if(delay == "on"){
					mydata.delayState=true;
					__app.dispatchEvent(new SdncEvt(SdncEvt.SWITCH_NQA,true));
				}else{
					mydata.delayState=false;
					__app.dispatchEvent(new SdncEvt(SdncEvt.SWITCH_NQA,false));
				}
				if(flow == "on"){
					mydata.flowState = true;
					__app.dispatchEvent(new SdncEvt(SdncEvt.SWITCH_FLOW,true));
				}else{
					mydata.flowState =false;
					__app.dispatchEvent(new SdncEvt(SdncEvt.SWITCH_FLOW,false));
				}
				if(cpu == "on"){
					mydata.cpuState = true;
					__app.ipcore.physicsView.networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
						if(item is StateNode)
							item.setClient("state",true);
					});
					__app.dispatchEvent(new SdncEvt(SdncEvt.REFRESH_NODES_CPU_RAM,"ON"));
				}else{
					mydata.cpuState =false;
					__app.ipcore.physicsView.networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
						if(item is StateNode)
							item.setClient("state",false);
					});
					__app.dispatchEvent(new SdncEvt(SdncEvt.REFRESH_NODES_CPU_RAM,"OFF"));
				}
			}
		}
		
		private function getAlgFault(e:Event):void
		{
			Alert.show("连接失败！","提示")
		}
		
		/**
		 * 当请求ops错误时触发的事件函数
		 * @param e
		 * 
		 */		
		private function onRequestOpsDeafalt(e:Event):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			Alert.show("ops连接出错","提示");
		}
		
		/**
		 *更改有无地图的状态 
		 * 
		 */
		public function switchState():void{
			var ebox:ElementBox=networkX.elementBox;
			if(physicsView.currentState=="withoutMap"){
				physicsView.currentState="withMap";
				physicsView.mapbackground.visible=true;
			}else if(physicsView.currentState=="withMap"){
				physicsView.currentState="withoutMap";
				physicsView.mapbackground.visible=false;
			}
		}
		public function switchNet():void{
			if(physicsView.netgroup.show==true){
				physicsView.shownet.visible = false
				physicsView.netgroup.show = false
				physicsView.netgroup.changeState(true)
			}else{
				physicsView.shownet.visible = true
				physicsView.netgroup.show = true
				physicsView.netgroup.changeState(false)
			}
		}
		
	}
}