#if("0"!="${t_nqa.size}")
#if("tunnel"=="${t_nqa.type}")
<testName>$!{t_nqa.name}</testName>
<tunnelName>$!{t_nqa.tunnelName}</tunnelName>
#end
#if("flow"=="${t_nqa.type}")
<testName>$!{t_nqa.name}</testName>
<pwType>vlan</pwType>
<pwId>$!{t_nqa.tunnelId}</pwId>
<peerIpAddr>$!{t_nqa.peerIp}</peerIpAddr>
<remotePwId></remotePwId>
<remoteIpAddress></remoteIpAddress>
#end
#end