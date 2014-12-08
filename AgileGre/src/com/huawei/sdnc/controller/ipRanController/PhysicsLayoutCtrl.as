package com.huawei.sdnc.controller.ipRanController
{
	import baidu.map.basetype.LngLat;
	import baidu.map.basetype.Size;
	import baidu.map.core.Map;
	import baidu.map.event.MapEvent;
	import baidu.map.layer.Layer;
	import baidu.map.layer.RasterLayer;
	
	import com.huawei.sdnc.event.SdncEvt;
	import com.huawei.sdnc.service.SdnService;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.tools.TopoUtil;
	import com.huawei.sdnc.view.ipRan.physics.AddNewDevice;
	import com.huawei.sdnc.view.ipRan.physics.DashLink;
	import com.huawei.sdnc.view.ipRan.physics.DetailInfo;
	import com.huawei.sdnc.view.ipRan.physics.GradientLink;
	import com.huawei.sdnc.view.ipRan.physics.PhysicsLayoutTool;
	import com.huawei.sdnc.view.ipRan.physics.PhysicsView;
	import com.huawei.sdnc.vo.TopoXmlVo;
	
	import flash.events.ContextMenuEvent;
	import flash.events.Event;
	import flash.events.FullScreenEvent;
	import flash.events.MouseEvent;
	import flash.geom.Point;
	import flash.sampler.startSampling;
	import flash.utils.Dictionary;
	
	import mx.controls.Alert;
	import mx.effects.AnimateProperty;
	import mx.effects.CompositeEffect;
	import mx.effects.Parallel;
	import mx.events.DragEvent;
	import mx.events.EffectEvent;
	import mx.events.MoveEvent;
	import mx.events.ResizeEvent;
	
	import spark.components.Button;
	
	import twaver.Collection;
	import twaver.Consts;
	import twaver.ElementBox;
	import twaver.IData;
	import twaver.IElement;
	import twaver.Link;
	import twaver.Node;
	import twaver.ShapeNode;
	import twaver.Styles;
	import twaver.Utils;
	import twaver.network.Network;
	import twaver.network.Overview;
	import twaver.networkx.NetworkX;

	public class PhysicsLayoutCtrl
	{
		
		
		public var page:PhysicsView;
		public var eBox:ElementBox=null;
		public var map:Map=null;
		public var network:NetworkX;
		public var curstate:String="withMap";
		public var __app:sdncui2;
		public var breakAddNode:Break_addNode;
		
		private var lngDics:Dictionary=new Dictionary();
		private var nodeLocations:Dictionary=new Dictionary();
		private var nodeDics:Dictionary=new Dictionary();
		private var sdnService:SdnService = new SdnService();
		private var topoXmlVo:TopoXmlVo;
		
		private var lastElement:IElement=null;
		private var tool:PhysicsLayoutTool=new PhysicsLayoutTool;
		private var asgRsgCsgNodes:Array=[];
		private var xLinks:Array=[];
		private static const  LINEAR_LINK:int=1; 
		private static const  CIRCLE_LINK:int=2;
		private var network_noMap:NetworkX;
		
		public function PhysicsLayoutCtrl()
		{
			
		}
		
		public function init():void
		{
			__app=SdncUtil.app;
			network=page.network;
			network.showVerticalScrollBar=false;
			network_noMap=page.network_noMap;
			eBox=new ElementBox;
			network.elementBox=eBox;
			network_noMap.elementBox=eBox;
			tool.network=network;
			tool.network_noMap=network_noMap;
			tool.eBox=eBox;
			//SdncUtil.addOverview(network);
			
			page.breakAddNode.eBox=eBox;
			page.breakAddNode.network=network_noMap;
			page.breakAddNode.initNetworkDragAndDropListener();
			page.ctrlVisiSwitch.eBox = eBox;
			initMap();
			initBox();
		}
		
		
		
		private function initMap():void
		{
			var width:Number=page.mapE.width;
			var height:Number=page.mapE.height;
			//map = new Map(new Size(1800, 1050));
			map = new Map(new Size(width, 1050));
			page.mapE.addChild(map);
			map.centerAndZoom(new LngLat(116.404, 39.915), 12);
			map.enableDragging = true;
			map.enableInertialDragging = true;
			map.enableScrollWheelZoom = true;
			map.enableDoubleClickZoom = false;
			map.enableContinuousZoom = false;
			map.enableKeyboard = false;
			map.addEventListener(MapEvent.ZOOM_CHANGED,function(evt:MapEvent):void{
				setNodeLocation();
			});
			
			map.addEventListener(MapEvent.MOVE_END,function(evt:MapEvent):void{
				setNodeLocation();
			});
			
			var layer:Layer = new RasterLayer("BaiduMap", map);
			map.addLayer(layer);
			
		}
		
		private function initBox():void
		{
			sdnService._topoXmlVo = new TopoXmlVo();
			sdnService._topoXmlVo.addEventListener(SdncEvt.L3TOPO_DATA_IS_READY,handleL3topo);
			sdnService.l3topoInfoQuery("assets/xml/sdn_l3topo.xml");
		}
		
		private function handleL3topo(e:SdncEvt):void
		{
			topoXmlVo=e.currentTarget as TopoXmlVo;
			for each(var nXml:XML in topoXmlVo._l3topoXml.l3toponodes.l3toponode)
			{
				var id:String = nXml.routerID;
				var lngLat:LngLat = new LngLat(nXml.longitude,nXml.latitude);
				lngDics[id] = lngLat;
				var point:Point = getPointByLng(map,lngLat);
				nodeLocations[id] = point;
				
				var node1:Node= TopoUtil.createNode(eBox,Node,id,nXml.systemName,nXml.nodeType,point);
				
				node1.setClient("x",nXml.x);
				node1.setClient("y",nXml.y);
				node1.setClient("nodeType",nXml.nodeType);
				node1.setClient("lngLat",lngLat);
				if(nXml.nodeType!="core"&&nXml.nodeType!="n")
					asgRsgCsgNodes.push(node1);
				nodeDics[id] = node;
				if(node1.getClient("nodeType") == "core"){
					node1.image = "core_withmap";
				}else if(node.getClient("nodeType") == "n"){
					node1.image = "basesta_withmap";
				}else{
					node1.image = "node_withmap";
				}
			}
			
			for each(var lXml:XML in topoXmlVo._l3topoXml.l3topolinks.l3topolink)
			{
				var fromN:Node = TopoUtil.getNodeById(eBox, lXml.leftRoterID);
				var toN:Node = TopoUtil.getNodeById(eBox, lXml.rigthRouterID);
				createLink(fromN,toN,LINEAR_LINK);
			}
			var coreNode:Node=eBox.getElementByID("1") as Node;
			for each(var node:Node in asgRsgCsgNodes)
			{
				var l:Link=TopoUtil.createLinkDash(eBox,coreNode,node);
				l.setClient("isXX",true);
				xLinks.push(l);
			}
			network.dragToPan=false;
			network.wheelToZoom=false;
			network.movableFunction=function(item:IElement):Boolean
			{
				return false;
			}
			network_noMap.dragToPan=true;
			network_noMap.wheelToZoom=true;
			network_noMap.movableFunction=function(item:IElement):Boolean
			{
				return true;
			}
			tool.showPath();
			setDetailWindow();
		}

		
		
		public function drawTopoWithoutMap():void
		{
			network.visible=false;
			network_noMap.visible=true;
			detail.visible=false;
			eBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is Node){
					var node:Node=item as Node;
					node.setStyle(Styles.LABEL_COLOR,0xffffff);
					node.setLocation(node.getClient("x"),node.getClient("y"));
					if(node.getClient("nodeType") == "core"){
						node.image = "core";
					}else if(node.getClient("nodeType") == "n"){
						node.image = "n";
					}else{
						node.image = "asg";
					}
				}
				else if(item is Link)
				{
					var link:Link=item as Link;
					if(link.getClient("isXX")!=null&&link.getClient("isXX"))
					{
						return;
					}
					var f:Node=link.fromNode;
					var t:Node=link.toNode;
					if(!(f.getClient("nodeType")=="core"||t.getClient("nodeType")=="core"||f.getClient("nodeType")=="n"||t.getClient("nodeType")=="n")){
					eBox.remove(item);
					var l:Link = createLink(f,t,CIRCLE_LINK);
					if(page.withoutmapbtn.currentState == "closectrl")
					{
						if(f.getClient("nodeType")=="core"||t.getClient("nodeType") == "core"){
							l.setStyle(Styles.LINK_ALPHA,0);
						}	
					}
					
					}
					link.setStyle(Styles.LINK_COLOR,0x03bbff);
				}
			});
			page.breakAddNode.init();
		}
		
		public function drawTopoWithMap():void
		{
			detail.visible=false;
			tool.clearCanvas(new ContextMenuEvent(""));
			network.visible=true;
			network_noMap.visible=false;
			network.movableFunction=function(item:IElement):Boolean
			{
				return false;
			}
			eBox.forEachByBreadthFirst(function(item:IData):void{
			if(item is Node){
				var node:Node=item as Node;
				node.setStyle(Styles.LABEL_COLOR,0x123456);
				
				if(node.getClient("nodeType") == "core"){
					node.image = "core_withmap";
				}else if(node.getClient("nodeType") == "n"){
					node.image = "basesta_withmap";
				}else{
					node.image = "node_withmap";
				}
				var lngLat:LngLat=node.getClient("lngLat");
				if(lngLat==null)
					return;
				var point:Point = getPointByLng(map,lngLat);
				node.location=point;
			}
			else if(item is Link)
			{
				var link:Link=item as Link;
				if(link.getClient("isXX")!=null&&link.getClient("isXX"))
				{
					return;
				}
				var f1:Node=link.fromNode;
				var t1:Node=link.toNode;
				eBox.remove(item);
				var l:Link = createLink(f1,t1,LINEAR_LINK);
			if(page.withmapbtn.currentState == "closectrl")
			{
				if(f1.getClient("nodeType")=="core"||t1.getClient("nodeType") == "core"){
					l.setStyle(Styles.LINK_ALPHA,0);
				}	
			}
			}
			});
		}
		
		
		public function movemap(e:int):void
		{
			switch(e){
				case 0:map.panBy(0, 100, {time:100, count:3});
					break;
				case 1:map.panBy(0, -100, {time:100, count:3});
					break;
				case 2:map.panBy(100, 0, {time:100, count:3});
					break;
				case 3:map.panBy(-100, 0, {time:100, count:3});
					break;
			}
		}
		
		private function getPointByLng(map1:Map,lng:LngLat):Point
		{
			var point:Point = map1.lnglatToPixel(lng);
			point.x = point.x - 23;
			point.y = point.y - 45;
			return point;
		}
		
		private function setNodeLocation():void
		{
			for each(var node:Node in nodeDics)
			{
				node.location = getPointByLng(map,lngDics[node.id]);
			}
		}
		private function createLink(from:Node,to:Node,linkType:int=2):Link{
			if(linkType == LINEAR_LINK){
				var linearLink:Link = new Link(from,to);
				linearLink.setStyle(Styles.LINK_WIDTH,2);
				eBox.add(linearLink);
				return linearLink;
			}
			var link:GradientLink=new GradientLink(from,to);
			link.setStyle(Styles.SHAPELINK_TYPE, Consts.SHAPELINK_TYPE_QUADTO);
			//var link:CircleLink = new CircleLink(from,to);
			eBox.add(link);
			return link;
		}
		private var detail:DetailInfo;
		private function setDetailWindow():void
		{
		    detail=new DetailInfo();
			page.addElement(detail);
			network.addEventListener(MouseEvent.DOUBLE_CLICK, function(e:MouseEvent):void {
				var element:IElement = network.getElementByMouseEvent(e);
				if(lastElement == element){
					return;
				}
				lastElement = element;
				if(element is Node){
					var point:Point = network.getLogicalPoint(e);
					detail.x = point.x - detail.measuredWidth / 2-100;
					detail.y = point.y - detail.measuredHeight +20;
					detail.visible = true;
				}else{
					detail.visible = false;
				}
			});
//			network.addEventListener(MouseEvent.CLICK,function(e:MouseEvent):void{
//				var element:IElement = network.getElementByMouseEvent(e);
//				lastElement=null;
//				if(detail.visible==true)
//					detail.visible=false;
//				
//			});
			detail.visible=false;
		}
		
	}
}