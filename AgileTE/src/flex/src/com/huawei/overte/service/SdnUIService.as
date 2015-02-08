package com.huawei.overte.service
{
	import com.huawei.overte.tools.ConnUtil;
	import com.huawei.overte.tools.SdncUtil;
	import com.huawei.overte.view.node.StateNode;
	
	import flash.events.TimerEvent;
	import flash.utils.ByteArray;
	import flash.utils.Timer;
	
	import mx.rpc.events.FaultEvent;
	
	import org.httpclient.events.HttpDataEvent;
	import org.httpclient.events.HttpErrorEvent;
	import org.httpclient.events.HttpResponseEvent;

	public class SdnUIService
	{
		public var stateNode:StateNode;
		private var projectType:String;
		[Bindable]
		public var connUtil:ConnUtil = ConnUtil.getInstence();
		public function SdnUIService()
		{
			projectType = SdncUtil.cuProjectType;
		}
		
		/**
		 *处理URL请求(使用httpservice) 
		 * @param url URL地址
		 * @param onResult 返回结果
		 * @param onFault 错误处理
		 */
		private function onQuery(url:String,onResult:Function,onFault:Function):void
		{
			connUtil.query(url,onResult,onFault);
		}
		/**
		 * 处理URL请求(使用httpclient)
		 * @param uri:请求的地址
		 * @param onResult:结果处理函数
		 * @param onFault:错误处理函数
		 * @param method:操作方法（get,post,put,del）
		 * @param params:向服务器传递的参数
		 */	
		private function onClientQuery(uri:String,onResult:Function,onFault:Function,method:String = "get",params:String = null,contentType:String="application/json"):void
		{
			connUtil.clientQuery(uri,method,onResult,onFault,params,contentType);
		}
		
		/**
		 * 管理域 请求 
		 * @param uri
		 * @param onResult
		 * @param onFault
		 * @param method
		 * @param params
		 * 
		 */	
		public function ipcoreRequest(uri:String,onResult:Function,onFault:Function,method:String = "get",params:String = null):void
		{
			if(projectType=="test"){
				connUtil.query(uri,onResult,onFault);
			}else{
				connUtil.clientQuery(uri,method,onResult,onFault,params);
			}
			
		}
	}
}