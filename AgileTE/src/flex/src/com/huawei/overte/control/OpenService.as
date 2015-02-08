package com.huawei.overte.control
{
	import com.huawei.overte.handle.AgileteRefresh;
	import com.huawei.overte.handle.DataHandleTool;
	import com.huawei.overte.model.Data;
	import com.huawei.overte.model.Device;
	import com.huawei.overte.tools.ConnUtil;
	import com.huawei.overte.tools.SdncUtil;
	import com.huawei.overte.view.overte.OverTEView;
	import com.huawei.overte.view.overte.OverTeNavPanel;
	import com.worlize.websocket.WebSocket;
	import com.worlize.websocket.WebSocketEvent;
	
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.events.FlexEvent;
	import mx.managers.PopUpManager;
	import mx.resources.IResourceManager;
	import mx.resources.ResourceManager;
	
	import org.httpclient.events.HttpDataEvent;
	import org.httpclient.events.HttpResponseEvent;

	public class OpenService
	{
		/**当前主应用程序**/
		//[Bindable]private var __app:overTegui2;
		/**管道流状态监测界面 对象**/
		[Bindable]public var page:OverTEView;  
		private var navPancel:OverTeNavPanel;
		private var flags:Boolean = false;
		/**连接java单例**/
		public var connUtil:ConnUtil = ConnUtil.getInstence();
		/**WebSocket服务**/
		public var websocket:WebSocket;
		private var resourceManager:IResourceManager = ResourceManager.getInstance();
		
		//private var refracetimer:Timer = new Timer(Number(resourceManager.getString('refresh','refresh.long')));
		//private var refracecheck:Timer = new Timer(Number(resourceManager.getString('refresh','refresh.short')));
		private var refracetimer:Timer = new Timer(Number(SdncUtil.refreshlong));
		private var refracecheck:Timer = new Timer(Number(SdncUtil.refreshshort));
		
		/**告警管理页面初始化 cwx200285**/
//		protected function init(event:FlexEvent):void{
//			/**开启告警服务**/
//			if(SdncUtil.openalermflag){
//				realForStartService();
//			}
//		}
		/**开启告警服务 cwx200285**/
		/**public function realForStartService():void{
			var alermstartip:String = SdncUtil.alermstartip;
			var uri:String=ConnUtil.protocolHeader+alermstartip+"/AgileFM/agilete/alarm"; //开启/发布服务
			DataHandleTool.showOnConsole("开启告警服务：\n"+uri);
			if(SdncUtil.openalermflag){
				SdncUtil.openalermflag = false;
				connUtil.clientQuery(uri,ConnUtil.METHOD_POST,onResult,onFault);
			}
			
		}*/
		
		//直接链接websocket
		public function realForStartService():void{
			if(SdncUtil.openalermflag){
				SdncUtil.openalermflag = false;
				webSocketAndGetAlarmData();
			}
				
		}
		
//		/**开启告警服务连接成功 cwx200285**/
//		private function onResult(e:HttpDataEvent):void{
//			Alert.okLabel = "确定";
//			if(e.bytes.toString().search("ok")!=-1){
//				DataHandleTool.showOnConsole("path接口告警服务开启成功");
//				/**建立webSocket连接并获取数据*/
//				webSocketAndGetAlarmData();
//			}else{
//				Alert.show("告警服务开启失败","提示");
//				SdncUtil.openalermflag = true;
//				DataHandleTool.showOnConsole("告警服务开启失败！错误信息："+e.bytes.toString());
//			}
//		}
		
//		/**开启告警服务连接失败 cwx200285**/
//		private function onFault(e:Event):void{
//			SdncUtil.openalermflag = true;
//			DataHandleTool.showOnConsole("告警服务开启连接失败！错误信息："+e.toString());
//		}
		/**建立webSocket连接并获取告警数据 cwx200285**/
		public function webSocketAndGetAlarmData():void   {
			var alermconnectip:String = SdncUtil.alermconnectip
			websocket = new WebSocket("ws://"+alermconnectip+"/alarm/other", "*");//连接/监听服务
			trace("********"+alermconnectip);
			websocket.addEventListener(WebSocketEvent.OPEN, onWebSocketChange);
			websocket.addEventListener(WebSocketEvent.CLOSED, onWebSocketChange);
			websocket.addEventListener(WebSocketEvent.MESSAGE,handleWebSocketMessage);
			websocket.connect();
			var socketTimer:Timer = new Timer(60000);
			socketTimer.addEventListener(TimerEvent.TIMER, onSocketTimer);
			socketTimer.start();
		}
		
		protected function onWebSocketChange(event:WebSocketEvent):void{
			DataHandleTool.showOnConsole("websocket创建："+event.type);
		}
		
		protected function onSocketTimer(event:TimerEvent):void{
			DataHandleTool.showOnConsole("alarm websocket.connect!");
			websocket.sendUTF("ok");
			websocket.connect();
		}
		
		
		private var refreshTopo:AgileteRefresh;
		public  function handleWebSocketMessage(event:WebSocketEvent):void{
			var alarmmessage:String = event.message.utf8Data;
			if("ok"!=alarmmessage){
				DataHandleTool.showOnConsole(new Date().time+"收到告警执行刷新");
				flags = true;
			}
		}
		
		
		public  function refracecheck2(event:TimerEvent):void{
			DataHandleTool.showOnConsole("================"+flags)	
				if(flags){
					trace("执行拓扑刷新*****************"+SdncUtil.refreshshort);
					DataHandleTool.showOnConsole(new Date() +"收到后台设备接口告警推送执行刷新"+SdncUtil.refreshshort);
					//刷新拓扑
					refreshTopo = new AgileteRefresh();
					Device.j = 1
					refreshTopo.regetData();
					//刷新左侧列表
					page = (SdncUtil.app.overte.topoview.selectedChild as OverTEView);
					if(page!=null){
						navPancel=page.navPanel;
						navPancel.openservicerefresh();
					}
				}
				flags = false;
		}
		public  function refrace(event:TimerEvent):void{
				trace("到30分钟执行拓扑刷新");
				DataHandleTool.showOnConsole(new Date() +"到30分钟自动执行刷新"+SdncUtil.refreshlong);
				//刷新拓扑
				refreshTopo = new AgileteRefresh();
				Device.j = 1
				refreshTopo.regetData();
				flags = false;
//				refracecheck.reset();
				
		}
		
		
		
		
		public function OpenService()
		{
			refracetimer.addEventListener(TimerEvent.TIMER,refrace);
			refracetimer.start();
			
			refracecheck.addEventListener(TimerEvent.TIMER,refracecheck2);
			refracecheck.start();
		}
	}
}