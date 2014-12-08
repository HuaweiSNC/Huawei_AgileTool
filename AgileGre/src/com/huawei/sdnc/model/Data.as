package com.huawei.sdnc.model
{
	import mx.collections.ArrayCollection;
	
	public class Data
	{
		[Bindable]
		public var states:ArrayCollection = new ArrayCollection;
		public var delayState:Boolean;
		public var flowState:Boolean;
		private static var instence:Data;
		public function Data()
		{
			
		}
		
		public static function getInstence():Data
		{
			if(instence==null)
			{
				instence = new Data();
			}
			return instence;
		}
	}
}