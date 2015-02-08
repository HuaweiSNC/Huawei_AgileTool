package com.huawei.overte.tools
{
	import com.adobe.net.URI;
	import com.huawei.overte.handle.DataHandleTool;
	import com.laiyonghao.Uuid;
	
	import flash.events.Event;
	import flash.events.HTTPStatusEvent;
	import flash.events.IOErrorEvent;
	import flash.events.SecurityErrorEvent;
	import flash.events.TimerEvent;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	import flash.net.URLRequestHeader;
	import flash.net.URLRequestMethod;
	import flash.net.navigateToURL;
	import flash.utils.ByteArray;
	import flash.utils.Timer;
	
	import mx.controls.Alert;
	import mx.events.CloseEvent;
	import mx.managers.PopUpManager;
	import mx.resources.ResourceManager;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.http.HTTPService;
	import mx.rpc.remoting.RemoteObject;
	
	import org.httpclient.HttpClient;
	import org.httpclient.HttpRequest;
	import org.httpclient.HttpResponse;
	import org.httpclient.events.HttpDataEvent;
	import org.httpclient.events.HttpDataListener;
	import org.httpclient.events.HttpErrorEvent;
	import org.httpclient.events.HttpListener;
	import org.httpclient.events.HttpResponseEvent;
	import org.httpclient.http.Delete;
	import org.httpclient.http.Get;
	import org.httpclient.http.Post;
	import org.httpclient.http.Put;
	
	/**
	 * 此类主要用于向服务器发送请求获取数据。此类为单例模式，请使用getInstence方法获取单例
	 */
	public class ConnUtil
	{
		public static var endpoint:String="http://localhost:8080/SDNController/messagebroker/amf";
		/**协议头*/
		public static var protocolHeader:String = "http://";
		public static var url:String = "";
		private var httpService:HTTPService;
		public static const METHOD_GET:String="get";
		public static const METHOD_POST:String="post";
		public static const METHOD_PUT:String="put";
		public static const METHOD_DELETE:String="del";
		
		private static var instence:ConnUtil;
		public function ConnUtil()
		{
		}
		
		public static function getInstence():ConnUtil
		{
			if(instence==null)
			{
				instence = new ConnUtil();
			}
			return instence;
		}
		
		private function onError(evt:Event):void
		{
			
		}
		
		/**
		 * 发送请求
		 * @param url 请求的地址
		 * @param param 请求的参数
		 * @param resultFunc 结果处理函数
		 * @param token 迁移时需要的tokenid
		 */
		public function sendRequest(url:String,param:String,resultFunc:Function,errorFun:Function,token:String = null,method:String = METHOD_GET,isJson:Boolean = true):void
		{
			var params:String = param;
			var httpClient:HttpClient = new HttpClient();
			var uri:URI = new URI(url);
			httpClient.listener.onData = resultFunc;
			httpClient.listener.onError = errorFun;
			var contentType:String = isJson ? "application/json" : "application/xml";
			if(params)
			{
				var jsonData:ByteArray = new ByteArray(); 
				jsonData.writeUTFBytes(params); 
				jsonData.position = 0;
			}
			if(token != null)//获取到tokenid之后发送迁移请求时为请求添加表头
			{
				var request:HttpRequest;//声明一个post请求（这个HttpRequest也可以声明get put 请求,可以代替get post put 方法,相当于自定义请求）
				if(method == METHOD_GET) request = new Get();
				else if(method == METHOD_POST) request = new Post();
				else if(method == METHOD_PUT) request = new Put();
				else request = new Delete();
				
				if(jsonData) request.body = jsonData;
				request.addHeader("X-Auth-Token", token); //添加tokenid表头
				request.addHeader("Content-Type", contentType);
				request.addHeader("Accept", contentType);
				httpClient.request(uri,request); 
			}
			//		else
			//		{
			//			httpClient.post(uri,jsonData,contentType);
			//		}
		}
		
		/*public function queryInstances(url:String,resultSuccess:Function,error:Function):void
		{
		//onQuery(url,onResultInstances,onFault);//原方法
		var httpClient:HttpClient = new HttpClient();
		var uri:URI = new URI(url);
		var token:String = SdncUtil.tokentId;
		httpClient.listener.onData = resultSuccess;
		httpClient.listener.onError = error;
		
		var jsonData:ByteArray = new ByteArray(); 
		var request:HttpRequest = new Get();
		request.addHeader("X-Auth-Token", token); //添加tokenid表头
		request.addHeader("Content-Type", "application/json");
		request.addHeader("Accept", "application/json");
		httpClient.request(uri,request); 
		}*/
		
		public var state:int;
		public var result:String=""
		/**
		 * 发送httpclient请求
		 * @param uriStr:请求地址
		 * @param method:请求方法
		 * @param resultFunc:响应函数
		 * @param faultFunc:错误处理函数
		 * @param params:传递的参数
		 */
		private var alrt:Alert;
		private var alrtTimer:Timer;
		public function clientQuery(uriStr:String,method:String,resultFunc:Function,faultFunc:Function,params:* = null,contentType1:String="application/json"):void
		{
			trace(uriStr)
			var urlLoader:URLLoader = new URLLoader();
			var urlRequest:URLRequest = null;
			urlLoader.addEventListener(Event.COMPLETE,function getdeReslut(event:Event):void{
				result = event.target.data;
				var _response:HttpResponse = new HttpResponse("", state.toString(), "", null)
				var httpevent:HttpResponseEvent = new HttpResponseEvent(_response);
				if (resultFunc != null){
					if(result.search("javascript")==-1){
						if("get" == method){
							resultFunc(httpevent, result);
						}else{
							var byte:ByteArray = new ByteArray();
							byte.writeUTFBytes(result);
							byte.position = 0;
							var httpDataEvent:HttpDataEvent = new HttpDataEvent(byte);
							resultFunc(httpDataEvent);
						}
					}else{
						PopupManagerUtil.getInstence().closeLoading();
						alrtTimer = new Timer(60000, 1);
						alrtTimer.addEventListener(TimerEvent.TIMER_COMPLETE, function removeAlert(evt:TimerEvent):void {
							PopUpManager.removePopUp(alrt);
							var opsIp:String=SdncUtil.opsIp;
							var uri:String=ConnUtil.protocolHeader+opsIp+"/AgileSEM/logout?service="+ConnUtil.protocolHeader+opsIp+"/AgileSEM/login?service="+ConnUtil.protocolHeader+opsIp+"/AgileSys/view/main/index.jsp";
							var  request:URLRequest  =  new  URLRequest(uri);  
							navigateToURL(request,"_self");  
						});
						alrt = Alert.show("服务验证错误,请检查是否保持登陆状态", "提示", Alert.OK, null, function closeDelete(e:CloseEvent):void
						{
							alrtTimer.stop();
							var opsIp:String=SdncUtil.opsIp;
							var uri:String=ConnUtil.protocolHeader+opsIp+"/AgileSEM/logout?service="+ConnUtil.protocolHeader+opsIp+"/AgileSEM/login?service="+ConnUtil.protocolHeader+opsIp+"/AgileSys/view/main/index.jsp";
							var  request:URLRequest  =  new  URLRequest(uri);  
							navigateToURL(request,"_self");  
						});
						alrtTimer.reset();
						alrtTimer.start();
					
					}
					
				}
			});
			urlLoader.addEventListener(HTTPStatusEvent.HTTP_STATUS,function httpStatusHander(event:HTTPStatusEvent):void{
				state = event.status
				trace(state)
			});
			switch(method)
			{
				case "get":
				{
					if(uriStr.search("=") != -1){
						uriStr = uriStr+"&only="+new Uuid();
					}else{
						uriStr = uriStr+"?only="+new Uuid();
					}
					urlRequest = new URLRequest(uriStr);
					urlRequest.method = URLRequestMethod.GET;
					break;
				}
				case "post":
//					if(uriStr.search("=") != -1){
//						uriStr = uriStr+"&only="+new Uuid();
//					}else{
//						uriStr = uriStr+"?only="+new Uuid();
//					}
					urlRequest = new URLRequest(uriStr);
					if(params){
						urlRequest.data = params
					}else{
						urlRequest.data = method;
					}
					urlRequest.method = URLRequestMethod.POST;
					break;
				case "put":
					
					if(uriStr.search("=") != -1){
						uriStr = uriStr+"&methodType="+URLRequestMethod.PUT;
					}else{
						uriStr = uriStr+"?methodType="+URLRequestMethod.PUT;
					}
					urlRequest = new URLRequest(uriStr);
					if(params) {
						urlRequest.data = params;
					}else{
						urlRequest.data = method;
					}
					urlRequest.method = URLRequestMethod.POST;
					break;
				case "del":
					if(uriStr.search("=") != -1){
						uriStr = uriStr+"&methodType="+URLRequestMethod.DELETE;
					}else{
						uriStr = uriStr+"?methodType="+URLRequestMethod.DELETE;
					}
					urlRequest = new URLRequest(uriStr);
					urlRequest.data = method;
					urlRequest.method = URLRequestMethod.POST;
					//					var variables:URLVariables = new URLVariables();
					//					variables._method = "DELETE";
					//					urlRequest.data = variables;
					break;
			}
			
			urlRequest.contentType = contentType1;
			//		urlLoader.addEventListener(ProgressEvent.PROGRESS,progressHander);
			urlLoader.addEventListener(IOErrorEvent.IO_ERROR,function getdeReslut(event:Event):void{
				
				result = event.target.data;
				DataHandleTool.showOnConsole(event.toString()+"------------");
				var _response:HttpResponse = new HttpResponse("", state.toString(), "", null)
				var httpresponseevent:HttpResponseEvent = new HttpResponseEvent(_response);
				if (resultFunc != null){
					if("get" == method){
						resultFunc(httpresponseevent, result);
					}else{
						var iobyte:ByteArray = new ByteArray();
						iobyte.writeUTFBytes(result);
						iobyte.position = 0;
						var httpDataEvent:HttpDataEvent = new HttpDataEvent(iobyte);
						resultFunc(httpDataEvent);
					}
				}
			});
			urlLoader.addEventListener(SecurityErrorEvent.SECURITY_ERROR,faultFunc);
			var header:URLRequestHeader = new URLRequestHeader("pragma", "no-cache");
			urlRequest.requestHeaders.push(header)
			urlLoader.load(urlRequest);
		}
		
		public function getOpsData(url:String,method:String,content:String,result:Function=null):void
		{
			var remote:RemoteObject = new RemoteObject();
			//		remote.showBusyCursor = true;
			remote.destination = "XmlService"; 
			remote.endpoint = endpoint;
			remote.getOpsData(url,method,content);
			remote.addEventListener(ResultEvent.RESULT,result);
			remote.addEventListener(FaultEvent.FAULT,faults);
		}
		
		public function getOpenStackData(url:String,method:String,content:String,host:String,tokenid:String,result:Function=null):void
		{
			var remote:RemoteObject = new RemoteObject();
			//		remote.showBusyCursor = true;
			remote.destination = "XmlService"; 
			remote.endpoint = endpoint;
			remote.getOpenStackData(url,method,content,host,tokenid);
			remote.addEventListener(ResultEvent.RESULT,result);
			remote.addEventListener(FaultEvent.FAULT,faults);
		}
		
		private function faults(event:FaultEvent):void
		{
			Alert.show("error:"+event.message.toString());
		}
		
		public function sendHttpService(server:String,port:String,resource:String,resultFunc:Function, method:String = "GET"):void
		{
			httpService=new HTTPService();
			httpService.url=ConnUtil.protocolHeader+server+":"+port+resource;
			httpService.method=method;
			httpService.resultFormat="object";
			httpService.addEventListener(ResultEvent.RESULT,resultFunc);
			httpService.addEventListener(FaultEvent.FAULT,doFaultHandler);
			httpService.send();
		}
		
		protected function doFaultHandler(event:FaultEvent):void
		{
			var obj:Object=event.currentTarget;
			obj.removeEventListener(ResultEvent.RESULT,sendHttpService);
			obj.removeEventListener(FaultEvent.FAULT,doFaultHandler);
			//		Alert.show(event.response.statusCode + " (" + event.response.statusMessage + ")\n\n" + event.text);
			trace("连接错误");
		}	
		/**
		 * 发送HTTP请求
		 * @param _url:请求地址
		 * @param _result:响应函数
		 * @param faultHandler:错误处理函数
		 */
		public function query(_url:String,_result:Function,_faultHander:Function):void
		{
			httpService=new HTTPService();
			httpService.url = _url;
			httpService.resultFormat = "e4x";
			httpService.addEventListener(ResultEvent.RESULT,_result);
			httpService.addEventListener(FaultEvent.FAULT,_faultHander);
			httpService.send();
		}
		
		
		public function clientAlarm(uriStr:String,method:String,resultFunc:Function,faultFunc:Function,contentType1:String="application/json"):void
		{
			var httpClient:HttpClient = new HttpClient();
			httpClient.timeout=60000;
			var uri:URI = new URI(uriStr);
			httpClient.listener.onData = resultFunc;
			httpClient.listener.onError = faultFunc;
			//		if(params)
			//		{
			//			var jsonData:ByteArray = new ByteArray(); 
			//			jsonData.writeUTFBytes(params); 
			//			jsonData.position = 0;
			//			var contentType:String = contentType1; 
			//		}
			switch(method)
			{
				case "get":
				{
					var httpListener:HttpDataListener = new HttpDataListener();
					httpListener.onDataComplete = resultFunc;
					httpListener.onError = faultFunc;
					httpClient.get(uri,httpListener);
					break;
				}
				case "post":
					httpClient.post(uri,"",contentType1);
					break;
			}
			
		}
	}
}
