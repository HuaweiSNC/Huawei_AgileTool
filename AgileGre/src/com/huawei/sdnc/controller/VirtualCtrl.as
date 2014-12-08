package com.huawei.sdnc.controller
{
	import com.huawei.sdnc.event.SdncEvt;
	import com.huawei.sdnc.tools.ConnUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.tools.TopoUtil;
	import com.huawei.sdnc.tools.VdcManagerUtil;
	import com.huawei.sdnc.view.common.node.CurveLink;
	import com.huawei.sdnc.view.dataCenter.virtural.VirtualView;
	import com.huawei.sdnc.vo.TopoXmlVo;
	
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.geom.Point;
	import flash.utils.ByteArray;
	
	import mx.controls.Alert;
	import mx.events.FlexEvent;
	import mx.events.PropertyChangeEvent;
	
	import org.httpclient.events.HttpDataEvent;
	
	import spark.components.Label;
	
	import twaver.Collection;
	import twaver.Consts;
	import twaver.ElementBox;
	import twaver.Follower;
	import twaver.ICollection;
	import twaver.IData;
	import twaver.IElement;
	import twaver.Link;
	import twaver.Node;
	import twaver.ShapeNode;
	import twaver.Styles;
	import twaver.Utils;
	import twaver.networkx.NetworkX;
	import twaver.networkx.interaction.InteractionEvent;

	public class VirtualCtrl
	{
		[Bindable]
		public var page:VirtualView;
		
		private var __box:ElementBox;
		private var __netWork:NetworkX;
		private var __lastElement:IElement = null;
		/**云形状节点*/
		private var __track:ShapeNode = new ShapeNode();
		/**节点背景*/
		private var __trackBackground:ShapeNode = new ShapeNode();
		private var tip:Label;
		private var __curVdcName:String;
		private var __cloudN:Node;
		[Bindable]
		public var connUtil:ConnUtil = ConnUtil.getInstence();
		[Bindable]
		public var vdcManager:VdcManagerUtil = VdcManagerUtil.getInstence();
		public function VirtualCtrl()
		{
		}
		
		/**
		 * 初始化组件 
		 */
		public function onInit(event:FlexEvent):void
		{
			__box = page.dataBox as ElementBox;
			__netWork = page.network;
			__netWork.elementBox = __box;
			__netWork.innerColorFunction = null;
			SdncUtil.addOverview(page.network);
			page.addEventListener(SdncEvt.POPUP_CREATEVDC_WINDOW,page.showCreateVdcWindow);
			SdncUtil.app.addEventListener(SdncEvt.VIRTUALVIEW_VDC_CHANGE,onVdcChange);
			__netWork.movableFunction = function(e:IElement):Boolean {
				return false;
			}
			__netWork.addEventListener(MouseEvent.MOUSE_MOVE, function(evt:MouseEvent):void{
				__lastElement = SdncUtil.handleMouseMove(evt,__netWork,__lastElement);
			});
			page.network.addInteractionListener(function(e:InteractionEvent):void{
				if(e.kind == InteractionEvent.CLICK_ELEMENT) {}
				if(e.kind == InteractionEvent.DOUBLE_CLICK_ELEMENT)
				{
					if(e.element is Node)
					{
						var node:Node = e.element as Node;
						if(node.image == "CloudGroup")
						{
							if(SdncUtil.cuProjectType == "normal")
							{ 
								if(!vdcManager.hosts) 
									Alert.show("/os-hosts no return");
								else if(!vdcManager.vms)
									Alert.show("/servers/detail no return");
								else
									SdncUtil.app.main.virtualView.dispatchEvent(new SdncEvt(SdncEvt.POPUP_CREATEVDC_WINDOW));
							}
							else
								SdncUtil.app.main.virtualView.dispatchEvent(new SdncEvt(SdncEvt.POPUP_CREATEVDC_WINDOW));
						}
					}
				}
			});
//			initBox();
			SdncUtil.app.dispatchEvent(new SdncEvt(SdncEvt.VIRTUALVIEW_VDC_CHANGE,
				{dcName:SdncUtil.dcHasDataArr[0],vdcName:SdncUtil.vdcNameArr[0]}));
			
			if(SdncUtil.cuProjectType == "normal")
			{
				if(connUtil.tokentId)
				{
					connUtil.sendRequest(connUtil.computeUrl + connUtil.hostsUrl,null,
						function(e:HttpDataEvent):void{
							var str:String = SdncUtil.convertByteArrayToString(e.bytes);
							trace("hosts:" + str);
							vdcManager.hosts = JSON.parse(str) as Object;
						},function(e:*):void{
							Alert.show(e.text);
						},connUtil.tokentId); 
					connUtil.sendRequest(connUtil.computeUrl + connUtil.serverUrl,null,
						function(e:HttpDataEvent):void{
							var str:String = SdncUtil.convertByteArrayToString(e.bytes);
							trace("vms:" + str);
							vdcManager.vms = JSON.parse(str) as Object;
						},function(e:*):void{
							Alert.show(e.text);
						},connUtil.tokentId);
				}
				else
				{
					Alert.show("tokentId is null");
				}
			}
		}
		/**
		 * 切换VDC时触发的监听
		 */
		protected function onVdcChange(event:SdncEvt):void
		{
			initBox();
			SdncUtil.currentDcName = event.params.dcName;
			__curVdcName = event.params.vdcName;
			
			var topoXmlVo:TopoXmlVo = SdncUtil.dcTopoXmlDic[SdncUtil.currentDcName];
			if(topoXmlVo) onTopoByXML(__box,topoXmlVo._vnmXml,__curVdcName);
			else Alert.show("no data!");
			__cloudN.name = __curVdcName;
		}
		/**
		 * 初始化box
		 */
		private function initBox():void
		{
			__box.clear();
			var points:ICollection = new Collection([
				new Point(592, 412),
				new Point(650, 128),new Point(960,284),
				new Point(1107,298), 	new Point(1121,406),
				new Point(1247,531), new Point(1107,654),
				new Point(594,654),
				new Point(453,529),new Point(592, 412)
			]);
			__track.points = points;
			__track.segments = new Collection([
				Consts.SEGMENT_MOVETO,
				Consts.SEGMENT_QUADTO,
				Consts.SEGMENT_QUADTO,
				Consts.SEGMENT_QUADTO,
				Consts.SEGMENT_LINETO,
				Consts.SEGMENT_QUADTO
			]);
			__track.setStyle(Styles.VECTOR_OUTLINE_WIDTH, 3.0);
			__track.setStyle(Styles.VECTOR_OUTLINE_COLOR, 0x6e95bf);
			__track.setStyle(Styles.VECTOR_OUTLINE_ALPHA, 0.8);
			__track.setStyle(Styles.VECTOR_ROUNDRECT_RADIUS, 5);
			__track.setStyle(Styles.VECTOR_FILL, false);
			__track.setStyle(Styles.SELECT_STYLE,Consts.SELECT_STYLE_NONE);
			__track.layerID = "1";
			__box.add(__track);
			
			__cloudN = TopoUtil.createNode(__box,Node,"cloudN","","CloudGroup");
			__cloudN.name = __curVdcName;
			var trackPoint:Point = __track.centerLocation;
			__track.setCenterLocation(trackPoint.x,trackPoint.y - 60);
			__cloudN.setCenterLocation(__track.centerLocation.x,__track.centerLocation.y + 60);
			__cloudN.setStyle(Styles.SELECT_STYLE,Consts.SELECT_STYLE_NONE);
			__cloudN.setStyle(Styles.LABEL_PADDING_BOTTOM,280);
			__cloudN.setStyle(Styles.LABEL_SIZE,50);
			__cloudN.layerID = "2";
		}
		
		/**
		 * 根据XML里处理当前network
		 * @param box:输入数据的box
		 * @param cuVnmXml:虚拟视图的关系XML
		 * @param curVdcName:当前选中的VDC名字
		 */
		private function onTopoByXML(box:ElementBox,cuVnmXml:XML,curVdcName:String):void
		{
			if(!cuVnmXml) return;
			for each(var network:XML in cuVnmXml.network)
			{
				var gatewayNum:int = network.subnets.subnet.length();
				var portNum:int = network.ports.port.length();
				var gatewayOffset:Number = 740 / gatewayNum;
				var portOffset:Number = 1100 / (portNum + 1);
				if(network.networkName == curVdcName)
				{
					var dis:Number = 130;
					for each(var gatewayPort:XML in network.subnets.subnet)
					{
						var gatewayNode:Follower = TopoUtil.createFollower(box,gatewayPort.subnetID,gatewayPort.subnetName,"getewayport");
						gatewayNode.host = __track;
						var gatewayLoc:Array = Utils.calculatePointAngleAlongLine(__track.points,__track.segments,true,dis);
						gatewayNode.centerLocation = gatewayLoc[0];
						dis += gatewayOffset;
					}
					dis = portOffset;//290;
					for each(var port:XML in network.ports.port)
					{
						var portNode:Follower = TopoUtil.createFollower(box,port.portID,port.portName,"port");
						portNode.host = __track;
						var portLoc:Array = Utils.calculatePointAngleAlongLine(__track.points,__track.segments,false,dis);
						portNode.centerLocation = portLoc[0];
						if(port.hasOwnProperty("attachs"))
						{
							onVM(box,port.attachs,portNode,dis);
						}
						dis += portOffset;//200;
					}
					if(dis > __track.lineLength)
					{
						dis = 0;
					}
				}
			}
		}
		/**
		 * 创建VM节点
		 * @param box:输入数据的box
		 * @param xml:每个VN的信息XML
		 * @param port:VM连接的port
		 * @param dis:节点之间相距的距离
		 */
		private function onVM(box:ElementBox,xml:XMLList,port:Follower,dis:Number):void
		{
			for each(var cuXml:XML in xml.attach)
			{
				var portCenterPoint:Point = port.centerLocation;
				var px:Number = 0;
				var py:Number = 0;
				var gap:Number = 120;
				if(dis < 290)
				{
					px = portCenterPoint.x - gap;
					py = portCenterPoint.y;
				}
				else if(dis >= 290 && dis <= 800)
				{
					px = portCenterPoint.x;
					py = portCenterPoint.y + gap
				}
				else
				{
					px = portCenterPoint.x + gap;
					py = portCenterPoint.y;
				}
				var attach:Follower = TopoUtil.createFollower(box,cuXml.attachID,cuXml.attachName,"");
				attach.centerLocation = new Point(px,py);
				var link:Link = TopoUtil.createLink(box,port,attach);
				link.setStyle(Styles.LINK_BUNDLE_OFFSET, 0);
//				link.setStyle(Styles.ICONS_NAMES, ["port"]);
//				link.setStyle(Styles.ICONS_POSITION, Consts.POSITION_CENTER);
			}
		}
	}
}