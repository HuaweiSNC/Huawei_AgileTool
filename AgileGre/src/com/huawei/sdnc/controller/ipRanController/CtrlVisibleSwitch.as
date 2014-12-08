package com.huawei.sdnc.controller.ipRanController
{
	import com.huawei.sdnc.tools.TopoUtil;
	import com.huawei.sdnc.view.ipRan.physics.PhysicsView;
	
	import spark.components.Label;
	
	import twaver.Consts;
	import twaver.ElementBox;
	import twaver.IData;
	import twaver.Link;
	import twaver.Node;
	import twaver.Styles;
	import twaver.networkx.NetworkX;
	

	public class CtrlVisibleSwitch
	{
		public var page:PhysicsView;
		public var eBox:ElementBox;
		public var pageCtrl:PhysicsLayoutCtrl;
		public function CtrlVisibleSwitch()
		{
		}
		//显示ctrl
		public function showCtrl():void
		{
			if(page.currentState == "withMap"){
				eBox.forEachByBreadthFirst(function(item:IData):void{
					if(item is Node){
						var node:Node=item as Node;
						if(node.getClient("nodeType") == "core"){
							node.setStyle(Styles.CONTENT_TYPE,Consts.CONTENT_TYPE_DEFAULT);
							node.image = "core_withmap";
							node.name = "core";
							node.setStyle(Styles.LABEL_COLOR,0x123456);
						}
					}
					if(item is Link){
						var link:Link = item as Link;
						var f:Node=link.fromNode;
						var t:Node=link.toNode;
						if(f.getClient("nodeType")=="core"||t.getClient("nodeType") == "core"){
							link.setStyle(Styles.LINK_ALPHA,1);
						}
					}
				});
				}
			
			if(page.currentState == "withoutMap"){
				eBox.forEachByBreadthFirst(function(item:IData):void{
					if(item is Node){
						var node:Node=item as Node;
						if(node.getClient("nodeType") == "core"){
							node.setStyle(Styles.CONTENT_TYPE,Consts.CONTENT_TYPE_DEFAULT);
							node.image = "core";
							node.name = "core";
							node.setStyle(Styles.LABEL_COLOR,0xffffff);
						}
					}
					if(item is Link){
						var link:Link = item as Link;
						var f:Node=link.fromNode;
						var t:Node=link.toNode;
						if(f.getClient("nodeType")=="core"||t.getClient("nodeType") == "core"){
							link.setStyle(Styles.LINK_ALPHA,1);
						}
					}
				});
			}
		}
		//隐藏ctrl
		public function hideCtrl():void
		{
			eBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is Node){
					var node:Node=item as Node;
					if(node.getClient("nodeType") == "core"){
						node.setStyle(Styles.CONTENT_TYPE,Consts.CONTENT_TYPE_NONE);
						node.name = "";
					}
				}
				
				if(item is Link){
					var link:Link = item as Link;
					var f:Node=link.fromNode;
					var t:Node=link.toNode;
					if(f.getClient("nodeType")=="core"||t.getClient("nodeType") == "core"){
						link.setStyle(Styles.LINK_ALPHA,0);
					}	
				}
			});
		}
	}
}