package com.huawei.agilete.base.action;

import java.util.ArrayList;
import java.util.List;

import com.huawei.agilete.base.dboperate.CassandraOperate;

public class DBAction {

	
	
	/**
	 * 添加或修改
	 * @param domainName
	 * @param domain
	 * @return
	 */
	public Boolean insert(String columnFamily,String key,String domainName,String domain){
		CassandraOperate db = new CassandraOperate();
		if(200 != db.getDbState().getStatusCode()){
			return false;
		}
		Boolean result = db.insertOrUpdate(columnFamily,key,domainName, domain);
		return result;
	}
	
	public Boolean delete(String columnFamily,String key,String ColumnName){
		CassandraOperate db = new CassandraOperate();
		if(200 != db.getDbState().getStatusCode()){
			return false;
		}
		Boolean result = db.delete(columnFamily,key,ColumnName);
		return result;
	}
	
	public String get(String columnFamily,String key,String ColumnName){
		CassandraOperate db = new CassandraOperate();
		if(200 != db.getDbState().getStatusCode()){
			return "";
		}
		String result = db.getByColumn(columnFamily,key,ColumnName);
		 if(null == result){
			 result = "";
		 }
		return result;
	}
	
	public List<String[]> getAll(String columnFamily,String key){
		CassandraOperate db = new CassandraOperate();
		if(200 != db.getDbState().getStatusCode()){
			return  new ArrayList<String[]>();
		}
		 List<String[]> result = db.getAllbyKey(columnFamily,key);
		 if(null == result){
			 result = new ArrayList<String[]>();
		 }
		return result;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DBAction db = new DBAction();
		List<String[]> list = db.getAll("nqa","NQA_tunnel_6_Tunnel1");
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i)[1]);
		}
		System.out.println();
		//db.insert("domains","domains", "1", "<domains><domain><name>管理域A</name><type>overte</type><domain></domains>");
		System.out.println();
	}

}
