package com.huawei.sdnc.view.common.AdvancedDataGrid.Renderer.Item
{
	import flash.display.Graphics;
	import mx.core.UIComponent;
	import mx.controls.advancedDataGridClasses.AdvancedDataGridItemRenderer;

	public class AdvancedDataGridItemRenderer extends mx.controls.advancedDataGridClasses.AdvancedDataGridItemRenderer
	{
		public function AdvancedDataGridItemRenderer()
		{
			super();
		}
		
		private var _settedHeight:Number=-1;
		private var _settedWidth:Number=-1;
		private var _settedY:Number=-1;
		
		private function resetVerticalPosition():void{
			super.height=this.textHeight;
			super.y=(_settedHeight-this.textHeight)/2+_settedY;
			
			return;
			bg.lineStyle(1,0xFF0000,0.5);
			bg.beginFill(0xCCCCCC,0.5);
			bg.drawRect(this.x, this.y, this.width, this.height-2);
		}
		
		private function get bg():Graphics{
			return UIComponent(parent).graphics;
		}
		
		override public function get y():Number{
			return _settedY;
		}
		
		override public function set y(value:Number):void{
			_settedY=value;
			resetVerticalPosition();
		}
		
		override public function set height(value:Number):void{
			_settedHeight=value;
			resetVerticalPosition();
		}
		
		override public function set width(value:Number):void{
			_settedWidth=value;
			super.width=value;
		}
		
		override public function get height():Number{
			return _settedHeight;
		}
		
		override public function get width():Number{
			return _settedWidth;
		}
	}
}