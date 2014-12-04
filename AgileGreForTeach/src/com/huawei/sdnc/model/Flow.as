package com.huawei.sdnc.model
{
	import com.huawei.sdnc.model.acl.AclGroup;
	import com.huawei.sdnc.model.acl.aclRuleBas4.AclRuleAdv4;
	import com.huawei.sdnc.model.qos.QosActRdrIf;
	import com.huawei.sdnc.model.qos.QosBehavior;
	import com.huawei.sdnc.model.qos.QosClassifier;
	import com.huawei.sdnc.model.qos.QosPolicy;
	import com.huawei.sdnc.model.qos.QosPolicyNode;
	import com.huawei.sdnc.model.qos.QosRuleAcl;
	import com.huawei.sdnc.tools.ConnUtil;
	import com.huawei.sdnc.tools.ErrorCodeUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.gre.dataHandle.DataHandleTool;
	
	import flash.events.Event;
	import flash.utils.ByteArray;
	
	import mx.collections.ArrayList;
	
	import org.httpclient.events.HttpErrorEvent;
	import org.httpclient.events.HttpResponseEvent;

	public class Flow extends MetaData
	{
		public function Flow()
		{
		}
		private var reFun:Function;
		public var device:Device;
		public function getFlow(aclIsReady:Function):void
		{
			reFun=aclIsReady;
			var opsIp:String=SdncUtil.serviceIp;
			var id:String=stateNode.getClient("id");
			device = stateNode.getClient("device");
			var getflowUrl:String="http://"+opsIp+"/AgileGre/rest/devices/"+id+"/serviceflow";
			connUtil.clientQuery(getflowUrl,ConnUtil.METHOD_GET,onGetFlowGroup,onGetFlowFault);
		}
		private function onGetFlowGroup(e:HttpResponseEvent,data:ByteArray):void
		{
			var errorcode:ErrorCodeUtil = new ErrorCodeUtil;
			if(!errorcode.parse(e,data)){
				device.deviceState = false;
				DataHandleTool.showOnConsole(errorcode.printErrorMessage(stateNode, "   getflow失败\n"));
				reFun();
				return;
			}
			try{
				device.acl.aclGroups=[];
				device.acl.aclNumOrNames=[];
				device.qos.qosPolicys=[];
				device.qos.qosClassifiers=[];
				device.qos.qosBehaviors=[];
				
				var ip:String=stateNode.getClient("ip");
				trace("返回码"+e.response.code);
				if(e.response.code=="200"){
					var flow:XML=new XML(data.toString());
					trace(flow)
					for each(var aclGroup:XML in flow.flows.flow){
						var ag:AclGroup=new AclGroup;
						var aclNoN:Number=Number(aclGroup.aclNumOrName);
						if(isNaN(aclNoN)||!(aclNoN>=3000&&aclNoN<4000)){
							continue;
						}
						ag.aclNumOrName=aclGroup.aclNumOrName;
						device.acl.aclNumOrNames.push(aclNoN);
						ag.aclStep="5";
						ag.aclType="Advance";
						var aclRuleAdv:AclRuleAdv4=new AclRuleAdv4;
						aclRuleAdv.aclRuleName="ruleName"+aclGroup.aclNumOrName;
						aclRuleAdv.aclProtocol=aclGroup.protocolId;
						aclRuleAdv.aclSourceIp=aclGroup.flowFilter.sourceIP;
						aclRuleAdv.aclSrcWild=aclGroup.flowFilter.sourcemask;
						aclRuleAdv.aclDestIp=aclGroup.flowFilter.destinationIP;
						aclRuleAdv.aclDestWild=aclGroup.flowFilter.destinationmask;
						
						aclRuleAdv.aclSrcPortOp=aclGroup.flowFilter.sourcePort;
						aclRuleAdv.aclDestPortOp=aclGroup.flowFilter.destinationPort;
						ag.aclRuleAdv4s.push(aclRuleAdv);
						device.acl.aclGroups.push(ag);
					}
					for each(var qosPolicy:XML in flow.flowPolicys.flowPolicy){
						var qosPolicyC:QosPolicy=new QosPolicy;
						qosPolicyC.policyName="gp";
						qosPolicyC.description=qosPolicy.description;
						qosPolicyC.step=qosPolicy.step;
						qosPolicyC.shareMode=qosPolicy.shareMode;
						qosPolicyC.statFlag=qosPolicy.statFlag;
						var qosPolicyNode:QosPolicyNode=new QosPolicyNode;
						qosPolicyNode.behaviorName=qosPolicy.classifierName+"_action";
						qosPolicyNode.classifierName=qosPolicy.policyName;
						qosPolicyC.qosPolicyNodes.push(qosPolicyNode);
						device.qos.qosPolicys.push(qosPolicyC);
					}
					
					for each(var qosClassifier:XML in flow.flowPolicys.flowPolicy){
						var qosClassifierC:QosClassifier=new QosClassifier;
						qosClassifierC.classifierName=qosClassifier.policyName;
						qosClassifierC.description=qosClassifier.description;
						qosClassifierC.operator="and";
						var qosRule:QosRuleAcl=new QosRuleAcl;
						qosRule.aclName=qosClassifier.aclName;
						qosClassifierC.qosRuleAcls.push(qosRule);
						device.qos.qosClassifiers.push(qosClassifierC);
					}
					for each(var qosBehavior:XML in flow.flowPolicys.flowPolicy){
						var qosBehaviorC:QosBehavior=new QosBehavior;
						qosBehaviorC.behaviorName=qosBehavior.policyName+"_action";
						qosBehaviorC.description=qosBehavior.description;
						var qosActRdrIfC:QosActRdrIf = new QosActRdrIf();
						qosActRdrIfC.ifName = qosBehavior.actions.action.ifName;
						qosActRdrIfC.actionType = "redirectIf";
						qosBehaviorC.qosActRdrIfs.push(qosActRdrIfC);
						device.qos.qosBehaviors.push(qosBehaviorC);
					}
					DataHandleTool.showOnConsole("设备"+ip+"   getflow成功\n");
					trace("设备"+ip+"   getflow成功");
				}else{
					DataHandleTool.showOnConsole("设备"+ip+"   getflow失败\n");
					trace("设备"+ip+"   getflow失败");
					device.deviceState = false;
				}
				if(reFun!=null){
					reFun();
				}
			}catch(e:Event){
				trace("getflow异常:"+e.toString());
				DataHandleTool.showOnConsole("设备"+device.ip+"   getflow失败\n");
				device.deviceState = false;
				if(reFun!=null){
					reFun();
				}
			}
		}
		private function onGetFlowFault(e:*):void
		{
			var ip:String=stateNode.getClient("ip");
			if(DataHandleTool.console!=null){
				var content:String="设备"+ip+"   getflow出错\n";
				DataHandleTool.console.console.text+=content;
			}
			device = stateNode.getClient("device");
			device.deviceState = false;
			trace("设备"+ip+"   geflow失败");
			if(reFun!=null){
				reFun();
			}
		}
	}
}