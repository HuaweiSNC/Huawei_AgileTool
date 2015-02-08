<qoss>
#foreach($t_qos in ${T_qos})
	<qos>
		<level>$!{t_qos.baValue}</level>
		<value>$!{t_qos.name}</value>
	</qos>
#end
</qoss>