package com.huawei.overte.control
{
	import com.huawei.overte.handle.DataHandleTool;
	import com.huawei.overte.model.Device;
	import com.huawei.overte.service.SdnUIService;
	import com.huawei.overte.tools.ConnUtil;
	import com.huawei.overte.tools.DataGetter;
	import com.huawei.overte.tools.PopupManagerUtil;
	import com.huawei.overte.tools.SdncUtil;
	import com.huawei.overte.view.common.EnterButton;
	import com.huawei.overte.view.overte.GeneralView;
	import com.huawei.overte.view.overte.OverTEView;
	import com.huawei.overte.view.overte.titlewindows.Console;
	
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.utils.ByteArray;
	import flash.utils.Timer;
	import flash.utils.setTimeout;
	
	import flexlib.controls.tabBarClasses.SuperTab;
	
	import mx.controls.Alert;
	import mx.core.INavigatorContent;
	import mx.managers.PopUpManager;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.httpclient.events.HttpResponseEvent;

	public class GeneralCtrl
	{
		[Bindable]public var page:GeneralView;
		/**主应用程序*/
		private var __app:overTegui2;
		/**标识管理域是否打开*/
		public var flag:Boolean=false;
		[Bindable]
		[Embed(source="assets/imgs/loginImgs/button_ipcoreEntry.png")]
		private var __IPCoreImg:Class;
		
		/**管理域xml数据*/
		private var _areaXML:XML;
		/**当前管理域对象**/
		private var _cuarea:Object;
		
		private var sdnService:SdnUIService = new SdnUIService();
		private var getter:DataGetter = new DataGetter();
		/**当前管理域下的Topo数据**/
		public var topoxml:XMLList;
		/**当前项目服务IP地址**/
		private var opsIp:String=SdncUtil.opsIp;
		/**当前项目服务名称**/
		private var webname:String=SdncUtil.projectname ;
		[Bindable]
		private var url:String="" ;
		public function GeneralCtrl()
		{
		}
		/**初始化此组件的内部结构。*/
		public function onInit():void
		{
			__app = SdncUtil.app;
//			url = __app.strurl
			webname = SdncUtil.projectname
			opsIp =SdncUtil.opsIp
			DataHandleTool.showOnConsole("__app"+__app+"opsIp"+opsIp+"===="+SdncUtil.opsIp+"===="+url+"ConnUtil.url"+ConnUtil.url);
		}
		/**当组件完成其构建、属性处理、测量、布置和绘制时分派方法*/
		public function onCreate():void
		{
			if(SdncUtil.cuProjectType=="test"){
				/**请求管理域 XML*/
				ConnUtil.getInstence().query("assets/xml/sdn_office_areas.xml",
					function(evt:ResultEvent):void{
						var javatest:String= "<domains>	<domain>		<id>1</id>		<name>管理域A</name>		<type>Agile TE View</type>		<topo>			<toponodes>				<toponode><nodeID>1</nodeID><nodeType>1.1.1.1</nodeType><systemName>P1</systemName><userName>netconf</userName><passWd></passWd><version>3.0</version><productType>NE5000E</productType>				</toponode>				<toponode><nodeID>2</nodeID><nodeType>2.2.2.2</nodeType><systemName>PE2</systemName><userName>netconf</userName><passWd></passWd><version>3.0</version><productType>NE5000E</productType>				</toponode>				<toponode><nodeID>3</nodeID><nodeType>3.3.3.3</nodeType><systemName>P2</systemName><userName>netconf</userName><passWd></passWd><version>3.0</version><productType>NE5000E</productType>				</toponode>				<toponode><nodeID>4</nodeID><nodeType>4.4.4.4</nodeType><systemName>PE1</systemName><userName>netconf</userName><passWd></passWd><version>3.0</version><productType>NE5000E</productType>				</toponode>				</toponodes>			<topoLinks>				<topoLink><name></name><headNodeConnector><Connectorid>GE0/1/1</Connectorid><toponode>	<nodeID>4</nodeID>	<nodeType>4.4.4.4</nodeType></toponode><Connectortype></Connectortype></headNodeConnector><tailNodeConnector><Connectorid>GE0/1/1</Connectorid><toponode>	<nodeID>1</nodeID>	<nodeType>1.1.1.1</nodeType></toponode><Connectortype></Connectortype></tailNodeConnector><cost>1</cost><bandwidth>10</bandwidth>				</topoLink>				<topoLink><name></name><headNodeConnector><Connectorid>GE0/1/4</Connectorid><toponode>	<nodeID>4</nodeID>	<nodeType>4.4.4.4</nodeType></toponode><Connectortype></Connectortype></headNodeConnector><tailNodeConnector><Connectorid>GE0/1/4</Connectorid><toponode>	<nodeID>3</nodeID>	<nodeType>3.3.3.3</nodeType></toponode><Connectortype></Connectortype></tailNodeConnector><cost>1</cost><bandwidth>10</bandwidth>				</topoLink>				<topoLink><name></name><headNodeConnector><Connectorid>GE0/1/3</Connectorid><toponode>	<nodeID>1</nodeID>	<nodeType>1.1.1.1</nodeType></toponode><Connectortype></Connectortype></headNodeConnector><tailNodeConnector><Connectorid>GE0/1/3</Connectorid><toponode>	<nodeID>3</nodeID>	<nodeType>3.3.3.3</nodeType></toponode><Connectortype></Connectortype></tailNodeConnector><cost>1</cost><bandwidth>10</bandwidth>				</topoLink>				<topoLink><name></name><headNodeConnector><Connectorid>GE0/1/2</Connectorid><toponode>	<nodeID>1</nodeID>	<nodeType>1.1.1.1</nodeType></toponode><Connectortype></Connectortype></headNodeConnector><tailNodeConnector><Connectorid>GE0/1/2</Connectorid><toponode>	<nodeID>2</nodeID>	<nodeType>2.2.2.2</nodeType></toponode><Connectortype></Connectortype></tailNodeConnector><cost>1</cost><bandwidth>10</bandwidth>				</topoLink>				<topoLink><name></name><headNodeConnector><Connectorid>GE0/1/2</Connectorid><toponode>	<nodeID>3</nodeID>	<nodeType>3.3.3.3</nodeType></toponode><Connectortype></Connectortype></headNodeConnector><tailNodeConnector><Connectorid>GE0/1/1</Connectorid><toponode>	<nodeID>2</nodeID>	<nodeType>2.2.2.2</nodeType></toponode><Connectortype></Connectortype></tailNodeConnector><cost>1</cost><bandwidth>10</bandwidth>				</topoLink>			</topoLinks>		</topo></domain></domains>"
						_areaXML = XML(evt.result.toString());//请求假数据用
						_areaXML = XML(javatest);//请求java假数据用
						getAllArea(_areaXML)
					},onFailed);
			}else{
				/**请求管理域**/
				DataHandleTool.showOnConsole(ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains");
			
				setTimeout(getdbarea,1000);
			}
		}
		public function getdbarea():void{
			__app = SdncUtil.app;
			url = __app.strurl
			webname = SdncUtil.projectname
			opsIp =SdncUtil.opsIp
			PopupManagerUtil.getInstence().closeLoading();
			PopupManagerUtil.getInstence().popupLoading(page);
			var uri:String=url+"/agilete/domains";
			DataHandleTool.showOnConsole(uri);
			sdnService.ipcoreRequest(uri, onGetAreaResult, onGetAreaFault);
		}
		/** HTTPService 请求失败函数 */
		private function onFailed(evt:FaultEvent):void
		{
			trace(evt.message);	
		}
		/**管理域请求成功返回方法**/	
		public function onGetAreaResult(e:HttpResponseEvent,data:String):void
		{
			if(e.response.code=="200"){
				DataHandleTool.showOnConsole("管理域请求成功");
				getter.webSocketAndGetAlarmData();//打开socket请求数据推送
				_areaXML = XML(data);
				getAllArea(_areaXML);
			}else{
				DataHandleTool.showOnConsole("管理域请求失败，错误代码："+e.toString());
				Alert.show("管理域请求失败，错误代码："+e.response.code)
				PopupManagerUtil.getInstence().closeLoading();
			}
		}
		/**管理域请求连接错误返回方法**/	
		var i:int=0
		private function onGetAreaFault(e:Event):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			if(i==0){
				DataHandleTool.showOnConsole("数据连接出错"+e.type+"在连接");
				PopupManagerUtil.getInstence().closeLoading();
				PopupManagerUtil.getInstence().popupLoading(page);
				var uri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains";
				sdnService.ipcoreRequest(uri, onGetAreaResult, onGetAreaFault);
				i++;
			}else{
				Alert.show("数据连接出错"+e.toString(),"提示");
			}
			
			
		}
		/**初始化管理域按钮**/	
		private function getAllArea(Areaxml:XML):void{
			if(SdncUtil.cuProjectType=="test"){
				for each(var xml:XML in Areaxml.domain)
				{
					var enterButton:EnterButton = new EnterButton();
					enterButton.id = xml.id
					enterButton.text = xml.name;
					enterButton.name = xml.type;
					enterButton._topoxml = xml.topo;
					enterButton.image = setEnterBtnImg(xml.type);
					enterButton.addEventListener(MouseEvent.CLICK,imgClickHandler);
					page.AreaGroup.addElement(enterButton);
				}
				
			}else{
				page.AreaGroup.removeAllElements();
				for each(var xml:XML in Areaxml.domain)
				{
					var enterButton:EnterButton = new EnterButton();
					enterButton.id = xml.id
					enterButton.text = xml.name
					enterButton.tooltip = xml.name
					enterButton.name = xml.type;
					enterButton._viewlabel = xml.type
					enterButton._topoxml = xml.topo;
					enterButton.image = setEnterBtnImg(xml.type);
					enterButton.addEventListener(MouseEvent.CLICK,imgClickHandler);
					page.AreaGroup.addElement(enterButton);
				}
				DataHandleTool.showOnConsole("初始化管理域按钮成功  管理域数量为="+page.AreaGroup.numChildren);
				PopupManagerUtil.getInstence().closeLoading();
			}
		}
		/**根据管理域呈现视图设置管理域按钮图标**/
		private function setEnterBtnImg(type:String):Class
		{
			switch(type)
			{
				case "Agile TE View":
					return __IPCoreImg;
			}
			return null;
		}
		/**管理域点击触发事件、若管理域已经进入视图 则跳转直该视图 否则进入该视图*/
		protected function imgClickHandler(event:MouseEvent):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			PopupManagerUtil.getInstence().popupLoading(__app);
			_cuarea = new Object;
			var allviewLength:int = __app.overte.topoview.numElements;//已经打开的管理域的数量
			var str:String = (event.currentTarget as EnterButton).name;//当前点击的 管理域
			for(var i:int;i<allviewLength;i++){
				//说明已经存在当前的管理域了
				if(__app.overte.topoview.getElementAt(i) is OverTEView
					&&(__app.overte.topoview.getChildAt(i) as OverTEView).ManAreasID == (event.currentTarget as EnterButton).id){
					__app.overte.topoview.selectedChild = __app.overte.topoview.getChildAt(i) as INavigatorContent;
					DataHandleTool.showOnConsole((event.currentTarget as EnterButton).text+"已经存在 进入视图");
					PopupManagerUtil.getInstence().closeLoading();
					flag=true;
				}
			}
			_cuarea["id"]=(event.currentTarget as EnterButton).id;
			_cuarea["name"]=(event.currentTarget as EnterButton).text;
			SdncUtil.cuArea=_cuarea;
			topoxml = (event.currentTarget as EnterButton)._topoxml
			switcharea(str,(event.currentTarget as EnterButton).text,(event.currentTarget as EnterButton).id)
			DataHandleTool.showOnConsole("进入管理域： "+_cuarea["name"]+"解决方案："+str);
		}
		/**
		 * 进入管理域视图
		 * @param SelectAreaName:当前选择管理域Name--当前选择页面VLLOverTEView
		 * @param SelectAreaText:当前选择管理域Text--当前选择管理域 管理域A
		 */
		public function switcharea(SelectAreaName:String,SelectAreaText:String,SelectId:String):void{
			PopupManagerUtil.getInstence().closeLoading();
			if(topoxml.toponodes.children().length()==0){
				Alert.show("管理域未绑定设备","提示");
				return;
			}
			if(flag==false){
				switch(SelectAreaName)
				{
					case "Agile TE View":
						var overteView:OverTEView = new OverTEView();
						Device.j = 1
						overteView.ManAreasID = SelectId;//管理域名称
						overteView.ManAreasName = SelectAreaText;//管理域名称
						overteView.TopoXML = topoxml;
						overteView.label = "Agile TE View";
						overteView.toolTip = SelectAreaText
						overteView.name = "overte";
						overteView.percentHeight = 100;
						overteView.percentWidth = 100;
						__app.overte.topoview.addElement(overteView);
						__app.overte.menutabbar.setClosePolicyForTab(__app.overte.menutabbar.numElements-1,SuperTab.CLOSE_SELECTED)
						__app.overte.topoview.selectedIndex = __app.overte.menutabbar.numElements-1;
						break;
				}
			}
			flag=false;
			
		}
	}
}