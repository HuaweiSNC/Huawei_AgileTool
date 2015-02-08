package com.huawei.agilete.base.dboperate;

public class DbManager {

	private static DbManager dbManager = null;
	
	private DbManager()
	{
		
	}
	
	/***
	 * 
	 * @return
	 */
	public synchronized static DbManager getInstance()
	{
		
		if (null != dbManager)
		{
			dbManager = new DbManager();
		}
		
		return dbManager;
	}
	
	public IDBOperater getDbOperater()
	{
		CassandraOperate dbOperate = new CassandraOperate();
		
		return (IDBOperater) dbOperate;
	}
	 

	
	
	
}
