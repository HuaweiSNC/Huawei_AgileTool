package com.huawei.overte.view.link 
{
	
	import twaver.Link;
	import twaver.Node;
	import twaver.Styles;
	import twaver.network.Network;
	import twaver.network.ui.LinkUI;
	
	public class MyNewLinkUI extends LinkUI
	{
		public function MyNewLinkUI(network:Network, link:Link)
		{
			super(network, link);
		}
		override public function checkAttachments():void {
			super.checkAttachments();
			checkChartAttachment();
		}
		private var fromAttachment:LinkLeftLabel;
		private var toAttachment:LinkRightLabel;
		
		protected function checkChartAttachment():void {
			if (fromAttachment == null) {
				fromAttachment = new LinkLeftLabel(this);
				this.addAttachment(fromAttachment);
			}
			if (toAttachment == null) {
				toAttachment = new LinkRightLabel(this);
				this.addAttachment(toAttachment);
			}
		}
	}
}