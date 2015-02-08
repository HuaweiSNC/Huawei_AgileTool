package com.huawei.overte.model
{
	import com.huawei.overte.event.SdncEvt;
	import com.huawei.overte.handle.DataHandleTool;
	import com.huawei.overte.tools.ConnUtil;
	import com.huawei.overte.tools.PopupManagerUtil;
	import com.huawei.overte.tools.SdncUtil;
	import com.huawei.overte.view.node.StateNode;
	import com.huawei.overte.view.overte.OverTEView;
	
	import flash.display.BitmapData;
	import flash.display.GradientType;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.TimerEvent;
	import flash.geom.Matrix;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.net.SharedObject;
	import flash.system.Capabilities;
	import flash.utils.ByteArray;
	import flash.utils.Timer;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.messaging.channels.StreamingAMFChannel;
	
	import org.httpclient.events.HttpDataEvent;
	import org.httpclient.events.HttpErrorEvent;
	import org.httpclient.events.HttpResponseEvent;
	
	import twaver.Follower;
	import twaver.Styles;
	import twaver.Utils;
	import twaver.network.Network;
	import twaver.networkx.NetworkX;

	public class Device extends MetaData
	{
		public function Device()
		{
			
		}
		/**设备ID**/
		public var id:String;
		/**设备IP**/
		public var ip:String;
		/**用户名**/
		public var username:String;
		/**设备名称**/
		public var devicename:String;
		/**设备密码**/
		public var passwd:String;
		/**设备版本**/
		public var version:String;
		/**设备类型**/
		public var productType:String;
		/**设备状态**/
		public var state:String;
		
		//当前设备数据是否全部取到的状态，true为全部取到，如果有错误或者取不到为false，此设备标红
		public var deviceState:Boolean = true;
		private var ifmIs:Boolean=false;
		private var reFun:Function;
		private var __app:overTegui2;//主应用程序
		[Bindable]private var curarea:Object;//当前管理域
		private var centerPoint:Point=new Point(Capabilities.screenResolutionX/2,Capabilities.screenResolutionY/3);
		/**要分的数目*/
		private var num:int=0;
		/**角间隔*/
		private var rad:Number = 2*Math.PI;
		/**初始角度*/
		private var initRad:Number = Math.PI/2;
		//半径
		private var radii:Number=200;
		private var now:Date;
		private var v1:Number;
		private var v2:Number;
		
		public function initDevice(fun:Function):void
		{
			reFun=fun;
			__app = SdncUtil.app;//初始化主应用程序
			curarea = (__app.overte.topoview.selectedChild as OverTEView).ManAreasID;
			pingTest(stateNode.getClient("id"),getdevicemessage)
//			getIfm();
//			getlocation();
		}
			
		/**
		 * 请求获取IFM接口信息 Vlanif口
		 */			
		public function getIfm():void
		{
			now = new Date();
			v1=now.valueOf();
			var opsIp:String=SdncUtil.opsIp;
			var webname:String = SdncUtil.projectname;
			var fromuri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"+curarea+"/devices/"+this.stateNode.getClient("id")+ "/ifms?type=Vlanif";
			connUtil.clientQuery(fromuri,ConnUtil.METHOD_GET,onGetFromIfmResult,onGetIfmFault);
			trace("请求设备"+this.stateNode.getClient("devicename")+"Ifm信息")
		}
		
		/**获取设备接口信息连接成功方法**/
		public function onGetFromIfmResult(e:HttpResponseEvent,data:String):void
		{
			var ifms:ArrayCollection = new ArrayCollection();
			var ifmsXml:XML = new XML(data);
			if(e.response.code=="200"){
				if(ifmsXml.children().length()!=0){
					for(var i:int=0;i<ifmsXml.ifm.length();i++){
						ifms.addItem({
							ifmName:ifmsXml.ifm[i].name,
							phyType:ifmsXml.ifm[i].phyType,
							ipAddress:ifmsXml.ifm[i].ips.ip.ipAddress,
							subnetMask:ifmsXml.ifm[i].ips.ip.subnetMask,
							state:ifmsXml.ifm[i].ips.ip.state
						});
						
					}
					stateNode.setClient("ifm",ifms)
					DataHandleTool.giveifmtodevice(stateNode.getClient("devicename"),ifms)
				}
				now = new Date();
				v2=now.valueOf();
				var t:Number = (v2-v1)/1000;
				DataHandleTool.showOnConsole("设备"+this.stateNode.getClient("devicename")+"   Ifm信息获取时间"+t+"s")
				reFun();
			}else{
				PopupManagerUtil.getInstence().closeLoading();
				Alert.show("获取设备接口信息信息失败","提示");
				DataHandleTool.showOnConsole("设备接口获取失败，错误代码为："+e.response.code);
			}
		}
		/**获取设备接口信息连接出错方法**/
		private function onGetIfmFault(e:Event):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			DataHandleTool.showOnConsole("获取设备接口信息连接出错");
			Alert.show("数据连接出错","提示");
		}
		private function getdevicemessage(curDevice:Device):void
		{
			getIfm();
			getlocation();
//			var opsIp:String=SdncUtil.opsIp;
//			var webname:String = SdncUtil.projectname;
//			var uri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/devices/"+stateNode.getClient("id");
//			connUtil.clientQuery(uri,ConnUtil.METHOD_GET,onGetDeviceResult,onGetDeviceFault);
//			DataHandleTool.showOnConsole("发送请求设备详细信息，设备ID：  "+ stateNode.getClient("id"))
		}
//		/**获取设备详细信息连接成功方法**/
//		private function onGetDeviceResult(e:HttpResponseEvent,data:String):void
//		{
//			var ifms:ArrayCollection = new ArrayCollection();
//			if(e.response.code=="200"){
//				var devicexml:XML = new XML(data);
//				stateNode.setStyle(Styles.LABEL_COLOR,0xffffff);
//				stateNode.setStyle(Styles.LABEL_SIZE,"14");
//				if(SdncUtil.showdeviceName&&SdncUtil.showdeviceIp){
//					stateNode.name=devicexml.device.deviceName+"\n"+devicexml.device.deviceTopoIp;
//				}else if(SdncUtil.showdeviceName&&SdncUtil.showdeviceIp==false){
//					stateNode.name=devicexml.device.deviceName;
//				}else if(SdncUtil.showdeviceName==false&&SdncUtil.showdeviceIp){
//					stateNode.name=devicexml.device.deviceTopoIp;
//				}
//				stateNode.setClient("username",devicexml.device.userName);
//				stateNode.setClient("devicename",devicexml.device.deviceName);
//				stateNode.setClient("passwd",devicexml.device.passwd);
//				stateNode.setClient("deviceTopoIp",devicexml.device.deviceTopoIp);
//				stateNode.setClient("ip",devicexml.device.ipAddress);
//				stateNode.setClient("version",devicexml.device.version);
//				stateNode.setClient("state",devicexml.device.state=='200'?'normal':'sick');
//				stateNode.setClient("productType",devicexml.device.type);
//				if(devicexml.device.state!="200"){
//					stateNode.setStyle(Styles.INNER_COLOR,0xea6060);
//				}
//				stateNode.image="icon_core_ipcore";
//				DataHandleTool.showOnConsole(devicexml.device.deviceName+"  设备信息Get成功,");
//				/**所有设备信息获取成功后开始初始化Topo界面**/
//			}else{
//				DataHandleTool.showOnConsole("获取设备信息失败,错误代码："+e.response.code);
//				PopupManagerUtil.getInstence().closeLoading();
//				Alert.show("获取设备信息失败,错误代码："+e.response.code,"提示");
//			}
//			
//		}
//		/**获取设备详细信息连接出错方法**/
//		private function onGetDeviceFault(e:Event):void	
//		{
//			DataHandleTool.showOnConsole("获取设备信息连接出错"+e.toString());
//			PopupManagerUtil.getInstence().closeLoading();
//		}
		/**
		 * 获取当前设备的Topo位置
		 */			
		public function getlocation():void
		{
			var opsIp:String=SdncUtil.opsIp;
			var webname:String = SdncUtil.projectname;
			var uri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"+curarea+"/topolocation";
			connUtil.clientQuery(uri,ConnUtil.METHOD_GET,onResult,onFault);
		}
		private function onResult(e:HttpResponseEvent,data:String):void
		{
			if(data.toString()!=""){
				var layoutXml:XML = XML(data.toString());
				for each(var position:XML in layoutXml.position){
					var devicename:String = position.devicename;
					var x:Number = Number(position.x);
					var y:Number = Number(position.y);
					if(stateNode != null&&devicename==stateNode.getClient("devicename")){
						var p:Point = new Point(x,y);
						stateNode.location=p;
						DataHandleTool.showOnConsole(stateNode.getClient("devicename")+"设备坐标请求成功"+p);
					}
				}
			}else{
				selfDefinedLayout();
			}
//			reFun();
			
		}
		private function onFault(e:Event):void
		{
			reFun();
			selfDefinedLayout();
			PopupManagerUtil.getInstence().closeLoading();
		}
		/********************************Device PingTest********************************************************************/
		private var fun1:Function;
		public function pingTest(id:String,fun:Function):void{
			fun1 = fun;
			var opsIp:String = SdncUtil.opsIp;
			var webname:String = SdncUtil.projectname;
			var uri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"+curarea+"/devices/"+id+"/deviceping";
			connUtil.clientQuery(uri,ConnUtil.METHOD_GET,onPingResult,onPingFault);
		}
		private function onPingResult(e:HttpResponseEvent,data:String):void
		{
			state="";
			if(data.toString().search("pinging success") == -1){
				state = "sick";
				if(stateNode!=null){
					stateNode.setStyle(Styles.INNER_COLOR,0xea6060);
				}
			}else{
				state = "normal";
			}
			if(fun1!=null)
			fun1(this);
			
		}
		private  function onPingFault(e:Event):void
		{
			trace("pingTest出现错误"+e.toString());	
			PopupManagerUtil.getInstence().closeLoading();
		}
		/**
		 * 如果没有找到保存的布局信息，就自动布局
		 * 
		 */		
		public static var j:int=1;
		private function selfDefinedLayout():void
		{
			var devices:Array = DataHandleTool.devices;
			var rad:Number=2*Math.PI/(devices.length);
			var initRad:Number=Math.PI/2+rad/2;
			
			for(var i:int=0;i<devices.length;i++){
				if(devices[i].deviceName == stateNode.getClient("devicename")){
					if(j==0){
						stateNode.location=new Point(centerPoint.x,centerPoint.y);
					}else{
						var x:Number=centerPoint.x+radii*Math.cos(initRad+i*rad);
						var y:Number=centerPoint.y+radii*Math.sin(initRad+i*rad);
						stateNode.location=new Point(x,y);
					}
				}
			}
			trace(j)
			j++;
		}
	}
}