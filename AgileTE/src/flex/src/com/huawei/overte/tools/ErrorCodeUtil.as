package com.huawei.overte.tools
{
	import com.huawei.overte.model.Device;
	import com.huawei.overte.view.node.StateNode;
	
	import flash.utils.ByteArray;
	
	import org.httpclient.events.HttpResponseEvent;

	public class ErrorCodeUtil
	{
		
		public static var FAILED_OPEN_SOCKET:int = 500;
		public static var FAILED_RPC_XML:int = 501;
		public static var FAILED_OTHER:int = 502;
		public static var SUCCESS_ACTION:int = 200;
		private var errMsg:String = "";
		public var errcode:int = SUCCESS_ACTION;
		
		public function ErrorCodeUtil()
		{
		}
		//先判断错误码
		public function parse(e:HttpResponseEvent, data:String):Boolean{
			
			if(e.response.code == "200"){
				errcode = SUCCESS_ACTION;
				return true;
			}
			//				if(tmpvalue.length>9 && tmpvalue.substr(0,9) == "<rpc-error" ){
			//					errMsg = "Errorcode:"+FAILED_RPC_XML +" \n  "+tmpvalue;
			//					errcode = FAILED_RPC_XML;
			//					return errcode;
			//				} 
			

			var tmpvalue:String = data.toString();
			if(e.response.code == "500"){
			     errMsg = "Errorcode:"+FAILED_OPEN_SOCKET +" , "+tmpvalue;
				 errcode = FAILED_OPEN_SOCKET;
				 return false;
			}

			if(e.response.code != "200"){
				errcode = FAILED_OTHER;
				errMsg = "Errorcode:"+FAILED_OTHER+"  \n  "+tmpvalue;
			}
			return false;
		}
 		//返回错误码
		public function printErrorMessage(node:com.huawei.overte.view.node.StateNode, addErrMsg:String):String
		{
			var devicename:String = node.getClient("devicename");	
			var message:String = "设备:"+devicename+"   "+errMsg+ addErrMsg;
		    trace(message);
		    return message;
		}
		
		
		
	}
}