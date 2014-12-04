package com.huawei.sdnc.controller.ipCoreController
{
	import com.huawei.sdnc.event.SdncEvt;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.Domain;
	import com.huawei.sdnc.view.common.MenuBtn;
	import com.huawei.sdnc.view.common.node.StateNode;
	import com.huawei.sdnc.view.gre.Console;
	import com.huawei.sdnc.view.gre.DevicesSetUpWindow;
	import com.huawei.sdnc.view.gre.MyLink;
	import com.huawei.sdnc.view.gre.dataHandle.DataHandleTool;
	
	import flash.events.Event;
	import flash.events.KeyboardEvent;
	import flash.events.MouseEvent;
	import flash.events.TimerEvent;
	import flash.net.URLRequest;
	import flash.net.navigateToURL;
	import flash.ui.Keyboard;
	import flash.utils.Timer;
	
	import mx.managers.PopUpManager;
	
	import twaver.IData;
	import twaver.IElement;
	import twaver.Node;
	import twaver.Styles;
	import twaver.networkx.NetworkX;

	public class IpCoreCtrl
	{
		[Bindable]
		public var page:Domain;
		private var __app:sdncui2;
		private var devicesSetUpWindow:DevicesSetUpWindow;
		private var networkx:NetworkX;
		
		private var ctrl:Boolean = false; 
		private var alt:Boolean = false;  
		private var d:Boolean = false; 
		/**定时器，用于定时获取设备CPU和RAM状态*/
		private var __timer:Timer;
		public function IpCoreCtrl()
		{
			
		}
		
		public function onInit():void
		{
			__app = SdncUtil.app;
		}
		public function onCreate():void
		{
			__app.addEventListener(SdncEvt.OPEN_SETUP_DEVICES_WINDOW,openSetUpDevicesWindow);
			__app.addEventListener(SdncEvt.CLOSE_SETUP_DEVICES_WINDOW,closeSetUpDevicesWindow);
			
			//打开控制台
			__app.addEventListener(SdncEvt.OPEN_CONSOLE,openConsole);
			__app.addEventListener(SdncEvt.CLOSE_CONSOLE,closeConsole);
			
			page.systemManager.stage.addEventListener(KeyboardEvent.KEY_UP,myKeyUp);
			page.systemManager.stage.addEventListener(KeyboardEvent.KEY_DOWN,myKeyDown);
			
            //打开帮助文档
			__app.addEventListener(SdncEvt.OPEN_HELP,openHelp);
			page.focusManager.setFocus(page.btn);
			
			__app.addEventListener(SdncEvt.CLEAR_PATH,clearPath);
			//打开流量统计图标
//			__app.addEventListener(SdncEvt.OPEN_FLOW_CHART,openFlowChart);
			var viewTypesArr:Array = SdncUtil.viewTypes[SdncUtil.curEntry] as Array;
			var len:int = viewTypesArr.length;
			for(var i:int = 0;i < len;i++)
			{
				var menuBtn:MenuBtn = new MenuBtn();
				menuBtn.id = viewTypesArr[i].name;
				menuBtn.label = viewTypesArr[i].desc;
				menuBtn.width = 155;
				menuBtn.percentHeight = 100;
				menuBtn.addEventListener(MouseEvent.CLICK,page.menuClickHandler);
				page.menuHg.addElementAt(menuBtn,i);
			}
			page.menuHg.getElementAt(0).dispatchEvent(new MouseEvent(MouseEvent.CLICK));
			__timer = new Timer(1000);
			__timer.addEventListener(TimerEvent.TIMER,refreshAllNodes);
		}
		
		
        protected function openHelp(event:Event):void
		{
			navigateToURL(new URLRequest("assets/help/help.html"),"_blank");
		}
		

		private function myKeyUp(evt:KeyboardEvent):void{
			if(evt.keyCode==Keyboard.CONTROL){
				ctrl=false;
			}
			if(evt.keyCode==Keyboard.ALTERNATE){
				alt=false;
			}
			if(evt.keyCode==Keyboard.D){
				d=false;
			}
		}
		private function myKeyDown(evt:KeyboardEvent):void{
			if(!ctrl&&evt.keyCode==Keyboard.CONTROL){
				ctrl=true;
				trrigerEvent();
			}
			if(!alt&&evt.keyCode==Keyboard.ALTERNATE){
				alt=true;
				trrigerEvent();
			}
			if(!d&&evt.keyCode==Keyboard.D){
				d=true;
				trrigerEvent();
			}
			
		}
		private function trrigerEvent():void
		{
			if(ctrl&&alt&&d){
				if(DataHandleTool.console!=null){
					DataHandleTool.console.visible=true;
				}
//				if(console==null)
//				{
//					__app.dispatchEvent(new SdncEvt(SdncEvt.OPEN_CONSOLE));
//					DataHandleTool.console=console;
//				}
				
			}
		}
		/**打开设备列表窗口*/
		public function openSetUpDevicesWindow(e:SdncEvt):void
		{
			if(devicesSetUpWindow!=null)
			{
				return;
			}
			devicesSetUpWindow= new DevicesSetUpWindow;
			devicesSetUpWindow.horizontalCenter="0";
			devicesSetUpWindow.verticalCenter="0";
			page.addElement(devicesSetUpWindow);
		}
		/**关闭设备列表窗口*/
		public function closeSetUpDevicesWindow(e:SdncEvt):void
		{
			if(devicesSetUpWindow!=null)
			{
				page.removeElement(devicesSetUpWindow);
				page.physicsView.mouseChildren=true;
				page.menuHg.mouseChildren=true;
				devicesSetUpWindow=null;
			}
		}
		/**
		 *每过一段时间刷新所有的节点信息 
		 *
		 */
		private function refreshTimerStart(event:SdncEvt):void
		{
			if(event.params == "ON")
			{
				refreshAllNodes();
				__timer.start();
			}else
			{
				__timer.reset();
				//__timer.delay = 500;
			}
		}
		
		private function refreshAllNodes(e:Event = null):void
		{
			page.physicsView.networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is StateNode)
				{
					var node:StateNode = item as StateNode;
//					var urlArr:Array = SdncUtil.dcUrlInfos[node._curDcName];
					node.updata("");
				}
			});
		}
		private var console:Console=null;
		private function openConsole(e:SdncEvt):void
		{
			if(console!=null)
			{
				console.visible=true;
//				DataHandleTool.console=console;
				return;
			}else{
				console= new Console;
				DataHandleTool.console=console;
				console.visible=false;
				page.addElement(console);
			}
		}
		/**
		 *关闭控制台 
		 */		
		private function closeConsole(e:SdncEvt):void
		{
			if(console!=null)
			{
				DataHandleTool.console.visible=false;
			}
		}
		private function clearPath(e:SdncEvt):void
		{
			page.physicsView.networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
				if(item is MyLink){
					var link:MyLink=item as MyLink;
					link.setStyle(Styles.LINK_COLOR, 0x60c6fb);
					link.name="";
				}
			});
		}
	}
}