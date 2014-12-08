package com.huawei.sdnc.tools
{
import com.huawei.sdnc.event.SdncEvt;
import com.huawei.sdnc.view.common.navpanel.MatrixGrid;
import com.huawei.sdnc.view.common.node.VmFollower;
import com.huawei.sdnc.vo.TopoXmlVo;

import flash.utils.ByteArray;

import mx.controls.Alert;

import org.httpclient.events.HttpErrorEvent;
import org.httpclient.events.HttpResponseEvent;

import twaver.Consts;
import twaver.ElementBox;
import twaver.IData;
import twaver.Link;
import twaver.Node;
import twaver.Styles;

public class OverlieUtil
{
	public static var instance:OverlieUtil;
	/**当前选择的VDC集合*/
	public var selectedVdcObjs:Object = {};
	/**当前已选择的vnm叠加*/
	public var cuSelectedOverlies:Object = {};
	
	/**模拟有染色效果的VM名称,真假混合工程用*/
	public var hasVlanEffectVmName:String = "vm1";
	
	/**当前叠加染色XML*/
	public var cuVlanlist:*;
	
	public var __hostName:String = "";
	
	public var matrixGrids:Object = {};
	
	//真假混合工程用
	public var changedVlantopo19:XML;
	public var changedVlantopo20:XML;
	
	public static function getInstence():OverlieUtil
	{
		if(instance==null)
		{
			instance=new OverlieUtil();
		}
		return instance;
	}
	
	public function overlieVdc1(grid:MatrixGrid,type:String):void
	{
		var dcName:String = grid.__dcName;
		var vdcID:String = grid.__vdcID;
		var color:uint = grid.color;
		
		var isOverlie:Boolean = false;
		if(!matrixGrids[dcName])
			matrixGrids[dcName] = {};
		if(type == SdncEvt.OVERLIE_VDC) 
		{
			isOverlie = true;
			matrixGrids[dcName][vdcID] = grid;
		}
		else
		{
			matrixGrids[dcName][vdcID] = null;
		}
		var cuVdcIDObj:Object = {"cuColor":color,"cuIsOverlie":isOverlie};
		if(!cuSelectedOverlies[dcName])
		{
			cuSelectedOverlies[dcName] = {};
		}
		cuSelectedOverlies[dcName][vdcID] = cuVdcIDObj;
		
		var topoXml:TopoXmlVo = SdncUtil.dcTopoXmlDic[dcName];
		if(topoXml)
		{
			var vnmXml:XML = topoXml._vnmXml;
			var selectedVnmXml:XMLList = vnmXml.network.(vniId == vdcID);
			if(selectedVnmXml)
			{
				if(!selectedVdcObjs[dcName])
				{
					selectedVdcObjs[dcName] = {};
				}
				if(!selectedVdcObjs[dcName][vdcID]) selectedVdcObjs[dcName][vdcID] = selectedVnmXml;
				else selectedVdcObjs[dcName][vdcID] = null;
			}
		}
		var box:ElementBox = SdncUtil.dataBoxsDic[dcName];
		
		if(SdncUtil.cuProjectType == "normal")
		{
			var urlArr:Array = SdncUtil.dcUrlInfos[dcName];
			var url:String = urlArr[3];
			url = url + "/trillInfos/trillInfo?trillId=" + vdcID;
			PopupManagerUtil.getInstence().popupLoading(SdncUtil.app);
			ConnUtil.getInstence().clientQuery(url,ConnUtil.METHOD_GET,function(e:HttpResponseEvent,data:ByteArray):void{
				var str:String = data.toString();
				str = str.replace(/xmlns(.*?)="(.*?)"/gm, "");
				createOverLieLinK(XML(str),box,dcName,isOverlie,color,selectedVnmXml);
				PopupManagerUtil.getInstence().closeLoading();
			},function(e:HttpErrorEvent):void{
//				logUtil.recordResult("vlantopo",e.text);
				Alert.show("Request vlantopo is " + e.text);
			});
//			cuVlanlist = SdncUtil.vlantopoInfos;
//			if(cuVlanlist[vdcID]) 
//				createOverLieLinK(cuVlanlist[vdcID],box,dcName,isOverlie,color,selectedVnmXml);
		}
		else
		{
			if(__hostName != "" && __hostName != null)
			{
				cuVlanlist = getVlanXML(__hostName);
			}
			if(SdncUtil.cuProjectType == "test")
			{
				if(__hostName == "")
					cuVlanlist = getVlanXML("server19");
			}
			for each(var vlan:XML in cuVlanlist)
			{
				if(vlan.vlanId == vdcID)
				{
					createOverLieLinK(vlan,box,dcName,isOverlie,color,selectedVnmXml);
					break;
				}
			}
		}
		if(isOverlie) return;
		var cuSelectedOverlie:Object = cuSelectedOverlies[dcName];
		var cuSelectedVdcArr:Object = selectedVdcObjs[dcName];
		for each(var cuSelVnmXml:XMLList in cuSelectedVdcArr)
		{
			if(cuSelVnmXml)
			{
				var vniId:String = cuSelVnmXml.vniId;
				var cuIsOverlie:Boolean = cuSelectedOverlie[vniId]["cuIsOverlie"];
				var cuColor:uint = cuSelectedOverlie[vniId]["cuColor"];
				box.forEachByBreadthFirst(function(data:IData):void{
					if(data is VmFollower)
					{
						var cuVm:VmFollower = data as VmFollower;
						signVmFollower(cuVm,cuSelVnmXml,cuIsOverlie,cuColor);
					}
				});
			}
		}
	}
	
	private function createOverLieLinK(vlan:XML,box:ElementBox,dcName:String,isOverlie:Boolean,color:uint,selectedVnmXml:XMLList):void
	{
		var vlanList:XMLList;
		if(vlan.hasOwnProperty("trillInfos"))
			vlanList = vlan.trillInfos.trillInfo.trilltreeNeighbors.trilltreeNeighbor;
		else
			vlanList = vlan.vlantreeNeighbors.vlantreeNeighbor;
		for each(var overline:XML in vlanList)
		{
			var fromN:Node = TopoUtil.getNodeById(box,dcName + "_" + String(overline.nodeid));
			var toN:Node = TopoUtil.getNodeById(box,dcName + "_" + String(overline.childnodeid));
			box.forEachByBreadthFirst(function(item:IData):void{
				if(item is Link)
				{
					var link:Link = item as Link;
					if(link.fromNode.id == fromN.id && link.toNode.id == toN.id)
					{
						if(isOverlie && (link.getStyle(Styles.LINK_COLOR) == 0x66CCFF))
						{
							var overLink:Link = new Link(null,fromN,toN);
							overLink.setStyle(Styles.LINK_BUNDLE_ENABLE,false);
							overLink.setStyle(Styles.LINK_COLOR,color);
							overLink.setStyle(Styles.LINK_TYPE,Consts.LINK_TYPE_PARALLEL);
							overLink.setStyle(Styles.LINK_BUNDLE_OFFSET,26);
							box.add(overLink);
						}
						else
						{
							if(uint(link.getStyle(Styles.LINK_COLOR)) == color)
							{
								box.remove(link);
							}
						}
					}
				}
				if(item is VmFollower)
				{
					var vm:VmFollower = item as VmFollower;
					if(selectedVnmXml)	signVmFollower(vm,selectedVnmXml,isOverlie,color);
				}
			});
		}
	}
	
	public function signVmFollower(cuVmFollower:VmFollower,cuSeledVnmXml:XMLList,isOverlie:Boolean,color:uint):void
	{
		for each(var instanceXml:XML in cuSeledVnmXml.ports.port)
		{
			if(cuVmFollower.nodeInfo)
			{
				var macAddress:String;
				if(SdncUtil.cuProjectType == "test")
					macAddress = String(cuVmFollower.nodeInfo.instance_mac_address);
				else
					macAddress = String(cuVmFollower.nodeInfo.mac_address);
				for each(var attach:XML in instanceXml.attachs.attach)
				{
					if(String(attach.vmMac) == macAddress || cuVmFollower.name == hasVlanEffectVmName)
					{
						if(isOverlie)
						{
							cuVmFollower.setStyle(Styles.LABEL_CORNER_RADIUS,5);
							cuVmFollower.setStyle(Styles.LABEL_OUTLINE_WIDTH,2);
							cuVmFollower.setStyle(Styles.LABEL_OUTLINE_COLOR,color);
						}
						else
						{
							cuVmFollower.setStyle(Styles.LABEL_OUTLINE_WIDTH,-1);
						}
					}
				}
			}
		}
	}
	
	public function getVlanXML(hostName:String):XMLList
	{
		var xml:XML;
		switch(hostName)
		{
			case "compute216":
			case "server19":
			{
				xml = changedVlantopo19;
				break;
			}
			case "compute217":
			case "server20":
			{
				xml = changedVlantopo20;
				break;
			}
			default:
			{
				break;
			}
		}
		return xml.sdnl2vlantopo.vlanInfomation;
	}
	
	/**
	 * 清除所有VLAN叠加
	 */
	public function clearAllOverlie(box:ElementBox):void
	{
		box.forEachByBreadthFirst(function(item:IData):void{
			if(item is Link)
			{
				var link:Link = item as Link;
				if(uint(link.getStyle(Styles.LINK_COLOR)) != 0x66CCFF)
				{
					box.remove(link);
				}
			}
		});
		matrixGrids = {};
		clearAllVmOverlie(box);
	}
	public function clearAllVmOverlie(box:ElementBox):void
	{
		var lastDcName:String = SdncUtil.currentDcName;
		cuSelectedOverlies[lastDcName] = null;
		selectedVdcObjs[lastDcName] = null;
		box.forEachByBreadthFirst(function(item:IData):void{
			if(item is VmFollower)
			{
				var vm:VmFollower = item as VmFollower;
				if(vm.getStyle(Styles.LABEL_OUTLINE_WIDTH) != -1)
				{
					vm.setStyle(Styles.LABEL_OUTLINE_WIDTH,-1);
				}
			}
		});
	}
	
	public function OverlieUtil()
	{
	}
}
}