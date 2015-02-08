package com.huawei.agilete.northinterface.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.FrameBuilder;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class AutobahnServer extends WebSocketServer {
	//private static int counter = 0;
	
	public AutobahnServer( int port , Draft d ){
		super( new InetSocketAddress( port ), Collections.singletonList( d ) );
	}
	
	public AutobahnServer( InetSocketAddress address, Draft d ) {
		super( address, Collections.singletonList( d ) );
	}

	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		System.out.println( "closed" );
	}

	@Override
	public void onError( WebSocket conn, Exception ex ) {
		System.out.println( "Error:" );
		ex.printStackTrace();
	}

	@Override
	public void onMessage( WebSocket conn, String message ) {
		conn.send( message );
	}

	@Override
	public void onMessage( WebSocket conn, ByteBuffer blob ) {
		conn.send( blob );
	}

	public void onWebsocketMessageFragment( WebSocket conn, Framedata frame ) {
		FrameBuilder builder = (FrameBuilder) frame;
		builder.setTransferemasked( false );
		conn.sendFrame( frame );
	}

	public void sendToAll( String text ) {
		Collection<WebSocket> con = connections();
		synchronized ( con ) {
			for( WebSocket c : con ) {
				c.send( text );
			}
		}
	}
	
	public void startServer( String text ) {
		WebSocketImpl.DEBUG = false;
		int port = 9003;
		AutobahnServer test;
		try {
			test = new AutobahnServer( port, new Draft_17());
			test.start();
			BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
			while ( true ) {
				String in = sysin.readLine();
				test.sendToAll( in );
				if( in.equals( "exit" ) ) {
					test.stop();
					break;
				} else if( in.equals( "restart" ) ) {
					test.stop();
					test.start();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	public static void main( String[] args ) throws  IOException, InterruptedException {
		WebSocketImpl.DEBUG = false;
		int port;
		try {
			port = new Integer( args[ 0 ] );
		} catch ( Exception e ) {
			System.out.println( "No port specified. Defaulting to 9003" );
			port = 9003;
		}
		AutobahnServer test = new AutobahnServer( port, new Draft_17());
		/*test.onMessage(test.connections(),"333333");*/
		test.start();
		
		BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
		while ( true ) {
			String in = sysin.readLine();
			test.sendToAll( in );
			if( in.equals( "exit" ) ) {
				test.stop();
				break;
			} else if( in.equals( "restart" ) ) {
				test.stop();
				test.start();
				break;
			}
		}
	}

}
