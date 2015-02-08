<vlans>
#foreach($t_vlan in ${T_Vlan})
	<vlan> 
        <name>$!{t_vlan.name}</name>  
		<desc>$!{t_vlan.desc}</desc>
		<index>$!{t_vlan.index}</index>
		<ifName>$!{t_vlan.ifName}</ifName>
		<level>$!{t_vlan.level}</level>
	</vlan>
#end
</vlans>