<tunnels>
#foreach($t_tunnel in ${T_Tunnel})
	<tunnel> 
        <name>$!{t_tunnel.name}</name>
		<interfaceName>$!{t_tunnel.interfaceName}</interfaceName>
		<identifyIndex>$!{t_tunnel.identifyIndex}</identifyIndex>
		<ingressIp>$!{t_tunnel.ingressIp}</ingressIp>
		<egressIp>$!{t_tunnel.egressIp}</egressIp>
		<hotStandbyTime>$!{t_tunnel.hotStandbyTime}</hotStandbyTime>
		<isDouleConfig></isDouleConfig>
		<desDeviceName></desDeviceName>
		<state>$!{t_tunnel.state}</state>
		#if("0"!="${t_tunnel.tunnelPaths.size}")
		<tunnelPaths>
		#foreach($tunnelPaths in ${t_tunnel.tunnelPaths})
			<tunnelPath>
				<pathType>$!{tunnelPaths.pathType}</pathType>
				<pathName>$!{tunnelPaths.pathName}</pathName>
			</tunnelPath>
			#end
		</tunnelPaths>
		#end
		#if("0"!="${t_tunnel.paths.size}")
		<paths>
		#foreach($paths in ${t_tunnel.paths})
			<path>
				<name>$!{paths.name}</name>
				#if("0"!="${paths.pathHops.size}")
				<nextHops>
				#foreach($pathHops in ${paths.pathHops})
					<nextHop>
						<id>$!{pathHops.id}</id>
						<nextIp>$!{pathHops.nextIp}</nextIp>
					</nextHop>
				#end
				</nextHops>
				#end
			</path>
			#end
		</paths>
		#end
	</tunnel>
#end
</tunnels>