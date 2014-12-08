package com.huawei.sdnc.view.common.node
{
	import com.huawei.sdnc.view.common.nodeui.RefNodeUI;
	
	import twaver.Node;

public class ServerNode extends RefNode
{
	private var __remoteInfo:XML;
	private var __nodeType:String;
	
	public function ServerNode(id:Object=null)
	{
		super(id);
		image = "topo_server";
		__nodeType = "host";
	}

	public function get _remoteInfo():XML
	{
		return __remoteInfo;
	}

	public function set _remoteInfo(value:XML):void
	{
		__remoteInfo = value;
	}

	/**host为主机节点,vm为虚拟机节点*/
	public function get _nodeType():String
	{
		return __nodeType;
	}

	public function set _nodeType(value:String):void
	{
		__nodeType = value;
	}

}
}