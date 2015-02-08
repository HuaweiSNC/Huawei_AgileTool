package com.huawei.agilete.base.dboperate;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.auth.IAuthenticator;
import org.apache.cassandra.thrift.AuthenticationRequest;
import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.huawei.agilete.data.MyData;
import com.huawei.networkos.ops.response.RetRpc;


public class CassandraOperate {

    private  Cassandra.Client cassandraClient;
    private  TTransport socket;
    
    private RetRpc dbState = new RetRpc();
    //private String columnFamily = "overte";//"overte";
    //private String key = "domains";
    public CassandraOperate(){
        init(MyData.getCassandraKeyspace());
    }

    private Boolean init(String keySpace) {
		Boolean result = true;
		try {

			String server = MyData.getCassandraServer();
			String userName = MyData.getCassandraUserName();
			String passWd = MyData.getCassandraPassWd();
			int port = Integer.valueOf(MyData.getCassandraPort());
			// 首先指定的地址
			socket = new TSocket(server, port);
			TFramedTransport transport = new TFramedTransport(socket);
			transport.open();
			// 指定通信协议为二进制流协议
			TBinaryProtocol binaryProtocol = new TBinaryProtocol(transport);
			cassandraClient = new Cassandra.Client(binaryProtocol);
			if (userName != null && passWd != null) {
				Map<String, String> credentials = new HashMap<String, String>();
				credentials.put(IAuthenticator.USERNAME_KEY, userName);
				credentials.put(IAuthenticator.PASSWORD_KEY, passWd);
				AuthenticationRequest authenticationRequest = new AuthenticationRequest(
						credentials);
				cassandraClient.login(authenticationRequest);
			}
			cassandraClient.set_keyspace(keySpace);
		} catch (Exception e1) {
			e1.printStackTrace();
			result = false;
			dbState.setStatusCode(500);
			dbState.setContent("init failure");
		}

		return result;
	}

    /*private Boolean init(String keySpace) {
        Boolean result = true;
        try{

            String server = MyData.getCassandraServer();
            int port = Integer.valueOf(MyData.getCassandraPort());
             首先指定的地址 
            socket = new TSocket(server, port);
            TFramedTransport transport = new TFramedTransport(socket);
             指定通信协议为二进制流协议 
            TBinaryProtocol binaryProtocol = new TBinaryProtocol(transport);
            cassandraClient = new Cassandra.Client(binaryProtocol);
             建立通信连接 
//            open(keySpace);
            socket.open();
            cassandraClient.set_keyspace(keySpace);
        }catch(Exception e1){
            result = false;
            dbState.setStatusCode(500);
            dbState.setContent("init failure");
        }

        return result;
    }*/
    
    public Boolean open(String keySpace) {
        Boolean result = true;
            try {
                if(!socket.isOpen()){
                    socket.open();
                    cassandraClient.set_keyspace(keySpace);
                }
            } catch (Exception e) {
                result = false;
                dbState.setStatusCode(500);
                dbState.setContent(e.getMessage());
                //e.printStackTrace();
            }

        return result;
    }



    /***
     * 
     * @param columnFamily
     * @param key
     * @param ColumnName
     * @param ColumnValue
     * @return
     */
    public Boolean insertOrUpdate(String columnFamily,String key,String ColumnName,String ColumnValue){
        Boolean result = true;
        ColumnParent parent = new ColumnParent(columnFamily);
        Column col = new Column(stringToByteBuffer(ColumnName));
        col.setValue(stringToByteBuffer(ColumnValue));
        col.setTimestamp(System.currentTimeMillis());
        try{
            cassandraClient.insert(    stringToByteBuffer(key),parent,col,ConsistencyLevel.ONE);
        }catch(Exception e){
            result = false;
            dbState.setStatusCode(500);
            dbState.setContent(e.getMessage());
        }finally{
            close();
        }
        return result;
    }

    /**
     * 删除记录
     */
    public Boolean delete(String columnFamily,String key,String ColumnName){
        Boolean result = true;
        ColumnPath col = new ColumnPath(columnFamily);
        col.setColumn(stringToByteBuffer(ColumnName));
        try{
            cassandraClient.remove(    stringToByteBuffer(key),col,System.currentTimeMillis(),ConsistencyLevel.ONE);
        }catch(Exception e){
            dbState.setStatusCode(500);
            dbState.setContent(e.getMessage());
            result = false;
        }finally{
            close();
        }
        return result;
    }

    /**
     * 获取数据
     */
    public String getByColumn(String columnFamily,String key,String ColumnName){
        String result = "";
        try{
            ColumnPath col = new ColumnPath(columnFamily);
            col.setColumn(stringToByteBuffer(ColumnName));
            //ColumnOrSuperColumn superColumn = cassandraClient.get(StringtoByteBuffer(key),col,ConsistencyLevel.ONE);
            Column column = cassandraClient.get(stringToByteBuffer(key),col,ConsistencyLevel.ONE).column;
            result = byteBufferToString(column.value);
        }catch(Exception e){
            dbState.setStatusCode(500);
            dbState.setContent(e.getMessage());
            return null;
        }finally{
            close();
        }
        return result;
    }

    public List<String[]> getAllbyKey(String columnFamily,String key){
        List<String[]>result = null;
        try{
            result = new ArrayList<String[]>();
            SlicePredicate predicate = new SlicePredicate();
            SliceRange sliceRange = new SliceRange();
            sliceRange.start = stringToByteBuffer("");
            sliceRange.finish = stringToByteBuffer("");
            sliceRange.reversed = false;
            //SliceRange sliceRange = new SliceRange(toByteBuffer(""), toByteBuffer(""), false, 0);
            predicate.setSlice_range(sliceRange);
            ColumnParent parent = new ColumnParent(columnFamily);
            List<ColumnOrSuperColumn> results = cassandraClient.get_slice(stringToByteBuffer(key), parent, predicate, ConsistencyLevel.ONE);
            //            List<ColumnOrSuperColumn> results = 
            //                    cassandraClient.get_range_slices( parent, predicate, ConsistencyLevel.ONE);

            for (ColumnOrSuperColumn r : results)
            {
                Column column = r.column;
                result.add(new String[]{byteBufferToString(column.name),byteBufferToString(column.value)});
            }
        }catch(Exception e){
            result = null;
            dbState.setStatusCode(500);
            dbState.setContent(e.getMessage());
        }finally{
            close();
        }
        return result;
    }

    /**
     * 关闭当前的远程访问连接
     */
    public void close() {
        socket.close();
    }

    //转Byte
    private ByteBuffer stringToByteBuffer(String value)
    {
        try{
            return ByteBuffer.wrap(value.getBytes("UTF-8"));
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    //得到字符串
    private String byteBufferToString(ByteBuffer buffer)
    {
        try{
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            return new String(bytes, "UTF-8");
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    private void test(){
        /* 选择需要操作的, 可以理解成数据库 */
        //String keyspace = "demo";

        //        /* 初始化连接 */
        //        init(keyspace);

        /* 创建一个表名*/
        //String columnFamily = "user007";

        /* KEY的名字 */
        //String tablename = "z64175";
        //CassandraOperate.insertOrUpdate("overte","","domain2", "<ColumnValue>");

        /* 获取一条记录 (由于插入和删除是同一条记录,有可能会检索不到哦!请大家主意! */
//        Column column = CassandraOperate.getInstance().getByColumn("domain1");
//        //System.out.println();
        //                CassandraOperate.getInstance().getAllbyKey();
        //                //System.out.println("read Table " + columnFamily);
        //                //System.out.println("read column name " + ":" + ByteBuffertoString(column.name));
        //                //System.out.println("read column value" + ":" + ByteBuffertoString(column.value));
        //                //System.out.println("read column timestamp" + ":" + (column.timestamp));
        //                //System.out.println("");

        /* 删除一条记录 */
        //        delete(columnFamily,tablename,"fengye",System.currentTimeMillis());
        //
        //        //System.out.println("read Table " + columnFamily);
        //        //System.out.println("read column name " + ":" + toString(column.name));
        //        //System.out.println("read column value" + ":" + toString(column.value));
        //        //System.out.println("read column timestamp" + ":" + (column.timestamp));

        //close();
    }

    public RetRpc getDbState() {
        return dbState;
    }
    public void setDbState(RetRpc dbState) {
        this.dbState = dbState;
    }
    
    
    public static void main(String[] args){
        //CassandraOperate.getInstance().test();
    }

}
