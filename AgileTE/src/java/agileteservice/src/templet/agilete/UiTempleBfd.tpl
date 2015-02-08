<bfds>
	#foreach($t_bfd in ${T_Bfd})
		<bfd> 
			<name>$!{t_bfd.name}</name>  
			<discLocal>$!{t_bfd.discLocal}</discLocal>
			<discRemote>$!{t_bfd.discRemote}</discRemote>
			<tunnelName>$!{t_bfd.tunnelName}</tunnelName>
			<minTxInterval>$!{t_bfd.minTxInterval}</minTxInterval>
			<minRxInterval>$!{t_bfd.minRxInterval}</minRxInterval>
			<wtrTimerInt>$!{t_bfd.wtrTimerInt}</wtrTimerInt>
			<teBackup>$!{t_bfd.teBackup}</teBackup>
			<adminDown>$!{t_bfd.shutdown}</adminDown>
		</bfd>
	#end
</bfds>
