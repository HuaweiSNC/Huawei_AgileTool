package com.huawei.sdnc.controller.ipCoreController
{
	import com.huawei.sdnc.tools.ConnUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	
	import flash.events.Event;
	import flash.utils.ByteArray;
	
	import org.httpclient.events.HttpDataEvent;
	import org.httpclient.events.HttpRequestEvent;
	import org.httpclient.events.HttpResponseEvent;

	public class FlowStatisDataProvide
	{
		public var opsIp:String=SdncUtil.opsIp;
		public function FlowStatisDataProvide()
		{
		}
		/*******************************************createRrd begin***********************************************/
		public function createRrd():void
		{
			var url:String = "http://"+opsIp+"/create/test.rrd";
			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_GET,onCreateResult,onCreatefault);
		}
		private function onCreateResult(e:HttpResponseEvent,data:ByteArray):void
		{
			
		}
		
		private function onCreatefault(e:Event):void
		{
			
		}
		/*******************************************createRrd end***********************************************/
		/*******************************************updateRrd  begin***********************************************/
		public function updateRrd(rrdName:String,body:String):void
		{
			var url:String = "http://"+opsIp+"/update/test.rrd";
			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_PUT,onUpdateResult,onUpdatefault,body);
		}
		private function onUpdateResult(e:HttpDataEvent):void
		{
			
		}
		private function onUpdatefault(e:Event):void
		{
			
		}
		/*******************************************updateRrd end***********************************************/
		/*******************************************fetchRrd begin***********************************************/
		public function fetchRrd(rrdName:String,startTime:String,endTime:String):void
		{
			var url:String = "http://10.111.78.91:8080/fetch/test.rrd/1386224710/1386224718";
			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_GET,onFetchResult,onFetchfault);
		}
		private function onFetchResult(e:HttpResponseEvent,data:ByteArray):void
		{
			var content:String=data.toString();
			content=content.replace(/\s+|Bandwidth/g,",").replace(/^,,,/,"").replace(/:,/g,":");
			var contentlist:Array = content.split(",");
			trace(content);
		}
		private function onFetchfault(e:Event):void
		{
			
		}
	}
}