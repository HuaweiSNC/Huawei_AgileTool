<policys>
	#foreach($t_policy in ${T_Policy})
		<policy>
			<name>$!{t_policy.name}</name>
			<tpNexthops>
				<tpNexthop>
					<nexthopIPaddr>$!{t_policy.ip}</nexthopIPaddr>
					<tpTunnels>
						<tpTunnel>
							<tunnelName>$!{t_policy.tunnelName}</tunnelName>
						</tpTunnel>
					</tpTunnels>
				</tpNexthop>
			</tpNexthops>
		</policy>
	#end
</policys>