package com.huawei.sdnc.controller.ipCoreController
{
	import com.huawei.sdnc.controller.ipCoreController.path.MptNetworkPlanner;
	import com.huawei.sdnc.netmanage.model.NetPlanner;
	import com.huawei.sdnc.view.common.node.StateNode;
	import com.huawei.sdnc.view.ipCore_DCI.MyLink;
	
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	
	import twaver.IData;
	import twaver.networkx.NetworkX;

	public class PingPath
	{
		private var nodes:Array;
		private var links:Array;
		private var timer:Timer;
		private var pathlinks:Array;
		private var networkX:NetworkX;
		public function PingPath()
		{
		}
		public function signPingPath(netPlanner:NetPlanner,networkx:NetworkX):void
		{
			networkX=networkx;
			nodes=new Array;
			links=new Array;
			networkx.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is StateNode){
					var netnode:StateNode=item as StateNode;
					nodes.push(netnode);
				}else if(item is MyLink){
					var netlink:MyLink=item as MyLink;
					links.push(netlink);
				}
			});
			
			var mptNetworkPlanner:MptNetworkPlanner = new MptNetworkPlanner;
			mptNetworkPlanner.setNetWork(nodes,links);
			pathlinks=mptNetworkPlanner.planAllPath(netPlanner);
			ping();
		}
		private var index:int=-1;
		private function ping():void
		{
			var count:int=pathlinks.length;
			if(count==1){
				pingPath(null);
			}
			else if(count>1){
				pingPath(null);
				timer=new Timer(2000,count-1);
				timer.addEventListener(TimerEvent.TIMER,pingPath);
				timer.start();
			}
		}
		
		public var colors:Array = [0xff0066,0x6c00ff,0x00ff42,0xffa200,0xff0c00,0xffffff,0x6900a0];
		private function pingPath(e:TimerEvent):void
		{
			index++;
			var links:Array = pathlinks[index];
			for(var j:int=0;j<links.length;j++){
				if(links[j]!=null){
					var link:MyLink=links[j];
					link.setClient("linkcolor",colors[index]);
					var getNqaData:GetNqaData = link.getClient("nqa");
					getNqaData.pingPath();
				}
			}
		}
//		private function findlink(leftnodeID:int,rightnodeID:int):MyLink
//		{
//			var ren:MyLink=null;
//			var isc:Boolean=true;
//			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
//				if(isc&&item is MyLink){
//					var mylink:MyLink = item as MyLink;
//					var left:int = mylink.leftnodeID;
//					var right:int = mylink.rightnodeID;
//					if(leftnodeID == left&&rightnodeID == right){
//						ren=mylink;
//						isc=false;
//					}
//				}
//			});
//			return ren;
//		}
	}
}