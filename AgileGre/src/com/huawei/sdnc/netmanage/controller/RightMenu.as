package com.huawei.sdnc.netmanage.controller
{
	import com.huawei.sdnc.event.SdncEvt;
	import com.huawei.sdnc.netmanage.model.NetLink;
	import com.huawei.sdnc.netmanage.model.NetNode;
	import com.huawei.sdnc.tools.SdncUtil;
	
	import flash.events.ContextMenuEvent;
	import flash.geom.Point;
	import flash.ui.ContextMenu;
	import flash.ui.ContextMenuItem;
	
	import twaver.ICollection;
	import twaver.IElement;
	import twaver.Styles;
	import twaver.networkx.NetworkX;
	public class RightMenu
	{
		public function RightMenu()
		{
		}
		public function newrightmenu(networkX:NetworkX):void
		{
			/**初始化右键菜单*/
			networkX.contextMenu = new ContextMenu();
			networkX.contextMenu.hideBuiltInItems();	
			
			networkX.contextMenu.addEventListener(ContextMenuEvent.MENU_SELECT, function(e:ContextMenuEvent):void{			
				var p:Point = new Point(e.mouseTarget.mouseX / networkX.zoom, e.mouseTarget.mouseY / networkX.zoom);		 	 	
				var datas:ICollection = networkX.getElementsByLocalPoint(p);
				if (datas.count > 0) {
					networkX.selectionModel.setSelection(datas.getItemAt(0));
				}else{
					networkX.selectionModel.clearSelection();
				}				
//				if(networkX.selectionModel.count == 0){
//					networkX.contextMenu.customItems = [];
//				}else{
					var item1:ContextMenuItem = new ContextMenuItem("增加节点", true);
					item1.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, handler);
					var item2:ContextMenuItem = new ContextMenuItem("增加线");
					item2.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, handler);
					var item3:ContextMenuItem = new ContextMenuItem("计算网规");
					item3.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, handler);
					networkX.contextMenu.customItems = [item1, item2, item3];  
					if (datas.count > 0) {
						var item:*=datas.getItemAt(0);
						if(item is NetLink){
							var item4:ContextMenuItem = new ContextMenuItem("设置线属性", true);
							networkX.contextMenu.customItems.push(item4);
							item4.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, handler);
						}
						if(item is NetNode){
							var item5:ContextMenuItem = new ContextMenuItem("设置节点属性", true);
							networkX.contextMenu.customItems.push(item5);
							item5.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, handler);
						}
					}
					
					
//				}
			});
			Styles.setStyle(Styles.SELECT_COLOR,0xffffff);
			var handler:Function = function(e:ContextMenuEvent):void{
				var i:int = 0;
				var element:IElement = null;
				var item:ContextMenuItem = ContextMenuItem(e.target);
				if(item.caption == "Remove Selection"){
					networkX.removeSelection();					
				}
				else{
					//for(i=0; i<networkX.selectionModel.selection.count; i++){
					var p:Point = new Point(e.mouseTarget.mouseX / networkX.zoom, e.mouseTarget.mouseY / networkX.zoom);
					var datas:ICollection = networkX.getElementsByLocalPoint(p);
					if(datas.count>0)
						element = datas.getItemAt(0);
					else
						element=null;
					
					if(item.caption == "增加节点"){
						SdncUtil.app.dispatchEvent(new SdncEvt(SdncEvt.OPEN_POINT_ADD,element));
					}
					else if(item.caption == "增加线"){
						SdncUtil.app.dispatchEvent(new SdncEvt(SdncEvt.OPEN_LINE_ADD,element));
					}
					else if(item.caption == "计算网规"){
						//打开流导入
						SdncUtil.app.dispatchEvent(new SdncEvt(SdncEvt.OPEN_NETRULE_ADD,element));
					}
					else if(item.caption == "设置线属性"){
						//打开流导入
						SdncUtil.app.dispatchEvent(new SdncEvt(SdncEvt.OPEN_LINE_EDIT,element));
					}
					else if(item.caption == "设置节点属性"){
						//打开流导入
						SdncUtil.app.dispatchEvent(new SdncEvt(SdncEvt.OPEN_POINT_EDIT,element));
					}
					//}
				}
			}
		}
	}
}