package com.huawei.sdnc.view.common.AdvancedDataGrid.Util
{
	
	import mx.controls.advancedDataGridClasses.AdvancedDataGridItemRenderer;
	import mx.core.IFactory;
	
	public class ItemRendererIFactory implements mx.core.IFactory
	{
		public var classname:String="";
		
		public function newInstance():*{
			var ret:Object;
			
			switch(classname.toLowerCase()){
				/*case "defaultheader":
					ret=new AdgHeaderRenderer();*/
				case "defaultitem":
				default:
					ret=new AdvancedDataGridItemRenderer();
				break;
			}
			
			return ret;
		} 
	}
}