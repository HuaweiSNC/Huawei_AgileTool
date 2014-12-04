package com.huawei.sdnc.view.common.node
{
	import com.huawei.sdnc.view.common.nodeui.RefNodeUI;
	
	import twaver.Consts;
	import twaver.Node;
	import twaver.Styles;
	
	public class RefNode extends Node
	{
		public function RefNode(id:Object=null)
		{
			super(id);
			this.setStyle(Styles.LABEL_POSITION,Consts.POSITION_TOP_TOP);
			this.setClient("reflector",true);
		}
		
		override public function get elementUIXClass():Class
		{
			return RefNodeUI;
		}
	}
}