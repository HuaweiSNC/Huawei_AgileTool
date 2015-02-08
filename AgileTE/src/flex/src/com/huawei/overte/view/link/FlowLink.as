package com.huawei.overte.view.link
{
	import twaver.Link;
	import twaver.Node;
	import twaver.Styles;
	
	public class FlowLink extends Link
	{
		public function FlowLink(id:Object=null, fromNode:Node=null, toNode:Node=null)
		{
			super(id, fromNode, toNode);
			this.setStyle(Styles.LINK_WIDTH, 1); //线宽 ，默认值为3
			this.setStyle("flow.color", 0x00FF00);
		}
		
		override public function get elementUIClass():Class{
			return FlowLinkUI;
		}
		
		private var _perStep:int =10;
		public function get perStep():int{
			return this._perStep;
		}
		
		public function set perStep(value:int):void{
			var old:int = this._perStep;
			this._perStep = value;
			this.dispatchPropertyChangeEvent("per.step", old, value);
		}
		
		var flowX:int = 0;
		
		public function flow():void{
			var old:int = this.flowX;
			this.flowX = (flowX +_perStep)%255 ;
			this.dispatchPropertyChangeEvent("flow.x", old, flowX);
		}
	}
}