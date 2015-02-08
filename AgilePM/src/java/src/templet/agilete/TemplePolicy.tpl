#if("0"!="${t_policy.size}")
<tnlPolicyName>$!{t_policy.name}</tnlPolicyName>
<description></description>
<tnlPolicyType>tnlBinding</tnlPolicyType>
<tpNexthops>
	<tpNexthop operation="merge">
		<nexthopIPaddr>$!{t_policy.ip}</nexthopIPaddr>
		<downSwitch>false</downSwitch>
		<ignoreDestCheck>false</ignoreDestCheck>
		<tpTunnels>
			<tpTunnel operation="merge">
				<tunnelName>$!{t_policy.tunnelName}</tunnelName>
			</tpTunnel>
		</tpTunnels>
	</tpNexthop>
</tpNexthops>
#end