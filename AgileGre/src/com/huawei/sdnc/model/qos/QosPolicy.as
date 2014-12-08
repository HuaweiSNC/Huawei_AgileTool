package com.huawei.sdnc.model.qos
{
	public class QosPolicy
	{
		public function QosPolicy()
		{
		}
		public var policyName:String;
		public var description:String;
		public var step:String;
		public var shareMode:String;
		public var statFlag:String;
		public var qosPolicyNodes:Array=[];
	}
}