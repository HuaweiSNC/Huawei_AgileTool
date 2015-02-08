package com.huawei.agilete.base.servlet.util;
import java.net.URI;
import java.util.List;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.huawei.agilete.base.action.DBAction;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.northinterface.bean.Alarm;
import org.dom4j.Element;



public class WebSocketClent extends WebSocketClient {
	private static WebSocketClent clent = null;
	public synchronized  static WebSocketClent getInstance(URI serverURI) {
		if (clent == null) {  
			clent = new WebSocketClent(serverURI);
			clent.connect();
		}  
		return clent;
	}
	private WsocketServer test2=null;
	private DBAction db = new DBAction();
	public DBAction getDb() {
		return db;
	}

	public void setDb(DBAction db) {
		this.db = db;
	}
	public WsocketServer getTest2() {
		return test2;
	}

	public void setTest2(WsocketServer test2) {
		this.test2 = test2;
	}

	public WebSocketClent(URI serverURI) {
		super(serverURI);
		test2 = WsocketServer.getInstance(Integer.parseInt(MyData.getAlarmServerPort()));
		test2.start();
	}

/*	public static void main(String[] args){
		//String uri ="ws://localhost:9000/channels/2/data";
		String uri ="ws://localhost:9003/www/ww";
		URI uri2  = URI.create(uri);
		WebSocketClent socketTest = new WebSocketClent(uri2);
		socketTest.connect();
		socketTest.send("ssssss");
	}*/

	@Override
	public void onClose(int arg0, String arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Exception arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(String arg0) {
		String message = this.currentAlarmMessage(arg0);
		db.insert("alarm","alarm",System.currentTimeMillis()+"",message);
		String message2 = this.alarmMessages();
		System.out.println(message);
		test2.sendToCommon(message2);
		if(isLinkDown(message)){
			test2.sendToOther(message);
		}
	}

	public String currentAlarmMessage(String str){
		String message = "";
		JSONObject object = JSONObject.fromObject(str);
		XMLSerializer xmlSerializer = new XMLSerializer();
		String xml = xmlSerializer.write(JSONSerializer.toJSON(object));
		String xml2 =xml.replace("<o>","").replace("</o>","").replace("<e class=\"object\">","").replace("</e>",""); 
		Alarm alarm = new Alarm(xml2);
		message = alarm.getOpsMessage3();
		return message;
	}
	
	
	public String alarmMessages(){
		StringBuffer message2 = new StringBuffer();
		List<String[]> list2 =(List<String[]>)db.getAllColumnByCondition("alarm","alarm",1,10,true);
		message2.append("<data>");
		for(String[] strings:list2){
			message2.append(strings[1]);
		}
		message2.append("</data>");
		return message2.toString();
	}
	
	
	
	
	/**
	 * 判断发送过来的告警是否是LinkDown告警
	 * 
	 * */
	public boolean isLinkDown(String content){
		boolean flag = false;
		try {
			Document doc = DocumentHelper.parseText(content);
			Element el = doc.getRootElement();
			Element elSnmpv2trap = el.element("snmpv2trap");
			Element elTrapoid = elSnmpv2trap.element("trapoid");
			if("1.3.6.1.6.3.1.1.5.3".equals(elTrapoid.getTextTrim())){
				flag = true;
			}
		}catch (DocumentException e) {
			//e.printStackTrace();
		}
		return flag;
	}
	
	
	
	
	
	@Override
	public void onOpen(ServerHandshake arg0) {
		System.out.println("open");
		
	}

	
}
