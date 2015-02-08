<flows>
#foreach($t_flow in ${T_Flow})
	<flow> 
        <name>$!{t_flow.name}</name>  
		<identifyIndex>$!{t_flow.identifyIndex}</identifyIndex>
		<desIp>$!{t_flow.desIp}</desIp>
		<policy>$!{t_flow.policy}</policy>
		<isDouleConfig></isDouleConfig>
		<interfaceName>$!{t_flow.interfaceName}</interfaceName>
	</flow>
#end
</flows>