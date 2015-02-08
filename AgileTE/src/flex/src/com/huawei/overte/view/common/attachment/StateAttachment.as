package com.huawei.overte.view.common.attachment
{
	import com.huawei.overte.view.common.StateBar;
	import com.huawei.overte.view.node.StateNode;
	import com.huawei.overte.view.node.StateNode;
	
	import twaver.Consts;
	import twaver.networkx.ui.BasicAttachment;
	import twaver.networkx.ui.ElementUI;
	
	public class StateAttachment extends BasicAttachment
	{
		
		private var stateBar:StateBar = new StateBar();
		public function StateAttachment(elementUI:ElementUI, showInAttachmentCanvas:Boolean=false)
		{
			super(elementUI, showInAttachmentCanvas);
			this.content = stateBar;
			stateBar.visible = false;
			this.network.addChild(stateBar);
		}
		
		override public function updateProperties():void
		{
			super.updateProperties();
			var node:com.huawei.overte.view.node.StateNode = StateNode(this.element);
			stateBar.ramPercent = node.ramRate;
			stateBar.cpuPercent = node.cpuRate;
			this.network.callLater(function():void {
				network.invalidateElementUI(element);
			});
		}
		
		/*override public function get position():String
		{
			return Consts.POSITION_TOPLEFT;
		}*/
		
		
		override public function get direction():String
		{
			return Consts.ATTACHMENT_DIRECTION_BELOW_RIGHT;
		}
		
		override public function get xOffset():Number
		{
			return 5;
		}
		
		override public function get yOffset():Number
		{
			return 20;
		}
		
	}
}