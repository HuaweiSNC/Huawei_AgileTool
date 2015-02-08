package com.huawei.overte.handle
{
	
	import com.huawei.overte.model.Data;
	import com.huawei.overte.model.Device;
	import com.huawei.overte.tools.ConnUtil;
	import com.huawei.overte.tools.DataGetter;
	import com.huawei.overte.tools.PopupManagerUtil;
	import com.huawei.overte.tools.SdncUtil;
	import com.huawei.overte.view.link.FlowLink;
	import com.huawei.overte.view.link.MyNewLink;
	import com.huawei.overte.view.node.StateNode;
	import com.huawei.overte.view.overte.OverTEView;
	import com.huawei.overte.view.overte.com.OverTEData;
	import com.huawei.overte.view.overte.titlewindows.Alarm;
	import com.huawei.overte.view.overte.titlewindows.Console;
	import com.huawei.overte.view.overte.titlewindows.NQAlist;
	import com.laiyonghao.Uuid;
	import com.worlize.websocket.WebSocketEvent;
	
	import flash.events.Event;
	import flash.events.TimerEvent;
	import flash.text.StaticText;
	import flash.utils.ByteArray;
	import flash.utils.Timer;
	import flash.xml.XMLDocument;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.effects.easing.Back;
	import mx.formatters.DateFormatter;
	import mx.rpc.xml.SimpleXMLDecoder;
	
	import org.httpclient.events.HttpDataEvent;
	import org.httpclient.events.HttpResponseEvent;
	
	import twaver.IData;
	import twaver.Link;
	import twaver.Node;
	import twaver.Styles;
	import twaver.network.Network;
	
	public class DataHandleTool
	{
		public function DataHandleTool()
		{
		}
		
		
		/**登陆用户名*/
		public static var username:String;
		/**Topo上devices设备集合对象*/
		public static var devices:Array;
		/**Topo上所有节点集合对象*/
		public static var stateNodesArr:Array = new Array;
		/**Topo上所有链路集合对象*/
		public static var links:Array;
		public static var console:Console;
		/**告警对象*/
		public static var alarm:Alarm;
		
		/**Topo上所有节点集合对象*/
		public static var nodeArray:Array=[];
		/**当前设备上的Tunnel**/
		public static var tunneldevice:ArrayCollection= new ArrayCollection();
		/**当前系统所有Tunnel**/
		public static var AllTunnel:ArrayCollection = new ArrayCollection();
		/**当前设备上的流**/
		public static var flowdevice:ArrayCollection= new ArrayCollection();//当前设备Flow
		/**当前系统登陆所有流**/
		public static var AllFlow:ArrayCollection = new ArrayCollection();
		/**测试工程  当前添加Tunnel标志：标志每次是否都需要初始化页面**/
		public static var tunnelFlag:Boolean=true;
		/**当前系统登陆所有TunnelPolicy**/
		public static var AllTunnelPolicy:ArrayCollection = new ArrayCollection();
		public static var opsIp:String=SdncUtil.opsIp;
		public static var flowName:String  = ""
		public static var webname:String = SdncUtil.projectname;
		public static var deviceid:String = "";
		public static var tunnelName:String;
		public static var deviceId:String = "";
		public static var type:String = "";
		public static var iname:String = "";
		[Bindable]public static var tunnelFlowxml:XML;
		[Bindable]public static var mydata:Data = Data.getInstence();
		/**实例化 数据连接**/
		public static var connUtil:ConnUtil = ConnUtil.getInstence();
		/**时间监听器  监听管道流NQA以及流量信息**/
		public static var timer:Timer=new Timer(10000);
		/**管道主备运行状态**/
		public static var whichTunnelOn:String="";
		/**存储主备路径在Topo上的Link信息**/
		public static var Pathlinkarray:ArrayCollection;
		/**存储管道NQA  流NQA数据**/
		public static var nqadata:String = "";
		/**控制台数据存储变量**/
		public static var contentstr:String = "";
		
		
		
		/**根据ifmip查找连线*/
		public static function findLinkByifmIP(IfmIp:String,networkX:Network)
		{
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is MyNewLink){
					var link:MyNewLink = item as MyNewLink;
					if(link.getClient("frominterfaceIP") == IfmIp){
						link.setStyle(Styles.LINK_COLOR,"0x000000");
					}			
				}
			});
		}
		
		
		
		
		/**
		 * 从ops读取的设备信息转换成设备数组
		 * @param data
		 * @return devices
		 */	
		public static function handleDeviceData(data:String):Array
		{
			//json取回来的数据里面没有state的属性 ，需要取回device之后ping，得到结果赋给device的state的属性
			try{
				if(data!=""){
					var jsonObj:Object = JSON.parse(data);
					var deviceArray:Array=jsonObj.devices as Array;
					devices=[];
					for(var i:int=0;i<deviceArray.length;i++){
						//取回来的所有device数组
						var device_obj:Object=deviceArray[i];
						var device:Object=device_obj["device"];
						var d:Device = new Device();
						d.id=device["id"];
						d.devicename=device["devicename"];
						d.ip=device["ip"];
						d.passwd=device["passwd"];
						d.productType=device["productType"];
						d.username=device["username"];
						d.version=device["version"];
						device["device"] = d;
						devices.push(device);
					}
				}
				return devices;
			}catch(e:*){
				trace("无设备");
				return null;
			}
			return devices;
		}
		
		
		/**
		 * 数据格式转换 将xml转换成 ArrayCollection
		 * @param xml
		 * @return  ArrayCollection
		 * lwx200286
		 **/	
		public static function xmlToArrayCollection(xml:XML):ArrayCollection{    
			var xmlDoc:XMLDocument = new XMLDocument(xml.toString());
			var decoder:SimpleXMLDecoder = new SimpleXMLDecoder(true);
			var resultObj:Object = decoder.decodeXML(xmlDoc);
			var ac:ArrayCollection =new ArrayCollection(Array(resultObj));//Array(resultObj.root.list.source.item)
			return ac;
		}
		
		/**
		 * 根据管道状态以及主备路径划线
		 * @param tunnelstate 管道运行状态
		 * @param pathArray  管道主备运行状态、包括路径类型以及下一跳具体信息
		 * lwx200286
		 **/	
		public static function drawLineByTunnel(tunnelstate:String,pathArray:ArrayCollection,networkX:Network):void
		{
			networkX.innerColorFunction=null;//是节点告警颜色去掉
			Pathlinkarray = new ArrayCollection();
			
			whichTunnelOn = tunnelstate
			var beginNode:StateNode;//下一跳开始节点		
			var endNode:StateNode;//下一跳结束节点
			var link:MyNewLink;//路径Link对象
			initLinkColor(networkX);//初始化Topo连线颜色
			
			for(var i:int=0;i<pathArray.length;i++){
				var patharray:ArrayCollection =pathArray[i].nextHops
				var linkarray:Array = new Array;
				var number:int = 0;
				if(whichTunnelOn=="best_effort")
				{
					number = patharray.length
				}
				else
				{
					number = patharray.length-1
				}
				for(var j:int=0;j<number;j++)
				{
					if(patharray.length>=2&&j == number-1){
						break;//画到最后一个点的时候退出循环
					}
					beginNode=findNodeByifmIP(patharray[j].nextIp,networkX);//根据设备上端口IP查找节点
					endNode=findNodeByifmIP(patharray[j+1].nextIp,networkX);//根据设备上端口IP查找节点
					if(beginNode!=null&&endNode!=null){
						link = findLink(beginNode,endNode,networkX);
					}else{
						PopupManagerUtil.getInstence().closeLoading();
						continue;
					}
					if(link!=null)
					{
						if(link.getStyle("link.color",false)!=65280)//判断是否设置过绿色
						{
							link.setStyle(Styles.LABEL_COLOR,0xffffff);
							link.setStyle(Styles.LINK_BUNDLE_GAP,0);
							link.setStyle(Styles.LINK_WIDTH, 2);
							//若当前管道为主运行状态
							if(tunnelstate == "primary"){
								if(pathArray[i].pathType=="primary"){
									if((mydata.priState.search("down")!=-1)&&(mydata.backState.search("down")!=-1))
									{
										link.setStyle(Styles.LINK_COLOR, 0xFF8000);
									}
									else{
										link.setStyle(Styles.LINK_COLOR, 0x00FF00);	//主Path显示为绿色
									}
								}else{
									link.setStyle(Styles.LINK_COLOR, 0xFF8000);	//备份Path显示为橙色
								}
							}else if(tunnelstate == "hot_standby"){
								if(pathArray[i].pathType=="primary"){
									link.setStyle(Styles.LINK_COLOR, 0xFF8000);	//主Path显示为橙色
								}else{
									if((mydata.priState.search("down")!=-1)&&(mydata.backState.search("down")!=-1))
									{
										link.setStyle(Styles.LINK_COLOR, 0xFF8000);
									}
									else{
										link.setStyle(Styles.LINK_COLOR, 0x00FF00);	//主Path显示为绿色
									}	//备份Path显示为绿色
								}
							}else if(tunnelstate == "best_effort"){
								
								link.setStyle(Styles.LINK_COLOR, 0x00FF00);	//主Path显示为黄色
								
								//link.getStyle(Styles.LINK_COLOR)
							}
							//根据ip变红   xc
							trace("变色。。。。。。。。8888。。"+link.getClient("frominterfaceState"));
							if(link.getClient("frominterfaceState") =="down"||link.getClient("tointerfaceState") =="down"){
								link.setStyle(Styles.LINK_COLOR,"0x981807");
							}
							linkarray.push(link);
						}
					}else{
						continue;
					}
				}
				Pathlinkarray.addItem({
					pathType:pathArray[i].pathType,
					linkarray:linkarray
				})
			}
		}
		/**
		 * 时间监听 定时取管道状态  并且取到管道NQA 流量
		 * lwx200286
		 **/
		public static function onTimer(event:TimerEvent):void
		{
			trace("进行中。。。。。。。")
			opsIp = SdncUtil.opsIp;
			webname = SdncUtil.projectname;
			if(SdncUtil.app.overte.topoview.selectedChild is OverTEView){
				deviceid = (SdncUtil.app.overte.topoview.selectedChild as OverTEView).overteCtrl.deviceId;
				tunnelName = (SdncUtil.app.overte.topoview.selectedChild as OverTEView).overteCtrl.tunnelName;
				flowName= (SdncUtil.app.overte.topoview.selectedChild as OverTEView).navPanel.flowName;
			}
			if(deviceid!=""&&tunnelName!=""&&flowName=="")
			{
				trace("Tunnel State")
				var uri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"+SdncUtil.CurAreaId+"/devices/"+deviceid+"/tunnels?name="+tunnelName+"__state"
				connUtil.clientQuery(uri,ConnUtil.METHOD_GET,onGetTunnelStateResult,onGetTunnelStateFault);
			}
			if(deviceid!=""&&flowName!="")
			{
				trace("Tunnel State")
				var uri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"+SdncUtil.CurAreaId+"/devices/"+deviceid+"/tunnels?name="+tunnelName+"__state"
				connUtil.clientQuery(uri,ConnUtil.METHOD_GET,onGetTunnelStateResult,onGetTunnelStateFault);
			}
		}
		//		/***真实工程：请求路径信息连接成功方法*/
		//		private static function onGetnqaResult(e:HttpResponseEvent,data:String):void{
		//			
		//			if(e.response.code != "200"){
		//				trace("获取状态信息出错！")
		//				return;
		//			}
		//			else
		//			{
		//				if(mydata.sychartdata.length>11)
		//				{
		//					for(var j:int = 0;j<(mydata.sychartdata.length-12);j++)
		//					{
		//						mydata.sychartdata.removeItemAt(0)
		//					}
		//				}
		//				var xml:XML = new XML(data);
		//				var nqaXML:XMLList = xml.data;
		//				for(var i:int = 0;i<nqaXML.length();i++)
		//				{
		//					var xml:XML = new XML(data);
		//					var nqaXML:XMLList = xml.data;
		//					for(var i:int = 0;i<nqaXML.length();i++)
		//					{
		//						var dateformet:DateFormatter = new DateFormatter;
		//						dateformet.formatString = "JJ:NN:SS";
		//						var time:String = dateformet.format(new Date(nqaXML[i].schedule.children()[0]));
		//						mydata.sychartdata.addItem({x:time,y:nqaXML[i].value1.children()[0]})
		//					}
		//				}
		//				
		//				
		//				
		//			}
		//		}
		//		private static function onGetfluxResult(e:HttpResponseEvent,data:String):void{
		//			
		//			if(e.response.code != "200"){
		//				trace("获取状态信息出错！")
		//				return;
		//			}
		//			else
		//			{
		//				if(mydata.llchartdata.length>11)
		//				{
		//					for(var j:int = 0;j<(mydata.llchartdata.length-12);j++)
		//					{
		//						//delete mydata.llchartdata[0];
		//						mydata.llchartdata.removeItemAt(0)
		//					}
		//				}
		//				var xml:XML = new XML(data);
		//				var nqaXML:XMLList = xml.data;
		//				for(var i:int = 0;i<nqaXML.length();i++)
		//				{
		//					var dateformet:DateFormatter = new DateFormatter;
		//					dateformet.formatString = "JJ:NN:SS";
		//					var time:String = dateformet.format(new Date(nqaXML[i].schedule.children()[0]));
		//					mydata.sychartdata.addItem({x:time,y:nqaXML[i].value1.children()[0]})
		//				}
		//				
		//				
		//			}
		//		}
		//		private static function onGetFault(e:Event):void{
		//			Alert.show("获取信息连接出错","提示");
		//			
		//		}
		/**
		 * 实时获取管道状态成功方法
		 * lwx200286
		 **/
		private static function onGetTunnelStateResult(e:HttpResponseEvent,data:String):void
		{
			if(e.response.code=="200"){
				var ms:XML = new XML(data);
				//若获取到的管道状态与当前点击管道的运行状态不一致  触发重新画线
				if(ms.tunnel.flow!=whichTunnelOn){
					drawLineByTunnel(ms.tunnel.flow,(SdncUtil.app.overte.topoview.selectedChild as OverTEView).overteCtrl.patharray,SdncUtil.network)
					if(flowName==""){
						var urlTunnelNqa:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"+SdncUtil.CurAreaId+"/devices/"+deviceid+"/nqasoon?name=tunnel_"+tunnelName;
						connUtil.clientQuery(urlTunnelNqa,ConnUtil.METHOD_GET,onGetTunnelNQAResult,onGetTunnelNQAFault);
					}else{
						drawLineByFlow(ms.tunnel.flow,(SdncUtil.app.overte.topoview.selectedChild as OverTEView).overteCtrl.patharray,SdncUtil.network)
						var urlflowNqa:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"+SdncUtil.CurAreaId+"/devices/"+deviceid+"/nqasoon?name=flow_"+flowName
						connUtil.clientQuery(urlflowNqa,ConnUtil.METHOD_GET,onGetflowResult,onGeflowFault);
					}
				}else{
					if(flowName==""){
						var uri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"+SdncUtil.CurAreaId+"/devices/"+deviceid+"/nqasoon?name=tunnel_"+tunnelName;
						connUtil.clientQuery(uri,ConnUtil.METHOD_GET,onGetTunnelNQAResult,onGetTunnelNQAFault);
					}else{
						var uri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"+SdncUtil.CurAreaId+"/devices/"+deviceid+"/nqasoon?name=flow_"+flowName
						connUtil.clientQuery(uri,ConnUtil.METHOD_GET,onGetflowResult,onGeflowFault);
					}
				}
			}else{
				PopupManagerUtil.getInstence().closeLoading();
				DataHandleTool.showOnConsole("获取管道状态失败\n"+e.toString())
			}
		}
		/**
		 * 实时获取管道状态  请求路径信息连接出错方法
		 * lwx200286
		 **/
		private static function onGetTunnelStateFault(e:Event):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			DataHandleTool.showOnConsole("获取管道状态连接出错\n"+e.toString())
		}
		/**
		 * 实时获取管道NQA连接成功返回方法、请求成功后请求流量
		 * lwx200286
		 **/
		private static function onGetTunnelNQAResult(e:HttpResponseEvent,data:String):void
		{
			RemoveFlowLinkByflagName(SdncUtil.network);
			if(e.response.code=="200"){
				var ms:XML = new XML(data);
				nqadata = ms.data.value1
				var url:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"+SdncUtil.CurAreaId+"/devices/"+deviceid+"/fluxsoon?name=tunnel_"+tunnelName
				connUtil.clientQuery(url,ConnUtil.METHOD_PUT,onGetTunnelLLResult,onGetTunnelLLFault,llxml);
			}else{
				PopupManagerUtil.getInstence().closeLoading();
				DataHandleTool.showOnConsole("实时获取管道NQA失败，错误代码："+e.response.code)
			}
		}
		/**
		 * 实时获取管道NQA连接出错返回方法,
		 * lwx200286
		 **/
		private static function onGetTunnelNQAFault(e:Event):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			DataHandleTool.showOnConsole("实时获取管道NQA连接出错"+e.toString())
		}
		public static var llxml:XML = null;
		private static function onGetTunnelLLResult(e:HttpDataEvent):void
		{
//			if(e.response.code=="200"){
				PopupManagerUtil.getInstence().closeLoading();
				var data:String = e.bytes.toString();
				var ms:XML = new XML(data);
				llxml = new XML(data)
				for(var i:int=0;i<Pathlinkarray.length;i++){
					for(var j:int=0;j<Pathlinkarray[i].linkarray.length;j++){
						var link :MyNewLink = Pathlinkarray[i].linkarray[j] as MyNewLink
						if(whichTunnelOn == "primary"){
							if(Pathlinkarray[i].pathType=="primary"){
								link.name ="       "+nqadata+"ms "+ms.data.value1/(1000*1000)+"M/s";
								DataHandleTool.showOnConsole("ms.data.value1："+ms.data.value1)
								break;
							}
						}
						if(whichTunnelOn == "hot_standby"){
							if(Pathlinkarray[i].pathType=="hot_standby"){
								link.name ="       "+nqadata+"ms "+ms.data.value1/(1000*1000)+"M/s";
								DataHandleTool.showOnConsole("ms.data.value1："+ms.data.value1)
								break;
							}
						}
						if(whichTunnelOn == "turnround"){
							//							if(Pathlinkarray[i].pathType=="hot_standby"){
							//								link.name ="       "+nqadata+"ms "+ms.data.value1+"%";
							//								break;
							//							}
						}
						
					}
				}
//			}else{
//				PopupManagerUtil.getInstence().closeLoading();
//				trace(e.type.toString())
//			}
		}
		/**真实工程：请求路径信息连接出错方法**/
		private static function onGetTunnelLLFault(e:Event):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			trace("获取流量出错"+e.toString(),"提示");
		}
		/**
		 * 初始化连线的颜色  清除FlowLink以及节点上alarmState
		 * @param networkX
		 **/		
		public static function initLinkColor(networkX:Network):void
		{
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is MyNewLink){
					var l:Link=item as Link;
					l.setStyle(Styles.LINK_COLOR, 0x60c6fb);
					l.setStyle(Styles.LINK_WIDTH, 2);
					l.name="";
				}
				if(item is FlowLink){
					networkX.elementBox.remove(item);
				}
				//每次清除之前的气泡
				if(item is StateNode){
					var node:StateNode = item as StateNode;
					node.alarmState.clear();
				}
			});
			
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is MyNewLink){
					var link:MyNewLink = item as MyNewLink;
					trace("连线的开始节点名称："+link.getClient("frominterfaceIP"));
					if(link.getClient("frominterfaceState") =="down"||link.getClient("tointerfaceState") =="down"){
						link.setStyle(Styles.LINK_COLOR,"0x981807");
					}		
				}
			});
			
		}
		/**
		 * 画线
		 * @param selectedobj Advancedatagrid点击每一行对象
		 * @param networkX TopoBox
		 */	
		public static var flowarray:ArrayCollection = new ArrayCollection;
		public static function drawLineByFlow(tunnelstate:String,selectedobj:ArrayCollection,networkX:Network):void
		{
			//是节点告警颜色去掉
			networkX.innerColorFunction=null;
			whichTunnelOn = tunnelstate
			var beginNode:StateNode;		
			var endNode:StateNode;	
			var link:MyNewLink;
			initLinkColor(networkX);
			RemoveFlowLinkByflagName(networkX)
			Pathlinkarray = new ArrayCollection();
			for(var i:int=0;i<selectedobj.length;i++){
				var patharray:ArrayCollection =selectedobj[i].nextHops
				var linkarray:Array = new Array
				if(patharray==null)
				{}
				else
				{
					var number:int = 0;
					if(whichTunnelOn=="best_effort")
					{
						number = patharray.length
					}
					else
					{
						number = patharray.length-1
					}
					for(var j:int=0;j<number;j++)
					{
						if(patharray.length>=2&&j == number-1){
							var node:StateNode = findNodeByifmIP(patharray[j].nextIp,networkX);
							if(node!=null){
								node.alarmState.setNewAlarmCount(OverTEData.FLOW_END,1)
							}
							//						findNodeByifmIP(patharray[j].nextIp,networkX).alarmState.setNewAlarmCount(OverTEData.FLOW_END,1);
							break;//画到最后一个点的时候退出循环
						}
						beginNode=findNodeByifmIP(patharray[j].nextIp,networkX);
						endNode=findNodeByifmIP(patharray[j+1].nextIp,networkX);
						if(j==0&&beginNode!=null){
							beginNode.alarmState.setNewAlarmCount(OverTEData.FLOW_BEGIN,1);
						}
						if(beginNode!=null&&endNode!=null){
							link = findLink(beginNode,endNode,networkX);
						}else{
							PopupManagerUtil.getInstence().closeLoading();
							continue;
						}
						if(link!=null)
						{
							if(link.getStyle("link.color",false)!=65280)//判断是否设置过绿色
							{
								link = findLink(beginNode,endNode,networkX);
								link.setStyle(Styles.LABEL_COLOR,0xffffff);
								link.setStyle(Styles.LINK_BUNDLE_GAP,0);
								link.setStyle(Styles.LINK_WIDTH, 2);
								var backupflow:FlowLink = new FlowLink(link.fromNode,link.toNode);
								backupflow.setStyle(Styles.LINK_BUNDLE_ID,"FlowLink")
								if(whichTunnelOn=="best_effort"){
									SdncUtil.network.elementBox.add(backupflow)
									link.name = ""
									link.setStyle(Styles.LINK_COLOR, 0x00FF00);	
								}else if(whichTunnelOn=="primary"){
									if(selectedobj[i].pathType=="primary"){
										if((mydata.priState.search("down")!=-1)&&(mydata.backState.search("down")!=-1))
										{
											link.setStyle(Styles.LINK_COLOR, 0xFF8000);
										}
										else{
											SdncUtil.network.elementBox.add(backupflow)
											link.name = ""
											link.setStyle(Styles.LINK_COLOR, 0x00FF00);	
										}
									}else{
										link.setStyle(Styles.LINK_COLOR, 0xFF8000);	
									}
								}else if(whichTunnelOn=="hot_standby")	{
									if(selectedobj[i].pathType=="primary"){
										link.setStyle(Styles.LINK_COLOR, 0xFF8000);	
									}else{
										if((mydata.priState.search("down")!=-1)&&(mydata.backState.search("down")!=-1))
										{
											link.setStyle(Styles.LINK_COLOR, 0xFF8000);
										}
										else{
											SdncUtil.network.elementBox.add(backupflow)
											link.name = ""
											link.setStyle(Styles.LINK_COLOR, 0x00FF00);	
										}	
									}
								}
								//变红                    xc
								trace("变色。。。。。。。。。。");
								networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
									if(item is MyNewLink){
										var link:MyNewLink = item as MyNewLink;
										trace("连线的开始节点名称："+link.getClient("frominterfaceIP"));
										if(link.getClient("frominterfaceState") =="down"||link.getClient("tointerfaceState") =="down"){
											link.setStyle(Styles.LINK_COLOR,"0x981807");
										}		
									}
								});
								linkarray.push(link);
							}
						}else{
							continue;
						}
					}
				}
				Pathlinkarray.addItem({
					pathType:selectedobj[i].pathType,
					linkarray:linkarray
				})
				
			}
		}
		/***真实工程：请求路径信息连接成功方法*/
		private static function onGetflowResult(e:HttpResponseEvent,data:String):void
		{
			if(e.response.code=="200"){
				var ms:XML = new XML(data);
				
				for(var i:int=0;i<Pathlinkarray.length;i++){
					for(var j:int=0;j<Pathlinkarray[i].linkarray.length;j++){
						var link :MyNewLink = Pathlinkarray[i].linkarray[j] as MyNewLink
						
						if(whichTunnelOn=="turnround"){
						}else if(whichTunnelOn=="primary"){
							if(Pathlinkarray[i].pathType=="primary"){
								AddflowlinkName(ms.data.value1,"",SdncUtil.network)
								link.name = ""
								link.setStyle(Styles.LINK_COLOR, 0x00FF00);	
							}
							//								else{
							//									link.setStyle(Styles.LINK_COLOR, 0xFF8000);	
							//								}
						}else if(whichTunnelOn=="hot_standby")	{
							if(Pathlinkarray[i].pathType!="primary"){
								link.setStyle(Styles.LINK_COLOR, 0x00FF00);	
								AddflowlinkName(ms.data.value1,"",SdncUtil.network)
								link.name = ""
							}
							//								else{
							//									link.setStyle(Styles.LINK_COLOR, 0xFF8000);
							//								}
						}
						if(link.getClient("frominterfaceState") =="down"||link.getClient("tointerfaceState") =="down"){
							link.setStyle(Styles.LINK_COLOR,"0x981807");
						}
					}
				}
			}else{
				PopupManagerUtil.getInstence().closeLoading();
			}
			
		}
		/**真实工程：请求路径信息连接出错方法**/
		private static function onGeflowFault(e:Event):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			trace("获取流信息连接出错"+e.toString(),"提示");
		}
		
		
		/***真实工程：请求路径信息连接成功方法*/
		private static function onGetflowLLResult(e:HttpResponseEvent,data:String):void
		{
			if(e.response.code=="200"){
				var ms:XML = new XML(data);
				for(var i:int=0;i<Pathlinkarray.length;i++){
					for(var j:int=0;j<Pathlinkarray[i].linkarray.length;j++){
						var link :MyNewLink = Pathlinkarray[i].linkarray[j] as MyNewLink
						
						if(whichTunnelOn=="回切状态"){
							//							if(Pathlinkarray[i].pathType=="main"){
							//								link.setStyle(Styles.LINK_COLOR, 0xffea00);	
							//							}else{
							//								link.setStyle(Styles.LINK_COLOR, 0xFF8000);	
							//							}
						}else if(whichTunnelOn=="primary"){
							if(Pathlinkarray[i].pathType=="primary"){
								AddflowlinkName(nqadata,ms.data.value1,SdncUtil.network)
								link.name = ""
								//								link.setStyle(Styles.LINK_COLOR, 0x00FF00);	
							}
						}else if(whichTunnelOn=="hot_standby")	{
							if(Pathlinkarray[i].pathType!="primary"){
								link.setStyle(Styles.LINK_COLOR, 0xFF8000);
								//								AddflowlinkName(nqadata,ms.data.value1,SdncUtil.network)
							}
						}
						if(link["frominterfaceState"]=="down"||link["tointerfaceState"]=="down"){
							link.setStyle(Styles.LINK_COLOR,0x60c6fb);
						}
					}
				}
				
			}else{
				PopupManagerUtil.getInstence().closeLoading();
			}
		}
		/**真实工程：请求路径信息连接出错方法**/
		private static function onGeflowLLFault(e:Event):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			trace("获取流量连接出错"+e.toString(),"提示");
		}
		
		
		/**
		 * 根据设备上端口IP查找节点
		 * @param devicesIfm 设备端口
		 * @param networkX TopoBox
		 * @return  设备名称+端口名称
		 */		
		public static function findNodeByNodeIP(devicesIfm:String,networkX:Network):String
		{
			var returnNode:StateNode=null;
			var returnStr:String="";
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void
			{
				if(item is StateNode){
					var node:StateNode = item as StateNode
					var ifms:ArrayCollection = node.getClient("ifm")
					if(ifms.length!=0&&ifms!=null){
						for(var i:int = 0;i<ifms.length;i++){
							if(devicesIfm==ifms[i].ipAddress){
								returnNode=item as StateNode;
								returnStr = returnNode.getClient("devicename")+"‘"+ifms[i].ifmName
							}
						}
					}
				}
			});
			return returnStr;
		}
		/**
		 * 向设备上添加缓存Vlanif接口
		 * @param deviceId 设备IP
		 * @param networkX TopoBox
		 * @return 
		 */		
		public static function addVlanifinDevice(deviceId:String,obj:Object,networkX:Network):void
		{
			var returnNode:StateNode=null;
			var devices:Array = devices					
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void
			{
				if(item is StateNode){
					var node:StateNode = (item as StateNode)
					if(node.getClient("id")==deviceId){
						(node.getClient("ifm") as ArrayCollection).addItem(obj)
					}
				}
			});
			
			//			for(var i:int=0;i<devices.length;i++){
			//				if(devices[i].id==deviceId){
			//					(devices[i].ifm as ArrayCollection).addItem(obj);
			//					break;
			//				}
			//			}
			//			return returnStr;
		}
		
		/**
		 * 根据设备上端口IP查找节点
		 * @param devicesIfm 设备端口IP
		 * @param networkX
		 * @return  StateNode
		 * lwx200286
		 */		
		public static function findNodeByifmIP(IfmIp:String,networkX:Network):StateNode
		{
			var returnNode:StateNode=null;
			var bool:Boolean=false;
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is StateNode){
					var node:StateNode = item as StateNode 
					if(node.getClient("ifm")!=""){
						var ifms:ArrayCollection = node.getClient("ifm") as ArrayCollection;
						if(ifms!=null){
							for(var i:int = 0;i<ifms.length;i++){
								if(IfmIp==ifms[i].ipAddress.toString()){
									returnNode=item as StateNode;
									bool = true;
									break;
								}
							}
							if(bool==false){
								trace(returnNode)
							}
						}
					}
					
				}
			});
			return returnNode;
		}
		/**
		 * 将设备接口与设备绑定上
		 * @param devicename 设备名称
		 * @ifms 设备接口信息
		 * */
		public static function giveifmtodevice(devicename:String,ifms:ArrayCollection):void{
			var devices:Array = DataHandleTool.devices;
			for(var i:int=0;i<devices.length;i++){
				if(devices[i].deviceName==devicename){
					devices[i].ifm = ifms;
				}
			}
		}
		
		/**
		 * 根据设备名称查找设备Ifm
		 * @param devicesName 设备名称
		 * @param networkX TopoBox
		 * @return 
		 */		
		public static function findIfmByNodeName(devicesName:String,networkX:Network):Array
		{
			var ifmsArray:Array = []
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is StateNode){
					var devicename:String = (item as StateNode).getClient("devicename")
					if(devicesName==devicename){
						ifmsArray=(item as StateNode).getClient("ifms");
					}
				}});
			return ifmsArray;
		}
		
		/**
		 * 根据设备名称查找设备Ifm
		 * @param devicesName 设备名称
		 * @param networkX TopoBox
		 * @return 
		 */		
		public static function findinterfaceByNextName(ifmname:String,networkX:Network):String
		{
			var ifmsArray:String="";
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is MyNewLink){
					var mynewlink:MyNewLink = item as MyNewLink;
					var fromarray:String = mynewlink.getClient("frominterfaceIP")
					//					_fromName.split("\n")
					var toarray:String = mynewlink.getClient("tointerfaceIP")
					if(fromarray==ifmname){
						ifmsArray=mynewlink.getClient("tointerfaceIP");
					}
					if(toarray==ifmname){
						ifmsArray=mynewlink.getClient("frominterfaceIP");
					}
				}
			});
			//			var array:Array = ifmsArray.split("\n")
			return ifmsArray;
		}
		/**
		 * 将ifm数组添加到devices数组中 与node绑定
		 * @param node 节点
		 * @param ifmArray ifm数组
		 * @return 
		 */		
		public static function nodeClintIfm(node:StateNode,ifmArray:ArrayCollection):void
		{
			if(node.getClient("ifms")==null){
				node.setClient("ifms",ifmArray)
			}
			var devices:Array = DataHandleTool.devices;
			for(var i:int=0;i<devices.length;i++){
				if(node.getClient("devicename")==devices[i].devicename&&!devices[i].hasOwnProperty("ifms")){
					devices[i].ifms = ifmArray
				}
				
			}
		}
		
		//		/**
		//		 * 将ifm数组添加到devices数组中 与node绑定
		//		 * @param deviceName 节点 名称
		//		 * @param ifmArray ifm数组
		//		 * @return 
		//		 */		
		//		public static function nodeClintIfmByTopo(deviceName:String,ifmArray:ArrayCollection,networkX:Network):void
		//		{
		//			var node:StateNode = new StateNode;
		//			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
		//				if(item is StateNode){
		//					var devicename:String = (item as StateNode).getClient("devicename")
		//					if(deviceName==devicename){
		//						node=item as StateNode;
		//					}
		//				}
		//			});
		//			
		//			if(node.getClient("ifms")==null){
		//				node.setClient("ifms",ifmArray)
		//			}
		//			var devices:Array = DataHandleTool.devices;
		//			for(var i:int=0;i<devices.length;i++){
		//				if(node.getClient("devicename")==devices[i].devicename&&!devices[i].hasOwnProperty("ifms")){
		//					devices[i].ifms = ifmArray
		//				}
		//				
		//			}
		//		}
		/**
		 * 移除Topo组件中所有的FlowLink
		 * @param networkX 
		 */		
		public static function RemoveFlowLinkByflagName(networkX:Network):void{
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is FlowLink){
					networkX.elementBox.remove(item);
				}
			});
		}
		
		/**
		 * 将流的NQA与流量添加到Flowlink上
		 * @param nqastr 当前流的nqa
		 * @param llstr 当前流的流量
		 */		
		public static function AddflowlinkName(nqastr:String,llstr:String,networkX:Network):void{
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is FlowLink){
					var link:FlowLink = item as FlowLink;
					link.setStyle(Styles.LABEL_COLOR,0xffffff);
					link.name = "        "+nqastr+"ms "+llstr+"";
				}
			});
		}
		//		/**
		//		 * 根据开始节点  结束节点查找流
		//		 * @param fromNode 开始节点
		//		 * @param toNode 结束节点
		//		 * @return 
		//		 */		
		//		public static function findLinkByNode(fromNode:StateNode,toNode:StateNode,networkX:Network):FlowLink{
		//			var lk:FlowLink;
		//			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
		//				if(item is FlowLink){
		//					var l:FlowLink=item as FlowLink;
		//					if(l.fromNode==fromNode&&l.toNode==toNode){
		//						lk=l;
		//					}
		//				}
		//			});
		//			
		//			return lk;
		//		}
		/**
		 * 根据节点IP获取Node  自动算路时最后一跳是该点的LoopbackIP
		 * @param IfmIP 当前节点在Topo上连线接口IP
		 * @param networkX
		 * @return String 当前设备Loopback口IP
		 */		
		public static function findLoopIPByIfmIP(IfmIP:String,networkX:Network):String{
			var loopbackip:String="";
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is MyNewLink){
					var mynewlink:MyNewLink = item as MyNewLink;
					var fromarray:String = mynewlink.getClient("frominterfaceIP")
					var toarray:String = mynewlink.getClient("tointerfaceIP")
					if(fromarray==IfmIP){
						loopbackip=mynewlink.fromNode.getClient("deviceTopoIp");
					}
					if(toarray==IfmIP){
						loopbackip=mynewlink.toNode.getClient("deviceTopoIp");
					}
				}
			});
			return loopbackip;
		}
		/**
		 * 根据开始节点  结束节点查找MyNewLink，MyNewLink不区分方向
		 * @param fromNode 开始节点
		 * @param toNode 结束节点
		 * @return MyNewLink.
		 * lwx200286
		 */			
		public static function findLink(fromNode:StateNode,toNode:StateNode,networkX:Network):MyNewLink{
			var lk:MyNewLink;
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is MyNewLink){
					var l:MyNewLink=item as MyNewLink;
					if(l.fromNode==fromNode&&l.toNode==toNode){
						lk=l;
					}
					if(l.toNode==fromNode&&l.fromNode==toNode){
						lk=l;
					}
				}
			});
			return lk;
		}
		/**
		 * 测试工程用于创建TopoLink
		 * @param networkX
		 */		
		public static function createLinkByTnlForTest(networkX:Network):void{
			for(var i=0;i<links.length;i++){
				var fromNode:Node;
				var toNode:Node;
				
				var fromNodeName:String = links[i]["fromDevice"];
				var toNodeName:String = links[i]["toDevice"];
				
				networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
					if(item is Node){
						var sn:Node=item as Node;
						var NodeName:String = sn.getClient("devicename");
						if(fromNodeName==NodeName){
							fromNode = sn;
						}
						if(toNodeName==NodeName){
							toNode = sn;
						}
					}
				});
				var aa:MyNewLink = new MyNewLink(fromNode,toNode);
				aa.setStyle(Styles.LINK_BUNDLE_ID,"Link")
				aa._fromName =fromNode.getClient("ifms")[0].ifName+"\n"+fromNode.getClient("ifms")[0].ifIpAddr;
				aa._toName = fromNode.getClient("ifms")[0].ifName+"\n"+fromNode.getClient("ifms")[0].ifIpAddr;
				aa.setStyle(Styles.LINK_COLOR,0x60c6fb)
				networkX.elementBox.add(aa);
			}
		}
		/**
		 * 根据设备ID查找设备的详细信息
		 * @param deviceid 设备ID
		 * @return Object  当前设备对象
		 * */
		public static function finddeviceById(deviceid:String):Object{
			var devices:Array = DataHandleTool.devices;
			var deviceMessage:Object = new Object();
			for(var i:int=0;i<devices.length;i++){
				if(devices[i].id==deviceid){
					deviceMessage=devices[i]
				}
			}
			return deviceMessage
		}
		/**
		 * 真实工程创建Topo连线
		 * @param oradddb 是否添加到数据库标识
		 * @param networkX
		 * lwx200286
		 */		
		public static function createLinkByTnlForReal(oradddb:Boolean,networkX:Network):void{
			trace("开始划线时间"+new Date().time)
			DataHandleTool.showOnConsole("创建Topo连线");
			for(var i:int=0;i<links.length;i++){
				var fromNode:Node=null;
				var toNode:Node=null;
				var fromNodeId:String = links[i]["fromDeviceID"];
				var toNodeId:String = links[i]["toDeviceID"];
				networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
					if(item is Node){
						var sn:Node=item as Node;
						var NodeName:String = sn.getClient("id");
						if(fromNodeId==NodeName){
							fromNode = sn;
						}
						if(toNodeId==NodeName){
							toNode = sn;
						}
					}
				});
				if(fromNode!=null&&toNode!=null){
					var mlk:MyNewLink = new MyNewLink(fromNode,toNode);
					/**第一层判断Topo中是否存在该Link**/
					if(!OrhaveLink(mlk,networkX)){
						mlk.setStyle(Styles.LINK_BUNDLE_ID,"Link");
						/**第二层判断Link起始位置显示格式**/
						if(SdncUtil.showInterface&&SdncUtil.showIp){
							mlk._fromName =links[i]["frominterface"]+"\n"+links[i]["frominterfaceIP"];
							mlk._toName = links[i]["tointerface"]+"\n"+links[i]["tointerfaceIP"];
						}else if(SdncUtil.showInterface&&SdncUtil.showIp==false){
							mlk._fromName =links[i]["frominterface"];
							mlk._toName = links[i]["tointerface"];
						}else if(SdncUtil.showInterface==false&&SdncUtil.showIp){
							mlk._fromName =links[i]["frominterfaceIP"];
							mlk._toName = links[i]["tointerfaceIP"];
						}
						/**Link set属性**/
						mlk.setClient("linkname",links[i]["linkname"]);
						mlk.setClient("fromDeviceID",links[i]["fromDeviceID"]);
						mlk.setClient("fromDevice",links[i]["fromDevice"]);
						mlk.setClient("frominterface",links[i]["frominterface"]);
						mlk.setClient("frominterfaceIP",links[i]["frominterfaceIP"]);
						mlk.setClient("frominterfaceState",links[i]["frominterfaceState"]);
						mlk.setClient("toDeviceID",links[i]["toDeviceID"]);
						mlk.setClient("toDevice",links[i]["toDevice"]);
						mlk.setClient("tointerface",links[i]["tointerface"]);
						mlk.setClient("tointerfaceIP",links[i]["tointerfaceIP"]);
						mlk.setClient("tointerfaceState",links[i]["tointerfaceState"]);
						mlk.setStyle(Styles.LINK_COLOR,0x60c6fb);
						if(links[i]["frominterfaceState"]=="down"||links[i]["tointerfaceState"]=="down"){
							mlk.setStyle(Styles.LINK_COLOR,0x60c6fb);
						}
						DataHandleTool.showOnConsole("Topo视图加入连线： 链路起点："+links[i]["fromDevice"]+"链路终点："+links[i]["toDevice"]);
						networkX.elementBox.add(mlk);
						/**判断该连线是否添加到数据库中    在自动发现链路时若Topo中无该链路 则添加到数据库中**/
						if(oradddb){
							var uuid:Uuid = new Uuid();//36位UUid
							var addLinlBody:String = "<topoLinks><topoLink>"; 
							addLinlBody+= "<name>"+uuid.toString().slice(0,31)+"</name>"; 
							addLinlBody+= "<headNodeConnector><Connectorid>"+links[i]["frominterface"]+"</Connectorid>"; 
							addLinlBody+="<Connectorip>"+links[i]["frominterfaceIP"]+"</Connectorip>"
							addLinlBody+= "<toponode><nodeID>"+links[i]["fromDeviceID"]+"</nodeID>"; 
							addLinlBody+= "<nodeType>"+links[i]["fromDevice"]+"</nodeType>"; 
							addLinlBody+= "</toponode></headNodeConnector><tailNodeConnector>"; 
							addLinlBody+= "<Connectorid>"+links[i]["tointerface"]+"</Connectorid>"; 
							addLinlBody+="<Connectorip>"+links[i]["tointerfaceIP"]+"</Connectorip>"
							addLinlBody+= "<toponode><nodeID>"+links[i]["toDeviceID"]+"</nodeID>"; 
							addLinlBody+= "<nodeType>"+links[i]["toDevice"]+"</nodeType>";
							addLinlBody+= "</toponode></tailNodeConnector>"; 
							addLinlBody+= "<cost>1</cost><bandwidth>10</bandwidth></topoLink></topoLinks>"; 
							trace("发送保存连线时间======"+new Date().time)
							var opsIp:String=SdncUtil.opsIp;
							var webname:String = SdncUtil.projectname;
							var uri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"+SdncUtil.CurAreaId+"/links";
							connUtil.clientQuery(uri,ConnUtil.METHOD_POST,onPostLinkResult,onPostLinkFault,addLinlBody);
						}
					}
				}
			}
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is MyNewLink){
					var link:MyNewLink = item as MyNewLink;
					trace("连线的开始节点名称："+link.getClient("frominterfaceIP"));
					if(link.getClient("frominterfaceState") =="down"||link.getClient("tointerfaceState") =="down"){
						link.setStyle(Styles.LINK_COLOR,"0x981807");
					}		
				}
			});
			trace("划线完成时间"+new Date().time)
			DataHandleTool.showOnConsole("Topo视图初始化连线完成");
			PopupManagerUtil.getInstence().closeLoading();
		}
		/**
		 * 自动发现链路  链路添加到数据库连接成功方法
		 * lwx200286
		 */	
		private static function onPostLinkResult(e:HttpDataEvent):void
		{ 
			trace("连线返回时间"+new Date().time+e.toString())
			if(e.bytes.toString()){
				PopupManagerUtil.getInstence().closeLoading();
				DataHandleTool.showOnConsole("Topo链路自动发现添加到数据库成功");
			}else{
				DataHandleTool.showOnConsole("Topo链路自动发现添加到数据库失败");
			}
		}
		/**
		 * 自动发现链路  链路添加到数据库连接出错方法
		 * lwx200286
		 */	
		private static function onPostLinkFault(e:Event):void
		{
			DataHandleTool.showOnConsole("Topo链路自动发现添加到数据库出错");
		}
		/**
		 * 判断topo中是否存在当前连线
		 * @param mynewlink  链路
		 * @param networkX
		 * @return Boolean  true存在该链路  false不存在该链路
		 * lwx200286
		 */		
		public static function OrhaveLink(mynewlink:MyNewLink,networkX:Network):Boolean{
			var flag:Boolean = false;
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is MyNewLink){
					var lk:MyNewLink=item as MyNewLink;
					if(lk.fromNode==mynewlink.fromNode&&lk.toNode==mynewlink.toNode){
						flag=true;
					}
					if(lk.fromNode==mynewlink.toNode&&lk.toNode==mynewlink.fromNode){
						flag=true;
					}
				}
			});
			return flag;
		}
		/**
		 * 在控制台中输出方法
		 * @param content  控制台显示内容
		 * lwx200286
		 */	
		public static function showOnConsole(content:String):void
		{
			contentstr+=content+"\n\n";
		}
		
		/**webSocket通道申报 处理方法*/
		public static function webSocketChanelCreateResultFunction(e:HttpResponseEvent = null,data:String = null):void
		{
			var str:String = String(data).replace(/xmlns(.*?)="(.*?)"/gm, "");
			if(isNotFound(data)) return;
			trace("webSocket通道申报成功");
			DataGetter.getInstance().webSocketAndGetAlarmData();
		}
		/**判定是否返回404*/
		public static function isNotFound(data:String):Boolean
		{
			var str:String = String(data).replace(/xmlns(.*?)="(.*?)"/gm, "");
			if(str.indexOf("Not Found")!=-1)
			{
				trace("Error 404：URL Not Found");
				return true;
			}
			else
			{
				return false;
			}
		}
		/**websocket请求管道流返回数据方法*/
		public static function handleWebSocketMessage(event:WebSocketEvent):void
		{
			try
			{
				DataHandleTool.showOnConsole("webSocket连接获取数据成功")
				var str:String = event.message.utf8Data;
				var xml:XML = new XML(str)
				var cuarea:String = (SdncUtil.app.overte.topoview.selectedChild as OverTEView).ManAreasID
				if(xml.domains.@id==cuarea)
				{
					mydata.nqaTunnelxml = xml.domains;
					(SdncUtil.app.overte.topoview.selectedChild as OverTEView).navPanel.tunnelxml = xml.domains;
					(SdncUtil.app.overte.topoview.selectedChild as OverTEView).navPanel.g.expandAll();
					
				}
				
			} 
			catch(error:Error) 
			{
				trace(error.toString())
			}
			
		}
		
		
		public static function pushlinks(linkListXml:XMLList):void{
			var links:Array=[];
			DataHandleTool.links=links;
			for(var i:int=0;i<linkListXml.length();i++)
			{
				var linkobj:Object=new Object;
				linkobj["linkname"]=linkListXml[i].name.toString();
				linkobj["fromDeviceID"]=linkListXml[i].headNodeConnector.toponode.nodeID.toString();
				linkobj["fromDevice"]=linkListXml[i].headNodeConnector.toponode.nodeType.toString();
				linkobj["frominterface"]=linkListXml[i].headNodeConnector.Connectorid.toString();
				linkobj["frominterfaceIP"]=linkListXml[i].headNodeConnector.Connectorip.toString();
				linkobj["frominterfaceState"]=linkListXml[i].headNodeConnector.ConnectorState.toString();
				linkobj["toDeviceID"]=linkListXml[i].tailNodeConnector.toponode.nodeID.toString();
				linkobj["toDevice"]=linkListXml[i].tailNodeConnector.toponode.nodeType.toString();
				linkobj["tointerface"]=linkListXml[i].tailNodeConnector.Connectorid.toString();
				linkobj["tointerfaceIP"]=linkListXml[i].tailNodeConnector.Connectorip.toString();
				linkobj["tointerfaceState"]=linkListXml[i].tailNodeConnector.ConnectorState.toString();
				trace("刷新时："+linkListXml[i].headNodeConnector.ConnectorState.toString());
				links.push(linkobj);
			}		
		}
		
		public static function pushnodes(devicearray:ArrayCollection,network:Network):void{
			for each(var devicerobj:Object in devicearray){
				var stateNode:Node=new StateNode;
				stateNode.setStyle(Styles.LABEL_COLOR,0xffffff);
				stateNode.setStyle(Styles.LABEL_SIZE,"14");
				nodeArray.push(stateNode);
				if(SdncUtil.showdeviceName&&SdncUtil.showdeviceIp){
					stateNode.name=devicerobj.deviceName.toString()+"\n"+devicerobj.deviceTopoIp.toString();
				}else if(SdncUtil.showdeviceName&&SdncUtil.showdeviceIp==false){
					stateNode.name=devicerobj.deviceName.toString();
				}else if(SdncUtil.showdeviceName==false&&SdncUtil.showdeviceIp){
					stateNode.name=devicerobj.deviceTopoIp.toString();
				}
				stateNode.setClient("username",devicerobj.userName.toString());
				stateNode.setClient("devicename",devicerobj.deviceName.toString());
				stateNode.setClient("passwd",devicerobj.Passwd.toString());
				stateNode.setClient("deviceTopoIp",devicerobj.deviceTopoIp.toString());
				stateNode.setClient("ip",devicerobj.ipAddress.toString());
				stateNode.setClient("version",devicerobj.version.toString());
				stateNode.setClient("productType",devicerobj.type.toString());
				stateNode.setClient("id",devicerobj.id.toString());
				devices.push(devicerobj);
				stateNode.image="icon_core_ipcore";
				network.elementBox.add(stateNode);
			}
		}
		
		public static function pushdevicebody(data:ArrayCollection):XML{
			var bodys:String = "<devices></devices>"
			var bodyx:XML = XML(bodys);
			//设备信息
			
			var devices:String = "<device><deviceName></deviceName><ipAddress></ipAddress><deviceTopoIp></deviceTopoIp><userName></userName><passwd></passwd><version></version><type></type></device>"
			var devicex:XML = XML(devices);
			
			devicex.deviceName.children()[0] = data[0].devicename;
			devicex.ipAddress.children()[0] = data[0].ip;
			devicex.deviceTopoIp.children()[0] = data[0].topoip;
			devicex.userName.children()[0] = data[0].username;
			devicex.passwd.children()[0] = data[0].passwd;
			devicex.version.children()[0] = data[0].version;
			devicex.type.children()[0] = data[0].productType;
			
			bodyx.appendChild(devicex)
			var body:XML = bodyx;
			body = body.replace("&lt;","<")
			body = body.replace("&gt;",">")
			return body;
		}
	}
	
}