package com.huawei.overte.view.node
{
	import com.huawei.overte.event.SdncEvt;
	import com.huawei.overte.tools.ConnUtil;
	import com.huawei.overte.tools.SdncUtil;
	import com.huawei.overte.view.node.StateNodeUI;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.events.TimerEvent;
	import flash.utils.ByteArray;
	import flash.utils.Timer;
	
	import mx.controls.Alert;
	import mx.events.FlexEvent;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.httpclient.events.HttpErrorEvent;
	import org.httpclient.events.HttpResponseEvent;
	import org.osmf.events.TimeEvent;
	
	import twaver.AlarmSeverity;
	import twaver.Consts;
	import twaver.Node;
	import twaver.Styles;

	public class StateNode extends RefNode
	{
		public var timer:Timer;
		private var __curDcName:String;
		private var __cpuRate:Number = 0;
		private var __ramRate:Number = 0;
		private var __nodeInfo:XML;
		private var __type:String = "";
		private var __curDcKeys:Array;
		private var __url:String;
		public var nodeId:int;
		public var nodeType:String;
		public var systemName:String;
		public function StateNode(id:Object=null)
		{
			super(id);
			if(id is String)
				__curDcName = (id as String).split("_")[0];
//			updata();
		}
		
		override public function set name(name:String):void
		{
			super.name = name;
			if(__type == "")
			{
				__type = name;
			}
		}
		
		public function get nodeInfo():XML
		{
			return __nodeInfo;
		}

		public function set nodeInfo(value:XML):void
		{
			__nodeInfo = value;
//			if(__nodeInfo && __nodeInfo.state != "running"&&SdncUtil.cuProjectType != "commix") 
//				this.alarmState.setNewAlarmCount(SdncUtil.CRITICALFAULT,1);
		}

		public function get ramRate():Number
		{
			return __ramRate;
		}

		public function set ramRate(value:Number):void
		{
//			dispatchPropertyChangeEvent("ramRate",__ramRate,value);
			__ramRate = value;
		}

		public function get cpuRate():Number
		{
			return __cpuRate;
		}

		public function set cpuRate(value:Number):void
		{
//			dispatchPropertyChangeEvent("cpuRate",__cpuRate,value);
			__cpuRate = value;
		}
		
		public function changeLabel(type:String):void
		{
			switch(type)
			{
				case "sysname":
					this.name = __nodeInfo.fpName;
					break;
				case "IP":
					this.name = __nodeInfo.fpIp;
					break;
				case "type&id":
					this.name = __type;
					break;
			}
		}
		public var opsIp:String=SdncUtil.opsIp;
		public function updata(url:String):void
		{
			if(SdncUtil.cuProjectType == "test" || SdncUtil.cuProjectType == "commix")
			{
				//ConnUtil.getInstence().query(url,onDevmResult,onFault);
				onDevmResult(new ResultEvent(""));
			}
			else
			{
				var id1:String=getClient("id");
				var url:String = ConnUtil.protocolHeader+opsIp+"/devices/"+id1+"/devm/memoryInfos/memoryInfo";
				ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_GET,getRamResult,onfaultRam);
				var uri:String = ConnUtil.protocolHeader+opsIp+"/devices/"+id1+"/devm/cpuInfos";
				ConnUtil.getInstence().clientQuery(uri,ConnUtil.METHOD_GET,getCpuResult,onfaultCpu);
//				__url = url;
//				ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_GET,onDevmRestResult,onClientFault);
			}
		}
		
		private function getCpuResult(e:HttpResponseEvent,data:String):void
		{
			//解析cpu 取<systemCpuUsage>
			var cpu:XML = XML(data.toString());
			var index:int = data.toString().indexOf("<cpuInfos>");
			if(index != -1){
				var result:String = "<devm> "+data.toString().substr(index);
				cpu = XML(result);
				for each(var CpuInfo:XML in cpu.cpuInfos.cpuInfo){
					var systemCpuUsage:String = CpuInfo.systemCpuUsage;
					cpuRate=Number(systemCpuUsage)/100;
					trace("CpuUsage++++"+systemCpuUsage);
					break;
				}
			}
		}
		private function getRamResult(e:HttpResponseEvent,data:String):void
		{
			// 解析ram 获取<osMemoryUsage>
			var ram:XML = XML(data.toString());
			var index:int = data.toString().indexOf("<memoryInfos>");
			if(index != -1){
				var result:String = "<devm> "+data.toString().substr(index);
				ram = XML(result);
				for each(var RamInfo:XML in ram.memoryInfos.memoryInfo){
					var osMemoryUsage:String = RamInfo.osMemoryUsage;
					ramRate=Number(osMemoryUsage)/100;
					trace("MemoryUsage++++"+osMemoryUsage);
					break;
				}
			}
			
		}
		private function onfaultRam(e:Event):void
		{
			trace("getRam失败"+e.toString());
		}
		private function onfaultCpu(e:Event):void
		{
			trace("getCpu失败"+e.toString());
		}
		/*******************************************************/
		
		
		private function onClientFault(e:*):void
		{
			trace(" state node Client连接错误" + e.text);
		}
		
		private function onDevmRestResult(e:HttpResponseEvent,data:String):void
		{
			var str:String = data.toString();
			str = str.replace(/xmlns(.*?)="(.*?)"/gm, "");
			var devmXmls:XML = XML(str);
			var position:String;
			for each(var module:XML in devmXmls.phyEntitys.phyEntity)
			{
				if(module.entClass == "mpuModule" && module.entStandbyState == "master")
				{
					position = module.position;
					break;
				}
			}
			ConnUtil.getInstence().clientQuery(__url + "/cpuInfos/cpuInfo?position=" + position,
				ConnUtil.METHOD_GET,onCpuInfoRestResult,onClientFault);
			ConnUtil.getInstence().clientQuery(__url + "/memoryInfos/memoryInfo?position=" + position,
				ConnUtil.METHOD_GET,onMemInfoRestResult,onClientFault);
//			position = "1";//测试数据无法对应，先固定
//			var url:String = "http://189.32.95.14:8080/devices/" + String(this.id) + "/devm/cpuInfos/cpuInfo?position=" + position;
//			url = "http://189.32.95.14:8080/devices/11/devm/cpuInfos/cpuInfo?position=" + position;
//			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_GET,onCpuInfoRestResult,onClientFault);
//			url = "http://189.32.95.14:8080/devices/" + String(this.id) + "/devm/memoryInfos/memoryInfo?position=" + position;
//			url = "http://189.32.95.14:8080/devices/11/devm/memoryInfos/memoryInfo?position=" + position;
//			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_GET,onMemInfoRestResult,onClientFault);
		}
		
		private function onCpuInfoRestResult(e:HttpResponseEvent,data:String):void
		{
			if(data)
			{
				var str:String = data.toString();
				str = str.replace(/xmlns(.*?)="(.*?)"/gm, "");
				var cpuinfoXml:XML = XML(str);
				cpuRate = Number(cpuinfoXml.cpuInfos.cpuInfo.systemCpuUsage) / 100;
			}
		}
		
		private function onMemInfoRestResult(e:HttpResponseEvent,data:String):void
		{
			if(data)
			{
				var str:String = data.toString();
				str = str.replace(/xmlns(.*?)="(.*?)"/gm, "");
				var memoryinfoXml:XML = XML(str);
				ramRate = Number(memoryinfoXml.memoryInfos.memoryInfo.simpleMemoryUsage) / 100;
			}
		}
		
		private function onFault(e:FaultEvent):void
		{
			trace(e.target.url + "连接错误 state node");
		}
		
		private function onDevmResult(e:ResultEvent):void
		{
			cpuRate = Math.random();
			ramRate = Math.random();
//			var devmXmls:XML = e.result as XML;
//			var position:String;
//			for each(var module:XML in devmXmls.phyEntitys.phyEntity)
//			{
//				if(module.entClass == "mpuModule" && module.entStandbyState == "master")
//				{
//					position = module.position;
//					break;
//				}
//			}
//			
//			
//			for each(var cpuInfo:XML in devmXmls.cpuInfos.cpuInfo)
//			{
//				if(cpuInfo.position == position)
//				{
//					var ranCpu:Number = Math.random();
//					//cpuRate = Number(cpuInfo.systemCpuUsage) / 100;//无数据，暂用随即函数生成数据
//					cpuRate = ranCpu;
//					break;
//				}
//			}
//			
//			for each(var ramInfo:XML in devmXmls.memoryInfos.memoryInfo)
//			{
//				if(ramInfo.position == position)
//				{
//					var ranRam:Number = Math.random();
//					//ramRate = Number(ramInfo.simpleMemoryUsage) / 100;//无数据，暂用随即函数生成数据
//					ramRate = ranRam;
//					break;
//				}
//			}
		}
		
		override public function get elementUIXClass():Class
		{
			return StateNodeUI;
		}

		public function get _curDcName():String
		{
			return __curDcName;
		}

		public function set _curDcName(value:String):void
		{
			__curDcName = value;
		}

		
	}
}