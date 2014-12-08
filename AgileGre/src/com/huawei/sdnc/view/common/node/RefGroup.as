package com.huawei.sdnc.view.common.node
{
	import com.huawei.sdnc.view.common.nodeui.RefGroupUI;
	import com.huawei.sdnc.view.common.nodeui.StateNodeUI;
	
	import twaver.Group;
	
	public class RefGroup extends Group
	{
		public function RefGroup(id:Object=null)
		{
			super(id);
		}
		
		override public function get elementUIXClass():Class
		{
			return RefGroupUI;
		}
	}
}