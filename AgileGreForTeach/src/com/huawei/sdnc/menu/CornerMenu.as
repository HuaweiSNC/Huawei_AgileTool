package com.huawei.sdnc.menu
{
	import com.greensock.TweenLite;
	import com.greensock.TweenMax;
	import com.huawei.sdnc.controller.ipCoreController.IpcoreRefresh;
	import com.huawei.sdnc.tools.SdncUtil;
	
	import flash.events.MouseEvent;
	import flash.geom.Point;
	
	import spark.components.Group;
	import spark.components.Image;
	import spark.effects.Rotate;
	
	public class CornerMenu extends Group
	{
		private var __mainBtn:Image;
		private var __optimizeBtn:Image;
		private var __homeBtn:Image;
		private var __backBtn:Image;
		private var __isExtended:Boolean;
		private var __optionBtnPoints:Array;
		[Embed(source="assets/imgs/menu/menubutton_open.png")]
		private var __mainBtnOpenImg:Class;
		[Embed(source="assets/imgs/menu/menubutton_close.png")]
		private var __mainBtnCloseImg:Class;
		[Embed(source="assets/imgs/menu/menu_reCaculatebutton.png")]
		private var __optimizeBtnImg:Class;
		[Embed(source="assets/imgs/menu/menu_homebutton.png")]
		private var __homeBtnImg:Class;
		[Embed(source="assets/imgs/menu/menu_backbutton.png")]
		private var __backBtnImg:Class;
		public function CornerMenu()
		{
			super();
			__optimizeBtn = new Image();
			__optimizeBtn.source = __optimizeBtnImg;
			__optimizeBtn.addEventListener(MouseEvent.CLICK,onOptimize);
			__optimizeBtn.addEventListener(MouseEvent.MOUSE_DOWN,mouseDownHandler);
			__optimizeBtn.addEventListener(MouseEvent.MOUSE_UP,mouseUpHandler);
			__optimizeBtn.addEventListener(MouseEvent.MOUSE_OUT,mouseUpHandler);
			__optimizeBtn.alpha = 0;
			__optimizeBtn.smooth = true;
			__optimizeBtn.buttonMode = true
			addElement(__optimizeBtn);
			__homeBtn = new Image();
			__homeBtn.source = __homeBtnImg;
			__homeBtn.addEventListener(MouseEvent.CLICK,onReturnHome);
			__homeBtn.addEventListener(MouseEvent.MOUSE_DOWN,mouseDownHandler);
			__homeBtn.addEventListener(MouseEvent.MOUSE_UP,mouseUpHandler);
			__homeBtn.addEventListener(MouseEvent.MOUSE_OUT,mouseUpHandler);
			__homeBtn.alpha = 0;
			__homeBtn.smooth = true;
			__homeBtn..buttonMode = true
			addElement(__homeBtn);
			__backBtn = new Image();
			__backBtn.source = __backBtnImg;
			__backBtn.addEventListener(MouseEvent.CLICK,onBack);
			__backBtn.addEventListener(MouseEvent.MOUSE_DOWN,mouseDownHandler);
			__backBtn.addEventListener(MouseEvent.MOUSE_UP,mouseUpHandler);
			__backBtn.addEventListener(MouseEvent.MOUSE_OUT,mouseUpHandler);
			__backBtn.alpha = 0;
			__backBtn.smooth = true;
			__backBtn..buttonMode = true
			addElement(__backBtn);
			__mainBtn = new Image();
			__mainBtn.source = __mainBtnCloseImg;
			__mainBtn.addEventListener(MouseEvent.CLICK,mainBtnClickHandler);
			__mainBtn.addEventListener(MouseEvent.MOUSE_DOWN,mouseDownHandler);
			__mainBtn.addEventListener(MouseEvent.MOUSE_UP,mouseUpHandler);
			__mainBtn.addEventListener(MouseEvent.MOUSE_OUT,mouseUpHandler);
			__mainBtn.smooth = true;
			__mainBtn..buttonMode = true
			addElement(__mainBtn);
			var centerP:Point = new Point(__mainBtn.width / 2,__mainBtn.height / 2);
			__optionBtnPoints = caculateCirclePoint(3,centerP,100);
		}
		
		protected function mouseUpHandler(event:MouseEvent):void
		{
			event.target.alpha = 1;
		}
		
		protected function mouseDownHandler(event:MouseEvent):void
		{
			event.target.alpha = .6;
		}
		
		/**全网重优化只在Tnlview并且是LIST页面的情况下可点击*/
		private var refreshTopo:IpcoreRefresh;
		private function onOptimize(evt:MouseEvent):void
		{
//			if(SdncUtil.cuOpenView == StaticConst.TNLVIEW && SdncUtil.cuTNLViewIndex == 1)
//				EventLocator.getInstance().dispatchEvent(new SdncEvt(SdncEvt.ALL_OPTIMIZE));
			TweenMax.to(SdncUtil.app.ipcore.physicsView.navPanel,0.6,{width:0});
		}
		
		private function onReturnHome(evt:MouseEvent):void
		{
//			EventLocator.getInstance().dispatchEvent(new SdncEvt(SdncEvt.RETURN_HOME));
			TweenMax.to(SdncUtil.app.ipcore.physicsView.navPanel,0.6,{width:430});
			refreshTopo = new IpcoreRefresh();
			refreshTopo.init();
		}
		
		private function onBack(evt:MouseEvent):void
		{
//			EventLocator.getInstance().dispatchEvent(new SdncEvt(SdncEvt.RETURN_FORMER));
			TweenMax.to(SdncUtil.app.ipcore.physicsView.navPanel,0.6,{width:430});
			refreshTopo = new IpcoreRefresh();
			refreshTopo.init();
		}
		
		protected function mainBtnClickHandler(event:MouseEvent):void
		{
			if(__isExtended)
			{
				TweenLite.to(__optimizeBtn,1,{x:0,y:0,alpha:0});
				TweenLite.to(__homeBtn,1,{x:0,y:0,alpha:0});
				TweenLite.to(__backBtn,1,{x:0,y:0,alpha:0});
				__mainBtn.source = __mainBtnCloseImg;
				__isExtended = false;
			}else
			{
				TweenLite.to(__optimizeBtn,1,{x:__optionBtnPoints[2].x,y:__optionBtnPoints[2].y,alpha:1});
				TweenLite.to(__homeBtn,1,{x:__optionBtnPoints[1].x,y:__optionBtnPoints[1].y,alpha:1});
				TweenLite.to(__backBtn,1,{x:__optionBtnPoints[0].x,y:__optionBtnPoints[0].y,alpha:1});
				__mainBtn.source = __mainBtnOpenImg;
				__isExtended = true;
			}
		}
		
		private function caculateCirclePoint(num:int,centerP:Point,radius:Number):Array
		{
			var angle:Number = -Math.PI / 2 / (num - 1);
			var arr:Array = [];
			for(var i:int = 0 ; i < num ; i++)
			{
				var point:Point = new Point();
				point.x = centerP.x + radius * Math.cos(0.1+angle * i);
				point.y = centerP.x + radius * Math.sin(0.1+angle * i);
				arr.push(point);
			}
			return arr;
		}
	}
}