#foreach($itemPath in ${T_ExplicitPath})
<explicitPathName>$!{itemPath.name}</explicitPathName>
#if("0"!="${itemPath.pathHops.size}")
	<explicitPathHops>
		#foreach($item in ${itemPath.pathHops})
			<explicitPathHop>
				<mplsTunnelHopIndex>$!{item.id}</mplsTunnelHopIndex>
				<mplsTunnelHopType>includeStrict</mplsTunnelHopType>
				<mplsTunnelHopIntType>default</mplsTunnelHopIntType>
				<mplsTunnelHopAddrType>IPV4</mplsTunnelHopAddrType>
				<mplsTunnelHopIpAddr>$!{item.nextIp}</mplsTunnelHopIpAddr>
			</explicitPathHop>
		#end
	</explicitPathHops>
	#end
#end
