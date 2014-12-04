package com.huawei.sdnc.model.acl
{
	public class AclGroup
	{
		public function AclGroup()
		{
		}
		public var aclNumOrName:String;
		public var aclStep:String;
		public var aclDescription:String;
		public var aclType:String;
		public var aclRuleAdv4s:Array=[];
	}
}