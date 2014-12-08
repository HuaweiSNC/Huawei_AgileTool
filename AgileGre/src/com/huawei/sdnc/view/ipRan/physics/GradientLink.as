package com.huawei.sdnc.view.ipRan.physics
{
	
	import twaver.Node;
	import twaver.ShapeLink;
	import twaver.Styles;
	
	public class GradientLink extends ShapeLink {
		public function GradientLink(id:Object=null, fromNode:Node=null, toNode:Node=null) {
			super(id, fromNode, toNode);
		}
		
		override public function get elementUIXClass():Class {
			return GradientLinkUI;
		}
	}
}