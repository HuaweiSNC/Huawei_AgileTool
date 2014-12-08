package com.huawei.sdnc.view.common.AdvancedDataGrid.Util
{
	public class MergeCellInfo extends Object
	{
		public var col:int=-1;
		public var row:int=-1;
		
		public var mergeOwnerCol:int=-1;
		public var mergeOwnerRow:int=-1;
		public var mergeNum:int=1;
		public var mergeDirection:String="vertical";
		public var label:String="";
		
		public function MergeCellInfo()
		{
			super();
		}
		
	}
}