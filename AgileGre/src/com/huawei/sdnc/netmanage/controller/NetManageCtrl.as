package com.huawei.sdnc.netmanage.controller
{
	import com.huawei.sdnc.netmanage.ToolUtil;
	import com.huawei.sdnc.netmanage.model.NetNode;
	import com.huawei.sdnc.netmanage.model.datas;
	import com.huawei.sdnc.tools.ConnUtil;
	import com.huawei.sdnc.tools.ErrorCodeUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	
	import flash.geom.Point;
	import flash.system.Capabilities;
	import flash.utils.ByteArray;
	
	import mx.collections.ArrayCollection;
	import mx.collections.HierarchicalData;
	import mx.controls.Alert;
	import mx.controls.Tree;
	import mx.core.UIComponent;
	import mx.events.CloseEvent;
	import mx.events.FlexEvent;
	import mx.events.ListEvent;
	import mx.managers.PopUpManager;
	
	import org.httpclient.events.HttpResponseEvent;
	
	import twaver.IData;
	import twaver.Node;
	import twaver.networkx.NetworkX;

	public class NetManageCtrl
	{
		public function NetManageCtrl()
		{
		}
		public var __app:sdncui2;
		private var networkX:NetworkX;
		public var opsIp:String = SdncUtil.opsIp;
		public var tree:XMLList = new XMLList
			public var alid:String = ""
		public function init():void
		{
			__app=SdncUtil.app;
			networkX=__app.ipcore.netManageView.networkX;
			var export:ExportTopo=new ExportTopo;
			export.exportTopo(__app.ipcore.physicsView.networkX,__app.ipcore.netManageView.networkX);
			var newmenu:RightMenu = new RightMenu;
			newmenu.newrightmenu(networkX);
			
			
			//var opsIp:String = SdncUtil.opsIp;
			var url:String = "http://"+opsIp+"/arithmetics/allArithmetics.xml";
			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_GET,getAlgResult,getAlgFault);
		}
		private function getAlgResult(e:HttpResponseEvent,data:ByteArray):void
		{
			var errorcode:ErrorCodeUtil = new ErrorCodeUtil;
			if(!errorcode.parse(e,data))
			{
				trace("获取topo所有算法失败");
				return;
			}
			tree = XML(data.toString()).children()
			var itree:XMLList = new XMLList;
			itree = tree.copy();
			var url:String
			alid = XML(data.toString()).@defaults[0]
			for(var i:int = 0;i<tree.children().length();i++)
			{
				if(tree.children()[i].@id[0]==XML(data.toString()).@defaults[0])
				{
					mydata.AlgNow = tree.children()[i].name.children()[0];
					url= "http://"+opsIp+"/arithmetics/"+tree.children()[i].filename.children()[0];
					break
				}
			}
			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_GET,getOneAlgReslut,getAlgFault);

		}
		
		private function getAlgFault():void
		{
			Alert.show("连接失败！","提示")
		}
		[Bindable]
		public var mydata:datas = datas.getInstence();
		private function getOneAlgReslut(e:HttpResponseEvent,data:ByteArray):void
		{
			// TODO Auto Generated method stub
			mydata.AlgoritmXML = XML(data.toString())
			//PopUpManager.removePopUp(this);
			//mydata.AlgNow = tree[0].children()[0].name[0].children()[0];
			mydata.AlNowID = alid;
			//Alert.show(mydata.AlgNow)
		}
	}
}