package com.huawei.sdnc.view.common.node
{
	import com.huawei.sdnc.event.SdncEvt;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.common.nodeui.RefNodeUI;
	
	import flash.events.Event;
	
	import twaver.Consts;
	import twaver.Follower;
	import twaver.Styles;

	public class VmFollower extends Follower
	{
		private var __nodeInfo:*;
		private var __hostName:String;
		public function VmFollower(id:Object=null)
		{
			super(id);
			image = "topo_VM";
			this.setStyle(Styles.LABEL_COLOR,"0xffffff");
//			SdncUtil.app.addEventListener(SdncEvt.CHANGE_VM_LABEL_TYPE,labelChange);
//			this.setStyle(Styles.CONTENT_TYPE, Consts.CONTENT_TYPE_VECTOR);
//			this.setStyle(Styles.VECTOR_SHAPE, Consts.SHAPE_CIRCLE);
//			this.setStyle(Styles.VECTOR_GRADIENT, Consts.GRADIENT_RADIAL_SOUTHWEST);				
//			this.setStyle(Styles.VECTOR_FILL_COLOR, 0x00FF00);
		}
		
		public function labelChange(type:String):void
		{
			if(!__nodeInfo) return;
			switch(type)
			{
				case "vm_netinfo":
					this.name = __nodeInfo.vm_netinfo
					break;
				case "name":
					this.name = __nodeInfo.name;
					break;
				case "hostname":
					this.name = "Host:" + __hostName;
					break;
				case "id":
					this.name = __nodeInfo.id;
					break;
				default:
					this.name = __nodeInfo.name;
					break;
			}
		}
		
		public function get nodeInfo():*
		{
			return __nodeInfo;
		}

		public function set nodeInfo(value:*):void
		{
			__nodeInfo = value;
		}

		public function get hostName():String
		{
			return __hostName;
		}

		public function set hostName(value:String):void
		{
			__hostName = value;
		}


	}
}