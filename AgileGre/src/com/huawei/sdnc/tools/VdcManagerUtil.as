package com.huawei.sdnc.tools
{
import com.huawei.sdnc.view.common.AdvancedDataGrid.Renderer.AdgHeaderRender;
import com.huawei.sdnc.view.common.AdvancedDataGrid.Renderer.AdgItemrender;
import com.huawei.sdnc.view.common.AdvancedDataGrid.SubClasses.AdvancedDataGridColumn;

import flash.utils.Dictionary;

import mx.collections.ArrayCollection;
import mx.core.ClassFactory;
import mx.rpc.events.ResultEvent;

public class VdcManagerUtil
{
	public static var instance:VdcManagerUtil;
	
	public var hosts:Object;
	
	public var vms:Object;
	
	public function VdcManagerUtil()
	{
	}
	
	public static function getInstence():VdcManagerUtil
	{
		if(instance == null)
		{
			instance = new VdcManagerUtil();
		}
		return instance;
	}
	
	public function getTableDp(vnmXml:XML):ArrayCollection
	{
		var hostsArr:Array = hosts.hosts;
		var vmsArr:Array = vms.servers;
		var ac:ArrayCollection = new ArrayCollection();
		for each(var host:Object in hostsArr)
		{
			var hostName:String = host["host_name"];
			for each(var vmInfo:Object in vmsArr)
			{
				var parentName:String = vmInfo["OS-EXT-SRV-ATTR:hypervisor_hostname"];
				if(hostName == parentName)
				{
					var cuPorts:Array = [];
					for each(var network:XML in vnmXml.network)
					{
						var flag:Boolean = false;
						for each(var port:XML in network.ports.port)
						{
							if(port.hasOwnProperty("attachs"))
							{
								var vmMac:String = String(port.attachs.attach[0].vmMac);
								var instanceMac:String = String(vmInfo["mac_address"]);
								vmMac = vmMac.replace(/-/g,"");
								instanceMac = instanceMac.replace(/:/g,"");
								if(vmMac == instanceMac)
								{
									cuPorts.push(port);
									flag = true;
									break;
								}
							}
						}
						if(!flag) cuPorts.push(null);
					}
					var item:Object = new Object();
					item.host = host;
					item.vm = vmInfo;
					item.ports = cuPorts;
					ac.addItem(item);
				}
			}
		}
		return ac;
	}
	
	
	public function createVNColumn(title:String,dataField:String):AdvancedDataGridColumn
	{
		var adgc:AdvancedDataGridColumn = new AdvancedDataGridColumn();
		adgc.width = 125;
		adgc.resizable = false;
		adgc.headerText = title;
		adgc.dataField = dataField;
		adgc.sortable = false;
		adgc.editable = false;
		adgc.headerRenderer =  new ClassFactory(AdgHeaderRender);
		adgc.itemRenderer = new ClassFactory(AdgItemrender);
		return adgc;
	}
}
}