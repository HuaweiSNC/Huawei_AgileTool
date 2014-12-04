package com.huawei.sdnc.controller
{
	import com.huawei.sdnc.tools.ConnUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.common.node.StateNode;
	
	import flash.events.Event;
	
	import mx.controls.Alert;
	
	import org.httpclient.events.HttpDataEvent;
	
	import twaver.IData;
	import twaver.networkx.NetworkX;
	import twaver.networkx.interaction.InteractionEvent;

	public class SaveTopoLocation
	{
		private var connUtil:ConnUtil = ConnUtil.getInstence();
		public function SaveTopoLocation()
		{
		}
		
		public function initMoveListener(networkx:NetworkX):void
		{
			networkx.addInteractionListener(function(e:InteractionEvent):void{
				if(e.kind == InteractionEvent.LIVE_MOVE_END||e.kind == InteractionEvent.LAZY_MOVE_END){
					trace("e.kind"+":"+InteractionEvent.LIVE_MOVE_END);
					saveLocation(networkx);
				}
				
			});
		}
		
		public function saveLocation(networkx:NetworkX):void
		{
			var body:String="<positions>";
			networkx.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is StateNode){
					var sn:StateNode = item as StateNode;
					var x:Number = sn.centerLocation.x;
					var y:Number = sn.centerLocation.y;
					var devicename:String = sn.getClient("devicename");
					var positionstr:String = 
					"<position>" 
				   +"<devicename>"+devicename+"</devicename>"
				   +"<x>"+x+"</x>"
				   +"<y>"+y+"</y>"
				   +"</position>";
					body+=positionstr;
				}
			});
			body+="</positions>";
			var opsip:String = SdncUtil.opsIp;
			var url:String = "http://"+opsip+"/database/agilegre?username=admin&Infoname=layer&&datatype=data";
			connUtil.clientQuery(url,ConnUtil.METHOD_PUT,onSaveResult,onSaveFault,body,"application/xml");
		}
		
		private function onSaveResult(e:HttpDataEvent):void
		{
			var result:String = e.bytes.toString();
			Alert.show("保存成功","提示",Alert.OK,SdncUtil.app.ipcore.physicsView);
			trace(result);
		}
		
		private function onSaveFault(e:Event):void
		{
			Alert.show("保存失败,请重试","提示",Alert.OK,SdncUtil.app.ipcore.physicsView);
		}
	}
}