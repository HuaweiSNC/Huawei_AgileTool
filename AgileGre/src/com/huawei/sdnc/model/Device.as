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
	import com.huawei.sdnc.tools.ConnUtil;
	import com.huawei.sdnc.tools.ErrorCodeUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.common.node.StateNode;
	import com.huawei.sdnc.view.ipCore_DCI.MyLink;
	import com.huawei.sdnc.view.ipCore_DCI.dataHandle.DataHandleTool;
	
	import flash.events.Event;
	import flash.events.TimerEvent;
	import flash.utils.ByteArray;
	import flash.utils.Timer;
	
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
		
		private var aclIs:Boolean=false;
		private var greIs:Boolean=false;
		private var qosIs:Boolean=false;
		private var ifmIs:Boolean=false;
		public var aclNumOrNames:Array=[];
		private var reFun:Function;
		
		public function initDevice(fun:Function):void
		{
			reFun=fun;
			getAcl();
			getQos();
			getGre();
			getIfm();
			SdncUtil.app.addEventListener(SdncEvt.SWITCH_NQA,switchNqa);
		}
		
		/**
		 * 
		 * @param isAll 是否是全部更新 默认为是
		 * @param re
		 * 
		 */		
		public function getAcl(isAll:Boolean=true,re:Function=null):void
		{
			acl=new Acl;
			aclIs=false;
			acl.stateNode=stateNode;
			if(isAll){
				acl.getAcl(aclIsReady);
			}else{
				if(re!==null){
					acl.getAcl(re);
				}
				
			}
			
		}
		/**
		 * @param isAll 是否是全部更新 默认为是
		 * @param re
		 */			
		public function getQos(isAll:Boolean=true,re:Function=null):void
		{
			qos=new Qos;
			qosIs=false;
			qos.stateNode=stateNode;
			if(isAll){
				qos.getQos(qosIsReady);
			}else{
				if(re!==null){
					qos.getQos(re);
				}
				
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
		
		private function aclIsReady():void
		{
			aclIs=true;
			isCon();
		}
		
		private function qosIsReady():void
		{
			qosIs=true;
			isCon();
		}
		
		private function ifmIsReady():void
		{
			ifmIs=true;
			isCon();
		}
		
		private function isCon():void
		{
			if(greIs&&qosIs&&aclIs&&ifmIs){
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
		/*********************************************删除流策略*****************************************************/
		/**
		 *删除流策略 
		 * @param policyName
		 * @param renFun
		 * 
		 */		
		private var policyObj:Object;
		private var hasDelPolicys:int=0;
		public function delPolicy(policyO:Object,renFun:Function):void{
			policyObj=policyO;
			reFun=renFun;
			var policyName:String=policyObj["policyName"];
			var opsIp:String=SdncUtil.opsIp;
			var id:String=stateNode.getClient("id");
			//首先检查globalPolicy有没有此名字的策略
			var qosPolicys:Array=qos.qosPolicys;
			var isEnd:Boolean=false;
			//查找全局策略名字
			var qosGlobalCfgs:QosGlobalCfgs=qos.qosGlobalCfgs
			var qosGlobalPolicyApplys:Array=qosGlobalCfgs.qosGlobalPolicyApplys;
			var policys:Array=qos.qosPolicys;
			for each(var policy:QosPolicy in policys){
				var policNameX:String=policy.policyName;
				var qosPolicyNodes:Array=policy.qosPolicyNodes;
				for each(var qosPolicyNode:QosPolicyNode in qosPolicyNodes){
					var classifierNameInNode:String=qosPolicyNode.classifierName;
					if(classifierNameInNode==policyName){
						isEnd=true;
						var urlX:String="http://"+opsIp+"/devices/"+id+"/qos/qosCbQos/qosPolicys/qosPolicy";
						var body:String =
							"<policyName>"+policNameX+"</policyName>"
							+"<qosPolicyNodes>"
							+"<qosPolicyNode operation='delete'><classifierName>"+classifierNameInNode+"</classifierName>" 
							//+"<behaviorName>"+policyName+"_action</behaviorName>" 
							+"</qosPolicyNode>" 
							+"</qosPolicyNodes>";
						connUtil.clientQuery(urlX,ConnUtil.METHOD_PUT,onDelPolicy,onDelPolicyfault,body,"application/XML");
						break;
					}
				}
				if(isEnd)
					break;
			}
			if(!isEnd){
				onDelPolicy(null);
			}
		}
		private function onDelPolicy(e:HttpDataEvent):void
		{
			if(e!=null)
				trace(e.bytes.toString());
				//删除流分类，流行为，及五元组
				var opsIp:String=SdncUtil.opsIp;
				var id:String=stateNode.getClient("id");
				var classifierName:String=policyObj["policyName"];
				var classifierDelUrl:String="http://"+opsIp+"/devices/"+id+"/qos/qosCbQos/qosClassifiers/qosClassifier?classifierName="+classifierName;
				connUtil.clientQuery(classifierDelUrl,ConnUtil.METHOD_DELETE,onDelClassifier,onDelClassifierfault);
		}
		private function onDelPolicyfault(e:HttpErrorEvent):void
		{
			reFun();
		}
		/**
		 * 流分类删除后 再删除流行为
		 * @param e
		 */		
		private function onDelClassifier(e:HttpDataEvent):void
		{
			var ip:String=stateNode.getClient("ip");
			trace(ip+"删除Classifier：   "+e.bytes.toString());
			var opsIp:String=SdncUtil.opsIp;
			var id:String=stateNode.getClient("id");
			var behaviorName:String=policyObj["behaviorName"];
			var classifierDelUrl:String="http://"+opsIp+"/devices/"+id+"/qos/qosCbQos/qosBehaviors/qosBehavior?behaviorName="+behaviorName;
			connUtil.clientQuery(classifierDelUrl,ConnUtil.METHOD_DELETE,onDelBehavior,onDelBehaviorfault);
		}
		/********************************************删除流行为之后再删除五元组*************************************************/
		private function onDelBehavior(e:HttpDataEvent):void
		{
			var ip:String=stateNode.getClient("ip");
			trace(ip+"删除behavior：    "+e.bytes.toString());
			var opsIp:String=SdncUtil.opsIp;
			var id:String=stateNode.getClient("id");
//			var aclNumOrName:String=policyObj["aclNumOrName"];
			
			
			var qosClassifiers:Array=qos.qosClassifiers;
			var classifierName1:String=policyObj["policyName"];
			//查询五元组名称
			var aclNumOrName:String="";
			for each(var qosClassifier:QosClassifier in qosClassifiers){
				var policyObj:Object=new Object;
				var classifierName:String=qosClassifier.classifierName;
				if(classifierName==classifierName1){
					for each(var qosRuleAcl:QosRuleAcl in qosClassifier.qosRuleAcls){
						aclNumOrName=qosRuleAcl.aclName;
						var classifierID:String=qosRuleAcl.classifierID;
					}
					break;
				}
			}
			if(aclNumOrName==""){
				reFun();
				return;
			}
			var aclDelUrl:String="http://"+opsIp+"/devices/"+id+"/acl/aclGroups/aclGroup?aclNumOrName="+aclNumOrName;
			trace("删除 "+stateNode.getClient("devicename")+"        aclNumOrName"+aclNumOrName);
			
			connUtil.clientQuery(aclDelUrl,ConnUtil.METHOD_DELETE,onDelAcl,onDelAclfault);
		}
		private function onDelAcl(e:HttpDataEvent):void
		{
			var ip:String=stateNode.getClient("ip");
			trace(ip+"删除acl："+e.bytes.toString());
			getAcl(false,onGetAcl);
		}
		private function onGetAcl():void
		{
			getQos(false,reFun);
//			reFun();
		}
		private function onDelAclfault(e:*):void
		{
			reFun();
		}
		private function onDelClassifierfault(e:HttpErrorEvent):void
		{
			var ip:String=stateNode.getClient("ip");
			trace(ip+"删除classifier错误");
			reFun();
		}
		private function onDelBehaviorfault(e:*):void
		{
			var ip:String=stateNode.getClient("ip");
			trace(ip+"删除behavior错误");
			reFun();
		}
		public function delAcl(aclNumOrName:String):void
		{
			
		}
		private function delPolicyRen():void{
			getQos(false,reFun);
		}
		/*********************************************编辑ACL begin*****************************************************/
		private var aclG:AclGroup;
		private var classifierN:String;
		private var behaviorN:String;
		private var tnlN:String;
		public function putAcl(aclGroup:AclGroup,classifierName:String,behaviorName:String,tnlName:String):void
		{
			aclG=aclGroup;
			classifierN=classifierName;
			behaviorN=behaviorName;
			tnlN=tnlName;
			var id:String=stateNode.getClient("id");
			var opsIp:String=SdncUtil.opsIp;
			var putAclGroupUrl:String="http://"+opsIp+"/devices/"+id+"/acl/aclGroups/aclGroup?aclNumOrName=3999/aclRuleAdv4s/aclRuleAdv4";
			var param:String="";
			connUtil.clientQuery(putAclGroupUrl,ConnUtil.METHOD_PUT,onPutAcl,onPutFault,param,"application/xml");
		}
		
		private function onPutAcl(e:HttpDataEvent):void
		{
			var opsIp:String=SdncUtil.opsIp;
			var qosClassifierUrl:String="http://"+opsIp+"/devices/"+id+"/qos/qosCbQos/qosClassifiers/qosClassifier";
			//流分类名字
			//classifierN
			//五元组名字
			var aclRuleNameE:String=(aclG.aclRuleAdv4s[0] as AclRuleAdv4).aclRuleName;
			var param:String="";
			connUtil.clientQuery(qosClassifierUrl,ConnUtil.METHOD_PUT,onPutClassifier,onPutFault,param,"application/xml");
		}
		private function onPutFault(e:HttpErrorEvent):void
		{
			
		}
		private function onPutClassifier(e:HttpDataEvent):void
		{
			//put流分类之后 再put流行为
			var id:String=stateNode.getClient("id");
			var opsIp:String=SdncUtil.opsIp;
			var qosBehaviorUrl:String="http://"+opsIp+"/devices/"+id+"/qos/qosCbQos/qosBehaviors/qosBehavior";
			//流分类名字
			//五元组名字
			var param:String=
				"<behaviorName>"+behaviorN+"</behaviorName>"
				+ "<qosActFilters>"
				+ "<qosActFilter>"
				+ "<actionType>filter</actionType>"
				+ "<filter>deny</filter>"
				+ "</qosActFilter>"
				+ "</qosActFilters>"
				+ "<qosActRdrIfs>"
				+ "<qosActRdrIf>"
				+ "<ifName>"+tnlN+"</ifName>"
				+ "<actionType>redirectIf</actionType>"
				+ "</qosActRdrIf>"
				+ "</qosActRdrIfs>";
			connUtil.clientQuery(qosBehaviorUrl,ConnUtil.METHOD_PUT,onPutClassifier,onPutFault,param,"application/xml");
		}
		/**********************************************post acl********************************************************/
		private var flowObj:Object;
		public function postAclGroup(aclGroup:AclGroup,renF:Function,flow_device:Object):void
		{
			flowObj=flow_device;
			reFun=renF;
			var id:String=stateNode.getClient("id");
			var opsIp:String=SdncUtil.opsIp;
			var postAclUrl:String="http://"+opsIp+"/devices/"+id+"/acl/aclGroups/aclGroup";
			//流分类名字
			//五元组名字
			var aclNumOrName:String=aclGroup.aclNumOrName;
			trace(stateNode.getClient("devicename")+"aclNumOrName:"+aclNumOrName);
			var param:String=
				"<aclNumOrName>"+aclNumOrName+"</aclNumOrName>"
				+"<aclStep>5</aclStep>"
				+"<aclRuleAdv4s>"
				+"<aclRuleAdv4>"
				+"<aclRuleName>ruleName"+aclNumOrName+"</aclRuleName>"
				+"<aclProtocol>"+(aclGroup.aclRuleAdv4s[0] as AclRuleAdv4).aclProtocol+"</aclProtocol>"
				+"<aclSourceIp>"+(aclGroup.aclRuleAdv4s[0] as AclRuleAdv4).aclSourceIp+"</aclSourceIp>"
				+"<aclSrcWild>"+(aclGroup.aclRuleAdv4s[0] as AclRuleAdv4).aclSrcWild+"</aclSrcWild>"
				+"<aclDestIp>"+(aclGroup.aclRuleAdv4s[0] as AclRuleAdv4).aclDestIp+"</aclDestIp>"
				+"<aclDestWild>"+(aclGroup.aclRuleAdv4s[0] as AclRuleAdv4).aclDestWild+"</aclDestWild>"
				+"</aclRuleAdv4>"
				+"</aclRuleAdv4s>";
			connUtil.clientQuery(postAclUrl,ConnUtil.METHOD_POST,onPostAclGroup,onPostAclFault,param,"application/xml");
		}
		private function onPostAclGroup(e:HttpDataEvent):void
		{
			reFun(e,flowObj);
		}
		private function onPostAclFault(e:*):void
		{
			reFun(e,flowObj);
		}
		/**********************************************post qos********************************************************/
		public function postQosClassifier(qosClassifier:QosClassifier,renF:Function,flow_device:Object):void
		{
			flowObj=flow_device;
			reFun=renF;
			var id:String=stateNode.getClient("id");
			var opsIp:String=SdncUtil.opsIp;
			var qosClassifierUrl:String="http://"+opsIp+"/devices/"+id+"/qos/qosCbQos/qosClassifiers/qosClassifier";
			
			//五元组名字
			var aclNumOrName:String;
			for each(var qosRule:QosRuleAcl in qosClassifier.qosRuleAcls){
				aclNumOrName=qosRule.aclName;
			}
			//流分类名字
			var classifierName:String=qosClassifier.classifierName;
			var param:String=
				"<classifierName>"+classifierName+"</classifierName>"
                 +"<operator>and</operator>"
				 +"<qosRuleAcls>"
                 +"<qosRuleAcl>"
				 +"<aclFamily>ipv4</aclFamily>"
				 +"<aclName>"+aclNumOrName+"</aclName>"
				 +"</qosRuleAcl>"
				 +"</qosRuleAcls>";
			connUtil.clientQuery(qosClassifierUrl,ConnUtil.METHOD_POST,onPostQosClassifier,onPostQosClassifierFault,param,"application/xml");
		}
		private function onPostQosClassifier(e:HttpDataEvent):void
		{
			reFun(e,flowObj);
		}
		private function onPostQosClassifierFault(e:*):void
		{
			reFun(e,flowObj);
		}
		/***************************************************post QosBehavior*************************************************************/
		public function postQosBehavior(qosBehavior:QosBehavior,renF:Function,flow_device:Object):void
		{
			flowObj=flow_device;
			reFun=renF;
			var id:String=stateNode.getClient("id");
			var opsIp:String=SdncUtil.opsIp;
			var qosBehaviorUrl:String="http://"+opsIp+"/devices/"+id+"/qos/qosCbQos/qosBehaviors/qosBehavior";
			//流分类名字
			var behaviorName:String=qosBehavior.behaviorName;
			//管道名称
			var tnlName:String=(qosBehavior.qosActRdrIfs[0] as QosActRdrIf).ifName;
			var param:String=
				"<behaviorName>"+behaviorName+"</behaviorName>"
				+"<qosActRdrIfs>"
				+"<qosActRdrIf>"
				+"<actionType>redirectIf</actionType>"
				+"<ifName>"+tnlName+"</ifName>"
				+"</qosActRdrIf>"
				+"</qosActRdrIfs>";
			connUtil.clientQuery(qosBehaviorUrl,ConnUtil.METHOD_POST,onPostQosBehavior,onPostBehaviorFault,param,"application/xml");
		}
		private function onPostQosBehavior(e:HttpDataEvent):void
		{
			reFun(e,flowObj);
		}
		private function onPostBehaviorFault(e:*):void
		{
			reFun(e,flowObj);
		}
		/***************************************************post QosPolicy*************************************************************/
		public function postQosPolicy(qosPolicy:QosPolicy,renF:Function,flow_device:Object):void
		{
			flowObj=flow_device;
			reFun=renF;
			var id:String=stateNode.getClient("id");
			var opsIp:String=SdncUtil.opsIp;
			var qosPolicyUrl:String="http://"+opsIp+"/devices/"+id+"/qos/qosCbQos/qosPolicys/qosPolicy";
			var behaviorName:String=(qosPolicy.qosPolicyNodes[0] as QosPolicyNode).behaviorName;
			var classifierName:String=(qosPolicy.qosPolicyNodes[0] as QosPolicyNode).classifierName;
			var param:String="<policyName>"+qosPolicy.policyName+"</policyName>"
				+"<qosPolicyNodes>"
				+"<qosPolicyNode>"
				+"<classifierName>"+classifierName+"</classifierName>"
				+"<behaviorName>"+behaviorName+"</behaviorName>"
                + "</qosPolicyNode>"
                +"</qosPolicyNodes>";
			connUtil.clientQuery(qosPolicyUrl,ConnUtil.METHOD_POST,onPostPolicy,onPostPolicyFault,param,"application/xml");
		}
		private function onPostPolicy(e:HttpDataEvent):void
		{
			var message:String=e.bytes.toString();
			trace("下发应用：   "+message);
			reFun();
		}
		private function onPostPolicyFault(e:*):void
		{
			trace("下发应用：   失败 方法 postQosPolicy");
			reFun();
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
		private function onApplySuccess(e:HttpDataEvent):void
		{
			var ip:String=stateNode.getClient("ip");
			trace("设备"+ip+"   下发应用成功\n"+e.bytes.toString());
			getQos(false,reFun);
		}
		private function onApplyFault(e:HttpErrorEvent):void
		{
			
		}
		public function postGreTunnel(greTunnel:GreTunnel,renF:Function):void
		{
			reFun=renF;
			var opsIp:String=SdncUtil.opsIp;
			var id:String=stateNode.getClient("id");
			var uri:String="http://"+opsIp+"/devices/"+id+"/gre/greTunnels/greTunnel";
			var greTunnelJsonStr:String=
				"[{\"tnlName\":\""+greTunnel.tnlName+"\"},"
				+"{\"tnlType\":\""+greTunnel.tnlType+"\"},"
				+"{\"srcType\":\""+greTunnel.srcType+"\"},"
				+"{\"srcIpAddr\":\""+greTunnel.srcIpAddr+"\"},"
				+"{\"dstVpnName\":\""+"_public_"+"\"},"
				+"{\"dstIpAddr\":\""+greTunnel.dstIpAddr+"\"},"
				+"{\"keepalvEn\":\""+false+"\"},"
				+"{\"keepalvPeriod\":\""+5+"\"},"
				+"{\"keepalvRetryCnt\":\""+3+"\"}]";
			connUtil.clientQuery(uri,ConnUtil.METHOD_POST,onPostGreTunnel,onPostGreTunnelFault,greTunnelJsonStr);
		}
		private function onPostGreTunnel(e:HttpDataEvent):void
		{
			trace("postGreTunnel      "+e.bytes.toString());
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
			var opsIp:String=SdncUtil.opsIp;
			var id:String=stateNode.getClient("id");
			var url:String="http://"+opsIp+"/devices/"+id+"/ifm/interfaces/interface";
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
		/********************************调用ifm带宽占用率和NQA************************************************************/
		public function removeListener():void
		{
			if(timer.hasEventListener(TimerEvent.TIMER)){
				if(timer.running){
					timer.stop();
				}
				timer.removeEventListener(TimerEvent.TIMER,timerHandler);
			}
		}
	    public var timer:Timer = new Timer(10000);
		private var desIp:String;
		private var link:MyLink;
		private var nqa:Nqa;
		public function getNqaIfmResult(desIp1:String,link1:MyLink):void
		{
			desIp=desIp1;
			link=link1;
			timer.addEventListener(TimerEvent.TIMER, timerHandler); 
			if(switchs){
				timer.start();
			}
		}
		
		
		private var switchs:Boolean=false;
		private function switchNqa(e:SdncEvt):void
		{
			var state:Boolean=e.params;
			switchs=state;
			if(timer.hasEventListener(TimerEvent.TIMER)){
				if(state){
					if(!timer.running){
						timer.start();
					}
				}else{
					if(timer.running){
						timer.stop();
						link.name ="";
					}
				}
			}
		}
		private function timerHandler(e:TimerEvent):void
		{
			nqa = new Nqa();
			nqa.getNqaData(stateNode,desIp,showData);
		}
		private function showData(value:String,bz:Number):void{
			var nqaData:String = "";
			if(bz == 0){
				nqaData = value;
			}else{
//			   ifmData = value;
//			   trace("返回最后算出ifm的数据"+ifmData);
			}
			var str:String="            ";
			if(timer.running){
				if(nqaData != null&&nqaData!=""){
				    str+=nqaData+"ms";
					
				}
				link.name =nqaData!=""?nqaData+"ms":"";
				link.setStyle(Styles.LABEL_COLOR,0xffffff);
			}
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
		
		/********************************流量统计方法********************************************************************/
		private var l:MyLink;
		private var cname:String;
		private var bname:String;
		private var flow_switchs:Boolean=false;
		/********************************新 流量统计--设置数据********************************************************************/
		public function setFlowStaticsData(classifierName:String,link:MyLink):void
		{
			l = link;
			cname = classifierName;
			bname = classifierName+"_action";
		}
		public var isEnabled:Boolean = false;
		public function newFlowStatices():void{
			if(!isEnabled){
				enableBehavior();
			}else{
				getMatchPassBytes();
			}
			
		}
		
		public  function enableBehavior():void{
			var opsIp:String = SdncUtil.opsIp;
			var id:String = stateNode.getClient("id");
			var url:String = "http://"+opsIp+"/devices/"+id+"/qos/qosCbQos/qosBehaviors/qosBehavior?behaviorName="+bname;
			var param:String = "<qosActStats><qosActStat><statFlag>enable</statFlag></qosActStat></qosActStats>";
			connUtil.clientQuery(url,ConnUtil.METHOD_POST,enablePostResult,enablePostFault,param);
			
		}
		
		private function enablePostResult(e:HttpDataEvent):void
		{
			isEnabled = true;
			getMatchPassBytes();
		}
		
		private function enablePostFault(e:Event):void
		{
			trace("使能失败");
		}
		
		private function getMatchPassBytes():void{
			var opsIp:String = SdncUtil.opsIp;
			var id:String = stateNode.getClient("id");
			var url:String = "http://"+opsIp+"/devices/"+id+"/qos/qosGlobalCfgs/qosGlobalPolicyApplys/qosGlobalPolicyApply" +
				"/qosGlobalClassifierStats/qosGlobalClassifierStat?classifierName="+cname+"&behaviorName="+bname;
			trace(url);
			connUtil.clientQuery(url,ConnUtil.METHOD_GET,getMpBytesResult,getMpBytesFault);
		}
		
		private function getMpBytesResult(e:HttpResponseEvent,data:ByteArray):void
		{
			var errorcode:ErrorCodeUtil = new ErrorCodeUtil;
			if(!errorcode.parse(e,data)){
				deviceState = false;
				return;
			}
			var qos:XML=XML(data.toString());
			l.setStyle(Styles.LABEL_COLOR,0xffffff);
			l.name = "0Bytes";
			var index:int=data.toString().indexOf("<qosGlobalCfgs>");
			if(index!=-1){
				var result:String="<qos>"+data.toString().substr(index);
				qos=XML(result);
				for each(var qosGlobalPolicyApply:XML in qos.qosGlobalCfgs.qosGlobalPolicyApplys.qosGlobalPolicyApply){
					var matchPassBytes:Number = qosGlobalPolicyApply.qosGlobalClassifierStats.qosGlobalClassifierStat.matchPassBytes;
					l.name = matchPassBytes+"Bytes";
					if(!SdncUtil.flowStaticsTimer.flowSwitch){
						l.name = "";
					}
					trace("matchPassBytes+++++++++"+matchPassBytes);
					break;
				}
			}
		}
		public function clearFlowData():void
		{
			l.name = "";
		}
		
		private function getMpBytesFault(e:Event):void
		{
		}
		/******************************************************************************/
		public function undoBehavior():void{
			var opsIp:String = SdncUtil.opsIp;
			var id:String = stateNode.getClient("id");
			var url:String = "http://"+opsIp+"/devices/"+id+"/qos/qosCbQos/qosBehaviors/qosBehavior";
			var body:String = "<behaviorName>"+bname+"</behaviorName>"+
				"<qosActStats><qosActStat operation='delete'><statFlag>enable</statFlag></qosActStat></qosActStats>";
			connUtil.clientQuery(url,ConnUtil.METHOD_PUT,undoBehaviorResult,undoBehaviorFault,body,"application/XML");
		}
		
		private function undoBehaviorResult(e:HttpDataEvent):void{
			if(e.bytes.toString().match("ok")){
				isEnabled = false;
				trace("undo使能成功");
			}else{
				trace("undo使能失败");
				trace(e.bytes.toString());
			}
		}
		private function undoBehaviorFault(e:Event):void{
			trace("undo使能失败");
		}

		/*******************************************Rollback********************************************************************/
		public function getCommitId(renFun:Function):void{
			reFun=renFun;
			var opsIp:String = SdncUtil.opsIp;
			var id:String = stateNode.getClient("id");
			var url:String = "http://"+opsIp+"/devices/"+id+"/cfg/checkPointInfos/checkPointInfo/commitId";
			connUtil.clientQuery(url,ConnUtil.METHOD_GET,getCommitIdResult,getCommitIdFault);
		}
		
		private function getCommitIdResult(e:HttpResponseEvent,data:ByteArray):void
		{
			var errorcode:ErrorCodeUtil = new ErrorCodeUtil;
			if(!errorcode.parse(e,data)){
				trace("获取回滚点失败");
				reFun();
				return;
			}
			var cfg:XML=XML(data.toString());
			var index:int=data.toString().indexOf("<checkPointInfos>");
			if(index!=-1){
				var result:String="<cfg>"+data.toString().substr(index);
				cfg=XML(result);
				for each(var checkPointInfo:XML in cfg.checkPointInfos.checkPointInfo)
				{
					commitId = checkPointInfo.commitId;
					var devicename:String = stateNode.getClient("devicename");
					trace(devicename+"回滚点:   "+commitId);
					break;
				}
			}
			if(reFun!=null){
			    reFun();
			}
		}
		
		private function getCommitIdFault(e:Event):void
		{
			trace("回滚点获取失败");
			if(reFun!=null){
				reFun();
			}
		}
		
		public function rollback(renFun:Function):void{
			reFun=renFun;
			var opsIp:String = SdncUtil.opsIp;
			var id:String = stateNode.getClient("id");
			var url:String = "http://"+opsIp+"/devices/"+id+"/_action/cfg/rollbackByCommitId";
			var body:String = "<commitId>"+commitId+"</commitId>";
			connUtil.clientQuery(url,ConnUtil.METHOD_POST,rollbackResult,rollbackFault,body);
		}
		
		private function rollbackResult(e:HttpDataEvent):void
		{
			if(e.bytes.toString().search("ok")!=-1){
				trace("回滚成功");
			}
			if(reFun!=null){
			   reFun();
			}
		}
		
		private function rollbackFault(e:Event):void
		{
			trace("回滚失败");
			if(reFun!=null){
				reFun();
			}
		}
	}
}