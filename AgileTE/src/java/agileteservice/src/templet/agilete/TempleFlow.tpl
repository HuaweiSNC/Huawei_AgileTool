#if("0"!="${t_flow.size}")
#if(${interfaceIs}=="1")
<instanceName>$!{t_flow.name}</instanceName>
#end
<instanceType>vpwsLdp</instanceType>
<encapsulateType>vlan</encapsulateType>
<l2vpnAcs>
	<l2vpnAc operation="merge">
	 <interfaceName>$!{t_flow.interfaceName}</interfaceName>
		<tagged>true</tagged>
	</l2vpnAc>
</l2vpnAcs>
<vpwsPws>
	<vpwsPw operation="merge">
		<pwRole>primary</pwRole>
		<pwId>$!{t_flow.identifyIndex}</pwId>
		<peerIp>$!{t_flow.desIp}</peerIp>
		<tnlPolicyName>$!{t_flow.policy}</tnlPolicyName>
		<ctrlWord>enable</ctrlWord>
		<trafficStatisticsEnable>true</trafficStatisticsEnable>
	</vpwsPw>
</vpwsPws>
#end