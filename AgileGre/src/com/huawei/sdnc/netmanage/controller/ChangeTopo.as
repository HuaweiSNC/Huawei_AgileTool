package com.huawei.sdnc.netmanage.controller
{

	import com.huawei.sdnc.netmanage.ToolUtil;
	import com.huawei.sdnc.netmanage.model.NetLink;
	import com.huawei.sdnc.netmanage.model.NetNode;
	
	import mx.controls.Alert;
	
	import org.osmf.net.NetClient;
	
	import twaver.IData;
	import twaver.Node;
	import twaver.networkx.NetworkX;


	public class ChangeTopo
	{
		public function ChangeTopo()
		{
			
		}

		public function changeline(data:Object,line:NetLink):void
		{
			var cost:String = data.cost;
			var bandwidth:String = data.bandwidth; 
			//line.cost = Number(cost);
			//line.bandwidth = Number(bandwidth);
			for(var key in data)
			{
				line.setClient(key,data[key]);
			}
		}

		public function changePoint(data:Object,node:NetNode):void
		{
			if(node as NetNode){
				//				var netLink:NetLink=line as NetLink;
				//				var leftnodeID:String =  netLink.leftnodeID;
				//				var rightnodeID:String = netLink.rightnodeID;
				node.nodeType = data.nodeType;
				node.systemName = data.systemName;
				
			}
			Alert.show("修改成功！","提示");
			
		}
		/**
		 *增加线功能 
		 * @param value
		 * @param networkX
		 * 
		 */		
		public function addLink(value:Object,networkX:NetworkX):void{
			
			var leftnodeID:int = Number(value["startnodeid"]);
			var rightnodeID:int = Number(value["endnodeid"]);
			var cost:int = Number(value["cost"]);
			var bandwidth:String = value["bandwidth"];
			var fromNode:Node;
			var toNode:Node;
			var a:Boolean=true;
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is NetNode){
					var node:NetNode = item as NetNode;
					var nodeId:int = node.nodeId;
					if(leftnodeID == nodeId){
						fromNode = node;
					}else if(rightnodeID == nodeId){
						toNode = node;
					}
				}
			});
			if(!isexist(networkX,leftnodeID,rightnodeID)){
				var newLink:NetLink = new NetLink(fromNode,toNode);
				networkX.elementBox.add(newLink);
				//newLink.cost = cost;
				for(var key in value)
				{
					newLink.setClient(key,value[key]);
				}
				//newLink.bandwidth = Number(bandwidth);
				newLink.leftnodeID =leftnodeID;
				newLink.rightnodeID =rightnodeID;
			}
			if(!isexist(networkX,rightnodeID,leftnodeID)){
				var reverseLink:NetLink = new NetLink(toNode,fromNode);
				networkX.elementBox.add(reverseLink);
				for(var key in value)
				{
					reverseLink.setClient(key,value[key]);
				}
				//reverseLink.cost = cost;
				//reverseLink.bandwidth = Number(bandwidth);
				reverseLink.leftnodeID =leftnodeID;
				reverseLink.rightnodeID =rightnodeID;
			}
			Alert.show("添加成功！","提示");
			
//			
//			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
//				if(item is NetLink){
//					var link:NetLink = item as NetLink;
//					if(link.leftnodeID == leftnodeID && link.rightnodeID == rightnodeID){
//						Alert.show("已存在此方向的线，不需重复添加！","提示");
//						a=false;
//						return;
//					}
//				}else if(item is NetNode){
//				   var node:NetNode = item as NetNode;
//				   var nodeId:int = node.nodeId;
//				   if(leftnodeID == nodeId){
//					   fromNode = node;
//				   }else if(rightnodeID == nodeId){
//					   toNode = node;
//				   }
//			   }
//			});
//			if(a&&fromNode!=null && toNode!=null){
//				var newLink:NetLink = new NetLink(fromNode,toNode);
//				networkX.elementBox.add(newLink);
//				newLink.cost = cost;
//				newLink.bandwidth = Number(bandwidth);
//				newLink.leftnodeID =leftnodeID;
//				newLink.rightnodeID =rightnodeID;
//				Alert.show("添加成功！","提示");
//			}
		}
		/**
		 *增加点功能 
		 * @param point
		 * @param networkx
		 * 
		 */		

		public function saveNode(point:Object,networkx:NetworkX):void
		{
			var devicename:String = point["devicename"];
			var nodetype:String = point["nodetype"];
			var netNode:NetNode=new NetNode;
			netNode.nodeType = nodetype;
			netNode.systemName = devicename;
			netNode.name = devicename;
			netNode.nodeId=ToolUtil.getNodeNumber(networkx) + 1;
			networkx.elementBox.add(netNode);
			ToolUtil.doLayoutDefault(networkx);
		}
		/**
		 *判断是否已存在连线 
		 * @param networkX
		 * @param leftnodeID
		 * @param rightnodeID
		 * @return 
		 * 
		 */		
		private function isexist(networkX:NetworkX,leftnodeID:int,rightnodeID:int):Boolean
		{
			var exist:Boolean=false;
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is NetLink){
					var link:NetLink = item as NetLink;
					if(link.leftnodeID == leftnodeID && link.rightnodeID == rightnodeID){
						exist=true;
					}
				}
			});
			return exist;
		}
	}
}