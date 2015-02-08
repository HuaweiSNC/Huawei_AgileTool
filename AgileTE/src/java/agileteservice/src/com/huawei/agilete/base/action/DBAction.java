package com.huawei.agilete.base.action;

import java.util.ArrayList;
import java.util.List;

import com.huawei.agilete.base.dboperate.CassandraOperate;

public class DBAction {

    
    
    /****
     * 向DB中插入数据内容 
     * @param columnFamily 表名 
     * @param key key 查询条件
     * @param domainName 字段名称 
     * @param domain 字段值 
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
    
    /***
     * 从DB中删除指定的数据
     * @param columnFamily 表名
     * @param key  key 查询条件
     * @param ColumnName 字段名称 
     * @return 是否删除成功
     */
    public Boolean delete(String columnFamily,String key,String ColumnName){
        CassandraOperate db = new CassandraOperate();
        if(200 != db.getDbState().getStatusCode()){
            return false;
        }
        Boolean result = db.delete(columnFamily,key,ColumnName);
        return result;
    }
    
    /***
     * 从DB中获取指定的数据内容
     * @param columnFamily 表名
     * @param key  key 查询条件
     * @param ColumnName 字段名称 
     * @return 符合条件的 columnvalue
     */
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
    
    /***
     * 列出指定表的指定KEY的所有值
     * @param columnFamily 表名
     * @param key 查询条件
     * @return 符合对条的列表数据 
     */
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
//    public static void main(String[] args) {
//        DBAction db = new DBAction();
//        List<String[]> list = db.getAll("user","user");
//        System.out.println("sss"+list.size());
//        for(int i=0;i<list.size();i++){
//            System.out.println(list.get(i)[0]+":"+list.get(i)[1]);
//        }
//        //System.out.println();
//        //db.insert("domains","domains", "1", "<domains><domain><name>管理域A</name><type>overte</type><domain></domains>");
//        //System.out.println();
//    }

}
