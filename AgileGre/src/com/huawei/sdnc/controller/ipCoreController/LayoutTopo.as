package com.huawei.sdnc.controller.ipCoreController
{
	import com.huawei.sdnc.netmanage.ToolUtil;
	import com.huawei.sdnc.tools.ConnUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.common.node.StateNode;
	
	import flash.events.Event;
	import flash.geom.Point;
	import flash.system.Capabilities;
	import flash.utils.ByteArray;
	
	import org.httpclient.events.HttpResponseEvent;
	
	import twaver.IData;
	import twaver.networkx.NetworkX;

	public class LayoutTopo
	{
		private var connUtil:ConnUtil = ConnUtil.getInstence();
		private var opsip:String = SdncUtil.opsIp;
		private var networkx:NetworkX;
		private var centerPoint:Point=new Point(Capabilities.screenResolutionX/2,Capabilities.screenResolutionY/3);
		/**要分的数目*/
		private var num:int=0;
		/**角间隔*/
		private var rad:Number;
		/**初始角度*/
		private var initRad:Number;
		//半径
		private var radii:Number=200;
		
		private var ren:Function;
		public function LayoutTopo()
		{
		}
		
		public function handleLayout(networkX:NetworkX,r:Function):void
		{
			networkx=networkX;
			ren=r;
			num = ToolUtil.getNodeNumber(networkx)-1;
			if(num == 0){
				rad=2*Math.PI;
				initRad=Math.PI/2;
			}else{
				rad=2*Math.PI/num;
				initRad=Math.PI/2+rad/2;
			}
			selfDefinedLayout();
			var url:String = "http://"+opsip+"/database/agilegre?username=admin&Infoname=layer";
			        //http://10.111.78.91:8080/database/agilegre?username=admin&Infoname=layer
			connUtil.clientQuery(url,ConnUtil.METHOD_GET,onResult,onFault);
		}
		private function onResult(e:HttpResponseEvent,data:ByteArray):void
		{
			if(data.toString()!=""){
				var layoutXml:XML = XML(data.toString());
				for each(var position:XML in layoutXml.position){
					var devicename:String = position.devicename;
					var x:Number = Number(position.x);
					var y:Number = Number(position.y);
					var sn:StateNode = queryNodeByDeviceName(devicename);
					if(sn != null){
						var p:Point = new Point(x,y);
						sn.location=p;
					}
				}
			}else{
				selfDefinedLayout();
			}
			ren();
			
		}
		private function onFault(e:Event):void
		{
			ren();
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
		private function selfDefinedLayout():void
		{
			var i:int = 0;
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