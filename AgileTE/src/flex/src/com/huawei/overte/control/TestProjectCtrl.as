package com.huawei.overte.control
{
	import com.huawei.overte.handle.DataHandleTool;
	import com.huawei.overte.view.link.MyNewLink;
	import com.huawei.overte.view.node.StateNode;
	import com.huawei.overte.view.overte.titlewindows.ADD_Link;
	
	import flash.geom.Point;
	import flash.system.Capabilities;
	
	import mx.managers.PopUpManager;
	import mx.rpc.events.ResultEvent;
	
	import twaver.Consts;
	import twaver.Link;
	import twaver.Node;
	import twaver.Styles;
	import twaver.network.Network;
	import twaver.networkx.NetworkX;

	public class TestProjectCtrl
	{
		public function TestProjectCtrl()
		{
		}
		public static var networkX:Network;
		public static var overTECtrl:OverTECtrl;
		private static var centerPoint:Point=new Point(Capabilities.screenResolutionX/2,Capabilities.screenResolutionY/3);
		private static var nodeArray:Array=[];
		/**
		 * 假数据的topo
		 * @param e
		 * 
		 */		
		public static  function onResultForTest(e:ResultEvent):void
		{
//			topoXml:XML
			var topoXml:XML=XML(e.result);
			var deviceListXml:XMLList=topoXml.devices.device;
			
			var j:int=0;
			for each(var deviceXml1:XML in deviceListXml)
			j++;
			var num:int=j-1;
			
			var rad:Number=2*Math.PI/num;
			var initRad:Number=Math.PI/2+rad/2;
			//半径
			var radii:Number=200;
			nodeArray=[];
			networkX.elementBox.clear();
			var i:int=0;
			var devices:Array=[];
			DataHandleTool.devices=devices;
			for each(var deviceXml:XML in topoXml.devices.device){
				var stateNode:Node=new StateNode;
				var deviceobj:Object=new Object;
				stateNode.setStyle(Styles.LABEL_POSITION,Consts.POSITION_BOTTOM);
//				stateNode.setStyle(Styles.LABEL_YOFFSET, stateNode.y);
				nodeArray.push(stateNode);
				stateNode.name="\n\n"+deviceXml.devicename.toString()+"\n"+deviceXml.ip.toString();
				stateNode.setClient("username",deviceXml.username.toString());
				stateNode.setClient("devicename",deviceXml.devicename.toString());
				stateNode.setClient("passwd",deviceXml.passwd.toString());
				stateNode.setClient("ip",deviceXml.ip.toString());
				stateNode.setClient("version",deviceXml.version.toString());
				stateNode.setClient("productType",deviceXml.productType.toString());
				stateNode.setClient("id",deviceXml.id.toString());
				
				deviceobj["username"]=deviceXml.username.toString();
				deviceobj["devicename"]=deviceXml.devicename.toString();
				deviceobj["passwd"]=deviceXml.passwd.toString();
				deviceobj["ip"]=deviceXml.ip.toString();
				deviceobj["version"]=deviceXml.version.toString();
				deviceobj["productType"]=deviceXml.productType.toString();
				deviceobj["id"]=deviceXml.id.toString();
				
				var ifms:Array=[];
				for each(var ifmsxml:XML in deviceXml.ifms.interfaces["interface"]){
					var ifIndex:String=ifmsxml.ifIndex;
					var ifName:String=ifmsxml.ifName;
					
					var ifIpAddr:String=ifmsxml.am4CfgAddrs.am4CfgAddr.ifIpAddr;
					var subnetMask:String=ifmsxml.am4CfgAddrs.am4CfgAddr.subnetMask;
					var ifmobj:Object=new Object;
					ifmobj["ifIndex"]=ifIndex.toString();
					ifmobj["ifName"]=ifName.toString();
					ifmobj["ifIpAddr"]=ifIpAddr.toString();
					ifmobj["subnetMask"]=subnetMask.toString();
					ifms.push(ifmobj);
				}
				deviceobj["ifms"]=ifms;
				devices.push(deviceobj);
				stateNode.setClient("ifms",ifms);
				if(i==0){
					stateNode.location=new Point(centerPoint.x,centerPoint.y);
				}else{
					var x:Number=centerPoint.x+radii*Math.cos(initRad+i*rad);
					var y:Number=centerPoint.y+radii*Math.sin(initRad+i*rad);
					stateNode.setLocation(x,y);
				}
				stateNode.image="icon_core_ipcore";
				stateNode.setStyle(Styles.LABEL_COLOR,0xffffff);
				networkX.elementBox.add(stateNode);
				i++;
			}
			var links:Array=[];
			DataHandleTool.links=links;
			for each(var linkXml:XML in topoXml.links.link){
				var linkobj:Object=new Object;
				linkobj["linkname"]=linkXml.linkname.toString();
				linkobj["fromDevice"]=linkXml.fromDevice.toString();
				linkobj["toDevice"]=linkXml.toDevice.toString();
				linkobj["frominterface"]=linkXml.frominterface.toString();
				linkobj["tointerface"]=linkXml.tointerface.toString();
				links.push(linkobj);
			}
			
			DataHandleTool.stateNodesArr=nodeArray;
			DataHandleTool.createLinkByTnlForTest(networkX);
		}
	}
}