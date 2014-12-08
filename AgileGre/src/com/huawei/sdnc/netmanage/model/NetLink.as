package com.huawei.sdnc.netmanage.model
{
	import twaver.Consts;
	import twaver.Link;
	import twaver.Node;
	import twaver.Styles;
	
	public class NetLink extends Link
	{
		public function NetLink(id:Object=null, fromNode:Node=null, toNode:Node=null)
		{
			super(id, fromNode, toNode);
			netStyle();
		}
		public var leftnodeID:int;
		public var rightnodeID:int;
		public var cost:int = 1;
		public var bandwidth:int = 1;
		
		private function netStyle():void
		{
			this.setStyle(Styles.LINK_WIDTH, 1);
			this.setStyle(Styles.LINK_BUNDLE_EXPANDED,false);
			this.setStyle(Styles.LINK_TYPE,Consts.LINK_TYPE_PARALLEL);
			this.setStyle(Styles.LINK_BUNDLE_OFFSET,26);
			this.setStyle(Styles.LINK_HANDLER_COLOR,0xffffff);
			this.setStyle(Styles.LINK_COLOR, 0x60c6fb);
			this.setStyle(Styles.LINK_BUNDLE_EXPANDED,true);
			this.setStyle(Styles.LINK_BUNDLE_GAP,1);
			this.setStyle(Styles.ARROW_TO,true);
			this.setStyle(Styles.ARROW_TO_FILL,true);
			this.setStyle(Styles.ARROW_TO_COLOR,0xafe1f3);
			this.setStyle(Styles.ARROW_TO_OUTLINE_WIDTH,0.1);
			this.setStyle(Styles.ARROW_TO_OUTLINE_COLOR,0xafe1f3);
			this.setStyle(Styles.ARROW_TO_OUTLINE_ALPHA,0);
		}
	}
}