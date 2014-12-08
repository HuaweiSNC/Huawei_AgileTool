package com.huawei.sdnc.view.common.nodeui
{
	import com.huawei.sdnc.view.common.attachment.ReflectorAttachment;
	import com.huawei.sdnc.view.common.node.RefNode;
	
	import flash.display.BitmapData;
	import flash.display.GradientType;
	import flash.display.Sprite;
	import flash.geom.Matrix;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	
	import mx.states.OverrideBase;
	
	import twaver.Consts;
	import twaver.IImageAsset;
	import twaver.Node;
	import twaver.Styles;
	import twaver.Utils;
	import twaver.networkx.IGraphics;
	import twaver.networkx.NetworkX;
	import twaver.networkx.ui.NodeUI;

	public class RefNodeUI extends NodeUI
	{
		public function RefNodeUI(network:NetworkX, node:Node)
		{
			super(network, node);
		}
		
		override public function updateProperties():void {
			super.updateProperties();
		}
		
		override protected function createUnionBounds():Rectangle {
			var result:Rectangle = super.createUnionBounds();
			result.height += 1 + this.node.height;
			return result;
		}
		/*private var __reflectorAttachment:ReflectorAttachment;
		override public function checkAttachments():void
		{
			super.checkAttachments();
			if(!__reflectorAttachment)
			{
				__reflectorAttachment = new ReflectorAttachment(this);
				this.addAttachment(__reflectorAttachment);
			}
			
		}*/
		
		override protected function drawContent(graphics:IGraphics):void {
			super.drawContent(graphics);
			var image:IImageAsset = Utils.getImageAsset(this.node.image);
			if(image == null){
				return;
			}
			var rect:Rectangle = this.bodyRect;
			var bitmapData:BitmapData = new BitmapData(rect.width, rect.height, true, 0x00000000);
			var color:* = element.getStyle(Styles.INNER_STYLE) == Consts.INNER_STYLE_DYE ? innerColor : null;
			var matrix:Matrix = new Matrix(rect.width/image.width, 0, 0, rect.height/image.height, 0, 0);
			bitmapData.draw(image.getBitmapData(color), matrix);
			var resultBitmapData:BitmapData = new BitmapData(rect.width, rect.height, true, 0x00000000);
			resultBitmapData.copyPixels(bitmapData,
				new Rectangle(0, 0, rect.width, rect.height), new Point(), this.createAlphaGradientBitmap());
			graphics.drawBitmapData(resultBitmapData, rect.x, rect.bottom + 1, rect.width, rect.height, "rectangle", "fill", 180);
		}
		
		private function createAlphaGradientBitmap():BitmapData {
			var rect:Rectangle = this.bodyRect;
			var alphaGradientBitmap:BitmapData = new BitmapData(rect.width, rect.height, true, 0x00000000);
			var gradientMatrix:Matrix = new Matrix();
			var gradientSprite:Sprite = new Sprite();
			gradientMatrix.createGradientBox(rect.width, rect.height * 0.4, Math.PI/2, 
				0, rect.height * (1.0 - 0.4));
			gradientSprite.graphics.beginGradientFill(GradientType.LINEAR, [0xFFFFFF, 0xFFFFFF], 
				[0, 0.4], [0, 255], gradientMatrix);
			gradientSprite.graphics.drawRect(0, rect.height * (1.0 - 0.4), 
				rect.width, rect.height * 1);
			gradientSprite.graphics.endFill();
			alphaGradientBitmap.draw(gradientSprite);
			return alphaGradientBitmap;
		}
		
	}
}