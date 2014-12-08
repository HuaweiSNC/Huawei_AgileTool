package com.huawei.sdnc.netmanage.controller
{
	import com.huawei.sdnc.netmanage.model.NetLink;
	import com.huawei.sdnc.netmanage.model.NetNode;
	import com.huawei.sdnc.tools.ConnUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.netmanage.model.datas;
	import flash.events.Event;
	import flash.utils.ByteArray;
	
	import mx.controls.Alert;
	
	import org.httpclient.HttpResponse;
	import org.httpclient.events.HttpDataEvent;
	import org.httpclient.events.HttpResponseEvent;
	
	import twaver.IData;
	import twaver.networkx.NetworkX;

	public class iOpsUtil
	{
		public function iOpsUtil()
		{
		}
		/**
		 * 发送put请求,拼出body,从已有topo到得node,link，从前台用户数据的 得到userlink
		 * */

		public var networkx:NetworkX;

		public var opsIp:String=SdncUtil.opsIp;

		public function makeBody(userData:Object,networkX:NetworkX):void{

			var toponode:String ="<toponodes>";
			var topolink:String ="<topoLinks>";
			var userdata:String = "";
			 networkx = networkX;
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void
			{
				if(item is NetNode)
				{
					var netNode:NetNode=item as NetNode;
					var nodeId:int = netNode.nodeId;
					var nodeType:String = netNode.nodeType;
					var systemName:String = netNode.systemName;
					toponode += "<toponode>" 
					                        +"<nodeID>"+nodeId+"</nodeID>" 
					                        +"<nodeType>"+nodeType+"</nodeType>" 
					                        +"<systemName>"+systemName+"</systemName>"
					                +"</toponode>";
				}
				else if(item is NetLink)
				{
					var netLink:NetLink=item as NetLink;
					var leftnodeID:int =  netLink.leftnodeID;
					var rightnodeID:int = netLink.rightnodeID;
					var cost:String = netLink.cost.toString();
					var bandwidth:String = netLink.bandwidth.toString();
					topolink += "<topoLink>"                        
       								 +"<leftnodeID>"+leftnodeID+"</leftnodeID>"      
       								 +"<rightnodeID>"+rightnodeID+"</rightnodeID>"    
       								 +" <cost>"+cost+"</cost>"                
	   								 +"<bandwidth>"+bandwidth+"</bandwidth>"      
      							     +"</topoLink>" 
				}
			});
			toponode += "</toponodes>";
			topolink += "</topoLinks></topo>";
			var srcNodeID:String = userData["srcNodeID"];
			var dstNodeID:String = userData["dstNodeID"];
			var bandwidth:String = userData["bandwidth"];
			userdata  = "<userLinks><userLink>"
								 +"<srcNodeID>"+srcNodeID+"</srcNodeID>"
								 +"<dstNodeID>"+dstNodeID+"</dstNodeID>"
								 +"<bandwidth>"+bandwidth+"</bandwidth>"
				                 +"</userLink></userLinks>";        
			var body:String = "<topodata><topo>"+toponode+topolink+userdata+"</topodata>";
			putTopoPath(body);
		}
		public function putTopoPath(body:String):void{
			var url:String = "http://"+opsIp+"/topopath";
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
			if(data == null){
				Alert.show("没有计算出路径","提示");
				return;
			}else if(data.match("Error: 500 Internal Server Error")){
				Alert.show("设备连接错误500","提示");
				return;
			}
			var topoPath:XML=XML(data);
			var index:int=data.indexOf("<paths>");
			if(index != -1){
				var result:String="<topopath>"+data.substr(index);
				topoPath=XML(result);
				for each(var path:XML in topoPath.paths.path){
					var strn:String=path.nodeID.toString();
					var reg:RegExp=/<nodeID>|<\/nodeID>/ig;
					var str:String=strn..replace(/\n/ig,"").replace(reg,",").replace(/,,/ig,",").replace(",","");
					var s:String=str.substring(0,str.lastIndexOf(","));
					var pathIDArr:Array = s.split(",");
					var signPath:SignPath = new SignPath();
					signPath.signPath(pathIDArr,networkx);
					break;
				}
			}
		}
		
		private function putTopoPathFault(e:Event):void
		{
		trace("获取topoPath出错");
		}
	}
}