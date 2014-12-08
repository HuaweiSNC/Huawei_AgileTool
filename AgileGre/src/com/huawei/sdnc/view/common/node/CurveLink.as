package com.huawei.sdnc.view.common.node
{
	import com.huawei.sdnc.view.common.nodeui.CurveLinkUI;
	
	import twaver.Link;
	import twaver.Node;

	public class CurveLink extends Link
	{
		public function CurveLink(id:Object=null,fromN:Node=null,toN:Node=null)
		{
			super(id,fromN,toN);
		}
		
		override public function get elementUIXClass():Class
		{
			return CurveLinkUI;
		}
	}
}