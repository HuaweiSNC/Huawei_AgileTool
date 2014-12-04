package com.huawei.sdnc.view.common.node
{
import com.huawei.sdnc.view.common.nodeui.GroupImgBgUI;

import twaver.Group;

public class ImgBgGroup extends Group
{
	public function ImgBgGroup(id:Object=null){
		super(id);
	}
	
	override public function get elementUIXClass():Class{
		return GroupImgBgUI;
	}	
}
}