package com.huawei.sdnc.view.common.node
{

	import twaver.*;

	public class Card extends Follower{
		
		public function Card(id:Object=null){
			super(id);
			this.icon = "card_icon";
		 	
		 	this.setStyle(Styles.CONTENT_TYPE, Consts.CONTENT_TYPE_VECTOR);
			this.setStyle(Styles.VECTOR_SHAPE, Consts.SHAPE_RECTANGLE);
			this.setStyle(Styles.VECTOR_GRADIENT, Consts.GRADIENT_NONE);				
			this.setStyle(Styles.VECTOR_DEEP, 2);	
			this.width = 30;
			this.height = 100;					
		}
	}
}