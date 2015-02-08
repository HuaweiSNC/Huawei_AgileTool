#if("xml"=="${t_type}")
<devices>
#foreach($t_device in ${T_Device})
<device> 
        <id>$!{t_device.id}</id>
        <deviceName>$!{t_device.deviceName}</deviceName> 
		<ipAddress>$!{t_device.ipAddress}</ipAddress>	
		<deviceTopoIp>$!{t_device.deviceTopoIp}</deviceTopoIp>
		<userName>$!{t_device.userName}</userName>
		<passwd>$!{t_device.passwd}</passwd>
		<version>$!{t_device.version}</version>
		<type>$!{t_device.type}</type>
</device>
#end
</devices>
#end
#if("json"=="${t_type}")
{
#foreach($t_device in ${T_Device})
	           "device":
	           {
	               "devicename": "$!{t_device.deviceName}",
	               "version": "$!{t_device.version}",
	               "productType": "$!{t_device.type}",
	               "passwd": "$!{t_device.passwd}",
	               "ip": "$!{t_device.ipAddress}",
	               "username": "$!{t_device.userName}"
	               #if(!""=="$!{t_device.id}")
	               ,
	               "id": $!{t_device.id}
	               #end
	           }
#end
}
#end