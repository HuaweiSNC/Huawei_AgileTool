package com.huawei.overte.event
{
	import flash.events.Event;

	public class SdncEvt extends Event
	{
		
		//public static const REGISTER_USER:String ="register_user";
		
		/**全屏事件*/
		public static const FULL_SCREEN_EVENT:String ="Full_Screen";
		/**打开帮助*/
		public static const OPEN_HELP:String ="open_help";
		
		/**打开管理域管理*/
		public static const OPEN_MANAGERAREA:String ="OPEN_managerarea";
		
		/**关闭管理域管理*/
		public static const CLOSE_MANAGERAREA:String ="CLOSE_managerarea";
		
		/**打开定义流*/
		public static const OPEN_MANAGERFLOW:String ="OPEN_managerflow";
		
		/**关闭定义流*/
		public static const CLOSE_MANAGERFLOW:String ="CLOSE_managerflow";
		
		
		/**打开定义管道*/
		public static const OPEN_MANAGERTUNEL:String ="OPEN_managerTunnel";
		
		/**关闭定义管道*/
		public static const CLOSE_MANAGERTUNEL:String ="CLOSE_managerTunnel";
		
		/**新建节点*/
		public static const NEW_NODE:String ="new_node";
		
		/**新建Link*/
		public static const NEW_LINK:String ="new_link";
		
		/**保存Link*/
		public static const SAVE_LINK:String ="save_link";
		
		/**保存Link*/
		public static const SAVE_NODE:String ="save_node";
		
		/**点击Panel*/
		public static const CLICK_ADG_PANCEL:String ="click_adg_panel";
		
		/**点击Panel 流事件*/
		public static const CLICK_ADGFLOW_PANCEL:String ="click_adgflow_panel";
		
		/**开启Panel*/
		public static const OPEN_TUNNEL_EVENT:String ="open_tunnel";
		
		/**关闭Panel*/
		public static const CLOSE_TUNNEL_EVENT:String ="close_tunnel";
		
		
		/**打开定义Tunnel事件*/
		public static const OPEN_TUNNEL_WINDOWS:String ="open_tunnel_windows";
		
		/**关闭定义Tunnel事件*/
		public static const CLOSE_TUNNEL_WINDOWS:String ="close_tunnel_windows";
		
		/**打开定义Flow事件*/
		public static const OPEN_FLOW_WINDOWS:String ="open_flow_windows";
		
		/**关闭定义Flow事件*/
		public static const CLOSE_FLOW_WINDOWS:String ="close_flow_windows";
		
		/**打开定义vlanmpaping事件*/
		public static const OPEN_VLANMAPPING_WINDOWS:String ="open_vlanmapping_windows";
		
		/**关闭定义vlanmpaping事件*/
		public static const CLOSE_VLANMAPPING_WINDOWS:String ="close_vlanmapping_windows";
		
		public static const SAVE_FLOW_EVENT:String ="save_flow_event";
		
		
		/**保存路径事件*/
		public static const SAVE_PATH_EVENT:String ="savepath";
		
		/**保存管道事件*/
		public static const SAVE_TUNNEL_EVENT:String ="savetunnel";
		
		/**保存BFD*/
		public static const SAVE_BFD_EVENT:String ="savebfd";
		
		/**打开系统还原列表窗口*/
		public static const OPEN_SYSTEM_ROLLBACK_WINDOW:String = "open_system_rollback_window";
		/**关闭系统还原列表窗口*/
		public static const CLOSE_SYSTEM_ROLLBACK_WINDOW:String = "close_system_rollback_window";
		
		public var params:*;
		public function SdncEvt(type:String, params:*=null, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			this.params = params;
		}
	}
}