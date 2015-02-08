package com.huawei.overte.view.link
{
	
	import twaver.Consts;
	import twaver.Link;
	import twaver.Node;
	
	public class MyNewLink extends Link
	{
		
		public var _fromName:String;
		public var _toName:String;
		
		
		public function MyNewLink(id:Object=null, fromNode:Node=null, toNode:Node=null)
		{
			super(id, fromNode, toNode);
		}
		override public function get elementUIClass():Class {
			return MyNewLinkUI;
		}
		
	}
}