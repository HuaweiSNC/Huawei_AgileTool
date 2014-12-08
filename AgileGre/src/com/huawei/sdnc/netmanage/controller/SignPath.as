package com.huawei.sdnc.netmanage.controller
{
	import com.huawei.sdnc.netmanage.model.NetLink;
	import com.huawei.sdnc.netmanage.model.NetNode;
	
	import twaver.IData;
	import twaver.Styles;
	import twaver.network.Network;
	import twaver.networkx.NetworkX;

	public class SignPath
	{
		public function SignPath()
		{
		}
		/**
		 *根据传进来的路径节点的ID，标记link 
		 * @param pathID 路径节点数组
		 * @param networkx 
		 * 
		 */		
		public function signPath(pathID:Array,networkx:NetworkX):void
		{
			var nodes:Array=new Array;
			for(var j:int=0;j<pathID.length;j++){
				var pathid:int=pathID[j];
				networkx.elementBox.forEachByBreadthFirst(function(item:IData):void{
					if(item is NetNode){
						var netNode:NetNode = item as NetNode;
						var nodeID:int = netNode.nodeId;
						if(pathid == nodeID){
							nodes.push(netNode);
						}
					}
				});
			}
			networkx.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is NetLink){
					var l:NetLink=item as NetLink;
					l.setStyle(Styles.LINK_COLOR,0x60c6fb);
					l.setStyle(Styles.ARROW_TO_COLOR,0xafe1f3);
				}
			});
			for(var  i:int=0;i<nodes.length-1;i++){
				var curnode:NetNode=nodes[i];
				var nextnode:NetNode=nodes[i+1];
				var link:NetLink=querylink(curnode,nextnode,networkx);
				if(link!=null){
					link.setStyle(Styles.LINK_COLOR,0xa54148);
					link.setStyle(Styles.ARROW_TO_COLOR,0xa54148);
				}
			}
		}
		private function querylink(curnode:NetNode,nextnode:NetNode,networkx:NetworkX):NetLink
		{
			var renlink:NetLink=null;
			networkx.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is NetLink){
					var link:NetLink=item as NetLink;
					if(link.fromNode == curnode&&link.toNode==nextnode){
						renlink= link;
					}
				}
			});
			return renlink;
		}
		/**
		 *暂时不用了 
		 * @param node
		 * @param nodes
		 * @return 
		 * 
		 */		
		private function isExistInPathNode(node:NetNode,nodes:Array):Boolean
		{
			for each(var n:NetNode in nodes){
				if(node == n){
					return true;
				}
			}
			return false;
		}
	}
}