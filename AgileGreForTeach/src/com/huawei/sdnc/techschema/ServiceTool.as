package com.huawei.sdnc.techschema
{
	import mx.collections.ArrayCollection;

	public class ServiceTool
	{
		public function ServiceTool()
		{
		}
		[Bindable]
		public static var body:String ;	//教学发送body内容
		[Bindable]
		public static var message:String ;	//教学返回message
		[Bindable]
		public static var url:String ;	//教学请求URL
		[Bindable]
		public static var schema:String="" ; //教学使用API
		[Bindable]
		public static var curtime:String ; //下发操作当前时间
		[Bindable]
		public static var usetime:String ; //下发花费时间
		[Bindable] 
		public static var code:String="" ; //下发成功失败返回码
		[Bindable]
		public static var protocolHeader:String ; //下发请求报头
		[Bindable]
		public static var Method:String ; //下发请求方法
		[Bindable]
		public static var handleArr:ArrayCollection ;
		
		[Bindable]
		public static var api:String="" ; //教学使用API
	
	}
		
}