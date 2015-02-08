package com.huawei.overte.model
{
	import com.huawei.overte.handle.DataHandleTool;
	import com.huawei.overte.tools.ConnUtil;
	import com.huawei.overte.tools.ErrorCodeUtil;
	import com.huawei.overte.tools.SdncUtil;
	import com.huawei.overte.tools.ifm.IfDynamicInfo;
	import com.huawei.overte.tools.ifm.IfStatistics;
	import com.huawei.overte.tools.ifm.Interface;
	import com.huawei.overte.tools.ifm.ifmAm4.IfmAm4;
	import com.huawei.overte.tools.ifm.ifmAm4.am4CfgAddr.Am4CfgAddr;
	
	import flash.events.TimerEvent;
	import flash.utils.ByteArray;
	import flash.utils.Timer;
	
	import org.httpclient.events.HttpErrorEvent;
	import org.httpclient.events.HttpResponseEvent;
	
	public class Ifm extends MetaData
	{
		public function Ifm()
		{
			
		}
		public var interfaces:Array;
		private var reFun:Function;
		public var device:Device;
//		public static var timer:Timer = new Timer(3000);
		
		public function getIfm(ifmIsReady:Function):void
		{
			reFun=ifmIsReady;
			var opsIp:String=SdncUtil.opsIp;
			var id:String=stateNode.getClient("id");
			device = stateNode.getClient("device");
			var aclUrl:String=ConnUtil.protocolHeader+opsIp+"/devices/"+id+"/ifm/interfaces/interface/ifmAm4/json";
			connUtil.clientQuery(aclUrl,ConnUtil.METHOD_GET,onGetResult,onGetFault);
			
//			timer.addEventListener(TimerEvent.TIMER,function(event:TimerEvent):void{
//			});
			//timer.start();
		}
		
		private function onGetResult(e:HttpResponseEvent,data:String):void
		{
			var errorcode:ErrorCodeUtil = new ErrorCodeUtil;
			if(!errorcode.parse(e,data)){
				device.deviceState = false;
				reFun();
				return;
			}
			try{
				  if(data.toString() == null||data.toString() == ""){
					  var devicename:String = stateNode.getClient("devicename");
					  trace("设备:"+devicename+"    Ifm没有数据");
					  reFun();
					  return;
				  }
				   interfaces=[];
					var jsonObj:Object = JSON.parse(data.toString());
					var receive:Array=[];
					var send:Array=[];
					var ifOperSpeed:String;
					var ifmArray:Array = jsonObj["ifm"];
					var interfacesObj:Object = ifmArray[0];
					var interfaceArr:Array = interfacesObj["interfaces"];//取到所有interface的数组
					for each(var o:Object in interfaceArr){
						var interArr:Array = o["interface"];//interface本身是一个数组
						var interfaceClass:Interface = new Interface();//实例化interface类
						for each(var o1:Object in interArr){
							if(o1.hasOwnProperty("ifName")){
								var ifname:String = o1["ifName"];
								interfaceClass.ifName = ifname;
							}
							
							//ifPhyType
							if(o1.hasOwnProperty("ifPhyType")){
								var ifPhyType:String = o1["ifPhyType"];
								interfaceClass.ifPhyType = ifPhyType;//interface加ifPhyType属性
							}
							//ifDynamicInfo
							if(o1.hasOwnProperty("ifDynamicInfo")){
								var ifDynamicInfo:Array = o1["ifDynamicInfo"];//ifDynamicInfo下面还有属性
								var ifDynamicInfoClass:IfDynamicInfo = new IfDynamicInfo();
								for each(var o2:Object in ifDynamicInfo){
									if(o2.hasOwnProperty("ifDynamicInfo")){
										ifOperSpeed = o2["ifDynamicInfo"]; 
										ifDynamicInfoClass.ifOperSpeed = ifOperSpeed;
									}
								}
								interfaceClass.ifDynamicInfo = ifDynamicInfoClass;//interface加ifDynamicInfo属性
							}
							//ifStatistics
							if(o1.hasOwnProperty("ifStatistics")){
								var ifStatistics:Array = o1["ifStatistics"];
								var ifStatisticsClass:IfStatistics = new IfStatistics();
								for each(var o3:Object in ifStatistics){
									if(o3.hasOwnProperty("receiveByte")){
										var receiveByte:String = o3["receiveByte"];
										ifStatisticsClass.receiveByte = receiveByte;
										receive.push(receiveByte);
									}
									if(o3.hasOwnProperty("sendByte")){
										var sendByte:String = o3["sendByte"];
										ifStatisticsClass.sendByte = sendByte;
										send.push(sendByte);
									}
								}
								interfaceClass.ifStatistics = ifStatisticsClass;
							}
							
							
							//ifmAm4
							if(o1.hasOwnProperty("ifmAm4")){
								var ifmAm4:Array = o1["ifmAm4"];
								var ifmAm4Class:IfmAm4 = new IfmAm4();
								for each(var o4:Object in ifmAm4){
									if(o4.hasOwnProperty("am4CfgAddrs")){
										var am4CfgAddrs:Array = o4["am4CfgAddrs"];
										var am4CfgAddrs1:Array=[];
										for each(var o5:Object in am4CfgAddrs){
											if(o5.hasOwnProperty("am4CfgAddr")){
												var am4CfgAddr:Array = o5["am4CfgAddr"];
												var am4CfgAddrClass:Am4CfgAddr = new Am4CfgAddr();
												for each(var o6:Object in am4CfgAddr){
													if(o6.hasOwnProperty("ifIpAddr")){
														var ifIpAddr:String = o6["ifIpAddr"];
														am4CfgAddrClass.ifIpAddr = ifIpAddr;
													}
													if(o6.hasOwnProperty("subnetMask")){
														var subnetMask:String = o6["subnetMask"];
														am4CfgAddrClass.subnetMask = subnetMask;
													}
												}
											}
											am4CfgAddrs1.push(am4CfgAddrClass);
										}
										ifmAm4Class.am4CfgAddrs = am4CfgAddrs1;
									}
								}
								interfaceClass.ifmAm4 = ifmAm4Class;
							}
							interfaces.push(interfaceClass);
							
						}
					}
					var ip:String=stateNode.getClient("ip");
					trace("设备:"+ip+"    getIfm成功");
//					DataHandleTool.showOnConsole("设备"+ip+"   getIfm成功\n");
				if(reFun!=null){
					reFun();
				}
				
				
			}catch(e:*){
				device.deviceState = false;
				if(reFun!=null){
					reFun();
				}
			}
			
		}
		
		private function onGetFault(e:HttpErrorEvent):void
		{
			var ip:String=stateNode.getClient("ip");
			device.deviceState = false;
//			trace("设备:"+ip+"    getIfm出错");
//			if(DataHandleTool.console!=null){
//				var content:String="设备"+ip+"   getIfm出错\n";
//				DataHandleTool.console.console.text+=content;
//			}
			reFun();
		}
		/**
		 * 计算ifm即流量监控%
		 * */
		
		public static function ifmJSResult(receive:Array,send:Array,ifOperSpeed:String):Number
		{
			var ifmResult:Number;
//			var time:Number = timer.delay;
//			var Speed:Number = Number(ifOperSpeed);
//			for (var j:int=0; j<receive.length; j++){
//				var deltReceive:Number = Number(receive[i])-Number(receive[i-1]);
//				var deltSend:Number = Number(send[i])-Number(send[i-1]);
//				ifmResult = (deltReceive+deltSend)/(ifOperSpeed*time*8);
//			}
			return ifmResult;
		}
	}
}