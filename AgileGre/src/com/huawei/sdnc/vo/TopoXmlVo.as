package com.huawei.sdnc.vo
{
import com.huawei.sdnc.event.SdncEvt;
import com.huawei.sdnc.tools.SdncUtil;

import flash.events.EventDispatcher;

public class TopoXmlVo extends EventDispatcher
{
	private var __fpNodeMap:Object = new Object();
	
	private var __dcName:String;
	
	/**l2拓扑关系XML*/
	private var __l2topoXml:XML;
	
	/**server层外部拓扑关系XML*/
	private var __lldpXml:XML;
	
	/**转发节点的具体信息XML*/
	private var __vclusterXml:XML;
	
	/**服务器信息XML*/
	private var __instancesXml:XML;/*此处已改用__instancesObj*/
	/**服务器信息json*/
	private var __instancesObj:Object;
	
	/**物理视图叠加虚拟视图信息XML*/
	private var __vlantopoXml:XML;
	
	/**虚拟视图网络信息XML*/
	private var __vnmXml:XML;
	
	private var __l3topoXml:XML;
	
	public function TopoXmlVo()
	{
	}
	
	private function insureDateFull():void
	{
	/*	if(__l2topoXml != null && __lldpXml != null && __vclusterXml != null && __vlantopoXml != null && __vnmXml != null)
		{
			dispatcherResultEvent(SdncEvt.DATA_IS_READY);
		}*/
		if(__l2topoXml != null&&__vclusterXml != null)
		{
			this.dispatchEvent(new SdncEvt(SdncEvt.L2TOPO_DATA_IS_READY));
		}
	}
	
	private function insureVlanData():void
	{
		if(__vnmXml != null && __vlantopoXml != null)
		{
			this.dispatchEvent(new SdncEvt(SdncEvt.VNM_VLANTOPO_IS_READY));
		}
	}
	
	public function get _l3topoXml():XML
	{
		return __l3topoXml;
	}
	
	[Bindable]
	public function set _l3topoXml(value:XML):void
	{
		__l3topoXml = value;
		dispatchEvent(new SdncEvt(SdncEvt.L3TOPO_DATA_IS_READY));
	}
	
	public function get _l2topoXml():XML
	{
		return __l2topoXml;
	}
	
	[Bindable]
	public function set _l2topoXml(value:XML):void
	{
		__l2topoXml = value;
		insureDateFull();
//		dispatcherResultEvent(SdncEvt.RESULT_12TOPO,_l2topoXml);
	}
	
	public function get _lldpXml():XML
	{
		return __lldpXml;
	}
	
	[Bindable]
	public function set _lldpXml(value:XML):void
	{
		__lldpXml = value;
		this.dispatchEvent(new SdncEvt(SdncEvt.LLDP_DATA_IS_READY));
//		dispatcherResultEvent(SdncEvt.RESULT_LLDP,_lldpXml);
	}
	
	public function get _vclusterXml():XML
	{
		return __vclusterXml;
	}
	
	[Bindable]
	public function set _vclusterXml(value:XML):void
	{
		__vclusterXml = value;
		for each(var vccluseter:XML in __vclusterXml.vcFpInfos.vcFpInfo)
		{
			__fpNodeMap[__dcName + "_" + vccluseter.fpId] = vccluseter;
			if(vccluseter.state != "running")
				dispatchEvent(new SdncEvt(SdncEvt.NODE_STATE_ERROE,__dcName + "_" + vccluseter.fpId));
		}
		insureDateFull();
//		dispatchEvent(new SdncEvt(SdncEvt.VCLUSTER_DATA_IS_READY));
//		dispatcherResultEvent(SdncEvt.RESULT_VCLUSTER,_vclusterXml);
	}
	
	public function get _vlantopoXml():XML
	{
		return __vlantopoXml;
	}
	
	[Bindable]
	public function set _vlantopoXml(value:XML):void
	{
		__vlantopoXml = value;
		var vlanList:XMLList;
		var vlanId:String;
		if(SdncUtil.cuProjectType == "normal")
		{
			if(__vlantopoXml.hasOwnProperty("trillInfos"))
			{
				vlanList = __vlantopoXml.trillInfos.trillInfo;
				vlanId = "trillId";
			}
			else
			{
				vlanList = __vlantopoXml.vlanInfos.vlanInfo;
				vlanId = "vlanId";
			}
		}
		else
		{
			vlanList =__vlantopoXml.sdnl2vlantopo.vlanInfomation;
			vlanId = "vlanId";
		}
		for each(var vlan:XML in vlanList)
		{
			SdncUtil.vlantopoInfos[vlan[vlanId]] = vlan;
		}
		insureVlanData();
	}
	
	public function get _vnmXml():XML
	{
		return __vnmXml;
	}
	
	[Bindable]
	public function set _vnmXml(value:XML):void
	{
		__vnmXml = value;
		insureVlanData();
	}
	
	public function get _instancesXml():XML
	{
		return __instancesXml;
	}
	
	[Bindable]
	public function set _instancesXml(value:XML):void
	{
		__instancesXml = value;
		dispatcherResultEvent(SdncEvt.RESULT_INSTANCES,__instancesXml);
	}
	public function get _instancesObj():Object
	{
		return __instancesObj;
	}
	
	[Bindable]
	public function set _instancesObj(value:Object):void
	{
		__instancesObj = value;
//		dispatcherResultEvent(SdncEvt.RESULT_INSTANCES,__instancesObj);
		dispatchEvent(new SdncEvt(SdncEvt.RESULT_INSTANCES,__instancesObj));
	}
	
	/**
	 *　ＤＣ名字 
	 */
	public function get _dcName():String
	{
		return __dcName;
	}
	
	/**
	 * @private
	 */
	[Bindable]
	public function set _dcName(value:String):void
	{
		__dcName = value;
	}
	
	private function dispatcherResultEvent(type:String,xml:XML = null):void
	{
		dispatchEvent(new SdncEvt(type,xml));
	}

	/**存放每个节点的信息,节点ID为键值*/
	public function get _fpNodeMap():Object
	{
		return __fpNodeMap;
	}

	/**
	 * @private
	 */
	public function set _fpNodeMap(value:Object):void
	{
		__fpNodeMap = value;
	}


}
}