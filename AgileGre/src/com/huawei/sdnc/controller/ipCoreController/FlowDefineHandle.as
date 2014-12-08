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
	import com.huawei.sdnc.tools.PopupManagerUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.common.node.StateNode;
	import com.huawei.sdnc.view.ipCore_DCI.AddFlowPolicy;
	import com.huawei.sdnc.view.ipCore_DCI.FlowDefine;
	import com.huawei.sdnc.view.ipCore_DCI.dataHandle.DataHandleTool;
	
	import mx.controls.Alert;
	import mx.events.CloseEvent;
	import mx.managers.PopUpManager;
	
	import org.httpclient.events.HttpDataEvent;
	
	import twaver.IData;

	public class FlowDefineHandle
	{
		public var flowDefine:FlowDefine;
		public var flow_devices:Array;
		public var addFlowPolicy:AddFlowPolicy;
		
		private var alertContent:String="";
		private var commitnum:int = 0;
		private var devicenum:int;
		private var __app:sdncui2;
		public function FlowDefineHandle()
		{
			__app=SdncUtil.app;
		}
		
		/**
		 *创建流策略之前获取回滚点ID 
		 * 
		 */		
		public function getCommitRollbackId():void{
			PopupManagerUtil.getInstence().popupLoading(__app.ipcore.physicsView);
			devicenum=flow_devices.length;
			for each(var deviceobj:Object in flow_devices){
				var stateNode:StateNode = deviceobj["stateNode"];
				var device:Device = stateNode.getClient("device");
				device.getCommitId(callback);
			}
		}
		private function callback():void
		{
			commitnum++;
			if(commitnum == devicenum){
				saveFlow();
			}
		}
		
		public function saveFlow():void
		{
			for each(var flow_device1:Object in flow_devices){
				var stateNode1:StateNode=flow_device1["stateNode"];
				var device:Device=stateNode1.getClient("device");
				//获取aclNumOrName
				var curAclNumOrName:String=DataHandleTool.getAclNumOrName(device.acl.aclNumOrNames,3000);
				if(curAclNumOrName == "0"){
					Alert.show("设备"+device.stateNode.getClient("deviceName")+"没有可用的aclNumOrName","提示",Alert.OK,__app.ipcore.physicsView);
					PopupManagerUtil.getInstence().closeLoading();
					return;
				}
				
				
				//第一步 post acl
				var aclGroup:AclGroup=new AclGroup;
				aclGroup.aclNumOrName=curAclNumOrName.toString();
				flow_device1["curAclNumOrName"]=curAclNumOrName.toString();
				var aclRuleAdv4:AclRuleAdv4=new AclRuleAdv4;
				aclRuleAdv4.aclProtocol=flow_device1["xType"];
				aclRuleAdv4.aclSourceIp=flow_device1["srcIp"];
				aclRuleAdv4.aclSrcPortOp=flow_device1["srcPop"];
				aclRuleAdv4.aclSrcWild=flow_device1["aclSrcWild"];
				aclRuleAdv4.aclDestIp=flow_device1["desIp"];
				aclRuleAdv4.aclDestPortOp=flow_device1["desPop"];
				aclRuleAdv4.aclDestWild=flow_device1["aclDestWild"];
				aclGroup.aclRuleAdv4s.push(aclRuleAdv4);
				device.postAclGroup(aclGroup,onPostAclGroup,flow_device1);
			}
		}
		
		//postACL成功后调用  post  qosClassifier
		private function onPostAclGroup(e:*,flow_device:Object):void
		{
			var sn:StateNode=flow_device["stateNode"];
			var ip:String=sn.getClient("ip");	
			var stateNode:StateNode=flow_device["stateNode"];
			var device:Device=stateNode.getClient("device");
			if(e is HttpDataEvent){
				var message:String=e.bytes.toString();
				if(message.search("rpc-error")==-1){
					trace("postACL Success "+ip+" :"+e.bytes.toString());
					DataHandleTool.showOnConsole("postACL Success "+ip+" :"+e.bytes.toString()+"\n");
//					device.getAcl(false,function():void{
					var qosClassifier:QosClassifier=new QosClassifier;
					qosClassifier.classifierName=flow_device["classifierName"];
					var qosRuleAcl:QosRuleAcl=new QosRuleAcl;
					qosRuleAcl.aclName=flow_device["curAclNumOrName"];
					qosClassifier.qosRuleAcls.push(qosRuleAcl);
					device.postQosClassifier(qosClassifier,onPostQosClassifier,flow_device);
//					});
				}else{
					trace("postACL 失败 "+ip+" :"+e.bytes.toString());
					DataHandleTool.showOnConsole("postACL 失败 "+ip+" :"+e.bytes.toString()+"\n");
					var str:String=ip+" postAcl出错";
					alertContent+=str;
//					device.getAcl(false,function():void{
						onPostQosPolicy();
//					}
//					);
				}
			}else{
				trace("postACL 失败 "+ip+" :"+e.bytes.toString());
				DataHandleTool.showOnConsole("postACL 失败 "+ip+" :"+e.bytes.toString()+"\n");
				var str1:String=ip+" postAcl出错";
				alertContent+=str;
				onPostQosPolicy();
			}
		}
		/**
		 * post流分类的回调方法  再post流行为
		 * */
		private function onPostQosClassifier(e:*,flow_device:Object):void
		{
			var sn:StateNode=flow_device["stateNode"];
			var ip:String=sn.getClient("ip");
			if(e is HttpDataEvent){
				var message:String=e.bytes.toString();
				var stateNode:StateNode=flow_device["stateNode"];
				var device:Device=stateNode.getClient("device");
				if(message.search("rpc-error")==-1){
					trace("postQosClassifier Success"+ip+":"+message);
					DataHandleTool.showOnConsole("postQosClassifier Success"+ip+":"+message+"\n");
					var qosBehavior:QosBehavior=new QosBehavior;
					qosBehavior.behaviorName=flow_device["behaviorName"];
					var qosActRdrIf:QosActRdrIf=new QosActRdrIf;
					qosActRdrIf.ifName=flow_device["tnlName"];
					qosBehavior.qosActRdrIfs.push(qosActRdrIf);
					device.postQosBehavior(qosBehavior,onPostQosBehavior,flow_device);
				}else{
					trace("postQosClassifier 失败"+ip+":"+message);
					var str:String=ip+" PostQosClassifie出错\n";
					DataHandleTool.showOnConsole(str);
					alertContent+=str;
					onPostQosPolicy();
				}
			}else{
				trace("postQosClassifier 失败"+ip+":"+message);
				var str0:String=ip+" PostQosClassifie出错\n";
				DataHandleTool.showOnConsole(str0);
				alertContent+=str0;
				onPostQosPolicy();
			}
			
			
		}
		/**
		 * post流行为之后 post流策略
		 * */
		private function onPostQosBehavior(e:HttpDataEvent,flow_device:Object):void
		{
			var sn:StateNode=flow_device["stateNode"];
			var ip:String=sn.getClient("ip");
			var message:String=e.bytes.toString();
			var stateNode:StateNode=flow_device["stateNode"];
			var device:Device=stateNode.getClient("device");
			
			//找到globalPolicy的标志
			var find:Boolean=false;
			
			//查找全局策略名字
			var globalPolicyName:String="";
			var qosGlobalCfgs:QosGlobalCfgs=device.qos.qosGlobalCfgs;
			if(qosGlobalCfgs!=null){
				var qosGlobalPolicyApplys:Array=qosGlobalCfgs.qosGlobalPolicyApplys;
				if(qosGlobalPolicyApplys!=null&&qosGlobalPolicyApplys.length>=1){
					globalPolicyName=(qosGlobalPolicyApplys[0] as QosGlobalPolicyApply).policyName;
					find=true;
					if(message.search("rpc-error")==-1){
						trace("postQosBehavior Success"+ip+":"+message);
						DataHandleTool.showOnConsole("postQosBehavior Success"+ip+":"+message+"\n");
						var qosPolicy:QosPolicy=new QosPolicy;
						qosPolicy.policyName=globalPolicyName;
						var qosPolicyNode:QosPolicyNode=new QosPolicyNode;
						qosPolicyNode.behaviorName=flow_device["behaviorName"];
						qosPolicyNode.classifierName=flow_device["classifierName"];
						qosPolicy.qosPolicyNodes.push(qosPolicyNode);
						device.postQosPolicy(qosPolicy,onPostQosPolicy,flow_device);
					}else{
						trace("postQosBehavior 失败"+ip+":"+message);
						DataHandleTool.showOnConsole("postQosBehavior 失败"+ip+":"+message+"\n");
						var str:String=ip+"PostQosBehavior出错\n";
						alertContent+=str;
						onPostQosPolicy();
					}
				}
			}
			
			
			//如果没有找到
			if(!find){
				if(message.search("rpc-error")==-1){
					//如果没有globalPolicy 就新建一个空的
					device.createGlobalPolicy(function(e:HttpDataEvent):void{
						trace("createGlobalPolicy的返回信息:     "+e.bytes);
						if(e.bytes.toString().search("rpc-error")==-1){
							var qosPolicy1:QosPolicy=new QosPolicy;
							qosPolicy1.policyName="gp";
							var qosPolicyNode1:QosPolicyNode=new QosPolicyNode;
							qosPolicyNode1.behaviorName=flow_device["behaviorName"];
							qosPolicyNode1.classifierName=flow_device["classifierName"];
							qosPolicy1.qosPolicyNodes.push(qosPolicyNode1);
							device.postQosPolicy(qosPolicy1,onPostQosPolicy,flow_device);
						}
					});
				}else{
					trace("postQosBehavior 失败"+ip+":"+message);
					var str2p:String=ip+"PostQosBehavior出错\n";
					DataHandleTool.showOnConsole(str2p);
					alertContent+=str2p;
					onPostQosPolicy();
				}
			}
		}
		private var hasSavedNum:int=0;
		private var hasInitNum:int=0;
		
		private var aclnum:int=0;
		private var qosnum:int=0;
		private function onPostQosPolicy():void
		{
			hasSavedNum++;
			if(hasSavedNum==devicenum){
				//流策略下发完成后，刷新数据
				for each(var obj:Object in flow_devices){
					var stateNode:StateNode=obj["stateNode"];
					var device:Device=stateNode.getClient("device");
					device.getQos(false,function():void{
						qosnum++;
						isToComplete();
					});
					device.getAcl(false,function():void{
						aclnum++;
						isToComplete();
					});
				}
			}
		}
		private function isToComplete():void
		{
			if(aclnum==devicenum&&qosnum==devicenum){
				if(alertContent==""){
					Alert.show("处理完成!","提示",Alert.OK,flowDefine,closeFunction);
				}
				else{
//					Alert.show(alertContent,"提示",Alert.OK,flowDefine,closeFunction);
					roolback();
				}
					
			}
		}
		
		public function closeFunction(e:CloseEvent):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			flowDefine.visible=true;
			flowDefine.refreshPolicyList();
			PopUpManager.removePopUp(addFlowPolicy);
		}
		private function roolback():void
		{
			var i:int=0;
			for each(var flow_device1:Object in flow_devices){
				var stateNode1:StateNode=flow_device1["stateNode"];
				var device:Device=stateNode1.getClient("device");
				device.rollback(function():void{
					i++;
					if(i==devicenum){
						Alert.show(alertContent,"提示",Alert.OK,flowDefine,closeFunction);
					}
				});
			}
		}
	}
}