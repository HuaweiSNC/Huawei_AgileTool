<tasks>
#foreach($t_task in ${T_task})
	<task>
		<sc>
		   <name>$!{t_task.name}</name>
		   <time>$!{t_task.time}</time>
		   <state>$!{t_task.state}</state>
		   <type>$!{t_task.type}</type>
		</sc>
		<request>
			$!{t_task.body}
		</request>
	</task>
#end
</tasks>