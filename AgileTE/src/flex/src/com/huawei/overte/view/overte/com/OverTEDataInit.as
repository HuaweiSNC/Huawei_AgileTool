package com.huawei.overte.view.overte.com
{
	import mx.collections.ArrayCollection;

	public class OverTEDataInit
	{
		public function OverTEDataInit()
		{
			OverTEData.ifmInterfaceDatas.addItem({devicename:"PE1",ifIndex:"1",ifName:"GE2/0/0",ifIpAddr2:"10.4.1.1",am4CfgAddr:new ArrayCollection([{ifIpAddr:"10.4.1.1"}])});
			OverTEData.ifmInterfaceDatas.addItem({devicename:"PE1",ifIndex:"2",ifName:"GE1/0/0",ifIpAddr2:"10.3.1.1",am4CfgAddr:new ArrayCollection([{ifIpAddr:"10.3.1.1"}])});
//		 <am4CfgAddrs>
//			<am4CfgAddr>
//				<ifIpAddr>10.3.1.1</ifIpAddr>
//				<subnetMask>255.255.255.30</subnetMask>
//				<addrType>main</addrType>
//			</am4CfgAddr>
//		</am4CfgAddrs>
			
			
			
		}
		
		
	}
}