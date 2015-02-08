<ifms id='$!{t_deviceId}'>
#foreach($t_ifm in ${T_Ifm})
	<ifm>
        <name>$!{t_ifm.name}</name> 
		<phyType>$!{t_ifm.phyType}</phyType>
		<ips>
			<ip>
				<ipAddress>$!{t_ifm.ipAddress}</ipAddress>	
				<subnetMask>$!{t_ifm.subnetMask}</subnetMask>
			</ip>
		</ips>
	</ifm>
#end
</ifms>
