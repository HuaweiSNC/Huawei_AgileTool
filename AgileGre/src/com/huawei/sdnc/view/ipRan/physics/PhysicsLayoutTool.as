package com.huawei.sdnc.view.ipRan.physics
{
	import flash.display.Graphics;
	import flash.events.ContextMenuEvent;
	import flash.geom.Matrix;
	import flash.geom.Point;
	import flash.ui.ContextMenu;
	import flash.ui.ContextMenuItem;
	
	import mx.controls.Alert;
	
	import twaver.Alarm;
	import twaver.AlarmSeverity;
	import twaver.Collection;
	import twaver.Consts;
	import twaver.Element;
	import twaver.ElementBox;
	import twaver.ICollection;
	import twaver.IData;
	import twaver.Node;
	import twaver.ShapeNode;
	import twaver.Styles;
	import twaver.networkx.NetworkX;

	public class PhysicsLayoutTool
	{
		public function PhysicsLayoutTool()
		{
		}
		
		public var network:NetworkX;
		public var network_noMap:NetworkX;
		public var eBox:ElementBox;
		private var asg4P:Point;
		private var asg4N:Node=null;
		private var csg1P:Point;
		private var csg2P:Point;
		private var asg3P:Point;
		private var asg2P:Point;
		private var asg1P:Point;
		private var rsgP:Point;
		public function showPath():void
		{
			refresh();
			network.contextMenu=new ContextMenu;
			network.contextMenu.hideBuiltInItems();
			
			var clearPath:ContextMenuItem=new ContextMenuItem("清除路径");
			clearPath.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT,clearCanvas);
			
			var genarate:ContextMenuItem=new ContextMenuItem("显示路径",true);
			genarate.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT,genaratePath);
			
			var setTrouble:ContextMenuItem=new ContextMenuItem("设置故障");
			setTrouble.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT,genarateTrouble);
			
			var removeAlarm:ContextMenuItem=new ContextMenuItem("清除故障");
			removeAlarm.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT,clearAlarm);
			
			network.contextMenu.customItems = [genarate,clearPath,setTrouble,removeAlarm]; 
			
			
			
			network_noMap.contextMenu=new ContextMenu;
			network_noMap.contextMenu.hideBuiltInItems();
			
			var clearPath1:ContextMenuItem=new ContextMenuItem("清除路径");
			clearPath1.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT,clearCanvas);
			
			var genarate1:ContextMenuItem=new ContextMenuItem("显示路径",true);
			genarate1.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT,genaratePath);
			
			var setTrouble1:ContextMenuItem=new ContextMenuItem("设置故障");
			setTrouble1.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT,genarateTrouble);
			
			var removeAlarm1:ContextMenuItem=new ContextMenuItem("清除故障");
			removeAlarm1.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT,clearAlarm);
			
			network_noMap.contextMenu.customItems = [genarate,clearPath,setTrouble,removeAlarm]; 
			
		}
		
		public function clearAlarm(e:ContextMenuEvent):void
		{
			refresh();
			asg4N.alarmState.clear();
			network.graphics.clear();
			genaratePath(new ContextMenuEvent(""));
		}
		private var criticalfault:AlarmSeverity = new AlarmSeverity(1,"criticalfault","CRITICAL",0xfa6465,"CRITICAL");
		public function genarateTrouble(e:ContextMenuEvent):void
		{
			refresh();
			if(network.visible==true)
			{
				asg4N.alarmState.setNewAlarmCount(criticalfault,1);
				network.graphics.clear();
				var g:Graphics=network.graphics;
				g.lineStyle(2,0x4dc000,1);
				var c1:Point=getControlPonit(csg1P,csg2P);
				g.moveTo(csg1P.x,csg1P.y);
				g.curveTo(c1.x,c1.y,csg2P.x,csg2P.y);
				
				var c2:Point=getControlPonit(csg2P,asg3P);
				g.moveTo(csg2P.x,csg2P.y);
				g.curveTo(c2.x,c2.y,asg3P.x,asg3P.y);
				
				var c3:Point=getControlPonit(asg3P,asg2P);
				g.moveTo(asg3P.x,asg3P.y);
				g.curveTo(c3.x,c3.y,asg2P.x,asg2P.y);
				
				var c4:Point=getControlPonit(asg2P,asg1P);
				g.moveTo(asg2P.x,asg2P.y);
				g.curveTo(c4.x,c4.y,asg1P.x,asg1P.y);
				
				var c5:Point=getControlPonit(asg1P,rsgP);
				g.moveTo(asg1P.x,asg1P.y);
				g.curveTo(c5.x,c5.y,rsgP.x,rsgP.y);
			}else
			{
				asg4N.alarmState.setNewAlarmCount(criticalfault,1);
				network_noMap.graphics.clear();
				var g1:Graphics=network_noMap.graphics;
				g1.lineStyle(2,0x4dc000,1);
				var c11:Point=getControlPonit(csg1P,csg2P);
				g1.moveTo(csg1P.x,csg1P.y);
				g1.curveTo(c11.x,c11.y,csg2P.x,csg2P.y);
				
				var c22:Point=getControlPonit(csg2P,asg3P);
				g1.moveTo(csg2P.x,csg2P.y);
				g1.curveTo(c22.x,c22.y,asg3P.x,asg3P.y);
				
				var c33:Point=getControlPonit(asg3P,asg2P);
				g1.moveTo(asg3P.x,asg3P.y);
				g1.curveTo(c33.x,c33.y,asg2P.x,asg2P.y);
				
				var c44:Point=getControlPonit(asg2P,asg1P);
				g1.moveTo(asg2P.x,asg2P.y);
				g1.curveTo(c44.x,c44.y,asg1P.x,asg1P.y);
				
				var c55:Point=getControlPonit(asg1P,rsgP);
				g1.moveTo(asg1P.x,asg1P.y);
				g1.curveTo(c55.x,c55.y,rsgP.x,rsgP.y);
			}
			
		
		}
		
		public function genaratePath(e:ContextMenuEvent):void
		{
			refresh();
			if(network.visible==true)
			{
			var gra:Graphics=network.graphics;
			gra.lineStyle(2,0x4dc000,1);//
			var c:Point=getControlPonit(rsgP,asg4P);
			gra.moveTo(rsgP.x,rsgP.y);
			gra.curveTo(c.x,c.y,asg4P.x,asg4P.y);
			
			var c1:Point=getControlPonit(asg4P,csg1P);
			gra.moveTo(asg4P.x,asg4P.y);
			gra.curveTo(c1.x,c1.y,csg1P.x,csg1P.y);
				
			}else
			{
				var gra1:Graphics=network_noMap.graphics;
				gra1.lineStyle(2,0x4dc000,1);//
				var c11:Point=getControlPonit(rsgP,asg4P);
				gra1.moveTo(rsgP.x,rsgP.y);
				gra1.curveTo(c11.x,c11.y,asg4P.x,asg4P.y);
				
				var c12:Point=getControlPonit(asg4P,csg1P);
				gra1.moveTo(asg4P.x,asg4P.y);
				gra1.curveTo(c12.x,c12.y,csg1P.x,csg1P.y);
			}
				
		}
		
		public function clearCanvas(e:ContextMenuEvent):void
		{
			if(network.visible==true)
			{
				network.graphics.clear();
				asg4N.alarmState.clear();
			}
			else
			{
				network_noMap.graphics.clear();
				asg4N.alarmState.clear();
			}
		}
		
		
		
		
		public function getControlPonit(from:Point,to:Point):Point
		{
			var centerPoint:Point = new Point((from.x + to.x)/2, (from.y + to.y)/2);
			var matrix:Matrix = new Matrix();
			matrix.translate(-centerPoint.x, -centerPoint.y);
			matrix.rotate(getAngle(from, to));
			matrix.translate(centerPoint.x, centerPoint.y);
			var o:Number = 80;
			var d:Number=getDistance(from,to)/200;
			if(from.x<to.x)
			{
				o=-80;
			}
			var controlPoint:Point = matrix.transformPoint(new Point(centerPoint.x, centerPoint.y-o*d));
			return controlPoint;
		}
		private static function getDistance(p1:Point, p2:Point):Number {
			var dx:Number = p2.x - p1.x;
			var dy:Number = p2.y - p1.y;
			return Math.sqrt(dx*dx  + dy*dy);
		}
		
		public static function getAngle(p1:Point, p2:Point):Number {
			if(p1.x == p2.x){
				if(p2.y == p1.y){
					return 0;
				}
				else if(p2.y > p1.y){
					return Math.PI/2;
				}
				else{
					return -Math.PI/2;
				}
			}
			return Math.atan((p2.y - p1.y) / (p2.x - p1.x));
		}
		
		
		private function drawDashed(p1:Point,p2:Point,length:Number=5,gap:Number=5):void
		{
			var g:Graphics = network.graphics;
			g.lineStyle(1,0);
			var max:Number  = Point.distance(p1,p2);
			var l:Number =0;
			var p3:Point;
			var p4:Point;
			while(l<max)
			{
				p3 = Point.interpolate(p2,p1,l/max);
				l += length;
				if(l>max)
				{
					l = max;
				}
				p4 = Point.interpolate(p2,p1,l / max);
				g.moveTo(p3.x,p3.y);
				g.lineTo(p4.x,p4.y);
				l += gap;
			}
		}
		
		public function refresh():void
		{
			eBox.forEach(function(data:IData):void{
				if(data is Node)
				{
					var node:Node=data as Node;
					if(node.id=="2")
					{
						rsgP=node.centerLocation;
					}else if(node.id=="6")
					{
						asg4P=node.centerLocation;
						asg4N=node;
					}else if(node.id=="7")
					{
						csg1P=node.centerLocation;
					}else if(node.id=="8")
					{
						csg2P=node.centerLocation;
					}else if(node.id=="5")
					{
						asg3P=node.centerLocation;
					}else if(node.id=="4")
					{
						asg2P=node.centerLocation;
					}else if(node.id=="3")
					{
						asg1P=node.centerLocation;
					}
				}
				
			});
		}
		
	}
}