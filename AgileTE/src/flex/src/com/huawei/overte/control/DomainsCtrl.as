package com.huawei.overte.control
{
	import com.huawei.overte.model.Data;
	import com.huawei.overte.tools.ConnUtil;
	import com.huawei.overte.tools.SdncUtil;
	
	import flash.events.Event;
	import flash.utils.ByteArray;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.resources.IResourceManager;
	import mx.resources.ResourceManager;
	
	import org.httpclient.events.HttpDataEvent;
	import org.httpclient.events.HttpResponseEvent;
	
	
	public class DomainsCtrl
	{
		
		public function DomainsCtrl()
		{
		}
		[Bindable]
		public var mydata:Data = Data.getInstence();
		var opsIp:String = SdncUtil.opsIp;
		private var resourceManager:IResourceManager = ResourceManager.getInstance();
		public function getDomains()
		{	
			var url:String = ConnUtil.protocolHeader+opsIp+"/AgileTeService/agilete/domains";
			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_GET,getReslut,getFault);
		}
		private function getReslut(e:HttpResponseEvent,data:String):void
		{
			if(data.toString()!="")
			{
				var resxml:XML=XML(data.toString());
				mydata.domainsxml = XML(data.toString())
				var x:int = resxml.children().length();
				mydata.domainsarr.removeAll();
				for(var i:int = 0;i<x;i++)
				{
					mydata.domainsarr.addItem({
						doid:resxml.children()[i].id.children()[0],
						doname:resxml.children()[i].name.children()[0],
						dotype:resxml.children()[i].type.children()[0],
						dodescribe:resxml.children()[i].describe.children()[0]
					})
				}
			}	
		}
		public function getFault(e:Event):void
		{
			// TODO Auto Generated method stub
			Alert.show(resourceManager.getString('global','all.geterror'),resourceManager.getString('global','all.prompt'));
		}
		
		/**生成增加域的body体 rwx202245**/
		public function makebody(domain:ArrayCollection,type:String,nowdoid:String)
		{
			var url:String = ConnUtil.protocolHeader+opsIp+"/AgileTeService/agilete/domains";
			//添加管理域
			if(type=="post")
			{
				//域信息
				var bodys:String = "<domains><domain><name>"+domain[0].name+"</name><type>"+domain[0].type+"</type><describe><"+"![CDATA["+domain[0].descr+"]]"+"></describe></domain></domains>"
				var bodyx:XML = XML(bodys);
				var body:XML = devicebody(bodyx)
				//设备信息
				
				ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_POST,postReslut,postFault,body);
			}
			//修改管理域
			else if(type=="put")
			{
				//域信息
				var bodys:String = "<domains><domain><id>"+nowdoid+"</id><name>"+domain[0].name+"</name><type>"+domain[0].type+"</type><describe><"+"![CDATA["+domain[0].descr+"]]"+"></describe></domain></domains>"
				var bodyx:XML = XML(bodys);
				var body:XML = devicebody(bodyx)
//				var str:String = "<toponodes></toponodes>"
//				bodyx.domain.appendChild(XML(str))
//				var devicesarr:ArrayCollection = mydata.nowdevices;
//				for(var i:int = 0;i<devicesarr.length;i++)
//				{
//					if(devicesarr[i].select=="true")
//					{
//						var devices:String = "<toponode><nodeID></nodeID><show></show></toponode>"
//						var devicex:XML = XML(devices);
//						devicex.nodeID.children()[0] = devicesarr[i].deid;
//						devicex.show.children()[0] = "true"
//						bodyx.domain.toponodes.appendChild(devicex)
//					}
//				}
//				var body:XML = bodyx;
//				body = body.replace("&lt;","<")
//				body = body.replace("&gt;",">")
				ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_PUT,putReslut,postFault,body);
			}
		}
		
		public function devicebody(bodyx:XML):XML{
			var str:String = "<toponodes></toponodes>"
			bodyx.domain.appendChild(XML(str))
			var devicesarr:ArrayCollection = mydata.nowdevices;
			for(var i:int = 0;i<devicesarr.length;i++)
			{
				if(devicesarr[i].select=="true")
				{
					var devices:String = "<toponode><nodeID></nodeID><show></show></toponode>"
					var devicex:XML = XML(devices);
					devicex.nodeID.children()[0] = devicesarr[i].deid;
					devicex.show.children()[0] = "true"
					bodyx.domain.toponodes.appendChild(devicex)
				}
			}
			var body:XML = bodyx;
			body = body.replace("&lt;","<")
			body = body.replace("&gt;",">")
			return body;
		}
		private function postReslut(e:HttpDataEvent):void
		{
			// TODO Auto Generated method stub
			var str:String=e.bytes.toString();
			if(Number(str))
			{
				Alert.show(resourceManager.getString('global','all.postsuccess'),resourceManager.getString('global','all.prompt'));
				getDomains()
			}
			else
			{
				Alert.show(resourceManager.getString('global','all.postfail')+" "+resourceManager.getString('global','all.errormessage')+" : "+str,resourceManager.getString('global','all.prompt'));
			}
		}
		private function putReslut(e:HttpDataEvent):void
		{
			// TODO Auto Generated method stub
			var str:String=e.bytes.toString();
			if(str=="true")
			{
				Alert.show(resourceManager.getString('global','all.savesuccess'),resourceManager.getString('global','all.prompt'));
				getDomains()
			}
			else if(str=="false")
			{
				Alert.show(resourceManager.getString('global','all.savefail')+" "+resourceManager.getString('global','all.errormessage')+" : "+str,resourceManager.getString('global','all.prompt'));
			}
		}
		private function postFault(e:Event):void
		{
			Alert.show(resourceManager.getString('global','all.puterror'),resourceManager.getString('global','all.prompt'));
		}

		private function getdeReslut(e:HttpResponseEvent,data:String):void
		{
			mydata.nowdevices.removeAll();
			if(data.toString()!="")
			{
				var resxml:XML=XML(data.toString());
				var x:int = resxml.children().length();
				for(var i:int = 0;i<x;i++)
				{
					mydata.nowdevices.addItem({
						deid:resxml.children()[i].id.children()[0],
						devicename:resxml.children()[i].deviceName.children()[0],
						ip:resxml.children()[i].ipAddress.children()[0],
						topoip:resxml.children()[i].deviceTopoIp.children()[0],
						username:resxml.children()[i].userName.children()[0],
						passwd:resxml.children()[i].passwd.children()[0],
						version:resxml.children()[i].version.children()[0],
						productType:resxml.children()[i].type.children()[0]
					})
				}
			}
		}
//		public function editDevice(data:ArrayCollection)
//		{
//			var bodys:String = "<devices></devices>"
//			var bodyx:XML = XML(bodys);
//			//设备信息
//
//				var devices:String = "<device><deviceName></deviceName><ipAddress></ipAddress><deviceTopoIp></deviceTopoIp><userName></userName><passwd></passwd><version></version><type></type><id></id></device>"
//				var devicex:XML = XML(devices);
//				
//				devicex.deviceName.children()[0] = data[0].devicename;
//				devicex.ipAddress.children()[0] = data[0].ip;
//				devicex.deviceTopoIp.children()[0] = data[0].topoip;
//				devicex.userName.children()[0] = data[0].username;
//				devicex.passwd.children()[0] = data[0].passwd;
//				devicex.version.children()[0] = data[0].version;
//				devicex.type.children()[0] = data[0].productType;
//				devicex.id.children()[0] = data[0].id;
//				
//				bodyx.appendChild(devicex)
//				var body:XML = bodyx;
//				body = body.replace("&lt;","<")
//				body = body.replace("&gt;",">")
//				var url:String = ConnUtil.protocolHeader+opsIp+"/AgileTeService/agilete/domains/"+data[0].doid+"/devices/"+data[0].id;
//				ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_PUT,putReslut,postFault,body);
//
//		}
		

//		public function delDevice(doid:String,deid:String)
//		{
//			var body:String = "<devices><device><id>"+deid+"</id></device></devices>"
//			var url:String = ConnUtil.protocolHeader+opsIp+"/AgileTeService/agilete/domains/"+doid+"/devices/"+deid;
//			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_DELETE,delReslut,postFault,body);
//		}
//		/**删除管理域**/
//
//		public function editDevice(data:ArrayCollection):void
//		{
//			var bodys:String = "<devices></devices>"
//			var bodyx:XML = XML(bodys);
//			//设备信息
//
//				var devices:String = "<device><deviceName></deviceName><ipAddress></ipAddress><deviceTopoIp></deviceTopoIp><userName></userName><passwd></passwd><version></version><type></type><id></id></device>"
//				var devicex:XML = XML(devices);
//				
//				devicex.deviceName.children()[0] = data[0].devicename;
//				devicex.ipAddress.children()[0] = data[0].ip;
//				devicex.deviceTopoIp.children()[0] = data[0].topoip;
//				devicex.userName.children()[0] = data[0].username;
//				devicex.passwd.children()[0] = data[0].passwd;
//				devicex.version.children()[0] = data[0].version;
//				devicex.type.children()[0] = data[0].productType;
//				devicex.id.children()[0] = data[0].id;
//				
//				bodyx.appendChild(devicex)
//				var body:XML = bodyx;
//				body = body.replace("&lt;","<")
//				body = body.replace("&gt;",">")
//				var url:String = ConnUtil.protocolHeader+opsIp+"/AgileTeService/agilete/domains/"+data[0].doid+"/devices/"+data[0].id;
//				ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_PUT,putReslut,postFault,body);
//
//		}
		
		public function delDevice(doid:String,deid:String):void
		{
			var body:String = "<devices><device><id>"+deid+"</id></device></devices>"
			var url:String = ConnUtil.protocolHeader+opsIp+"/AgileTeService/agilete/domains/"+doid+"/devices/"+deid;
			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_DELETE,delReslut,postFault,body);
		}

		public function delDomains(doid:String):void
		{
			var body:String = "<domains><domain><id>"+doid+"</id></domain></domains>"
			var url:String = ConnUtil.protocolHeader+opsIp+"/AgileTeService/agilete/domains/"+doid;
			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_DELETE,delReslut,postFault);
		}
		private function delReslut(e:HttpDataEvent):void
		{
			var str:String=e.bytes.toString();
			if(str!="")
			{
				Alert.show(resourceManager.getString('global','all.deletedsuccess'),resourceManager.getString('global','all.prompt'));
				getDomains()
			}
			else
			{
				Alert.show(resourceManager.getString('global','all.deletedfail')+" "+resourceManager.getString('global','all.errormessage')+" : "+str,resourceManager.getString('global','all.prompt'));
//				Alert.show("删除失败！错误信息："+str,"提示");
			}
		}
	}
}