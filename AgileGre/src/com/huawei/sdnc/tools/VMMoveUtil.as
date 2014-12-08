package com.huawei.sdnc.tools
{
import com.huawei.sdnc.event.SdncEvt;
import com.huawei.sdnc.service.SdnService;
import com.huawei.sdnc.view.common.navpanel.MatrixGrid;
import com.huawei.sdnc.view.common.node.ServerNode;
import com.huawei.sdnc.view.common.node.VmFollower;
import com.huawei.sdnc.vo.TopoXmlVo;

import flash.utils.Dictionary;

import mx.controls.Alert;

import org.httpclient.events.HttpDataEvent;

import twaver.ElementBox;
import twaver.Follower;
import twaver.Grid;
import twaver.IData;
import twaver.IElement;
import twaver.Link;
import twaver.Node;
import twaver.Styles;

public class VMMoveUtil
{
	public static var instance:VMMoveUtil;
	
	/**已点击的网元 / 当前点击的网元*/
	private var __clickedNodeForDcDic:Dictionary = new Dictionary();
	/**是否可以点击下一个网元,目的是当前点击的所请求数据返回后作相应处理*/
	private var __isClickNextForDcDic:Dictionary = new Dictionary();
	/**当前已选中的网元 / 已展开的server*/
	private var __cuSelectNodesForDcDic:Dictionary = new Dictionary();
	/**虚拟机GRID组*/
	private var __vmGridArrForDcDic:Dictionary = new Dictionary();
	
	private var __gridColorDic:Dictionary = new Dictionary();
	
	public var physicsBox:ElementBox;
	
	private var __vmId:String;
	
	private var __isMoveVm:Boolean = true;
	
	[Bindable]
	private var overlieUtil:OverlieUtil = OverlieUtil.getInstence();
	[Bindable]
	public var connUtil:ConnUtil = ConnUtil.getInstence();
	
	public static function getInstence():VMMoveUtil
	{
		if(instance == null)
		{
			instance = new VMMoveUtil();
		}
		return instance;
	}
	
	public function getVM(element:IElement,cuDcName:String):void
	{
		if(element is ServerNode && __isClickNextForDcDic[cuDcName])
		{
			__clickedNodeForDcDic[cuDcName] = element as ServerNode;
			var flag:Boolean = false;//当前点击的server，是否已经展开
			for each(var cuSelNode:ServerNode in __cuSelectNodesForDcDic[cuDcName])
			{
				if(cuSelNode.id == __clickedNodeForDcDic[cuDcName].id) flag = true;
			}
			if(!flag)
			{
				if(__cuSelectNodesForDcDic[cuDcName].length == 2)
				{
					__cuSelectNodesForDcDic[cuDcName].shift();
					var firstGrid:Grid = __vmGridArrForDcDic[cuDcName].shift();
					physicsBox.remove(firstGrid);
				}
				__cuSelectNodesForDcDic[cuDcName].push(__clickedNodeForDcDic[cuDcName]);//?
				var queryUrl:String = SdncUtil.dcUrlInfos[cuDcName][5];
				if(SdncUtil.cuProjectType != "test")
					queryUrl = ConnUtil.getInstence().computeUrl + ConnUtil.getInstence().serverUrl;
				var topoXmlVo:TopoXmlVo = onVmQuery(queryUrl,__clickedNodeForDcDic[cuDcName],__vmGridArrForDcDic[cuDcName]);
				topoXmlVo.addEventListener(SdncEvt.RESULT_INSTANCES,onInstances);
				__isClickNextForDcDic[cuDcName] = false;
//					trace("====");
//					trace(__cuSelectNodesForDcDic[cuDcName]);
//					trace(__clickedNodeForDcDic[cuDcName]);
			}
			else
			{
				//删除cuSelectNodes中的ClickedNode
				var indexofArr:int = __cuSelectNodesForDcDic[cuDcName].indexOf(__clickedNodeForDcDic[cuDcName]);
				__cuSelectNodesForDcDic[cuDcName].splice(indexofArr,1);
				//删除vmGridArr中ClickedNode对应的Grid
				var cuGrid:IData = physicsBox.getDataByID(__clickedNodeForDcDic[cuDcName].id + "Grid");
				var indexofArr_:int = __vmGridArrForDcDic[cuDcName].indexOf(cuGrid);
				__vmGridArrForDcDic[cuDcName].splice(indexofArr_,1);
				//删除BOX中ClickedNode对应的Grid
				physicsBox.remove(cuGrid);
			}
		}			
	}
	
	/**
	 * Vm请求
	 * 
	 * @param url VM请求地址
	 * @param clickedNode触发网元
	 * @return TopoXmlVo对象
	 * 
	 */
	public function onVmQuery(url:String,clickedNode:ServerNode,vmGridArr:Array):TopoXmlVo
	{
		var sevice:SdnService;
		if(clickedNode._nodeType == "host")
		{
			var remoteSubType:String = clickedNode._remoteInfo.chassisIdSubtype;
			if(remoteSubType != null && remoteSubType != "")
			{
				sevice = DCDataDealUtil.getInstence().__serviceDic[SdncUtil.currentDcName];
				sevice._topoXmlVo = new TopoXmlVo();
				if(SdncUtil.cuProjectType == "test")//test工程时读取本地虚拟机数据
				{
					if(clickedNode.name == "server19")//暂时限制为servername为compute和controller的节点才有虚拟机数据
						url = "assets/xml/testInstance/server19_instance.xml";
					else if(clickedNode.name == "server20")
						url = "assets/xml/testInstance/server20_instance.xml";
					sevice.instancesInfoQuery(url);
				}
				else//为混合工程时，读取真实虚拟机数据
				{
					sevice.instancesInfoQuery(url);
				}
			}
		}
		return sevice._topoXmlVo;
	}
		
	/**
	 * 处理HOST相对应的VM
	 **/
	private function onInstances(evt:SdncEvt):void
	{
		var cuState:String = SdncUtil.app.main.currentState;
		if(cuState != "physics_view") return;
		
		var dcName:String = SdncUtil.currentDcName;
		__isClickNextForDcDic[dcName] = true;//?
		var clickedNode:ServerNode = __clickedNodeForDcDic[dcName];
		
		var gridId:String = clickedNode.id+"Grid";
		var grid:Grid = TopoUtil.createGrid(physicsBox,gridId,clickedNode.name);
		__vmGridArrForDcDic[dcName].push(grid);
		//给GRID背景染色
		var color:uint;
		if(__gridColorDic[gridId])
			color = __gridColorDic[gridId];
		else
		{
			if(__vmGridArrForDcDic[dcName][0] == grid)
				color = 0x5f52a0;
			else if(__vmGridArrForDcDic[dcName][1] == grid)
				color = 0x009944;
		}
		__gridColorDic[gridId] = color;
		grid.setStyle(Styles.GRID_FILL_COLOR,color);
		
		if(SdncUtil.cuProjectType == "test")
		{
			var vmXmlInfos:XML = new XML(evt.params);
			for each(var vmInfo:XML in vmXmlInfos.instance)
			{
				var vmF:Follower;
				if(SdncUtil.cuProjectType == "test")//本地数据 防止id相同
				{
					vmF = TopoUtil.createFollower(physicsBox,clickedNode.id + vmInfo.vm_netinfo,vmInfo.name);
				}
				else
				{
					vmF = TopoUtil.createFollower(physicsBox,vmInfo.id,vmInfo.name);
				}
				vmF.parent = grid;
				vmF.host = grid;
				(vmF as VmFollower).hostName = clickedNode.name;
				(vmF as VmFollower).nodeInfo = vmInfo;
			}
		}
		else
		{
			var instancesArr:Array = evt.params.servers as Array;
			for each(var vmInfo_:Object in instancesArr)
			{
				trace(vmInfo_.name + "--" + vmInfo_["mac_address"]);
				if(vmInfo_["OS-EXT-SRV-ATTR:host"] == clickedNode.name)
				{
					var vmF_:Follower;
					vmF_ = TopoUtil.createFollower(physicsBox,vmInfo_.id,vmInfo_.name);
					vmF_.parent = grid;
					vmF_.host = grid;
					(vmF_ as VmFollower).hostName = clickedNode.name;
					(vmF_ as VmFollower).nodeInfo = vmInfo_;
				}
				
			}
		}
		
		//    	trace("vmXmlInfos.instance.length:"+vmXmlInfos.instance.length());//生成22个假数据
		if(SdncUtil.cuProjectType == "commix" && clickedNode.name == "compute161")
		{
			for(var i:int=0;i<18;i++)
			{
				var vmF1:Follower = TopoUtil.createFollower(physicsBox,"follower"+i,"follower"+i);
				vmF1.parent = grid;
				vmF1.host = grid;
				(vmF1 as VmFollower).hostName = clickedNode.name;
				//				(vmF1 as VmFollower).nodeInfo = vmInfo;
			}
		}
		
		var cuSelectedOverlie:Object = overlieUtil.cuSelectedOverlies[dcName];
		var cuSelectedVdcArr:Object = overlieUtil.selectedVdcObjs[dcName];
		//可能的问题 :VM染色问题
		grid.children.forEach(function(data:IData):void{
			for each(var cuSelVnmXml:XMLList in cuSelectedVdcArr)
			{
				if(cuSelVnmXml && cuSelectedOverlie)
				{
					var vniId:String = cuSelVnmXml.vniId;
					var cuIsOverlie:Boolean = cuSelectedOverlie[vniId]["cuIsOverlie"];
					var cuColor:uint = cuSelectedOverlie[vniId]["cuColor"];
					//					trace(vniId+"___"+cuIsOverlie+"___" + cuColor);
					overlieUtil.signVmFollower(data as VmFollower,cuSelVnmXml,cuIsOverlie,cuColor);
				}
			}
			
		});
		onVmFollowerLocation(__vmGridArrForDcDic[dcName],clickedNode);
	}
	
	/**
	 * 处理GRID坐标和VM在GRID中的位置
	 * @param:arr:虚拟机组的数组
	 * @param:referenceN:点击的server节点
	 */
	private function onVmFollowerLocation(arr:Array,referenceN:Node):void
	{
		var xArr:Array = [158,852];
		for(var i:* in arr)
		{
			var grid:Grid = arr[i] as Grid;
			grid.setLocation(xArr[i%2],referenceN.y + 130);
			grid.children.forEach(function(data:IData):void{
				var cuNode:Node = data as Node;
				var itemIndex:int = grid.children.getItemIndex(data);
				var columnIndex:int = itemIndex % 8;
				var rowIndex:int = itemIndex < 16 ? (itemIndex < 8 ? 0 : 1) : 2;
				cuNode.setStyle(Styles.FOLLOWER_ROW_INDEX, rowIndex);
				cuNode.setStyle(Styles.FOLLOWER_COLUMN_INDEX, columnIndex);
			});
		}
	}
	
	/**初始化进入VM的一些标志和值
	 * @param cuBox:当前操作的databox
	 * @param dcName:当前的dc名字
	 */
	public function initGoToFlagValue(cuBox:ElementBox,dcName:String):void
	{
		__clickedNodeForDcDic[dcName] = null;
		__isClickNextForDcDic[dcName] = true;
		__cuSelectNodesForDcDic[dcName] = [];
		__vmGridArrForDcDic[dcName] = [];
	}
	/** 清除虚拟机组*/
	public function clearGrids():void
	{
		var arr:Array = __vmGridArrForDcDic[SdncUtil.currentDcName];
		for each(var item:* in arr)
		{
			physicsBox.remove(item);
		}
		arr = [];
	}
	
	/**VM迁移*/
	public function onVmMove(evt:SdncEvt):void
	{
		var grid:Grid = evt.params as Grid;
		if(grid.name.indexOf("follower") != -1) return;
		var dcName:String = SdncUtil.currentDcName;
		__vmId = SdncUtil.dragedVM.id.toString();
		overlieUtil.__hostName = grid.name;
		trace("__hostName:"+overlieUtil.__hostName);
		var box:ElementBox = SdncUtil.dataBoxsDic[dcName];
		box.forEachByBreadthFirst(function(data:IData):void{
			if(data is Link)
			{
				var cuLink:Link = data as Link;
				if(uint(cuLink.getStyle(Styles.LINK_COLOR)) != 0x66CCFF)
				{
					box.remove(data);
				}
			}
		});
		var cuDcMG:Object = overlieUtil.matrixGrids[dcName];
		for each(var item:Object in cuDcMG)
		{
			var cuGrid:MatrixGrid = item as MatrixGrid;
			if(cuGrid) overlieUtil.overlieVdc1(cuGrid,SdncEvt.OVERLIE_VDC);
		}
		if(SdncUtil.cuProjectType != "test")
		{
			//如果其中一个参数为空，则提示并跳出避免对后台进行错误操作
			if(!(connUtil.tenantId && connUtil.tokentId && __vmId && overlieUtil.__hostName))
			{
				Alert.show("Do not have enough parameter!");
				return;
			}
			onVMMove();
		}
	}
	
	/**处理VM迁移*/
	private function onVMMove():void
	{
		if(__isMoveVm)
		{
			__isMoveVm = false;
			var url:String = ConnUtil.getInstence().computeUrl + "/servers/" + __vmId + "/action";
			var param:String = "{\"os-migrateLive\": {\"host\": \"" + overlieUtil.__hostName + "\",\"block_migration\": true,\"disk_over_commit\": false}}";
			ConnUtil.getInstence().sendRequest(url,param,onMoveResult,onMoveError,connUtil.tokentId); //获取到tokenid后发送迁移请求
		}
	}
	
	private function onMoveResult(e:HttpDataEvent):void
	{
		trace(e.bytes.toString());
	}
	private function onMoveError(e:*):void
	{
		Alert.show(e.text);
	}
	
	public function VMMoveUtil()
	{
	}
}
}