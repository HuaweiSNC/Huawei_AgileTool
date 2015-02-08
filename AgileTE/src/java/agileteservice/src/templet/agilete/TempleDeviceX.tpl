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
		<subdeviceid>$!{t_device.subdeviceid}</subdeviceid>
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
	               #if(""!="$!{t_device.id}")
	               ,
	               "id": "$!{t_device.id}"
	               #end
	               #if(""!="$!{t_device.deviceTopoIp}")
	               ,
	               "subdevices": 
	               [
	               		{
	               		#if(""!="$!{t_device.subdeviceid}")
	               		"id": "$!{t_device.subdeviceid}",
	               		#end
	               		"ip":"$!{t_device.deviceTopoIp}",
	               		"username":"$!{t_device.userName}",
	               		"passwd":"$!{t_device.passwd}",
	               		"port":22
	               		}
	               ]
	               #end
	               
	           }
#end
}
#end