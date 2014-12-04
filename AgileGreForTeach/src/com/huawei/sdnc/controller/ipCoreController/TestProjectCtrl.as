package com.huawei.sdnc.controller.ipCoreController
{
	import com.huawei.sdnc.view.common.node.StateNode;
	import com.huawei.sdnc.view.gre.dataHandle.DataHandleTool;
	
	import flash.geom.Point;
	import flash.system.Capabilities;
	
	import mx.collections.ArrayCollection;
	import mx.rpc.events.ResultEvent;
	
	import twaver.Consts;
	import twaver.Node;
	import twaver.Styles;
	import twaver.networkx.NetworkX;

	public class TestProjectCtrl
	{
		public function TestProjectCtrl()
		{
		}
		public static var networkX:NetworkX;
		public static var ipcorePhysicsCtrl:IpCoreForPhysicsCtrl;
		private static var centerPoint:Point=new Point(Capabilities.screenResolutionX/2,Capabilities.screenResolutionY/3);
		private static var nodeArray:Array=[];
		/**
		 * 假数据的topo
		 * @param e
		 * 
		 */		
		public static  function onResultForTest(e:ResultEvent):void
		{
			var topoXml:XML=XML(e.result);
			var deviceListXml:XMLList=topoXml.device;
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
			for each(var deviceXml:XML in topoXml.device){
				var stateNode:Node=new StateNode;
				var deviceobj:Object=new Object;
				stateNode.setStyle(Styles.LABEL_POSITION,Consts.POSITION_BOTTOM_BOTTOM);
				nodeArray.push(stateNode);
				stateNode.name=deviceXml.devicename;
				stateNode.setClient("username",deviceXml.username);
				stateNode.setClient("devicename",deviceXml.devicename);
				stateNode.setClient("passwd",deviceXml.passwd);
				stateNode.setClient("ip",deviceXml.ip);
				stateNode.setClient("version",deviceXml.version);
				stateNode.setClient("productType",deviceXml.productType);
				stateNode.setClient("id",deviceXml.id);
				
				deviceobj["username"]=deviceXml.username;
				deviceobj["devicename"]=deviceXml.devicename;
				deviceobj["passwd"]=deviceXml.passwd;
				deviceobj["ip"]=deviceXml.ip;
				deviceobj["version"]=deviceXml.version;
				deviceobj["productType"]=deviceXml.productType;
				deviceobj["id"]=deviceXml.id;
				devices.push(deviceobj);
				//初始化流策略
				var policyes:Array=[];
				for each(var policyXml:XML in deviceXml.policys.policy){
					var policyName:String=policyXml.policyName;
					var policyType:String=policyXml.policyType;
					var srcIp:String=policyXml.srcIp;
					var tnlNameInPolicy:String=policyXml.tnlName;
					var desIp:String=policyXml.desIp;
					var obj:Object=new Object;
					obj["policyName"]=policyName;
					obj["policyType"]=policyType;
					obj["srcIp"]=srcIp;
					obj["tnlName"]=tnlNameInPolicy;
					obj["desIp"]=desIp;
					policyes.push(obj);
				}
				stateNode.setClient("policyes",policyes);
				//初始化tnl
				var tnls:Array=[];
				for each(var tnlXml:XML in deviceXml.greTunnels.greTunnel){
					var tnlName:String=tnlXml.tnlName;
					var tnlType:String=tnlXml.tnlType;
					
					var srcType:String=tnlXml.srcType;
					var srcIpAddr:String=tnlXml.srcIpAddr;
					var srcIfName:String=tnlXml.srcIfName;
					var dstVpnName:String=tnlXml.dstVpnName;
					var dstIpAddr:String=tnlXml.dstIpAddr;
					var tnlobj:Object=new Object;
					tnlobj["tnlName"]=tnlName;
					tnlobj["tnlType"]=tnlType;
					tnlobj["srcType"]=srcType;
					if(srcType=="隧道源ip"){
						tnlobj["src_value"]=srcIpAddr;
					}else{
						tnlobj["src_value"]=srcIfName;
					}
					
					tnlobj["srcIpAddr"]=srcIpAddr;
					tnlobj["srcIfName"]=srcIfName;
					tnlobj["dstVpnName"]=dstVpnName;
					tnlobj["dstIpAddr"]=dstIpAddr;
					tnls.push(tnlobj);
				}
				stateNode.setClient("tnls",tnls);
				if(i==0){
					stateNode.location=new Point(centerPoint.x,centerPoint.y);
				}else{
					var x:Number=centerPoint.x+radii*Math.cos(initRad+i*rad);
					var y:Number=centerPoint.y+radii*Math.sin(initRad+i*rad);
					stateNode.location=new Point(x,y);
				}
				stateNode.image="icon_core_ipcore";
				//改变node标签颜色
				stateNode.setStyle(Styles.LABEL_COLOR,0xffffff);
				networkX.elementBox.add(stateNode);
				i++;
			}
			DataHandleTool.fileNameTypeDropDownList=new ArrayCollection(devices);
			DataHandleTool.stateNodesArr=nodeArray;
			//根据tnl创建link
			DataHandleTool.createLinkByTnlForTest(networkX);
		}
	}
}