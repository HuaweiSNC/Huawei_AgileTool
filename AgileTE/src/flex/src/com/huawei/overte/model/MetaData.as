package com.huawei.overte.model
{
	import com.huawei.overte.tools.ConnUtil;
	import com.huawei.overte.view.node.StateNode;

	public class MetaData
	{
		public function MetaData()
		{
		}
		[Bindable]
		public var connUtil:ConnUtil = ConnUtil.getInstence();
		public var stateNode:StateNode;
	}
}