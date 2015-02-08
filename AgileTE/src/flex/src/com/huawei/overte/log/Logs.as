package com.huawei.overte.log
{
	import mx.logging.ILogger;
	import mx.logging.LogEventLevel;
	import mx.logging.LogLogger;
	import mx.logging.targets.TraceTarget;
	import mx.logging.Log;     
	public class Logs
	{
		public  var logger:ILogger;    
		public function Logs()
		{
			logger = new LogLogger("testlog"); 
			var logTarget:TraceTarget = new TraceTarget(); 
			logTarget.filters = ["*"];     
			logTarget.level = LogEventLevel.ALL;     
			logTarget.includeCategory = true;     
			logTarget.includeDate = true;     
			logTarget.includeLevel = true;     
			logTarget.includeTime = true;     
			logTarget.addLogger(logger);     
			Log.addTarget(logTarget);  
		}
		
	}
}