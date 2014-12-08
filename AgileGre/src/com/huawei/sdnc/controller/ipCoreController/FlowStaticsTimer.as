package com.huawei.sdnc.controller.ipCoreController
{
	import com.huawei.sdnc.event.SdncEvt;
	import com.huawei.sdnc.model.Device;
	import com.huawei.sdnc.tools.SdncUtil;
	
	import flash.events.TimerEvent;
	import flash.utils.Timer;

	public class FlowStaticsTimer
	{
		private var timer:Timer;
		private var __app:sdncui2;
		private var _deviceList:Array=[];
		private var curIndex:int = 0;
		public var flowSwitch:Boolean=false;
		
		public function FlowStaticsTimer()
		{
			__app = SdncUtil.app;
			timer = new Timer(5000);
			__app.addEventListener(SdncEvt.SWITCH_FLOW,switchFlowStatic);
			timer.addEventListener(TimerEvent.TIMER,flowStaticsTimerHandle)
			timer.start();
		}
		
		private function flowStaticsTimerHandle(e:TimerEvent):void
		{
			if(flowSwitch)
			{
				if(curIndex<_deviceList.length){
					var device:Device=_deviceList[curIndex];
					device.newFlowStatices();
					curIndex++;
				}else if(curIndex >= _deviceList.length){
					curIndex =0;
				}
			}
		}
		
		public function set deviceList(value:Array):void
		{
			_deviceList = value;
			curIndex = 0;
		}
		private function switchFlowStatic(e:SdncEvt):void
		{
			var state:Boolean=e.params;
			flowSwitch=state;
			if(!flowSwitch){
				for each(var devi:Device in _deviceList){
					devi.clearFlowData();
					devi.undoBehavior();
				}
			}
		}
	}
}