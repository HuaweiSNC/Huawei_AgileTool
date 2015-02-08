package com.huawei.overte.tools
{
	
	//##################################################################
	//#
	//#		图片资源文件嵌入
	//#
	//##################################################################
	
	import flash.utils.describeType;
	
	import twaver.Defaults;
	import twaver.Utils;
	
	public class Images
	{
		
		[Embed(source="assets/imgs/topoimgs/icon_agg.png")]
		public static const AGGNode:Class;	
		[Embed(source="assets/imgs/topoimgs/icon_computer.png")]
		public static const ComputerNode:Class;	
		[Embed(source="assets/imgs/topoimgs/icon_core.png")]
		public static const CORENode:Class;	
		[Embed(source="assets/imgs/topoimgs/icon_tor.png")]
		public static const TORNode:Class;
		[Embed(source="assets/imgs/topoimgs/topo_area_node.png")]
		public static const AREANode:Class;
		[Embed(source="assets/imgs/topoimgs/topo_cloud_group.png")]
		public static const CloudGroup:Class;	
		[Embed(source="assets/imgs/ctrl/ctrl_flow.png")]
		public static const ctrl_flow:Class;
		[Embed(source="assets/imgs/ctrl/ctrl_restAPI.png")]
		public static const ctrl_restAPI:Class;
		
		[Embed(source="assets/imgs/ctrl/ctrl_app.png")]
		public static const ctrl_app:Class;
		[Embed(source="assets/imgs/ctrl/ctrl_openstack.png")]
		public static const ctrl_openstack:Class;
		
		[Embed(source="assets/imgs/ctrl/topo_agg_tow.png")]
		public static const topo_agg_tow:Class;
		[Embed(source="assets/imgs/ctrl/topo_core_tow.png")]
		public static const topo_core_tow:Class;
		[Embed(source="assets/imgs/ctrl/topo_tor_tow.png")]
		public static const topo_tor_tow:Class;
		
		[Embed(source="assets/imgs/ctrl/ctrl_flow.png")]
		public static const Controller:Class;
		[Embed(source="assets/imgs/ctrl/ctrl_openflow.png")]
		public static const ctrl_openflow:Class;
		
		[Embed(source="assets/imgs/show.png")]
		public static const show:Class;	
		[Embed(source="assets/imgs/hide.png")]
		public static const hide:Class;
		
		[Embed(source="assets/imgs/topoimgs/topo_server.png")]
		public static const topo_server:Class;	
		[Embed(source="assets/imgs/topoimgs/topo_VM.png")]
		public static const topo_VM:Class;	
		
		[Embed(source="assets/imgs/virtual/getewayport.png")]
		public static const getewayport:Class;	
		[Embed(source="assets/imgs/virtual/port.png")]
		public static const port:Class;	
		
		[Embed(source="assets/imgs/warningicon.png")]
		public static const warningicon:Class;
		
		//IPRAN image
		[Embed(source="assets/imgs/ipran/topo/ipran_node.png")]
		public static const asg:Class;
		[Embed(source="assets/imgs/ipran/topo/ipran_node.png")]
		public static const csg:Class;
		[Embed(source="assets/imgs/ctrl/ctrl_flow.png")]
		public static const core:Class;
		[Embed(source="assets/imgs/ipran/topo/ipran_basestation.png")]
		public static const n:Class;
		[Embed(source="assets/imgs/ipran/topo/ipran_node.png")]
		public static const rsg:Class;
		
		
		[Embed(source="assets/imgs/ipran/topo/core_withmap.png")]
		public static const core_withmap:Class;
		[Embed(source="assets/imgs/ipran/topo/node_withmap.png")]
		public static const node_withmap:Class;
		[Embed(source="assets/imgs/ipran/topo/basesta_withmap.png")]
		public static const basesta_withmap:Class;
		[Embed(source="assets/imgs/ipcore/icon_core_ipcore.png")]
		public static const icon_core_ipcore:Class;
		
		
		public static function init():void{
			Defaults.LOADER_CONTEXT_CHECK_POLICY_FILE = true;
			var classInfo:XML = describeType(Images);
			for each (var c:XML in classInfo..constant){
				var name:String = c.@name;
				Utils.registerImageByClass(name, Images[name]);
			}		
		}
	}
}