 package com.huawei.sdnc.view.common
{
	import flash.events.Event;
	import flash.events.MouseEvent;
	
	import mx.containers.Panel;
	import mx.controls.Alert;
	import mx.controls.Button;
	import mx.events.FlexEvent;
	
	
	[Event(name="min", type="mx.events.FlexEvent")]
	[Event(name="max", type="mx.events.FlexEvent")]	
	[Event(name="close", type="mx.events.FlexEvent")]
 
	public class MyPanel extends Panel
	{	
		[Embed(source="/assets/baidu.png")]
		public static const titleIco:Class;
		[Embed(source="/assets/close_over.gif")]
		public static const closeOverIco:Class;
		[Embed(source="/assets/close.gif")]
		public static const closeIco:Class;
		[Embed(source="/assets/max.gif")]
		public static const maxIco:Class;
		[Embed(source="/assets/max_over.gif")]
		public static const maxOverIco:Class;
		[Embed(source="/assets/max1.gif")]
		public static const max1Ico:Class;
		[Embed(source="/assets/max1_over.gif")]
		public static const max1OverIco:Class;
		[Embed(source="/assets/min.gif")]
		public static const minIco:Class;
		[Embed(source="/assets/min_over.gif")]
		public static const minOverIco:Class;
		[Embed(source="/assets/min1.gif")]
		public static const min1Ico:Class;
		[Embed(source="/assets/min1_over.gif")]
		public static const min1OverIco:Class;
		
		private var _showBtn:Boolean = true;
		private var isMax:Boolean=false;
		private var isMin:Boolean=false;
		private var minBtn:Button;
		private var maxBtn:Button;
		private var closeBtn:Button;
		
		private var originalWidth:Number;
		private var originalHeight:Number;
		private var originalX:Number;
		private var originalY:Number;
		
		private var _x:Number;
		private var _y:Number;
		private var _width:Number;
		private var _height:Number;
		public function MyPanel(){
			doubleClickEnabled = true;
		}
		
		override protected function createChildren():void{
			super.createChildren();
			
			addEventListener(MouseEvent.CLICK,test);
			this.titleBar.addEventListener(MouseEvent.MOUSE_DOWN,_startDrag);
			this.titleBar.addEventListener(MouseEvent.CLICK,onTop);
			
			this.titleBar.addEventListener(MouseEvent.DOUBLE_CLICK,doubleTop);
			
			this.addEventListener(MouseEvent.MOUSE_UP,_stopDrag);
			if(_showBtn){
				this.addEventListener(FlexEvent.CREATION_COMPLETE, addButton);
				this.addEventListener("min", min);
				this.addEventListener("max", max);
				this.addEventListener("close",close);
			}
		}
		private function test(e:MouseEvent):void{
//			parent.setChildIndex(this,parent.numChildren-1);
		}
		
		private function doubleTop(e:Event):void{
			dispatchEvent(new FlexEvent("max"));
		}
		
		//设置该panel显示最前
		private function onTop(e:Event):void{
//			parent.setChildIndex(this,parent.numChildren-1);
		}
		//panel拖动
		private function _startDrag(e:Event):void{
//			this.startDrag();
			
		}
		private function _stopDrag(e:Event):void{
//			this.stopDrag();
		}
		//向panel中加按钮
		public function set showBtn(show:Boolean):void{
			_showBtn = show;
			if(titleBar != null){
				addButton(new FlexEvent(""));
			}
		}
		
		public function get showBtn():Boolean{
			return _showBtn;
		}
		public function addButton(event:FlexEvent):void{
			//panel的初始状态
			originalX = this.x;
			originalY = this.y;
			originalWidth = this.width;
			originalHeight = this.height;
			
//			titleIcon = titleIco;
			if(_showBtn){
				if(minBtn == null){
					minBtn = new Button();
					minBtn.width=26;
					minBtn.height=26;
					minBtn.toolTip="最小化";
					minBtn.focusEnabled=false;
					//设置button的图片
					minBtn.setStyle("upSkin", minIco);
					minBtn.setStyle("overSkin", minOverIco);
					minBtn.setStyle("downSkin", minOverIco);
					minBtn.addEventListener(MouseEvent.CLICK, function(e:Event):void{
						dispatchEvent(new FlexEvent("min"));
					});
					titleBar.addChild(minBtn);
					//当鼠标放在minBtn上时取消对titleBar的监听
					minBtn.addEventListener(MouseEvent.MOUSE_OVER,function(e:Event):void{
						titleBar.removeEventListener(MouseEvent.MOUSE_DOWN,_startDrag);
						titleBar.removeEventListener(MouseEvent.MOUSE_UP,_stopDrag);
					});
				
					minBtn.addEventListener(MouseEvent.MOUSE_OUT,function(e:Event):void{
						if(isMax!=true)
						{	
						titleBar.addEventListener(MouseEvent.MOUSE_DOWN,_startDrag);
						titleBar.addEventListener(MouseEvent.MOUSE_UP,_stopDrag);
						}
					});
	
				}
				if(maxBtn == null){
					maxBtn = new Button();
					maxBtn.width=26;
					maxBtn.height=26;
					maxBtn.toolTip="最大化";
					maxBtn.focusEnabled=false;
					maxBtn.setStyle("upSkin", maxIco);
					maxBtn.setStyle("overSkin", maxOverIco);
					maxBtn.setStyle("downSkin", maxOverIco);
					maxBtn.addEventListener(MouseEvent.CLICK,function(e:Event):void{
						dispatchEvent(new FlexEvent("max"));
					});
					titleBar.addChild(maxBtn);
					//当鼠标放在maxBtn上时取消对titleBar的监听
					maxBtn.addEventListener(MouseEvent.MOUSE_OVER,function(e:Event):void{
						titleBar.removeEventListener(MouseEvent.MOUSE_DOWN,_startDrag);
						titleBar.removeEventListener(MouseEvent.MOUSE_UP,_stopDrag);
					});
					
					maxBtn.addEventListener(MouseEvent.MOUSE_OUT,function(e:Event):void{
						if(isMax!=true)
						{
						titleBar.addEventListener(MouseEvent.MOUSE_DOWN,_startDrag);
						titleBar.addEventListener(MouseEvent.MOUSE_UP,_stopDrag);
						}
					});
					
				}
				if(closeBtn == null){
					closeBtn = new Button();
					closeBtn.width=26;
					closeBtn.height=26;
					closeBtn.toolTip="关闭";
					closeBtn.focusEnabled=false;
					closeBtn.setStyle("upSkin", closeIco);
					closeBtn.setStyle("overSkin", closeOverIco);
					closeBtn.setStyle("downSkin", closeOverIco);
					closeBtn.addEventListener(MouseEvent.CLICK,function(e:Event):void{
						dispatchEvent(new FlexEvent("close"));
					});
					titleBar.addChild(closeBtn);
					//当鼠标放在closeBtn上时取消对titleBar的监听
					closeBtn.addEventListener(MouseEvent.MOUSE_OVER,function(e:Event):void{
						titleBar.removeEventListener(MouseEvent.MOUSE_DOWN,_startDrag);
						titleBar.removeEventListener(MouseEvent.MOUSE_UP,_stopDrag);
					});
					
					closeBtn.addEventListener(MouseEvent.MOUSE_OUT,function(e:Event):void{
						if(isMax!=true)
						{
						titleBar.addEventListener(MouseEvent.MOUSE_DOWN,_startDrag);
						titleBar.addEventListener(MouseEvent.MOUSE_UP,_stopDrag);
						}
					});
					
				}
				layoutBtn();
			}else{
				titleBar.removeChild(minBtn);
				minBtn = null;
				titleBar.removeChild(maxBtn);
				maxBtn = null;
				titleBar.removeChild(closeBtn);
				closeBtn = null;
			}
		}
		private function min(event:FlexEvent):void{
			event.stopPropagation();
			
			if(isMin==false){
//				_x=this.x;
//				_y=this.y;
//				_width=this.width;
//				_height=this.height;
//				_x=originalX;
//				_y=originalY;
//				_width=originalWidth;
//				_height=originalHeight;
				
				this.width=200;
				this.height=titleBar.height;
				this.toolTip="恢复";
				this.move(0,parent.height-titleBar.height);
				isMin=true;
				titleBar.width=200;
				layoutBtn();
				minBtn.setStyle("upSkin", min1Ico);
				minBtn.setStyle("overSkin", min1OverIco);
				minBtn.setStyle("downSkin", min1OverIco);
				maxBtn.setStyle("upSkin", maxIco);
				maxBtn.setStyle("overSkin", maxOverIco);
				maxBtn.setStyle("downSkin", maxOverIco);
				isMax=false;
				minBtn.toolTip = "还原";
				maxBtn.toolTip = "最大化";
			}else{
//				this.width=_width;
//				this.height=_height;
//				titleBar.width=_width;
//				this.move(_x,_y);
				this.width=originalWidth;
				this.height=originalHeight;
				titleBar.width=originalWidth;
				this.move(originalX,originalY);
				isMin=false;
				this.titleBar.addEventListener(MouseEvent.MOUSE_DOWN,_startDrag);
				this.addEventListener(MouseEvent.MOUSE_UP,_stopDrag);
				layoutBtn();
				minBtn.setStyle("upSkin", minIco);
				minBtn.setStyle("overSkin", minOverIco);
				minBtn.setStyle("downSkin", minOverIco);
				minBtn.toolTip = "最小化";
			}
			
		}
		private function max(event:FlexEvent):void{
			event.stopPropagation();
			if(isMax==false){
//				_x=this.x;
//				_y=this.y;
//				_width=this.width;
//				_height=this.height;
				this.width=parent.width;
				this.height=parent.height;
				titleBar.width=parent.width;
				this.move(0,0);
				isMax=true;
				minBtn.setStyle("upSkin", minIco);
				minBtn.setStyle("overSkin", minOverIco);
				minBtn.setStyle("downSkin", minOverIco);
				isMin = false;
				maxBtn.setStyle("toolTip","恢复");
				maxBtn.setStyle("upSkin", max1Ico);
				maxBtn.setStyle("overSkin", max1OverIco);
				maxBtn.setStyle("downSkin", max1OverIco);
				this.titleBar.removeEventListener(MouseEvent.MOUSE_DOWN,_startDrag);
				this.removeEventListener(MouseEvent.MOUSE_UP,_stopDrag);
				layoutBtn();
				maxBtn.toolTip = "还原";
				minBtn.toolTip = "最小化";
			}else{
				this.width=originalWidth;
				this.height=originalHeight;
				titleBar.width=originalWidth;
				this.move(originalX,originalY);
				isMax=false;
				this.titleBar.addEventListener(MouseEvent.MOUSE_DOWN,_startDrag);
				this.addEventListener(MouseEvent.MOUSE_UP,_stopDrag);
				layoutBtn();
				maxBtn.setStyle("upSkin", maxIco);
				maxBtn.setStyle("overSkin", maxOverIco);
				maxBtn.setStyle("downSkin", maxOverIco);
				maxBtn.toolTip = "最大化";
			}	
		}
		private function close(event:FlexEvent):void{
			event.stopPropagation();
			this.titleBar.removeEventListener(MouseEvent.CLICK,onTop);
//			parent.removeElement(this);
		}
		private function layoutBtn():void{
			if(closeBtn!=null){
				closeBtn.move(titleBar.width-29,titleBar.height-26);
			}
			if(maxBtn!=null){
				maxBtn.move(titleBar.width-53,titleBar.height-26);
			}
			if(minBtn != null){
				minBtn.move(titleBar.width - 78, titleBar.height-26);
			}
		}
	}
}