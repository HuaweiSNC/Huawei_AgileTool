package com.huawei.overte.handle
{
	import com.huawei.overte.tools.ConnUtil;
	import com.huawei.overte.tools.SdncUtil;
	import com.huawei.overte.view.node.StateNode;
	
	import flash.events.Event;
	import flash.geom.Point;
	import flash.system.Capabilities;
	import flash.utils.ByteArray;
	
	import org.httpclient.events.HttpResponseEvent;
	
	import twaver.IData;
	import twaver.network.Network;
	import twaver.networkx.NetworkX;

	public class LayoutTopo
	{
		private var connUtil:ConnUtil = ConnUtil.getInstence();
		private var opsip:String = SdncUtil.opsIp;
		private var networkx:Network;
		private var centerPoint:Point=new Point(Capabilities.screenResolutionX/2,Capabilities.screenResolutionY/3);
		/**要分的数目*/
		private var num:int=0;
		/**角间隔*/
		private var rad:Number;
		/**初始角度*/
		private var initRad:Number;
		//半径
		private var radii:Number=200;
		private var deviceName:String="";
		private var ren:Function;
		public function LayoutTopo()
		{
		}
		
		public function handleLayout(networkX:Network,devicename:String,curarea:String):void
		{
			networkx=networkX;
			deviceName = devicename
			var opsIp:String=SdncUtil.opsIp;
			var webname:String = SdncUtil.projectname;
			var uri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"+curarea+"/topolocation";
			connUtil.clientQuery(uri,ConnUtil.METHOD_GET,onGetLocationResult,onFault);
		}
		private function onGetLocationResult(e:HttpResponseEvent,data:String):void
		{
			if(data.toString()!=""){
				var Xml:XML = XML(data.toString());
				for each(var position:XML in Xml.position){
					var devicename:String = position.devicename;
					var x:Number = Number(position.x);
					var y:Number = Number(position.y);
					var sn:StateNode = queryNodeByDeviceName(devicename);
					if(sn != null){
						var p:Point = new Point(x,y);
						sn.location=p;
					}else{
						selfDefinedLayout();
					}
					DataHandleTool.showOnConsole("Topo视图初始设备：  "+devicename);
				}
			}else{
				selfDefinedLayout();
			}
			
		}
		private function onFault(e:Event):void
		{
//			ren();
			selfDefinedLayout();
		}
		
		private function queryNodeByDeviceName(devicename:String):StateNode
		{
			var renNode:StateNode = null;
			var symbol:Boolean = true;
			networkx.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is StateNode&&symbol){
					var sn:StateNode = item as StateNode;
					var deviceName:String = sn.getClient("devicename");
					if(devicename == deviceName){
						renNode=sn;
						symbol=false;
					}
				}
			});
			return renNode;
		}
		/**
		 * 如果没有找到保存的布局信息，就自动布局
		 * 
		 */	
		var i:int = 0;
		private function selfDefinedLayout():void
		{
			networkx.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is StateNode){
					var stateNode:StateNode = item as StateNode;
					if(i==0){
						stateNode.location=new Point(centerPoint.x,centerPoint.y);
					}else{
						var x:Number=centerPoint.x+radii*Math.cos(initRad+i*rad);
						var y:Number=centerPoint.y+radii*Math.sin(initRad+i*rad);
						stateNode.location=new Point(x,y);
					}
					i++;
				}
			});
		}
	}
}