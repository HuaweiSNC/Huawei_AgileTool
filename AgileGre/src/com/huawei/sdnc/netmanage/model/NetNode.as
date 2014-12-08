package com.huawei.sdnc.netmanage.model
{
	import twaver.Node;
	import twaver.Styles;
	
	public class NetNode extends Node
	{
		public function NetNode(id:Object=null)
		{
			super(id);
			mySetStyle();
		}
		public var nodeId:int;
		public var nodeType:String;
		public var systemName:String;
		
		private function mySetStyle():void
		{
			this.image="icon_core_ipcore";
			this.setStyle(Styles.LABEL_COLOR,0xffffff);
		}
	}
}