package com.huawei.overte.service
{
import com.adobe.net.URI;
import com.huawei.overte.tools.ConnUtil;
import com.huawei.sdnc.tools.LogUtil;
import com.huawei.sdnc.tools.PopupManagerUtil;
import com.huawei.overte.tools.SdncUtil;
import com.huawei.sdnc.vo.TopoXmlVo;

import flash.events.Event;
import flash.events.IOErrorEvent;
import flash.utils.ByteArray;

import mx.controls.Alert;
import mx.events.CloseEvent;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.ResultEvent;

import org.httpclient.HttpClient;
import org.httpclient.HttpRequest;
import org.httpclient.events.HttpDataEvent;
import org.httpclient.events.HttpErrorEvent;
import org.httpclient.events.HttpResponseEvent;
import org.httpclient.http.Get;
import org.httpclient.http.Post;

/**
 * SDN基础数据请求类
 */
public class SdnService
{
	private var __topoXmlVo:TopoXmlVo;
	private var projectType:String;
	private var __l2topoUrl:String;
	private var __l3topoUrl:String;
	
	[Bindable]
	public var logUtil:LogUtil = LogUtil.getInstence();
	[Bindable]
	public var connUtil:ConnUtil = ConnUtil.getInstence();
	
	public function SdnService()
	{
		projectType = SdncUtil.cuProjectType;
	}
	/**
	 * 查询l3topo
	 */
	public function l3topoInfoQuery(url:String):void
	{
		switch(projectType)
		{
			case "test":
				onQuery(url,onResultTopo13,onFault);
				break;
			/*case "normal":
				onClientQuery(url,onClientResultL3topo,onClientFault);
				__l3topoUrl=url;
				break;*/
		}
	}
	public function onClientResultL3topo(e:HttpResponseEvent,data:String):void
	{
		if(data.toString().search("<html>") != -1)
		{
			Alert.show("Internal Server Error")
			return;
		}
		var str:String = data.toString();
		var xml:XML = XML(str);
		xml = XML(delNs(data));
		if(!xml.hasOwnProperty("sdnl3topo"))
		{
			Alert.show("no data! retry?!","Tip",Alert.YES|Alert.NO,null,reQueryL3topo);
			return;
		}
		/*var xml:XML = XML(data.toString());
		var ns:Namespace = new Namespace("http://www.huawei.com/netconf/vrp");*/
		//		str = str.replace(" xmlns=\"http://www.huawei.com/netconf/vrp\" xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\" format-version=\"1.0\" content-version=\"1.0\">",">");
		__topoXmlVo._l3topoXml =xml.sdnl3topo[0];
	}
	
	private function reQueryL3topo(e:CloseEvent):void
	{
		if(e.detail == Alert.YES)
		{
			onClientQuery(__l3topoUrl,onClientResultL3topo,onClientFault);
		}
	}
	
	private function onResultTopo13(e:ResultEvent):void
	{
		__topoXmlVo._l3topoXml = e.result.sdnl3topo[0];
	}
	
	/**读取l2拓扑关系XML
	 * @param url:XML地址
	 */
	public function topo12InfoQuery(url:String):void
	{
		if(projectType == "normal")
		{
			logUtil.recordRequest("l2topo",url,ConnUtil.METHOD_GET);
			onClientQuery(url,onClientResultTopol2,function(e:HttpErrorEvent):void{
				logUtil.recordResult("l2topo",e.text);
				Alert.show("Request l2topo is " + e.text);
				PopupManagerUtil.getInstence().closeLoading();
			});
			__l2topoUrl = url;
		}
		else
		{
			onQuery(url,onResultTopo12,onFault);
		}
	}
	
	private function onResultTopo12(e:ResultEvent):void
	{
		__topoXmlVo._l2topoXml = e.result.sdnl2topo[0];
	}
	
	private function onClientResultTopol2(e:HttpResponseEvent,data:String):void
	{
		if(data.toString().search("<html>") != -1)
		{
			Alert.show("Internal Server Error,request l2topo is failed!");
			logUtil.recordResult("l2topo","error!");
			PopupManagerUtil.getInstence().closeLoading();
			return;
		}
		var xml:XML = XML(delNs(data));
		if(!xml.hasOwnProperty("sdnl2topo"))
		{
			Alert.show("no data! retry?","Tip",Alert.YES|Alert.NO,null,reQueryL2topo);
			return;
		}
		__topoXmlVo._l2topoXml = xml.sdnl2topo[0];
		logUtil.recordResult("l2topo","success!");
	}
	
	private function reQueryL2topo(e:CloseEvent):void
	{
		if(e.detail == Alert.YES)
		{
			onClientQuery(__l2topoUrl,onClientResultTopol2,onClientFault);
		}
	}
	/**读取server层外部拓扑关系
	 * @param url:XML地址
	 */
	public function lldpQuery(url:String):void
	{
		if(projectType == "normal")
		{
			logUtil.recordRequest("lldp",url,ConnUtil.METHOD_GET);
			onClientQuery(url,onClientResultlldp,function(e:HttpErrorEvent):void{
				logUtil.recordResult("lldp",e.text);
				Alert.show("Request lldp is " + e.text);
			});
		}
		else
		{
			onQuery(url,onResultlldp,onFault);
		}
	}
	
	private function onResultlldp(e:ResultEvent):void
	{
		__topoXmlVo._lldpXml = e.result as XML;
	}
	
	private function onClientResultlldp(e:HttpResponseEvent,data:String):void
	{
		if(data.toString().search("<html>") != -1)
		{
			Alert.show("Internal Server Error,Request lldp is failed!")
			logUtil.recordResult("lldp","error!");
			return;
		}
		__topoXmlVo._lldpXml = XML(delNs(data));
		logUtil.recordResult("lldp","success!");
	}
	/**读取节点具体信息
	 * @param url:XML地址
	 */
	public function vclusterQuery(url:String):void
	{
		if(projectType == "normal")
		{
			logUtil.recordRequest("vcluster",url,ConnUtil.METHOD_GET);
			onClientQuery(url,onClientResultVcluster,function(e:HttpErrorEvent):void{
				logUtil.recordResult("vcluster",e.text);
				Alert.show("Request vcluster is " + e.text);
			});
		}
		else
		{
			onQuery(url,onResultVcluster,onFault);
		}
	}
	
	private function onResultVcluster(e:ResultEvent):void
	{
		__topoXmlVo._vclusterXml = e.result as XML;
	}
	
	private function onClientResultVcluster(e:HttpResponseEvent,data:String):void
	{
		if(data.toString().search("<html>") != -1)
		{
			Alert.show("Internal Server Error,Request vcluster is failed!")
			logUtil.recordResult("vcluster","error!");
			return;
		}
		__topoXmlVo._vclusterXml = XML(delNs(data));
		logUtil.recordResult("vcluster","success!");
	}
	/**读取物理视图叠加虚拟视图信息
	 * @param url:XML地址
	 */
	public function vlantopoQuery(url:String):void
	{
		if(projectType == "normal")
		{
			logUtil.recordRequest("vlantopo",url,ConnUtil.METHOD_GET);
			onClientQuery(url,onClientResultVlantopo,function(e:HttpErrorEvent):void{
				logUtil.recordResult("vlantopo",e.text);
				Alert.show("Request vlantopo is " + e.text);
			});
		}
		else
		{
			onQuery(url,onResultVlantopo,onFault);
		}
	}
	
	private function onResultVlantopo(e:ResultEvent):void
	{
		__topoXmlVo._vlantopoXml = e.result as XML;
	}
	
	private function onClientResultVlantopo(e:HttpResponseEvent,data:String):void
	{
		if(data.toString().search("<html>") != -1)
		{
			Alert.show("Internal Server Error,Request vlantopo is failed!")
			logUtil.recordResult("vlantopo","error!");
			return;
		}
		__topoXmlVo._vlantopoXml = XML(delNs(data));
		logUtil.recordResult("vlantopo","success!");
	}
	/**读取虚拟视图网络信息
	 * @param url:XML地址
	 */
	public function vnmQuery(url:String):void
	{
		if(projectType == "normal")
		{
			logUtil.recordRequest("vnm",url,ConnUtil.METHOD_GET);
			onClientQuery(url,onClientResultVnm,function(e:HttpErrorEvent):void{
				logUtil.recordResult("vnm",e.text);
				Alert.show("Request vnm is " + e.text);
			});
		}
		else
		{
			onQuery(url,onResultVnm,onFault);
		}
	}
	
	private function onResultVnm(e:ResultEvent):void
	{
		__topoXmlVo._vnmXml = e.result as XML;
	}
	
	private function onClientResultVnm(e:HttpResponseEvent,data:String):void
	{
		if(data.toString().search("<html>") != -1)
		{
			Alert.show("Internal Server Error,Request vnm is failed!")
			logUtil.recordResult("vnm","error!");
			return;
		}
		var xml:XML = <networks/>;
		xml.appendChild(XML(delNs(data)).networks.network);
		__topoXmlVo._vnmXml = xml;
		logUtil.recordResult("vnm","success!");
	}
	
	/**读取服务器信息
	 * @param url:XML地址
	 */
	public function instancesInfoQuery(url:String):void
	{
		if(projectType != "test")
		{
			logUtil.recordRequest("instances",url,ConnUtil.METHOD_GET);
			connUtil.sendRequest(url,null,onClientResultInstances,function(e:*):void{
				Alert.show(e.text);
			},connUtil.tokentId);
		}
		else
		{
			onQuery(url,onResultInstances,onFault);
		}
	}
	
	/**混合工程 加入token后才能获取*/
	private function onClientResultInstances(e:HttpDataEvent):void
	{
		var obj:Object = JSON.parse(SdncUtil.convertByteArrayToString(e.bytes)) as Object;
		__topoXmlVo._instancesObj = obj;
		logUtil.recordResult("instances","success!");
	}
	
	private function onResultInstances(e:ResultEvent):void
	{
		__topoXmlVo._instancesXml = e.result as XML;
	}
	
	private function onFault(e:FaultEvent):void
	{
		trace(e.message,"请求失败!");
	}

	private function onClientFault(e:HttpErrorEvent):void
	{
		trace("请求失败!");
	}
	
	public function get _topoXmlVo():TopoXmlVo
	{
		return __topoXmlVo;
	}

	[Bindable]
	public function set _topoXmlVo(value:TopoXmlVo):void
	{
		__topoXmlVo = value;
	}
	/**
	 *处理XML中的命名空间 
	 * @param data 原始数据
	 * @return 返回处理后的数据，为字符串模式
	 */
	private function delNs(data:String):String
	{
		var str:String = data.toString();
		str = str.replace(/xmlns(.*?)="(.*?)"/gm, "");
		return str;
	}
	/**
	 *处理URL请求(使用httpservice) 
	 * @param url URL地址
	 * @param onResult 返回结果
	 * @param onFault 错误处理
	 */
	private function onQuery(url:String,onResult:Function,onFault:Function):void
	{
		connUtil.query(url,onResult,onFault);
	}
	/**
	 * 处理URL请求(使用httpclient)
	 * @param uri:请求的地址
	 * @param onResult:结果处理函数
	 * @param onFault:错误处理函数
	 * @param method:操作方法（get,post,put,del）
	 * @param params:向服务器传递的参数
	 */	
	private function onClientQuery(uri:String,onResult:Function,onFault:Function,method:String = "get",params:String = null):void
	{
		connUtil.clientQuery(uri,method,onResult,onFault,params);
	}
	
	
}
}