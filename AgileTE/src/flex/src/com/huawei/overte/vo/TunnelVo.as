package com.huawei.overte.vo
{
	import mx.collections.ArrayCollection;

	public class TunnelVo
	{
		public function TunnelVo()
		{
		}
	
		private var _tunnelName:String;
		private var _interfaceName:String;
		private var _mplsTunnelIndex:int;
		private var _mplsTunnelEgressLSRId:String;
		private var _mplsTunnelIngressLSRId:String;
		
		private var _hotStandbyWtr:int;
		private var _explicitPaths:ArrayCollection;

		public function get tunnelName():String
		{
			return _tunnelName;
		}

		public function set tunnelName(value:String):void
		{
			_tunnelName = value;
		}

		public function get interfaceName():String
		{
			return _interfaceName;
		}

		public function set interfaceName(value:String):void
		{
			_interfaceName = value;
		}

		public function get mplsTunnelIndex():int
		{
			return _mplsTunnelIndex;
		}

		public function set mplsTunnelIndex(value:int):void
		{
			_mplsTunnelIndex = value;
		}

		public function get mplsTunnelEgressLSRId():String
		{
			return _mplsTunnelEgressLSRId;
		}

		public function set mplsTunnelEgressLSRId(value:String):void
		{
			_mplsTunnelEgressLSRId = value;
		}

		public function get mplsTunnelIngressLSRId():String
		{
			return _mplsTunnelIngressLSRId;
		}

		public function set mplsTunnelIngressLSRId(value:String):void
		{
			_mplsTunnelIngressLSRId = value;
		}

		public function get hotStandbyWtr():int
		{
			return _hotStandbyWtr;
		}

		public function set hotStandbyWtr(value:int):void
		{
			_hotStandbyWtr = value;
		}

		public function get explicitPaths():ArrayCollection
		{
			return _explicitPaths;
		}

		public function set explicitPaths(value:ArrayCollection):void
		{
			_explicitPaths = value;
		}

	}
}