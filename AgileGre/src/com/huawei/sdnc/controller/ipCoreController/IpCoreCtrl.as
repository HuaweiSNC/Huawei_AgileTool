package com.huawei.sdnc.controller.ipCoreController
{
	import com.huawei.sdnc.event.SdncEvt;
	import com.huawei.sdnc.netmanage.view.AddLine;
	import com.huawei.sdnc.netmanage.view.AddNetmanage;
	import com.huawei.sdnc.netmanage.view.AddPoint;
	import com.huawei.sdnc.netmanage.view.ConfigPayandBandwidth;
	import com.huawei.sdnc.netmanage.view.PointEdit;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.Ipcore;
	import com.huawei.sdnc.view.common.MenuBtn;
	import com.huawei.sdnc.view.common.node.StateNode;
	import com.huawei.sdnc.view.ipCore_DCI.Console;
	import com.huawei.sdnc.view.ipCore_DCI.DCISetUpWindow;
	import com.huawei.sdnc.view.ipCore_DCI.DevicesSetUpWindow;
	import com.huawei.sdnc.view.ipCore_DCI.FlowDefine;
	import com.huawei.sdnc.view.ipCore_DCI.FlowEnter;
	import com.huawei.sdnc.view.ipCore_DCI.FlowStatistics;
	import com.huawei.sdnc.view.ipCore_DCI.MyLink;
	import com.huawei.sdnc.view.ipCore_DCI.PathCalculation;
	import com.huawei.sdnc.view.ipCore_DCI.PipelineDefine;
	import com.huawei.sdnc.view.ipCore_DCI.SystemRollback;
	import com.huawei.sdnc.view.ipCore_DCI.dataHandle.DataHandleTool;
	
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
		public var page:Ipcore;
		private var __app:sdncui2;
		private var dciSetUpWindow:DCISetUpWindow;
		private var devicesSetUpWindow:DevicesSetUpWindow;
		private var systemRollbackWindow:SystemRollback;
		private var pathCalcu:PathCalculation;
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
			__app.addEventListener(SdncEvt.OPEN_SETUP_DCI_WINDOW,openSetUpDCIWindow);
			__app.addEventListener(SdncEvt.CLOSE_SETUP_DCI_WINDOW,closeSetUpDCIWindow);
			__app.addEventListener(SdncEvt.OPEN_SETUP_DEVICES_WINDOW,openSetUpDevicesWindow);
			__app.addEventListener(SdncEvt.CLOSE_SETUP_DEVICES_WINDOW,closeSetUpDevicesWindow);
			__app.addEventListener(SdncEvt.OPEN_SYSTEM_ROLLBACK_WINDOW,openSystemRollbackWindow);
			__app.addEventListener(SdncEvt.CLOSE_SYSTEM_ROLLBACK_WINDOW,closeSystemRollbackWindow);
			__app.addEventListener(SdncEvt.OPEN_FLOW_EDIT,openFlowEdit);
			__app.addEventListener(SdncEvt.OPEN_PATH_CALCULATION,openPathCalcWindow);
			__app.addEventListener(SdncEvt.CLOSE_PATH_CALCULATION,closePathCalcWindow);
			__app.addEventListener(SdncEvt.CLOSE_FLOW_DEFINE,closeFlowDefine);
			
			__app.addEventListener(SdncEvt.OPEN_PIPELINE_DEFINE,openPipelineDefine);
			__app.addEventListener(SdncEvt.CLOSE_PIPELINE_DEFINE,closePipelineDefine);
			
			__app.addEventListener(SdncEvt.OPEN_FLOW_ENTER,openFlowEnter);
			__app.addEventListener(SdncEvt.CLOSE_FLOW_ENTER,closeFlowEnter);
			
			__app.addEventListener(SdncEvt.REFRESH_NODES_CPU_RAM,refreshTimerStart);
			
			//打开控制台
			__app.addEventListener(SdncEvt.OPEN_CONSOLE,openConsole);
			__app.addEventListener(SdncEvt.CLOSE_CONSOLE,closeConsole);
			
			page.systemManager.stage.addEventListener(KeyboardEvent.KEY_UP,myKeyUp);
			page.systemManager.stage.addEventListener(KeyboardEvent.KEY_DOWN,myKeyDown);
			
            //打开帮助文档
			__app.addEventListener(SdncEvt.OPEN_HELP,openHelp);
			page.focusManager.setFocus(page.btn);
			
			//网规
			__app.addEventListener(SdncEvt.OPEN_POINT_ADD,openPoint);
			__app.addEventListener(SdncEvt.OPEN_LINE_ADD,openLine);
			__app.addEventListener(SdncEvt.OPEN_NETRULE_ADD,openNetrule);
			__app.addEventListener(SdncEvt.OPEN_LINE_EDIT,openLineE);
			__app.addEventListener(SdncEvt.OPEN_POINT_EDIT,openPointE);
			
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
		
		protected function closeSystemRollbackWindow(event:SdncEvt):void
		{
			if(systemRollbackWindow!=null)
			{
				page.removeElement(systemRollbackWindow);
				page.physicsView.mouseChildren=true;
				page.menuHg.mouseChildren=true;
				systemRollbackWindow=null;
			}
		}
		
		protected function openSystemRollbackWindow(event:SdncEvt):void
		{
			if(systemRollbackWindow!=null)
			{
				return;
			}
			systemRollbackWindow= new SystemRollback;
			systemRollbackWindow.horizontalCenter="0";
			systemRollbackWindow.verticalCenter="0";
			page.addElement(systemRollbackWindow);
		}
		
		/**关闭路径计算窗口*/
		protected function closePathCalcWindow(event:SdncEvt):void
		{
			if(pathCalcu!=null)
			{
				page.removeElement(pathCalcu);
				page.physicsView.mouseChildren=true;
				page.menuHg.mouseChildren=true;
				pathCalcu=null;
			}
		}
		/**打开路径计算窗口*/
		protected function openPathCalcWindow(event:SdncEvt):void
		{
			var ele:IElement=event.params;
			if(pathCalcu!=null)
			{
				return;
			}
			pathCalcu= new PathCalculation;
			pathCalcu.element=ele;
			pathCalcu.horizontalCenter="0";
			pathCalcu.verticalCenter="0";
			page.addElement(pathCalcu);
		}
		
        protected function openHelp(event:Event):void
		{
			navigateToURL(new URLRequest("assets/help/help.html"),"_blank");
		}
		
		/**
		 * 网规视图邮件菜单方法
		 * */
		public function openPoint(e:SdncEvt):void
		{
			var addpoint:AddPoint=AddPoint(PopUpManager.createPopUp(page,AddPoint,false));
			PopUpManager.centerPopUp(addpoint);
		}
		
		public function openLine(e:SdncEvt):void
		{
			var ele:*=e.params;
			var addline:AddLine=AddLine(PopUpManager.createPopUp(page,AddLine,false));
			addline.element=ele;
			PopUpManager.centerPopUp(addline);
		}
		
		public function openNetrule(e:SdncEvt):void
		{
			var addNetrule:AddNetmanage=AddNetmanage(PopUpManager.createPopUp(page,AddNetmanage,false));
			PopUpManager.centerPopUp(addNetrule);
		}
		
		public function openLineE(e:SdncEvt):void
		{
			var ele:*=e.params;
			var LineE:ConfigPayandBandwidth = ConfigPayandBandwidth(PopUpManager.createPopUp(page,ConfigPayandBandwidth,false));
			LineE.element=ele;
			PopUpManager.centerPopUp(LineE);
		}

		public function openPointE(e:SdncEvt):void 
		{
			var ele:*=e.params;
			var PointE:PointEdit = PointEdit(PopUpManager.createPopUp(page,PointEdit,false));
			PointE.element=ele;
			PopUpManager.centerPopUp(PointE);
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
		public function openSetUpDCIWindow(e:SdncEvt):void
		{
			if(dciSetUpWindow!=null)
			{
				return;
			}
			dciSetUpWindow=new DCISetUpWindow;
			dciSetUpWindow.horizontalCenter="0";
			dciSetUpWindow.verticalCenter="0";
			page.addElement(dciSetUpWindow);
			page.physicsView.mouseChildren=false;
			page.menuHg.mouseChildren=false;
			
			networkx=page.physicsView.networkX;
			networkx.addEventListener(MouseEvent.CLICK,handleFunction);
		}
		
		private function handleFunction(e:MouseEvent):void
		{
			var ele:IElement=networkx.getElementByMouseEvent(e);
			if(ele is Node)
			{
				//					var value:String=testPathWindow.churukou.selection.value.toString();
				var value:String=dciSetUpWindow.textInputIndex as String;
//				if(value=="0")
//				{
//					dciSetUpWindow.entrance_textInput.text="entranceNode";
//					var al:ArrayList=new ArrayList();
//					al.addItem({label:"intertrace",isSelected:"0"});
//					al.addItem({label:"intertrace2",isSelected:"0"});
//					al.addItem({label:"intertrace3",isSelected:"0"});
//					al.addItem({label:"intertrace4",isSelected:"0"});
//					al.addItem({label:"intertrace5",isSelected:"0"});
//					testPathWindow.dp=al;
//				}else
//				{
//					testPathWindow.export_textInput.text="exportNode";
//					var al1:ArrayList=new ArrayList();
//					al1.addItem({label:"intertrace",isSelected:"0"});
//					al1.addItem({label:"intertrace2",isSelected:"0"});
//					testPathWindow.dp2=al1;
//				}
			}
		}
		public function closeSetUpDCIWindow(e:SdncEvt):void
		{
			if(dciSetUpWindow!=null)
			{
				page.removeElement(dciSetUpWindow);
				page.physicsView.mouseChildren=true;
				page.menuHg.mouseChildren=true;
				dciSetUpWindow=null;
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
		private var flowDefine:FlowDefine;
		/**打开流定义窗口*/
		public function openFlowEdit(e:SdncEvt):void
		{
			if(flowDefine!=null)
			{
				return;
			}
			flowDefine= new FlowDefine;
			flowDefine.element=e.params as IElement;
			flowDefine.verticalCenter="-70";
			flowDefine.horizontalCenter="0";
			page.physicsView.mouseChildren=false;
			page.menuHg.mouseChildren=false;
			page.addElement(flowDefine);
		}
		/**关闭流定义窗口*/
		public function closeFlowDefine(e:SdncEvt):void
		{
			if(flowDefine!=null)
			{
				page.removeElement(flowDefine);
				page.physicsView.mouseChildren=true;
				page.menuHg.mouseChildren=true;
				flowDefine=null;
			}
		}
		private var pipelineDefine:PipelineDefine;
		/**打开管道定义窗口*/
		public function openPipelineDefine(e:SdncEvt):void
		{
			if(pipelineDefine!=null)
			{
				return;
			}
			pipelineDefine= new PipelineDefine;
			pipelineDefine.element=e.params;
			pipelineDefine.verticalCenter="-70";
			pipelineDefine.horizontalCenter="0";
			page.addElement(pipelineDefine);
		}
		/**关管道定义窗口*/
		public function closePipelineDefine(e:SdncEvt):void
		{
			if(pipelineDefine!=null)
			{
				page.removeElement(pipelineDefine);
				page.physicsView.mouseChildren=true;
				page.menuHg.mouseChildren=true;
				pipelineDefine=null;
			}
		}
		private var flowEnter:FlowEnter;
		/**打开流导入管道窗口*/
		public function openFlowEnter(e:SdncEvt):void
		{
			if(flowEnter!=null)
			{
				return;
			}
			flowEnter= new FlowEnter;
			flowEnter.element=e.params;
			flowEnter.verticalCenter="-70";
			flowEnter.horizontalCenter="0";
			page.addElement(flowEnter);
		}
		/**关闭流导入管道窗口*/
		public function closeFlowEnter(e:SdncEvt):void
		{
			if(flowEnter!=null)
			{
				page.removeElement(flowEnter);
				page.physicsView.mouseChildren=true;
				page.menuHg.mouseChildren=true;
				flowEnter=null;
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
//				page.removeElement(console);
//				page.physicsView.mouseChildren=true;
//				page.menuHg.mouseChildren=true;
//				console=null;
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
		private var flowStaticstics:FlowStatistics=null;
		private function openFlowChart(e:SdncEvt):void
		{
			if(flowStaticstics==null){
				flowStaticstics=new FlowStatistics;
				flowStaticstics.right=30;
				page.addElement(flowStaticstics);
			}else{
				if(!flowStaticstics.visible)
				flowStaticstics.visible=true;
			}
		}
	}
}