package com.huawei.sdnc.model
{
	import com.huawei.sdnc.tools.ConnUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.common.node.StateNode;
	import com.huawei.sdnc.view.gre.MyLink;
	
	import flash.events.Event;
	import flash.events.SecurityErrorEvent;
	import flash.events.TimerEvent;
	import flash.utils.ByteArray;
	import flash.utils.Timer;
	
	import org.httpclient.events.HttpDataEvent;
	import org.httpclient.events.HttpErrorEvent;
	import org.httpclient.events.HttpResponseEvent;

	public class Nqa extends MetaData
	{
		public function Nqa()
		{
		}
		public var averageRtt:String;
		private var reFun:Function;
		public var opsIp:String=SdncUtil.opsIp;
		public var id:String= "";
		public var destAddr:String;
		public var srcAddr:String;
		public var mylink:MyLink;
		public var fun:Function;
		/**
		 * NQA调用接口
		 * */
		public function getNqaData(stateNodeTemp:StateNode, desIp:String,srcIp:String,link:MyLink,function1:Function):void{
			fun = function1;
			//stateNode为beginNode
			stateNode = stateNodeTemp;
			id = stateNode.getClient("id");
			destAddr =  desIp;
			srcAddr = srcIp;
			mylink = link;
			if(stateNode != null  || destAddr != "" ||srcAddr != "" ){
//				var isPing:Boolean = stateNode.getClient("nqaisPinged");
				var isPing:Boolean = mylink.getClient("nqaisPinged");
				if(isPing==null||!isPing){
					stateNode.setClient("nqacount",0);
					deleteNqa();
				}else{
				    getNqa();
				}
			}
		}
		
		
		/**
		 * 先删除已存在的ping请求
		 * eg.http://10.135.30.166:8080/devices/4/dtools/IcmpPings/IcmpPing?adminName=adm11&testName=test11
		 * 无论是否存在，先删除一次，成功即是原来已有，失败是原来没有
		 * */
        public function deleteNqa():void{
			var delNqaUrl:String = "http://"+opsIp+"/devices/"+id+"/dtools/IcmpPings/IcmpPing?adminName=admin&testName=test";
				connUtil.clientQuery(delNqaUrl,ConnUtil.METHOD_DELETE,delResultFunc,onfaultDel);
		}
		
		public function delResultFunc(e:HttpDataEvent):void{
			if(e.bytes.toString().search("error") == -1){
				trace("averageRtt------delete success----"+averageRtt);
			}else{
				trace("averageRtt-----delete fail-----"+averageRtt);
			}
			postNqa();
		}
		public function onfaultDel(e:Event):void{
			trace("delete失败"+e.toString());
			postNqa();
		}
		
		
		/**
		 * POST ping请求
		 * eg.http://10.135.30.166:8080/devices/4/dtools/IcmpPings/IcmpPing
		 * */
		public function postNqa():void{
			var postNqaUrl:String="http://"+opsIp+"/devices/"+id+"/dtools/IcmpPings/IcmpPing";
			//body-xml
			
		   var body:String="<testName>test</testName><adminName>admin</adminName><sourceAddr>"+srcAddr
			                      +"</sourceAddr><sourceAddrType>IPV4</sourceAddrType><destAddrType>IPV4</destAddrType>"
								  +"<destAddr>"+destAddr+"</destAddr><packetCount>15</packetCount><interval>10</interval>";
		   //<packetCount>15</packetCount><interval>10</interval>
		   trace("postUrl--------------"+postNqaUrl);
		   trace(body);
			connUtil.clientQuery(postNqaUrl,ConnUtil.METHOD_POST,postResultFunc,onfaultPost,body,"application/XML");
		}
		public function postResultFunc(e:HttpDataEvent):void{
			if(e.bytes.toString().search("error")== -1 ){
				trace("averageRtt---post success");
				mylink.setClient("nqaisPinged",true);
				
			}else{
				trace("averageRtt---post fail");
				trace(e.bytes.toString());
			}
		}
		
		public function onfaultPost(e:Event):void{
			trace("Post失败"+e.toString());
		}
		
		/**
		 * 读取NQA结果
		 *eg. http://10.135.30.166:8080/devices/4/dtools/IcmpPings/IcmpPing?adminName=adm11&testName=test11/TestResults
		 * */
		public function getNqa():void{
			var getNqaUrl:String="http://"+opsIp+"/devices/"+id+"/dtools/IcmpPings/IcmpPing?adminName=admin&testName=test";
			trace("getNqaUrl----------"+getNqaUrl);
			connUtil.clientQuery(getNqaUrl,ConnUtil.METHOD_GET,onGetResult,onGetFault);
		}
		
		public function onGetResult(e:HttpResponseEvent,data:ByteArray):void{
			if(data.toString().search("rtt") == -1){
				averageRtt="NA";
			}
			var index:int=data.toString().indexOf("<IcmpPings>");
			if(index != -1){
				if(data.toString().search("</dtools>")!=-1){
					var endIndex:Number=data.toString().indexOf("</dtools>")+"</dtools>".length;
					var result:String="<dtools>"+data.toString().substring(index,endIndex);
					var nqa:XML=XML(result);
					for each(var PacketResult:XML in nqa.IcmpPings.IcmpPing.PacketResults.PacketResult){
						averageRtt = PacketResult.rtt;
						trace("averageRtt::::get::"+averageRtt);
						break;
					}
				}
			}
			fun(averageRtt,0);
			
			var nqacount:* = mylink.getClient("nqacount");
			if(nqacount == null){
				nqacount = 0;
			}else if(nqacount >= 15){
				mylink.setClient("nqaisPinged",false);
				nqacount=0;
			}
			mylink.setClient("nqacount",++nqacount);
			trace("nqacount++"+nqacount);
		}
		public function onGetFault(e:Event):void{
			trace("get失败"+e.toString());
		}
	}
}