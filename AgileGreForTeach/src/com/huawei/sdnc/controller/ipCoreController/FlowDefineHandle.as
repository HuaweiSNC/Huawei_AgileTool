package com.huawei.sdnc.controller.ipCoreController
{
	import com.huawei.sdnc.model.Device;
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
	import com.huawei.sdnc.tools.PopupManagerUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.common.node.StateNode;
	import com.huawei.sdnc.view.gre.bussView.CreateFlow;
	import com.huawei.sdnc.view.gre.bussView.FlowListView;
	import com.huawei.sdnc.view.gre.dataHandle.DataHandleTool;
	
	import flash.utils.ByteArray;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.events.CloseEvent;
	import mx.managers.PopUpManager;
	
	import org.httpclient.events.HttpDataEvent;
	import org.httpclient.events.HttpResponseEvent;
	
	import twaver.IData;
	
	public class FlowDefineHandle
	{
		public var flowDefine:FlowListView;
		public var flow_devices:Array;
		public var addFlowPolicy:CreateFlow;
		
		private var alertContent:String="";
		private var commitnum:int = 0;
		private var devicenum:int;
		private var __app:sdncui2;
		public var connUtil:ConnUtil = ConnUtil.getInstence();
		public function FlowDefineHandle()
		{
			__app=SdncUtil.app;
		}
		
		public function saveFlow():void
		{
			PopupManagerUtil.getInstence().popupLoading(__app.ipcore.physicsView);
			var param:String="<servicflow><flows><flow>";
			param+="<flowName>"+flow_devices[0]["policyName"]+"</flowName>";
			param+="<filterType></filterType>";
			param+="<filterTemplateType></filterTemplateType>";
			param+="<flowFilter>";
			param+="<sourceIP>"+flow_devices[0]["srcIp"]+"</sourceIP>";
			param+="<destinationIP>"+flow_devices[0]["desIp"]+"</destinationIP>";
			param+="<sourcePrefix></sourcePrefix>";
			param+="<destinationPrefix></destinationPrefix>";
			param+="<sourcePort>"+flow_devices[0]["srcPop"]+"</sourcePort>";
			param+="<destinationPort>"+flow_devices[0]["desPop"]+"</destinationPort>";
			param+="<sourcemask>"+flow_devices[0]["aclSrcWild"]+"</sourcemask>";
			param+="<destinationmask>"+flow_devices[0]["aclDestWild"]+"</destinationmask>";
			param+="<protocolId>"+flow_devices[0]["xType"]+"</protocolId>";
			param+="<as></as>";
			param+="<vlanId></vlanId>";
			param+="<vlanPriority></vlanPriority>";
			param+="</flowFilter></flow></flows><flowPolicys><flowPolicy>";
			param+="<policyName>"+flow_devices[0]["policyName"]+"</policyName>";
			param+="<flowName>"+flow_devices[0]["policyName"]+"</flowName>";
			param+="<actions>";
			
			for each(var flow_device1:Object in flow_devices){
				var stateNode1:StateNode=flow_device1["stateNode"];
				var device:Device=stateNode1.getClient("device");
				param+="<action><nodeId>"+device.stateNode.getClient("id")+"</nodeId><ifName>"+flow_device1["tnlName"]+"</ifName></action>"
				
			}
			param+="</actions><tpType></tpType><tpId></tpId></flowPolicy></flowPolicys></servicflow>";
			postflow(param)
		}
		/**********************************************post flow(acl qos policy)********************************************************/
		public var v1:Number
		public var v2:Number
		public function postflow(param:String):void
		{
			v1 = new Date().time
			var opsIp:String=SdncUtil.serviceIp;
			var postflowUrl:String="http://"+opsIp+"/AgileGre/rest/serviceflow";
			connUtil.clientQuery(postflowUrl,ConnUtil.METHOD_POST,onPostFlowGroup,onPostflowFault,param,"application/xml","Boolean ret = servicflow.create()");
		}
		
		private function onPostFlowGroup(e:HttpDataEvent):void
		{
			
			var message:String=e.bytes.toString();
			v2 = new Date().time
			if(message.search("ok")!=-1){
				onPostQosPolicy();
			}
			ServiceTool.api = "serviceflow";
			ServiceTool.message = message;
			ServiceTool.usetime = (v2-v1)+"ms";
			ServiceTool.curtime = SdncUtil.getFormatStr("MM-DD  JJ:NN",new Date())
			PopupManagerUtil.getInstence().closeLoading();
			
		}
		private function onPostflowFault(e:*):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			onPostQosPolicy();
		}
		
		
		private var hasSavedNum:int=0;
		private var hasInitNum:int=0;
		
		private var aclnum:int=0;
		private var qosnum:int=0;
		private var flownum:int=0;
		private function onPostQosPolicy():void
		{
				for each(var obj:Object in flow_devices){
					var stateNode:StateNode=obj["stateNode"];
					var device:Device=stateNode.getClient("device");
					device.getflow(false,function():void{
						flownum++;
						isToComplete();
					});
				}
		}
		private function isToComplete():void
		{
			trace(flownum+"=========="+devicenum)
			if(flownum==flow_devices.length){
				Alert.show("处理完成!","提示",Alert.OK,flowDefine,closeFunction);
			}
		}
		
		public function closeFunction(e:CloseEvent):void
		{
			PopupManagerUtil.getInstence().closeLoading();
//			flowDefine.visible=true;
			flowDefine.element = addFlowPolicy.element;
			flowDefine.refreshPolicyList();
			addFlowPolicy.putempty();
			PopUpManager.removePopUp(addFlowPolicy);
		}
	}
}