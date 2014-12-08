package com.huawei.sdnc.controller
{
import com.huawei.sdnc.event.SdncEvt;
import com.huawei.sdnc.service.SdnService;
import com.huawei.sdnc.tools.ConnUtil;
import com.huawei.sdnc.tools.Images;
import com.huawei.sdnc.tools.OverlieUtil;
import com.huawei.sdnc.tools.SdncUtil;
import com.huawei.sdnc.tools.TopoUtil;
import com.huawei.sdnc.tools.VMMoveUtil;
import com.huawei.sdnc.view.ISerializable;
import com.huawei.sdnc.view.common.node.ServerNode;
import com.huawei.sdnc.view.common.node.StateNode;
import com.huawei.sdnc.view.common.node.VmFollower;
import com.huawei.sdnc.view.dataCenter.physics.FlowMonitor;
import com.huawei.sdnc.view.dataCenter.physics.PhysicsLayout;
import com.huawei.sdnc.vo.TopoXmlVo;

import flash.events.Event;
import flash.events.MouseEvent;
import flash.events.TimerEvent;
import flash.geom.Point;
import flash.utils.Dictionary;
import flash.utils.Timer;

import mx.collections.ArrayCollection;
import mx.controls.Alert;
import mx.controls.Label;
import mx.events.FlexEvent;
import mx.events.PropertyChangeEvent;
import mx.events.ResizeEvent;
import mx.managers.CursorManager;

import twaver.Alarm;
import twaver.AlarmSeverity;
import twaver.Consts;
import twaver.ElementBox;
import twaver.Follower;
import twaver.Grid;
import twaver.Group;
import twaver.IData;
import twaver.IElement;
import twaver.Link;
import twaver.Node;
import twaver.Styles;
import twaver.Utils;
import twaver.XMLSerializer;
import twaver.network.Network;
import twaver.network.layout.AutoLayouter;
import twaver.network.layout.SpringLayouter;
import twaver.networkx.NetworkX;
import twaver.networkx.interaction.InteractionEvent;

public class PhysicsLayoutCtrl
{
	[Bindable]
	public var page:PhysicsLayout;
	/**平面布局用到的容器*/
	private var __eBox:ElementBox;
	/**Twaver拓扑组件*/
	private var __netWork:NetworkX;
	/**上一次操作的网元*/
	private var __lastElement:IElement = null;
	
	/**暂存CORE类型节点*/
	private var __coreArr:Array = [];
	/**暂存AGG类型节点*/
	private var __aggArr:Array = [];
	/**暂存TOR类型节点*/
	private var __torArr:Array = [];
	/**暂存computer类型节点*/
	private var __computerArr:Array = [];
	/**暂存remote远程节点*/
	private var __remoteArr:Array = [];
	/**暂存link*/
	private var __linkArr:Array = [];
	/**中心参考点*/
	private var __centerP:Point = new Point(800,50);
	/**自定义布局X轴间隔*/
	private var __xGap:Number = 150;
	/**自定义布局Y轴间隔*/
	private var __yGap:Number = 150;
	/**定时器，用于定时获取设备CPU和RAM状态*/
	private var __timer:Timer;
	private var __serializeDcDics:Dictionary = new Dictionary();
	private var __serviceArr:Array = [];
	private var __count:int = 0;
	private var __sourceGrid:Grid;
	private var __sourceCellObj:Object;
	[Bindable]
	private var vmMoveUtil:VMMoveUtil = VMMoveUtil.getInstence();
	
	public function PhysicsLayoutCtrl()
	{
	}
	
	/** 物理视图初始化组件和box */
	public function onInit(event:FlexEvent):void
	{
		if(SdncUtil.cuProjectType != "normal")
		{
			page.overlieUtil.changedVlantopo19 = page.changedVlantopo19;
			page.overlieUtil.changedVlantopo20 = page.changedVlantopo20;
		}
		page._app = SdncUtil.app;
		__eBox = new ElementBox();
		__netWork = page.network;
		__netWork.elementBox = __eBox;
		__netWork.innerColorFunction = null;
		SdncUtil.addOverview(__netWork);
		page.autoLayouter = new AutoLayouter(__netWork);
		page.springLaouter = new SpringLayouter(__netWork);
		initBox();
		page.isRoot = true;
		page._app.addEventListener(SdncEvt.PHYSICSVIEW_DC_CHANGE,onDcChange);
		page._app.addEventListener(SdncEvt.PHYSICSVIEW_BACK_TO_ROOT,getNormalDC);
		page._app.addEventListener(SdncEvt.REFRESH_NODES_STATE,refreshTimerStart);
		page._app.addEventListener(SdncEvt.CHANGE_NODE_LABEL_TYPE,onNodeLabelTypeChange);
		page._app.addEventListener(SdncEvt.CHANGE_VM_LABEL_TYPE,onVmLabelTypeChange);
		page._app.addEventListener(SdncEvt.VM_MOVE,vmMoveUtil.onVmMove);
		page._app.addEventListener(SdncEvt.PHYSICSVIEW_PING_START,page.doPingTestHandler);
		page._app.addEventListener(SdncEvt.OVERLIE_VDC,page.overlieVdc);
		page._app.addEventListener(SdncEvt.CLEAROVERLIE_VDC,page.overlieVdc);
		__netWork.elementBox.addDataPropertyChangeListener(TopoUtil.handleExpand);
		__netWork.addEventListener(MouseEvent.MOUSE_MOVE, function(evt:MouseEvent):void{
			__lastElement = SdncUtil.handleMouseMove(evt,__netWork,__lastElement);
		});
		page.addEventListener(ResizeEvent.RESIZE,function(e:ResizeEvent):void{
			page.network.height = page.height;
			page.network.width = page.width;
		});
		__netWork.addInteractionListener(onInteractionEvent);
		page.cuNetBox = __eBox;
		__timer = new Timer(SdncUtil.refreshTime * 1000);
		__timer.addEventListener(TimerEvent.TIMER,refreshAllNodes);
	}
	
	/**处理TOPO的InteractionEvent*/
	private function onInteractionEvent(e:InteractionEvent):void
	{
		SdncUtil.groupSelectStyle(e.element);
		if(e.kind == InteractionEvent.CLICK_ELEMENT)
		{
			var sEvt:SdncEvt = new SdncEvt(SdncEvt.TOPO_SELECTED_ITEM,e.element);
			page._app.dispatchEvent(sEvt);
		}
		else if(e.kind == InteractionEvent.DOUBLE_CLICK_ELEMENT)
		{
			var element:IElement = e.element;
			if(element is Node) onElementDoubleClick(element);
			if(element is Link)
			{
				var p:Point =new Point(e.mouseEvent.localX,e.mouseEvent.localY);
				page._app.dispatchEvent(new SdncEvt(SdncEvt.OPEN_FLOWMONITOR,p));
			}
		}
		else if(e.kind == InteractionEvent.CLICK_BACKGROUND)
		{
			page._app.dispatchEvent(new SdncEvt(SdncEvt.CLOSE_FLOWMONITOR));
		}
		if(e.kind == InteractionEvent.LAZY_MOVE_START || e.kind == InteractionEvent.LIVE_MOVE_START)//拖动虚拟机时的逻辑处理
		{
			var selectNode:IData = page.cuNetBox.selectionModel.lastData;
			if(selectNode is VmFollower) 
			{
				var vm:VmFollower = selectNode as VmFollower;
				if(vm.host is Grid )
				{
					__sourceGrid = vm.host as Grid;
					var point:Point = __netWork.getLogicalPoint(e.mouseEvent);
					__sourceCellObj = __sourceGrid.getCellObject(point);//将虚拟机的原始位置信息保存起来，以备未拖到位弹回原位置时使用
				}
			}
		}
		if(e.kind == InteractionEvent.LAZY_MOVE_END || e.kind == InteractionEvent.LIVE_MOVE_END){
			var selNode:IData = page.cuNetBox.selectionModel.lastData;
			if(selNode is VmFollower)
			{
				SdncUtil.dragedVM = selNode as VmFollower;//将拖动的VM保存成全局，待发送迁移请求的时候使用
				SdncUtil.processHost(page,__netWork,selNode as Follower,__sourceGrid,__sourceCellObj,e.mouseEvent,page.isOverlieCleared);//拖到另外一个GRID时的处理函数								
			}
		}
	}
	
	/**
	 * 初始化BOX
	 * 根据url获取数据并添加相应的网元
	 */
	private function initBox():void
	{
		for each(var cuDcName:String in SdncUtil.dcNameArr)
		{
			var dcinfo:XML = SdncUtil.dcMap[cuDcName];
			var cuGroup:Group;
			var urlArr:Array = SdncUtil.dcUrlInfos[cuDcName];
			if(urlArr != null && urlArr.length > 0)
			{
				var sdnService:SdnService = new SdnService();
				sdnService._topoXmlVo = new TopoXmlVo();
				sdnService.topo12InfoQuery(urlArr[0]);
//				sdnService.lldpQuery(urlArr[1]);
				sdnService.vclusterQuery(urlArr[2]);
				sdnService.vlantopoQuery(urlArr[3]);
				sdnService.vnmQuery(urlArr[4]);
				sdnService._topoXmlVo._dcName = cuDcName;
				__serviceArr.push(sdnService);
				sdnService._topoXmlVo.addEventListener(SdncEvt.L2TOPO_DATA_IS_READY,function(evt:SdncEvt):void{
					var topoXmlVo:TopoXmlVo = evt.currentTarget as TopoXmlVo;
					var dcName:String = topoXmlVo._dcName;
					SdncUtil.dcTopoXmlDic[dcName] = topoXmlVo;
					cuGroup = onDc(dcName,topoXmlVo._l2topoXml);
					cuGroup.name = SdncUtil.dcMap[dcName].desc;
					topoXmlVo.addEventListener(SdncEvt.NODE_STATE_ERROE,onNodeStateError);
 					page.groupLayout(cuGroup);
				});
				sdnService._topoXmlVo.addEventListener(SdncEvt.VNM_VLANTOPO_IS_READY,function(evt:SdncEvt):void{
					var topoXmlVo:TopoXmlVo = evt.currentTarget as TopoXmlVo;
					var dcName:String = topoXmlVo._dcName;
					SdncUtil.dcTopoXmlDic[dcName] = topoXmlVo;
					var vdcNameArr:Array = SdncUtil.vdcNameArr;
					for each(var cuVdc:XML in topoXmlVo._vnmXml.network)
					{
						var networkName:String = String(cuVdc.networkName);
						if(!SdncUtil.isExistInArr(vdcNameArr,networkName))
						{
							vdcNameArr.push(networkName);
						}
					}
					page._app.main.navPanel.matrixSelector.initMatrixSelector();
				});
			}
			else
			{
				cuGroup = TopoUtil.createGroup(__eBox,dcinfo.name,dcinfo.desc +
					"-----No Data","CloudGroup");
				page.groupLayout(cuGroup);
			}
			SdncUtil.currentDcName = "dc";
		}
	}
	
	/**
	 * 创建每个dctopo图
	 * @param l2topoXml:当前DC的拓扑关系XML
	 * @param lldpXml:当前DC的server拓扑关系
	 * @param vclusterXml:节点详细信息XML 
	 */
	private function onDc(cuDcName:String,l2topoXml:XML):Group
	{
		var dcG:Group = TopoUtil.createGroup(__eBox,l2topoXml.@controller,l2topoXml.@controller,
			SdncUtil.imagesObjects.Cloud,0.6,Consts.SHAPE_ROUNDRECT);
		dcG.setStyle(Styles.GROUP_PADDING,10);
		
		initArrary();
		var topoXmlVo:TopoXmlVo = SdncUtil.dcTopoXmlDic[cuDcName]
		for each(var cuNode:XML in l2topoXml.l2toponodes.l2toponode)
		{
			var cuNodeType:String = String(cuNode.nodeType);
			var node:Node = TopoUtil.createNode(__eBox,StateNode,cuDcName + "_" + cuNode.fpID,cuNode.systemName,
				SdncUtil.imagesObjects[cuNodeType],new Point(cuNode.x,cuNode.y));
			if(!(node as StateNode).nodeInfo)
				(node as StateNode).nodeInfo = topoXmlVo._fpNodeMap[node.id];
			if(cuNodeType == "CORE")
				__coreArr.push(node);
			else if(cuNodeType == "AGG")
				__aggArr.push(node);
			else if(cuNodeType == "TOR")
				__torArr.push(node);
			
			if(cuNode.hasOwnProperty("location"))
			{
				var location:String = cuNode.location[0];
				if(location.search("area") != -1)
				{
					var locationArr:Array = location.split("/");
					var cuAreaId:String = cuDcName + locationArr[1];
					var cuAreaName:String = locationArr[1];
					var areaG:Group = TopoUtil.createGroup(__eBox,cuAreaId,cuAreaName,SdncUtil.imagesObjects.AREA,0.6);
					areaG.addChild(node);
					if(dcG.childrenCount == 0)
					{
						dcG.addChild(areaG);
					}
					else
					{
						dcG.children.forEach(function(data:IData):void{
							if(data.id != cuAreaId)
							{
								dcG.addChild(areaG);
							}
						});
					}
				}
				else
				{
					dcG.addChild(node);
				}
			}
			else
			{
				dcG.addChild(node);
			}
			/*if(cuNode.nodeType == "TOR")
			{
				if(__sdnService._topoXmlVo._lldpXml == null)
				{
					var urlArr:Array = SdncUtil.dcUrlInfos[cuDcName];
					__sdnService.lldpQuery(urlArr[1]);
					__sdnService._topoXmlVo.addEventListener(SdncEvt.LLDP_DATA_IS_READY,function(e:SdncEvt):void{
						createComputerLevel(__sdnService._topoXmlVo._lldpXml,__eBox,cuDcName,node,areaG);
					});
				}
			} */
		}
		if(!SdncUtil.cordinateFlag)
			topoDoLayout();
		CreateLinkByDC(l2topoXml,cuDcName);
		if(__count == SdncUtil.dcHasDataArr.length - 1)
		{
			loadAndCreateLLdp();
		}
		__count++;
		return dcG;
	}
	
	private function loadAndCreateLLdp():void
	{
		var len:int = __serviceArr.length;
		for(var i:int = 0; i < len ; i++)
		{
			var sdnService:SdnService = (__serviceArr[i] as SdnService);
			var urlArr:Array = SdncUtil.dcUrlInfos[sdnService._topoXmlVo._dcName];
			sdnService.lldpQuery(urlArr[1]);
			sdnService._topoXmlVo.addEventListener(SdncEvt.LLDP_DATA_IS_READY,function(evt:SdncEvt):void{
				var topoXmlVo:TopoXmlVo = evt.currentTarget as TopoXmlVo;
				if(topoXmlVo._lldpXml==null) return;
				__eBox.forEachByBreadthFirst(function(item:IData):void{
					if(item is StateNode)
					{
						var node:StateNode = item as StateNode;
						if(node.image == "TORNode")
						{
							var areaG:Group;
							if((node.parent as Group).name.search("area") != -1)
							{
								areaG = node.parent as Group;
							}else
							{
								areaG = null;
							}
							createComputerLevel(topoXmlVo._lldpXml,__eBox,topoXmlVo._dcName,node,areaG);
						}
					}
				});
			});
		}
	}
	
	/**
	 * 通过DCXML画线
	 * @param dc:当前DC拓扑关系XML
	 */
	private function CreateLinkByDC(dc:XML,dcName:String):void
	{
		for each(var lineXml:XML in dc.l2topolinks.l2topolink)
		{
			var fromN:Node = TopoUtil.getNodeById(__eBox,dcName + "_" + lineXml.leftFPID);
			var toN:Node = TopoUtil.getNodeById(__eBox,dcName + "_" + lineXml.rigthFPID);
			var cuLink:Link = TopoUtil.createLink(__eBox,fromN,toN);
			cuLink.setClient("leftIfName", String(lineXml.leftIfName));
			cuLink.setClient("rightIfName", String(lineXml.rightIfName));
			cuLink.setClient("leftFPID", String(lineXml.leftFPID));
			cuLink.setClient("rightFPID", String(lineXml.rigthFPID));
		}
	}
	
	/**进入单个DC视图
	 * cordinateFlag = true
	 * @param dcName:要进入的dc名字,默认为dc1
	 */
	private function goToDcByXY(dcName:String = "dc1"):void
	{
		
		SdncUtil.currentDcName = dcName;
		var topoXmlVo:TopoXmlVo = SdncUtil.dcTopoXmlDic[dcName];
		var dc:XML = topoXmlVo._l2topoXml;
		var cuBox:ElementBox;
		if(dc)
		{
			var elementBox:ElementBox = SdncUtil.dataBoxsDic[dcName];
			if(elementBox == null)
			{
				cuBox = new ElementBox;
				SdncUtil.dataBoxsDic[dcName] = cuBox;
				vmMoveUtil.initGoToFlagValue(cuBox,dcName);
				for each(var lineXml:XML in dc.l2topolinks.l2topolink)
				{
					var fromN:Node = TopoUtil.getOrCreateNodeById(cuBox, dc, lineXml.leftFPID, dc.l2toponodes.l2toponode);
					var toN:Node = TopoUtil.getOrCreateNodeById(cuBox, dc, lineXml.rigthFPID, dc.l2toponodes.l2toponode);
					if(!(fromN as StateNode).nodeInfo)(fromN as StateNode).nodeInfo = topoXmlVo._fpNodeMap[fromN.id];
					if(!(toN as StateNode).nodeInfo)(toN as StateNode).nodeInfo = topoXmlVo._fpNodeMap[toN.id];
					TopoUtil.createLink(cuBox,fromN,toN);
				}
				cuBox.forEachByBreadthFirst(function(item:IData):void{
					if(item is StateNode)
					{
						if(item.name.search("TOR") != -1)
						{
							createComputerLevel(topoXmlVo._lldpXml,cuBox,dcName,item as StateNode);
						}
					}
				});
			}
			else
			{
				cuBox = elementBox;
			}
			__netWork.elementBox = cuBox;
			page.cuNetBox = cuBox;
		}
		else
		{
			trace(dcName + "无数据!");
		}
		
	}
	/**
	 * 根据XML创建SEVER层
	 * @param computerLevelXml:远端computer层拓扑关系XML
	 * @param box:当前写入的box
	 * @param parentNode:与server层相连的node
	 * @param area:机房分组,默认为null，在多dc显示状态下需要传入值
	 */
	private function createComputerLevel(computerLevelXml:XML,box:ElementBox,dcname:String,parentNode:Node,area:Group = null):void
	{
		if(computerLevelXml == null)return;
		for each(var nodeXml:XML in computerLevelXml.lldpIfsTopo.lldpIfTopo)
		{
			if(parentNode.id == (dcname + "_" + String(nodeXml.fpID)))
			{
				var shelf:Group = TopoUtil.createGroup(box,dcname + "shelf" + nodeXml.fpID , "shelf" + nodeXml.fpID ,
					SdncUtil.imagesObjects.Computer,0.6,Consts.SHAPE_ROUNDRECT);
				for each(var remoteXml:XML in nodeXml.lldpRemoteInfos.lldpRemoteInfo)
				{
					var romoteNode:Node = TopoUtil.createNode(box,ServerNode,dcname + String(remoteXml.systemName) ,remoteXml.systemName,"");
					romoteNode.setSize(35,38);
					shelf.addChild(romoteNode);
					if(romoteNode is ServerNode)
					{
						(romoteNode as ServerNode)._remoteInfo = remoteXml;
					}
					TopoUtil.createLink(box,parentNode,romoteNode);
				}
				if(area)
				{
					area.addChild(shelf);
				}
				var subNodeW:Number = (shelf.children.getItemAt(0) as Node).width;
				TopoUtil.setGroupChildrenLocation(shelf,subNodeW,parentNode,10,150);
				__computerArr.push(shelf);
				break;
			}
		}
	}
	/**处理相应的DC,根据dcName取得相应的XML,然后请求XML里的拓扑文件路径
	 * cordinateFlag = false
	 * @param dcName:相应DC的名字
	 */
	private function goToDcCustom(dcName:String):void
	{
//		clearCuDCAndXmlData();
		SdncUtil.currentDcName = dcName;
		var xml:XML = SdncUtil.dcMap[dcName];
//		var cuDcUrlInfo:Array = SdncUtil.dcUrlInfos[dcName];
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
			onNodeAndLink(l2topoXml,lldpXml,vclusterXml);
		}
	}
	
	/**
	 * 遍历XML找到NODE并创建,然后根据XML里线的关系将NODE连线
	 * @param l2topoXml:dc的拓扑关系XML
	 * @param lldpXml:远程computer的拓扑关系XML
	 * @param vclusterXml:节点的详细信息XML
	 */
	private function onNodeAndLink(l2topoXml:XML,lldpXml:XML,vclusterXml:XML):void
	{
		if(l2topoXml)
		{
			var cuBox:ElementBox;
			var dcName:String = SdncUtil.currentDcName;
			var topoXmlVo:TopoXmlVo = SdncUtil.dcTopoXmlDic[dcName];
			var elementBox:ElementBox = SdncUtil.dataBoxsDic[dcName];
			if(elementBox == null)
			{
				cuBox = new ElementBox;
				SdncUtil.dataBoxsDic[dcName] = cuBox;
				vmMoveUtil.initGoToFlagValue(cuBox,dcName);
				var vcFpInfoDic:Dictionary = new Dictionary();
				var nodes:XMLList = l2topoXml.l2toponodes.l2toponode;
				var links:XMLList = l2topoXml.l2topolinks.l2topolink;
				for each(var cuNode:XML in nodes)
				{
					var nodeId:String = dcName + "_" + cuNode.fpID;
					var nodeName:String = cuNode.systemName;
					var nodeType:String = cuNode.nodeType;
					var img:String = SdncUtil.imagesObjects[nodeType];
					var node:Node = TopoUtil.createNode(cuBox,StateNode,nodeId,nodeName,img);
//					transmitG.addChild(node);
					node.setSize(50,53);
					if(node is StateNode && !(node as StateNode).nodeInfo) 
						(node as StateNode).nodeInfo = topoXmlVo._fpNodeMap[node.id];
					if(nodeType == "CORE"){	 __coreArr.push(node); }
					else if(nodeType == "AGG"){ __aggArr.push(node); }
					else if(nodeType == "TOR"){ 
						__torArr.push(node);
						if(lldpXml != null)onLldpXml(cuBox,node,lldpXml,dcName);
					}
				}
				for each(var lineXml:XML in links)
				{
					var fromN:Node = TopoUtil.getNodeById(cuBox, dcName + "_" + lineXml.leftFPID);
					var toN:Node = TopoUtil.getNodeById(cuBox, dcName + "_" + lineXml.rigthFPID);
					var cuLink:Link = TopoUtil.createLink(cuBox,fromN,toN);
					__linkArr.push(cuLink);
				}
				topoDoLayout();
			}
			else
			{
				cuBox = elementBox;
			}
			__netWork.elementBox = cuBox;
			page.cuNetBox = cuBox;
		}
	}
	/**根据NODE找到相关联的远程COMPUTER,创建并与NODE连线
	 * @param box:当前写入的box
	 * @param node:与host相连的node
	 * @param xml:host与l2topo的关系XML
	 * @param cuDcName:当前DC名字
	 */
	private function onLldpXml(box:ElementBox,node:Node,xml:XML,cuDcName:String):void
	{
		for each(var lldpIfTopo:XML in xml.lldpIfsTopo.lldpIfTopo)
		{
			if(node.id == cuDcName + "_" + lldpIfTopo.fpID)
			{
				var computerG:Group = TopoUtil.createGroup(box,"sheft"+lldpIfTopo.fpID,"sheft"+lldpIfTopo.fpID,
					SdncUtil.imagesObjects["Computer"],0.8,Consts.SHAPE_ROUNDRECT);
				computerG.setSize(50,53);
				__computerArr.push(computerG);
				var remotes:Array = [];
				for each(var remoteInfo:XML in lldpIfTopo.lldpRemoteInfos.lldpRemoteInfo)
				{
					var remoteN:Node = TopoUtil.createNode(box,ServerNode,cuDcName + "_" + String(remoteInfo.chassisId),remoteInfo.systemName);
					remoteN.setSize(35,38);
					if(remoteN is ServerNode)
					{
						(remoteN as ServerNode)._remoteInfo = remoteInfo;
					}
					computerG.addChild(remoteN);
					var link:Link = TopoUtil.createLink(box,node,remoteN);
					__linkArr.push(link);
					remotes.push(remoteN);
				}
				__remoteArr.push(remotes);
			}
		}
	}
	
	/**
	 * 布局
	 */
	public function topoDoLayout():void
	{
		TopoUtil.cycleSetNodeLocation(__coreArr,__xGap,__yGap,__centerP,0);
		TopoUtil.cycleSetNodeLocation(__aggArr,__xGap,__yGap,__centerP,1);
		TopoUtil.cycleSetNodeLocation(__torArr,__xGap,__yGap,__centerP,2);
		TopoUtil.cycleSetNodeLocation(__computerArr,__xGap,__yGap,__centerP,3);
	}
	
	/**
	 *清空数组 
	 * 
	 */
	private function initArrary():void
	{
		__coreArr = [];
		__aggArr = [];
		__torArr = [];
		__computerArr = [];
		__linkArr = [];
	}
	
//------------------------------------------------------------------------------------------------------
	/**
	 * 处理TOR网元或Group元素的双击事件 
	 * @param element 双击的网元
	 * 
	 */	
	private function onElementDoubleClick(element:IElement):void
	{
		var cuDcName:String = SdncUtil.currentDcName;
		if(element.name.indexOf("compute") != -1 || element.name == "server19" || element.name == "server20")
		{
			vmMoveUtil.physicsBox = page.cuNetBox;
			vmMoveUtil.getVM(element,cuDcName);
		}
		else if(element is Group)
		{
		}
	}
	
	/**
	 * 处理dc切换
	 */
	private function onDcChange(evt:SdncEvt):void
	{
		page.overlieUtil.clearAllOverlie(page.cuNetBox);
		var dcName:String = evt.params;
		initArrary();
		if(SdncUtil.cordinateFlag)
			goToDcByXY(dcName);
		else
			goToDcCustom(dcName);
		page.isRoot = false;
		serializableCuBox(page,dcName);
		__netWork.centerByLogicalPoint(840,400);
		page.isOverlieCleared = true;
		page._app.main.optionPanel.dispatchEvent(new SdncEvt(SdncEvt.SET_SWITCHBTN_STATE));
		if(SdncUtil.cuProjectType == "commix") page.getHostInfo();
	}
	
	private function serializableCuBox(serializable:ISerializable,dcName:String):void
	{
		var xmlStr:String = __serializeDcDics[dcName];
		if(xmlStr == null || xmlStr == "")
		{
			__serializeDcDics[dcName] = SdncUtil.serializableDataBox(page);
		}
	}
	
	public function deserializeHandle():void
	{
		var dcName:String = SdncUtil.currentDcName;
		var xmlStr:String = __serializeDcDics[dcName];
		if(xmlStr && xmlStr != "")
			SdncUtil.deserializeDataBox(page,xmlStr);
	}
	
	/**
	 * 恢复初始视图
	 */
	private function getNormalDC(event:Event):void
	{
		if(page.springLaouter.running) page.springLaouter.stop();
		__netWork.elementBox = __eBox;
		page.cuNetBox = __eBox;
		page._app.main.optionPanel.dispatchEvent(new SdncEvt(SdncEvt.SET_SWITCHBTN_STATE));
		page.isRoot = true;
	}
	
	protected function onNodeStateError(event:SdncEvt):void
	{
		page.cuNetBox.forEachByBreadthFirst(function(item:IData):void
		{
			if(item is  StateNode)
			{
				var node:StateNode = item as StateNode;
				if(node.id == event.params)
				{
					node.alarmState.setNewAlarmCount(SdncUtil.CRITICALFAULT,1);
				}
			}
		});
	}
	
	/**
	 *每过一段时间刷新所有的节点信息 
	 *
	 */
	private function refreshTimerStart(event:SdncEvt):void
	{
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
	
	private function refreshAllNodes(e:Event = null):void
	{
		page.cuNetBox.forEachByBreadthFirst(function(item:IData):void{
			if(item is StateNode)
			{
				var node:StateNode = item as StateNode;
				var urlArr:Array = SdncUtil.dcUrlInfos[node._curDcName];
				node.updata(urlArr[7]);
			}
		});
	}
	
	protected function onNodeLabelTypeChange(event:SdncEvt):void
	{
		page.cuNetBox.forEachByBreadthFirst(function(item:IData):void{
			if(item is StateNode)
			{
				var node:StateNode = item as StateNode;
				node.changeLabel(event.params);
			}
		});
	}
	
	protected function onVmLabelTypeChange(event:SdncEvt):void
	{
		page.cuNetBox.forEachByBreadthFirst(function(item:IData):void{
			if(item is VmFollower)
			{
				var node:VmFollower = item as VmFollower;
				node.labelChange(event.params);
			}
		});
	}
	
}
}