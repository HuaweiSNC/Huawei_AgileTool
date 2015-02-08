package com.huawei.overte.view.link
{
	import flash.text.TextField;
	
	import com.huawei.overte.view.link.MyNewLink;
	
	import twaver.Consts;
	import twaver.Link;
	import twaver.Node;
	import twaver.network.ui.ElementUI;
	import twaver.network.ui.LabelAttachment;
	
	public class LinkRightLabel extends LabelAttachment
	{
		public function LinkRightLabel(elementUI:ElementUI, showInAttachmentCanvas:Boolean=false, K3721K:Boolean=false)
		{
			super(elementUI, showInAttachmentCanvas, K3721K);
		}
		
		override public function updateProperties():void {
			super.updateProperties();
			var textField:TextField = content as TextField;
			textField.textColor = 0xffffff
			if (textField) {
				var link:Link = Link(this.element);
				var from:Node = link.fromNode;
				if (from) {
					if(null ==(link as MyNewLink)._fromName){
						textField.text = "";
					}else{
						textField.text =  (link as MyNewLink)._fromName;
					}
					
				}
			}
		}
		
		override public function get position():String {
			return Consts.POSITION_FROM;
		}
		
		override public function get xOffset():Number {
			var link:Link = Link(this.element);
			var from:Node = link.fromNode;
			if (from) {
				return from.width*1.5
			}
			return 0;
		}
	}
}