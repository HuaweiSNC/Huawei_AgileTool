package com.huawei.sdnc.tools
{
	import twaver.Consts;
	import twaver.Utils;

	public class XmlConvert
	{
		[Embed(source="assets/imgs/topo/topo_agg_node.png")]
		public static const img123:Class;
		public function XmlConvert()
		{
		}
		/*转换原始XML 参数为原始XML*/
		static public function convert(originalXml:XML):String
		{
			Utils.registerImageByClass("img123",img123);
			var resultXml:XML;
			resultXml = new XML();
			resultXml=
						<twaver v='3.0.9' p='flex'>
							<dataBox type='twaver.ElementBox'>
								<layerBox>
									<layer name='default' visible='true' editable='true' movable='true'/>
								</layerBox>
							</dataBox>
						</twaver>;
			var num:int = 0;
			/*顶层循环DC分组*/
			var dcXml:XML = 
				<data type='twaver.Group' ref='0'>
				</data>;
			dcXml.@ref = originalXml.@controller;
			var dcStr:String = "<s n='outer.style'>glow</s>";
			dcXml.appendChild(XML(dcStr));
			dcStr = "<s n='group.shape'>" + Consts.SHAPE_ROUNDRECT + "</s>";
			dcXml.appendChild(XML(dcStr));
			/*dcStr = "<s n='group.fill.color'>10524670</s>"
			dcXml.appendChild(XML(dcStr));*/
			dcStr = "<p n='location' x='0' y='0'/>"
			dcXml.appendChild(XML(dcStr));
			dcStr = "<p n='name'><![CDATA[" + originalXml.@controller + "]]></p>"
			dcXml.appendChild(XML(dcStr));
			resultXml.appendChild(dcXml);
			/*转换原始XML中的节点信息*/
			for each(var xml:XML in originalXml.l2toponodes.l2toponode)
			{
				num++;
				var nodeXml:XML = 
					<data type='twaver.Node' ref='0'>
						
					</data>;
				nodeXml.@ref = xml.fpID;
				var nodeStr:String = "<p n='name'><![CDATA[" + xml.systemName + "]]></p>";
				nodeXml.appendChild(XML(nodeStr));
				nodeStr = "<p n='location' x='" + xml.x + "' y='" + xml.y + "'/>";
				nodeXml.appendChild(XML(nodeStr));
				nodeStr = "<p n='image'>img123</p>"
				nodeXml.appendChild(XML(nodeStr));
				nodeStr = "<p n='parent' ref='" + originalXml.@controller + "'/>"
				nodeXml.appendChild(XML(nodeStr));
				resultXml.appendChild(nodeXml);
			}
			/*转换原始XML中的link信息*/
			for each(var oriLinkXml:XML in originalXml.l2topolinks.l2topolink)
			{
				num++;
				var linkXml:XML=
					<data type='twaver.Link' ref='2'>
					</data>
				linkXml.@ref = num;
				var linkStr:String = "<p n='name'><![CDATA[" + oriLinkXml.leftSystemName + "-" + oriLinkXml.rightSystemName + "]]></p>"
				linkXml.appendChild(XML(linkStr));
				linkStr = "<p n='fromNode' ref='" + oriLinkXml.leftFPID + "'/>";
				linkXml.appendChild(XML(linkStr));
				linkStr = "<p n='toNode' ref='" + oriLinkXml.rigthFPID + "'/>";
				linkXml.appendChild(XML(linkStr));
				linkStr = "<p n='parent' ref='" + originalXml.@controller + "'/>";
				linkXml.appendChild(XML(linkStr));
				linkStr = "<s n='link.color'>" + oriLinkXml.color + "</s>"
				linkXml.appendChild(XML(linkStr));
				resultXml.appendChild(linkXml);
			}
			
			/*分组处理*/
			/*for(var j:int = 0;j < 2;j++)
			{
				var groupXml:XML=
					<data type='twaver.Group' ref='g1' id='group'>
					</data>
				var str7:String = "<s n='group.shape'>oval</s>"
					groupXml.appendChild(XML(str7));
					str7 = "<s n='outer.shape'>oval</s>";
					groupXml.appendChild(XML(str7));
					str7 ="<p n='location' x='100' y='100'/>";
					groupXml.appendChild(XML(str7));
					resultXml.appendChild(groupXml);
			}*/
			var resultStr:String = resultXml.toString();
			var strLen:int = resultStr.length;
			for(var i:int = 0; i<strLen; i++)
			{
				resultStr = resultStr.replace('"',"'");
			}
			return resultStr;
		}
	}
}