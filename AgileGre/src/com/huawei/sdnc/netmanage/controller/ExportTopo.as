package com.huawei.sdnc.netmanage.controller
{
	import com.huawei.sdnc.netmanage.model.NetLink;
	import com.huawei.sdnc.netmanage.model.NetNode;
	import com.huawei.sdnc.view.common.node.StateNode;
	import com.huawei.sdnc.view.ipCore_DCI.MyLink;
	
	import twaver.ElementBox;
	import twaver.IData;
	import twaver.Node;
	import twaver.networkx.NetworkX;

	public class ExportTopo
	{
		public function ExportTopo()
		{
		}
		/**
		 * 
		 * @param fromNetwork 原来物理视图的networkx
		 * @param toNetwork  网归视图的networkx
		 * 
		 */		
		public function exportTopo(fromNetwork:NetworkX,toNetwork:NetworkX):void
		{
			var nodeObjArr:Array=new Array;
			var beginID:int=1;
			fromNetwork.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is StateNode){
					var sn:StateNode = item as StateNode;
					var netNode:NetNode=new NetNode;
					netNode.setCenterLocation(sn.x,sn.y);
					netNode.nodeId = beginID++ ;
					netNode.nodeType=sn.getClient("productType");
					netNode.systemName=sn.name;
					netNode.name=sn.name;
					var obj:Object=new Object;
					obj["oldNode"]=sn;
					obj["newNode"]=netNode;
					nodeObjArr.push(obj);
					toNetwork.elementBox.add(netNode);
				}
				
			});
			fromNetwork.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is MyLink){
					var myLink:MyLink = item as MyLink;
					var fromNode:StateNode=myLink.fromNode as StateNode;
					var toNode:StateNode=myLink.toNode as StateNode;
					queryNode(fromNode,toNode,nodeObjArr,toNetwork.elementBox);
				}
			});
			
		}
		
		private function queryNode(fromNode:StateNode,toNode:StateNode,nodeArray:Array,eBox:ElementBox):void
		{
            var f:NetNode=null;
			var t:NetNode=null;
			for each(var obj:Object in nodeArray){
				var oldNode:StateNode=obj["oldNode"] as StateNode;
				var newNode:NetNode=obj["newNode"] as NetNode;
				if(fromNode==oldNode){
					f=newNode;
				}else if(toNode==oldNode){
					t=newNode;
				}
			}
			if(f!=null&&t!=null){
				eBox.forEachByBreadthFirst(function(item:IData):void{
					if(item is NetLink){
						var nl:NetLink = item as NetLink;
						if(nl.fromNode == f&&nl.toNode == t){
							return;
						}
					}
				});
				var netLink:NetLink=new NetLink(f,t);
				netLink.leftnodeID=f.nodeId;
				netLink.rightnodeID=t.nodeId;
				eBox.add(netLink);
			}
		}
	}
}