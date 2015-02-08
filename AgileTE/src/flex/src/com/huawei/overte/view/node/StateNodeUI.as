package com.huawei.overte.view.node
{
	import com.huawei.overte.tools.Images;
	import com.huawei.overte.view.common.attachment.StateAttachment;
	//import com.huawei.overte.view.node.RefNodeUI;
	import com.huawei.overte.view.node.StateNode;
	
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.GradientType;
	import flash.display.Sprite;
	import flash.geom.Matrix;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	
	import mx.containers.VBox;
	import mx.controls.Image;
	import mx.core.UIComponent;
	
	import twaver.Consts;
	import twaver.IImageAsset;
	import twaver.Node;
	import twaver.Styles;
	import twaver.Utils;
	import twaver.networkx.IGraphics;
	import twaver.networkx.NetworkX;
	import twaver.network.Network;
	import twaver.networkx.ui.NodeUI;
	import twaver.networkx.IGraphics;
	import twaver.networkx.NetworkX;
	import twaver.network.Network;
	import twaver.networkx.ui.NodeUI;
	
	public class StateNodeUI extends RefNodeUI
	{
		public function StateNodeUI(network:NetworkX, node:StateNode)
		{
			super(network, node);
		}
		
		override public function checkAttachments():void
		{
			super.checkAttachments();
			checkStateAttachment();
		}
		
		private var __stateAttachment:StateAttachment;
		
		protected function checkStateAttachment():void
		{
			if(this.node.getClient("state"))
			{
				if(this.__stateAttachment == null)
				{
					this.__stateAttachment = new StateAttachment(this);
					this.addAttachment(this.__stateAttachment);
				}
//				(this.node as StateNode).timer.start();
				
			} else
			{
				if(this.__stateAttachment != null)
				{
					this.removeAttachment(this.__stateAttachment);
					this.__stateAttachment = null;
				}
				/*(this.node as StateNode).timer.reset();
				(this.node as StateNode).timer.delay = 500;*/
			}
		}
	}
}