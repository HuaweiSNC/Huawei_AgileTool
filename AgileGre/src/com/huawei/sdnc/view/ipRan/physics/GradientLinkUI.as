package com.huawei.sdnc.view.ipRan.physics
{
	import flash.display.GradientType;
	import flash.geom.Matrix;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	
	import twaver.Collection;
	import twaver.ICollection;
	import twaver.ShapeLink;
	import twaver.Styles;
	import twaver.Utils;
	import twaver.networkx.IGraphics;
	import twaver.networkx.NetworkX;
	import twaver.networkx.ui.ShapeLinkUI;
	
	public class GradientLinkUI extends ShapeLinkUI {
		private static const OFFSET:Number = 50;
		public function GradientLinkUI(network:NetworkX, link:ShapeLink) {
			super(network, link);
		}
		
		override public function drawBody(g:IGraphics):void{
			var points:ICollection = this.linkPoints;
			if(points == null || points.count == 0){
				return;
			}
			points=getPoints(this.fromPoint, this.toPoint, 1,OFFSET);
			var width:Number = element.getStyle(Styles.LINK_WIDTH);
			g.lineStyle(width);
			
			var colors:Array = this.element.getClient("link.gradient.colors");
			var alphas:Array = [100, 100, 100];
			var ratios:Array = [0, 128, 255];
			var bounds:Rectangle = this.bodyRect;
			var matrix:Matrix = new Matrix();
			matrix.createGradientBox(bounds.width, bounds.height, Math.PI, bounds.x, bounds.y);
			//g.lineGradientStyle(GradientType.LINEAR, colors, alphas, ratios, matrix);
			g.lineStyle(2,0x03bbff);
			g.drawPoints(points);
		}
		
		
		private static function getDistance(p1:Point, p2:Point):Number {
			var dx:Number = p2.x - p1.x;
			var dy:Number = p2.y - p1.y;
			return Math.sqrt(dx*dx  + dy*dy);
		}
		
		public static function getPoints(from:Point, to:Point, offset:Number,yOFFSET:Number):ICollection {
			var centerPoint:Point = new Point((from.x + to.x)/2, (from.y + to.y)/2);
			var matrix:Matrix = new Matrix();
			matrix.translate(-centerPoint.x, -centerPoint.y);
			matrix.rotate(getAngle(from, to));
			matrix.translate(centerPoint.x, centerPoint.y);
			var o:Number = yOFFSET;
			if(from.x < to.x){
				o = - yOFFSET;
			}
			var controlPoint:Point = matrix.transformPoint(new Point(centerPoint.x, centerPoint.y - o * getDistance(from,to)/200));
			
			var result:ICollection = new Collection();
			var t:Number = 0, c1x:Number, c1y:Number, c2x:Number, c2y:Number, px:Number, py:Number;
			while ( t < offset ) {
				c1x = from.x + ( controlPoint.x - from.x ) * t;
				c1y = from.y + ( controlPoint.y - from.y ) * t;
				
				c2x = controlPoint.x + ( to.x - controlPoint.x ) * t;
				c2y = controlPoint.y + ( to.y - controlPoint.y ) * t;
				
				px = c1x + ( c2x - c1x ) * t;
				py = c1y + ( c2y - c1y ) * t;
				result.addItem(new Point(px, py));
				
				t += 0.01;
			}
			return result;
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
		
		
	}
}