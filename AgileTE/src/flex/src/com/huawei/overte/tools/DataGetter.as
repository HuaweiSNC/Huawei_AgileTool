package com.huawei.overte.tools
{
	import com.huawei.overte.handle.DataHandleTool;
	import com.worlize.websocket.WebSocket;
	import com.worlize.websocket.WebSocketEvent;
	
	import flash.events.Event;
	import flash.events.TimerEvent;
	import flash.utils.ByteArray;
	import flash.utils.Timer;
	
	import org.httpclient.events.HttpResponseEvent;

	public class DataGetter
	{
		public function DataGetter()
		{
			
		}
		public var websocket:WebSocket;
		public var conn:ConnUtil = ConnUtil.getInstence();
		private var socketIp:String=SdncUtil.socketIp;
		/**向服务器申报webSocket所用通道*/
		public function postChanelToServer():void
		{
			conn.clientQuery(ConnUtil.protocolHeader+socketIp+"/channels/2","post",DataHandleTool.webSocketChanelCreateResultFunction,null, chanelErrorFunc);
		}
		private static var instance:DataGetter;
		public static function getInstance():DataGetter
		{
			if(instance == null)
				instance =new DataGetter();
			return instance;
		}
		public function chanelErrorFunc(event:Event):void
		{
			DataHandleTool.showOnConsole(event.type)
			trace(event.type);
		}
		
		/**建立webSocket连接并获取数据*/
		public  function webSocketAndGetAlarmData():void
		{
			DataHandleTool.showOnConsole("建立webSocket连接并获取数据")
			websocket = new WebSocket("ws://"+socketIp, "*");
			websocket.addEventListener(WebSocketEvent.OPEN, onWebSocketChange);
			websocket.addEventListener(WebSocketEvent.CLOSED, onWebSocketChange);
			websocket.addEventListener(WebSocketEvent.MESSAGE, DataHandleTool.handleWebSocketMessage);
			websocket.connect();
			var socketTimer:Timer = new Timer(60000);
			socketTimer.addEventListener(TimerEvent.TIMER, onSocketTimer);
			socketTimer.start();
		}
		protected function onSocketTimer(event:TimerEvent):void
		{
			DataHandleTool.showOnConsole("websocket.connect!")
			trace("websocket.connect!");
			websocket.connect();
		}
		
		
		protected function onWebSocketChange(event:WebSocketEvent):void
		{
			DataHandleTool.showOnConsole("WebSocket服务"+event.type)
			trace(event.type);
		}
		
	}
}