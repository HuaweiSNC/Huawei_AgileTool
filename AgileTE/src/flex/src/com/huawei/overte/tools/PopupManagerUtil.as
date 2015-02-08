package com.huawei.overte.tools
{
import com.huawei.overte.view.common.Loading;
import com.laiyonghao.Uuid;

import mx.managers.PopUpManager;

public class PopupManagerUtil
{
	public static var instance:PopupManagerUtil;
	
	private var loading:Loading;
	/**单例模式加载弹出转圈**/
	public static function getInstence():PopupManagerUtil
	{
		if(instance==null)
		{
			instance=new PopupManagerUtil();
		}
		return instance;
	}
	/**加载弹出转圈方法**/
	public function popupLoading(parent:*,model:Boolean=true):void
	{
		loading = Loading(PopUpManager.createPopUp(parent, Loading, model));
//		loading.id= (new Uuid()).toString()
		PopUpManager.centerPopUp(loading);
	}
	/**卸载弹出转圈方法**/
	public function closeLoading():void
	{
//		if(null != loading){
//		trace(loading.id+"=================================================")
//			
//		}
		PopUpManager.removePopUp(loading);
	}
	
	public function PopupManagerUtil()
	{
	}
}
}