package com.huawei.sdnc.controller.ipCoreController
{
	import com.huawei.sdnc.event.SdncEvt;
	import com.huawei.sdnc.tools.SdncUtil;
	
	import flash.events.TimerEvent;
	import flash.utils.Timer;

	public class NqaTimer
	{
		private var _nqaDataList:Array=[];
		private var timer:Timer;
		private var __app:sdncui2;
		private var curIndex:int = 0;
		public var nqaSwitch:Boolean=false;
		public function NqaTimer()
		{
			__app = SdncUtil.app;
			timer = new Timer(10000);
			__app.addEventListener(SdncEvt.SWITCH_NQA,switchNqa);
			timer.addEventListener(TimerEvent.TIMER,nqaTimerHandle)
			timer.start();
		}
		
		private function nqaTimerHandle(e:TimerEvent):void
		{
			if(nqaSwitch)
			{
				if(curIndex<_nqaDataList.length){
					var getNqaData:GetNqaData=_nqaDataList[curIndex];
					getNqaData.startRequestNqa();
					curIndex++;
				}else if(curIndex >= _nqaDataList.length){
					curIndex =0;
				}
			}
		}
		private function switchNqa(e:SdncEvt):void
		{
			var state:Boolean=e.params;
			nqaSwitch=state;
			if(!nqaSwitch){
				for each(var getNqaData:GetNqaData in _nqaDataList){
					if(getNqaData != null)
						getNqaData.clearData();
				}
			}
		}
		
		public function set nqaDataList(value:Array):void
		{
			_nqaDataList = value;
			curIndex = 0;
		}
	}
}