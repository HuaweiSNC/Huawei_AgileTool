package com.huawei.sdnc.view.ipCore_DCI
{
	import com.huawei.sdnc.tools.SdncUtil;
	
	import twaver.Consts;
	import twaver.Link;
	import twaver.Node;
	import twaver.Styles;
	
	public class MyLink extends Link
	{
		public var leftnodeID:int;
		public var rightnodeID:int;
		public var cost:int = 1;
		public var bandwidth:int = 1;
		public function MyLink(id:Object=null, fromNode:Node=null, toNode:Node=null)
		{
			super(id, fromNode, toNode);
			this.setStyle(Styles.LINK_WIDTH, 1);
			this.setStyle(Styles.LINK_BUNDLE_EXPANDED,false);
			this.setStyle(Styles.LINK_TYPE,Consts.LINK_TYPE_PARALLEL);
			this.setStyle(Styles.LINK_BUNDLE_OFFSET,26);
			this.setStyle(Styles.LINK_HANDLER_COLOR,0xffffff);
			this.setStyle(Styles.LINK_COLOR, 0x60c6fb);
			
			this.setStyle(Styles.LINK_BUNDLE_EXPANDED,false);
			this.setStyle(Styles.LINK_BUNDLE_GAP,SdncUtil.linkGapMin);
			this.setStyle(Styles.LINK_TYPE,Consts.LINK_TYPE_TRIANGLE);

		}
	}
}