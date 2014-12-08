// ActionScript file
package com.huawei.sdnc.view.ipcoreV
{
	import flash.events.Event;
	import com.huawei.sdnc.event.SdncEvt;
	
	public class IpCoreSdncEvt extends SdncEvt
	{
		public static const RESULT_IPCORE:String = "ipcoretopo";
		
		public var myparams:*;
		public function IpCoreSdncEvt(type:String, myparams:*, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			this.myparams = params;
		}
	}
}