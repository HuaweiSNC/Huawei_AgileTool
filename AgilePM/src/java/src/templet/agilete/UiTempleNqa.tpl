
<nqa>
#foreach($t_nqa in ${T_nqa})
		<data>
			<schedule>$!{t_nqa.time}</schedule>
			<value1>$!{t_nqa.rttAverage}</value1>
			<value2>$!{t_nqa.loss}</value2>
		</data>
#end
</nqa>
