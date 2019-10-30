package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Map;

@SuppressWarnings("unchecked")
public class WsServer extends WebSocketServer {
    private WsContextHandler _contextHandler;

    public WsServer(int port, WsContextHandler contextHandler) throws UnknownHostException {
        super(new InetSocketAddress(port));
        _contextHandler = contextHandler;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake clientHandshake) {

    }

    @Override
    public void onClose(WebSocket conn, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket conn, String data) {
        try {
            do_onMessage(conn, data.getBytes("utf-8"));
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer data) {
        do_onMessage(conn, data.array());
    }

    public void do_onMessage(WebSocket socket, byte[] message) {
        try {
            _contextHandler.handle(socket, message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }



}
