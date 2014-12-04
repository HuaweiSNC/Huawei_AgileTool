package com.huawei.sdnc.view.gre.dataHandle
{
	import com.huawei.sdnc.model.Device;
	import com.huawei.sdnc.model.Gre;
	import com.huawei.sdnc.model.GreTunnel;
	import com.huawei.sdnc.model.Ifm;
	import com.huawei.sdnc.model.Nqa;
	import com.huawei.sdnc.model.QosItem;
	import com.huawei.sdnc.model.QosListCalculate;
	import com.huawei.sdnc.model.acl.AclGroup;
	import com.huawei.sdnc.model.acl.aclRuleBas4.AclRuleAdv4;
	import com.huawei.sdnc.model.ifm.Interface;
	import com.huawei.sdnc.model.ifm.ifmAm4.IfmAm4;
	import com.huawei.sdnc.model.ifm.ifmAm4.am4CfgAddr.Am4CfgAddr;
	import com.huawei.sdnc.model.qos.QosActRdrIf;
	import com.huawei.sdnc.model.qos.QosBehavior;
	import com.huawei.sdnc.model.qos.QosClassifier;
	import com.huawei.sdnc.model.qos.QosPolicy;
	import com.huawei.sdnc.model.qos.QosPolicyNode;
	import com.huawei.sdnc.model.qos.QosRuleAcl;
	import com.huawei.sdnc.techschema.ServiceTool;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.common.node.StateNode;
	import com.huawei.sdnc.view.gre.Console;
	import com.huawei.sdnc.view.gre.MyLink;
	
	import flash.events.Event;
	import flash.events.TimerEvent;
	import flash.utils.ByteArray;
	import flash.utils.Timer;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	
	import twaver.ElementBox;
	import twaver.IData;
	import twaver.Link;
	import twaver.Styles;
	import twaver.networkx.NetworkX;

	public class DataHandleTool
	{
		public function DataHandleTool()
		{
		}
		
			
		public static var devices:Array;
		public static var stateNodesArr:Array;
		[Bindable]
		public static var handleArr:ArrayCollection ;
		[Bindable]
		public static var fileNameTypeDropDownList:ArrayCollection 
		[Bindable]
		public static var body:String ;
		
		
		[Bindable]
		public static var message:String ;
		
		[Bindable]
		public static var url:String ;
		
		[Bindable]
		public static var urlxml:XML ;
		
		[Bindable]
		public static var Method:String ;
		
		public static var console:Console;
		/**
		 * pipe定义的管道变量
		 * 管道名称，源IP，目的IP
		 * */
		public static var pipe:ArrayCollection = new ArrayCollection([
			{id:0,pipename:'tnl1',src:'10.111.86.144',des:'10.111.86.140',tnl:'Tnl0/0/1'},
			{id:1, pipename:'tnl2',src:'10.111.86.142',des:'10.111.86.143',tnl:'Tnl0/0/2'}
		]);
		public static var qosCalculate:QosListCalculate = new QosListCalculate();
		/**
		 * 从ops读取的设备信息转换成设备数组
		 * @param data
		 * @return devices
		 * 
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
					fileNameTypeDropDownList = new ArrayCollection(devices)
				}
				return devices;
			}catch(e:*){
				trace("无设备");
				return null;
			}
			return devices;
		}
		/**
		 * 返回策略数组
		 * @param srcIp
		 * @param policyXml
		 * @return 
		 * 
		 */				
		public static function initDevicePolicy(srcIp:String,policyXml:XML):Array{
			var policyes:Array=[];
			for each(var device:XML in policyXml.device){
				var deviceName:String=device.devicename;
				if(srcIp==deviceName){
					for each(var policy:XML in device.policys.policy){
						var obj:Object=new Object;
						obj["policyName"]=policy.policyName[0];
						obj["policyType"]=policy.policyType[0];
						obj["srcIp"]=policy.srcIp[0];
						obj["nextIp"]=policy.nextIp[0];
						obj["desIp"]=policy.desIp[0];
						policyes.push(obj);
					}
					break;
				}
			}
			return policyes;
		}
		
		private static var timer:Timer=new Timer(1000);
		
		/**
		 * test工程topo图画线方法
		 * @param beginPolicy
		 * @param networkX
		 */		
		public static function drawLineByPolicy(beginPolicy:Object,networkX:NetworkX):void
		{
			//是节点告警颜色去掉
			networkX.innerColorFunction=null;
			initLinkColor(networkX);
			var linkArr:Array=[];
			var isContinue:Boolean=true;
			var srcIp_begin:String=beginPolicy["srcIp"];
			var policyName_begin:String=beginPolicy["policyName"];
			var curNode:StateNode=findNode(srcIp_begin,networkX);
			var nextNode:StateNode;		
			var curIp:String=curNode.getClient("ip");
			var nextIp:String;
			while(isContinue){
				
				var policyes:Array=curNode.getClient("policyes");
				
				for each(var policy:Object in policyes){
					var policyName:String=policy["policyName"];
					var policyType:String=policy["policyType"];
					if(policyName_begin==policyName){
						nextIp=policy["nextIp"];
						
						nextNode=findNode(nextIp,networkX);
							
						var ll:MyLink=findLink(curNode,nextNode,networkX);
						curNode=nextNode;
						if(policyType=="end"){
							isContinue=false;
							
						}
						if(isContinue)
						linkArr.push(ll);
						break;
					}
					
				}
				
			}
			if(timer!=null){
				timer.reset();
			}
			timer.addEventListener(TimerEvent.TIMER,function(event:TimerEvent):void{
				for each(var link:MyLink in linkArr){
					//在link上添加随机数
					var index: Number = Math.round(Math.random() * (80 - 20)) + 20;
					link.name = index+"%";
					
				}
			});
			timer.start();
			for each(var link:MyLink in linkArr){
				link.setStyle(Styles.LINK_COLOR, 0xf72551);
			}
		}
		
		/**
		 * 画线
		 * @param beginPolicy
		 * @param networkX
		 * 
		 */		
		public static function drawLineByPolicyTnl(beginPolicy:Object,networkX:NetworkX):void
		{
			//是节点告警颜色去掉
			networkX.innerColorFunction=null;
			initLinkColor(networkX);
			var linkArr:Array=[];
			var isContinue:Boolean=true;
			var policyName:String=beginPolicy["policyName"];
			//找到起始设备节点
			var beginNode:StateNode=findNodeByBeginPolicy(beginPolicy,networkX);
			beginNode.alarmState.setNewAlarmCount(SdncUtil.FLOW_BEGIN,1);
			var curNode:StateNode=beginNode;
			var nextNode:StateNode;
			var isFindPolicy:Boolean=true;
			var isFindTnl:Boolean=true;
			//找到开始策略里的tnl
			if(beginNode!=null){
				while(isContinue){
					var tnls:Array=curNode.getClient("tnls");
					var policyes:Array=curNode.getClient("policyes");
					for each(var p:Object in policyes){
						var pname:String=p["policyName"];
						if(policyName==pname){
							var tnlxName:String=p["tnlName"];
							
							for each(var tnl:Object in tnls){
								var tnlonam:String=tnl["tnlName"];
								if(tnlxName==tnlonam){
									var dstIpAddr:String=tnl["dstIpAddr"];
									nextNode=findNode(dstIpAddr,networkX);
									if(nextNode!=null){
										var link1:MyLink=findLink(curNode,nextNode,networkX);
										linkArr.push(link1);
										curNode=nextNode;
										isFindTnl=true;
										break;
									}else{
										isContinue=false;
									}
									
								}else{
									isFindTnl=false;
								}
							}
							if(!isFindTnl){
								isContinue=false;
							}
							isFindPolicy=true;
							break;
						}else{
							isFindPolicy=false;
						}
					}
					//如果没有找到相同名字的策略
					if(!isFindPolicy){
						isContinue=false;
					}
				}
				
			}
			
			if(timer!=null){
				timer.reset();
				timer=null;
			}
			timer=new Timer(1000);
			timer.addEventListener(TimerEvent.TIMER,function(event:TimerEvent):void{
				for each(var link:MyLink in linkArr){
					//在link上添加随机数
					var index: Number = Math.round(Math.random() * (97 - 20)) + 20;
					var ms:Number =  Math.round(Math.random() * (70 - 1)) + 1;
					link.name ="            "+ms+"ms , "+index+"%";
					if(index>80){
						link.setStyle(Styles.LABEL_COLOR,0xf72551);
					}else{
						link.setStyle(Styles.LABEL_COLOR,0xffffff);
					}
				}
			});
			timer.start();
			curNode.alarmState.setNewAlarmCount(SdncUtil.FLOW_END,1);
			for each(var link:MyLink in linkArr){
				link.setStyle(Styles.LINK_COLOR, 0xf72551);
				link.setStyle(Styles.LINK_WIDTH, 2);
				var index: Number = Math.round(Math.random() * (80 - 20)) + 20;
			}
		}
		
		/**
		 * 真实工程根据选中的流策略将tnl变色
		 * */
		public static function drawLineByPolicyforReal(policyObj:Object,networkX:NetworkX):void{
			initLinkColor(networkX);
			var qosItem:QosItem=policyObj["qosItem"];
			var policyList:Array=qosCalculate.calculateList(qosItem);
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is MyLink){
					var myLink:MyLink=item as  MyLink;
					myLink.setStyle(Styles.LINK_COLOR, 0x60c6fb);
					myLink.name = "";
				}
				if(item is StateNode){
					var node:StateNode=item as  StateNode;
					var device:Device=node.getClient("device");
					if(device!=null){
					}
				}
			});
			var beginNode:StateNode;
			var endNode:StateNode;
			var deviceListForFlowStatics:Array=[];
			for(var i:int=0;i<policyList.length;i++){
				var qosItem1:QosItem=policyList[i] as QosItem;
				var qosSrcAddress:String=qosItem1.qosSrcAddress;
				var qosdestAddress:String=qosItem1.qosdestAddress;
				var fromNode:StateNode=findNode(qosSrcAddress,networkX);
				var device:Device=fromNode.getClient("device");
				var toNode:StateNode=findNode(qosdestAddress,networkX);
				if(i==0)
					beginNode=fromNode;
				endNode=toNode;
				var link:MyLink=findLink(fromNode,toNode,networkX);
				
				//调用是使能统计的接口
				var classifiername:String = policyObj["policyName"];
//				device.flowStatices(classifiername,link);
				deviceListForFlowStatics.push(device);
				link.setStyle(Styles.LINK_COLOR, 0xf72551);
				link.setStyle(Styles.LINK_WIDTH, 2);
				link.setClient("agent",true);
				networkX.elementBox.resetBundleLinks();
			}
			networkX.innerColorFunction=null;
			beginNode.alarmState.setNewAlarmCount(SdncUtil.FLOW_BEGIN,1);
			endNode.alarmState.setNewAlarmCount(SdncUtil.FLOW_END,1);
		}
		
		
		
		
		
		/**
		 *查找Node 根据设备ip 
		 * 
		 */		
		public static function findNode(ip:String,networkX:NetworkX):StateNode
		{
			var node:StateNode=null;
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is StateNode){
					var ip_now:String=item.getClient("ip");
					if(ip==ip_now){
						node= item as StateNode;
//						node.setStyle(Styles.INNER_COLOR,0xf80314);
					}
				}
			});
			return node;
		}
		
		/**
		 * 根据开始策略查找设备节点
		 * @param beginPolicy
		 * @param networkX
		 * @return 
		 * 
		 */		
		protected static function findNodeByBeginPolicy(beginPolicy:Object,networkX:NetworkX):StateNode
		{
			var returnNode:StateNode=null;
			var policyName:String=beginPolicy["policyName"];
			if(SdncUtil.cuProjectType == "test"){
				networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
					if(item is StateNode){
						var policyes:Array=item.getClient("policyes");
						for each(var policy:Object in policyes){
							var pName:String=policy["policyName"];
							var pType:String=policy["policyType"];
							if(policyName==pName&&pType=="begin"){
								returnNode=item as StateNode;
								break;
							}
						}
					}
				});
			}else{
				networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
					if(item is StateNode){
						var device:Device = item.getClient("device");
						var qosPolicyArr:Array = device.qos.qosPolicys;
						for each(var policy:QosPolicy in qosPolicyArr){
							var policyName1:String=policy.policyName;
							if(policyName==policyName1){
								returnNode = item as StateNode;
								break;
							}
						}
					}
				});
			}
			return returnNode;
		}
		/**
		 *查找link 
		 * @param fromNode
		 * @param toNode
		 * @param networkX
		 * @return 
		 * 
		 */		
		public static function findLink(fromNode:StateNode,toNode:StateNode,networkX:NetworkX):MyLink
		{
			var lk:MyLink=new MyLink;
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is MyLink){
					var l:MyLink=item as MyLink;
					if(l.fromNode==fromNode&&l.toNode==toNode){
						lk=l;
					}
				}
			});
			return lk;
		}

		/**
		 * 初始化连线的颜色
		 * @param networkX
		 * 
		 */		
		public static function initLinkColor(networkX:NetworkX):void
		{
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is MyLink){
					var l:MyLink=item as MyLink;
					l.setStyle(Styles.LINK_COLOR, 0x60c6fb);
					l.setStyle(Styles.LINK_WIDTH, 1);
					l.setClient("agent",false);
					l.name="";
				}
				//每次清除之前的气泡
				if(item is StateNode){
					var node:StateNode = item as StateNode;
					node.alarmState.clear();
				}
			});
		}
		/**
		 * 根据tnl创建link test工程使用
		 * 假数据  画topo图中的所有管道
		 * @param networkX
		 * 
		 */		
		public static function createLinkByTnlForTest(networkX:NetworkX):void
		{
			var ebox:ElementBox=networkX.elementBox;
			ebox.forEachByBreadthFirst(function(item:IData):void{
				if(item is StateNode){
					var stateNode:StateNode=item as StateNode;
					var tnls:Array=stateNode.getClient("tnls");
					for each(var tnl:Object in tnls){
						var dstIpAddr:String=tnl["dstIpAddr"];
						var toNode:StateNode=findNode(dstIpAddr,networkX);
						var myLink:MyLink=new MyLink(stateNode,toNode);
						myLink.setClient("name",tnl.tnlName)
						myLink.setStyle(Styles.LINK_BUNDLE_EXPANDED,true);
						myLink.setStyle(Styles.LINK_BUNDLE_GAP,10);
						ebox.add(myLink);
					}
				}
			});
		}
		
		/**
		 * 根据tnl创建link 真实工程使用
		 * 真实数据 画topo图中的所有管道
		 * @param networkX
		 */		
		public static function createLinkByTnlForNormal(networkX:NetworkX):void
		{
			var getNqaDataList:Array=[];
			networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is StateNode){
					var stateNode:StateNode=item as StateNode;
					var device:Device=stateNode.getClient("device");
					if(device!=null){
						// 计算当前设备的qos
//						device.addQosList(qosCalculate);
						var gre:Gre=device.gre;
						for each(var greTunnel:GreTunnel in gre.greTunnels){
							var dstIpAddr:String = greTunnel.dstIpAddr;
							var srcIpAddr:String = greTunnel.srcIpAddr;
							var dstNode:StateNode=findNodeByDstIp(dstIpAddr,networkX);
							if(dstNode!=null&&dstNode!=stateNode){
							var myLink:MyLink=new MyLink(stateNode,dstNode);
							myLink.leftnodeID=stateNode.nodeId;
							myLink.rightnodeID=dstNode.nodeId;
							networkX.elementBox.add(myLink);
//							device.getNqaIfmResult(dstNode.getClient("ip"),myLink);
							}
						}
					}
				}
			});
		}
		/**
		 * 根据目的IP地址到该节点
		 * @param innerIp
		 * @param networkX
		 * @return 
		 */	
		public static function findNodeByDstIp(dstIp:String,networkX:NetworkX):StateNode
		{
			var willReturnNode:StateNode=null;
			var ebox:ElementBox=networkX.elementBox;
			ebox.forEachByBreadthFirst(function(item:IData):void{
				if(item is StateNode){
					var stateNode:StateNode=item as StateNode;
					var device:Device=stateNode.getClient("device");
					var ifm:Ifm=device.ifm;
					var interfaces:Array=ifm.interfaces;
					//ifmAm4
					var isBreak:Boolean=false;
					for each(var interface1:Interface in interfaces){
						var ifPhyType:String=interface1.ifPhyType;
						var ifmAm4:IfmAm4=interface1.ifmAm4;
						var am4CfgAddrs:Array=ifmAm4.am4CfgAddrs;
//						if(ifPhyType=="Vlanif"){
							for each(var am4CfgAddr:Am4CfgAddr in am4CfgAddrs){
								var ifIpAddr:String=am4CfgAddr.ifIpAddr;
								if(ifIpAddr==dstIp){
									willReturnNode=stateNode;
									isBreak=true;
									break;
								}
							}
//						}
					if(isBreak)
						break;
					}
				}
			});
				return willReturnNode;
		}
		/**
		 * 根据目的IP地址到该节点
		 * @param innerIp
		 * @param networkX
		 * @return 
		 */	
		public static function findNodeByDstIpfortest(dstIp:String,networkX:NetworkX):StateNode
		{
			var willReturnNode:StateNode=null;
			var ebox:ElementBox=networkX.elementBox;
			ebox.forEachByBreadthFirst(function(item:IData):void{
				if(item is StateNode){
					var stateNode:StateNode=item as StateNode;
					if(stateNode.getClient("ip")==dstIp){
						willReturnNode=stateNode;
					}
				}
			});
			return willReturnNode;
		}
		/**
		 * 根据tnl的增删，更新topo图中的link
		 * @param srcIp
		 * @param dstIpAddr
		 * @param method post del
		 * @param networkX
		 * 
		 */		
		public static var o:Object;
		public static var v1:Number
		public static var v2:Number
		public static function addOrDelLinkByTnl(deviceIp:String,dstIpAddr:String,method:String,networkX:NetworkX,tnlName_str:String=""):void
		{
			o = new Object()
			v1 = new Date().time
				
			var srcNode:StateNode=findNode(deviceIp,networkX);
			var dstNode:StateNode=findNode(dstIpAddr,networkX);	
			if(method=="post"){
				if(srcNode!=null&&dstNode!=null){
					var newLink:MyLink=new MyLink(srcNode,dstNode);
					networkX.elementBox.add(newLink);
					ServiceTool.Method = "[POST]"
					ServiceTool.api = "utunnel"
					ServiceTool.schema = "Boolean ret = utunnel.create()"
					ServiceTool.url = "http://127.0.0.1:8999/AgileGre/rest/devices/"+srcNode.getClient("id")+"/utunnel";
					ServiceTool.code = "200"
					ServiceTool.curtime = SdncUtil.getFormatStr("MM-DD  JJ:NN",new Date())
					ServiceTool.body = "<utunnel><tunnels><tunnel><tunnelName>"+tnlName_str+"</tunnelName><tunnelId/><direction/><tunnelType>gre</tunnelType><sourceNodeId>"+srcNode.getClient("ip")+"</sourceNodeId><destinationNodeId>"+dstNode.getClient("ip")+"</destinationNodeId><sourceTpType>ip_address</sourceTpType><sourceTpId/><bandwidth/><latency/><adminStatus/></tunnel></tunnels></utunnel>"
					ServiceTool.message = "<ok xmlns:nc='urn:ietf:params:xml:ns:netconf:base:1.0'/>"
				}
			}else if(method=="del"){
				var delLink:MyLink=findLink(srcNode,dstNode,networkX);
				if(delLink!=null){
					networkX.elementBox.remove(delLink);
					ServiceTool.Method = "[DELETE]"
					ServiceTool.schema = "Boolean ret = utunnel.delete()"
					ServiceTool.url = "http://127.0.0.1:8999/AgileGre/rest/devices/"+srcNode.getClient("id")+"/utunnel?tnlName="+delLink.getClient("name");
					ServiceTool.code = "200"
					ServiceTool.api = "utunnel"
					ServiceTool.curtime = SdncUtil.getFormatStr("MM-DD  JJ:NN",new Date())
					ServiceTool.body = ""
					ServiceTool.message = "<ok xmlns:nc='urn:ietf:params:xml:ns:netconf:base:1.0'/>"
				}
			}
		}
		/**
		 * 
		 * @param xml
		 * @return 
		 * 
		 */		
		public static function formatGreXml(greXml:XML):XML{
			var re:XML=null;
			var index:int=greXml.toString().indexOf("<greTunnels>");
			if(index!=-1){
				var result:String="<gre> "+greXml.toString().substr(index);
				re=XML(result);
			}
			return re;
		}
		/**
		 *格式化qos数据，根节点的 xmlns
		 * @param qosXml
		 * @return 
		 * 
		 */		
		public static function formatQosXml(qosXml:XML):XML{
			var re:XML=null;
			var index:int=qosXml.toString().indexOf("<qosCbQos>");
			if(index!=-1){
				var result:String="<qos> "+qosXml.toString().substr(index);
				re=XML(result);
			}
			return re;
		}
		/**
		 *修改后的更新列表方法 
		 * @param stateNode
		 * @return 
		 * 
		 */		
		public static function refreshPolicyList2(stateNode:StateNode):Array{
			var policyArr:Array=[];
			var device:Device=stateNode.getClient("device");
			var qosClassifiers:Array=device.qos.qosClassifiers;
			var qosBehaviors:Array=device.qos.qosBehaviors;
			for each(var qosClassifier:QosClassifier in qosClassifiers){
				var policyObj:Object=new Object;
				var classifierName:String=qosClassifier.classifierName;
				
				//查询五元组名称
				var aclName:String="";
				for each(var qosRuleAcl:QosRuleAcl in qosClassifier.qosRuleAcls){
					aclName=qosRuleAcl.aclName;
					var classifierID:String=qosRuleAcl.classifierID;
				}
				policyObj["aclNumOrName"]=aclName;
				
				//根据五元组名称查找acl
				var aclGroups:Array=device.acl.aclGroups;
				for each(var aclGroup:AclGroup in aclGroups){
					var aclNumOrName:String=aclGroup.aclNumOrName;	
					if(aclName==aclNumOrName){
						
						for each(var aclRuleBas4:AclRuleAdv4 in aclGroup.aclRuleAdv4s){
							var aclRuleName:String=aclRuleBas4.aclRuleName;
							policyObj["aclRuleName"]=aclRuleName;
							//源ip
							var aclSourceIp:String=aclRuleBas4.aclSourceIp;
							policyObj["srcIp"]=aclSourceIp;
							//源端口
							var aclSrcPortOp:String=aclRuleBas4.aclSrcPortOp;
							policyObj["srcPop"]=aclSrcPortOp;
							//目的ip
							var aclDestIp:String=aclRuleBas4.aclDestIp;
							policyObj["desIp"]=aclDestIp;
							//目的端口
							var aclDestPortOp:String=aclRuleBas4.aclDestPortOp;
							policyObj["desPop"]=aclDestPortOp;
							//协议类型  
							var aclProtocol:String=aclRuleBas4.aclProtocol;
							policyObj["xType"]=aclProtocol;
						}
						break;
					}
				}
				for each(var qosBehavior:QosBehavior in qosBehaviors){
					var behaviorName:String=qosBehavior.behaviorName;
					if(behaviorName==classifierName+"_action"){
						policyObj["policyName"]=classifierName;
						policyObj["behaviorName"]=behaviorName;
						for each(var qosActRdrIf:QosActRdrIf in qosBehavior.qosActRdrIfs){
							var ifName:String=qosActRdrIf.ifName;
							policyObj["tnlName"]=ifName;
						}
						policyArr.push(policyObj);
						break;
					}
				}
			}
			return policyArr;
		}
		
		public static function refreshPolicyList(stateNode:StateNode):Array{
			var policyArr:Array=[];
			var device:Device=stateNode.getClient("device");
			if(device!=null){
				var qosPolicyes:Array=device.qos.qosPolicys;
				for each(var qosPolicy:QosPolicy in qosPolicyes){
					//流分类名称
					var classifierName:String="";
					//流行为名称
					var behaviorName:String="";
					//acl五元组名称
					var aclName:String="";
					
					var policyObjTmp:Object=new Object;
					var policyName:String=qosPolicy.policyName;
					policyObjTmp["policyName"]=policyName;
					for each(var qosPolicyNode:QosPolicyNode in qosPolicy.qosPolicyNodes){
						classifierName=qosPolicyNode.classifierName;
						behaviorName=qosPolicyNode.behaviorName;
					}
					var qosClassifiers:Array=device.qos.qosClassifiers;
					for each(var qosClassifier:QosClassifier in qosClassifiers){
						var classifierName1:String=qosClassifier.classifierName;
						if(classifierName==classifierName1){
							//查询五元组名称
							for each(var qosRuleAcl:QosRuleAcl in qosClassifier.qosRuleAcls){
								aclName=qosRuleAcl.aclName;
								var classifierID:String=qosRuleAcl.classifierID;
							}
						}
					}
					
					//根据五元组名称查找acl
					var aclGroups:Array=device.acl.aclGroups;
					for each(var aclGroup:AclGroup in aclGroups){
						var aclNumOrName:String=aclGroup.aclNumOrName;
						if(aclName==aclNumOrName){
							for each(var aclRuleBas4:AclRuleAdv4 in aclGroup){
								var aclRuleName:String=aclRuleBas4.aclRuleName;
								policyObjTmp["aclRuleName"]=aclRuleName;
								//源ip
								var aclSourceIp:String=aclRuleBas4.aclSourceIp;
								policyObjTmp["srcIp"]=aclSourceIp;
								//源端口
								var aclSrcPortOp:String=aclRuleBas4.aclSrcPortOp;
								policyObjTmp["srcPop"]=aclSrcPortOp;
								//目的ip
								var aclDestIp:String=aclRuleBas4.aclDestIp;
								policyObjTmp["desIp"]=aclDestIp;
								//目的端口
								var aclDestPortOp:String=aclRuleBas4.aclDestPortOp;
								policyObjTmp["desPop"]=aclDestPortOp;
								//协议类型  
								var aclProtocol:String=aclRuleBas4.aclProtocol;
								policyObjTmp["xType"]=aclProtocol;
							}
							break;
						}
						
					}
					policyObjTmp["classifierName"]=classifierName;
					policyObjTmp["behaviorName"]=behaviorName;
					policyObjTmp["aclName"]=aclName;
					policyArr.push(policyObjTmp);
				}
			}
			return policyArr;
		}
		
		public static function getPrototype():String
		{
			var renStr:String="";
			switch("1")
			{
				case "0":
					renStr="ip协议";
					break;
				case "47":
					renStr="gre协议";
					break;
				case "1":
					renStr="icmp协议";
					break;
				case "2":
					renStr="igmp协议";
					break;
				case "4":
					renStr="ipinip协议";
					break;
				case "6":
					renStr="tcp协议";
					break;
				case "17":
					renStr="udp协议";
					break;
				case "89":
					renStr="ospf协议";
					break;
			}
			return renStr;
		}
		public static function getAclNumOrName(numOrNames:Array,curNum:Number):String
		{
			var isexist:Boolean = false;
			var retvalue:int = 0;
			for(var j:int = curNum; j <= 3999; j++)
			{
				isexist = false;
				for(var i:int=0;i<numOrNames.length;i++){
					var numi:Number=numOrNames[i];
					if(numi==j){
						isexist = true;
						break;
					}
				}
				if (!isexist)
				{
					retvalue = j;
					break;
				}
			}
			
			return retvalue.toString();
		}
		
		public static function showOnConsole(content:String):void
		{
			if(console!=null){
				DataHandleTool.console.console.text+=content;
			}
		}
	}

}