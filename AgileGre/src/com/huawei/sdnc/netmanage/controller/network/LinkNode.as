package com.huawei.sdnc.netmanage.controller.network
{
	import com.huawei.sdnc.netmanage.model.NetLink;
	import com.huawei.sdnc.netmanage.model.NetNode;
	
	import mx.collections.ArrayList;

	public class LinkNode
	{
		public function LinkNode()
		{
			
		}
  
		public var count:int = 0;
		// 本节点指向的netNode
		public var nodeid:int = 0;
		//由父节点指向本节点的Netlink
		public var pComeinLink:NetLink = null;
		// 指定父节点的node
		public var pFatherNode:LinkNode = null;
		// 指定子节点的链接列表
		private var pChildNodeList:ArrayList = new ArrayList();
		// 增加子节点
		public function addChildNode(node:LinkNode):void
		{
			if (null != node)
			{
				pChildNodeList.addItem(node);			
			}
		}
		 
		//判断是否是闭环
		private function isbreakCirle():Boolean
		{
			var ptmpNode:LinkNode = this.pFatherNode;
			while(ptmpNode != null)
			{
				if (this.nodeid == ptmpNode.nodeid)
				{
					return true;
				}
				ptmpNode = ptmpNode.pFatherNode;
			}
			
			return false;
		}
		
	 
		public function plan(srcNodeID:int, dstNodeID:int, bandwidth:int, network:MptNetworkPlanner):void
		{
			// 已找到终给点
			if(dstNodeID == nodeid)
			{
				network.addResult(this)
				return;
			}
			
			// 当前的节点为空，或者是是闭环，直接退出
			if(0 == nodeid || isbreakCirle())
			{
				return;
			}
			
			// 防止成环的情况发生
			if(pFatherNode != null && srcNodeID == nodeid)
			{
				return;
			}
			
			var nodeArray:Array = network.getNextLinks(nodeid, bandwidth);
			var nodenet:NetLink = null;
			var nextNode:LinkNode = null;
			// 加一个层次
			var number:int = count+1;
			// 开始递归查找数据
			for(var i:int = 0; i < nodeArray.length; i++)
			{
				nodenet = nodeArray[i];
				nextNode = new LinkNode();
				nextNode.pComeinLink = nodenet;
				nextNode.nodeid = nodenet.rightnodeID;
				nextNode.pFatherNode = this;
				nextNode.count = number;
				pChildNodeList.addItem(nextNode);
				nextNode.plan(srcNodeID, dstNodeID, bandwidth, network);
			}
 	
		}
	
	}
	
}