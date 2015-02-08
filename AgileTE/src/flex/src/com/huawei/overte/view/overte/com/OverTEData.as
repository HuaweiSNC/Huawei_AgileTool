package com.huawei.overte.view.overte.com
{
	import mx.collections.ArrayCollection;
	
	import twaver.AlarmSeverity;

	public class OverTEData
	{
		public function OverTEData()
		{
		}
		/**自定义气泡*/
		public static const FLOW_BEGIN:AlarmSeverity = new AlarmSeverity(1,"flow_begin","Begin",0x00FF00,"begin");
		public static const FLOW_END:AlarmSeverity = new AlarmSeverity(1,"flow_end","End",0x00FF00,"end");
		[Bindable]
		public  static var  flowPolicies:ArrayCollection=new ArrayCollection([
			{instanceName:"flow1",srcIp:"4.4.4.4",peerip:"3.3.3.3"}
		]); //流
		[Bindable]
		public  static var  ifmInterfaceDatas:ArrayCollection=new ArrayCollection();
		
		
		[Bindable]
		public  static var  PipePolicies:ArrayCollection=new ArrayCollection(
			[
			{tunnelName:"Tunnel1",interfaceName:" GigabitEthernet1/0/0",mplsTunnelIndex:"1",
				mplsTunnelEgressLSRId:"3.3.3.3",mplsTunnelIngressLSRId:"4.4.4.4",hotStandbyWtr:"15",
				tunnelPaths:new ArrayCollection([
					{pathType:"primary",explicitPaths:new ArrayCollection([
						{mplsTunnelHopIpAddr:"10.4.1.1"},
						{mplsTunnelHopIpAddr:"10.4.1.2"},
						{mplsTunnelHopIpAddr:"10.2.1.2"},
						{mplsTunnelHopIpAddr:"3.3.3.3"}
						
					])},
					{pathType:"host_standby",explicitPaths:new ArrayCollection([
						{mplsTunnelHopIpAddr:"10.3.1.1"},
						{mplsTunnelHopIpAddr:"10.3.1.2"},
						{mplsTunnelHopIpAddr:"10.5.1.2"},
						{mplsTunnelHopIpAddr:"3.3.3.3"}
						
					])}])
			}
		]
		); //管道
//		[Bindable]
//		public  static var  PipePoliciesPath:ArrayCollection=new ArrayCollection(); //管道
		
		public static var mainpath:ArrayCollection = new ArrayCollection([
			{mplsTunnelHopIpAddr:"10.4.1.1"},
			{mplsTunnelHopIpAddr:"10.4.1.2"},
			{mplsTunnelHopIpAddr:"10.2.1.2"},
			{mplsTunnelHopIpAddr:"3.3.3.3"}
			
		]);
		public static var backuppath:ArrayCollection = new ArrayCollection([
			{mplsTunnelHopIpAddr:"10.3.1.1"},
			{mplsTunnelHopIpAddr:"10.3.1.2"},
			{mplsTunnelHopIpAddr:"10.5.1.2"},
			{mplsTunnelHopIpAddr:"3.3.3.3"}
			
		]);
		[Bindable]
		public  static var  PathArray:ArrayCollection=new ArrayCollection(); //Path 路径
		
		[Bindable]
		public  static var  AdgXML:Array; //Path 路径
		
	}
}