package com.huawei.sdnc.model
{
	import com.huawei.sdnc.tools.ConnUtil;
	import com.huawei.sdnc.view.common.node.StateNode;

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