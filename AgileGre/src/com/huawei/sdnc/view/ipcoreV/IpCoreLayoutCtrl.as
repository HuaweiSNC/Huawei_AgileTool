// ActionScript file
package com.huawei.sdnc.view.ipcoreV
{
	import com.huawei.sdnc.event.SdncEvt;
	import com.huawei.sdnc.tools.ConnUtil;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.tools.TopoUtil;
	import com.huawei.sdnc.view.common.node.ServerNode;
	import com.huawei.sdnc.view.common.node.StateNode;
	
	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;
	import flash.events.MouseEvent;
	import flash.geom.Point;
	import flash.utils.Dictionary;
	
	import mx.events.FlexEvent;
	import mx.rpc.events.ResultEvent;
	
	public class IpCoreLayoutCtrl
	{
		[Bindable]
		public var page:IPCoreLayout;
		private var _ipcoretoponodeXml:XML;
		private var _ipcoretopolinkXml:XML;
		private var _ipcoretopoXml:XML;
		
		public function IpCoreLayoutCtrl()
		{
			return;
		}
		
		/**初始化组件和box */
		public function onInit():void
		{
			ConnUtil.getInstence().query("http://10.107.51.25:9090/alto/nodes.xml", onResultIpcoretoponode, onFault);
			ConnUtil.getInstence().query("http://10.107.51.25:9090/alto/edges.xml", onResultIpcoretopolink, onFault);
		}
		
		private function onResultIpcoretoponode(e:ResultEvent):void
		{
			_ipcoretoponodeXml = e.result as XML;
		}
		
		private function onResultIpcoretopolink(e:ResultEvent):void
		{
			_ipcoretopolinkXml =  e.result as XML;
			page.refreshAreaBox(_ipcoretoponodeXml, _ipcoretopolinkXml)
		}
		
		private function onFault(e:Event):void
		{
			trace(e.currentTarget.url,"请求失败!");
		}
		
		/**处理TOPO刷新*/
		private function onRefreshTopo(evt:IpCoreSdncEvt):void
		{
			var cuState:String = SdncUtil.app.main.currentState;
			if(cuState != "ipcore_view") return;	
			
		}
	}
}