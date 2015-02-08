package com.huawei.agilete.base.servlet.util;
import java.net.URI;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
public class WebSocketOtherClient extends WebSocketClient {
    public WebSocketOtherClient(URI serverURI) {
        super(serverURI);
        // TODO Auto-generated constructor stub
    }
    public WebSocketOtherClient(URI serverURI,Draft draft,Map<String, String> map){
        super(serverURI,draft,map);
    }
    public static void main(String[] args){
        String uri ="ws://localhost:9003/alarm/other";
        URI uri2  = URI.create(uri);
        WebSocketOtherClient socketTest = new WebSocketOtherClient(uri2);
        socketTest.connect();
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onError(Exception arg0) {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * 该方法就是用来接受服务端 推送过来的信息
     * 参数arg0就是服务端 推送过来的信息内容
     * */
    @Override
    public void onMessage(String arg0) {
        //System.out.println(arg0);
    }

    @Override
    public void onOpen(ServerHandshake arg0) {
        //System.out.println("open");
        
    }
}
