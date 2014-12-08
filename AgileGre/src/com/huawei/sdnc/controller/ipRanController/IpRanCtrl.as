package com.huawei.sdnc.controller.ipRanController
{
	import com.as3xls.xls.ExcelFile;
	import com.as3xls.xls.Sheet;
	import com.huawei.sdnc.event.SdncEvt;
	import com.huawei.sdnc.tools.SdncUtil;
	import com.huawei.sdnc.view.IpRan;
	import com.huawei.sdnc.view.common.MenuBtn;
	import com.huawei.sdnc.view.ipRan.physics.AddNewDevice;
	import com.huawei.sdnc.view.ipRan.physics.GradientLinkUI;
	import com.huawei.sdnc.view.ipRan.physics.TestPathWindow;
	import com.huawei.sdnc.view.ipRan.physics.TestPathWindow1;
	
	import flash.display.Graphics;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.geom.Matrix;
	import flash.geom.Point;
	import flash.net.FileFilter;
	import flash.net.FileReference;
	import flash.utils.ByteArray;
	
	import mx.collections.ArrayCollection;
	import mx.collections.ArrayList;
	import mx.controls.Alert;
	
	import spark.components.RadioButton;
	
	import twaver.Collection;
	import twaver.Consts;
	import twaver.ElementBox;
	import twaver.ICollection;
	import twaver.IData;
	import twaver.IElement;
	import twaver.Node;
	import twaver.ShapeNode;
	import twaver.Styles;
	import twaver.core.util.h._ED;
	import twaver.network.Network;
	import twaver.network.interaction.InteractionEvent;
	import twaver.networkx.NetworkX;

	public class IpRanCtrl
	{
		[Bindable]
		public var page:IpRan;
		private var __app:sdncui2;
		private var refExcelFile:FileReference; 
		private var xls:ExcelFile;
		/**记录选项面板开关状态*/
		private var __isShowOptions:Boolean;
		private var addNewDevice:AddNewDevice=null;
		private var testPathWindow:TestPathWindow1=null;
		public function IpRanCtrl()
		{
		}
		public function onInit():void
		{
			__app = SdncUtil.app;
		}
		public function onCreate():void
		{
			__app.addEventListener(SdncEvt.CLOSE_ADDDEVICE_WINDOW,closeAddWindow);
			__app.addEventListener(SdncEvt.IMPORT_EXCEL_FILE,importExcelFile);
			__app.addEventListener(SdncEvt.ADD_NEW_ITEM_NODE,addNewItemNode);
			__app.addEventListener(SdncEvt.OPEN_ADDDEVICE_WINDOW,openAddDeviceWindow);
			__app.addEventListener(SdncEvt.SAVE_NEW_ITEM_NODE,saveNewNodes);
			__app.addEventListener(SdncEvt.TEST_PATH,testPath);
			__app.addEventListener(SdncEvt.CLOSE_TEST_PATH,closeTestPath);
			__app.addEventListener(SdncEvt.CONNECT_ENTRACE_EXPORT_PATH,handleEntraceExportPath);
			
			var viewTypesArr:Array = SdncUtil.viewTypes[SdncUtil.curEntry] as Array;
			var len:int = viewTypesArr.length;
			for(var i:int = 0;i < len;i++)
			{
				var menuBtn:MenuBtn = new MenuBtn();
				menuBtn.id = viewTypesArr[i].name;
				menuBtn.label = viewTypesArr[i].desc;
				menuBtn.width = 155;
				menuBtn.percentHeight = 100;
				menuBtn.addEventListener(MouseEvent.CLICK,page.menuClickHandler);
				page.menuHg.addElementAt(menuBtn,i);
			}
			page.menuHg.getElementAt(0).dispatchEvent(new MouseEvent(MouseEvent.CLICK));
		}
		
		
		public function openAddDeviceWindow(e:SdncEvt):void
		{
			if(addNewDevice!=null)
			{
				return;
			}
			addNewDevice=new AddNewDevice;
			addNewDevice.horizontalCenter="0";
			addNewDevice.verticalCenter="0";
			page.addElement(addNewDevice);
			page.physicsView.mouseChildren=false;
			page.menuHg.mouseChildren=false;
		}
		public function closeAddWindow(e:Event):void
		{
			if(addNewDevice!=null)
			{
				page.removeElement(addNewDevice);
				page.physicsView.mouseChildren=true;
				page.menuHg.mouseChildren=true;
				addNewDevice=null;
			}
		}
		
		public function importExcelFile(e:SdncEvt):void
		{
			refExcelFile=new FileReference();
			//("Excel (*.xlsx,*.xls)", "*.xlsx;*.xls");
			var vExcelBytes:FileFilter = new FileFilter("Excel (*.xls)", "*.xls");
			var allTypes:Array = new Array(vExcelBytes);
			refExcelFile.browse(allTypes);
			refExcelFile.addEventListener(Event.SELECT,onSelected);
			
		}
		public function onSelected(event:Event):void
		{
			var t:String=refExcelFile.type
			//				//得到行
			//				var vRows:uint=vsheet.rows;
			//				//得到列
			//				var vCols:uint=vsheet.cols;
			refExcelFile.load();
			refExcelFile.addEventListener(Event.COMPLETE,function(event:Event):void{
				var data:ByteArray = new ByteArray();
				// 读 bytes放入bytearray变数
				refExcelFile.data.readBytes(data, 0, 0); 
				// Load the Excel file
				xls=new ExcelFile();
				xls.loadFromByteArray(data);
				try{
					var sheet:Sheet = xls.sheets[0];  
				}catch(e:Error){
					Alert.show("请保存为Excel 97-2003工作薄","格式错误");
					return;
				}
				var value:String = sheet.getCell(0, 0).value;
				Alert.show("单元格的值："+value);
			});
		}
		
		public function addNewItemNode(e:SdncEvt):void
		{
			if(addNewDevice!=null){
				addNewDevice.additem();
			}
		}
		/**
		 * 保存新增节点
		 * */
		public function saveNewNodes(e:SdncEvt):void
		{
			var ac:ArrayCollection=addNewDevice.g.dataProvider as ArrayCollection;
			for(var i:int=0;i<ac.length;i++){
				var obj:Object=ac.getItemAt(i);
				
			}
			Alert.show("保存成功");
		}
		/**
		 * 显示或隐藏optionpanel
		 * */
		public function setupBtnClickhandler(e:MouseEvent):void
		{
			if(!__isShowOptions)
			{
				page.ipRanoptionPanel.visible=true;
				page.ipRanoptionPanel.includeInLayout = true;
				__isShowOptions = true;
			}else
			{
				page.ipRanoptionPanel.visible = false;
				page.ipRanoptionPanel.includeInLayout = false;
				__isShowOptions = false;
			}
		}
		/****打开测试路径窗口*/
		private var net:NetworkX;
		public function testPath(e:SdncEvt):void
		{
			if(testPathWindow!=null)
				return;
			testPathWindow=new TestPathWindow1;
//			testPathWindow.left="20";
//			testPathWindow.top="20";
			page.addElement(testPathWindow);
			if(page.physicsView.network.visible==true)
			{
			    net=page.physicsView.network;
			}else
			{
				net=page.physicsView.network_noMap;
			}
			net.addEventListener(MouseEvent.CLICK,handleFunction)
		}
		/**关闭测试路径窗口*/
		public function closeTestPath(e:SdncEvt):void
		{
			if(testPathWindow!=null)
			{
				page.removeElement(testPathWindow);
				testPathWindow=null;
				net.removeEventListener(MouseEvent.CLICK,handleFunction);
			}
		}
		private function handleFunction(e:MouseEvent):void
		{
			var ele:IElement=net.getElementByMouseEvent(e);
				if(ele is Node)
				{
//					var value:String=testPathWindow.churukou.selection.value.toString();
					var value:String=testPathWindow.textInputIndex as String;
					if(value=="0")
					{
						testPathWindow.entrance_textInput.text="entranceNode";
						var al:ArrayList=new ArrayList();
						al.addItem({label:"intertrace",isSelected:"0"});
						al.addItem({label:"intertrace2",isSelected:"0"});
						al.addItem({label:"intertrace3",isSelected:"0"});
						al.addItem({label:"intertrace4",isSelected:"0"});
						al.addItem({label:"intertrace5",isSelected:"0"});
						testPathWindow.dp=al;
					}else
					{
						testPathWindow.export_textInput.text="exportNode";
						var al1:ArrayList=new ArrayList();
						al1.addItem({label:"intertrace",isSelected:"0"});
						al1.addItem({label:"intertrace2",isSelected:"0"});
						testPathWindow.dp2=al1;
					}
				}
		}
		
		private var asg4P:Point;
		private var asg4N:Node=null;
		private var csg1P:Point;
		private var csg2P:Point;
		private var asg3P:Point;
		private var asg2P:Point;
		private var asg1P:Point;
		private var rsgP:Point;
		private var shapeNode:ShapeNode=null;
		
		private var rsgNode:Node;
		private var asg4Node:Node;
		private var csg1Node:Node;
		/**
		 * 绘制新路径
		 * */
		private function handleEntraceExportPath(e:SdncEvt):void
		{
			var eBox:ElementBox=net.elementBox;
			refresh(eBox);
			
//			var gra:Graphics=net.graphics;
//			gra.lineStyle(2,0x4dc000,1);//
//			var c:Point=getControlPonit(rsgP,asg4P);
//			gra.moveTo(rsgP.x,rsgP.y);
//			gra.curveTo(c.x,c.y,asg4P.x,asg4P.y);
//			
//			var c1:Point=getControlPonit(asg4P,csg1P);
//			gra.moveTo(asg4P.x,asg4P.y);
//			gra.curveTo(c1.x,c1.y,csg1P.x,csg1P.y);
			
			
			shapeNode = new ShapeNode();		
			shapeNode.setStyle(Styles.VECTOR_FILL,false);
			shapeNode.setStyle(Styles.VECTOR_OUTLINE_WIDTH,2);
			shapeNode.setStyle(Styles.VECTOR_OUTLINE_COLOR,0xFF0000);
			eBox.add(shapeNode);
			
			
			eBox.forEach(function(data:IData):void{
				if(data is Node)
				{
					var node:Node=data as Node;
					if(node.id=="2")
					{
						rsgP=node.centerLocation;
						rsgNode=node;
					}else if(node.id=="6")
					{
						asg4P=node.centerLocation;
						asg4N=node;
						asg4Node=node;
					}else if(node.id=="7")
					{
						csg1P=node.centerLocation;
						csg1Node=node;
					}
				}
			});
			
			var segments:ICollection=new Collection;
			var shapeNodePoints:ICollection=new Collection;
			shapeNodePoints.addItem(csg1P);
			segments.addItem(Consts.SEGMENT_MOVETO);
			
			
			var points:ICollection = GradientLinkUI.getPoints(csg1Node.centerLocation,asg4Node.centerLocation,1,55);
			
			points.forEach(function(p:Point):void{
				var point:Point = new Point(p.x, p.y + 10);
				shapeNodePoints.addItem(point);
				segments.addItem(Consts.SEGMENT_LINETO);
			});
			
			var points1:ICollection = GradientLinkUI.getPoints(asg4Node.centerLocation,rsgNode.centerLocation,1,55);
			
			points1.forEach(function(p:Point):void{
				var point:Point = new Point(p.x, p.y + 10);
				shapeNodePoints.addItem(point);
				segments.addItem(Consts.SEGMENT_LINETO);
			});
			
			this.shapeNode.segments = segments;
			this.shapeNode.points = shapeNodePoints;
		}
		public function getControlPonit(from:Point,to:Point):Point
		{
			var centerPoint:Point = new Point((from.x + to.x)/2, (from.y + to.y)/2);
			var matrix:Matrix = new Matrix();
			matrix.translate(-centerPoint.x, -centerPoint.y);
			matrix.rotate(getAngle(from, to));
			matrix.translate(centerPoint.x, centerPoint.y);
			var o:Number = 80;
			var d:Number=getDistance(from,to)/200;
			if(from.x<to.x)
			{
				o=-80;
			}
			var controlPoint:Point = matrix.transformPoint(new Point(centerPoint.x, centerPoint.y-o*d));
			return controlPoint;
		}
		private static function getDistance(p1:Point, p2:Point):Number {
			var dx:Number = p2.x - p1.x;
			var dy:Number = p2.y - p1.y;
			return Math.sqrt(dx*dx  + dy*dy);
		}
		
		public static function getAngle(p1:Point, p2:Point):Number {
			if(p1.x == p2.x){
				if(p2.y == p1.y){
					return 0;
				}
				else if(p2.y > p1.y){
					return Math.PI/2;
				}
				else{
					return -Math.PI/2;
				}
			}
			return Math.atan((p2.y - p1.y) / (p2.x - p1.x));
		}
		public function refresh(eBox:ElementBox):void
		{
			eBox.forEach(function(data:IData):void{
				if(data is Node)
				{
					var node:Node=data as Node;
					if(node.id=="2")
					{
						rsgP=node.centerLocation;
					}else if(node.id=="6")
					{
						asg4P=node.centerLocation;
						asg4N=node;
					}else if(node.id=="7")
					{
						csg1P=node.centerLocation;
					}else if(node.id=="8")
					{
						csg2P=node.centerLocation;
					}else if(node.id=="5")
					{
						asg3P=node.centerLocation;
					}else if(node.id=="4")
					{
						asg2P=node.centerLocation;
					}else if(node.id=="3")
					{
						asg1P=node.centerLocation;
					}
				}
				
			});
		}
	}
}