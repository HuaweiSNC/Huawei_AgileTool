package com.huawei.sdnc.netmanage.controller.network
{
	import com.huawei.sdnc.netmanage.model.NetLink;
	import com.huawei.sdnc.netmanage.model.NetNode;
	import com.huawei.sdnc.netmanage.model.NetPlanner;
	
	import mx.collections.ArrayList;

	public class MptNetworkPlanner
	{
		private var nodeArray:Array;
		private var linkArray:Array;
		private var resultNodeList:ArrayList = new ArrayList();
		 
	 
		
		public function MptNetworkPlanner()
		{
			
		}
		
		public function setNetWork(myNodeArray:Array, myLinkArray:Array):void
		{
			nodeArray = myNodeArray;
			linkArray = myLinkArray;
		}
		
		
		/***
		 * 根据源节点ID来获取指定的节点
		 * 
		 * **/
		public function plan(netPlan:NetPlanner) : Array
		{
			var retArray:Array=[];
			if (null == nodeArray || null == linkArray)
			{
				return retArray;
			}
			
			// 获取需要的数据
			var dstNode:int = netPlan.dstNodeID;
			var srcNode:int = netPlan.srcNodeID;
			var bandwidth:int = netPlan.bandwidth;
			resultNodeList.removeAll();
				
			// 开始展开计算, 展开了树结构后，再进一步计算可行的路线
			var rootNode:LinkNode = new LinkNode();
			var node:NetNode = getNetNode(srcNode);
			rootNode.nodeid = node.nodeId;
			rootNode.plan(srcNode, dstNode, bandwidth, this);
			
			// 获取最短的路径数据
			return getShortSuitable();
		}
		
		// 计算当前节点的权值 , 当前只计算层次
		private function getValueOfLinkNode(node:LinkNode):int
		{
			return node.count;
		}
		
		//返回最短路径信息 
		public function getShortSuitable():Array
		{
			var nodeSecondValue:int = 0;
			var nodeValue:int = 0;
			var node:LinkNode = null;
			var shortNode:LinkNode = null;
			var retArray:Array = new Array;
			var retArrayTmp:Array = new Array;
			// 查找最短路径NetLink
			for(var i:int = 0; i < resultNodeList.length; i++)
			{
				node = resultNodeList.getItemAt(i) as LinkNode;
				if(nodeValue ==0 )
				{
					nodeValue = getValueOfLinkNode(node);
					shortNode = node;
					continue;
				}  
				
				nodeSecondValue = getValueOfLinkNode(node);
                // 最短路径的条件, 层次最少
				if (nodeValue > nodeSecondValue)
				{
					nodeValue = nodeSecondValue;
					shortNode = node;
				}
			}
			
			// 返回获取的数据
			if(shortNode != null)
			{
				// 向上找当前数据
				while (shortNode != null)
				{
					retArrayTmp.push(shortNode.pComeinLink);
					shortNode = shortNode.pFatherNode
				}
				// 堆栽反转
				var pComeinLink:NetLink = null;
				while (retArrayTmp.length > 0)
				{
					pComeinLink = retArrayTmp.pop();
					retArray.push(pComeinLink);
				}
			}
			
			return retArray;
			 
		}
		
		public function addResult(node:LinkNode):void
		{
			resultNodeList.addItem(node);
		}

		/***
		 * 根据源节点ID来获取指定的节点
		 * 
		 * **/
		public function getNextLinks(srcnodeid:int, bandwidth:int): Array
		{
			var retArray:Array = [];
			var nodelink:NetLink = null;
			for(var i:int = 0; i < linkArray.length; i++){
				
				nodelink = linkArray[i];
				// 计算符合带宽条件, 以及符合当前起始节点的
				if(nodelink.leftnodeID == srcnodeid && bandwidth <= nodelink.bandwidth)
				{
					retArray.push(nodelink);
				}
			}
			
			return retArray;
		}
		
		/***
		 * 根据节点ID来获取指定的节点
		 * 
		 * **/
		public function getNetNode(nodeId:int): NetNode
		{
			var nodeObject:NetNode = null;
			for(var i:int=0;i<nodeArray.length;i++){
			
				nodeObject = nodeArray[i];
				if (nodeObject.nodeId == nodeId)
				{
					break;
				}
			}
			return nodeObject;
		}
		
  }
	
}