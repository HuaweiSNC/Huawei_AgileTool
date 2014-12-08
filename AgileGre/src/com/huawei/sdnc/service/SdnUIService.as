package com.huawei.sdnc.service
{
	import com.huawei.sdnc.controller.ipCoreController.IpCoreForPhysicsCtrl;
	import com.huawei.sdnc.tools.ConnUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.common.node.StateNode;
	
	import flash.events.TimerEvent;
	import flash.utils.ByteArray;
	import flash.utils.Timer;
	
	import mx.rpc.events.FaultEvent;
	
	import org.httpclient.events.HttpDataEvent;
	import org.httpclient.events.HttpErrorEvent;
	import org.httpclient.events.HttpResponseEvent;

	public class SdnUIService
	{
		public var stateNode:StateNode;
		private var projectType:String;
		public static var ipcorePhysicsCtrl:IpCoreForPhysicsCtrl;
		[Bindable]
		public var connUtil:ConnUtil = ConnUtil.getInstence();
		public function SdnUIService()
		{
			projectType = SdncUtil.cuProjectType;
		}
		/**
		 *查询gre 
		 * @param url
		 * 
		 */		
		public function queryGreData(url:String):void
		{
			if(projectType != "test")
			{
				onClientQuery(url,onResultGre,onGreFault,ConnUtil.METHOD_GET);
			}
		}
		/**
		 * 
		 * @param e
		 * @param data
		 * 
		 */		
		public  function onResultGre(e:HttpResponseEvent,data:ByteArray):void
		{
			var gre:XML=XML(data.toString());
			var index:int=data.toString().indexOf("<greTunnels>");
			//data.toString().replace(/xmlns(.*?)="(.*?)"/gm, "");
			if(index==-1){
				ipcorePhysicsCtrl.greInitNum++;
			}else{
				var result:String="<gre> "+data.toString().substr(index);
				gre=XML(result);
				stateNode.setClient("greXml",gre);
				ipcorePhysicsCtrl.greInitNum++;
			}
		}
		
		public function queryQosData(url:String):void
		{
			if(projectType != "test")
			{
				onClientQuery(url,onResultQos,onQosFault,ConnUtil.METHOD_GET);
			}
		}
		
		public  function onResultQos(e:HttpResponseEvent,data:ByteArray):void
		{
			var qos:XML=XML(data.toString());
			var index:int=data.toString().indexOf("<qosCbQos>");
			//data.toString().replace(/xmlns(.*?)="(.*?)"/gm, "");
			if(index==-1){
				ipcorePhysicsCtrl.initQosNum++;
			}else{
				var result:String="<qos> "+data.toString().substr(index);
				qos=XML(result);
				stateNode.setClient("qosXml",qos);
				ipcorePhysicsCtrl.initQosNum++;
			}
		}
		
		/**
		 *查询acl 
		 * @param url
		 * 
		 */		
		public function queryAclData(url:String):void
		{
			if(projectType != "test")
			{
				onClientQuery(url,onResultAcl,onAclFault,ConnUtil.METHOD_GET);
			}
		}
		
		public  function onResultAcl(e:HttpResponseEvent,data:ByteArray):void
		{
			var acl:XML=XML(data.toString());
			var index:int=data.toString().indexOf("<aclGroups>");
			//data.toString().replace(/xmlns(.*?)="(.*?)"/gm, "");
			if(index==-1){
			}else{
				var result:String="<acl> "+data.toString().substr(index);
				acl=XML(result);
				stateNode.setClient("aclXml",acl);
			}
		}
		
		/**
		 * 编辑五元组
		 * @param flowobj
		 * 
		 */		
		private var flowObj:Object;
		public function putAcl(flowobj:Object):void
		{
			//先添加aclgroup
			flowObj=flowobj;
			var id:String=stateNode.getClient("id");
			var opsIp:String=SdncUtil.opsIp;
			var postAclGroupUrl:String="";
			var param:String="";
			onClientQuery(postAclGroupUrl,onResultPutAcl,onPutAclDefault,ConnUtil.METHOD_PUT,param,"application/xml");
		}
		
		private function onResultPutAcl(e:HttpDataEvent):void
		{
			//编辑五元组后  put流分类
			var id:String=stateNode.getClient("id");
			var opsIp:String=SdncUtil.opsIp;
			var qosClassifierUrl:String="http://"+opsIp+"/devices/"+id+"/qos/qosCbQos/qosClassifiers/qosClassifier";
			//流分类名字
			var classifierNameE:String=flowObj["classifierName"];
			//五元组名字
			var aclRuleNameE:String=flowObj["aclRuleName"];
			var param:String="";
			onClientQuery(qosClassifierUrl,putClassifier,putClassifierDefault,ConnUtil.METHOD_PUT,param,"application/xml");
		}
		
		private function onPutAclDefault(e:HttpErrorEvent):void
		{
			
		}
		/**
		 *put流行为后的回调方法 
		 * @param e
		 * 
		 */		
		private function putClassifier(e:HttpDataEvent):void
		{
			//put流分类之后 再put流行为
			var id:String=stateNode.getClient("id");
			var opsIp:String=SdncUtil.opsIp;
			var behaviorName:String=flowObj["behaviorName"];
			var tnlName:String=flowObj["tnlName"];
			var qosBehaviorUrl:String="http://"+opsIp+"/devices/"+id+"/qos/qosCbQos/qosBehaviors/qosBehavior";
			//流分类名字
			var classifierNameE:String=flowObj["classifierName"];
			//五元组名字
			var aclRuleNameE:String=flowObj["aclRuleName"];
			var param:String=
				"<behaviorName>"+behaviorName+"</behaviorName>"
               + "<qosActFilters>"
               + "<qosActFilter>"
               + "<actionType>filter</actionType>"
               + "<filter>deny</filter>"
               + "</qosActFilter>"
               + "</qosActFilters>"
               + "<qosActRdrIfs>"
               + "<qosActRdrIf>"
               + "<ifName>"+tnlName+"</ifName>"
               + "<actionType>redirectIf</actionType>"
               + "</qosActRdrIf>"
               + "</qosActRdrIfs>";
			onClientQuery(qosBehaviorUrl,onPutBehavior,putClassifierDefault,ConnUtil.METHOD_PUT,param,"application/xml");
		}
		
		private function onPutBehavior(e:HttpDataEvent):void
		{
			//流行为put之后 接着put流策略
			var id:String=stateNode.getClient("id");
			var opsIp:String=SdncUtil.opsIp;
			
			var policyName:String=flowObj["policyName"];
			var classifierName:String=flowObj["classifierName"];
			var behaviorName:String=flowObj["behaviorName"];
			
			var qosPolicyUrl:String="http://"+opsIp+"/devices/"+id+"/qos/qosCbQos/qosPolicys/qosPolicy";
			var param:String=
				"<policyName>"+policyName+"</policyName>"
                +"<description>string</description>"
                +"<statFlag>enable</statFlag>"
                +"<shareMode>enable</shareMode>"
                +"<step>5</step>"
                +"<qosPolicyNodes>"
                +"<qosPolicyNode>"
                +"<classifierName>"+classifierName+"</classifierName>"
                +"<behaviorName>"+behaviorName+"</behaviorName>"
                +"</qosPolicyNode>"
                +"</qosPolicyNodes>";
//				"[{\"policyName\":\""+tnlName_str+"\"},"
//				+"{\"description\":\""+"string"+"\"},"
//				+"{\"statFlag\":\""+"enable"+"\"},"
//				+"{\"shareMode\":\""+"enable"+"\"},"
//				+"{\"step\":\""+"5"+"\"},"
//				+"{\"qosPolicyNodes\":\"{}\"},";
				
			onClientQuery(qosPolicyUrl,onPutBehavior,putClassifierDefault,ConnUtil.METHOD_PUT,param,"application/xml");
		}
		private function putClassifierDefault(e:HttpErrorEvent):void
		{
			
		}
		
		private function onFault(e:FaultEvent):void
		{
			trace(e.message,"请求失败!");
		}
		
		private function onGreFault(e:HttpErrorEvent):void
		{
			trace("gre请求失败!");
			ipcorePhysicsCtrl.greInitNum++;
		}
		
		private function onQosFault(e:HttpErrorEvent):void
		{
			trace("qos请求失败!");
			ipcorePhysicsCtrl.initQosNum++;
		}
		
		private function onAclFault(e:HttpErrorEvent):void
		{
			trace("acl请求失败!");
		}
		/**
		 *处理URL请求(使用httpservice) 
		 * @param url URL地址
		 * @param onResult 返回结果
		 * @param onFault 错误处理
		 */
		private function onQuery(url:String,onResult:Function,onFault:Function):void
		{
			connUtil.query(url,onResult,onFault);
		}
		/**
		 * 处理URL请求(使用httpclient)
		 * @param uri:请求的地址
		 * @param onResult:结果处理函数
		 * @param onFault:错误处理函数
		 * @param method:操作方法（get,post,put,del）
		 * @param params:向服务器传递的参数
		 */	
		private function onClientQuery(uri:String,onResult:Function,onFault:Function,method:String = "get",params:String = null,contentType:String="application/json"):void
		{
			connUtil.clientQuery(uri,method,onResult,onFault,params,contentType);
		}
		
		/**
		 *ipcore 请求 
		 * @param uri
		 * @param onResult
		 * @param onFault
		 * @param method
		 * @param params
		 * 
		 */	
		public function ipcoreRequest(uri:String,onResult:Function,onFault:Function,method:String = "get",params:String = null):void
		{
			if(projectType=="test"){
				connUtil.query(uri,onResult,onFault);
			}else{
				connUtil.clientQuery(uri,method,onResult,onFault,params);
			}
			
		}
	}
}