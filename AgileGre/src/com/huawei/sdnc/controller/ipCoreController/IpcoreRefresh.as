package com.huawei.sdnc.controller.ipCoreController
{
	import com.huawei.sdnc.model.Device;
	import com.huawei.sdnc.service.SdnUIService;
	import com.huawei.sdnc.tools.PopupManagerUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.common.node.StateNode;
	import com.huawei.sdnc.view.ipCore_DCI.PhysicsView;
	import com.huawei.sdnc.view.ipCore_DCI.dataHandle.DataHandleTool;
	
	import flash.events.Event;
	import flash.geom.Point;
	import flash.system.Capabilities;
	import flash.utils.ByteArray;
	
	import mx.controls.Alert;
	import mx.events.FlexEvent;
	
	import org.httpclient.events.HttpResponseEvent;
	
	import twaver.Consts;
	import twaver.ElementBox;
	import twaver.IData;
	import twaver.Node;
	import twaver.Styles;
	import twaver.network.layout.AutoLayouter;
	import twaver.networkx.NetworkX;
	/**
	 * 此类为控制面板上刷新按钮
	 * */
	public class IpcoreRefresh
	{
		public function IpcoreRefresh()
		{
		}
		private var networkX:NetworkX;
		public var autoLayouter:AutoLayouter;
		private var projectType:String;
		private var sdnService:SdnUIService = new SdnUIService();
		private var nodeArray:Array=[];
		private var physicsView:PhysicsView;
		private var centerPoint:Point=new Point(Capabilities.screenResolutionX/2,Capabilities.screenResolutionY/3);
		public var greInitNum:int=0;
		/**
		 * 刷新先将原network上的东西全部清除，再重新获取
		 * */
		public function init():void{
			physicsView = SdncUtil.app.ipcore.physicsView;
			networkX = physicsView.networkX;
			networkX.elementBox.clear();
			getData();
		}
		public function getData():void{
			var uri:String="";
			if(projectType=="test"){
				uri="assets/xml/dci/test_tnl.xml";
				sdnService.ipcoreRequest(uri, TestProjectCtrl.onResultForTest, onRequestOpsDeafalt);
			}else{
				var opsIp:String=SdncUtil.opsIp;
				uri="http://"+opsIp+"/devices";
				sdnService.ipcoreRequest(uri, onResultForClient, onRequestOpsDeafalt);
				PopupManagerUtil.getInstence().popupLoading(physicsView,false);
			}
		}
		
		/**
		 * 从ops读取的设备信息转换成设备数组
		 */	
		public function onResultForClient(e:HttpResponseEvent, data1:ByteArray):void
		{
			var data:String=data1.toString();
			var devices:Array=DataHandleTool.handleDeviceData(data);
			if(devices == null || devices.length == 0){
				PopupManagerUtil.getInstence().closeLoading();
				return;
			}
			nodeArray=[];
			networkX.elementBox.clear();
			for(var i:int=0;i<devices.length;i++){
				var stateNode:Node=new StateNode;
				stateNode.setStyle(Styles.LABEL_POSITION,Consts.POSITION_BOTTOM_BOTTOM);
				stateNode.setStyle(Styles.LABEL_COLOR,0xffffff);
				nodeArray.push(stateNode);
				var obj:Object=devices[i] as Object;
				var username:String=obj["username"];
				var devicename:String=obj["devicename"];
				var passwd:String=obj["passwd"];
				var ip:String=obj["ip"];
				var version:String=obj["version"];
				var productType:String=obj["productType"];
				var id:String=obj["id"];
				
				stateNode.name=devicename;
				stateNode.setClient("username",username);
				stateNode.setClient("devicename",devicename);
				stateNode.setClient("passwd",passwd);
				stateNode.setClient("ip",ip);
				stateNode.setClient("version",version);
				stateNode.setClient("productType",productType);
				stateNode.setClient("id",id);
				stateNode.image="icon_core_ipcore";
				networkX.elementBox.add(stateNode);
			}
			var layout:LayoutTopo = new LayoutTopo;
			layout.handleLayout(networkX,function():void{
				//根据tnl创建连线
				var opsIp:String=SdncUtil.opsIp;
				networkX.elementBox.forEachByBreadthFirst(function(item:IData):void{
					if(item is StateNode){
						var sn:StateNode=item as StateNode;
						var device:Device=new Device;
						sn.setClient("device",device);
						device.stateNode=sn;
						device.initDevice(add);
					}
				});
				DataHandleTool.stateNodesArr=nodeArray;
			});
		}
		
		private function add():void
		{
			greInitNum++;
			trace("已经初始化的设备数：       "+greInitNum);
			if(greInitNum==DataHandleTool.devices.length){
				drawLink();
			}
		}
		
		/**
		 * 当初始化所有设备完成后 开始画线
		 */
		public function drawLink():void
		{
			greInitNum=0;
			DataHandleTool.createLinkByTnlForNormal(networkX);
			PopupManagerUtil.getInstence().closeLoading();
		}
		
		/**
		 * 当请求ops错误时触发的事件函数
		 * @param e
		 * 
		 */		
		private function onRequestOpsDeafalt(e:Event):void
		{
			PopupManagerUtil.getInstence().closeLoading();
			Alert.show("ops连接出错","提示");
		}
	}
}