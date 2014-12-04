package com.huawei.sdnc.model.ifm
{
	import com.huawei.sdnc.model.ifm.ifmAm4.IfmAm4;

	public class Interface
	{
		public function Interface()
		{
		}
		public var ifName:String;
		public var ifPhyType:String;
		public var ifDynamicInfo:IfDynamicInfo;
		public var ifStatistics:IfStatistics;
		public var ifmAm4:IfmAm4;
	}
}