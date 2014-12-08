package com.huawei.sdnc.tools
{
import com.huawei.sdnc.event.SdncEvt;
import com.huawei.sdnc.view.common.node.ServerNode;
import com.huawei.sdnc.view.common.node.StateNode;
import com.huawei.sdnc.view.common.node.VmFollower;

import flash.events.Event;
import flash.events.TimerEvent;
import flash.utils.Timer;

import mx.controls.Alert;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.ResultEvent;

import twaver.ElementBox;
import twaver.IData;
import twaver.Link;
import twaver.Styles;
import twaver.core.util.h._ED;

public final class PingTestUtil
{
	public static var instance:PingTestUtil;
	
	public var isReturn:Boolean = false;
	
	/**ping通路径数据数组*/
	public var __traceArr:Array = [];
	/**ping通测试中经过的连线数组*/
	public var __changedLines:Array = [];
	/**起点host名字*/
	public var __startHost:String;
	/**终点host名字*/
	public var __endHost:String;
	/**终点host名字*/
	public var _app:sdncui2;
	/**终点host名字*/
	public var cuNetBox:ElementBox;
	
	public static function getInstence():PingTestUtil
	{
		if(instance==null)
		{
			instance=new PingTestUtil();
		}
		return instance;
	}
	
	public function onPing():void
	{
		_app = SdncUtil.app;
		if(cuNetBox.selectionModel.count != 2 || (!(cuNetBox.selectionModel.firstData is VmFollower) || !(cuNetBox.selectionModel.lastData is VmFollower)))
		{
			Alert.show("Please select two VM!","Warning",4,null,function():void{
				_app.dispatchEvent(new SdncEvt(SdncEvt.PHYSICSVIEW_PING_END));
			});
			return;
		}
		__startHost = (cuNetBox.selectionModel.firstData as VmFollower).hostName;
		__endHost = (cuNetBox.selectionModel.lastData as VmFollower).hostName;
		if(!isReturn) ConnUtil.getInstence().query("assets/xml/dc1/dc1_icmptraces1.xml",onTraceResult,onFault);
		else 
		{
			var temp:String;
			temp = __startHost;
			__startHost = __endHost;
			__endHost = temp;
			ConnUtil.getInstence().query("assets/xml/dc1/dc1_icmptraces2.xml",onTraceResult,onFault);
		}
	}
	public function onTraceResult(event:ResultEvent):void
	{
		var resultXml:XML = event.result as XML;
		__traceArr.length = 0;
		for each(var traceXml:XML in resultXml.IcmpTraces.IcmpTrace.TestResults.TestResult)
		{
			__traceArr.push(traceXml.address);
		}
		var timer:Timer = new Timer(700,__traceArr.length+2);
		timer.addEventListener(TimerEvent.TIMER,onTimer);
		timer.addEventListener(TimerEvent.TIMER_COMPLETE,onTimerComplete);
		timer.start();
	}
	
	public function onFault(event:FaultEvent):void
	{
		trace("httpserver error")
	}
	
	public function onTimer(event:TimerEvent):void
	{
		var linkColor:uint = 0x00ff00;
		var i:int = (event.target as Timer).currentCount - 1;
		cuNetBox.forEachByBreadthFirst(function(element:IData):void{
			if(element is Link)
			{
				var link:Link = element as Link;
				var fromN:StateNode = link.fromNode as StateNode;
				var fromNFpIp:String = String(fromN.nodeInfo.fpIp);
				var curAddress:String = String(__traceArr[i]);
				var preAddress:String = String(__traceArr[i-1]);
				
				if(i < __traceArr.length)
				{
					if(link.fromNode is StateNode && link.toNode is StateNode)
					{
						var toN:StateNode = link.toNode as StateNode;
						var toNFpIp:String = String(toN.nodeInfo.fpIp);
						if((fromNFpIp == preAddress && toNFpIp == curAddress) || (fromNFpIp == curAddress && toNFpIp == preAddress) )
						{
							link.setStyle(Styles.LINK_COLOR,linkColor);
							__changedLines.push(link);
						}
					}
				}else if(i == __traceArr.length)
				{
					if(link.toNode is ServerNode)
					{
						
						if(fromNFpIp == preAddress && (link.toNode as ServerNode).name == __endHost)
						{
							link.setStyle(Styles.LINK_COLOR,linkColor);
							__changedLines.push(link);
						}
					}
				}
			}
		});
	}
	
	public function onTimerComplete(event:TimerEvent):void
	{
		for each(var link:Link in __changedLines)
		{
			link.setStyle(Styles.LINK_COLOR,0x66CCFF);
		}
		__changedLines = [];
		var timer:Timer = (event.target as Timer);
		timer.reset();
		timer.removeEventListener(TimerEvent.TIMER,onTimer);
		timer.removeEventListener(TimerEvent.TIMER_COMPLETE,onTimerComplete);
		if(!isReturn) 
		{
			isReturn = true;
			onPing();
		}else
		{
			isReturn = false;
			_app.dispatchEvent(new SdncEvt(SdncEvt.PHYSICSVIEW_PING_END));
		}
	}
	
	public function PingTestUtil()
	{
	}
}
}