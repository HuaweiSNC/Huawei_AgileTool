package com.huawei.sdnc.netmanage.controller
{
	import com.huawei.sdnc.netmanage.model.NetLink;
	import com.huawei.sdnc.netmanage.model.NetNode;
	import com.huawei.sdnc.netmanage.model.datas;
	import com.huawei.sdnc.tools.ConnUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	
	import flash.events.Event;
	import flash.utils.ByteArray;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	
	import org.httpclient.HttpResponse;
	import org.httpclient.events.HttpDataEvent;
	import org.httpclient.events.HttpResponseEvent;
	
	import twaver.IData;
	import twaver.networkx.NetworkX;

	public class OpsUtil
	{
		public function OpsUtil()
		{
		}
		/**
		 * 发送put请求,拼出body,从已有topo到得node,link，从前台用户数据的 得到userlink
		 * */

		public var mydata:datas = datas.getInstence();
		public var xml:XMLList;
		public var networkx:NetworkX;
		public var xmlre:String
		public var opsIp:String=SdncUtil.opsIp;
		
		public function makeBody(userDatas:ArrayCollection,networkX:NetworkX):void
		{
			 
			xml = new XMLList;
			xml = mydata.AlgoritmXML.requestcontent.topodata;
			xmlre = mydata.AlgoritmXML.toString();
			
			//复制出需要的节点
			var nodesxml:XMLList = xml.topo.toponodes.foreachnode.toponode;
			var linksxml:XMLList = xml.topo.topoLinks.foreachlink.topoLink;
			var userlinksxml:XMLList = xml.userLinks.foreachuserlink.userLink;
			var arrl :Array = xml..topo.topoLinks.foreachlink.@property.split(",")
			var arrul :Array = xml..userLinks.foreachuserlink.@property.split(",")
				//删除不需要的节点
			delete xml.topo.toponodes.foreachnode[0];
			delete xml.topo.topoLinks.foreachlink[0];
			delete xml.userLinks.foreachuserlink[0];
			
			networkx = networkX;
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void
			{
				if(item is NetNode)
				{
					var toponode:String ="";
					var netNode:NetNode=item as NetNode;
					var nodeId:int = netNode.nodeId;
					var nodeType:String = netNode.nodeType;
					var systemName:String = netNode.systemName;
					toponode += "<toponode>" 
					+"<nodeID>"+nodeId+"</nodeID>" 
					+"<nodeType>"+nodeType+"</nodeType>" 
					+"<systemName>"+systemName+"</systemName>"
					+"</toponode>";
					xml.topo.toponodes.appendChild(XML(toponode))
				}
				else if(item is NetLink)
				{
					var netLink:NetLink=item as NetLink;
					
					var str:String = linksxml.toString()
					str = str.replace("${startnodeid}",netLink.leftnodeID)
					str = str.replace("${endnodeid}",netLink.rightnodeID)
					for (var i:int = 0;i<arrl.length;i++)
					{
						str = str.replace("${"+arrl[i]+"}",netLink.getClient(arrl[i]))
					}
					xml.topo.topoLinks.appendChild(XML(str))
				}
			});
			for(var x:int = 0;x<userDatas.length;x++)
			{
				var userData:Object = userDatas[x]
				var ustr:String = userlinksxml.toString()
				ustr = ustr.replace("${leftnode}",userData["leftnode"])
				ustr = ustr.replace("${rightnode}",userData["rightnode"])
				for(var j:int = 0;j<arrul.length;j++)
				{
					ustr = ustr.replace("${"+arrul[j]+"}",userData[arrul[j]])
				}
				xml.userLinks.appendChild(XML(ustr))
			}

			mydata.AlgoritmXML = XML(xmlre)
			//Alert.show(mydata.AlgoritmXML);
			var arr:Array = String(xml).split("\n");
			var str:String = ""
				for(var i:int = 0;i<arr.length;i++)
				{
					str+=arr[i].toString();
				}
				for(;str.search("null")!=-1||str.search("undefined")!=-1;)
				{
				str = str.replace("null","0")
				str = str.replace("undefined","0")
				}
			putTopoPath(str);
			//     ------------------------------------新代码与旧代码的分割线----------------------------------
			
		}
		
		
		public function putTopoPath(body:String):void
		{
			var url:String = "http://"+opsIp+"/topopath/"+mydata.AlNowID;
			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_PUT,putTopoPathResult,putTopoPathFault,body,"application/xml");
		}
		
		/**
		 * 解析获取的路线
		 * */
		private function putTopoPathResult(e:HttpDataEvent):void
		{
			
			var data:String = e.bytes.toString();
			//			var data:String = "<topopath xmlns='http://www.huawei.com/netconf/vrp' xmlns:nc='urn:ietf:params:xml:ns:netconf:base:1.0' format-version='1.0' content-version='1.0'>"+
			//    "<paths><path><nodeID>5</nodeID><nodeID>1</nodeID><nodeID>2</nodeID><nodeID>7</nodeID></path></paths></topopath>";
			if(data == null)
			{
				Alert.show("没有计算出路径","提示");
				return;
			}
			else if(data.match("Error: 500 Internal Server Error"))
			{
				Alert.show("设备连接错误500","提示");
				return;
			}
			var topoPath:XML=XML(data).children()[0];
			var index:int=data.indexOf("<paths>");
			var arr:Array = [[],[]];
			//var aaa:Array = new Array;
			
			if(index != -1)
			{
				for(var i:int = 0;i<topoPath.children().length();i++)
				{
					arr[i] = new Array
					var xml:XML = topoPath.children()[i];
					for( var j:int = 0;j<xml.children().length();j++)
					{
						var a:XML = xml.children()[j];
					//	var b:XML = a.children()[0];
						arr[i][j] = a.children()[0];
					}
					
				}
				mydata.Alresult = arr;
				
				//     ------------------------------------新代码与旧代码的分割线----------------------------------
//				var result:String="<topopath>"+data.substr(index);
//				topoPath=XML(result);
//				for each(var path:XML in topoPath.paths.path)
//				{
//					var strn:String=path.nodeID.toString();
//					var reg:RegExp=/<nodeID>|<\/nodeID>/ig;
//					var str:String=strn..replace(/\n/ig,"").replace(reg,",").replace(/,,/ig,",").replace(",","");
//					var s:String=str.substring(0,str.lastIndexOf(","));
//					var pathIDArr:Array = s.split(",");
//					var signPath:SignPath = new SignPath();
//					signPath.signPath(pathIDArr,networkx);
//					break;
//				}
			}
			
		}
		
		private function putTopoPathFault(e:Event):void
		{
			trace("获取topoPath出错");
		}
		
	}
}