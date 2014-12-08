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
	import com.huawei.sdnc.view.ipRan.physics.DetailInfo;
	import com.huawei.sdnc.view.ipRan.physics.GradientLink;
	import com.huawei.sdnc.view.ipRan.physics.PhysicsLayoutTool;
	import com.huawei.sdnc.view.ipRan.physics.PhysicsView;
	import com.huawei.sdnc.vo.TopoXmlVo;
	
	import flash.events.ContextMenuEvent;
	import flash.geom.Point;
	import flash.net.URLRequest;
	import flash.net.navigateToURL;
	import flash.ui.ContextMenu;
	import flash.ui.ContextMenuItem;
	import flash.utils.Dictionary;
	
	import mx.controls.Alert;
	
	import twaver.Consts;
	import twaver.ElementBox;
	import twaver.ICollection;
	import twaver.IData;
	import twaver.IElement;
	import twaver.Link;
	import twaver.Node;
	import twaver.Styles;
	import twaver.networkx.NetworkX;
	
	
	
	public class PhysicsLayoutCtrl1
	{
		
		
		public var page:PhysicsView;
		public var map:Map=null;
		public var network_map:NetworkX;
		public var eBox_map:ElementBox=null;
		private var network_noMap:NetworkX;
		private var eBox_noMap:ElementBox;
		public var __app:sdncui2;
		public var breakAddNode:Break_addNode;
		
		private var lngDics:Dictionary=new Dictionary();
		private var nodeLocations:Dictionary=new Dictionary();
		private var nodeDics:Dictionary=new Dictionary();
		private var sdnService:SdnService = new SdnService();
		private var topoXmlVo:TopoXmlVo;
		
		private var tool:PhysicsLayoutTool=new PhysicsLayoutTool;
		private var asgRsgCsgNodes:Array=[];
		private var xLinks:Array=[];
		private static const  LINEAR_LINK:int=1; 
		private static const  CIRCLE_LINK:int=2;
		private var autoLayout:IpRanAutoLayout;
		
		
		private var autolayout:IpRanAutoLayout=new IpRanAutoLayout;
		public function PhysicsLayoutCtrl1()
		{
			
		}
		
		public function init():void
		{
			__app=SdncUtil.app;
			network_map=page.network;
			network_map.showVerticalScrollBar=false;
			network_noMap=page.network_noMap;
			eBox_map=network_map.elementBox;
			eBox_noMap=network_noMap.elementBox;
			
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
//			map.enableDragging = true;
//			map.enableInertialDragging = true;
//			map.enableScrollWheelZoom = true;
//			map.enableDoubleClickZoom = false;
//			map.enableContinuousZoom = false;
//			map.enableKeyboard = false;
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
			network_map.contextMenu = new ContextMenu();
			network_map.contextMenu.hideBuiltInItems();	
			network_map.contextMenu.addEventListener(ContextMenuEvent.MENU_SELECT, function(e:ContextMenuEvent):void{
				var p:Point = new Point(e.mouseTarget.mouseX / network_map.zoom, e.mouseTarget.mouseY / network_map.zoom);		 	 	
				var datas:ICollection = network_map.getElementsByLocalPoint(p);
				network_map.contextMenu.customItems=[];
				if (datas.count > 0) {
					network_map.selectionModel.setSelection(datas.getItemAt(0));
				}else{
					network_map.selectionModel.clearSelection();
				}
				
				if(network_map.selectionModel.count != 0){
					for(var i:int=0; i<network_map.selectionModel.selection.count; i++){
						var ele:IElement = network_map.selectionModel.selection.getItemAt(i);
						if(ele is Node){
							var item1:ContextMenuItem = new ContextMenuItem("节点信息", true);
							item1.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, handler);
							network_map.contextMenu.customItems = [item1];
							break;
						}
					}
				}
			});
			
			var handler:Function=function(e:ContextMenuEvent):void
			{
				var element:IElement = null;
				var item:ContextMenuItem = ContextMenuItem(e.target);
				for(var i:int=0; i<network_map.selectionModel.selection.count; i++){
					element = network_map.selectionModel.selection.getItemAt(i);
					if(element is Node){
						var request:URLRequest=new URLRequest("http://189.32.95.14:8080/devices");
						navigateToURL(request);
						break;
					}
				}
			}
				
				
			sdnService._topoXmlVo = new TopoXmlVo();
			sdnService._topoXmlVo.addEventListener(SdncEvt.L3TOPO_DATA_IS_READY,handleL3topo);
			sdnService.l3topoInfoQuery("assets/xml/sdn_l3topo.xml");
			
			network_map.dragToPan=false;
			network_map.wheelToZoom=false;
			network_map.movableFunction=function(item:IElement):Boolean
			{
				return false;
			}
			network_noMap.dragToPan=true;
			network_noMap.wheelToZoom=true;
			network_noMap.movableFunction=function(item:IElement):Boolean
			{
				return true;
			}
			
			
		}
		
		private function handleL3topo(e:SdncEvt):void
		{
			layoutWithMap(e);
			physicsAutoLayout();
		}
		/**
		 * 带地图的布局
		 * */
		private function layoutWithMap(e:SdncEvt):void
		{
			topoXmlVo=e.currentTarget as TopoXmlVo;
			for each(var nXml:XML in topoXmlVo._l3topoXml.l3toponodes.l3toponode)
			{
				var id:String = nXml.routerID;
				var lngLat:LngLat = new LngLat(nXml.longitude,nXml.latitude);
				lngDics[id] = lngLat;
				var point:Point = getPointByLng(map,lngLat);
				nodeLocations[id] = point;
				
				var nod:Node= TopoUtil.createNode(eBox_map,Node,id,nXml.systemName,nXml.nodeType,point);
				nod.setClient("nodeType",nXml.nodeType);
				nod.setClient("lngLat",lngLat);
				if(nXml.nodeType!="core"&&nXml.nodeType!="n")
					asgRsgCsgNodes.push(nod);
				nodeDics[id] = nod;
				if(nod.getClient("nodeType") == "core"){
					nod.image = "core_withmap";
				}else if(nod.getClient("nodeType") == "n"){
					nod.image = "basesta_withmap";
				}else{
					nod.image = "node_withmap";
				}
			}
			
			for each(var lXml:XML in topoXmlVo._l3topoXml.l3topolinks.l3topolink)
			{
				var fromN:Node = TopoUtil.getNodeById(eBox_map, lXml.leftRoterID);
				var toN:Node = TopoUtil.getNodeById(eBox_map, lXml.rigthRouterID);
				createLink(eBox_map,fromN,toN,LINEAR_LINK);
			}
			var coreNode:Node=eBox_map.getElementByID("1") as Node;
			for each(var node:Node in asgRsgCsgNodes)
			{
				var l:Link=TopoUtil.createLinkDash(eBox_map,coreNode,node);
				l.setClient("isXX",true);
			}
		}
		/**
		 * 自动布局
		 * */
		private function physicsAutoLayout():void
		{
			//初始化自动布局
			autoLayout=new IpRanAutoLayout;
			autoLayout.l3topoXml=topoXmlVo._l3topoXml;
			autoLayout.eBox=eBox_noMap;
			autoLayout.autoLayout(); 
			
			for each(var lXml:XML in topoXmlVo._l3topoXml.l3topolinks.l3topolink)
			{
				var fromN:Node = TopoUtil.getNodeById(eBox_noMap, lXml.leftRoterID);
				var toN:Node = TopoUtil.getNodeById(eBox_noMap, lXml.rigthRouterID);
				if(fromN==null||toN==null)
					continue;
				var fromNodeType:String=fromN.getClient("nodeType");
				var toNodeType:String=toN.getClient("nodeType");
				if(fromNodeType!="n"&&fromNodeType!="core"&&toNodeType!="n"&&toNodeType!="core"){
					var cl:Link=createLink(eBox_noMap,toN,fromN,CIRCLE_LINK);
				}
				if(fromNodeType=="n"||fromNodeType=="core"||toNodeType=="n"||toNodeType=="core"){
					var link1:Link=createLink(eBox_noMap,fromN,toN,LINEAR_LINK);
					link1.setStyle(Styles.LINK_COLOR,0x03bbff);
				}
			}
			var coreNode:Node=eBox_noMap.getElementByID("1") as Node;
			eBox_noMap.forEachByBreadthFirst(function(item:IData):void{
				if(item is Node){
					var node1:Node=item as Node;
					if(node1.getClient("nodeType")!="n"&&node1.getClient("nodeType")!="core"){
						if(node1.getClient("nodeType")!="rsg"){
						var l:Link=TopoUtil.createLinkDash(eBox_noMap,coreNode,node1);
						l.setClient("isXX",true);
						}
					}
				}
				
			});
		}
		
		public function drawTopoWithoutMap():void
		{
			network_map.visible=false;
			network_noMap.visible=true;
			eBox_noMap.forEachByBreadthFirst(function(item:IData):void{
				if(item is Link){
					var link:Link=item as Link;
					var f:Node=link.fromNode;
					var t:Node=link.toNode;
					if(page.withoutmapbtn.currentState == "closectrl")
					{
						if(f.getClient("nodeType")=="core"||t.getClient("nodeType") == "core"){
							link.setStyle(Styles.LINK_ALPHA,0);
						}
					}
				}
			});
		}
		
		public function drawTopoWithMap():void
		{
			network_map.visible=true;
			network_noMap.visible=false;
			eBox_map.forEachByBreadthFirst(function(item:IData):void{
				if(item is Link){
					var link:Link=item as Link;
					var f:Node=link.fromNode;
					var t:Node=link.toNode;
					if(page.withmapbtn.currentState == "closectrl")
					{
						if(f.getClient("nodeType")=="core"||t.getClient("nodeType") == "core"){
							link.setStyle(Styles.LINK_ALPHA,0);
						}
					}
				}
				
			});
			
		}
		
		
		public function movemap(e:int):void
		{
			switch(e){
				case 0:map.panBy(0, 100, {time:100, count:1});
					break;
				case 1:map.panBy(0, -100, {time:100, count:1});
					break;
				case 2:map.panBy(100, 0, {time:100, count:1});
					break;
				case 3:map.panBy(-100, 0, {time:100, count:1});
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
		private function createLink(box:ElementBox,from:Node,to:Node,linkType:int=2):Link{
			if(linkType == LINEAR_LINK){
				var linearLink:Link = new Link(from,to);
				linearLink.setStyle(Styles.LINK_WIDTH,2);
				box.add(linearLink);
				return linearLink;
			}
			var link:GradientLink=new GradientLink(from,to);
			
			link.setStyle(Styles.SHAPELINK_TYPE, Consts.SHAPELINK_TYPE_QUADTO);
			//var link:CircleLink = new CircleLink(from,to);
			box.add(link);
			return link;
		}
		private var detail:DetailInfo;
		private function setDetailWindow():void
		{
//			detail=new DetailInfo();
//			page.addElement(detail);
//			network.addEventListener(MouseEvent.DOUBLE_CLICK, function(e:MouseEvent):void {
//				var element:IElement = network.getElementByMouseEvent(e);
//				if(lastElement == element){
//					return;
//				}
//				lastElement = element;
//				if(element is Node){
//					var point:Point = network.getLogicalPoint(e);
//					detail.x = point.x - detail.measuredWidth / 2-100;
//					detail.y = point.y - detail.measuredHeight +20;
//					detail.visible = true;
//				}else{
//					detail.visible = false;
//				}
//			});
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