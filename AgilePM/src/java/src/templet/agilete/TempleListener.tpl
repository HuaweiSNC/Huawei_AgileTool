<listenerData>
	<id>$!{id}</id>
	#foreach($templetModel in ${T_TempletModel})
	<$!{templetModel.name}><![CDATA[$!{templetModel.value}]]></$!{templetModel.name}>
	#end
</listenerData>