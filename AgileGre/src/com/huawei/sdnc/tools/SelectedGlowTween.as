package com.huawei.sdnc.tools
{
	import flash.display.InteractiveObject;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.filters.GlowFilter;
	
	public class SelectedGlowTween
	{
		private var _target:InteractiveObject;
		private var _color:uint;
		private var _toggle:Boolean;
		private var _blur:Number;
		
		public function SelectedGlowTween()
		{
		}
		
		public function remove():void
		{
			_target.removeEventListener(Event.ENTER_FRAME, blinkHandler);
			_target.filters=null;
			_target=null;
		}
		
		public function startGlowHandler(target:InteractiveObject=null, color:uint=0xFFFFFF):void
		{
			_target=target;
			_color=color;
			_toggle=true;
			_blur=2;
			
			_target.addEventListener(Event.ENTER_FRAME, blinkHandler, false, 0, true);
		}
		
		public function stopGlowHandler():void
		{
			if(_target != null){
				_target.removeEventListener(Event.ENTER_FRAME, blinkHandler);
				remove();
			}
			
		}
		
		private function blinkHandler(evt:Event):void
		{
//			if (_blur >= 20)
//				_toggle=false;
//			else if (_blur <= 10)
//				_toggle=true;
//			_toggle ? _blur++ : _blur--;
//			var glow:GlowFilter=new GlowFilter(_color, 1, _blur, _blur, 2, 2);
			
			var glow:GlowFilter=new GlowFilter(0x0099ff, 1, 6, 6, 2, 2);
			_target.filters=[glow];
		}
		
	}
	
	
}
