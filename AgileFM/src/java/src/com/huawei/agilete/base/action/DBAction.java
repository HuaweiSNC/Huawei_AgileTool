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
		Boolean result = db.insertOrUpdate(columnFamily,key,domainName, domain);
		return result;
	}
	
	public Boolean delete(String columnFamily,String key,String ColumnName){
		CassandraOperate db = new CassandraOperate();
		Boolean result = db.delete(columnFamily,key,ColumnName);
		return result;
	}
	
	public String get(String columnFamily,String key,String ColumnName){
		CassandraOperate db = new CassandraOperate();
		String result = db.getByColumn(columnFamily,key,ColumnName);
		 if(null == result){
			 result = "";
		 }
		return result;
	}
	
	
	
	/**
	 * 
	 * 列名范围查询
	 * 
	 * columnFamily 相当于类名 
	 * key  相当于对象名，类的一个具体实现，表示一行 
	 * stratColumnName  表示开始列名 
	 * endColumnName   表示结束列名 
	 * 注意的是stratColumnName的排序要在endColumnName否则会报错
	 * isReversed    是否倒序  true为倒序 ，false为正序
	 * 
	 *  
	 * */
	public List<String[]> getByColumnNameRange (String columnFamily,String key,String stratColumnName,String endColumnName,boolean isReversed){
		CassandraOperate db = new CassandraOperate();
		List<String[]> result = db.getByColumnNameRange(columnFamily,key,stratColumnName,endColumnName,isReversed);
		 if(null == result){
			 result = new ArrayList<String[]>();
		 }
		return result;
	}
	
	
	
	
/**
 * 
 * 
 * 分页得到数据
 * 
 * 
 * 
 * 
 * */

	public List<String[]> getAllColumnByCondition(String columnFamily,String key,Integer pageNum,Integer limmit,boolean isReversed){
		CassandraOperate db = new CassandraOperate();
		List<String[]> conditionResult = new ArrayList<String[]>();
		List<String[]> result = null;
		try {
			result = db.getAllColumnByKey(columnFamily,key,isReversed);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(null!=result){
			if(pageNum>0&&limmit>0){
				if((pageNum-1)*limmit<=result.size()){
					if(limmit>=result.size()){
						conditionResult = new ArrayList<String[]>(result.subList((pageNum-1)*result.size(),result.size()));
					}else{
						if((pageNum-1)*limmit+limmit+1<=result.size()){
							conditionResult = new ArrayList<String[]>(result.subList((pageNum-1)*limmit,(pageNum-1)*limmit+limmit));
						}else{
							conditionResult = new ArrayList<String[]>(result.subList((pageNum-1)*limmit,result.size()));
						}
					}
				}
			}
			
		}
		return conditionResult;
		
	}
	public List<String[]> getAll(String columnFamily,String key){
		CassandraOperate db = new CassandraOperate();
		 List<String[]> result = db.getAllbyKey(columnFamily,key);
		 if(null == result){
			 result = new ArrayList<String[]>();
		 }
		return result;
	}
	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args){
		DBAction db = new DBAction();
		/*db.insert("alarm","alarm","6", "<domains><domain><name>管理域A</name><type>overte</type><domain></domains>");*/
		System.out.println("add");
		List list = db.getAll("alarm","alarm");
		System.out.println(list.size());
		List<String[]> list2 =(List<String[]>)db.getAllColumnByCondition("alarm","alarm",1,10,true);
		for(String[] strings:list2){
			System.out.println("name:"+strings[0]+"value:"+strings[1]);
		}
		
		
		System.out.println("****************************************8");
		List<String[]> list3 =(List<String[]>)db.getByColumnNameRange("alarm","alarm","1394700885859","1394701224606",false);
		for(String[] strings:list3){
			System.out.println("name:"+strings[0]+"value:"+strings[1]);
		}
		
		
		
		
		
		
	}

}
