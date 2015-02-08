<qosVlanDs>
#foreach($t_oTQosVlanDs in ${T_oTQosVlanDs})
	<qosVlan> 
       <vlanId>$!{t_oTQosVlanDs.vlanId}</vlanId>
       <name>$!{t_oTQosVlanDs.name}</name>
	</qosVlan>
	#end
</qosVlanDs>