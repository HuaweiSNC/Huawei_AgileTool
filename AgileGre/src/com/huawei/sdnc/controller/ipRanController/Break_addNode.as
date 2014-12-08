package com.huawei.sdnc.controller.ipRanController
{
	import com.huawei.sdnc.view.ipRan.physics.GradientLink;
	import com.huawei.sdnc.view.ipRan.physics.PhysicsView;
	
	import flash.display.Bitmap;
	import flash.events.MouseEvent;
	import flash.geom.Point;
	
	import mx.controls.Image;
	import mx.core.DragSource;
	import mx.core.UIComponent;
	import mx.events.DragEvent;
	import mx.managers.DragManager;
	
	import twaver.Collection;
	import twaver.Defaults;
	import twaver.Dummy;
	import twaver.ElementBox;
	import twaver.ICollection;
	import twaver.IImageAsset;
	import twaver.Node;
	import twaver.Styles;
	import twaver.Utils;
	import twaver.networkx.NetworkX;

	public class Break_addNode
	{
		public var page:PhysicsView;
		public var eBox:ElementBox;
		public var network:NetworkX;
		private var _yuanx:Number;
		private var _yuany:Number;
		private var _yuanr:Number;
		private var _centerP1:Point;
		private var _centerP2:Point;
		private var _centerP3:Point;
		public function Break_addNode()
		{
		}
		public function init():void{
			_centerP1 = new Point(600,650);
			_centerP2 = new Point(1020,640);
			_centerP3 = new Point(820,560);
		}
		//拖出新节点
		public function drag(e:MouseEvent):void
		{
			var imageAsset:IImageAsset = Utils.getImageAsset("rsg");
			var dragImage:Image = new Image();
			dragImage.mouseChildren = false;
			dragImage.mouseEnabled = false;
			dragImage.source = new Bitmap(imageAsset.bitmapData);

			var ds:DragSource = new DragSource();
			ds.addData(page.btnCreateNode, "twaver");
			var item:Object = { label:"Node", elementClass:twaver.Node, dragImage:dragImage,
				width:imageAsset.width, height:imageAsset.height };
			ds.addData(item, "data");
			DragManager.doDrag(page.btnCreateNode, ds, e, dragImage, -e.localX+imageAsset.width/2, -e.localY+imageAsset.height/2, 0.5);
		}
		
		private var markLink:GradientLink = new GradientLink();
		private var lastLink:GradientLink = null;
		public  function initNetworkDragAndDropListener():void
		{
			network.addEventListener(DragEvent.DRAG_ENTER,function(evt:DragEvent):void{
				if(evt.dragSource.hasFormat("twaver")){
					DragManager.acceptDragDrop(UIComponent(evt.target));
				}
			});
			network.addEventListener(DragEvent.DRAG_OVER, function (evt:DragEvent):void{
				var data:Object = evt.dragSource.dataForFormat("data");
				if(!data){
					return;
				}
				var link:GradientLink = null;
				var list:ICollection = network.getElementsByDisplayObject(data.dragImage);
				for(var i:int=0; i<list.count; i++){
					link = list.getItemAt(i) as GradientLink;
					if(link != null && link != markLink){
						break;
					}
				}
				if(link != null && link != markLink){
					//	updateMark(link, data, network.getLogicalPoint(evt));
				}else{
					removeMark(true);
				}
			});
			
			network.addEventListener(DragEvent.DRAG_DROP,function (evt:DragEvent):void{
				var data:Object = evt.dragSource.dataForFormat("data");
				if(!data){
					return;
				}
				if(lastLink){
					lastLink.setStyle(Styles.LINK_ALPHA, markLink.getStyle(Styles.LINK_ALPHA));
				}
				
				var link:GradientLink = null;
				var list:ICollection = network.getElementsByDisplayObject(data.dragImage);
				for(var i:int=0; i<list.count; i++){
					link = list.getItemAt(i) as GradientLink;
					if(link != null && link != markLink){
						break;
					}
				}
				if(link != null && link != markLink){
					//判断以哪个中心为圆心
					var node:Node = new Node();
					node.image = "node";
					node.name = data.label;
					if(link.fromNode.x < 735)
					{
						_yuanx = _centerP1.x;
						_yuany = _centerP1.y;
						if(link.fromNode.id == "6" ){
						_yuanx = _centerP3.x;
						_yuany = _centerP3.y;
						_yuanr = 200;
						}
					
					}else if(link.fromNode.x > 890){
							_yuanx = _centerP2.x;
							_yuany = _centerP2.y;
						if(link.fromNode.id == "3"){
							_yuanx = _centerP3.x;
							_yuany = _centerP3.y;
							_yuanr = 200;
						}
					}
//					}else if(link.fromNode.id == "3" || link.fromNode.id == "2" ){
					else if(link.fromNode.y > 360 ){
						_yuanx = _centerP3.x;
						_yuany = _centerP3.y;
						_yuanr = 200;
						if(link.fromNode.id == "6" ){
							_yuanx = _centerP1.x;
							_yuany = _centerP1.y;
						}else if(link.fromNode.id == "3"){
							_yuanx = _centerP2.x;
							_yuany = _centerP2.y;
						}
					} 
					_yuanr = 135;
					//得到角度，在中心点两侧时的新加node的位置
					var jd:Number = Math.atan((network.getLogicalPoint(evt).y-_yuany)/(network.getLogicalPoint(evt).x-_yuanx));
					var nodeX:Number = 0;
					var nodeY:Number = 0;
					if(network.getLogicalPoint(evt).x < _yuanx){
						nodeX = _yuanx +  Math.cos(jd+Math.PI) * _yuanr;
						nodeY = _yuany +  Math.sin(jd+Math.PI) * _yuanr;
					}else{
						nodeX = _yuanx +  Math.cos(jd) * _yuanr;
						nodeY = _yuany +  Math.sin(jd) * _yuanr;
					}
					node.setCenterLocation(nodeX,nodeY);
					network.elementBox.add(node);
					network.selectionModel.setSelection(node);
					//加节点之后打断成两条线
					var link1:GradientLink = new GradientLink(link.fromNode, node);
					var points1:ICollection = new Collection([
						MathTan(link.fromNode, node)
					]);
					link1.elementUIClass.linkPoints = points1;
					network.elementBox.add(link1);
					
					var link2:GradientLink = new GradientLink(node, link.toNode);
					var points2:ICollection = new Collection([
						MathTan(node, link.toNode)
					]);
					link2.elementUIClass.linkPoints = points2;
					network.elementBox.add(link2);
					
					network.elementBox.remove(link);
				}
				removeMark(false);
			});
			
		}
		
		
		
		private function removeMark(moving:Boolean):void {
			if(network.elementBox.contains(markLink)){
				var fromNode:Node = markLink.fromNode;
				var toNode:Node = markLink.toNode;
				network.elementBox.remove(markLink);
				if(lastLink && moving){
					lastLink.setStyle(Styles.LINK_ALPHA, markLink.getStyle(Styles.LINK_ALPHA));
					lastLink = null;
				}
			}
		}
		
		private function MathTan(node1:Node,node2:Node):Point{
			var jd:Number = Math.atan2((node1.centerLocation.y-_yuany),(node1.centerLocation.x-_yuanx));
			var jd1:Number = Math.atan2((node2.centerLocation.y-_yuany),(node2.centerLocation.x-_yuanx));
			var jj:Number = Math.abs(Math.abs(jd1) - Math.abs(jd))/2;
			var nodeX:Number = 0;
			var nodeY:Number = 0;
			nodeX = _yuanx +  Math.cos(jd1+jj) * _yuanr;
			nodeY = _yuany +  Math.sin(jd1+jj) * _yuanr;
			return new Point(nodeX,nodeY);
		}
		
		
	}
}