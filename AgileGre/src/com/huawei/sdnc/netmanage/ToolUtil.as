package com.huawei.sdnc.netmanage
{
	import com.huawei.sdnc.netmanage.model.NetNode;
	
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
		public static function doLayoutDefault(networkx:NetworkX):void
		{
			var num:int=ToolUtil.getNodeNumber(networkx)-1;
			if(num<=0)
				return;
			var rad:Number=2*Math.PI/num;
			var initRad:Number=Math.PI/2+rad/2;
			//半径
			var radii:Number=200;
			var i:int=0;
			networkx.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is NetNode){
					var netNode:NetNode=item as NetNode;
					if(i==0){
						netNode.location=new Point(centerPoint.x,centerPoint.y);
					}else{
						var x:Number=centerPoint.x+radii*Math.cos(initRad+i*rad);
						var y:Number=centerPoint.y+radii*Math.sin(initRad+i*rad);
						netNode.location=new Point(x,y);
					}
					i++;
				}
			});
		}
	}
}