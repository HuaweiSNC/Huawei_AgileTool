package com.huawei.sdnc.model
{
	import com.huawei.sdnc.model.qos.QosActRdrIf;
	import com.huawei.sdnc.model.qos.QosBehavior;
	import com.huawei.sdnc.model.qos.QosClassifier;
	import com.huawei.sdnc.model.qos.QosGlobalCfgs;
	import com.huawei.sdnc.model.qos.QosGlobalPolicyApply;
	import com.huawei.sdnc.model.qos.QosPolicy;
	import com.huawei.sdnc.model.qos.QosPolicyNode;
	import com.huawei.sdnc.model.qos.QosRuleAcl;
	import com.huawei.sdnc.model.qos.QosSlotPolicyApply;
	import com.huawei.sdnc.model.qos.QosSlotQoss;
	import com.huawei.sdnc.tools.ConnUtil;
	import com.huawei.sdnc.tools.ErrorCodeUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.gre.dataHandle.DataHandleTool;
	
	import flash.utils.ByteArray;
	
	import org.httpclient.events.HttpDataEvent;
	import org.httpclient.events.HttpErrorEvent;
	import org.httpclient.events.HttpResponseEvent;

	public class Qos extends MetaData
	{
		public function Qos()
		{
			
		}
		public var qosPolicys:Array=[];
		public var qosClassifiers:Array=[];
		public var qosBehaviors:Array=[];
		public var qosSlotQoss:QosSlotQoss;
		public var qosGlobalCfgs:QosGlobalCfgs;
		public var device:Device;
		
		private var reFun:Function;
		public function getQos(qosIsReady:Function):void
		{
			reFun=qosIsReady;
			var opsIp:String=SdncUtil.opsIp;
			var id:String=stateNode.getClient("id");
			device = stateNode.getClient("device");
			var qosUrl:String="http://"+opsIp+"/devices/"+id+"/qos";
			connUtil.clientQuery(qosUrl,ConnUtil.METHOD_GET,onGetResult,onGetFault);
		}
		
		private function onGetResult(e:HttpResponseEvent,data:ByteArray):void
		{
			var errorcode:ErrorCodeUtil = new ErrorCodeUtil;
			if(!errorcode.parse(e,data)){
				device.deviceState = false;
				DataHandleTool.showOnConsole(errorcode.printErrorMessage(stateNode, "   getQos失败\n"));
				reFun();
				return;
			}
			try{
				qosPolicys=[];
				qosClassifiers=[];
				qosBehaviors=[];
				var ip:String=stateNode.getClient("ip");
				qosSlotQoss = new QosSlotQoss();
				qosGlobalCfgs = new QosGlobalCfgs();
				var index:int=data.toString().indexOf("<qosCbQos>");
				var qos:XML=XML(data.toString());
				if(index!=-1){
					//data.toString().replace(/xmlns(.*?)="(.*?)"/gm, "");
					var result:String="<qos> "+data.toString().substr(index);
					qos=XML(result);
					
//					for each(var qosPolicy:XML in qos.qosCbQos.qosPolicys.qosPolicy){
//						var qosPolicyC:QosPolicy=new QosPolicy;
//						qosPolicyC.policyName=qosPolicy.policyName;
//						qosPolicyC.description=qosPolicy.description;
//						qosPolicyC.step=qosPolicy.step;
//						qosPolicyC.shareMode=qosPolicy.shareMode;
//						qosPolicyC.statFlag=qosPolicy.statFlag;
//						qosPolicys.push(qosPolicyC);
//						for each(var qosPolicyNodeX:XML in qosPolicy.qosPolicyNodes.qosPolicyNode){
//							var qosPolicyNode:QosPolicyNode=new QosPolicyNode;
//							qosPolicyNode.behaviorName=qosPolicyNodeX.behaviorName;
//							qosPolicyNode.classifierName=qosPolicyNodeX.classifierName;
//							qosPolicyNode.priority=qosPolicyNodeX.qosPolicyNode;
//							qosPolicyC.qosPolicyNodes.push(qosPolicyNode);
//						}
//					}
//					for each(var qosClassifier:XML in qos.qosCbQos.qosClassifiers.qosClassifier){
//						var qosClassifierC:QosClassifier=new QosClassifier;
//						qosClassifierC.classifierName=qosClassifier.classifierName;
//						qosClassifierC.description=qosClassifier.description;
//						qosClassifierC.operator=qosClassifier.operator;
//						for each(var qosRuleAcl:XML in qosClassifier.qosRuleAcls.qosRuleAcl){
//							var qosRule:QosRuleAcl=new QosRuleAcl;
//							qosRule.classifierID=qosRuleAcl.classifierID;
//							qosRule.aclName=qosRuleAcl.aclName;
//							qosClassifierC.qosRuleAcls.push(qosRule);
//						}
//						qosClassifiers.push(qosClassifierC);
//					}
//					for each(var qosBehavior:XML in qos.qosCbQos.qosBehaviors.qosBehavior){
//						var qosBehaviorC:QosBehavior=new QosBehavior;
//						qosBehaviorC.behaviorName=qosBehavior.behaviorName;
//						qosBehaviorC.description=qosBehavior.description;
//						for each(var qosActRdrIf:XML in qosBehavior.qosActRdrIfs.qosActRdrIf){
//							var qosActRdrIfC:QosActRdrIf = new QosActRdrIf();
//							qosActRdrIfC.ifName = qosActRdrIf.ifName;
//							qosActRdrIfC.ifName = qosActRdrIfC.ifName.replace(" ", "");
//							qosActRdrIfC.actionType = qosActRdrIf.actionType;
//							qosBehaviorC.qosActRdrIfs.push(qosActRdrIfC);
//						}
//						qosBehaviors.push(qosBehaviorC);
//					}
					
					for each(var qosSlotPolicyApply:XML in qos.qosSlotQoss.qosSlotPolicyApplys.qosSlotPolicyApply){
						var qosSlotPolicyApplyC:QosSlotPolicyApply = new QosSlotPolicyApply();
						qosSlotPolicyApplyC.policyName = qosSlotPolicyApply.policyName;
						qosSlotPolicyApplyC.slotId = qosSlotPolicyApply.slotId;
						qosSlotPolicyApplyC.direction = qosSlotPolicyApply.direction;
						qosSlotQoss.qosSlotPolicyApplys.push(qosSlotPolicyApplyC);
					}
					
					
					for each(var qosGlobalPolicyApply:XML in qos.qosGlobalCfgs.qosGlobalPolicyApplys.qosGlobalPolicyApply){
						if(qosGlobalPolicyApply==null)
							break;
						var qosGlobalPolicyApplyC:QosGlobalPolicyApply = new QosGlobalPolicyApply();
						qosGlobalPolicyApplyC.policyName = qosGlobalPolicyApply.policyName;
						qosGlobalPolicyApplyC.direction = qosGlobalPolicyApply.direction;
						qosGlobalCfgs.qosGlobalPolicyApplys.push(qosGlobalPolicyApplyC);
					}
					
					trace("设备"+ip+"   getQos成功");
					DataHandleTool.showOnConsole("设备"+ip+"   getQos成功\n");
				}else if(data.toString()==""){
					DataHandleTool.showOnConsole("设备"+ip+"   Qos无数据\n");
					trace("设备"+ip+"   Qos没有数据");
				}else{
					trace("设备"+ip+"   getQos失败");
					DataHandleTool.showOnConsole("设备"+ip+"   getQos失败\n");
					device.deviceState = false;
				}
				
				if(reFun!=null){
					reFun();
				}
			}catch(e:*){
				DataHandleTool.showOnConsole("设备"+ip+"   getQos失败\n");
				if(reFun!=null){
					device.deviceState = false;
					reFun();
				}
			}
		}
		
		
		private function onGetFault(e:*):void
		{
			var ip:String=stateNode.getClient("ip");
			device = stateNode.getClient("device");
			device.deviceState = false;
			trace("设备"+ip+"   getQos失败");
			DataHandleTool.showOnConsole("设备"+ip+"   getQos失败\n");
			device.deviceState = false;
			if(reFun!=null){
				reFun();
			}
		}
		public function delQosPolicy(policyName:String,renFun:Function):void
		{
			reFun=renFun;
			var opsIp:String=SdncUtil.opsIp;
			var id:String=stateNode.getClient("id");
			var qosDelUrl:String="http://"+opsIp+"/devices/"+id+"/qos/qosCbQos/qosPolicys/qosPolicy?policyName="+policyName;
			connUtil.clientQuery(qosDelUrl,ConnUtil.METHOD_DELETE,onDelResult,onDelDefault);
		}
		public function onDelResult(e:HttpDataEvent):void
		{
			trace(e.bytes.toString());
			reFun();
		}
		private function onDelDefault(e:HttpErrorEvent):void
		{
			reFun();
		}
	}
}