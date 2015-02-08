package com.huawei.agilete.base.servlet.util;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.FrameBuilder;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.huawei.agilete.base.action.DBAction;

public class WsocketServer extends WebSocketServer {
	private static WsocketServer server = null;
	private DBAction db = new DBAction();
	public synchronized  static WsocketServer getInstance(int port) {
		if (server == null) {  
			server = new WsocketServer(port);
		}  
		return server;
	}
	private static int counter = 0;
	// 普通推送用户(接口连接：服务器地址：监听端口/alarm/common)
	private final Collection<WebSocket> commonCollections = new HashSet<WebSocket>();
	// 只推送linkdown消息的用户(接口连接：服务器地址：监听端口/alarm/other)
	private final Collection<WebSocket> otherCollections = new HashSet<WebSocket>();

	public WsocketServer(int port, Draft d) throws UnknownHostException {
		super(new InetSocketAddress(port), Collections.singletonList(d));
	}

	public WsocketServer(InetSocketAddress address, Draft d) {
		super(address, Collections.singletonList(d));
	}

	public WsocketServer(int port){
		super(new InetSocketAddress(port));
	}

	/**
	 * 客户端用户发送连接就会生成一个WebSocket ClientHandshake保存一些握手信息，比如uri
	 * */
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		counter++;
		System.out.println("client connection number:" + counter);
		if ("/alarm/other".equals(handshake.getResourceDescriptor())
				|| "/alarm/other/".equals(handshake.getResourceDescriptor())) {
			otherCollections.add(conn);
		} else if ("/alarm/common".equals(handshake.getResourceDescriptor())
				|| "/alarm/common/".equals(handshake.getResourceDescriptor())) {
			commonCollections.add(conn);
		}
		String message = this.alarmMessages();		
		this.sendToCommon(message);
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
	
	
	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println("closed");
		System.out.println("客户端"+conn.getRemoteSocketAddress().getAddress().toString()+"关掉连接！");
		System.out.println(commonCollections.remove(conn));
		System.out.println(otherCollections.remove(conn));
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		System.out.println("Error:");
		ex.printStackTrace();
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		conn.send(message);
	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer blob) {
		conn.send(blob);
	}

	public void onWebsocketMessageFragment(WebSocket conn, Framedata frame) {
		FrameBuilder builder = (FrameBuilder) frame;
		builder.setTransferemasked(false);
		conn.sendFrame(frame);
	}

	/**
	 * 发送给所有连接用户
	 * */
	public void sendToAll(String text) {
		Collection<WebSocket> con = connections();
		synchronized (con) {
			for (WebSocket c : con) {
				c.send(text);
			}
		}
	}

	/**
	 * 发送给只接受linkdown消息的用户
	 * */
	public void sendToOther(String text) {
		Collection<WebSocket> con = otherCollections;
		if (null != con && con.size() > 0) {
			synchronized (con) {
				for (WebSocket c : con) {
					c.send(text);
				}
			}
		}
	}

	/**
	 * 发送给普通用户
	 * */
	public void sendToCommon(String text) {
		Collection<WebSocket> con = commonCollections;
		if (null != con && con.size() > 0) {
			synchronized (con) {
				for (WebSocket c : con) {
					c.send(text);
				}
			}
		}
	}

	/*
	 * public static void main( String[] args ) throws IOException,
	 * InterruptedException { WebSocketImpl.DEBUG = false; int port; try { port
	 * = new Integer( args[ 0 ] ); } catch ( Exception e ) { System.out.println(
	 * "No port specified. Defaulting to 9003" ); port = 9003; } WsocketServer
	 * test = new WsocketServer( port, new Draft_17()); test.start();
	 * 
	 * BufferedReader sysin = new BufferedReader( new InputStreamReader(
	 * System.in ) ); while ( true ) { String in = sysin.readLine();
	 * test.sendToCommon( in ); test.sendToOther( in ); if( in.equals( "exit" )
	 * ) { test.stop(); break; } else if( in.equals( "restart" ) ) {
	 * test.stop(); test.start(); break; } } }
	 */
}
