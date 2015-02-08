package com.huawei.overte.model
{
	import mx.collections.ArrayCollection;
	public class MrtgDatas
	{
		[Bindable]
		public var taskName:ArrayCollection = new ArrayCollection;
		public var ipandport:String = "";
		public var setsme:XML = new XML;
		public function MrtgDatas()
		{
		}
		private static var instence:MrtgDatas;
		public static function getInstence():MrtgDatas
		{
			if(instence==null)
			{
				instence = new MrtgDatas();
			}
			return instence;
		}
		
	}
}