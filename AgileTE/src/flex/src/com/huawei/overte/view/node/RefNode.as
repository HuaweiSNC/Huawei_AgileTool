package com.huawei.overte.view.node
{
	
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