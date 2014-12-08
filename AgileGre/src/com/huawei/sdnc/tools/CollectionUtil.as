package com.huawei.sdnc.tools
{
	import mx.collections.ArrayCollection;

	public class CollectionUtil
	{
		public function CollectionUtil()
		{
		}
		
		public static function convertXmllistToArrayCollection(list:XMLList):ArrayCollection
		{
			var col:ArrayCollection;
			if(list)
			{
				col = new ArrayCollection;
				for(var i:int = 0; i < list.length(); i++)
				{
					var xml:XML = list[i];
					var obj:Object = new Object;
					obj.id = xml.@id;
					obj.label = xml.@label;
					obj.icon = xml.@icon;
					col.addItem(obj);
				}
			}
			return col;
		}
		
	}
}