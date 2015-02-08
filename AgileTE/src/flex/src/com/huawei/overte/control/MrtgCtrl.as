package com.huawei.overte.control
{
	import com.huawei.overte.model.MrtgDatas;
	import com.huawei.overte.tools.ConnUtil;
	import com.huawei.overte.view.overte.titlewindows.*;
	
	import flash.events.Event;
	import flash.net.URLRequest;
	import flash.net.navigateToURL;
	import flash.utils.ByteArray;
	
	import mx.controls.Alert;
	
	import org.httpclient.events.HttpDataEvent;
	import org.httpclient.events.HttpResponseEvent;
	import org.httpclient.http.multipart.Part;

	public class MrtgCtrl
	{
		//public var opsIp:String = SdncUtil.opsIp;
		public var mydata:MrtgDatas = MrtgDatas.getInstence()
		public var _app:MRTG_Sets;
		public var newjobname:String;
		public var formwork:String = "nqa";
		public function MrtgCtrl()
		{
		}
		public function getMRTGList():void
		{
			var url:String = ConnUtil.protocolHeader+mydata.ipandport+"/AgilePM/agilepm/domain/getListenerList/"+formwork;
			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_GET,getdeReslut,getFault);
		}
		public function getListener(id:String):void
		{
			newjobname = id;
			var url:String = ConnUtil.protocolHeader+mydata.ipandport+"/AgilePM/agilepm/domain/getListener/"+formwork+"/"+id;
			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_GET,getoneReslut,getFault);
			
		}
		public function newtask(body:String):void
		{
			var url:String = ConnUtil.protocolHeader+mydata.ipandport+"/AgilePM/agilepm/domain/createListener/"+formwork;
			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_POST,postReslut,getFault,body);
		}
		public function edittask(body:String):void
		{
			var url:String = ConnUtil.protocolHeader+mydata.ipandport+"/AgilePM/agilepm/domain/modifyListener/"+formwork;
			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_PUT,putReslut,getFault,body);
		}
		public function showwebpage(id:String):void
		{
			var url:String=ConnUtil.protocolHeader+mydata.ipandport+"/AgilePM/statisticsPage/html/"+formwork+"_"+id+".html";  
			var request:URLRequest=new URLRequest(url);  
			navigateToURL(request,"_blank");
		}
		private function putReslut(e:HttpDataEvent):void
		{
			// TODO Auto Generated method stub
			var str:String=e.bytes.toString();
			if(str=="OK")
			{
				Alert.show("保存成功！","提示");
				//getMRTGList();
			}
			else
			{
				Alert.show("保存失败！错误信息："+str,"提示");
			}
		}
		private function postReslut(e:HttpDataEvent):void
		{
			// TODO Auto Generated method stub
			var str:String=e.bytes.toString();
			if(str=="OK")
			{
								Alert.show("创建成功！","提示");
								getMRTGList();
			}
			else
			{
								Alert.show("创建失败！错误信息："+str,"提示");
			}
		}
		private function getoneReslut(e:HttpResponseEvent,data:String):void
		{
			var xml:XML = new XML
			if(e.response.code=="200")
			{
				if(data.toString()=="undefined")
				{
					var str:String = "<listenerData><id>"+newjobname+"</id></listenerData>"
					newtask(str)
				}
				else
				{
					xml = XML(data.toString());
					_app.setlabels(xml);
					//mydata.setsme = xml;
				}
				var i:Number = Math.random()
			}
			else 
			{
				xml = <error></error>;
				Alert.show("获取配置信息错误！","提示")
			}
			
			//return xml;	
		}
		private function getdeReslut(e:HttpResponseEvent,data:String):void
		{
			mydata.taskName.removeAll()
			if(e.response.code=="200")
			{
				var xml:XML = XML(data.toString());
				for(var i:int = 0;i<xml.children().length();i++)
				{
					mydata.taskName.addItem({name:xml.children()[i].children()[0]})
				}
					
			}
			else 
			{
				Alert.show("获取配置列表错误！","提示")
			}
			
		}
		public function getFault(e:Event):void
		{
			// TODO Auto Generated method stub
			Alert.show("连接失败","提示")
			//			Alert.show("连接失败！","提示")
		}
	}
}