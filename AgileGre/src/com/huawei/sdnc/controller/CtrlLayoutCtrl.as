package com.huawei.sdnc.controller
{
import com.huawei.sdnc.event.SdncEvt;
import com.huawei.sdnc.tools.ConnUtil;
import com.huawei.sdnc.tools.SdncUtil;
import com.huawei.sdnc.tools.TopoUtil;
import com.huawei.sdnc.view.common.node.ImgBgGroup;
import com.huawei.sdnc.view.common.node.RefNode;
import com.huawei.sdnc.view.common.node.ServerNode;
import com.huawei.sdnc.view.common.node.StateNode;
import com.huawei.sdnc.view.common.node.VmFollower;
import com.huawei.sdnc.view.dataCenter.ctrlV.CtrlView;
import com.huawei.sdnc.view.dataCenter.physics.FlowMonitor;
import com.huawei.sdnc.vo.TopoXmlVo;

import flash.events.Event;
import flash.events.MouseEvent;
import flash.events.TimerEvent;
import flash.geom.Point;
import flash.system.System;
import flash.utils.Dictionary;
import flash.utils.Timer;

import mx.collections.ArrayCollection;
import mx.controls.Alert;
import mx.events.FlexEvent;
import mx.managers.SystemManager;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.ResultEvent;

import twaver.AlarmSeverity;
import twaver.Consts;
import twaver.DataBox;
import twaver.ElementBox;
import twaver.Follower;
import twaver.Grid;
import twaver.Group;
import twaver.IData;
import twaver.IElement;
import twaver.Link;
import twaver.Node;
import twaver.Styles;
import twaver.network.layout.AutoLayouter;
import twaver.network.layout.SpringLayouter;
import twaver.networkx.NetworkX;
import twaver.networkx.interaction.InteractionEvent;

//20130424 by zhuyk

public class CtrlLayoutCtrl
{
	[Bindable]
	public var page:CtrlView;
	
	private var __netWork:NetworkX;
	/**转发节点组*/
	private var __transmitG:Group;
	private var __app:sdncui2;
//	private var box:ElementBox;
	/**上一次操作的元素*/
	private var __lastElement:IElement = null;
	/**上一次操作的Link类型的元素*/
	private var __lastLinkElement:IElement = null;
	/**已点击的网元*/
	private var __clickedNode:ServerNode;
	/**是否可以点击下一个网元*/
	private var __isClickNext:Boolean = true;
	/**当前已选中的网元*/
	private var __cuSelectNodes:Array = [];
	/**node状态获取定时器*/
	private var __timer:Timer;
	public function CtrlLayoutCtrl()
	{
	}
	/**
	 * 控制视图初始化
	*/
	public function init(event:FlexEvent):void
	{
//		box = page.dataBox as ElementBox;
		__netWork = page.network;
		__netWork.innerColorFunction = null;
		SdncUtil.addOverview(page.network);
		//调整布局
		page.addEventListener(SdncEvt.CTRL_LAYOUT_CHANGE,function(evt:SdncEvt):void{
			changePositionValue(evt.params);
		});
		__app = SdncUtil.app;
		__app.main.addEventListener(SdncEvt.MODULE_CHANGE,refreshTopoData);
		__app.addEventListener(SdncEvt.CTRLVIEW_DC_CHANGE,onDcChange);
		__app.addEventListener(SdncEvt.REFRESH_NODES_STATE,refreshTimerStart);
		__app.addEventListener(SdncEvt.CHANGE_NODE_LABEL_TYPE,onNodeLabelTypeChange);
		page.addEventListener(SdncEvt.CTRL_DASH_CLICK,onDashClick);
		__netWork.addEventListener(MouseEvent.MOUSE_MOVE, function(evt:MouseEvent):void{
			__lastElement = SdncUtil.handleMouseMove(evt,__netWork,__lastElement);
		});
		page.network.addInteractionListener(function(e:InteractionEvent):void{
			SdncUtil.groupSelectStyle(e.element);
			if(e.element is Link && __lastLinkElement !=e.element && e.kind ==InteractionEvent.CLICK_ELEMENT)
			{
				var link:Link = e.element as Link;
				if(link.getStyle(Styles.LINK_PATTERN))
				{
					var sEvt:SdncEvt = new SdncEvt(SdncEvt.CTRL_DASH_CLICK,e.element);
					page.dispatchEvent(sEvt);
					page.logView.visible = true;
					page.logView.includeInLayout = true;
					__lastLinkElement = e.element;
				}
			}
			else
			{
				page.logView.visible = false;
				page.logView.includeInLayout = false;
				__lastLinkElement = null;
			}		
		});
		__app.main.navPanel.createDcNameBtns();
		__timer = new Timer(SdncUtil.refreshTime * 1000);
		__timer.addEventListener(TimerEvent.TIMER,onRefreshTimer);
	}
	
	private function refreshTopoData(evt:SdncEvt):void
	{
		if(SdncUtil.isRefreshCtrlTopo)
		{
			SdncUtil.isRefreshCtrlTopo = false;
			SdncUtil.dataBoxsDicCtrl = new Dictionary;
			__app.main.navPanel.createDcNameBtns(false);
		}
	}
	
	private function onDcChange(e:SdncEvt):void
	{
		var clickedDcName:String = String(e.params);
		var hasData:Boolean = false;
		for each(var dcName:String in SdncUtil.dcHasDataArr)
		{
			if(clickedDcName == dcName)
			{
				onDc(clickedDcName);
				hasData = true;
			}
		}
		if(!hasData)Alert.show("The current dc no data!","Information");
		else __app.main.optionPanel.dispatchEvent(new SdncEvt(SdncEvt.SET_SWITCHBTN_STATE));
	}
	
	protected function onDashClick(event:SdncEvt):void
	{
		var link:Link = event.params as Link;
		if(link.fromNode is Group)
		{
			displayRESTAPILog();
			return;
		}
		if(link.fromNode.name.search("controller") != -1)
		{
			ConnUtil.getInstence().query("assets/xml/dc1/dc1_openflow_log.xml",onOFLogResult,onFault);
		}else
		{
			displayRESTAPILog()
		}
	}
	
	private function displayRESTAPILog():void
	{
		page.__logAc.removeAll();
		var o1:Object = new Object();
		o1.name = "SDN APP";
		o1.state = "left";
		o1.label = "sending the request of REST API log...";
		page.__logAc.addItem(o1);
		var o2:Object = new Object();
		o2.name = "REST API";
		o2.state = "right";
		o2.label = "request succesed.sending back log...";
		page.__logAc.addItem(o2);
		var o3:Object = new Object();
		o3.name = "SDN APP";
		o3.state = "left";
		o3.label = "receive log succesed";
		page.__logAc.addItem(o3);
	}
	
	private function onFault(event:FaultEvent):void
	{
		trace("httpserver error")
	}
	
	private function onOFLogResult(event:ResultEvent):void
	{
		var logXml:XML = event.result as XML;
		page.__logAc.removeAll();
		for each(var log:XML in logXml.syslog)
		{
			var obj:Object = new Object();
			obj.name = log.logType;
			if(log.logType == "OpenFlow")obj.state = "left";
			else obj.state = "right";
			obj.label = log.logContent
			page.__logAc.addItem(obj);
		}
	}
	
	/**
	 * 创建controller及openstack等
	 * @param box:当前要写入的box
	 */
	private function createControllerAndSoOn(box:ElementBox):void
	{
		var xml:XML = SdncUtil.cuProjectXML;
		//将APP和OPENSTACK放到一个GROUP里
		var appG:Group = TopoUtil.createGroup(box,"app_openstack","","ctrl_app",0.6,Consts.SHAPE_OVAL);
		appG.setStyle(Styles.GROUP_FILL_ALPHA, 0.7);
		appG.setStyle(Styles.GROUP_PADDING_TOP, 40);
		appG.setStyle(Styles.GROUP_PADDING_BOTTOM, 80);
		appG.setStyle(Styles.GROUP_FILL_COLOR, 0x31435c);
		appG.setStyle(Styles.GROUP_OUTLINE_COLOR, 0x184a65);
		appG.setStyle(Styles.GROUP_GRADIENT,Consts.GRADIENT_SPREAD_NORTH);
		appG.setStyle(Styles.GROUP_GRADIENT_ALPHA,0.8);
		appG.setStyle(Styles.GROUP_GRADIENT_COLOR,0x155067);
		appG.expanded = true;
		
		var appN:Node = TopoUtil.createNode(box,RefNode,"app","APP","ctrl_app",new Point(750,30));
		appN.setStyle(Styles.LABEL_POSITION,Consts.POSITION_BOTTOM_BOTTOM);
		appG.addChild(appN);
		
		var openstack:String = xml.openstack;
		if( openstack != null && openstack != "")
		{
			var openN:Node = TopoUtil.createNode(box,RefNode,"openstack","OPENSTACK","ctrl_openstack", new Point(830,25));
			openN.setStyle(Styles.LABEL_POSITION,Consts.POSITION_BOTTOM_BOTTOM);
			appG.addChild(openN);
		}
		
		var restN:Node = TopoUtil.createNode(box,Node,"restAPI","REST API","ctrl_restAPI",new Point(800,135));
		var gLink:Link = TopoUtil.createLinkDash(box,appG,restN,.6,0);
		gLink.setStyle(Styles.LINK_FROM_POSITION,Consts.POSITION_BOTTOM);
		gLink.setStyle(Styles.LINK_TO_YOFFSET,-5);
		
		//创建放controller的group
		var ctrlG:Group = TopoUtil.createGroup(box,"controller_group","","AREANode",0,Consts.SHAPE_ROUNDRECT);
		//ctrlG.setStyle(Styles.GROUP_PADDING_TOP,30);
		ctrlG.setStyle(Styles.GROUP_PADDING_BOTTOM,150);
		ctrlG.expanded = true;
		
		//遍历获取controller
		var i:int = 0;
		var ctrlPositionA:Array = [new Point(615,251),new Point(961,251),new Point(561,251),new Point(1161,251)];
		for each(var controller:XML in xml.controllers.controller)
		{
			var ctrlName:String = controller.name;
			var ctrlN:Node = TopoUtil.createNode(box,RefNode,ctrlName,ctrlName,"Controller",ctrlPositionA[i]);
			ctrlG.addChild(ctrlN);
			var ctrlLink:Link = TopoUtil.createLinkDash(box,restN,ctrlN,.6,0);
			ctrlLink.setStyle(Styles.LINK_FROM_POSITION,Consts.POSITION_BOTTOM);
			i++;
		}
	}
	/**
	 * 处理相应的DC,根据dcName取得相应的XML,然后请求XML里的拓扑文件路径
	 * @param dcName:要展现拓扑关系的DC名字
	 */
	public function onDc(dcName:String):void
	{
		clearCuDCAndXmlData();
		var xml:XML = SdncUtil.dcMap[dcName];
		var cuBox:ElementBox;
		var elementBox:ElementBox = SdncUtil.dataBoxsDicCtrl[dcName];
		if(elementBox == null)
		{
			cuBox = new ElementBox;
			SdncUtil.dataBoxsDicCtrl[dcName] = cuBox;
		
			createControllerAndSoOn(cuBox);
			
			//当前拓扑节点所属controller
			var cuDcCtrl:String = String(xml.controller);
			//创建放所有转发节点的group
			__transmitG = TopoUtil.createGroup(cuBox,"transmitG_group","",SdncUtil.imagesObjects["Cloud"],0.8,Consts.SHAPE_ROUNDRECT);
			__transmitG.setSize(121,79);
			__transmitG.setStyle(Styles.GROUP_PADDING_TOP, 30);
			__transmitG.setStyle(Styles.GROUP_PADDING_BOTTOM, 60);
			__transmitG.expanded = true;
//			var cuDcUrlInfo:Array = SdncUtil.dcUrlInfos[dcName];
			var topoXmlVo:TopoXmlVo = SdncUtil.dcTopoXmlDic[dcName];
			if(!topoXmlVo)
			{
				Alert.show("no data!");
				return;
			}
			if(xml.hasOwnProperty("datafile"))
			{
				var l2topoXml:XML = topoXmlVo._l2topoXml;
				var lldpXml:XML = topoXmlVo._lldpXml;
				var vclusterXml:XML = topoXmlVo._vclusterXml;
				onNodeAndLink(cuBox,cuDcCtrl,topoXmlVo._dcName,l2topoXml,lldpXml,vclusterXml);
			}
		}
		else cuBox = elementBox;
		__netWork.elementBox = cuBox;
		page.curBox = cuBox;
	}
	
	/**
	 * 移除BOX里当前DC数据,初始化当前DC的XML
	 */
	private function clearCuDCAndXmlData():void
	{
		coreArr = [];
		aggArr = [];
		torArr = [];
		computerArr = [];
		linkArr = [];
		dashLinkArr = [];
		__cuSelectNodes = [];
	}
	
	private var coreArr:Array = [];
	private var aggArr:Array = [];
	private var torArr:Array = [];
	private var computerArr:Array = [];
	private var remoteArr:Array = [];
	private var linkArr:Array = [];
	private var dashLinkArr:Array = [];
	/**
	 * 遍历XML找到NODE并创建,然后根据XML里线的关系将NODE连线
	 * @param box:需要写入的box
	 * @param cuDcCtrl:当前DC的controller名字
	 * @param l2topoXml:l2拓扑关系XML
	 * @param lldpXml:远程computer和节点的拓扑关系XML
	 * @param vclusterXml:节点详细信息XML
	 */
	private function onNodeAndLink(box:ElementBox,cuDcCtrl:String,cuDcName:String,l2topoXml:XML,lldpXml:XML,vclusterXml:XML):void
	{
		if(l2topoXml)
		{
			var topoXmlVo:TopoXmlVo = SdncUtil.dcTopoXmlDic[cuDcName];
			var vcFpInfoDic:Dictionary = new Dictionary();
			for each(var nodeInfo:XML in vclusterXml.vcFpInfos.vcFpInfo)
			{
				vcFpInfoDic[cuDcName + "_" + nodeInfo.fpId] = nodeInfo;
			}
			
			var nodes:XMLList = l2topoXml.l2toponodes.l2toponode;
			var links:XMLList = l2topoXml.l2topolinks.l2topolink;
			for each(var cuNode:XML in nodes)
			{
				var nodeId:String = cuDcName + "_" + cuNode.fpID;
				var nodeName:String = cuNode.systemName;
				var nodeType:String = cuNode.nodeType;
				var img:String = SdncUtil.imagesObjects[nodeType];
				var node:Node = TopoUtil.createNode(box,StateNode,nodeId,nodeName,img);
				if(!(node as StateNode).nodeInfo)
					(node as StateNode).nodeInfo = topoXmlVo._fpNodeMap[node.id];
				__transmitG.addChild(node);
//				node.setSize(50,53);
//				if(node is StateNode) (node as StateNode).nodeInfo = vcFpInfoDic[nodeId];
				if(nodeType == "CORE"){	 coreArr.push(node); }
				else if(nodeType == "AGG"){ aggArr.push(node); }
				else if(nodeType == "TOR"){ 
					torArr.push(node);
					if(lldpXml)onLldpXml(box,node,lldpXml,cuDcName);
				}
			}
			for each(var lineXml:XML in links)
			{
				var fromN:Node = TopoUtil.getNodeById(box, cuDcName + "_" + lineXml.leftFPID);
				var toN:Node = TopoUtil.getNodeById(box, cuDcName + "_" + lineXml.rigthFPID);
				var cuLink:Link = TopoUtil.createLink(box,fromN,toN);
				linkArr.push(cuLink);
			}
			var ctrlNode:Node = null;
			if(box.containsByID(cuDcCtrl)){
				ctrlNode = box.getDataByID(cuDcCtrl) as Node;;
			}
			linkToCtrl(box,coreArr,ctrlNode);
			linkToCtrl(box,aggArr,ctrlNode);
			linkToCtrl(box,torArr,ctrlNode);
			linkToCtrl(box,computerArr,ctrlNode);
		}
		topoDoLayout();
	}
	/**
	 * 根据NODE找到相关联的远程COMPUTER,创建并与NODE连线
	 * @param box:当前写入的box
	 * @param node:跟远程computer相连的节点
	 * @param xml:远程computer和节点的拓扑关系XML
	 * @param cuDcName:当前DC的名字
	 */
	private function onLldpXml(box:ElementBox,node:Node,xml:XML,cuDcName:String):void
	{
		for each(var lldpIfTopo:XML in xml.lldpIfsTopo.lldpIfTopo)
		{
			if(node.id == cuDcName + "_" + lldpIfTopo.fpID)
			{
				var computerG:Group = TopoUtil.createGroup(box,"sheft"+lldpIfTopo.fpID,"sheft"+lldpIfTopo.fpID,
					SdncUtil.imagesObjects["Computer"],0.8,Consts.SHAPE_ROUNDRECT);
				__transmitG.addChild(computerG);
				computerG.setSize(50,53);
				computerArr.push(computerG);
				var remotes:Array = [];
				for each(var remoteInfo:XML in lldpIfTopo.lldpRemoteInfos.lldpRemoteInfo)
				{
					var remoteN:Node = TopoUtil.createNode(box,ServerNode,remoteInfo.chassisId,remoteInfo.systemName);
					remoteN.setSize(35,38);
					if(remoteN is ServerNode)
					{
						(remoteN as ServerNode)._remoteInfo = remoteInfo;
					}
					computerG.addChild(remoteN);
					var link:Link = TopoUtil.createLink(box,node,remoteN);
					linkArr.push(link);
					remotes.push(remoteN);
				}
				remoteArr.push(remotes);
			}
		}
	}
	/**
	 * 将节点连接到controller
	 * @param box:要写入的box
	 * @param arr:要连接的节点数组
	 * @param ctrlN:要连接的controller节点
	 */
	private function linkToCtrl(box:ElementBox,arr:Array,ctrlN:Node):void
	{
		ctrlN.setStyle(Styles.ICONS_NAMES,["ctrl_openflow"]);
		ctrlN.setStyle(Styles.ICONS_POSITION, Consts.POSITION_BOTTOM_BOTTOM);
		for(var i:* in arr)
		{
			var cuDashLine:Link = TopoUtil.createLinkDash(box,ctrlN,arr[i],.6);
			dashLinkArr.push(cuDashLine);
			cuDashLine.setStyle(Styles.LINK_TO_POSITION,Consts.POSITION_TOP);
		}
	}
	
	private var __centerP:Point = new Point(640,460);
	private var __xGap:Number = 150;
	private var __yGap:Number = 115;
	/**
	 * 调整网元参考中心点和网元间隔的值
	 */
	private function changePositionValue(evt:Event):void
	{
		var o:Object = evt.target;
		if(o.id == "horizontalGap") __xGap = o.value;
		else if(o.id == "verticalGap") __yGap = o.value;
		else if(o.id == "centerX") __centerP.x = o.value;
		else if(o.id == "centerY") __centerP.y = o.value;
		if(evt.type == MouseEvent.CLICK) topoDoLayout();
	}
	
	/**
	 * 布局
	 */
	private function topoDoLayout():void
	{
		TopoUtil.cycleSetNodeLocation(coreArr,__xGap,__yGap,__centerP,0);
		TopoUtil.cycleSetNodeLocation(aggArr,__xGap,__yGap,__centerP,1);
		TopoUtil.cycleSetNodeLocation(torArr,__xGap,__yGap,__centerP,2);
		TopoUtil.cycleSetNodeLocation(computerArr,__xGap,__yGap,__centerP,3);
	}
	
	protected function onNodeLabelTypeChange(event:SdncEvt):void
	{
		page.curBox.forEachByBreadthFirst(function(item:IData):void{
			if(item is StateNode)
			{
				var node:StateNode = item as StateNode;
				node.changeLabel(event.params);
			}
		});
	}
	
	/**
	 *每过一段时间刷新所有的节点信息 
	 *
	 */
	private function refreshTimerStart(event:SdncEvt):void
	{
		if(!__app.main.currentState == "ctrl_view") return;
		if(event.params == "ON")
		{
			refreshAllNodes();
			__timer.start();
		}else
		{
			__timer.reset();
			__timer.delay = 500;
		}
	}
	
	protected function onRefreshTimer(event:TimerEvent):void
	{
		refreshAllNodes();
	}
	
	private function refreshAllNodes():void
	{
		page.curBox.forEachByBreadthFirst(function(item:IData):void{
			if(item is StateNode)
			{
				var node:StateNode = item as StateNode;
				node.updata("assets/xml/dc1/dc1_devm.xml");
			}
		});
	}
}
}