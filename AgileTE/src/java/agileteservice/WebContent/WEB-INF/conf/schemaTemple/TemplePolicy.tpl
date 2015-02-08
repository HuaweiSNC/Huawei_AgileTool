<tnlPolicyName>$tnlPolicyName</tnlPolicyName>
<description></description>
<tnlPolicyType>tnlBinding</tnlPolicyType>
<tpNexthops>
	<tpNexthop operation="merge">
		<nexthopIPaddr>$nexthopIPaddr</nexthopIPaddr>
		<downSwitch>false</downSwitch>
		<ignoreDestCheck>false</ignoreDestCheck>
		<tpTunnels>
			<tpTunnel operation="merge">
				<tunnelName>$tunnelName</tunnelName>
			</tpTunnel>
		</tpTunnels>
	</tpNexthop>
</tpNexthops>