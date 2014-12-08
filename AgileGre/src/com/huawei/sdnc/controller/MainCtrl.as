package com.huawei.sdnc.controller
{
import com.huawei.sdnc.event.SdncEvt;
import com.huawei.sdnc.service.SdnService;
import com.huawei.sdnc.tools.ConnUtil;
import com.huawei.sdnc.tools.SdncUtil;
import com.huawei.sdnc.view.Main;
import com.huawei.sdnc.view.common.MenuBtn;
import com.huawei.sdnc.vo.TopoXmlVo;

import flash.events.Event;
import flash.events.MouseEvent;
import flash.events.TimerEvent;
import flash.utils.Dictionary;
import flash.utils.Timer;

import mx.controls.Alert;
import mx.events.FlexEvent;
import mx.formatters.DateFormatter;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.ResultEvent;

import org.httpclient.events.HttpDataEvent;

import twaver.Element;
import twaver.ElementBox;
import twaver.Node;
import twaver.QuickFinder;
import twaver.SelectionModel;
import twaver.networkx.NetworkX;

public class MainCtrl
{
	[Bindable]
	public var page:Main;
	/**记录选项面板开关状态*/
	private var __isShowOptions:Boolean;
	public function MainCtrl()
	{
	}
	/**
	 * main初始化
	 */
	public function onInit(event:FlexEvent):void
	{
		var xml:XML = SdncUtil.cuProjectXML;
		ConnUtil.getInstence().init(xml);
		SdncUtil.refreshTime = xml.controllers.defaultpolltime;
		page.addEventListener(SdncEvt.VIRTUAL_VNM_CHANGE,onVnmChange);
		page.searchBar.addEventListener(SdncEvt.SEARCH_ELEMENT,onSearch);
		page.addEventListener(SdncEvt.SYSTEM_REFRESH,onSystemRefresh);
	}
	
	private function onSystemRefresh(evt:SdncEvt):void
	{
		SdncUtil.isRefreshPhysicTopo = true;
		SdncUtil.isRefreshCtrlTopo = true;
		page.menuHg.getElementAt(0).dispatchEvent(new MouseEvent(MouseEvent.CLICK));
	}
	
	protected function onSearch(event:SdncEvt):void
	{
		var box:ElementBox = new ElementBox();
		var network:NetworkX = new NetworkX();
		switch(page.currentState)
		{
			case "physics_view":
				box = page.physicsView.dataCenter.cuNetBox;
				network = page.physicsView.dataCenter.network;
				break;
			case "ctrl_view":
				box = page.ctrlView.curBox;
				network = page.ctrlView.network;
				break;
			case "virtual_view":
				box = page.virtualView.dataBox as ElementBox;
				network = page.virtualView.network;
				break;
		}
		var nameFinder:QuickFinder = new QuickFinder(box,"name");
		var selectM:SelectionModel = network.selectionModel;
		var result:Array = [];
		var regExp:RegExp = new RegExp(event.params,"i");
		result = nameFinder.find(regExp);
		if(result.length == 0)
		{
			box.forEachByBreadthFirst(function(item:Element):void{
				if(item.name)
				{
					if(item.name.search(regExp) != -1)
					{
						result.push(item);
					}
				}
			});
		}
		if(result.length == 0)
		{
			Alert.show("No results!","Search");
			return;
		}
		selectM.clearSelection();
		for each(var data:Object in result)
		{
			selectM.appendSelection(data);
		}
	}
	
	public function onCreate(event:FlexEvent):void
	{
		var viewTypesArr:Array = SdncUtil.viewTypes[SdncUtil.curEntry] as Array;
		var len:int = viewTypesArr.length;
		for(var i:int = 0;i < len;i++)
		{
			var menuBtn:MenuBtn = new MenuBtn();
			menuBtn.id = viewTypesArr[i].name;
			menuBtn.label = viewTypesArr[i].desc;
			menuBtn.width = 155;
			menuBtn.percentHeight = 100;
			menuBtn.addEventListener(MouseEvent.CLICK,page.menuClickHandler);
			page.menuHg.addElementAt(menuBtn,i);
		}
		page.menuHg.getElementAt(0).dispatchEvent(new MouseEvent(MouseEvent.CLICK));
	}
	/**
	 * 更新导航面板
	 * @param addVdcName:增加的vdc名字
	 * 
	 */
	public function onVnmChange(event:SdncEvt):void
	{
		var xml:XML = SdncUtil.cuProjectXML;
		var vdcNameArr:Array = [];
		for each(var dcName:String in SdncUtil.dcHasDataArr)
		{
			var topoXmlVo:TopoXmlVo = SdncUtil.dcTopoXmlDic[dcName];
			var vnmXml:XML = topoXmlVo._vnmXml;
			for each(var cuVdc:XML in vnmXml.network)
			{
				var networkName:String = String(cuVdc.networkName);
				if(!SdncUtil.isExistInArr(vdcNameArr,networkName))
				{
					vdcNameArr.push(networkName);
				}
			}
//			for each(var vnm:XML in vnmXml)
//			{
//				var flag:Boolean = false;
//				for each(var vdcName:String in SdncUtil.vdcNameArr)
//				{
//					if(vnm.networkName == vdcName){
//						flag = true;
//						break;
//					}
//				}
//				if(!flag) SdncUtil.vdcNameArr.push(vnm.networkName);
//			}
		}
		SdncUtil.vdcNameArr = vdcNameArr;
//		for each(var cuVdc:XML in xml.vdcs.vdc)
//		{
//			SdncUtil.vdcNameArr.push(String(cuVdc.vdcnetworkname));
//		}
	/*	page.navPanel.updataMatrixSelector();*/
	}
	/**
	 * 显示和关闭控制面板
	 */
	public function setupBtnClickhandler(event:Event):void
	{
		if(!__isShowOptions)
		{
			page.optionPanel.visible = true;
			page.optionPanel.includeInLayout = true;
			__isShowOptions = true;
		}else
		{
			page.optionPanel.visible = false;
			page.optionPanel.includeInLayout = false;
			__isShowOptions = false;
		}
	}
}
}