package com.huawei.sdnc.model
{
	import com.huawei.sdnc.event.SdncEvt;
	import com.huawei.sdnc.model.acl.AclGroup;
	import com.huawei.sdnc.model.acl.aclRuleBas4.AclRuleAdv4;
	import com.huawei.sdnc.model.qos.QosActRdrIf;
	import com.huawei.sdnc.model.qos.QosBehavior;
	import com.huawei.sdnc.model.qos.QosClassifier;
	import com.huawei.sdnc.model.qos.QosGlobalCfgs;
	import com.huawei.sdnc.model.qos.QosGlobalPolicyApply;
	import com.huawei.sdnc.model.qos.QosPolicy;
	import com.huawei.sdnc.model.qos.QosPolicyNode;
	import com.huawei.sdnc.model.qos.QosRuleAcl;
	import com.huawei.sdnc.techschema.ServiceTool;
	import com.huawei.sdnc.tools.ConnUtil;
	import com.huawei.sdnc.tools.ErrorCodeUtil;
	import com.huawei.sdnc.tools.PopupManagerUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.common.node.StateNode;
	import com.huawei.sdnc.view.gre.MyLink;
	import com.huawei.sdnc.view.gre.dataHandle.DataHandleTool;
	
	import flash.events.Event;
	import flash.events.TimerEvent;
	import flash.utils.ByteArray;
	import flash.utils.Timer;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.messaging.channels.StreamingAMFChannel;
	
	import org.httpclient.events.HttpDataEvent;
	import org.httpclient.events.HttpErrorEvent;
	import org.httpclient.events.HttpResponseEvent;
	
	import twaver.Styles;
	import twaver.network.Network;
	import twaver.networkx.NetworkX;

	public class Device extends MetaData
	{
		public function Device()
		{
			
		}
		public var id:String;
		public var ip:String;
		public var username:String;
		public var devicename:String;
		public var passwd:String;
		public var version:String;
		public var productType:String;
		public var qosGlobalCfgs:Array=[]
		//设备是否能连上的状态
		public var state:String;
		//当前设备数据是否全部取到的状态，true为全部取到，如果有错误或者取不到为false，此设备标红
		public var deviceState:Boolean = true;
		//初始化设备直接初始化回滚点
		public var commitId:String;
		
		public var acl:Acl;
		public var gre:Gre;
		public var qos:Qos;
		public var ifm:Ifm;
		public var flow:Flow;
		
		private var aclIs:Boolean=false;
		private var greIs:Boolean=false;
		private var qosIs:Boolean=false;
		private var ifmIs:Boolean=false;
		private var flowIs:Boolean=false;
		
		public var aclNumOrNames:Array=[];
		private var reFun:Function;
		
		public function initDevice(fun:Function):void
		{
			reFun=fun;
			getqosGlobalCfgs();
			getflow();
			getGre();
			getIfm();
		}
		/**********************************************get acl  qos********************************************************/
		public function getflow(isAll:Boolean=true,re:Function=null):void
		{
			flow=new Flow;
			flow.stateNode=stateNode;
			
			acl=new Acl;
			acl.stateNode=stateNode;
			
			qos=new Qos;
			qos.stateNode=stateNode;
			flowIs=false;
			
			if(isAll){
				flow.getFlow(flowIsReady);
			}else{
				if(re!==null){
					flow.getFlow(re);
				}
				
			}
		}
		/**********************************************get qosGlobalCfgs********************************************************/
		public function getqosGlobalCfgs():void
		{
			var opsIp:String=SdncUtil.serviceIp;
			var id:String=stateNode.getClient("id");
			var getflowUrl:String="http://"+opsIp+"/AgileGre/rest/devices/"+id+"/qosGlobalCfgs";
			connUtil.clientQuery(getflowUrl,ConnUtil.METHOD_GET,onGetFlowGroup,onGetFlowFault);
		}
		
		private function onGetFlowGroup(e:HttpResponseEvent,data:ByteArray):void
		{
			var errorcode:ErrorCodeUtil = new ErrorCodeUtil;
			if(!errorcode.parse(e,data)){
				DataHandleTool.showOnConsole(errorcode.printErrorMessage(stateNode, "   getqosGlobalCfgs失败\n"));
				reFun();
				return;
			}
			try{
				var ip:String=stateNode.getClient("ip");
				trace("返回码"+e.response.code);
				if(e.response.code=="200"){
					var index:int=data.toString().indexOf("<qosGlobalCfgs>");
					var result:String="<qos> "+data.toString().substr(index);
					var flow:XML=new XML(result);
					for each(var qosGlobalPolicyApply:XML in flow.qosGlobalCfgs.qosGlobalPolicyApplys.qosGlobalPolicyApply){
						if(qosGlobalPolicyApply==null)
							break;
						var qosGlobalPolicyApplyC:QosGlobalPolicyApply = new QosGlobalPolicyApply();
						qosGlobalPolicyApplyC.policyName = qosGlobalPolicyApply.policyName;
						qosGlobalPolicyApplyC.direction = qosGlobalPolicyApply.direction;
						qosGlobalCfgs.push(qosGlobalPolicyApplyC);
					}
					DataHandleTool.showOnConsole("设备"+ip+"   getqosGlobalCfgs成功\n");
					trace("设备"+ip+"   getqosGlobalCfgs成功");
				}else{
					DataHandleTool.showOnConsole("设备"+ip+"   getqosGlobalCfgs失败\n");
					trace("设备"+ip+"   getqosGlobalCfgs失败");
				}
				if(reFun!=null){
				}
			}catch(e:Event){
				trace("getflow异常:"+e.toString());
				DataHandleTool.showOnConsole("设备"+ip+"   getflow失败\n");
			}
		}
		private function onGetFlowFault(e:*):void
		{
			var ip:String=stateNode.getClient("ip");
			if(DataHandleTool.console!=null){
				var content:String="设备"+ip+"   getflow出错\n";
				DataHandleTool.console.console.text+=content;
			}
		}				
				
		
		/**
		 * 
		 * @param isAll 是否是全部更新 默认为是
		 * @param re
		 * oc
		 */			
		public function getIfm(isAll:Boolean=true,re:Function=null):void
		{
			ifm = new Ifm;
			ifmIs=false;
			ifm.stateNode=stateNode;
			if(isAll){
				ifm.getIfm(ifmIsReady);
			}else{
				if(re!==null){
					ifm.getIfm(re);
				}
			}
		}
		
		/**
		 * 
		 * @param isAll 是否是全部更新 默认为是
		 * @param re
		 * 
		 */			
		public function getGre(isAll:Boolean=true,re:Function=null):void
		{
			gre=new Gre;
			greIs=false;
			gre.stateNode=stateNode;
			if(isAll){
				gre.getGre(greIsReady);
			}else{
				if(re!==null){
					gre.getGre(re);
				}
				
			}
			
		}
		
		
		private function greIsReady():void
		{
			greIs=true;
			isCon();
		}
		
		
		
		private function ifmIsReady():void
		{
			ifmIs=true;
			isCon();
		}
		
		private function flowIsReady():void
		{
			flowIs=true;
			trace(":::::::::::::::::"+flowIs)
			isCon();
		}
		
		private function isCon():void
		{
			trace(greIs+"======"+ifmIs+"======"+flowIs)
			if(greIs&&ifmIs&&flowIs){
				if(deviceState){
					trace("设备数据正常");
				}else{
					stateNode.setStyle(Styles.INNER_COLOR,0xea6060);
				}
				reFun();
			}
		}
		// 计算当前设备的qoslist信息
		public function addQosList(qosCalculate:QosListCalculate):void
		{
			var qosClassifiers:Array=qos.qosClassifiers;
			var qosBehaviors:Array=qos.qosBehaviors;
			for each(var qosClassifier:QosClassifier in qosClassifiers){
				var qositem:QosItem = new QosItem();
				qositem.stateNode=stateNode;
				qositem.devicename=stateNode.getClient("devicename");
				var classifierName:String=qosClassifier.classifierName;
				
				var qosRuleAcls:Array=qosClassifier.qosRuleAcls;
				var aclN:String="";
				for each(var qosRuleAcl:QosRuleAcl in qosRuleAcls){
					aclN=qosRuleAcl.aclName;
				}
				var aclGroups:Array=acl.aclGroups;
				for each(var aclGroup:AclGroup in aclGroups){
					var aclNumOrName:String=aclGroup.aclNumOrName;
					if(aclNumOrName==aclN){
						if(aclGroup.aclRuleAdv4s.length>0){
							qositem.qosSrcIp=(aclGroup.aclRuleAdv4s[0] as AclRuleAdv4).aclSourceIp;
							qositem.qosdestIp=(aclGroup.aclRuleAdv4s[0] as AclRuleAdv4).aclDestIp;
							qositem.aclGroup=aclGroup;
						}else if(aclGroup.aclRuleAdv4s.length==0){
							qositem.qosSrcIp="";
							qositem.qosdestIp="";
							qositem.aclGroup=aclGroup;
						}
						break;
					}
				}
				
				qositem.qosName = classifierName;
				qositem.qosSrcAddress = stateNode.getClient("ip");
				for each(var qosBehavior:QosBehavior in qosBehaviors){
					var behaviorName:String=qosBehavior.behaviorName;
					var ifName:String="";
					//找到管道名字
					if(behaviorName==classifierName+"_action"){
						var qosActRdrIfs:Array=qosBehavior.qosActRdrIfs;
						for each(var qosActRdrIf:QosActRdrIf in qosActRdrIfs){
							ifName=qosActRdrIf.ifName;
						}
					}
					//根据管道名字找到设备ip
					var greTunnels:Array=gre.greTunnels;
					for each(var  greTunnel:GreTunnel in greTunnels){
						var tnlName:String=greTunnel.tnlName;
						if(tnlName==ifName){
							var dstIp:String=greTunnel.dstIpAddr;
							var node:StateNode=DataHandleTool.findNodeByDstIp(dstIp,SdncUtil.app.ipcore.physicsView.networkX);
							if(node!=null){
								qositem.qosdestAddress = node.getClient("ip");
								qositem.ifName=ifName;
								qosCalculate.addQos(qositem);
							}
							
						}
					}
				}
			}
		}
		

		
		
		/***************************************************** 生成全局流策略*********************************************************/
		public function createGlobalPolicy(renF:Function):void
		{
			reFun=renF;
			var id:String=stateNode.getClient("id");
			var opsIp:String=SdncUtil.opsIp;
			var qosPolicyUrl:String="http://"+opsIp+"/devices/"+id+"/qos/qosCbQos/qosPolicys/qosPolicy";
			var param:String=
				"<policyName>gp</policyName>";
			connUtil.clientQuery(qosPolicyUrl,ConnUtil.METHOD_POST,onCreateGlobal,onCreateFault,param,"application/xml");
		}
		private function onCreateGlobal(e:HttpDataEvent):void
		{
			var message:String=e.bytes.toString();
			trace(message);
			if(message.search("rpc-error")==-1){
				var id:String=stateNode.getClient("id");
				var opsIp:String=SdncUtil.opsIp;
				var qosPolicyUrl:String="http://"+opsIp+"/devices/"+id+"/qos";
				var param:String="<qosGlobalCfgs>"
					+"<qosGlobalPolicyApplys>"
					+"<qosGlobalPolicyApply>"
					+"<policyName>gp</policyName>"
					+"<direction>inbound</direction>"
					+"</qosGlobalPolicyApply>"
					+"</qosGlobalPolicyApplys>"
					+"</qosGlobalCfgs>";
				connUtil.clientQuery(qosPolicyUrl,ConnUtil.METHOD_POST,reFun,onCreateFault,param,"application/xml");
			}
		}
		private function onCreateFault(e:*):void
		{
			reFun(e);
		}
		
		public var v1:Number
		public var v2:Number
		public function postGreTunnel(greTunnel:GreTunnel,renF:Function):void
		{
			reFun=renF;
			v1 = new Date().time
			var opsIp:String=SdncUtil.serviceIp;
			var id:String=stateNode.getClient("id");
			var uri:String="http://"+opsIp+"/AgileGre/rest/devices/"+id+"/utunnel";
			var greTunnelJsonStr:String="<utunnel><tunnels><tunnel>" +
				"<tunnelName>"+greTunnel.tnlName+"</tunnelName>" +//Tunnel名称
				"<tunnelId></tunnelId><direction></direction>" +
				"<tunnelType>"+greTunnel.tnlType+"</tunnelType>" +//Tunnel类型
				"<sourceNodeId>"+greTunnel.srcIpAddr+"</sourceNodeId>" +//Tunnel源IP
				"<destinationNodeId>"+greTunnel.dstIpAddr+"</destinationNodeId>" +//Tunnel目的IP
				"<sourceTpType>"+greTunnel.srcType+"</sourceTpType>" +//Tunnel源类型IP
				"<sourceTpId></sourceTpId><bandwidth></bandwidth><latency></latency><adminStatus></adminStatus></tunnel></tunnels></utunnel>"
			connUtil.clientQuery(uri,ConnUtil.METHOD_POST,onPostGreTunnel,onPostGreTunnelFault,greTunnelJsonStr,"application/json","Boolean ret = utunnel.create()");
		}
		private function onPostGreTunnel(e:HttpDataEvent):void
		{
			trace("postGreTunnel      "+e.bytes.toString());
			var message:String=e.bytes.toString();
			v2 = new Date().time
			if(message.search("error")==-1){
			}
			ServiceTool.message = message
			ServiceTool.api = "utunnel"
			ServiceTool.usetime = (v2-v1)+"ms";
			ServiceTool.curtime = SdncUtil.getFormatStr("MM-DD  JJ:NN",new Date())
			DataHandleTool.showOnConsole("postGreTunnel      "+e.bytes.toString()+"\n");
			reFun(e);
		}
		private function onPostGreTunnelFault(e:*):void
		{
			reFun(e);
		}
		
		public function putIfmIp(body:String,fun:Function):void
		{
			reFun=fun;
			var opsIp:String=SdncUtil.serviceIp;
			var id:String=stateNode.getClient("id");
			var url:String="http://"+opsIp+"/AgileGre/rest/devices/"+id+"/ifms";
			connUtil.clientQuery(url,ConnUtil.METHOD_PUT,onPutIfmIp,onPutIfmIpFault,body,"application/xml");
		}
		private function onPutIfmIp(e:HttpDataEvent):void
		{
			var message:String=e.bytes.toString();
			if(message.search("error")==-1){
				trace("putIfmIp成功");
				DataHandleTool.showOnConsole("putIfmIp成功\n");
			}
			reFun();
		}
		private function onPutIfmIpFault(e:*):void
		{
			reFun();
		}
		
		/********************************Device PingTest********************************************************************/
		private var fun1:Function;
		public function pingTest(id:String,fun:Function):void{
			fun1 = fun;
			var opsIp:String = SdncUtil.opsIp;
			var url:String = "http://"+opsIp+"/devices/"+id+"/_restping";
			connUtil.clientQuery(url,ConnUtil.METHOD_GET,onPingResult,onPingFault);
		}
		
		private function onPingResult(e:HttpResponseEvent,data:ByteArray):void
		{
			if(data.toString().search("pinging success") == -1){
				state = "sick";
			}else{
				state = "normal";
			}
			fun1(this);
		}
		private  function onPingFault(e:Event):void
		{
			trace("pingTest出现错误");			
		}
		
		

		
	}
}