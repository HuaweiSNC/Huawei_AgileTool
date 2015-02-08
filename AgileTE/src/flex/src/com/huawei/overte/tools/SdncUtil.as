package com.huawei.overte.tools
{

import com.huawei.overte.event.SdncEvt;
import com.huawei.overte.handle.DataHandleTool;

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
import twaver.network.Overview;
import twaver.network.layout.AutoLayouter;
import twaver.networkx.NetworkX;
import twaver.networkx.OverviewX;

/**常用工具类*/
public class SdncUtil
{
	
	
	/** 配置定时刷新时间，30分钟刷无条件新拓扑**/
	public static var refreshlong:String;
	/**定时刷新的监测时间，15秒内有告警刷新拓扑图**/
	public static var refreshshort:String;
	
	/**工程主应用程序**/
	public static var app:overTegui2;
	/**当前项目web服务项目名称**/
	public static var projectname:String;
	/**当前项目web服务地址**/
	public static var opsIp:String;
	
	/**当前项目Socket服务地址**/
	public static var socketIp:String;
	/**当前项目告警服务连接地址**/
	public static var alermconnectip:String;
	/**当前项目告警服务开启地址**/
	public static var alermstartip:String;
	/**当前工程,分别为sdn_office_fordc_test和sdn_office_fordc_interop*/
	public static var cuProjectXML:XML;
	/**当前工程名*/
	public static var cuProjectName:String;
	/**当前工程类型（test和normal）*/
	public static var cuProjectType:String;
	/**每个场景所对应的视图集合*/
	public static var viewTypes:Object = new Object();
	/**每个管理域所对应的视图集合*/
	public static var AreaviewTypes:Object = new Object();
	
	/**当前系统登陆管理域名称**/
	public static var cuArea:Object=new Object;
	
	/**当前系统所有Tunnel**/
	public static var AllTunnel:ArrayCollection = new ArrayCollection();
	
	/**当前Tnetwork**/
	public static var network:Network;
	
	/**当前Topo图上显示设置 true则显示  false则不显示**/
	public static var showInterface:Boolean=false;
	
	/**当前Topo图上显示设置 true则显示  false则不显示**/
	public static var showIp:Boolean=false;
	
	/**当前Topo图上显示设置 true则显示  false则不显示**/
	public static var showdeviceName:Boolean=true;
	
	/**当前Topo图上显示设置 true则显示  false则不显示**/
	public static var showdeviceIp:Boolean=false;
	
	/**当前选择管理域**/
	public static var CurAreaId:String="";
	/**当前告警服务是否开启**/
	public static var openalermflag:Boolean = true;
	/**当前Alerm的websocket是否开启**/
	public static var openalermwsflag:Boolean = true;
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
	/**
	 * 移除设备数组中当前设备
	 */
	public static function removeCurDevice(curdevice:String,devices:Array):Array{
		var desArray:Array = new Array();
		for(var i:int=0;i<devices.length;i++){
			if(curdevice!=devices[i].deviceName){
				desArray.push(devices[i])
			}
		}
		return desArray;				
	}
	/**
	 * 添加鸟瞰图
	 * @param network:要添加鸟瞰图的network实例
	 */
	public static function addOverview(network:Network):void{
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
		
		var overview:Overview = new Overview();
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
	 * 根据设备名称查找该设备信息
	 */
	public static function getDeviceByName(deviceName:String):Object{
		var devices:Array = DataHandleTool.devices;
		for(var i:int=0;i<devices.length;i++){
			if(deviceName==devices[i].devicename){
				return devices[i];
			}
		}
		return null;
	}
	}
}