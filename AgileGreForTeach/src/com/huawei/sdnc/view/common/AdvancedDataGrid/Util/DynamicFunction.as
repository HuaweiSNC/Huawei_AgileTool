package com.huawei.sdnc.view.common.AdvancedDataGrid.Util
{
	import mx.charts.ChartItem;
	import mx.charts.HitData;
	import mx.charts.chartClasses.Series;
	import mx.controls.advancedDataGridClasses.AdvancedDataGridColumn;
	import mx.formatters.NumberFormatter;
	import mx.formatters.SwitchSymbolFormatter;
	
	public class DynamicFunction
	{
		public function DynamicFunction()
		{
		}
		
		public function getFunction(funcName:String):Function{
			switch(funcName.toLowerCase()){
				case "tointcomma":
					return toIntComma;
				break;
				case "todotnumcomma":
					return toDotNumComma;
				break;
			}
			return null;
		}
		
        
        // ------------------ for DataGrid LabelFunctions ------------------------------------
        public function toIntComma(item:Object, column:AdvancedDataGridColumn):String { // 소숫점 없애고 천자리 콤마
        	var ret:String=item[column.dataField];
        	var percent:Boolean=false;
        	var temp:Number;
        	
        	if(ret.indexOf("%")>-1){
        		ret=ret.replace("%","");
        		percent=true;
        	}
        	
        	temp=Number(ret);
        	percent?ret=temp+"%":false;
        	
        	return ret;
        }
        
        public function toDotNumComma(item:Object, column:AdvancedDataGridColumn):String { // 소숫점 1자리, 천자리 콤마
        	var checkStr:String=item[column.dataField];
        	var ret:NumberFormatter = new NumberFormatter();
        	var temp:Number;
        	var percent:Boolean=false;
        	
        	if(checkStr.indexOf("%")>-1){
        		checkStr=checkStr.replace("%","");
        		percent=true;
        	}
        	
        	temp=int(Number(checkStr)*10)/10;
        	checkStr=ret.format(temp);
        	percent?checkStr=checkStr+"%":false;
        	
        	return checkStr;
        }
        
	}
}