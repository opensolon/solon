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
    public void onOpen(WebSocket conn, ClientHandshake clientHandshake) {

    }

    @Override
    public void onClose(WebSocket conn, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket conn, String data) {

    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer data) {
        do_onMessage(conn, data.array());
    }

    public void do_onMessage(WebSocket conn, byte[] msg) {
        ObjectInput objectInput = null;
        ObjectOutput objectOutput = null;

        try {
            objectInput = new ObjectInputStream(new ByteArrayInputStream(msg));
            Map<String, Object> rpcxMsg = (Map<String, Object>) objectInput.readObject();
            WsRequest request = new WsRequest(rpcxMsg);
            WsResponse response = new WsResponse(request);

            request.setRemoteAddr(conn.getRemoteSocketAddress());

            _contextHandler.handle(request, response);

            response.close();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutput = new ObjectOutputStream(byteArrayOutputStream);
            objectOutput.writeObject(response.message());

            conn.send(byteArrayOutputStream.toByteArray());
//            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (objectInput != null) {
                try {
                    objectInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (objectOutput != null) {
                try {
                    objectOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

}
