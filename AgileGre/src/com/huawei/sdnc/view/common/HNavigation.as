package com.huawei.sdnc.view.common
{
	import com.huawei.sdnc.view.skins.HNavigationSkin;
	import com.huawei.sdnc.view.skins.NavigationSkin;
	
	import flash.events.MouseEvent;
	
	import mx.controls.Spacer;
	
	import spark.components.Button;
	
	public class HNavigation extends Spacer
	{
		
		/**数据源*/
		private var _array:Array;
		
		public function get array():Array
		{
			return _array;
		}

		public function set array(value:Array):void
		{
			_array = value;	
			updateArray();
		}

		public function HNavigation()
		{
		
		}
		
		/**上一个按钮大长度*/
		private var indexWidth:Number = 0;
		
		/**修改数据源时触发页面重绘*/
		private function updateArray():void
		{
			if(array != null && array.length>0)
			{
				var i:int = 0;
				indexWidth = 0;
				for(i;i<array.length;i++)
				{
					var button:Button;
					if(this.numChildren > i)
					{
						button = this.getChildByName(i.toString()) as Button;
					}else
					{
						button = new Button();
						if(i == 0)
							button.setStyle("skinClass",NavigationSkin);
						else
						{
							button.setStyle("skinClass",HNavigationSkin);	
							button.buttonMode = true;
							button.addEventListener(MouseEvent.CLICK,button_clickHandler);
						}
						button.name = i.toString();
						button.height = 22;
						this.addChild(button);
					}
					if(array[i] != button.label)
					{
						button.label = array[i];
						button.width = measureText(array[i]).width + 24;
					}
					button.x = indexWidth;
					indexWidth += button.width - 7;
				}
				
				if(this.numChildren > array.length)
				{
					for(i;i<this.numChildren;i++)
					{
						this.removeChildAt(i);
					}
				}
			}
		}
		
		private function button_clickHandler(e:MouseEvent):void
		{
			var btn:Button = e.target as Button;
			for(var i:int=this.numChildren-1;i>=0;i--)
			{
				if(int(btn.name) < i)
				{
					this.removeChildAt(i);
					array.pop();
				}
			}
		}
	}
}