package com.huawei.agilete.northinterface.util;

import java.util.HashMap;
import java.util.Map;

import com.huawei.agilete.northinterface.bean.MrtgModel;

public class ProcessManager {

	public static Map<String, Process> processMap = new HashMap<String,Process>();
	public static Map<String, MrtgModel> mrtgModelMap = new HashMap<String,MrtgModel>();
	
	public static String killProcess(String id){
		Process p = processMap.get(id);
		p.destroy();
		p=null;
		processMap.remove(id);
		return "OK";
	}
	
}
