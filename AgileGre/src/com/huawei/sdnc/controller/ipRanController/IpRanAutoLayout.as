package com.huawei.sdnc.controller.ipRanController
{
	import baidu.map.basetype.LngLat;
	
	import flash.geom.Matrix;
	import flash.geom.Point;
	import flash.utils.Dictionary;
	
	import mx.controls.Alert;
	
	import twaver.Element;
	import twaver.ElementBox;
	import twaver.IData;
	import twaver.Node;

	public class IpRanAutoLayout
	{
		
		public function IpRanAutoLayout()
		{
		}
		
		public var eBox:ElementBox;
		public var l3topoXml:XML
		public var nodes:Array;
		public var links:Array;
		/**所有节点*/
		private var allNodes:Array=[];
		
		/****内环links*/
		private var insideLinks:Array=[];
		/**外环links*/
		private var externalLinks:Array=[];
		
		/**
		 * 类型为N的节点
		 * */
		private var nNodes:Array=[];
		
		
		//------------------------------------------------------
		/**csg节点*/
		private var csgNodes:Array=[];
		/**外环节点的分组*/
		private var csgNodeArrs:Array=[];
		/**内环节点*/
		private var rsgAsgNodes:Array=[];
		//中心点
		private var centerPoit:Point=new Point(800,300);
		//半径
		private var radii:Number=150;
		//初始弧度
		private var initRadin:Number=Math.PI/2;
		
		public function autoLayout():void
		{
			getRsgAndAsgNode();
			getInsideAndExternalLinks();
			sortInsideNodes();
			sortExternalNodes();
			
			
			//分为多少份
			var num:int=rsgAsgNodes.length;
			//放置core节点
			if(eBox.containsByID("1"))
			{
				var no:Node=eBox.getElementByID("1") as Node;
			    no.centerLocation=new Point(750,50);
				no.image = "core";
			}else
			{
				var node1:Node=new Node("1");
				node1.centerLocation=new Point(750,50);
				node1.setClient("nodeType","core");
				node1.image = "core";
				eBox.add(node1);
			}
			
			//布局rsg,asg
			for(var c:int=0;c<rsgAsgNodes.length;c++ )
			{
				var node:Node=rsgAsgNodes[c] as Node;
				var gg:String=node.getClient("nodeType");
				
				var x:Number=radii*Math.cos(initRadin+2*Math.PI/num*c)+centerPoit.x;
				var y:Number=centerPoit.y-radii*Math.sin(initRadin+2*Math.PI/num*c);
				node.setLocation(x,y);
				eBox.add(node);
			}
			
			//布局外环
			for each(var csgNodeArr:Array in csgNodeArrs)
			{
				var l:int=csgNodeArr.length;
				if(l==2)continue;
				var firstId:String=csgNodeArr[0];
				var secondId:String=csgNodeArr[l-1];
				var firstPoint:Point=null;
				var secondPoint:Point=null;
				eBox.forEachByBreadthFirst(function(item:IData):void{
					if(item is Node)
					{
						var node:Node=item as Node;
						var id:String=node.id as String;
						if(id==firstId)
						{
							firstPoint=node.location;
						}
						if(id==secondId)
						{
							secondPoint=node.location;
						}
					}
				});
				var cenX:Number=(firstPoint.x+secondPoint.x)/2;
				var cenY:Number=(firstPoint.y+secondPoint.y)/2;
				var o:Number=100;
				if(cenX<500)
				{
					o=-100;
				}
				
				var cuPoint:Point=new Point(cenX,cenY);
				var matrix:Matrix = new Matrix();
				matrix.translate(-cuPoint.x, -cuPoint.y);
				var hudu:Number=getAngle(cuPoint, centerPoit);
				matrix.rotate(hudu);
				matrix.translate(cuPoint.x, cuPoint.y);
				var cq:Point = matrix.transformPoint(new Point(cuPoint.x, cuPoint.y+150));
				
//				var n:Node=new Node;
//				n.name="中心";
//				n.location=cq;
//				eBox.add(n);
				
				//算出已用角度
				var a:Number=Point.distance(firstPoint,cuPoint);
				var b:Number=Point.distance(cuPoint,cq);
				var alreadyUsed:Number=2*Math.atan(a/b);
				var banjing:Number=Point.distance(cq,firstPoint);
				var leavedrad:Number=2*Math.PI-alreadyUsed;
				
				
				
				var obj:Object=getInitialAngle(cq,firstPoint,secondPoint);
				if(obj==null)
					continue;
				var radian:Number=obj["initialAngle"] as Number;
				var angleIndex:String=obj["angleIndex"];
				if(angleIndex=="firstAngle")
				{
					
				}
				else
				{
					var newArr:Array=new Array;
					for(var ii:int=csgNodeArr.length-1;ii>=0;ii--){
						newArr.push(csgNodeArr[ii]);
					}
					csgNodeArr=newArr;
				}
				
				var i:int=1;
				for(var j:int=0;j<csgNodeArr.length;j++)
				{
					var csgNodeId:String=csgNodeArr[j];
					for each(var n:Node in csgNodes)
					{
						var d:String=n.id as String;
						if(csgNodeId==d)
						{
							n.setLocation(banjing*Math.cos(radian+leavedrad/(csgNodeArr.length-1)*i)+cq.x,cq.y-banjing*Math.sin(radian+leavedrad/(csgNodeArr.length-1)*i));
							eBox.add(n);
							i++;
							
							//查询相连的n类型节点
							var id:String="";
							var nd:Node=null;
							for each(var dic:Dictionary in nLinks)
							{
								var leftRouterId:String=dic["leftRoterID"];
								var rightRouterId:String=dic["rigthRouterID"];
								if(csgNodeId==leftRouterId)
								{
									id=rightRouterId;
								}else if(csgNodeId==rightRouterId){
									id=leftRouterId;
								}
							}
							if(id!="")
							{
								for each(var n1:Node in allNodes)
								{
									var id1:String=n1.id as String;
									var type:String=n1.getClient("nodeType");
									if(type=="n"){
										if(id1==id){
											nd=n1;
											break;
										}
									}
								}
//								var m:Matrix = new Matrix();
//								m.translate(-n.x, -n.y);
//								var an:Number=getAngle(n.location,cq);
//								m.rotate(an);
//								m.translate(n.x, n.y);
//								var cx:Point = matrix.transformPoint(new Point(n.x, n.y+20));
								
								if(n.location.x<cq.x){
								    nd.location=new Point(n.location.x-150,n.location.y);
								}else if(n.location.x>cq.x){
									nd.location=new Point(n.location.x+150,n.location.y);
								}
								eBox.add(nd);
							}
							break;
						}
					}
				}
			}
			
		}
		
		
		/**
		 * 获取大环节点，rsg、asg类型节点，并排序
		 * */
		
		private function getRsgAndAsgNode():void
		{
			for each(var nXml:XML in l3topoXml.l3toponodes.l3toponode)
			{
				var nodeType:String=nXml.nodeType;
				var routerId:String=nXml.routerID;
				var nodeName:String=nXml.systemName;
				var node:Node=new Node(routerId);
				node.setClient("nodeType",nodeType);
				var lngLat:LngLat = new LngLat(nXml.longitude,nXml.latitude);
				node.setClient("lngLat",lngLat);
				node.name = nodeName;
				
				if(nodeType=="rsg")
				{
					node.setClient("nodeType","rsg");
					rsgAsgNodes.push(node);
				}
				else if(nodeType=="asg")
				{
					node.setClient("nodeType","asg");
					rsgAsgNodes.push(node);
				}
				else if(nodeType=="csg")
				{
					node.setClient("nodeType","csg");
					csgNodes.push(node);
				}
				else if(nodeType=="core")
				{
					node.setClient("nodeType","core");
				}
				else 
				{
					node.setClient("nodeType","n");
				}
				
				if(node.getClient("nodeType") == "core"){
					node.image = "core_withmap";
				}else if(node.getClient("nodeType") == "n"){
					node.image = "n";
					nNodes.push(node);
				}else{
					node.image = "asg";
				}
				allNodes.push(node);
			}
			
		}
		
		private var nLinks:Array=[];
		/**
		 * 获取外环link和内环link
		 * */
		private function getInsideAndExternalLinks():void
		{
			for each(var lXml:XML in l3topoXml.l3topolinks.l3topolink)
			{
				var leftRoterID:String=lXml.leftRoterID;
				var rigthRouterID:String=lXml.rigthRouterID;
				var linkInfo:Dictionary=new Dictionary();
				linkInfo["leftRoterID"]=leftRoterID;
				linkInfo["rigthRouterID"]=rigthRouterID;
				linkInfo["isUsed"]=false;
				if(isInsideNode(leftRoterID)&&isInsideNode(rigthRouterID))
				{
					if(!isEcho(linkInfo))
					insideLinks.push(linkInfo);
				}
				
				else if(!isN_CoreTypeId(leftRoterID)&&!isN_CoreTypeId(rigthRouterID))
				{
					externalLinks.push(linkInfo);
				}else
				{
					for each(var n:Node in allNodes)
					{
						var id1:String=n.id as String;
						var type:String=n.getClient("nodeType");
						if(type=="n"){
							if(id1==leftRoterID||id1==rigthRouterID){
								nLinks.push(linkInfo);
							}
						}
					}
				}
			}
		}
		/**
		 * 对内环节点排序
		 * */
		
		private function sortInsideNodes():void
		{
			var sortedArr:Array=[];
			var firstId:String="2";
			var curId:String=firstId;
			sortedArr.push(curId);
			for(var i:int=0;i<insideLinks.length;i++)
			{
				for each(var d:Dictionary in insideLinks)
				{
					var isUsed:Boolean=d["isUsed"];
					if(!isUsed)
					{
						var leftRoterID:String=d["leftRoterID"];
						var rigthRouterID:String=d["rigthRouterID"];
						if(leftRoterID==curId)
						{
							sortedArr.push(rigthRouterID);
							curId=rigthRouterID;
							d["isUsed"]=true;
						}else if(rigthRouterID==curId)
						{
							sortedArr.push(leftRoterID);
							curId=leftRoterID;
							d["isUsed"]=true;
						}
					}
				}
				
			}
			var sortNodes:Array=[];
			for(var j:int=0;j<sortedArr.length-1;j++)
			{
				var id:String=sortedArr[j];
				for each(var node:Node in rsgAsgNodes)
				{
					if(String(node.id)==id)
					{
						sortNodes.push(node);
						break;
					}
				}
			}
			rsgAsgNodes=sortNodes;
		}
		/**
		 * 对外环节点分组、排序
		 * */
		
		private function sortExternalNodes():void
		{
			var array:Array=[];
			var isExist:Boolean=false;
			for each(var rsgNode:Node in rsgAsgNodes)
			{
				
				if(isExistInExternalLinks(rsgNode))
				{
					var csgArray:Array=[];
					var routerId:String=rsgNode.id as String;
					
					for each(var id:String in array)
					{
						if(id==routerId)
							isExist=true;
						break;
					}
					if(isExist)
					{
						isExist=false;
						continue;
					}
					csgArray.push(routerId);
					var curRouterId:String=routerId;
					for each(var dicL:Dictionary in externalLinks)
					{
						dicL["isUsed"]=false;
					}
					for each(var dicLink:Dictionary in externalLinks)
					{
						var isUsed:Boolean=dicLink["isUsed"];
						if(!isUsed){
							var leftId:String=dicLink["leftRoterID"];
							var rightId:String=dicLink["rigthRouterID"];
							if(leftId==curRouterId&&rightId!=curRouterId)
							{
								csgArray.push(rightId);
								curRouterId=rightId;
								dicLink["isUsed"]=true;
								if(isInsideNode(rightId))
								{
									array.push(rightId);
									csgNodeArrs.push(csgArray);
									break;
								}
								
							}else if(rightId==curRouterId&&leftId!=curRouterId)
							{
								csgArray.push(leftId);
								curRouterId=leftId;
								dicLink["isUsed"]=true;
								//如果下一个节点是asg类型
								if(isInsideNode(leftId))
								{
									array.push(rightId);
									csgNodeArrs.push(csgArray);
									break;
								}
							}
						}
					}
				}
			}
			
			
		}
		/**
		 * 判断节点是否是内环节点
		 * 
		 * */
		private function isInsideNode(routerId:String):Boolean
		{
			for each(var node:Node in rsgAsgNodes)
			{
				var Id:String=node.id as String;
				if(routerId==Id)
				{
					return true;
				}
			}
			return false;
		}
		/**
		 * 判断是否是N类型或者Core类型
		 * */
		public function isN_CoreTypeId(nodeId:String):Boolean
		{
			for each(var n:Node in allNodes)
			{
				var id1:String=n.id as String;
				var type:String=n.getClient("nodeType");
				if(type=="n"&&id1==nodeId){
					return true;
				}
				if(type=="core"&&id1==nodeId){
					return true;
				}
			}
			
			return false;
		}
		/**
		 * 判断内环link数组insideLinks中是否已存在当前link
		 * 
		 * */
		public function isEcho(data:Dictionary):Boolean
		{
			var curLeft:String=data["leftRoterID"];
			var curRight:String=data["rigthRouterID"];
			for each(var di:Dictionary in insideLinks)
			{
				var leftRoterID:String=di["leftRoterID"];
				var rigthRouterID:String=di["rigthRouterID"];
				if(curLeft==leftRoterID&&curRight==rigthRouterID){
					return true;
				}else if(curLeft==rigthRouterID&&curRight==leftRoterID){
					return true;
				}
			}
			
			return false;
		}
		/**
		 * 判断节点是包含于外环link中
		 * */
		public function isExistInExternalLinks(node:Node):Boolean
		{
			var routerId:String=node.id as String;
			for each(var dicLink:Dictionary in externalLinks)
			{
				var leftId:String=dicLink["leftRoterID"];
				var rightId:String=dicLink["rigthRouterID"];
				if(routerId==leftId||routerId==rightId)
				{
					return true;
				}
			}
			return false;
		}
		/**
		 * @param innerCenter 内环的中心点
		 * */
		public static function getAngle(p1:Point, innerCenter:Point):Number {
			if(p1.x == innerCenter.x){
				if(innerCenter.y == p1.y){
					return 0;
				}
				else if(innerCenter.y > p1.y){
					return Math.PI/2;
				}
				else{
					return -Math.PI/2;
				}
			}
			if(innerCenter.x<p1.x&&innerCenter.y<p1.y){
				return -(Math.PI/2-Math.atan((innerCenter.y - p1.y) / (innerCenter.x - p1.x)));
//				return -Math.atan((innerCenter.y - p1.y) / (innerCenter.x - p1.x));
			}else if(innerCenter.x>p1.x&&innerCenter.y<p1.y){
				return Math.PI/2+Math.atan((innerCenter.y - p1.y) / (innerCenter.x - p1.x));
			}else if(innerCenter.x<p1.x&&innerCenter.y>p1.y){
				return Math.atan((innerCenter.y - p1.y) / (innerCenter.x - p1.x))-Math.PI/2;
			}else if(innerCenter.x>p1.x&&innerCenter.y>p1.y){
				return Math.atan((innerCenter.y - p1.y) / (innerCenter.x - p1.x))+Math.PI/2;
			}
			return Math.atan((innerCenter.y - p1.y) / (innerCenter.x - p1.x));
		}
		/**
		 * 获取初始弧度
		 * @param centerPoint外环圆心
		 * */
		public function getInitialAngle(centerPoint:Point,firstPoint:Point,secondPonit:Point):Object
		{
			var obj:Object=new Object;
			var cenX:Number=(firstPoint.x+secondPonit.x)/2;
			var cenY:Number=(firstPoint.y+secondPonit.y)/2;
			var firstAngle:Number=-Math.atan((firstPoint.y-centerPoint.y)/(firstPoint.x-centerPoint.x));
			var secondeAngle:Number=-Math.atan((secondPonit.y-centerPoint.y)/(secondPonit.x-centerPoint.x));
			if(centerPoint.x<cenX&&centerPoint.y>cenY){
				if(firstAngle>secondeAngle){
					obj["initialAngle"]=firstAngle;
					obj["angleIndex"]="firstAngle";
					return obj;
				}else{
					obj["initialAngle"]=secondeAngle;
					obj["angleIndex"]="secondeAngle";
					return obj;
				}
			}if(centerPoint.x>cenX&&centerPoint.y>cenY){
				if(firstAngle<0&&secondeAngle<0&&firstAngle>secondeAngle){
					obj["initialAngle"]=Math.PI+firstAngle;
					obj["angleIndex"]="firstAngle";
					return obj;
				}else if(firstAngle<0&&secondeAngle<0&&secondeAngle>firstAngle){
					obj["initialAngle"]=Math.PI+secondeAngle;
					obj["angleIndex"]="secondeAngle";
					return Math.PI+secondeAngle;
				}else if(firstAngle<0&&secondeAngle>0){
					obj["initialAngle"]=Math.PI+secondeAngle;
					obj["angleIndex"]="secondeAngle";
					return obj;
				}else if(firstAngle>0&&secondeAngle<0){
					obj["initialAngle"]=Math.PI+firstAngle;
					obj["angleIndex"]="firstAngle";
					return obj;
				}
			}
			return null;
		}
		
		
	}
}