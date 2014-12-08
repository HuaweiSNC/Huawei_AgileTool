package com.huawei.sdnc.tools
{
import com.huawei.sdnc.event.SdncEvt;
import com.huawei.sdnc.service.SdnService;
import com.huawei.sdnc.view.common.node.ServerNode;
import com.huawei.sdnc.view.common.node.StateNode;
import com.huawei.sdnc.view.dataCenter.physics.PhysicsLayout;
import com.huawei.sdnc.vo.TopoXmlVo;

import flash.geom.Point;
import flash.utils.Dictionary;

import mx.controls.Alert;

import twaver.Consts;
import twaver.ElementBox;
import twaver.Group;
import twaver.IData;
import twaver.Link;
import twaver.Node;
import twaver.Styles;
import twaver.networkx.NetworkX;

public class DCDataDealUtil
{
	public var physicsView:PhysicsLayout;
	
	/**平面布局用到的容器*/
	public var __eBox:ElementBox;
	
	/**暂存CORE类型节点*/
	private var __coreArr:Array = [];
	/**暂存AGG类型节点*/
	private var __aggArr:Array = [];
	/**暂存TOR类型节点*/
	private var __torArr:Array = [];
	/**暂存computer类型节点*/
	private var __computerArr:Array = [];
	/**暂存remote远程节点*/
	private var __remoteArr:Array = [];
	/**暂存link*/
	private var __linkArr:Array = [];
	/**中心参考点*/
	private var __centerP:Point = new Point(800,50);
	/**自定义布局X轴间隔*/
	private var __xGap:Number = 150;
	/**自定义布局Y轴间隔*/
	private var __yGap:Number = 150;
	
	public var __serviceDic:Dictionary = new Dictionary;
	
	/**存放DCgroup*/
	public var __dcGArr:Array = [];
	
	private static var instence:DCDataDealUtil;
	
	public static function getInstence():DCDataDealUtil
	{
		if(instence==null)
		{
			instence = new DCDataDealUtil();
		}
		return instence;
	}
	
	/**
	 * 初始化BOX
	 * 根据url获取数据并添加相应的网元
	 */
	public function initBox():void
	{
		for each(var cuDcName:String in SdncUtil.dcNameArr)
		{
			var dcinfo:XML = SdncUtil.dcMap[cuDcName];
			var urlArr:Array = SdncUtil.dcUrlInfos[cuDcName];
			if(urlArr != null && urlArr.length > 0)
			{
				var sdnService:SdnService = new SdnService();
				sdnService._topoXmlVo = new TopoXmlVo();
				sdnService.topo12InfoQuery(urlArr[0]);
				sdnService.lldpQuery(urlArr[1]);
				sdnService.vclusterQuery(urlArr[2]);
				sdnService.vlantopoQuery(urlArr[3]);
				sdnService.vnmQuery(urlArr[4]);
				sdnService._topoXmlVo._dcName = cuDcName;
				__serviceDic[cuDcName] = sdnService;
				sdnService._topoXmlVo.addEventListener(SdncEvt.L2TOPO_DATA_IS_READY,onTopoData);
				sdnService._topoXmlVo.addEventListener(SdncEvt.VNM_VLANTOPO_IS_READY,onTopoData);
				sdnService._topoXmlVo.addEventListener(SdncEvt.LLDP_DATA_IS_READY,onTopoData);
			}
			else
			{
				var cuGroup:Group = TopoUtil.createGroup(__eBox,dcinfo.name,dcinfo.desc +
					"-----No Data","CloudGroup");
				__dcGArr.push(cuGroup);
				groupLayout(__dcGArr);
			}
		}
		SdncUtil.currentDcName = "dc";
	}
	
	private function onTopoData(evt:SdncEvt):void
	{
		var topoXmlVo:TopoXmlVo = evt.currentTarget as TopoXmlVo;
		var dcName:String = topoXmlVo._dcName;
		SdncUtil.dcTopoXmlDic[dcName] = topoXmlVo;
		var cuGroup:Group;
		if(evt.type == SdncEvt.L2TOPO_DATA_IS_READY)
		{
			cuGroup = onDc(dcName,topoXmlVo._l2topoXml);
			cuGroup.name = SdncUtil.dcMap[dcName].desc;
			topoXmlVo.addEventListener(SdncEvt.NODE_STATE_ERROE,onNodeStateError);
			__dcGArr.push(cuGroup);
			groupLayout(__dcGArr);
		}
		else if(evt.type == SdncEvt.VNM_VLANTOPO_IS_READY)
		{
			var vdcNameArr:Array = SdncUtil.vdcNameArr;
			for each(var cuVdc:XML in topoXmlVo._vnmXml.network)
			{
				var networkName:String = String(cuVdc.networkName);
				if(!SdncUtil.isExistInArr(vdcNameArr,networkName))
				{
					vdcNameArr.push(networkName);
				}
			}
			physicsView._app.main.navPanel.matrixSelector.initMatrixSelector();
		}
		else if(evt.type == SdncEvt.LLDP_DATA_IS_READY)
		{
			if(topoXmlVo._l2topoXml != null)
			{
				if(topoXmlVo._lldpXml==null) return;
				__eBox.forEachByBreadthFirst(function(item:IData):void{
					if(item is StateNode)
					{
						var node:StateNode = item as StateNode;
						if(node.image == "TORNode")
						{
							createComputerLevel(topoXmlVo._lldpXml,__eBox,topoXmlVo._dcName,node,node.parent as Group);
						}
					}
				});
			}
		}
		if(topoXmlVo._l2topoXml && topoXmlVo._vnmXml && topoXmlVo._lldpXml)
		{
			PopupManagerUtil.getInstence().closeLoading();
		}
	}
	
	/**
	 * 创建每个dctopo图
	 * @param l2topoXml:当前DC的拓扑关系XML
	 * @param lldpXml:当前DC的server拓扑关系
	 * @param vclusterXml:节点详细信息XML 
	 */
	private function onDc(cuDcName:String,l2topoXml:XML):Group
	{
		var dcG:Group = TopoUtil.createGroup(__eBox,l2topoXml.@controller,l2topoXml.@controller,
			SdncUtil.imagesObjects.Cloud,0.6,Consts.SHAPE_ROUNDRECT);
		dcG.setStyle(Styles.GROUP_PADDING,10);
		
		initArrary();
		var topoXmlVo:TopoXmlVo = SdncUtil.dcTopoXmlDic[cuDcName];
		for each(var cuNode:XML in l2topoXml.l2toponodes.l2toponode)
		{
			var cuNodeType:String = String(cuNode.nodeType);
			var node:Node = TopoUtil.createNode(__eBox,StateNode,cuDcName + "_" + cuNode.fpID,cuNode.systemName,
				SdncUtil.imagesObjects[cuNodeType],new Point(cuNode.x,cuNode.y));
			if(!(node as StateNode).nodeInfo)
				(node as StateNode).nodeInfo = topoXmlVo._fpNodeMap[node.id];
			if(cuNodeType == "CORE")
				__coreArr.push(node);
			else if(cuNodeType == "AGG")
				__aggArr.push(node);
			else if(cuNodeType == "TOR")
				__torArr.push(node);
			
			var areaG:Group;
			if(cuNode.hasOwnProperty("location"))
			{
				var location:String = cuNode.location[0];
				if(location.search("area") != -1)
				{
					var locationArr:Array = location.split("/");
					var cuAreaId:String = cuDcName + locationArr[1];
					var cuAreaName:String = locationArr[1];
					areaG = TopoUtil.createGroup(__eBox,cuAreaId,cuAreaName,SdncUtil.imagesObjects.AREA,0.6);
					areaG.addChild(node);
					if(dcG.childrenCount == 0)
					{
						dcG.addChild(areaG);
					}
					else
					{
						dcG.children.forEach(function(data:IData):void{
							if(data.id != cuAreaId)
							{
								dcG.addChild(areaG);
							}
						});
					}
				}
				else
				{
					dcG.addChild(node);
				}
			}
			else
			{
				dcG.addChild(node);
			}
			if(cuNode.nodeType == "TOR" && topoXmlVo._lldpXml != null)
			{
				createComputerLevel(topoXmlVo._lldpXml,__eBox,cuDcName,node,areaG ? areaG : dcG);
			} 
		}
		if(!SdncUtil.cordinateFlag)
			topoDoLayout();
		CreateLinkByDC(l2topoXml,cuDcName);
//		loadAndCreateLLdp(cuDcName);
		return dcG;
	}
	
	/*private function loadAndCreateLLdp(dcName:String):void
	{
		if(__serviceDic[dcName])
		{
			var sdnService:SdnService = __serviceDic[dcName] as SdnService;
			var urlArr:Array = SdncUtil.dcUrlInfos[sdnService._topoXmlVo._dcName];
			sdnService.lldpQuery(urlArr[1]);
			sdnService._topoXmlVo.addEventListener(SdncEvt.LLDP_DATA_IS_READY,function(evt:SdncEvt):void{
				var topoXmlVo:TopoXmlVo = evt.currentTarget as TopoXmlVo;
				if(topoXmlVo._lldpXml==null) return;
				__eBox.forEachByBreadthFirst(function(item:IData):void{
					if(item is StateNode)
					{
						var node:StateNode = item as StateNode;
						if(node.image == "TORNode")
						{
							var areaG:Group;
							if((node.parent as Group).name.search("area") != -1)
							{
								areaG = node.parent as Group;
							}else
							{
								areaG = null;
							}
							createComputerLevel(topoXmlVo._lldpXml,__eBox,topoXmlVo._dcName,node,areaG);
						}
					}
				});
			});
		}
	}*/
	
	/**进入单个DC视图
	 * cordinateFlag = true
	 * @param dcName:要进入的dc名字,默认为dc1
	 */
	public function goToDcByXY(dcName:String = "dc1"):void
	{
		SdncUtil.currentDcName = dcName;
		var topoXmlVo:TopoXmlVo = SdncUtil.dcTopoXmlDic[dcName];
		var dc:XML = topoXmlVo._l2topoXml;
		var cuBox:ElementBox;
		if(dc)
		{
			var elementBox:ElementBox = SdncUtil.dataBoxsDic[dcName];
			if(elementBox == null)
			{
				cuBox = new ElementBox;
				SdncUtil.dataBoxsDic[dcName] = cuBox;
				VMMoveUtil.getInstence().initGoToFlagValue(cuBox,dcName);
				for each(var lineXml:XML in dc.l2topolinks.l2topolink)
				{
					var fromN:Node = TopoUtil.getOrCreateNodeById(cuBox, dc, lineXml.leftFPID, dc.l2toponodes.l2toponode);
					var toN:Node = TopoUtil.getOrCreateNodeById(cuBox, dc, lineXml.rigthFPID, dc.l2toponodes.l2toponode);
					if(!(fromN as StateNode).nodeInfo)(fromN as StateNode).nodeInfo = topoXmlVo._fpNodeMap[fromN.id];
					if(!(toN as StateNode).nodeInfo)(toN as StateNode).nodeInfo = topoXmlVo._fpNodeMap[toN.id];
					TopoUtil.createLink(cuBox,fromN,toN);
				}
				cuBox.forEachByBreadthFirst(function(item:IData):void{
					if(item is StateNode)
					{
						if(item.name.search("TOR") != -1)
						{
							createComputerLevel(topoXmlVo._lldpXml,cuBox,dcName,item as StateNode);
						}
					}
				});
			}
			else
			{
				cuBox = elementBox;
			}
			physicsView.network.elementBox = cuBox;
			physicsView.cuNetBox = cuBox;
		}
		else
		{
			trace(dcName + "无数据!");
		}
		
	}
	
	/**处理相应的DC,根据dcName取得相应的XML,然后请求XML里的拓扑文件路径
	 * cordinateFlag = false
	 * @param dcName:相应DC的名字
	 */
	public function goToDcCustom(dcName:String):void
	{
		//		clearCuDCAndXmlData();
		SdncUtil.currentDcName = dcName;
		var xml:XML = SdncUtil.dcMap[dcName];
		//		var cuDcUrlInfo:Array = SdncUtil.dcUrlInfos[dcName];
		var topoXmlVo:TopoXmlVo = SdncUtil.dcTopoXmlDic[dcName];
		if(!topoXmlVo)
		{
			Alert.show("no data!");
			return;
		}
		if(xml.hasOwnProperty("datafile"))
		{
			var l2topoXml:XML = topoXmlVo._l2topoXml;
			var lldpXml:XML = topoXmlVo._lldpXml;
			var vclusterXml:XML = topoXmlVo._vclusterXml;
			onNodeAndLink(l2topoXml,lldpXml,vclusterXml);
		}
	}
	
	/**
	 * 遍历XML找到NODE并创建,然后根据XML里线的关系将NODE连线
	 * @param l2topoXml:dc的拓扑关系XML
	 * @param lldpXml:远程computer的拓扑关系XML
	 * @param vclusterXml:节点的详细信息XML
	 */
	private function onNodeAndLink(l2topoXml:XML,lldpXml:XML,vclusterXml:XML):void
	{
		if(l2topoXml)
		{
			var cuBox:ElementBox;
			var dcName:String = SdncUtil.currentDcName;
			var topoXmlVo:TopoXmlVo = SdncUtil.dcTopoXmlDic[dcName];
			var elementBox:ElementBox = SdncUtil.dataBoxsDic[dcName];
			if(elementBox == null)
			{
				cuBox = new ElementBox;
				SdncUtil.dataBoxsDic[dcName] = cuBox;
				VMMoveUtil.getInstence().initGoToFlagValue(cuBox,dcName);
				var vcFpInfoDic:Dictionary = new Dictionary();
				var nodes:XMLList = l2topoXml.l2toponodes.l2toponode;
				var links:XMLList = l2topoXml.l2topolinks.l2topolink;
				for each(var cuNode:XML in nodes)
				{
					var nodeId:String = dcName + "_" + cuNode.fpID;
					var nodeName:String = cuNode.systemName;
					var nodeType:String = cuNode.nodeType;
					var img:String = SdncUtil.imagesObjects[nodeType];
					var node:Node = TopoUtil.createNode(cuBox,StateNode,nodeId,nodeName,img);
//					transmitG.addChild(node);
					node.setSize(50,53);
					if(node is StateNode && !(node as StateNode).nodeInfo) 
						(node as StateNode).nodeInfo = topoXmlVo._fpNodeMap[node.id];
					if(nodeType == "CORE"){	 __coreArr.push(node); }
					else if(nodeType == "AGG"){ __aggArr.push(node); }
					else if(nodeType == "TOR"){ 
						__torArr.push(node);
						if(lldpXml != null)onLldpXml(cuBox,node,lldpXml,dcName);
					}
				}
				for each(var lineXml:XML in links)
				{
					var fromN:Node = TopoUtil.getNodeById(cuBox, dcName + "_" + lineXml.leftFPID);
					var toN:Node = TopoUtil.getNodeById(cuBox, dcName + "_" + lineXml.rigthFPID);
					var cuLink:Link = TopoUtil.createLink(cuBox,fromN,toN);
					__linkArr.push(cuLink);
				}
				topoDoLayout();
			}
			else
			{
				cuBox = elementBox;
			}
			physicsView.network.elementBox = cuBox;
			physicsView.cuNetBox = cuBox;
		}
	}
	/**根据NODE找到相关联的远程COMPUTER,创建并与NODE连线
	 * @param box:当前写入的box
	 * @param node:与host相连的node
	 * @param xml:host与l2topo的关系XML
	 * @param cuDcName:当前DC名字
	 */
	private function onLldpXml(box:ElementBox,node:Node,xml:XML,cuDcName:String):void
	{
		for each(var lldpIfTopo:XML in xml.lldpIfsTopo.lldpIfTopo)
		{
			if(node.id == cuDcName + "_" + lldpIfTopo.fpID)
			{
				var computerG:Group = TopoUtil.createGroup(box,"sheft"+lldpIfTopo.fpID,"sheft"+lldpIfTopo.fpID,
					SdncUtil.imagesObjects["Computer"],0.8,Consts.SHAPE_ROUNDRECT);
				computerG.setSize(50,53);
				__computerArr.push(computerG);
				var remotes:Array = [];
				for each(var remoteInfo:XML in lldpIfTopo.lldpRemoteInfos.lldpRemoteInfo)
				{
					var remoteN:Node = TopoUtil.createNode(box,ServerNode,cuDcName + "_" + String(remoteInfo.chassisId),remoteInfo.systemName);
					remoteN.setSize(35,38);
					if(remoteN is ServerNode)
					{
						(remoteN as ServerNode)._remoteInfo = remoteInfo;
					}
					computerG.addChild(remoteN);
					var link:Link = TopoUtil.createLink(box,node,remoteN);
					__linkArr.push(link);
					remotes.push(remoteN);
				}
				__remoteArr.push(remotes);
			}
		}
	}
	
	/**
	 * 根据XML创建SEVER层
	 * @param computerLevelXml:远端computer层拓扑关系XML
	 * @param box:当前写入的box
	 * @param parentNode:与server层相连的node
	 * @param area:机房分组,默认为null，在多dc显示状态下需要传入值
	 */
	private function createComputerLevel(computerLevelXml:XML,box:ElementBox,dcname:String,parentNode:Node,area:Group = null):void
	{
		if(computerLevelXml == null)return;
		for each(var nodeXml:XML in computerLevelXml.lldpIfsTopo.lldpIfTopo)
		{
			if(parentNode.id == (dcname + "_" + String(nodeXml.fpID)))
			{
				var shelf:Group = TopoUtil.createGroup(box,dcname + "shelf" + nodeXml.fpID , "shelf",
					SdncUtil.imagesObjects.Computer,0.6,Consts.SHAPE_ROUNDRECT);
				for each(var remoteXml:XML in nodeXml.lldpRemoteInfos.lldpRemoteInfo)
				{
					var romoteNode:Node = TopoUtil.createNode(box,ServerNode,dcname + String(remoteXml.systemName) ,remoteXml.systemName,"");
					romoteNode.setSize(35,38);
					shelf.addChild(romoteNode);
					if(romoteNode is ServerNode)
					{
						(romoteNode as ServerNode)._remoteInfo = remoteXml;
					}
					TopoUtil.createLink(box,parentNode,romoteNode);
				}
				if(area)
				{
					area.addChild(shelf);
				}
				var subNodeW:Number = (shelf.children.getItemAt(0) as Node).width;
				TopoUtil.setGroupChildrenLocation(shelf,subNodeW,parentNode,10,150);
				__computerArr.push(shelf);
				break;
			}
		}
	}
	
	/**
	 * 通过DCXML画线
	 * @param dc:当前DC拓扑关系XML
	 */
	private function CreateLinkByDC(dc:XML,dcName:String):void
	{
		for each(var lineXml:XML in dc.l2topolinks.l2topolink)
		{
			var fromN:Node = TopoUtil.getNodeById(__eBox,dcName + "_" + lineXml.leftFPID);
			var toN:Node = TopoUtil.getNodeById(__eBox,dcName + "_" + lineXml.rigthFPID);
			var cuLink:Link = TopoUtil.createLink(__eBox,fromN,toN);
			cuLink.setClient("leftIfName", String(lineXml.leftIfName));
			cuLink.setClient("rightIfName", String(lineXml.rightIfName));
			cuLink.setClient("leftFPID", String(lineXml.leftFPID));
			cuLink.setClient("rightFPID", String(lineXml.rigthFPID));
		}
	}
	
	/**
	 *清空数组 
	 * 
	 */
	public function initArrary():void
	{
		__coreArr = [];
		__aggArr = [];
		__torArr = [];
		__computerArr = [];
		__linkArr = [];
	}
	
	/**DC平面布局起始Ｘ坐标值 */
	private var __startPointX:int = 350;
	/**DC平面布局起始Ｙ坐标值 */
	private var __startPointY:int = 261;
	/**DC平面布局起始间隔值 */
	private var __dcGap:int = 860;
	/**
	 * 对DC类型的Group进行自动布局,
	 * 将所有的_group放到dcGArr数组中，
	 * 然后根据参照坐标和间隔值进行布局
	 * @param _group:DC组
	 */
	private function groupLayout(dcGArr:Array):void
	{
		var dcNums:int = dcGArr.length; 
		if(dcNums < SdncUtil.dcNameArr.length) return;
		for(var i:int = 0; i < dcNums; i++)
		{
			var cuGroup:Group = dcGArr[i];
			cuGroup.setLocation(__startPointX + __dcGap * i,__startPointY);
		}
	}
	
	/**
	 * 布局
	 */
	public function topoDoLayout():void
	{
		TopoUtil.cycleSetNodeLocation(__coreArr,__xGap,__yGap,__centerP,0);
		TopoUtil.cycleSetNodeLocation(__aggArr,__xGap,__yGap,__centerP,1);
		TopoUtil.cycleSetNodeLocation(__torArr,__xGap,__yGap,__centerP,2);
		TopoUtil.cycleSetNodeLocation(__computerArr,__xGap,__yGap,__centerP,3);
	}
	
	protected function onNodeStateError(event:SdncEvt):void
	{
		physicsView.cuNetBox.forEachByBreadthFirst(function(item:IData):void
		{
			if(item is  StateNode)
			{
				var node:StateNode = item as StateNode;
				if(node.id == event.params)
				{
					node.alarmState.setNewAlarmCount(SdncUtil.CRITICALFAULT,1);
				}
			}
		});
	}
	public function DCDataDealUtil()
	{
	}
}
}