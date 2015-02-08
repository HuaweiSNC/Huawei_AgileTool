
<nqa>
#foreach($t_flux in ${T_flux})
		<data>
			<schedule>$!{t_flux.time}</schedule>
			<value1>$!{t_flux.rttAverage}</value1>
			<value2>$!{t_flux.loss}</value2>
			<receibveByte>$!{t_flux.receibveByte}</receibveByte>
			<sendByte>$!{t_flux.sendByte}</sendByte>
		</data>
#end
</nqa>
