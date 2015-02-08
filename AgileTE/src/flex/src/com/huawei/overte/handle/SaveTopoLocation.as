package com.huawei.overte.handle
{
	import com.huawei.overte.tools.ConnUtil;
	import com.huawei.overte.tools.SdncUtil;
	import com.huawei.overte.view.node.StateNode;
	import com.huawei.overte.view.overte.OverTEView;
	
	import flash.events.Event;
	
	import flashx.textLayout.elements.OverflowPolicy;
	
	import mx.controls.Alert;
	import mx.resources.IResourceManager;
	import mx.resources.ResourceBundle;
	import mx.resources.ResourceManager;
	
	import org.httpclient.events.HttpDataEvent;
	
	import twaver.IData;
	import twaver.network.Network;
	import twaver.network.interaction.InteractionEvent;

	public class SaveTopoLocation
	{
		private var connUtil:ConnUtil = ConnUtil.getInstence();
		public var cuArea:String="";
		public var _app:overTegui2;
		private var resourceManager:IResourceManager =ResourceManager.getInstance()
		public function SaveTopoLocation()
		{
			_app = SdncUtil.app;
			cuArea = (_app.overte.topoview.selectedChild as OverTEView).ManAreasID
		}
		
		public function initMoveListener(networkx:Network):void
		{
			networkx.addInteractionListener(function(e:InteractionEvent):void{
				if(e.kind == InteractionEvent.LIVE_MOVE_END||e.kind == InteractionEvent.LAZY_MOVE_END){
					trace("e.kind"+":"+InteractionEvent.LIVE_MOVE_END);
					saveLocation(cuArea,networkx);
				}
			});
		}
		
		public function saveLocation(cuArea:String,networkx:Network):void
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
			var opsIp:String=SdncUtil.opsIp;
			var webname:String = SdncUtil.projectname;
			var uri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"+cuArea+"/topolocation";
			connUtil.clientQuery(uri,ConnUtil.METHOD_PUT,onSaveResult,onSaveFault,body,"application/xml");
		}
		
		private function onSaveResult(e:HttpDataEvent):void
		{
			var result:String = e.bytes.toString();
			if(result.search("ok")!=-1){
				/**"保存成功","提示"**/
				Alert.show(resourceManager.getString('global','all.savesuccess')
					,resourceManager.getString('global','all.prompt'));
			}else{
				/**"保存失败"+result,"提示"**/
				Alert.show(resourceManager.getString('global','all.savefail')
					,resourceManager.getString('global','all.prompt'));
			}
			trace(result);
		}
		
		private function onSaveFault(e:Event):void
		{
			/**"保存Topo连接出错","提示"**/
			Alert.show(resourceManager.getString('global','all.saveerror')
				,resourceManager.getString('global','all.prompt'));
		}
	}
}