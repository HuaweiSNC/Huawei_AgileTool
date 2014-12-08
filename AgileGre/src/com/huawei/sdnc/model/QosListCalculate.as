package com.huawei.sdnc.model
{
	import com.huawei.sdnc.model.QosItem;
	
	public class QosListCalculate
	{
		public function QosListCalculate()
		{
		}
		
		public var qosItemArr:Array=[];
		public var qosStartArr:Array=[];
		
		public function getQosItem(qosName:String, qosSrcIp:String, destIp:String): QosItem
		{
	 
			for(var i:int=0;i<qosStartArr.length;i++){
				var qosTwo:QosItem=qosStartArr[i];
				if( qosTwo.qosName == qosName 
					&& qosTwo.qosSrcIp == qosSrcIp 
					&& qosTwo.qosdestIp == destIp
				)
				{
					return qosTwo;
				} 
			}
			
			return null;
		}
  
		public function addQos(data:QosItem):void
		{
			qosItemArr.push(data);
		}
		
		public function calculate():void
		{
			var data:QosItem;
			 
			for(var i:int=0;i<qosItemArr.length;i++){
				var qos_obj:QosItem=qosItemArr[i];
				var qosTmpArr:Array = [];
				data = calculatePreQos(qos_obj, qosTmpArr);
				addStartArr(data);
			}
		}
		
		/**
		 * 计算以当前节点为起始的流列表信息
		 * @param 指定的QOS节点
		 * @return 返回QOS流列表信息
		 * 
		 */
		public function calculateList(data:QosItem):Array
		{
			var qosListArr:Array=[];
			qosListArr.push(data);
			// 递归查找下一个节点信息
			calculateNextQos(data, qosListArr);
			return qosListArr;
		}
		
		private function addStartArr(data:QosItem):void
		{
			// 判断是否已经加入当前节点
			for(var i:int = 0; i < qosStartArr.length; i++){
				var qos_obj:QosItem = qosStartArr[i];
				if(qos_obj == data)
				{
					return ; 
				} 
			}
			qosStartArr.push(data);
		}
		
		/**
		 * 计算最上层的节点信息
		 * @param 指定的QOS节点
		 * @return 返回最上层的QOS节点信息
		 * 
		 */
		public function calculatePreQos(qosIt:QosItem, qosTmpArr:Array):QosItem
		{
			var isFind:Boolean = false;
			var retQosItem:QosItem = qosIt;
//			trace(qosIt.qosdestAddress+"qosIt");
			for(var i:int=0;i<qosItemArr.length;i++){
				
				var qos_obj:QosItem=qosItemArr[i];
				// 如果有目标地址为当前输入的Ip地址，并且流名称相同。
				if(detectPreQos(qosIt, qos_obj))
				{
					isFind = true;
					retQosItem = qos_obj;
					// 破解环结构的依赖
					if (isbreakCirle(qosTmpArr, retQosItem))
					{
						return retQosItem;
					}
					qosTmpArr.push(retQosItem);
					break;
				} 
			}
			
			// 如果已经找到上个节点，递归查找是否还有其它的上游节点
			if (isFind == true)
			{
				return calculatePreQos(retQosItem, qosTmpArr);
			}
			
			// 如果没有上游节点，直接返回当前的节点，认为是最上层的节点
			return retQosItem;
		}
		
		/**
		 * 破解环形结构
		 * @param 指定的QOS节点
		 * @return 返回最上层的QOS节点信息
		 * 
		 */
		private function isbreakCirle(qosListArr:Array, qosIt:QosItem):Boolean
		{
			
			for each(var qosItem:QosItem in qosListArr){
				if(qosItem==qosIt){
					return true;
				}
			}
			
			return false;
		}
		
		/**
		 * 计算最上层的节点信息
		 * @param 指定的QOS节点
		 * @return 返回最上层的QOS节点信息
		 * 
		 */
		public function calculateNextQos(qosIt:QosItem, qosListArr:Array):void
		{
			var isFind:Boolean = false;
			var retQosItem:QosItem = qosIt;
			
			for(var i:int=0;i<qosItemArr.length;i++){
				
				var qos_obj:QosItem=qosItemArr[i];
				// 如果有目标地址为当前的下一层，并且流名称相同。
				if(detectNextQos(qosIt, qos_obj))
				{
					isFind = true;
					retQosItem = qos_obj;
					// 破解环结构的依赖
					if (isbreakCirle(qosListArr, retQosItem))
					{
						return;
					}
					qosListArr.push(retQosItem);
					
					break;
				} 
			}
			
			// 如果已经找到下个节点，递归查找是否还有其它的下游节点
			if (isFind == true)
			{
				calculateNextQos(retQosItem, qosListArr);
			}
 
		}
		
		
		/**
		 * 判断第二个流是否是当前流的上层
		 * @param 指定的当前的QOS节点
		 * @param 指定的其它的QOS节点
		 * @return 是否是上层流
		 * 
		 */
		private function detectPreQos(qosCurrent:QosItem, qosTwo:QosItem):Boolean
		{
			if(qosCurrent == qosTwo)
			{
				return false;
			}
			if(qosTwo.qosdestAddress == qosCurrent.qosSrcAddress 
				&& qosTwo.qosName == qosCurrent.qosName && qosTwo.qosSrcIp == qosCurrent.qosSrcIp 
				&& qosTwo.qosdestIp == qosCurrent.qosdestIp)
			{
				return true;
			}
			return false;
		}
		
		/**
		 * 判断第二个流是否是当前流的上层
		 * @param 指定的当前的QOS节点
		 * @param 指定的其它的QOS节点
		 * @return 是否是上层流
		 */
		private function detectNextQos(qosCurrent:QosItem, qosTwo:QosItem):Boolean
		{
			if(qosCurrent == qosTwo)
			{
				return false;
			}
			if(qosTwo.qosSrcAddress == qosCurrent.qosdestAddress
				&& qosTwo.qosName == qosCurrent.qosName 
				&& qosTwo.qosSrcIp == qosCurrent.qosSrcIp 
				&& qosTwo.qosdestIp == qosCurrent.qosdestIp)
			{
				return true;
			}
			return false;
		}
		
	}

}