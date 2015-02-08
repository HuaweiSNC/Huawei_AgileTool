<instanceName>$instanceName</instanceName>
<instanceType>vpwsLdp</instanceType>
<encapsulateType>vlan</encapsulateType>
<l2vpnAcs>
	<l2vpnAc operation="merge">
		<interfaceName>$interfaceName</interfaceName>
		<tagged>true</tagged>
		<accessPort>false</accessPort>
	</l2vpnAc>
</l2vpnAcs>
<vpwsPws>
	<vpwsPw operation="merge">
		<pwRole>primary</pwRole>
		<pwId>$pwId</pwId>
		<peerIp>$peerIp</peerIp>
		<tnlPolicyName>$tnlPolicyName</tnlPolicyName>
	</vpwsPw>
</vpwsPws>