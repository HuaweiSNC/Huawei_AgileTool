package com.huawei.sdnc.tools
{
	
	import flash.geom.Point;
	import flash.system.Capabilities;
	
	import twaver.IData;
	import twaver.Node;
	import twaver.networkx.NetworkX;

	public class ToolUtil
	{
		public function ToolUtil()
		{
		}
		private static var centerPoint:Point=new Point(Capabilities.screenResolutionX/2,Capabilities.screenResolutionY/3);
		public static function getNodeNumber(networkx:NetworkX):int
		{
			var i:int=0;
			networkx.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is Node){
					i++;
				}
			});
			return i;
		}
	}
}