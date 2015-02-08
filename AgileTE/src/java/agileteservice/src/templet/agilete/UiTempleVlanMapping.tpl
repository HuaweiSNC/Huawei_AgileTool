<vlanmappings>
	#foreach($t_vlanMapping in ${T_VlanMapping})
		<vlanmapping> 
			<interfaceName>$!{t_vlanMapping.interfaceName}</interfaceName>
				<mappingPorts>
				#foreach($mapping in $!{t_vlanMapping.mappingPortList})
					<mappingPort>
						<mappingVid>$!{mapping.mappingVid}</mappingVid>
						<outerVlansNew>
								<vlansIndex></vlansIndex>
						</outerVlansNew>
						<internalVlansNew>
								<vlansIndex>$!{mapping.internalVlansNew}</vlansIndex>
						</internalVlansNew>
					</mappingPort>
					#end
				</mappingPorts>
		</vlanmapping>
	#end
</vlanmappings>
