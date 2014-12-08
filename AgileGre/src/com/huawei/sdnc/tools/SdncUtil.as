package com.huawei.sdnc.tools
{
import com.huawei.sdnc.controller.ipCoreController.FlowStaticsTimer;
import com.huawei.sdnc.controller.ipCoreController.NqaTimer;
import com.huawei.sdnc.event.SdncEvt;
import com.huawei.sdnc.service.SdnService;
import com.huawei.sdnc.view.ISerializable;
import com.huawei.sdnc.view.common.node.ServerNode;
import com.huawei.sdnc.view.common.node.StateNode;
import com.huawei.sdnc.view.common.node.VmFollower;
import com.huawei.sdnc.view.dataCenter.physics.PhysicsLayout;
import com.huawei.sdnc.view.dataCenter.virtural.VirtualView;
import com.huawei.sdnc.vo.TopoXmlVo;

import flash.display.StageDisplayState;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.geom.Point;
import flash.net.URLLoader;
import flash.net.URLRequest;
import flash.utils.ByteArray;
import flash.utils.Dictionary;

import mx.collections.ArrayCollection;
import mx.containers.Box;
import mx.controls.Alert;
import mx.controls.Button;
import mx.effects.AnimateProperty;
import mx.effects.CompositeEffect;
import mx.effects.Parallel;
import mx.events.EffectEvent;
import mx.formatters.DateFormatter;
import mx.managers.SystemManager;

import spark.components.Image;
import spark.components.Label;

import twaver.Alarm;
import twaver.AlarmSeverity;
import twaver.AlarmState;
import twaver.Consts;
import twaver.ElementBox;
import twaver.Follower;
import twaver.Grid;
import twaver.Group;
import twaver.IData;
import twaver.IElement;
import twaver.Link;
import twaver.Node;
import twaver.ShapeNode;
import twaver.Styles;
import twaver.XMLSerializer;
import twaver.network.Network;
import twaver.network.layout.AutoLayouter;
import twaver.networkx.NetworkX;
import twaver.networkx.OverviewX;

/**常用工具类*/
public class SdncUtil
{
	public static var app:sdncui2;
	
	public static var opsIp:String;
	/**当前工程,分别为sdn_office_fordc_test和sdn_office_fordc_interop*/
	public static var cuProjectXML:XML;
	/**当前工程名*/
	public static var cuProjectName:String;
	/**当前工程类型（test和normal）*/
	public static var cuProjectType:String;
	/**每个场景所对应的视图集合*/
	public static var viewTypes:Object = new Object();
	/**当前选择的场景名称*/
	public static var curEntry:String;
	/**存放所有dc*/
	public static var dcMap:Object = new Object();
	/**存放所有dc的名称*/
	public static var dcNameArr:Array = new Array();
	/**存放所有vdc的名称*/
	public static var vdcNameArr:Array = [];
	/**存放vnmXml*/
	public static var vnmXMl:XML;
	/**有数据的DC名字 */	
	public static var dcHasDataArr:Array = new Array();
//	/**url作为键,通过读url返回的结果作为值*/
//	public static var dcUrlDics:Dictionary = new Dictionary();
	/**每个DC对应的所有URL数组
	 * 数组中第0位为l2topo(fabric内部TOPO)的XML地址,
	 * 第1位为lldp(外部TOPO)的XML地址,
	 * 第2位为vcluster(转发节点的具体信息)的XML地址,
	 * 第3位为vlantopo(物理视图叠加虚拟视图信息)的XML地址,
	 * 第4位为vnm(虚拟视图网络信息)的XML地址,
	 * 第5位为instances(服务器信息--虚拟机)的XML地址（真实工程只能从OpenStack上获取）,
	 * 第6位为ifm(接口信息流量统计)的XML地址,
	 * 第7位为devm(设备管理CPU、内存信息统计)的XML地址*/
	public static var dcUrlInfos:Object = new Object();
	/**vlantopo中的每个vlanXML*/
	public static var vlantopoInfos:Object = new Object();
	/**当前DC和vlantopo匹配上了的vnm对应的vlantopoXML*/
	public static var vlansInCurDc:Object = new Object();
	/**存放图片请求的键值*/
	public static var imagesObjects:Object = {CORE:"CORENode",TOR:"TORNode",AGG:"AGGNode",Computer:"ComputerNode",
		Cloud:"CloudGroup",AREA:"AREANode"};
	/**存放所有controller*/
	public static var ctrlMap:Dictionary = new Dictionary();
	/**存放所有节点对应的详细信息*/
//	public static var vcFpInfoMap:Dictionary = new Dictionary();
	/**存放当前project 的所有Dc信息*/
//	public static var dcInfoObj:Dictionary = new Dictionary();
	/**当前DC视图名称（单个DC视图时有效）如果值为dc,则代表整个数据中心*/
	public static var currentDcName:String = "dc";
	
	/**存入物理视图所有DC的BOX*/
	public static var dataBoxsDic:Dictionary = new Dictionary();
	/**存入虚拟视图所有DC的BOX*/
	public static var dataBoxsDicCtrl:Dictionary = new Dictionary();
	
	/**网元是否有坐标信息*/
	public static var cordinateFlag:Boolean = false;
	
	/**状态的刷新时间*/
	public static var refreshTime:int;
	
	/**存放各个Dc所需的xml数据*/
	public static var dcTopoXmlDic:Dictionary = new Dictionary();
	
	/**自定义三个警告等级*/
	public static const CRITICALFAULT:AlarmSeverity = new AlarmSeverity(1,"criticalfault","CRITICAL",0xfa6465,"CRITICAL");
	public static const MAJORFAULT:AlarmSeverity = new AlarmSeverity(1,"majorfault","MAJOR",0xeb9d60,"MAJOR");
	public static const WARNING:AlarmSeverity = new AlarmSeverity(1,"warning","WARNING",0xe2c333,"WARNING");
	
	/**自定义气泡*/
	public static const FLOW_BEGIN:AlarmSeverity = new AlarmSeverity(1,"flow_begin","Begin",0x00FF00,"begin");
	public static const FLOW_END:AlarmSeverity = new AlarmSeverity(1,"flow_end","End",0x00FF00,"end");
	
	/**cordinateFlag = false时,对所点击的Group内的网元进行自动布局*/
	public static var linkColor:Number = 0x66CCFF;
	/**当前拖动的VM */	
	public static var dragedVM:VmFollower;
	/**虚拟机当前连接的serverMac地址*/
	public static var hasVmServerMac:String;
	/**是否重新加载物理视图数据*/
	public static var isRefreshPhysicTopo:Boolean = false;
	/**是否重新加载控制视图数据*/
	public static var isRefreshCtrlTopo:Boolean = false;
	/**最小link间隙*/
	public static var linkGapMin:int = 19;
	/**最大link间隙*/
	public static var linkGapMax:int = 25;
	/**流量统计定时器*/
	public static var flowStaticsTimer:FlowStaticsTimer;
	/**时延统计*/
	public static var nqaTimer:NqaTimer;
	
	public static function isExistInArr(arr:Array,str:String):Boolean
	{
		var bool:Boolean = false;
		for each(var cuStr:String in arr)
		{
			if(cuStr == str)
			{
				bool = true;	break;
			}
		}
		return bool;
	}
	
	/**
	 * 序列化DataBox
	 * @param serializable 可序列化接口
	 * @return 序列化后的字符串
	 */
	public static function serializableDataBox(serializable:ISerializable):String
	{
		var serializer:XMLSerializer = new XMLSerializer(serializable.dataBox);
		return serializer.serialize();
	}
	
	/**
	 * 反序列化
	 * @param serializable 可序列化接口
	 * @param xmlStr 反序列化的字符串
	 * 
	 */
	public static function deserializeDataBox(serializable:ISerializable,xmlStr:String):void
	{
		var serializer:XMLSerializer = new XMLSerializer(serializable.dataBox);
		serializer.deserialize(xmlStr);
	}
	
	/**
	 * 当没有坐标信息时对组内进行布局
	 * @param network:当前操作的network实例
	 * @param g:操作的组
	 * @param autoLayouter:使用的自动布局器
	 * @param layoutStyle:布局名称（twaver中Consts.LAYOUT_）
	 */
	public static function doLayoutForGroup(network:NetworkX,g:Group,autoLayouter:AutoLayouter,layoutStyle:String = Consts.LAYOUT_HIERARCHIC):void {
		var g:Group = Group(network.selectionModel.lastData);
		if(!g){
			return;
		}
		var oldCenterLocation:Point = g.centerLocation;
		network.movableFunction = function(e:IElement):Boolean {
			return e == g || e.parent == g;
		};
		autoLayouter.animate = false;
		autoLayouter.repulsion = 1;
		autoLayouter.doLayout(layoutStyle, function():void {
			g.centerLocation = oldCenterLocation;
			var coreArr:Array = new Array();
			var aggArr:Array = new Array();
			g.children.forEach(function(data:Node):void{
				if(data.image == "CORENode")
				{
					coreArr.push(data);
				}
				else if(data.image == "AGGNode")
				{
					aggArr.push(data);
				}
			});
			resetArrPosition(coreArr);
			resetArrPosition(aggArr);
			network.movableFunction = null;
			autoLayouter.animate = true;
		});
	}
	
	/**
	 * 重设数组内元素的坐标 
	 * @param arr 数组
	 * @param gapValue　相邻元素的间隔值
	 * 
	 */
	public static function resetArrPosition(arr:Array,gapValue:int = 60):void
	{
		var length:int = arr.length;
		if(length > 0)
		{
			var centerY:Number = 0;
			var yMin:Number = 10000;
			var yMax:Number = 0;
			var i:int = 0;
			for(i = 0; i < length; i++)
			{
				var nodey:Number = arr[i].y;
				if(nodey > yMax) yMax = nodey;
				if(nodey < yMin) yMin = nodey;
			}
			centerY = (yMax - yMin) / 2 + yMin;
			for(i = 0; i < length; i++)
			{
				var cuNode:Node = arr[i];
				var gap:Number = gapValue;
				if(i % 2 == 0)
				{
					gap = -gapValue;
				}
				cuNode.setLocation(cuNode.x + gap,centerY);
			}
		}
	}
	
	/**
	 * 添加鸟瞰图
	 * @param network:要添加鸟瞰图的network实例
	 */
	public static function addOverview(network:NetworkX):void{
		var show:Parallel = new Parallel();
		show.duration = 250;
		addAnimateProperty(show, "alpha", 1, false);
		addAnimateProperty(show, "width", 100, false);
		addAnimateProperty(show, "height", 100, false);
		
		var hide:Parallel = new Parallel();
		hide.duration = 250;
		addAnimateProperty(hide, "alpha", 0, false);	
		addAnimateProperty(hide, "width", 0, false);
		addAnimateProperty(hide, "height", 0, false);
		
		var overview:OverviewX = new OverviewX();
		overview.network = network;
		overview.visible = true;
		overview.width = 100;
		overview.height = 100;
		overview.right = 17;
		overview.bottom = 17;
		overview.backgroundColor = 0xffffff;
		overview.backgroundAlpha = .2;
		overview.setStyle("showEffect", show);
		overview.setStyle("hideEffect", hide);
		
		var toggler:Image = new Image();
		toggler.source = Images.hide;
		toggler.width = 17;
		toggler.height = 17;
		toggler.right = 0;
		toggler.bottom = 0;
		toggler.addEventListener(MouseEvent.CLICK, function(e:*):void{
			if(toggler.source == Images.show){
				toggler.source = Images.hide;
				overview.network = network;
				overview.visible = true;
			}else{
				toggler.source = Images.show;
				overview.visible = false;
			}					
		});	
		hide.addEventListener(EffectEvent.EFFECT_END, function(e:*):void{
			overview.network = null;
		});			
		network.parentDocument.addElement(overview);
		network.parentDocument.addElement(toggler);
	}
	
	private static function addAnimateProperty(effect:CompositeEffect, property:String, toValue:Number, isStyle:Boolean = true):AnimateProperty{
		var animateProperty:AnimateProperty = new AnimateProperty();
		animateProperty.isStyle = isStyle;
		animateProperty.property = property;
		animateProperty.toValue = toValue; 
		effect.addChild(animateProperty);
		return animateProperty;
	}
	
	/**
	 * 将日期转换为指定格式的字符串
	 * @param format:转换成的格式
	 * @param dt:要转换的源数据
	 */
	public static function getFormatStr(format:String,dt:Date):String
	{
		var df:DateFormatter = new DateFormatter();
		df.formatString = format;
		return df.format(dt);
	}
	
	/**加载XML
	 * @param url:加载路径
	 * @param loadCompliteFun:回调
	 */
	public static function xmlLoader(url:String,loadCompliteFunc:Function):void
	{
		var urlLoader:URLLoader = new URLLoader();
		urlLoader.addEventListener(Event.COMPLETE,loadCompliteFunc);
		urlLoader.load(new URLRequest(url));
	}
	
	/**
	 * 处理Follower的鼠标事件
	 * @param page:操作的页面
	 * @param netWork:操作的network实例
	 * @param follower:
	 * @param mouseEvent:
	 */
	public static function processHost(page:Object,netWork:NetworkX,follower:Follower,sourceGrid:Grid,sourceCellObj:Object, mouseEvent:MouseEvent,isCleared:Boolean):void{
//		trace(page,netWork);
//		trace(follower,sourceGrid,sourceCellObj.name);
		if(follower == null){
			return;
		}
		follower.host = null;
		follower.parent = netWork.currentSubNetwork;
		var point:Point = netWork.getLogicalPoint(mouseEvent);	
		page.cuNetBox.forEachByLayerReverse(function(element:IElement):Boolean{
			trace("element:"+element);
			
			if(follower == element || !netWork.isVisible(element)){
				trace("1");
				resumeVmLoction(follower,sourceGrid,sourceCellObj);
				return true;
			}
			/*if(element is Node && !Node(element).rect.containsPoint(point)){
				resumeVmLoction(follower,sourceGrid,sourceCellObj);
				return true;
			}*/
			if(element is VmFollower && !VmFollower(element).rect.containsPoint(point)){
				trace("2");
				resumeVmLoction(follower,sourceGrid,sourceCellObj);
				return true;
			}
			if(!(element is Grid))
			{
				trace("3");
//				trace("is not Grid!");
				resumeVmLoction(follower,sourceGrid,sourceCellObj);
				return true;
			}
//			if(element as Grid == sourceGrid)
//			{
//				resumeVmLoction(follower,sourceGrid,sourceCellObj);
//				return true;
//			}
			var grid:Grid = element as Grid;
			if(grid != null && grid.host != follower){
				trace("4");
				//grid的每一个child做VmFollower(child).rect.containsPoint(point)测试
//				grid.children
				var cellObject:Object = grid.getCellObject(point);
				if(cellObject != null){
					follower.host = grid;
					follower.parent = grid;
					follower.setStyle(Styles.FOLLOWER_ROW_INDEX, cellObject.rowIndex);
					follower.setStyle(Styles.FOLLOWER_COLUMN_INDEX, cellObject.columnIndex);
//					trace("follower.name:"+follower.name+isCleared);
					if(SdncUtil.cuProjectType == "test" && follower.name == "19_vm4")SdncUtil.app.dispatchEvent(new SdncEvt(SdncEvt.VM_MOVE,grid));
//					else if(SdncUtil.cuProjectType == "commix" && follower.name == "vm3")SdncUtil.app.dispatchEvent(new SdncEvt(SdncEvt.VM_MOVE,grid));//与VNMXML中的VENT1的attatch名字相对应
					else SdncUtil.app.dispatchEvent(new SdncEvt(SdncEvt.VM_MOVE,grid));//与VNMXML中的VENT1的attatch名字相对应
					return false;
				} else
				{
					resumeVmLoction(follower,sourceGrid,sourceCellObj);
					return true;
				}
			}
			if((element is VmFollower) && VmFollower(element).host != follower){
				trace("5");
				//				follower.host = VmFollower(element);
				//				follower.parent = VmFollower(element);
				resumeVmLoction(follower,sourceGrid,sourceCellObj);
				return false;
			}
			return true;
		});
	}
	
	/**
	 *恢复VM节点的拖动初始位置 
	 * @param follower 拖动的VM
	 * @param sourceGrid V	M拖动前所在的GRID
	 * @param sourceCellObj V	M拖动前所在的GRID的cellObject
	 * 
	 */
	private static function resumeVmLoction(follower:Follower,sourceGrid:Grid,sourceCellObj:Object):void
	{
		if(sourceGrid && sourceCellObj)
		{
			follower.host = sourceGrid;
			follower.parent = sourceGrid;
			follower.setStyle(Styles.FOLLOWER_ROW_INDEX, sourceCellObj.rowIndex);
			follower.setStyle(Styles.FOLLOWER_COLUMN_INDEX, sourceCellObj.columnIndex);
		}
	}
	
	/**
	 * Group选中样式
	 * @param element:要操作的网元
	 */
	public static function groupSelectStyle(element:IElement):void
	{
		if(element is Group)
		{
			var g:Group = element as Group;
			var shape:String = g.getStyle(Styles.GROUP_SHAPE);
			if(g.expanded == false) shape = Consts.SHAPE_ROUNDRECT;
			g.setStyle(Styles.SELECT_SHAPE,shape);
		}
	}
	
	/**
	 * 处理鼠标MOVE
	 * @param e:鼠标事件
	 * @param network:network实例
	 * @param lastElement:上一个操作的网元
	 */
	public static function handleMouseMove(e:MouseEvent,network:NetworkX,lastElement:IElement):IElement{
		var nodeColor:Number = 0x5d6134;
		var nodeOverColor:Number = 0xfbfffe;
		var linkOverColor:Number = 0xFF00FF;
		var selectColor:Number = 0x00ff64;
		var element:IElement = network.getElementByMouseEvent(e);
		var p:Point = new Point(e.localX,e.localY);
		var tip:Label;
		if(element != lastElement && !network.isMovingElement && !network.isSelectingElement){
			if(lastElement is Node){
				lastElement.setStyle(Styles.VECTOR_FILL_COLOR, nodeColor);
				lastElement.setStyle(Styles.LABEL_BOLD, false);
				lastElement.setStyle(Styles.LABEL_FILL, false); 
				if(app.main.virtualView)tip = app.main.virtualView.tip;
				if(tip)
				{
					tip.visible = false;
				}
				/*if((lastElement as Node).image == "CloudGroup")
				{
					
				}*/
			}
			else if(lastElement is Link){
				lastElement.setStyle(Styles.LINK_COLOR, linkColor);
			}
			
			if(element is Node){
				if((element as Node).image == "CloudGroup") return element;
				element.setStyle(Styles.VECTOR_FILL_COLOR, nodeOverColor);
				element.setStyle(Styles.LABEL_BOLD, true);
				element.setStyle(Styles.LABEL_FILL, true); 
//				element.setStyle(Styles.SELECT_STYLE,Consts.SELECT_STYLE_SHADOW);
//				element.setStyle(Styles.SELECT_BLURX,8);
//				element.setStyle(Styles.SELECT_BLURY,8);
//				element.setStyle(Styles.SELECT_DISTANCE,64);
				element.setStyle(Styles.SELECT_COLOR,"0xffffff");
			}
			else if(element is Link){
				element.setStyle(Styles.SELECT_COLOR, selectColor);
				element.setStyle(Styles.SELECT_WIDTH,1);
				linkColor = element.getStyle(Styles.LINK_COLOR);
				element.setStyle(Styles.LINK_COLOR, linkOverColor);
			}					
			lastElement = element;
		}
		if(element is Node)
		{
			if((element as Node).image == "CloudGroup")
			{
				if(app.main.virtualView) tip = app.main.virtualView.tip;
				if(tip)
				{
					if(tip.visible)
					{
						tip.x = p.x + 12;
						tip.y = p.y + 12;
					}else
					{
						tip.visible = true;
						tip.x = p.x +12;
						tip.y = p.y + 12;
					}
				}
			}
			lastElement = element;
		}
		if(lastElement is Node)
		{
			if((lastElement as Node).image == "CloudGroup")
			{
				
			}
		}
		return lastElement;
	}
	
	public static function convertByteArrayToString(bytes:ByteArray):String
	{
		var str:String;
		if(bytes)
		{
			bytes.position = 0;
			str = bytes.readUTFBytes(bytes.length);
		}
		return str;
	}
	
	/**
	 * 全屏
	 */
	public static function fullScreen():void{
		try{
			if(app.stage.displayState == StageDisplayState.FULL_SCREEN){
				app.stage.displayState = StageDisplayState.NORMAL;
			}else{
				app.stage.displayState = StageDisplayState.FULL_SCREEN;
				app.dispatchEvent(new SdncEvt(SdncEvt.FULL_SCREEN_EVENT));
			}					
		}catch(e:*){
			Alert.show(e, "An error occurred fullscreen");
		}				
	}
	
	public function SdncUtil()
	{
	}
}
}