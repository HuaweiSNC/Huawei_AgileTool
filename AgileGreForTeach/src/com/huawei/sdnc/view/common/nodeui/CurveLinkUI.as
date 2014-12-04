package com.huawei.sdnc.view.common.nodeui
{
	import com.huawei.sdnc.view.common.node.CurveLink;
	
	import flash.geom.Point;
	
	import twaver.Collection;
	import twaver.ICollection;
	import twaver.Node;
	import twaver.networkx.NetworkX;
	import twaver.networkx.ui.LinkUI;

	public class CurveLinkUI extends LinkUI
	{
		public function CurveLinkUI(network:NetworkX, link:CurveLink)
		{
			super(network,link);
		}
		public override function calculateLinkPoints():ICollection
		{
			var fromPoint:Point = this.fromPoint;
			var toPoint:Point = this.toPoint;
			if(!fromPoint || !toPoint)
			{
				return null;
			}
			var points:ICollection = new Collection;
			points.addItem(fromPoint);
			if(this.link.getStyle("overturn"))
			{
				points.addItem([new Point(fromPoint.x,toPoint.y),toPoint]);
			}
			else 
			{
				points.addItem([new Point(toPoint.x,fromPoint.y),toPoint]);
			}
			return points;
		}
	}
}