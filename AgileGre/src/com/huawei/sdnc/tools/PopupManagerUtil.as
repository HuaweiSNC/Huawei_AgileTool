package com.huawei.sdnc.tools
{
import com.huawei.sdnc.view.common.Loading;

import mx.managers.PopUpManager;

public class PopupManagerUtil
{
	public static var instance:PopupManagerUtil;
	
	private var loading:Loading;
	
	public static function getInstence():PopupManagerUtil
	{
		if(instance==null)
		{
			instance=new PopupManagerUtil();
		}
		return instance;
	}
	
	public function popupLoading(parent:*,model:Boolean=true):void
	{
		loading = Loading(PopUpManager.createPopUp(parent, Loading, model));
//		loading.width = parent.width;
//		loading.height = parent.height;
		PopUpManager.centerPopUp(loading);
	}
	
	public function closeLoading():void
	{
		PopUpManager.removePopUp(loading);
	}
	
	public function PopupManagerUtil()
	{
	}
}
}