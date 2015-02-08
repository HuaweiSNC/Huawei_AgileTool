package com.huawei.overte.control
{
	import com.huawei.overte.handle.DataHandleTool;
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
	
	
	public class DeviceCtrl
	{
		public function DeviceCtrl()
		{
		}
		[Bindable]
		public var mydata:Data = Data.getInstence();
		public var opsIp:String = SdncUtil.opsIp;
		public var conn:ConnUtil  =  ConnUtil.getInstence();

		/**获取设备列表 rwx202245*/

		private var resourceManager:IResourceManager = ResourceManager.getInstance();

		public function getAllDevices():void
		{
			var url:String = ConnUtil.protocolHeader+opsIp+"/AgileTeService/agilete/domains/devices";
			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_GET,getdeReslut,getFault);
			trace("获取设备列表发送请求时间"+new Date().valueOf())
		}
		
		/**增加设备 rwx202245*/
		public function AddDevices(data:ArrayCollection):void
		{
			
//			var bodys:String = "<devices></devices>"
//			var bodyx:XML = XML(bodys);
//			//设备信息
//			
//			var devices:String = "<device><deviceName></deviceName><ipAddress></ipAddress><deviceTopoIp></deviceTopoIp><userName></userName><passwd></passwd><version></version><type></type></device>"
//			var devicex:XML = XML(devices);
//			
//			devicex.deviceName.children()[0] = data[0].devicename;
//			devicex.ipAddress.children()[0] = data[0].ip;
//			devicex.deviceTopoIp.children()[0] = data[0].topoip;
//			devicex.userName.children()[0] = data[0].username;
//			devicex.passwd.children()[0] = data[0].passwd;
//			devicex.version.children()[0] = data[0].version;
//			devicex.type.children()[0] = data[0].productType;
//			
//			bodyx.appendChild(devicex)
//			var body:XML = bodyx;
//			body = body.replace("&lt;","<")
//			body = body.replace("&gt;",">")
			
			var body:XML = DataHandleTool.pushdevicebody(data)
			var url:String = ConnUtil.protocolHeader+opsIp+"/AgileTeService/agilete/domains/devices";
			conn.clientQuery(url,ConnUtil.METHOD_POST,postReslut,getFault,body.toString());
			trace("发送添加设备请求"+new Date().valueOf())
		}
		/**修改设备 rwx202245*/
		public function EditDevices(data:ArrayCollection):void
		{
			var bodys:String = "<devices></devices>"
			var bodyx:XML = XML(bodys);
			//设备信息
			var devices:String = "<device><deviceName></deviceName><ipAddress></ipAddress><deviceTopoIp></deviceTopoIp><userName></userName><passwd></passwd><version></version><type></type><id></id><subdeviceid></subdeviceid></device>"
			var device:XML = XML(devices);
			
			device.deviceName.children()[0] = data[0].devicename;
			device.ipAddress.children()[0] = data[0].ip;
			device.deviceTopoIp.children()[0] = data[0].topoip;
			device.userName.children()[0] = data[0].username;
			device.passwd.children()[0] = data[0].passwd;
			device.version.children()[0] = data[0].version;
			device.type.children()[0] = data[0].productType;
			device.id.children()[0] = data[0].id;
			device.subdeviceid.children()[0] = data[0].subdeviceid;
			bodyx.appendChild(device)
			var body:XML = bodyx;
			body = body.replace("&lt;","<")
			body = body.replace("&gt;",">")
//			var body:XML = DataHandleTool.pushdevicebody(data)
		
			var url:String = ConnUtil.protocolHeader+opsIp+"/AgileTeService/agilete/domains/devices";
			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_PUT,putReslut,getFault,body);
		}
		
		/**删除设备 rwx202245*/
		public function DelDevices(id:String):void
		{
			did = id;
			var body:String = "<devices><device><id>"+id+"</id></device></devices>"
			var url:String = ConnUtil.protocolHeader+opsIp+"/AgileTeService/agilete/domains/devices/"+id;
			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_DELETE,delReslut,getFault,body);
			trace("发送删除设备请求"+new Date().valueOf())

		}
		
		public function getFault(e:Event):void
		{
			// TODO Auto Generated method stub
			Alert.show(resourceManager.getString('global','all.error'),
				resourceManager.getString('global','all.prompt'))
			DataHandleTool.showOnConsole("连接失败："+e.toString())
//			Alert.show("连接失败！","提示")
		}
		private function postReslut(e:HttpDataEvent):void
		{
			// TODO Auto Generated method stub
			var str:String=e.bytes.toString();
			if(Number(str))
			{
//				Alert.show("添加成功！","提示");
				Alert.show(resourceManager.getString('global','all.postsuccess'),
					resourceManager.getString('global','all.prompt'))
				trace("设备增加成功时间"+new Date().valueOf())
				getAllDevices()
			}
			else
			{
//				Alert.show("添加失败！错误信息："+str,"提示");
				Alert.show(resourceManager.getString('global','all.postfail')+"\n"+resourceManager.getString('global','all.devicefail'),
					resourceManager.getString('global','all.prompt'))
				DataHandleTool.showOnConsole("添加失败！错误信息："+str)
			}
		}
		private function putReslut(e:HttpDataEvent):void
		{
			// TODO Auto Generated method stub
			var str:String=e.bytes.toString();
			if(str=="true")
			{
				Alert.show(resourceManager.getString('global','all.savesuccess'),
					resourceManager.getString('global','all.prompt'))
//				Alert.show("保存成功！","提示");
				getAllDevices()
			}
			else
			{
				Alert.show(resourceManager.getString('global','all.savefail'),
					resourceManager.getString('global','all.prompt'))
//				Alert.show("保存失败！错误信息："+str,"提示");
				DataHandleTool.showOnConsole("保存失败！错误信息："+str)
			}
		}
		public var did:String=""
		private function delReslut(e:HttpDataEvent):void
		{
			// TODO Auto Generated method stub
			var str:String=e.bytes.toString();
			if(str.search("true")!=-1)
			{
				Alert.show(resourceManager.getString('global','all.deletedsuccess'),
					resourceManager.getString('global','all.prompt'))
			
				var url:String = ConnUtil.protocolHeader+opsIp+"/AgilePM/agilepm/domain/deleteAllDataForDeviceId/"+did;
				ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_DELETE,delDReslut,getFault,"");
				trace("删除设备成功时间"+new Date().valueOf())
				
			}
			else
			{
				Alert.show(resourceManager.getString('global','all.deletedfail'),
					resourceManager.getString('global','all.prompt'))
//				Alert.show("删除失败！错误信息："+str,"提示");
				DataHandleTool.showOnConsole("删除失败！错误信息："+str)
			}
		}
		private function delDReslut(e:HttpDataEvent):void
		{
			getAllDevices();
		}
		/**获取所有设备返回结果处理方法*/
		private function getdeReslut(e:HttpResponseEvent,data:String):void
		{
			if(e.response.code=="200")
			{
				var resxml:XML=XML(data.toString());
				var x:int = resxml.children().length();
				mydata.nowdevices.removeAll();
				for(var i:int = 0;i<x;i++)
				{
					mydata.nowdevices.addItem({
						num:i+1,
						deid:resxml.children()[i].id.children()[0],
						devicename:resxml.children()[i].deviceName.children()[0],
						ip:resxml.children()[i].ipAddress.children()[0],
						topoip:resxml.children()[i].deviceTopoIp.children()[0],
						username:resxml.children()[i].userName.children()[0],
						passwd:resxml.children()[i].passwd.children()[0],
						version:resxml.children()[i].version.children()[0],
						productType:resxml.children()[i].type.children()[0],
						subdeviceid:resxml.children()[i].subdeviceid.children()[0],
						state:"uninitialized",
						select:"flase"
					})
				}
				trace("增加设备完成读取设备列表完成"+new Date().valueOf())
			}
			else
			{
				
				Alert.show(resourceManager.getString('global','all.getfail')+resourceManager.getString('global','all.errorcode')+e.response.code,
					resourceManager.getString('global','all.prompt'))
//				Alert.show("错误信息："+data.toString(),"错误")
			}
			
		}
	}
	
}