package com.huawei.overte.handle
{
	import com.huawei.overte.handle.DataHandleTool;
	import com.huawei.overte.model.Device;
	import com.huawei.overte.service.SdnUIService;
	import com.huawei.overte.tools.ConnUtil;
	import com.huawei.overte.tools.PopupManagerUtil;
	import com.huawei.overte.tools.SdncUtil;
	import com.huawei.overte.view.node.StateNode;
	import com.huawei.overte.view.overte.OverTEView;
	
	import flash.events.Event;
	import flash.geom.Point;
	import flash.system.Capabilities;
	import flash.utils.ByteArray;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.events.FlexEvent;
	
	import org.httpclient.events.HttpResponseEvent;
	
	import twaver.Consts;
	import twaver.ElementBox;
	import twaver.IData;
	import twaver.Node;
	import twaver.Styles;
	import twaver.network.Network;
	import twaver.network.layout.AutoLayouter;
	import twaver.networkx.NetworkX;

	/**
	 * 此类为控制面板上自动发现链路
	 *  * */
	public class AutoFindLink
	{
		public function AutoFindLink()
		{
		}
		/**Twaver network Topo组件**/
		private var network:Network;
		/**Topo组件布局对象**/
		public var autoLayouter:AutoLayouter;
		/**当前项目类型**/
		private var projectType:String;
		private var sdnService:SdnUIService = new SdnUIService();
		/**Topo组件父容器**/
		private var overteView:OverTEView;
		/**当前进入管理域ID**/
		private var curareaId:String;
		/**数据请求opsIp**/
		private var opsIp:String=SdncUtil.opsIp;
		/**数据请求项目名称**/
		private var webname:String = SdncUtil.projectname;
		
		/**刷新先将原network上的东西全部清除，再重新获取**/
		public function init():void{
			overteView = (SdncUtil.app.overte.topoview.selectedChild as OverTEView)
			curareaId= (SdncUtil.app.overte.topoview.selectedChild as OverTEView).ManAreasID;
			network = overteView.networkX;
			getospfData();
		}
		public function getospfData():void{
			if(projectType=="test"){
				
			}else{
				PopupManagerUtil.getInstence().closeLoading();
				PopupManagerUtil.getInstence().popupLoading(overteView);
				var uri:String=ConnUtil.protocolHeader+opsIp+"/"+webname+"/agilete/domains/"+curareaId+"/links?name=ospf";
				sdnService.ipcoreRequest(uri, onGetLinkResult, onGetLinkFault);//查询管理域
				DataHandleTool.showOnConsole("发送自动获取链路信息请求");
			}
		}
		
		/**获取管理域下Topo信息成功方法**/	
		public function onGetLinkResult(e:HttpResponseEvent, data:String):void
		{
			if(e.response.code=="200"){
				var linkxml:XML = new XML(data)
				var linkListXml:XMLList = linkxml.topoLink
				if(linkListXml!=null){
					var links:Array=[]
					DataHandleTool.links = links;
					for(var i:int=0;i<linkListXml.length();i++)
					{
						var linkobj:Object=new Object;
						linkobj["linkname"]=linkListXml[i].name.toString();
						linkobj["fromDeviceID"]=linkListXml[i].headNodeConnector.toponode.nodeID.toString();
						linkobj["fromDevice"]=linkListXml[i].headNodeConnector.toponode.nodeType.toString();
						linkobj["frominterface"]=linkListXml[i].headNodeConnector.Connectorid.toString();
						linkobj["frominterfaceIP"]=linkListXml[i].headNodeConnector.Connectorip.toString();
						
						linkobj["toDeviceID"]=linkListXml[i].tailNodeConnector.toponode.nodeID.toString();
						linkobj["toDevice"]=linkListXml[i].tailNodeConnector.toponode.nodeType.toString();
						linkobj["tointerface"]=linkListXml[i].tailNodeConnector.Connectorid.toString();
						linkobj["tointerfaceIP"]=linkListXml[i].tailNodeConnector.Connectorip.toString();
						links.push(linkobj);
					}	
					DataHandleTool.createLinkByTnlForReal(true,network);
				}else{
					DataHandleTool.showOnConsole("自动获取链路失败 错误代码：  "+e.response.code);
				}
			}
		}
		/**获取管理域下Topo信息出错方法**/	
		private function onGetLinkFault(e:Event):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			DataHandleTool.showOnConsole("自动获取链路信息出错\n"+e.toString());
			Alert.show("自动获取链路信息出错","提示");
		}		
	}
}