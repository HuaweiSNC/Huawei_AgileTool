package com.huawei.sdnc.netmanage.model
{
	import mx.collections.ArrayCollection;

	public class datas
	{
		[Bindable]
		public var AlgoritmXML:XML = new XML;
		[Bindable]
		public var AlgNow:String = new String;
		[Bindable]
		public var Acldatas:Object = new Object;
		[Bindable]
		public var AlNow:String = "";
		[Bindable]
		public var AlNowID:String = "";
		[Bindable]
		public var Alpueue:ArrayCollection = new ArrayCollection;
		[Bindable]
		public var Alresult:Array = new Array;
		private static var instence:datas;
		public function datas()
		{

		}
		
		public static function getInstence():datas
		{
			if(instence==null)
			{
				instence = new datas();
			}
			return instence;
		}
	}
}