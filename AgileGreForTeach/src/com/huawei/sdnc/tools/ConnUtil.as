package com.huawei.sdnc.tools
{
import com.adobe.net.URI;
import com.huawei.sdnc.techschema.ServiceTool;
import com.huawei.sdnc.view.gre.dataHandle.DataHandleTool;

import flash.events.Event;
import flash.events.SecurityErrorEvent;
import flash.utils.ByteArray;

import mx.controls.Alert;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.ResultEvent;
import mx.rpc.http.HTTPService;
import mx.rpc.remoting.RemoteObject;

import org.httpclient.HttpClient;
import org.httpclient.HttpRequest;
import org.httpclient.events.HttpDataEvent;
import org.httpclient.events.HttpDataListener;
import org.httpclient.events.HttpErrorEvent;
import org.httpclient.events.HttpListener;
import org.httpclient.events.HttpResponseEvent;
import org.httpclient.events.HttpStatusEvent;
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
	
	private var httpService:HTTPService;
	public static const METHOD_GET:String="GET";
	public static const METHOD_POST:String="POST";
//	public static const METHOD_HEAD:String="HEAD";
//	public static const METHOD_OPTIONS:String="OPTIONS";
	public static const METHOD_PUT:String="PUT";
//	public static const METHOD_TRACE:String="TRACE";
	public static const METHOD_DELETE:String="DELETE";
	/**从获取tokens返回的数据中得到computeUrl*/
	public var computeUrl:String;
	/**从获取tokens返回的数据中得到networkUrl*/
	public var networkUrl:String;
	/**serverUrl*/
	public var serverUrl:String = "/servers/detail";
	/**hostsUrl*/
	public var hostsUrl:String = "/os-hosts";
	/**devmUrl*/
	public var devmUrl:String = "/devm";
	/**opsURL*/
	public var opsUrl:String = "";
	/**添加network的URL后缀*/
	public var addNetWork:String = "v2.0/networks";
	
	/**租户ID*/
	public var tenantId:String;
	/**tokenID主要为发送请求添加头部*/
	public var tokentId:String;
	
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
	
	public function init(xml:XML):void
	{
		for each(var controller:XML in xml.controllers.controller)
		{
			SdncUtil.ctrlMap[controller.name] = controller;
		}
		if(SdncUtil.cuProjectType != "test")
		{
			var ip:String = xml.openstack.ip;
			var port:String = xml.openstack.port;
			var tokenURL:String = xml.openstack.tokenURL;
			var url:String = protocolHeader + ip + ":" + port + tokenURL ;//从XML获取地址
			var tenantName:String = xml.openstack.tenantName;
			var userName:String = xml.openstack.user;
			var password:String = xml.openstack.passwd;
			var param:String = "{\"auth\":{\"tenantName\":\""+tenantName+"\",\"passwordCredentials\":{\"username\":\""+userName+"\",\"password\":\""+password+"\"}}}";
			clientQuery(url,METHOD_POST,onTokenResult,getTokenError,param);//获取token id
		}
		for each(var cuDc:XML in xml.dcs.dc)
		{
			var cuDcName:String = cuDc.name;
			SdncUtil.dcNameArr.push(cuDcName);
			SdncUtil.dcMap[cuDcName] = cuDc;
			if(cuDc.hasOwnProperty("datafile"))
			{
				var datasuite:XMLList = cuDc.datafile.datasuite;
				opsUrl = String(datasuite.path);
				var topo12Url:String = opsUrl + String(datasuite.l2topo);//"http://10.135.32.108:8080/devices/95/sdn/sdnl2topo"
				var lldpUrl:String = opsUrl + String(datasuite.lldp);
				var vclusterUrl:String = opsUrl + String(datasuite.vcluster);
				var vlantopoUrl:String = opsUrl + String(datasuite.vlantopo);
				var vnmUrl:String = opsUrl + String(datasuite.vnm);
				var instanceUrl:String = opsUrl + String(datasuite.host);//真实工程只能从OpenStack上获取
				var ifmUrl:String = opsUrl + String(datasuite.ifm);
				var devmUrl:String = opsUrl + String(datasuite.devm);
				SdncUtil.dcUrlInfos[cuDc.name] = [topo12Url,lldpUrl,vclusterUrl,vlantopoUrl,vnmUrl,instanceUrl,ifmUrl,devmUrl];
				SdncUtil.dcHasDataArr.push(cuDcName);
			}
		}
	}
	
	private function onTokenResult(evt:HttpDataEvent):void
	{
		var obj:Object = JSON.parse(evt.bytes.toString()) as Object;//将json数据转换成object
		tenantId = obj.access.token.tenant.id;
		tokentId = obj.access.token.id;
		for each(var service:Object in obj.access.serviceCatalog)
		{
			if(service["type"] == "compute")
				computeUrl = service.endpoints[0].publicURL;
			else if(service["type"] == "network")
				networkUrl = service.endpoints[0].publicURL;
		}
	}
	
	private function getTokenError(evt:*):void
	{
		Alert.show("get token " + evt.text);
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
	
	/**
	 * 发送httpclient请求
	 * @param uriStr:请求地址
	 * @param method:请求方法
	 * @param resultFunc:响应函数
	 * @param faultFunc:错误处理函数
	 * @param params:传递的参数
	 */
	public var schemaType:String=""
	public function clientQuery(uriStr:String,method:String,resultFunc:Function,faultFunc:Function,params:* = null,contentType1:String="application/json",schemaType:String=""):void
	{
		this.schemaType="";
		DataHandleTool.showOnConsole(uriStr)
		var httpClient:HttpClient = new HttpClient();
		httpClient.timeout=180000;
		var uri:URI = new URI(uriStr);
		httpClient.listener.onData = resultFunc;
		httpClient.listener.onError = faultFunc;
		httpClient.listener.onStatus = onStatusresult
		if(schemaType!=""){
			//记录日志
			ServiceTool.body = params
			ServiceTool.url = uriStr
			ServiceTool.Method = method
			ServiceTool.schema = schemaType;
			this.schemaType = schemaType
		}
		if(params)
		{
			var jsonData:ByteArray = new ByteArray(); 
			jsonData.writeUTFBytes(params); 
			jsonData.position = 0;
			var contentType:String = contentType1; 
		}
		switch(method)
		{
			case "GET":
			{
				var httpListener:HttpDataListener = new HttpDataListener();
				httpListener.onDataComplete = resultFunc;
				httpListener.onError = faultFunc;
				httpClient.get(uri,httpListener);
				break;
			}
			case "POST":
				if(params) httpClient.post(uri,jsonData,contentType);
				break;
			case "PUT":
				if(params) httpClient.put(uri,jsonData,contentType);
				break;
			case "DELETE":
				httpClient.del(uri);
				break;
		}
		
		
		
		
	}
	
	public function onStatusresult(e:*):void{
		if(e is HttpStatusEvent&&this.schemaType!=""){
			ServiceTool.code = e.response.code
		}
			
	}
//	public function onData():void{
//		trace("=======")
//	}
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
		httpService.url="http://"+server+":"+port+resource;
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
//		httpService.clearResult();
		httpService.resultFormat = "e4x";
		httpService.addEventListener(ResultEvent.RESULT,_result);
		httpService.addEventListener(FaultEvent.FAULT,_faultHander);
		httpService.send();
	}
}
}
