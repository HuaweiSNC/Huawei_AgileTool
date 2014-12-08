package com.huawei.sdnc.model
{
	import com.huawei.sdnc.model.acl.AclGroup;
	import com.huawei.sdnc.model.acl.aclRuleBas4.AclRuleAdv4;
	import com.huawei.sdnc.tools.ConnUtil;
	import com.huawei.sdnc.tools.ErrorCodeUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.ipCore_DCI.dataHandle.DataHandleTool;
	
	import flash.events.Event;
	import flash.utils.ByteArray;
	
	import mx.collections.ArrayList;
	
	import org.httpclient.events.HttpErrorEvent;
	import org.httpclient.events.HttpResponseEvent;

	public class Acl extends MetaData
	{
		public function Acl()
		{
		}
		public var aclGroups:Array;
		private var reFun:Function;
		public var aclNumOrNames:Array;
		public var device:Device;
		public function getAcl(aclIsReady:Function):void
		{
			reFun=aclIsReady;
			var opsIp:String=SdncUtil.opsIp;
			var id:String=stateNode.getClient("id");
			device = stateNode.getClient("device");
			var aclUrl:String="http://"+opsIp+"/devices/"+id+"/acl";
			connUtil.clientQuery(aclUrl,ConnUtil.METHOD_GET,onGetResult,onGetAclFault);
		}
		
		private function onGetResult(e:HttpResponseEvent,data:ByteArray):void
		{
			var errorcode:ErrorCodeUtil = new ErrorCodeUtil;
			if(!errorcode.parse(e,data)){
				device.deviceState = false;
				DataHandleTool.showOnConsole(errorcode.printErrorMessage(stateNode, "   getAcl失败\n"));
				reFun();
				return;
			}
			try{
				aclNumOrNames=[];
				aclGroups=[];
				var ip:String=stateNode.getClient("ip");
				var index:int=data.toString().indexOf("<aclGroups>");
				trace("返回码"+e.response.code);
				if(index!=-1){
					var acl:XML=XML(data.toString());
					//data.toString().replace(/xmlns(.*?)="(.*?)"/gm, "");
					var result:String="<acl> "+data.toString().substr(index);
					acl=XML(result);
					
					for each(var aclGroup:XML in acl.aclGroups.aclGroup){
					var ag:AclGroup=new AclGroup;
					var aclNoN:Number=Number(aclGroup.aclNumOrName);
					if(isNaN(aclNoN)||!(aclNoN>=3000&&aclNoN<4000)){
						continue;
					}
					ag.aclNumOrName=aclGroup.aclNumOrName;
					aclNumOrNames.push(aclNoN);
					
					ag.aclStep=aclGroup.aclStep;
					ag.aclDescription=aclGroup.aclDescription;
					ag.aclType=aclGroup.aclType;
					for each(var aclRuleAdv4:XML in aclGroup.aclRuleAdv4s.aclRuleAdv4){
					        var aclRuleAdv:AclRuleAdv4=new AclRuleAdv4;
					        aclRuleAdv.aclRuleName=aclRuleAdv4.aclRuleName;
							
							aclRuleAdv.aclRuleID=aclRuleAdv4.aclRuleID;
							aclRuleAdv.aclAction=aclRuleAdv4.aclAction;
							aclRuleAdv.aclProtocol=aclRuleAdv4.aclProtocol;
							aclRuleAdv.aclSourceIp=aclRuleAdv4.aclSourceIp;
							aclRuleAdv.aclSrcWild=aclRuleAdv4.aclSrcWild;
							aclRuleAdv.aclDestIp=aclRuleAdv4.aclDestIp;
							aclRuleAdv.aclDestWild=aclRuleAdv4.aclDestWild;
							
							aclRuleAdv.aclSrcPortOp=aclRuleAdv4.aclSrcPortOp;
							aclRuleAdv.aclDestPortOp=aclRuleAdv4.aclDestPortOp;
							ag.aclRuleAdv4s.push(aclRuleAdv);
						}
						aclGroups.push(ag);
					}
					
					DataHandleTool.showOnConsole("设备"+ip+"   getAcl成功\n");
					trace("设备"+ip+"   getAcl成功");
				}else if(data.toString()==""){
					DataHandleTool.showOnConsole("设备"+ip+"   Acl无数据\n");
					trace("设备"+ip+"   getAcl没有数据");
				}else{
					DataHandleTool.showOnConsole("设备"+ip+"   getAcl失败\n");
					trace("设备"+ip+"   getAcl失败");
					device.deviceState = false;
				}
				
				if(reFun!=null){
					reFun();
				}
					
			}catch(e:Event){
				trace("getAcl异常:"+e.toString());
				DataHandleTool.showOnConsole("设备"+ip+"   getAcl失败\n");
				device.deviceState = false;
				if(reFun!=null){
					reFun();
				}
			}
		}
		
		private function onGetAclFault(e:*):void
		{
			var ip:String=stateNode.getClient("ip");
			if(DataHandleTool.console!=null){
				var content:String="设备"+ip+"   getAcl出错\n";
				DataHandleTool.console.console.text+=content;
			}
			device = stateNode.getClient("device");
			device.deviceState = false;
			trace("设备"+ip+"   getAcl失败");
			if(reFun!=null){
				reFun();
			}
		}
	}
}