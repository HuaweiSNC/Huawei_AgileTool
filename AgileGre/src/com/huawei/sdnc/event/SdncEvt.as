package com.huawei.sdnc.event
{
	import flash.events.Event;

	public class SdncEvt extends Event
	{
		/**模块切换*/
		public static const MODULE_CHANGE:String = "module_change";
		
		/**选中了拓扑图中的某一网元*/
		public static const TOPO_SELECTED_ITEM:String = "topo_selected_item";
		
		/**返回instances结果XML*/
		public static const RESULT_INSTANCES:String = "topo_instances";
		
		/**返回12topo结果XML*/
		public static const RESULT_12TOPO:String = "topo_12topo";
		
		/**返回lldpXml结果XML*/
		public static const RESULT_LLDP:String = "topo_lldp";
		
		/**返回vcluster结果XML*/
		public static const RESULT_VCLUSTER:String = "topo_vcluster";
		
		/**控制视图中切换DC*/
		public static const CTRLVIEW_DC_CHANGE:String = "CtrlView_Dc_Change";
		
		/**控制视图中切换DC*/
		public static const PHYSICSVIEW_DC_CHANGE:String = "PhysicsView_Dc_Change";
		
		/**物理视图中返回到root*/
		public static const PHYSICSVIEW_BACK_TO_ROOT:String = "Physicsview_back_to_root";
		
		/**调整控制视图布局*/
		public static const CTRL_LAYOUT_CHANGE:String = "ctrl_layout_change";
		
		/**虚拟视图中切换VDC*/
		public static const VIRTUALVIEW_VDC_CHANGE:String = "virtualview_vdc_change";
		
		/**点击控制视图虚线*/
		public static const CTRL_DASH_CLICK:String = "ctrl_dash_click";
		
		/**添加或编辑虚拟网络*/
		public static const VIRTUALVIEW_ADDOREDIT_VDC:String = "virtualview_addOrEdit_vdc";
		
		/**弹出增加或编辑VDC窗口*/
		public static const POPUP_ADDOREDIT_VDC_WINDOW:String = "popup_addOrEdit_vdc_window";
		
		/**删除VDC*/
		public static const VIRTUALVIEW_DEL_VDC:String = "virtualview_del_vdc";
		
		/**编辑PORT*/
		public static const VIRTUALVIEW_EDIT_PORT:String = "virtualview_edit_port";
		
		/**删除PORT*/
		public static const VIRTUALVIEW_DEL_PORT:String = "virtualview_del_port";
		
		/**PORT已编辑*/
		public static const VIRTUALVIEW_PORT_CHANGE:String = "virtualview_port_change";
		
		/**切换节点标签显示类型*/
		public static const CHANGE_NODE_LABEL_TYPE:String = "change_node_label_type";
		
		/**切换VM标签显示类型*/
		public static const CHANGE_VM_LABEL_TYPE:String = "change_vm_label_type";
		
		/**弹出创建VDC窗口*/
		public static const POPUP_CREATEVDC_WINDOW:String = "popup_createVdc_window";
		
		/**开始VMPING通测试*/
		public static const PHYSICSVIEW_PING_START:String = "physicsview_ping_start";
		
		/**结束VMPING通测试*/
		public static const PHYSICSVIEW_PING_END:String = "physicsview_ping_end";
		
		/**虚拟视图XML更改*/
		public static const VIRTUAL_VNM_CHANGE:String = "virtual_vnm_change";
		
		/**L2TOPO和vcluster数据已获取到*/
		public static const L2TOPO_DATA_IS_READY:String = "l2topo_data_is_ready";
		
		/**lldp数据已获取到*/
		public static const LLDP_DATA_IS_READY:String = "lldp_data_is_ready";
		
		/**vnm和vlantopo数据已获取到*/
		public static const VNM_VLANTOPO_IS_READY:String = "vnm_vlantopo_is_ready";
		
		/**搜索网元*/
		public static const SEARCH_ELEMENT:String = "search_element";
		
		/**节点运行状态错误*/
		public static const NODE_STATE_ERROE:String = "node_state_error";
		
		/**设置状态开关按钮*/
		public static const SET_SWITCHBTN_STATE:String = "set_switchbtn_state";
		
		/**刷新节点信息*/
		public static const REFRESH_NODES_STATE:String = "refresh_nodes_state";
		
		/**关闭流量监视窗*/
		public static const CLOSE_FLOWMONITOR:String = "close_flowmonitor";
		
		/**打开流量监视窗 */	
		public static const OPEN_FLOWMONITOR:String = "open_flowmonitor";

		
		/**l3topo */	
		public static const L3TOPO_DATA_IS_READY:String = "l3topo_data_is_ready";
		
		/**打开新增节点窗口*/
		public static const OPEN_ADDDEVICE_WINDOW:String = "open_addDevice_window"; 
		
		/**关闭新增节点窗口*/
		public static const CLOSE_ADDDEVICE_WINDOW:String = "close_addDevice_window"; 
		
		/**导入excel文件*/
		public static const IMPORT_EXCEL_FILE:String = "import_excel_file"; 
		
		/**增加一条新node节点的item*/
		public static const ADD_NEW_ITEM_NODE:String = "add_new_item_node"; 
		
		/**保存新增加的node节点*/
		public static const SAVE_NEW_ITEM_NODE:String = "save_new_item_node"; 
		
		/**派发全屏事件*/
		public static const FULL_SCREEN_EVENT:String = "full_screen_event"; 
		
		/**打开测试路径窗口*/
		public static const TEST_PATH:String = "test_path"; 
		
		/**关闭路径窗口*/
		public static const CLOSE_TEST_PATH:String = "close_test_path"; 
		
		/**连接入口和出口*/
		public static const CONNECT_ENTRACE_EXPORT_PATH:String = "connect_entrace_export_path"; 

		/**叠加VDC */		
		public static const OVERLIE_VDC:String = "overlie_vdc";
		public static const CLEAROVERLIE_VDC:String = "clearoverlie_vdc";
		
		/**vm迁移*/		
		public static const VM_MOVE:String = "vm_move";
		
		/**系统刷新*/		
		public static const SYSTEM_REFRESH:String = "system_refresh";
		
		/**打开DCI设置窗口*/
		public static const OPEN_SETUP_DCI_WINDOW:String = "open_setup_dci_window";
		
		/**关闭DCi设置窗口*/
		public static const CLOSE_SETUP_DCI_WINDOW:String = "close_setup_dci_window";
		
		/**打开配置设备窗口*/
		public static const OPEN_SETUP_DEVICES_WINDOW:String = "open_setup_devices_window";
		
		/**打开系统还原列表窗口*/
		public static const OPEN_SYSTEM_ROLLBACK_WINDOW:String = "open_system_rollback_window";
		
		/**关闭配置设备窗口*/
		public static const CLOSE_SETUP_DEVICES_WINDOW:String = "close_setup_devices_window";
		
		/**关闭系统还原列表窗口*/
		public static const CLOSE_SYSTEM_ROLLBACK_WINDOW:String = "close_system_rollback_window";
		
		/**更新topo*/
		public static const REFRESH_DEVICES_TOPO:String="refresh_devices_topo";
		
		/**编辑流*/
		public static const OPEN_FLOW_EDIT:String="open_flow_edit";
		
		/**关闭流定义*/
		public static const CLOSE_FLOW_DEFINE:String="close_flow_define";
		
		/**打开管道定义*/
		public static const OPEN_PIPELINE_DEFINE:String = "open_Pipeline_Define";
		
		/**关闭管道定义*/
		public static const CLOSE_PIPELINE_DEFINE:String = "close_Pipeline_Define";
		
		/**打开流导入*/
		public static const OPEN_FLOW_ENTER:String = "open_flow_enter";
		
		/**关闭流导入窗口*/
		public static const CLOSE_FLOW_ENTER:String = "close_flow_enter";
		
		/**打开路径计算*/
		public static const OPEN_PATH_CALCULATION:String = "open_path_calculation";
		
		/**关闭路径计算*/
		public static const CLOSE_PATH_CALCULATION:String = "close_path_calculation";
		
		/**刷新cpu ram*/
		public static const REFRESH_NODES_CPU_RAM:String = "refresh_nodes_cpu_ram";
		
		/**NQA开关*/
		public static const SWITCH_NQA:String = "refresh_nodes_nqa";
		
		/**FLOW 流量统计开关*/
		public static const SWITCH_FLOW:String = "refresh_flow_statistics";
		
		/**项目加载时显示控制台*/
		public static const OPEN_CONSOLE:String ="OPEN_CONSOLE";
		
		/**关闭控制台*/
		public static const CLOSE_CONSOLE:String ="CLOSE_CONSOLE";
		
		/**打开帮助文档*/
		public static const OPEN_HELP:String ="OPEN_HELP";
		
		/**打开增加点窗口*/
		public static const OPEN_POINT_ADD:String = "open_point_add";
		
		/**打开增加线窗口*/
		public static const OPEN_LINE_ADD:String = "open_line_add";
		
		/**打开增加网规窗口*/
		public static const OPEN_NETRULE_ADD:String = "open_netrule_add";
		
		/**打开设置线窗口*/
		public static const OPEN_LINE_EDIT:String = "open_line_edit";
		
		/**打开设置节点窗口*/
		public static const OPEN_POINT_EDIT:String = "open_point_edit";
		
		/**关闭增加点窗口*/
		public static const CLOSE_POINT_ADD:String = "close_point_add";
		
		/**关闭增加线窗口*/
		public static const CLOSE_LINE_ADD:String = "close_line_add";
		
		/**关闭增加网规窗口*/
		public static const CLOSE_NETRULE_ADD:String = "close_netrule_add";
		
		/**关闭设置线窗口*/
		public static const CLOSE_LINE_EDIT:String = "close_line_edit";
		
		/**关闭设置节点窗口*/
		public static const CLOSE_POINT_EDIT:String = "close_point_edit";
		
		/**清除物理视图中的路径计算结果*/
		public static const CLEAR_PATH:String = "clear_path";
		
		/**监控流量的图表*/
		public static const OPEN_FLOW_CHART:String = "open_flow_chart";
		public var params:*;
		public function SdncEvt(type:String, params:*=null, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			this.params = params;
		}
	}
}