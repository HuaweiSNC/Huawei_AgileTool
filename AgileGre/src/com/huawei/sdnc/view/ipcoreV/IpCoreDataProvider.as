/*
jixiaofeng
*/

package com.huawei.sdnc.view.ipcoreV
{
	import com.huawei.sdnc.event.SdncEvt;
	import com.huawei.sdnc.tools.ConnUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.ipcoreV.IpCoreSdncEvt;
	
	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;
	
	import mx.rpc.events.ResultEvent;
	
	public class IpCoreDataProvider extends EventDispatcher
	{
		private var __ipcoretopoXml:XML;
		
		public function IpCoreDataProvider(target:IEventDispatcher=null)
		{
			super(target);
		}
		
		public function IpcoretopoInfoQuery(url:String):void
		{
			ConnUtil.getInstence().query(url,onResultIpcoretopo,onFault);
			
		}
		
		private function onResultIpcoretopo(e:ResultEvent):void
		{
			var url:String = e.currentTarget.url;
			_ipcoretopoXml = e.result as XML
		}
	
		public function get _ipcoretopoXml():XML
		{
			return __ipcoretopoXml;
		}
		
		public function set _ipcoretopoXml(value:XML):void
		{
			__ipcoretopoXml = value;
			dispatcherResultEvent(IpCoreSdncEvt.RESULT_IPCORE,__ipcoretopoXml);
		}
		
		private function dispatcherResultEvent(type:String,xml:XML):void
		{
			SdncUtil.app.dispatchEvent(new IpCoreSdncEvt(type,xml));
		}
		
		private function onFault(e:Event):void
		{
			trace(e.currentTarget.url,"请求失败!");
		}
		
	}
}
