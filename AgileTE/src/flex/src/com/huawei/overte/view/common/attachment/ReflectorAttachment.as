package com.huawei.overte.view.common.attachment
{
	import com.huawei.overte.view.common.NodeReflector;
	
	import flash.display.BitmapData;
	
	import twaver.Consts;
	import twaver.Node;
	import twaver.Utils;
	import twaver.networkx.ui.BasicAttachment;
	import twaver.networkx.ui.ElementUI;
	
	public class ReflectorAttachment extends BasicAttachment
	{
		private var nodeRef:NodeReflector = new NodeReflector();
		public function ReflectorAttachment(elementUI:ElementUI, showOnTop:Boolean=false)
		{
			super(elementUI, showOnTop);
			this.content = nodeRef;
			this.network.addChild(nodeRef);
			nodeRef.visible = false;
		}
		
		override public function updateProperties():void
		{
			super.updateProperties();
			var node:Node = this.element as Node;
//			nodeRef.bitmapData = Utils.getImageAsset(node.image).getBitmapData();
			nodeRef.setBitMapSource(Utils.getImageAsset(node.image).getBitmapData());
		}
		
		override public function get direction():String
		{
			return Consts.ATTACHMENT_DIRECTION_BELOW_LEFT;
		}
		
		override public function get position():String
		{
			return Consts.POSITION_BOTTOM;
		}
		
		override public function get xOffset():Number
		{
			return -25;
		}
		
		override public function get yOffset():Number
		{
			return -9;
		}
	}
}