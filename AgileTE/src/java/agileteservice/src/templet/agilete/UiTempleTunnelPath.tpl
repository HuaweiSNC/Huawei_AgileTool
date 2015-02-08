<paths>
		#foreach($paths in ${T_TunnelPath})
			<path>
				<name>$!{paths.name}</name>
				<pathType>$!{paths.type}</pathType>
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