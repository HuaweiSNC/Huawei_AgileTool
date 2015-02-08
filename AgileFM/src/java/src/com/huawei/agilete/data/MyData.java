package com.huawei.agilete.data;
import com.huawei.agilete.base.common.ReadConfig;
public class MyData {
	private static String CassandraPath;
	private static String CassandraServer;
	private static String CassandraPort;
	private static String CassandraKeyspace;
	
	private static String OpsServerIp;
	private static String OpsServerPort;
	private static String AlarmOpsServerPort;
	private static String OpsUserName;
	private static String OpsPw;
	
	
	private static String OpsUrl;
	
	private static String AlarmServerIp;
	private static String AlarmServerPort;
	
	
	
	public MyData(){
		
		ReadConfig r  =new ReadConfig();
		CassandraPath = r.get().getProperty("CassandraPath");
		CassandraServer = r.get().getProperty("CassandraServer");
		CassandraPort = r.get().getProperty("CassandraPort");
		CassandraKeyspace = r.get().getProperty("CassandraKeyspace");
		OpsUrl = r.get().getProperty("OpsUrl");
		
		OpsServerIp = r.get().getProperty("OpsServerIp");;
		OpsServerPort = r.get().getProperty("OpsServerPort");;
		AlarmOpsServerPort = r.get().getProperty("AlarmOpsServerPort");;
		OpsUserName = r.get().getProperty("OpsUserName");;
		OpsPw = r.get().getProperty("OpsPw");;
		
		AlarmServerIp = r.get().getProperty("AlarmServerIp");;
		AlarmServerPort = r.get().getProperty("AlarmServerPort");;
		
	}

	
	
	public static String getCassandraPath() {
		if(null == CassandraPath){
			ReadConfig r  =new ReadConfig();
			CassandraPath = r.get().getProperty("CassandraPath");
		}
		return CassandraPath;
	}
	public static void setCassandraPath(String cassandraPath) {
		CassandraPath = cassandraPath;
	}
	public static String getCassandraServer() {
		if(null == CassandraServer){
			ReadConfig r  =new ReadConfig();
			CassandraServer = r.get().getProperty("CassandraServer");
		}
		return CassandraServer;
	}
	public static void setCassandraServer(String cassandraServer) {
		CassandraServer = cassandraServer;
	}
	public static String getCassandraPort() {
		if(null == CassandraPort){
			ReadConfig r  =new ReadConfig();
			CassandraPort = r.get().getProperty("CassandraPort");
		}
		return CassandraPort;
	}
	public static void setCassandraPort(String cassandraPort) {
		CassandraPort = cassandraPort;
	}
	public static String getCassandraKeyspace() {
		if(null == CassandraKeyspace){
			ReadConfig r  =new ReadConfig();
			CassandraKeyspace = r.get().getProperty("CassandraKeyspace");
		}
		return CassandraKeyspace;
	}
	public static void setCassandraKeyspace(String cassandraKeyspace) {
		CassandraKeyspace = cassandraKeyspace;
	}
	public static String getOpsUrl() {
		if(null == OpsUrl){
			ReadConfig r  =new ReadConfig();
			OpsUrl = r.get().getProperty("OpsUrl");
		}
		return OpsUrl;
	}
	public static void setOpsUrl(String opsUrl) {
		OpsUrl = opsUrl;
	}



	public static String getAlarmServerIp() {
		if(null == AlarmServerIp){
			ReadConfig r  =new ReadConfig();
			AlarmServerIp = r.get().getProperty("AlarmServerIp");
		}
		return AlarmServerIp;
	}



	public static void setAlarmServerIp(String alarmServerIp) {
		AlarmServerIp = alarmServerIp;
	}



	public static String getAlarmServerPort() {
		if(null == AlarmServerPort){
			ReadConfig r  =new ReadConfig();
			AlarmServerPort = r.get().getProperty("AlarmServerPort");
		}
		return AlarmServerPort;
	}

	public static void setAlarmServerPort(String alarmServerPort) {
		AlarmServerPort = alarmServerPort;
	}



	public static String getOpsServerIp() {
		
		if(null == OpsServerIp){
			ReadConfig r  =new ReadConfig();
			OpsServerIp = r.get().getProperty("OpsServerIp");
		}
		return OpsServerIp;
	}



	public static void setOpsServerIp(String opsServerIp) {
		OpsServerIp = opsServerIp;
	}



	public static String getOpsServerPort() {
		
		if(null == OpsServerPort){
			ReadConfig r  =new ReadConfig();
			OpsServerPort = r.get().getProperty("OpsServerPort");
		}
		return OpsServerPort;
		
	}



	public static void setOpsServerPort(String opsServerPort) {
		OpsServerPort = opsServerPort;
	}





	public static String getAlarmOpsServerPort() {
		if(null == AlarmOpsServerPort){
			ReadConfig r  =new ReadConfig();
			AlarmOpsServerPort = r.get().getProperty("AlarmOpsServerPort");
		}
		return AlarmOpsServerPort;
	}



	public static void setAlarmOpsServerPort(String alarmOpsServerPort) {
		AlarmOpsServerPort = alarmOpsServerPort;
	}



	public static String getOpsUserName() {
		

		if(null == OpsUserName){
			ReadConfig r  =new ReadConfig();
			OpsUserName = r.get().getProperty("OpsUserName");
		}
		return OpsUserName;
		
	}



	public static void setOpsUserName(String opsUserName) {
		OpsUserName = opsUserName;
	}



	public static String getOpsPw() {
		if(null == OpsPw){
			ReadConfig r  =new ReadConfig();
			OpsPw = r.get().getProperty("OpsPw");
		}
		return OpsPw;
	}



	public static void setOpsPw(String opsPw) {
		OpsPw = opsPw;
	}


	
	
	
	
	
	
	
	
	
	
	
	
}
