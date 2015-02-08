#if("0"!="${t_vlanMapping1To1.size}")
<ifName>$!{t_vlanMapping1To1.interfaceName}</ifName>
<l2MapType>Mapping1to1</l2MapType>
#foreach($mapping in $!{t_vlanMapping1To1.mappingPortList})
<mapVid>$!{mapping.mappingVid}</mapVid>
<mapInnerVid></mapInnerVid>
<mappingVid>$!{mapping.internalVlansNew}</mappingVid>
#end
#end