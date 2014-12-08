package com.huawei.sdnc.controller.ipCoreController
{
	import com.huawei.sdnc.event.SdncEvt;
	import com.huawei.sdnc.model.Device;
	import com.huawei.sdnc.model.Nqa;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.common.node.StateNode;
	import com.huawei.sdnc.view.ipCore_DCI.MyLink;
	
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	
	import twaver.Styles;

	public class GetNqaData
	{
		private var desIp:String;
		private var link:MyLink;
		/*private var timer:Timer;*/
		private var stateNode:StateNode;
		private var switchs:Boolean=false;
		private var _app:sdncui2;
		public function GetNqaData()
		{
			_app=SdncUtil.app;
			/*_app.addEventListener(SdncEvt.SWITCH_NQA,switchNqa);*/
		}
		
		/*public function getNqaIfmResult(desIp1:String,link1:MyLink,stateNode1:StateNode):void
		{
			desIp=desIp1;
			link=link1;
			timer=new Timer(60000);
			stateNode=stateNode1;
			timer.addEventListener(TimerEvent.TIMER, timerHandler); 
			if(switchs){
				timer.start();
				timerHandler(null);
			}
		}*/
		
		public function setNqaData(desIp1:String,link1:MyLink,stateNode1:StateNode):void
		{
			desIp=desIp1;
			link=link1;
			stateNode=stateNode1;
		}
		
		/*private function timerHandler(e:TimerEvent):void
		{
			var nqa:Nqa = new Nqa();
			nqa.getNqaData(stateNode,desIp,showData);
		}*/
		
		public function startRequestNqa():void
		{
			var nqa:Nqa = new Nqa();
			nqa.getNqaData(stateNode,desIp,showData);
		}
		
		private function showData(value:String,bz:Number):void{
			var nqaData:String = value;
			var str:String="            ";
			if(nqaData != null&&nqaData!=""){
				str+=nqaData+"ms";
			}
			link.name =nqaData!=""?nqaData+"ms":"";
			link.setStyle(Styles.LABEL_COLOR,0xffffff);
			if(!SdncUtil.nqaTimer.nqaSwitch){
				link.name = "";
			}
		}
		
		public function clearData():void
		{
			link.name = "";
		}
		/**
		 * 开关
		 * @param e
		 * 
		 */		
		/*private function switchNqa(e:SdncEvt):void
		{
			var state:Boolean=e.params;
			switchs=state;
			if(timer.hasEventListener(TimerEvent.TIMER)){
				if(state){
					if(!timer.running){
						timer.start();
						timerHandler(null);
					}
				}else{
					if(timer.running){
						timer.stop();
						link.name ="";
					}
				}
			}
		}*/
		public function pingPath():void{
			var nqa:Nqa = new Nqa();
			nqa.getNqaData(stateNode,desIp,onPingResult);
		}
		private function onPingResult(value:String,bz:Number):void{
			if(value=="NA"){
				pingPath();
			}else{
				link.name =value+"ms";
				var color:int = link.getClient("linkcolor");
//				link.setStyle(Styles.LINK_COLOR,0x2700b8);
				link.setStyle(Styles.LINK_COLOR,color);
				link.setStyle(Styles.LABEL_COLOR,0xffffff);
			}
			
		}
	}
}