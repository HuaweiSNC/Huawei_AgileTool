package com.huawei.overte.model
{
	import mx.collections.ArrayCollection;
	//rwx202245
	public class Data
	{
		[Bindable]
		public var states:ArrayCollection = new ArrayCollection;
		[Bindable]
		public var domainsarr:ArrayCollection = new ArrayCollection;//所有域信息，不包括topo
		[Bindable]
		public var domainsxml:XML = new XML;//所有域信息的xml，包括topo信息
		[Bindable]
		public var nowdevices:ArrayCollection = new ArrayCollection;//当前所有设备信息列表
		[Bindable]
		public var areatypes:ArrayCollection = new ArrayCollection;//解决方案集合
		[Bindable]
		public var newdevices:ArrayCollection = new ArrayCollection;//新添加的设备信息
		[Bindable]
		public var vlanArray:ArrayCollection = new ArrayCollection;//查询的vlan信息
		
		[Bindable]
		public var nqaTunnelxml:XML //nqa列表信息
		[Bindable]
		public var sychartdata:ArrayCollection  = new ArrayCollection;//时延统计图数据
		[Bindable]
		public var llchartdata:ArrayCollection = new ArrayCollection;//流量统计图数据
		[Bindable]
		public var delayState:Boolean;
		[Bindable]
		public var flowState:Boolean;
		
		
		[Bindable]
		public var priState:String;
		[Bindable]
		public var backState:String;
		
		
		[Bindable]
		public var NqaTime:int=20;//时延统计间隔时间，默认10
		private static var instence:Data;
		public function Data()
		{
			
		}
		
		public static function getInstence():Data
		{
			if(instence==null)
			{
				instence = new Data();
			}
			return instence;
		}
	}
}