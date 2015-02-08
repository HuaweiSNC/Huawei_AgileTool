package com.huawei.overte.view.link
{
	import flash.display.GradientType;
	import flash.display.Graphics;
	import flash.display.InterpolationMethod;
	import flash.display.SpreadMethod;
	import flash.events.TimerEvent;
	import flash.geom.Matrix;
	import flash.geom.Point;
	import flash.utils.Timer;
	
	import twaver.ICollection;
	import twaver.Link;
	import twaver.Styles;
	import twaver.Utils;
	import twaver.network.Network;
	import twaver.network.ui.LinkUI;
	
	public class FlowLinkUI extends LinkUI
	{
		public function FlowLinkUI(network:Network, link:Link)
		{
			super(network, link);
			var flowLinkTimer:Timer = new Timer(50); 
			flowLinkTimer.addEventListener(TimerEvent.TIMER, function(evt:*):void{
				(link as FlowLink).flow();
			});				
			flowLinkTimer.start();
		}
		
		override protected function drawBody(g:Graphics):void{
			super.drawBody(g);
			super.setStyle(Styles.LINK_LABEL_ROTATABLE, 0x000000);
			var points:ICollection = this.linkPoints;
			if(points == null || points.count == 0 ||  !(link.getStyle("flow.color") is uint)){
				return;
			}
			var flowColor:uint = link.getStyle("flow.color");
			var colors:Array = [flowColor, flowColor, 0, 0];
			var alphas:Array = [1, 1, 0, 0];
			var ratios:Array = [0, 128, 128, 255];
			
			var matrix:Matrix = new Matrix();
			var boxWidth:Number = (link as FlowLink).perStep * 2;
			var boxHeight:Number = (link as FlowLink).perStep * 2;
			var boxRotation:Number = this.angle;
			var flowX:Number =( link as FlowLink).flowX;
			matrix.createGradientBox(boxWidth, boxHeight, this.angle, flowX, flowX);
			
			g.lineGradientStyle(GradientType.LINEAR, colors, alphas, ratios, matrix, SpreadMethod.REFLECT);
			
			Utils.drawLine(g, points, link.getStyle(Styles.LINK_PATTERN));
		}
	}
}