package com.huawei.sdnc.tools
{
	
//##################################################################
//#
//#		sdn项目中创建网元的一些静态方法
//#
//##################################################################
	
import com.huawei.sdnc.view.common.node.RefGroup;
import com.huawei.sdnc.view.common.node.StateNode;
import com.huawei.sdnc.view.common.node.VmFollower;

import flash.geom.Point;

import mx.containers.Box;
import mx.events.PropertyChangeEvent;
import mx.formatters.DateFormatter;

import twaver.AlarmSeverity;
import twaver.Consts;
import twaver.ElementBox;
import twaver.Follower;
import twaver.Grid;
import twaver.Group;
import twaver.IData;
import twaver.Link;
import twaver.Node;
import twaver.Styles;

/**
 * 此类为画拓扑图时常用的一些方法
 */
public class TopoUtil
{
	public function TopoUtil()
	{
	}
	/** 处理组展开时的外框形状 */
	public static function handleExpand(e:PropertyChangeEvent):void
	{
		if (e.property == "expanded")
		{
			var g:Group=e.source as Group;
			if (g && g.image == "AREANode")
			{
				if(!g.expanded) g.setStyle(Styles.OUTER_SHAPE,Consts.SHAPE_ROUNDRECT);
				else g.setStyle(Styles.OUTER_SHAPE,Consts.SHAPE_ROUNDRECT);
			}
		}
	}
	/**
	 * 创建Group
	 * @param box:要写入的box
	 * @param id:新建组的id
	 * @param groupName:新建组的名字
	 * @param groupImage:新建组的图标
	 * @param outlineA:新建组的外框透明度
	 * @param shape:新建组的外框形状，默认为oval(Consts.SHAPE_)
	 * @return Group
	 */
	public static function createGroup(box:ElementBox,id:String,groupName:String = "",groupImage:String = "",outlineA:Number = 0.6,shape:String = Consts.SHAPE_ROUNDRECT):Group
	{
		if(box.containsByID(id)){
			return box.getDataByID(id) as RefGroup;
		}
		var g:RefGroup = new RefGroup(id);
		if(groupName != "") g.name = groupName;
		g.setStyle(Styles.GROUP_OUTLINE_ALPHA,outlineA);
		g.setStyle(Styles.GROUP_OUTLINE_COLOR,"0x34f508");
		g.setStyle(Styles.GROUP_FILL_ALPHA,"0.01");
		g.setStyle(Styles.GROUP_SHAPE,shape);
		g.setStyle(Styles.LABEL_COLOR,"0xffffff");
		g.setStyle(Styles.LABEL_SIZE,18);
		g.setStyle(Styles.OUTER_SHAPE,Consts.SHAPE_ROUNDRECT);
		g.setStyle(Styles.SELECT_COLOR,"0xffffff");
		g.setStyle(Styles.SELECT_SHAPE,Consts.SHAPE_ROUNDRECT);
//		g.setStyle(Styles.LABEL_FONT, "sdncFont");
//		g.setStyle(Styles.LABEL_EMBED, true);
//		g.setStyle(Styles.SELECT_STYLE,Consts.SELECT_STYLE_GLOW);
//		g.setStyle(Styles.SELECT_BLURX,8);
//		g.setStyle(Styles.SELECT_BLURY,8);
//		g.setStyle(Styles.SELECT_DISTANCE,64);
		if(groupImage != "") g.image = groupImage;
		box.add(g);
		return g;
	}
	/**
	 * 创建格子框
	 * @param box:要写入的box
	 * @param id:新建格子框的id
	 * @param nodeName:新建格子框的名字
	 * @param imgS:新建格子框的图标
	 * @param point:新建格子框的坐标
	 * @return Grid
	 * 
	 */
	public static function createGrid(box:ElementBox,id:String,nodeName:String = "",imgS:String = "",point:Point = null):Grid
	{
		var grid:Grid = new Grid(id);
		grid.name = nodeName;
		grid.setStyle(Styles.OUTER_COLOR, 0x1b5e98);
		grid.setStyle(Styles.OUTER_WIDTH, 1);
		grid.setStyle(Styles.GRID_BORDER, 18);
		grid.setStyle(Styles.GRID_PADDING, 18);
		grid.setStyle(Styles.GRID_CELL_DEEP, -1);
		grid.setStyle(Styles.GRID_DEEP, 0);
		grid.setStyle(Styles.GRID_COLUMN_COUNT, 8);
		grid.setStyle(Styles.GRID_ROW_COUNT, 3);		
		grid.setStyle(Styles.GRID_FILL_COLOR, 0xe0e0e0);		
		grid.setStyle(Styles.GRID_FILL_ALPHA, 0.15);
		grid.setStyle(Styles.LABEL_COLOR,"0xffffff");
		grid.setStyle(Styles.LABEL_POSITION,Consts.POSITION_TOP);
		grid.setStyle(Styles.LABEL_PADDING_TOP,30);
		grid.setStyle(Styles.LABEL_SIZE,20);
		grid.setSize(680, 270);
//		grid.setStyle(Styles.LABEL_FONT, "sdncFont");
//		grid.setStyle(Styles.LABEL_EMBED, true);
		if(imgS != "") grid.image = imgS;
		if(point) grid.location = point;
		box.add(grid);
		return grid;
	}
	
	/**
	 * 创建一个跟随节点
	 * @param box:要写入的box
	 * @param id:新建节点的id
	 * @param nodeName:新建节点的名字
	 * @param imgS:新建节点的图标
	 * @param point:新建节点的坐标
	 * @return Follower
	 * 
	 */
	public static function createFollower(box:ElementBox,id:String,nodeName:String = "",imgS:String = "",point:Point = null):Follower
	{
		if(box.containsByID(id)){
			return box.getDataByID(id) as Follower;
		}
		var follower:Follower = new VmFollower(id);
		follower.name = nodeName;
		if(imgS != "") follower.image = imgS;
		if(point) follower.location = point;
		follower.setStyle(Styles.LABEL_SIZE,14);
		/*follower.setStyle(Styles.LABEL_FONT, "sdncFont");
		follower.setStyle(Styles.LABEL_EMBED, true);*/
		follower.setStyle(Styles.SELECT_SHAPE,Consts.SHAPE_ROUNDRECT);
		follower.setStyle(Styles.SELECT_COLOR,0xffffff);
		box.add(follower);
		return follower
	}
	
	/**
	 *创建一个接口 
	 * @param box:要写入的box
	 * @param id:新建接口的id
	 * @param nodeName:新建接口的名字
	 * @return Follower
	 * 
	 */
	public static function createPort(box:ElementBox,id:String,nodeName:String = ""):Follower
	{
		if(box.containsByID(id)){
			return box.getDataByID(id) as Follower;
		}
		var node:Follower = new Follower(nodeName);
		node.name = nodeName;
		node.setSize(10, 10);
		node.setStyle(Styles.SELECT_COLOR, 0x999f5c);
		node.setStyle(Styles.CONTENT_TYPE, Consts.CONTENT_TYPE_VECTOR);
		node.setStyle(Styles.VECTOR_SHAPE, Consts.SHAPE_CIRCLE);
		node.setStyle(Styles.VECTOR_GRADIENT, Consts.GRADIENT_RADIAL_NORTHEAST);
		node.setStyle(Styles.VECTOR_FILL_COLOR, 0x5d6134);
		node.setStyle(Styles.SELECT_COLOR,0xffffff);
		node.setStyle(Styles.SELECT_SHAPE,Consts.SHAPE_ROUNDRECT);
		node.setStyle(Styles.LABEL_COLOR,"0xffffff");
		node.setStyle(Styles.LABEL_SIZE,14);
		box.add(node);
		return node;
	}
	
	/**
	 * 新建一个节点
	 * @param box:要写入的box
	 * @param cls:节点的类型(Node,StateNode和ServerNode)
	 * @param id:新建节点的id
	 * @param nodeName:新建节点的名字
	 * @param imgS:新建节点的图标
	 * @param point:新建节点的坐标
	 * @return Node(根据cls参数决定)
	 * 
	 */
	public static function createNode(box:ElementBox,cls:Class,id:String,nodeName:String = "",imgS:String = "", point:Point = null):Node
	{
		if(box.containsByID(id)){
			return box.getDataByID(id) as Node;
		}
		var node:Node = new cls(id);
		if(nodeName != "") node.name = nodeName;
		if(imgS != "") node.image = imgS;
		if(point) node.location = point;
		node.setStyle(Styles.LABEL_COLOR,"0xffffff");
		node.setStyle(Styles.SELECT_COLOR,"0xffffff");
//		node.setStyle(Styles.SELECT_SHAPE,Consts.SHAPE_ROUNDRECT);
//		node.setStyle(Styles.LABEL_FONT, "sdncFont");
//		node.setStyle(Styles.LABEL_EMBED, true);
		box.add(node);
		return node;
	}
	
	/**
	 * 创建连线
	 * @param box:要写入的box
	 * @param fromNode:开始节点
	 * @param toNode:结束节点
	 * @param linkColor:连线颜色
	 * @param selectColor:选中颜色
	 * @return Link
	 * 
	 */
	public static function createLink(box:ElementBox,fromNode:Node,toNode:Node,linkColor:uint = 0x66CCFF,selectColor:uint = 0x00FF00):Link{
		var link:Link = new Link(fromNode, toNode);
		link.setStyle(Styles.LINK_WIDTH, 2);
		link.setStyle(Styles.LINK_BUNDLE_EXPANDED,false);
		link.setStyle(Styles.LINK_TYPE,Consts.LINK_TYPE_PARALLEL);
		link.setStyle(Styles.LINK_BUNDLE_OFFSET,26);
		link.setStyle(Styles.LINK_HANDLER_COLOR,0xffffff);
		link.setStyle(Styles.LINK_COLOR, linkColor);
//		link.setStyle(Styles.LINK_LOOPED_GAP,20);
		/*link.setStyle(Styles.LINK_EXTEND,0);
		link.setStyle(Styles.LINK_TYPE,Consts.LINK_TYPE_FLEXIONAL);*/
		box.add(link);
		return link;
	}
	/**
	 *创建环状连线
	 * @param box:要写入的box
	 * @param fromNode:开始节点
	 * @param toNode:结束节点
	 * @param offset:与节点中心的偏移量
	 * @param alpha:透明度
	 * @param linkColor:连线颜色
	 * @return Link
	 * 
	 */
//	public static function circleLink(box:ElementBox,from:Node,to:Node,linkType:Number=2,linkColor:uint = 0x66CCFF,selectColor:uint = 0x00FF00):Link
//	{
//		if(linkType == 1){
//			var l:Link = new Link(from,to);
//			box.add(l);
//			return l;
//		}
//		var link:CircleLink = new CircleLink(from,to);
//		box.add(link);
//		return link;
//	}
	/**
	 *创建虚线连接 
	 * @param box:要写入的box
	 * @param fromNode:开始节点
	 * @param toNode:结束节点
	 * @param alpha:透明度
	 * @param offset:与节点中心的偏移量
	 * @param linkColor:连线颜色
	 * @return Link
	 * 
	 */
	public static function createLinkDash(box:ElementBox,fromNode:Node,toNode:Node,alpha:Number = 1,offset:int = 20,linkColor:uint = 0x66CCFF):Link{
		var link:Link = new Link(fromNode, toNode);
		link.setStyle(Styles.LINK_ALPHA, alpha);
		link.setStyle(Styles.LINK_WIDTH, 1);
		link.setStyle(Styles.LINK_PATTERN, [3, 2]); 
		if(offset != 20) link.setStyle(Styles.LINK_BUNDLE_OFFSET, offset);
//		link.setStyle(Styles.LINK_TYPE, Consts.LINK_TYPE_PARALLEL);
		link.setStyle(Styles.LINK_COLOR, linkColor);
		box.add(link);
		return link;
	}
	
	/**
	 * 根据ID遍历BOX里存在的NODE并返回该NODE
	 * @param box:要查找的box
	 * @param id:要查找的节点id
	 * @return Node
	 * 
	 */
	public static function getNodeById(box:ElementBox,id:String):Node
	{
		var node:Node = null;
		if(box.containsByID(id)){
			node =  box.getDataByID(id) as Node;
		}
		return node;
	}
	/**
	 * 根据ID遍历BOX里的NODE,如果没有则创建,最后返回该NODE
	 * @param box:要操作的box
	 * @param dc:l2拓扑关系XML
	 * @param idStr:节点的id
	 * @param nodeList:所有节点的XML
	 * @return Node
	 * 
	 */
	public static function getOrCreateNodeById(box:ElementBox,dc:XML,idStr:String,nodeList:XMLList):Node
	{
		var node:Node = null;
		for each(var xml:XML in nodeList)
		{
			if(xml.fpID == idStr)
			{
				node = TopoUtil.createNode(box,StateNode,dc.@controller +"_"+ String(xml.fpID),xml.systemName,
					SdncUtil.imagesObjects[xml.nodeType],new Point(xml.x,xml.y));
			}
		}
		return node;
	}
	
	/**
	 * 循环设置数组内每个节点的坐标
	 * @param xGap:X轴间隔
	 * @param yGap:Y轴间隔
	 * @param centerP:中心点
	 * @param level:节点所在层
	 * @param direct:horizontal或vertical
	 * 
	 * */
	public static function cycleSetNodeLocation(arr:Array,xGap:Number,yGap:Number,centerP:Point,level:int,direct:String = "horizontal"):void
	{
		var length:int = arr.length;
		var cuGap:Number;
		//X轴和Y轴偏移量
		var xExcursion:Number;
		var yExcursion:Number;
		//数组对折后的个数
		var nums:Number = length / 2;
		//是否是偶数
		var isEven:Boolean = length%2 == 0;
		var node:Node = null;
		cuGap = direct == "horizontal" ? xGap : yGap;
		//如果为偶数都向中心偏移当前间隔的1/2
		var pianyi:Number = isEven ? cuGap/2 : 0;
		var cuOffset:Number;
		for( var i:* in arr )
		{
			node = arr[i];
			if(nums >= (i+1)) cuOffset = - cuGap * (i+1) + pianyi;
			else if((i+1-nums) < 1) cuOffset = 0;
			else cuOffset = cuGap * (i+1-nums) - pianyi;
			if(direct == "horizontal")
			{
				xExcursion = cuOffset;
				yExcursion = yGap * level;
//				node.setLocation(centerP.x + cuOffset,centerP.y + yGap * level);
			}
			else
			{
				xExcursion = xGap * level;
				yExcursion = cuOffset;
//			    node.setLocation(centerP.x + xGap * level,centerP.y + cuOffset);
			}
			node.setLocation(centerP.x + xExcursion,centerP.y + yExcursion);
			if(node is Group)
			{
				var subNodeW:Number = (node.children.getItemAt(0) as Node).width;
				setGroupChildrenLocation(node,subNodeW,node);
			}
		}
	}
	
	/**
	 * 设置Group内节点的locatoin;从左到右的顺序
	 * @param node 需要布局的Group
	 * @param subNodeW 每个子节点的宽度
	 * @param referenceN 参考节点（TOR节点）
	 * @param subNodeXGap 子节点X间隔
	 * @param subNodeYGap 子节点与参考节点（TOR）Y轴间隔
	 * 
	 */
	public static function setGroupChildrenLocation(node:Node,subNodeW:Number,referenceN:Node,subNodeXGap:Number = 10,subNodeYGap:Number = 0):void
	{
		var nodeCount:int = node.childrenCount;
		var totalWidth:Number = subNodeW * nodeCount + subNodeXGap * (nodeCount - 1);
		var toX:Number = (referenceN.x+referenceN.width/2) - totalWidth/2;
		var toY:Number = referenceN.y + subNodeYGap;
		node.children.forEach(function(data:IData):void{
			var cuNode:Node = data as Node;
			var index:int = node.children.getItemIndex(data);
			cuNode.setLocation(toX + (subNodeW+subNodeXGap) * index,toY);
		});
	}
}
}