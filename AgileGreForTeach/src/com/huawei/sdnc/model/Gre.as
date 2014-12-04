package com.huawei.sdnc.model
{
	import com.huawei.sdnc.tools.ConnUtil;
	import com.huawei.sdnc.tools.ErrorCodeUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.gre.dataHandle.DataHandleTool;
	
	import flash.utils.ByteArray;
	
	import org.httpclient.events.HttpErrorEvent;
	import org.httpclient.events.HttpResponseEvent;
	
	import twaver.Styles;

	public class Gre extends MetaData
	{
		public var greTunnels:Array;
		private var reFun:Function;
		public var device:Device;
		public function Gre()
		{
		}
		
		public function getGre(greIsReady:Function):void
		{
			reFun=greIsReady;
			var opsIp:String=SdncUtil.serviceIp;
			var id:String=stateNode.getClient("id");
			device = stateNode.getClient("device");
//			var greUrl:String="http://"+opsIp+"/devices/"+id+"/gre";
//			connUtil.clientQuery(greUrl,ConnUtil.METHOD_GET,onGetResult,onGetFault);
			var greUrl:String="http://"+opsIp+"/AgileGre/rest/devices/"+id+"/utunnel";
			connUtil.clientQuery(greUrl,ConnUtil.METHOD_GET,onGetResult,onGetFault);
		}
		
		public function onGetResult(e:HttpResponseEvent,data:ByteArray):void
		{
			var errorcode:ErrorCodeUtil = new ErrorCodeUtil;
			if(!errorcode.parse(e,data)){
				device.deviceState = false;
				DataHandleTool.showOnConsole(errorcode.printErrorMessage(stateNode, "   getGre失败\n"+data.toString()));
				reFun();
				return;
			}
			try{
				greTunnels=[];
				var ip:String=stateNode.getClient("ip");
				var gre:XML=XML(data.toString());
//				var index:int=data.toString().indexOf("<greTunnels>");
//				if(index!=-1){
//					var result:String="<gre> "+data.toString().substr(index);
//					gre=XML(result);
//					for each(var greTunnel:XML in gre.greTunnels.greTunnel){
//						var tnlName:String=greTunnel.tnlName;
//						var tnlType:String=greTunnel.tnlType;
//						var srcType:String=greTunnel.srcType;
//						var srcIpAddr:String=greTunnel.srcIpAddr;
//						var srcIfName:String=greTunnel.srcIfName;
//						var dstIpAddr:String=greTunnel.dstIpAddr;
//						var dstVpnName:String=greTunnel.dstVpnName;
//						
//						var greTunnelClass:GreTunnel=new GreTunnel;
//						greTunnelClass.tnlName=tnlName;
//						greTunnelClass.tnlType=tnlType;
//						greTunnelClass.srcType=srcType;
//						greTunnelClass.srcIpAddr=srcIpAddr;
//						greTunnelClass.srcIfName=srcIfName;
//						greTunnelClass.dstIpAddr=dstIpAddr;
//						greTunnelClass.dstVpnName=dstVpnName;
//						
//						greTunnels.push(greTunnelClass);
//					}
				if(e.response.code=="200"){
					for each(var greTunnel:XML in gre.tunnels.tunnel){
						var tnlName:String=greTunnel.tunnelName;
						var tnlType:String=greTunnel.tunnelType;
						var srcType:String=greTunnel.sourceTpType;
						var srcIpAddr:String=greTunnel.sourceNodeId;
						var srcIfName:String=greTunnel.srcIfName;
						var dstIpAddr:String=greTunnel.destinationNodeId;
						var dstVpnName:String="_public_";
						
						var greTunnelClass:GreTunnel=new GreTunnel;
						greTunnelClass.tnlName=tnlName;
						greTunnelClass.tnlType=tnlType;
						greTunnelClass.srcType=srcType;
						greTunnelClass.srcIpAddr=srcIpAddr;
						greTunnelClass.srcIfName=srcIfName;
						greTunnelClass.dstIpAddr=dstIpAddr;
						greTunnelClass.dstVpnName=dstVpnName;
						
						greTunnels.push(greTunnelClass);
					}
				trace("设备"+ip+"   getGre成功");
				var content:String="设备"+ip+"   getGre成功\n";
				DataHandleTool.showOnConsole(content);
				}else if(data.toString()==""){
					DataHandleTool.showOnConsole("设备"+ip+"   Gre无数据\n");
					trace("设备"+ip+"   Grel没有数据");
				}else{
					trace("设备"+ip+"   getGre失败");
					DataHandleTool.showOnConsole("设备"+ip+"   getGre失败\n"+data.toString());
					device.deviceState = false;
				}
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
		private function onGetFault(e:*):void
		{
			var ip:String=stateNode.getClient("ip");
			device.deviceState = false;
			trace("设备"+ip+"   getGre失败");
			DataHandleTool.showOnConsole("设备"+ip+"   getGre失败\n"+e.toString()	);
			if(reFun!=null){
				reFun();
			}
		}
	}
}