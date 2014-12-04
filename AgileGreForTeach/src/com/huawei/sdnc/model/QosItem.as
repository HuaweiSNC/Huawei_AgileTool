package com.huawei.sdnc.model
{
	import com.huawei.sdnc.model.acl.AclGroup;
	import com.huawei.sdnc.view.common.node.StateNode;
	 
	// 当前QOS项
	public class QosItem
	{
		public function QosItem()
		{
		}
		
		public var qosName:String;
		public var qosSrcAddress:String;
		public var qosdestAddress:String;
		
		public var qosSrcIp:String;
		public var qosdestIp:String;
		public var ifName:String;
		public var aclGroup:AclGroup;
		public var stateNode:StateNode;
		public var devicename:String;
		// 找到所有的qosClassifiers流分类 ==>> 找到对称的流行为 ==>> 找到管道 ==>> 找到管道的目标IP地址 ==>> 找到对应的设备IP地址
		
		
	}
 
}