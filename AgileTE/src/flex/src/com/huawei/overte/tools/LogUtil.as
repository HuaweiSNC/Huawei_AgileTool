package com.huawei.overte.tools
{
import flash.utils.Dictionary;

public class LogUtil
{
	public static var instance:LogUtil;
	
	public var queryDic:Dictionary = new Dictionary;
	
	public static function getInstence():LogUtil
	{
		if(instance==null)
		{
			instance=new LogUtil();
		}
		return instance;
	}
	
	public function recordRequest(keyword:String,url:String,method:String):void
	{
		queryDic[keyword] = {};
		printLog(" request: " + keyword + "  method:" + method + "   url:" + url);
	}
	
	public function recordResult(keyword:String,result:String):void
	{
		queryDic[keyword] = result;
		printLog(" response: " + keyword + " is " + result);
	}
	
	public static function printLog(str:String):void
	{
		trace(str);
	}
	
	public function LogUtil()
	{
	}
}
}