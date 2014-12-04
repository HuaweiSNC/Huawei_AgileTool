package com.huawei.sdnc.view.common.nodeui
{
	import com.huawei.sdnc.tools.Images;
	
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.Graphics;
	import flash.geom.Rectangle;
	
	import mx.controls.Image;
	
	import twaver.Consts;
	import twaver.Group;
	import twaver.Utils;
	import twaver.networkx.IGraphics;
	import twaver.networkx.NetworkX;
	import twaver.networkx.ui.GroupUI;

public class GroupImgBgUI extends GroupUI
{
	public function GroupImgBgUI(network:NetworkX, group:Group){
		super(network, group);
	}
	
	override protected function drawContent(g:IGraphics):void{	
		if(!group.expanded){
			super.drawContent(g);
			return;
		}
		var rect:Rectangle = this.bodyRect;
		rect.inflate(rect.width * 0.2, rect.height * 0.2);
		var bitMap:Bitmap = new Images.ctrl_circle_bg() as Bitmap;
		bitMap.smoothing = true;
		g.drawBitmapData(bitMap.bitmapData, rect.x, rect.y, rect.width, rect.height, Consts.SHAPE_OVAL, Consts.STRETCH_FILL);
//		Utils.drawImage(g, 'ctrl_circle_bg', rect.x, rect.y, rect.width, rect.height, null, Consts.STRETCH_FILL);
	}
}
}