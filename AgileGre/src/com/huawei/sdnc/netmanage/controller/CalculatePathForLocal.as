package com.huawei.sdnc.netmanage.controller
{
	import com.huawei.sdnc.netmanage.controller.network.MptNetworkPlanner;
	import com.huawei.sdnc.netmanage.model.NetLink;
	import com.huawei.sdnc.netmanage.model.NetNode;
	import com.huawei.sdnc.netmanage.model.NetPlanner;
	
	import twaver.IData;
	import twaver.Styles;
	import twaver.networkx.NetworkX;

	public class CalculatePathForLocal
	{
		private var nodes:Array;
		private var links:Array;
		public function CalculatePathForLocal()
		{
		}
		
		public function CalculatePath(netPlan:NetPlanner,networkx:NetworkX):void
		{
			nodes=new Array;
			links=new Array;
			networkx.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is NetNode){
					var netnode:NetNode=item as NetNode;
					nodes.push(netnode);
				}else if(item is NetLink){
					var netlink:NetLink=item as NetLink;
					links.push(netlink);
				}
			});
			var mptNetworkPlanner:MptNetworkPlanner = new MptNetworkPlanner;
			mptNetworkPlanner.setNetWork(nodes,links);
			var pathlinks:Array=mptNetworkPlanner.plan(netPlan);
			
			networkx.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is NetLink){
					var l:NetLink=item as NetLink;
					l.setStyle(Styles.LINK_COLOR,0x60c6fb);
					l.setStyle(Styles.ARROW_TO_COLOR,0xafe1f3);
				}
			});
			for each(var netlink:NetLink in pathlinks){
				if(netlink!=null){
					netlink.setStyle(Styles.LINK_COLOR,0xa54148);
					netlink.setStyle(Styles.ARROW_TO_COLOR,0xa54148);
				}
				
			}
		}
	}
}