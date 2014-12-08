package com.huawei.sdnc.view.ipRan.physics
{
	import twaver.Link;
	import twaver.Node;
	import twaver.Styles;
	
	public class DashLink extends Link
	{
		public function DashLink(id:Object=null, fromNode:Node=null, toNode:Node=null)
		{
			super(id, fromNode, toNode);
			this.setStyle(Styles.LINK_PATTERN,[8,8]);
			this.setStyle(Styles.LINK_COLOR,0x000000);
			this.setStyle(Styles.LINK_WIDTH,1);
			this.setClient("isXX",true);
		}
	}
}