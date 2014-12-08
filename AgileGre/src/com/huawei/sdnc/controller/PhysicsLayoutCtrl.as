package com.huawei.sdnc.controller
{
import com.huawei.sdnc.event.SdncEvt;
import com.huawei.sdnc.tools.DCDataDealUtil;
import com.huawei.sdnc.tools.PopupManagerUtil;
import com.huawei.sdnc.tools.SdncUtil;
import com.huawei.sdnc.tools.TopoUtil;
import com.huawei.sdnc.tools.VMMoveUtil;
import com.huawei.sdnc.view.ISerializable;
import com.huawei.sdnc.view.common.node.StateNode;
import com.huawei.sdnc.view.common.node.VmFollower;
import com.huawei.sdnc.view.dataCenter.physics.PhysicsLayout;

import flash.events.MouseEvent;
import flash.events.TimerEvent;
import flash.geom.Point;
import flash.utils.Dictionary;
import flash.utils.Timer;

import mx.events.FlexEvent;
import mx.events.ResizeEvent;

import twaver.ElementBox;
import twaver.Follower;
import twaver.Grid;
import twaver.Group;
import twaver.IData;
import twaver.IElement;
import twaver.Link;
import twaver.Node;
import twaver.network.layout.AutoLayouter;
import twaver.network.layout.SpringLayouter;
import twaver.networkx.NetworkX;
import twaver.networkx.interaction.InteractionEvent;

public class PhysicsLayoutCtrl
{
	[Bindable]
	public var page:PhysicsLayout;
	/**Twaver拓扑组件*/
	private var __netWork:NetworkX;
	/**上一次操作的网元*/
	private var __lastElement:IElement = null;
	
	/**定时器，用于定时获取设备CPU和RAM状态*/
	private var __timer:Timer;
	private var __serializeDcDics:Dictionary = new Dictionary();
	private var __serviceArr:Array = [];
	private var __count:int = 0;
	private var __sourceGrid:Grid;
	private var __sourceCellObj:Object;
	[Bindable]
	private var vmMoveUtil:VMMoveUtil = VMMoveUtil.getInstence();
	[Bindable]
	private var dcDealUtil:DCDataDealUtil = DCDataDealUtil.getInstence();
	
	/** 物理视图初始化组件和box */
	public function onInit(event:FlexEvent):void
	{
		if(SdncUtil.cuProjectType != "normal")
		{
			page.overlieUtil.changedVlantopo19 = page.changedVlantopo19;
			page.overlieUtil.changedVlantopo20 = page.changedVlantopo20;
		}
		page._app = SdncUtil.app;
		__netWork = page.network;
		__netWork.innerColorFunction = null;
		SdncUtil.addOverview(__netWork);
		page.autoLayouter = new AutoLayouter(__netWork);
		page.springLaouter = new SpringLayouter(__netWork);
		page._app.addEventListener(SdncEvt.PHYSICSVIEW_DC_CHANGE,onDcChange);
		page._app.addEventListener(SdncEvt.PHYSICSVIEW_BACK_TO_ROOT,getNormalDC);
		page._app.addEventListener(SdncEvt.REFRESH_NODES_STATE,refreshTimerStart);
		page._app.addEventListener(SdncEvt.CHANGE_NODE_LABEL_TYPE,onNodeLabelTypeChange);
		page._app.addEventListener(SdncEvt.CHANGE_VM_LABEL_TYPE,onVmLabelTypeChange);
		page._app.addEventListener(SdncEvt.VM_MOVE,vmMoveUtil.onVmMove);
		page._app.addEventListener(SdncEvt.PHYSICSVIEW_PING_START,page.doPingTestHandler);
		page._app.addEventListener(SdncEvt.OVERLIE_VDC,page.overlieVdc);
		page._app.addEventListener(SdncEvt.CLEAROVERLIE_VDC,page.overlieVdc);
		page._app.main.addEventListener(SdncEvt.MODULE_CHANGE,refreshTopoData);
		__netWork.elementBox.addDataPropertyChangeListener(TopoUtil.handleExpand);
		__netWork.addEventListener(MouseEvent.MOUSE_MOVE, function(evt:MouseEvent):void{
			__lastElement = SdncUtil.handleMouseMove(evt,__netWork,__lastElement);
		});
		page.addEventListener(ResizeEvent.RESIZE,function(e:ResizeEvent):void{
			page.network.height = page.height;
			page.network.width = page.width;
		});
		__netWork.addInteractionListener(onInteractionEvent);
		__timer = new Timer(SdncUtil.refreshTime * 1000);
		__timer.addEventListener(TimerEvent.TIMER,refreshAllNodes);
		initPageDate();
		page.cuNetBox = dcDealUtil.__eBox;
		__netWork.elementBox = dcDealUtil.__eBox;
	}
	
	private function initPageDate():void
	{
		dcDealUtil.physicsView = page;
		dcDealUtil.__eBox = new ElementBox();
		dcDealUtil.__dcGArr = [];
		dcDealUtil.initBox();
		//弹出加载框
		PopupManagerUtil.getInstence().popupLoading(SdncUtil.app);
	}
	
	public function refreshTopoData(evt:SdncEvt):void
	{
		if(SdncUtil.isRefreshPhysicTopo)
		{
			SdncUtil.isRefreshPhysicTopo = false;
			initPageDate();
			SdncUtil.dataBoxsDic = new Dictionary;
			page._app.dispatchEvent(new SdncEvt(SdncEvt.PHYSICSVIEW_BACK_TO_ROOT,null));
		}
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
		dcDealUtil.initArrary();
		if(SdncUtil.cordinateFlag)
			dcDealUtil.goToDcByXY(dcName);
		else
			dcDealUtil.goToDcCustom(dcName);
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
	private function getNormalDC(event:SdncEvt):void
	{
		if(page.springLaouter.running) page.springLaouter.stop();
		__netWork.elementBox = dcDealUtil.__eBox;
		page.cuNetBox = dcDealUtil.__eBox;
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
	
	private function refreshAllNodes(e:TimerEvent = null):void
	{
		page.cuNetBox.forEachByBreadthFirst(function(item:IData):void{
			if(item is StateNode)
			{
				var node:StateNode = item as StateNode;
//				var urlArr:Array = SdncUtil.dcUrlInfos[node._curDcName];
//				node.updata(urlArr[7]);
				var url:String = "http://10.135.22.53:8080/devices/9/devm";//目前添加了ID=9的FP设备
				node.updata(url);
			}
		});
	}
	/**改变Node的显示的名字*/
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
	/**改变VM的显示的名字*/
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
	
	public function PhysicsLayoutCtrl(){ }
}
}